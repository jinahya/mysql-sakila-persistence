package com.github.jinahya.mysql.sakila.persistence;

/*-
 * #%L
 * mysql-sakila-persistence
 * %%
 * Copyright (C) 2026 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers the exercises of
 * {@code src/test/sql/exercises/sales_by_film_category_exercises_03_advanced.sql}, against the installed
 * {@code sakila} database; a nested class per exercise, which answers it with JPQL and with the
 * Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class SalesByFilmCategory_Exercises_03_Advanced_PersistenceIT {

    @DisplayName("SAKILA_SALES_BY_FILM_CATEGORY_A_02: the three largest gaps between a category and the next one down")
    @Nested
    class SAKILA_SALES_BY_FILM_CATEGORY_A_02_Test {

        // JPQL has no LEAD; the next category down is the one after which, and before the other,
        // NOT EXISTS any
        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT s, n, s.totalSales - n.totalSales AS gap
                                                                                      FROM SalesByFilmCategory s, SalesByFilmCategory n
                                                                                      WHERE (n.totalSales < s.totalSales OR (n.totalSales = s.totalSales AND n.category > s.category))
                                                                                        AND NOT EXISTS (SELECT m
                                                                                                        FROM SalesByFilmCategory m
                                                                                                        WHERE (m.totalSales < s.totalSales
                                                                                                               OR (m.totalSales = s.totalSales AND m.category > s.category))
                                                                                                          AND (m.totalSales > n.totalSales
                                                                                                               OR (m.totalSales = n.totalSales AND m.category < n.category)))
                                                                                      ORDER BY gap DESC, s.category""",
                                                                              Object[].class)
                    .setMaxResults(3)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((SalesByFilmCategory) r[0]).getCategory(),
                                r -> ((SalesByFilmCategory) r[0]).getTotalSales().toPlainString(),
                                r -> ((SalesByFilmCategory) r[1]).getCategory(),
                                r -> ((BigDecimal) r[2]).toPlainString())
                    .containsExactly(
                            tuple("Sports", "5314.21", "Sci-Fi", "557.23"),
                            tuple("Documentary", "4217.52", "Horror", "494.98"),
                            tuple("Drama", "4587.39", "Comedy", "203.81"));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var s = cq.from(SalesByFilmCategory.class);
                final var n = cq.from(SalesByFilmCategory.class);
                final var sCategory = s.get(SalesByFilmCategory_.category);
                final var sTotalSales = s.get(SalesByFilmCategory_.totalSales);
                final var nCategory = n.get(SalesByFilmCategory_.category);
                final var nTotalSales = n.get(SalesByFilmCategory_.totalSales);
                final var between = cq.subquery(SalesByFilmCategory.class);
                final var m = between.from(SalesByFilmCategory.class);
                final var mCategory = m.get(SalesByFilmCategory_.category);
                final var mTotalSales = m.get(SalesByFilmCategory_.totalSales);
                between.select(m)
                        .where(cb.or(cb.lt(mTotalSales, sTotalSales),
                                     cb.and(cb.equal(mTotalSales, sTotalSales),
                                            cb.greaterThan(mCategory, sCategory))),
                               cb.or(cb.gt(mTotalSales, nTotalSales),
                                     cb.and(cb.equal(mTotalSales, nTotalSales),
                                            cb.lessThan(mCategory, nCategory))));
                final var gap = cb.diff(sTotalSales, nTotalSales);
                cq.select(cb.array(s, n, gap))
                        .where(cb.or(cb.lt(nTotalSales, sTotalSales),
                                     cb.and(cb.equal(nTotalSales, sTotalSales),
                                            cb.greaterThan(nCategory, sCategory))),
                               cb.not(cb.exists(between)))
                        .orderBy(cb.desc(gap), cb.asc(sCategory));
                return em.createQuery(cq)
                        .setMaxResults(3)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((SalesByFilmCategory) r[0]).getCategory(),
                                r -> ((SalesByFilmCategory) r[0]).getTotalSales().toPlainString(),
                                r -> ((SalesByFilmCategory) r[1]).getCategory(),
                                r -> ((BigDecimal) r[2]).toPlainString())
                    .containsExactly(
                            tuple("Sports", "5314.21", "Sci-Fi", "557.23"),
                            tuple("Documentary", "4217.52", "Horror", "494.98"),
                            tuple("Drama", "4587.39", "Comedy", "203.81"));
        }
    }

    @DisplayName("SAKILA_SALES_BY_FILM_CATEGORY_A_03: each category's rank by sales, next to its rank by rentals")
    @Nested
    class SAKILA_SALES_BY_FILM_CATEGORY_A_03_Test {

        // JPQL has no window functions; RANK() OVER (...) is SQL's
        @Test
        void nativeQuery() {
            final var list = __Exercises_TestUtils.apply(em -> (List<?>) em.createNativeQuery("""
                    WITH rented AS (SELECT c.name AS category, COUNT(*) AS rentals
                                    FROM rental r
                                             JOIN inventory i ON i.inventory_id = r.inventory_id
                                             JOIN film_category fc ON fc.film_id = i.film_id
                                             JOIN category c ON c.category_id = fc.category_id
                                    GROUP BY c.name)
                    SELECT s.*,
                           RANK() OVER (ORDER BY s.total_sales DESC) AS sales_rank,
                           rd.rentals,
                           RANK() OVER (ORDER BY rd.rentals DESC)    AS rentals_rank
                    FROM sales_by_film_category s
                             JOIN rented rd ON rd.category = s.category
                    ORDER BY sales_rank, s.category""")
                    .getResultList());
            list.forEach(r -> log.debug("{}", r instanceof Object[] a ? Arrays.toString(a) : r));
            assertThat(list)
                    .extracting(r -> ((Object[]) r)[0],
                                r -> ((Number) ((Object[]) r)[2]).intValue(),
                                r -> ((Number) ((Object[]) r)[3]).longValue(),
                                r -> ((Number) ((Object[]) r)[4]).intValue())
                    .containsExactly(
                            tuple("Sports", 1, 1179L, 1),
                            tuple("Sci-Fi", 2, 1101L, 4),
                            tuple("Animation", 3, 1166L, 2),
                            tuple("Drama", 4, 1060L, 6),
                            tuple("Comedy", 5, 941L, 11),
                            tuple("Action", 6, 1112L, 3),
                            tuple("New", 7, 940L, 12),
                            tuple("Games", 8, 969L, 9),
                            tuple("Foreign", 9, 1033L, 8),
                            tuple("Family", 10, 1096L, 5),
                            tuple("Documentary", 11, 1050L, 7),
                            tuple("Horror", 12, 846L, 14),
                            tuple("Children", 13, 945L, 10),
                            tuple("Classics", 14, 939L, 13),
                            tuple("Travel", 15, 837L, 15),
                            tuple("Music", 16, 830L, 16));
        }
    }
}
