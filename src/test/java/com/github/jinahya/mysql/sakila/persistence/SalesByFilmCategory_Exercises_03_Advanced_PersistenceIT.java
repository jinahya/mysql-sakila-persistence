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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers the exercises of
 * {@code src/test/sql/sales_by_film_category_exercises_03_advanced.sql}, against the installed
 * {@code sakila} database; a nested class per exercise, which answers it with JPQL and with the
 * Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class SalesByFilmCategory_Exercises_03_Advanced_PersistenceIT {

    @DisplayName(
            "exercise 1: the running total of the categories' sales, and its share of all sales")
    @Nested
    class Exercise1Test {

        // JPQL has no window function; a self-join sums, for each category, the categories up to it
        // and all of them
        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT s,
                                                                                             SUM(CASE WHEN t.totalSales > s.totalSales
                                                                                                           OR (t.totalSales = s.totalSales AND t.category <= s.category)
                                                                                                      THEN t.totalSales ELSE 0 END),
                                                                                             ROUND(100 * SUM(CASE WHEN t.totalSales > s.totalSales
                                                                                                                       OR (t.totalSales = s.totalSales AND t.category <= s.category)
                                                                                                                  THEN t.totalSales ELSE 0 END) / SUM(t.totalSales), 2)
                                                                                      FROM SalesByFilmCategory s, SalesByFilmCategory t
                                                                                      GROUP BY s
                                                                                      ORDER BY s.totalSales DESC, s.category""",
                                                                              Object[].class)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list).hasSize(16)
                    .extracting(r -> ((SalesByFilmCategory) r[0]).getCategory(),
                                r -> ((SalesByFilmCategory) r[0]).getTotalSales().toPlainString(),
                                r -> ((BigDecimal) r[1]).toPlainString(),
                                r -> ((Number) r[2]).doubleValue())
                    .startsWith(
                            tuple("Sports", "5314.21", "5314.21", 7.88d),
                            tuple("Sci-Fi", "4756.98", "10071.19", 14.94d),
                            tuple("Animation", "4656.30", "14727.49", 21.85d))
                    .endsWith(
                            tuple("Travel", "3549.64", "63988.84", 94.93d),
                            tuple("Music", "3417.72", "67406.56", 100.00d));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var s = cq.from(SalesByFilmCategory.class);
                final var t = cq.from(SalesByFilmCategory.class);
                final var category = s.get(SalesByFilmCategory_.category);
                final var totalSales = s.get(SalesByFilmCategory_.totalSales);
                final var tTotalSales = t.get(SalesByFilmCategory_.totalSales);
                final var upTo = cb.or(
                        cb.gt(tTotalSales, totalSales),
                        cb.and(cb.equal(tTotalSales, totalSales),
                               cb.lessThanOrEqualTo(t.get(SalesByFilmCategory_.category),
                                                    category)));
                final var runningTotal = cb.sum(cb.<BigDecimal>selectCase()
                                                        .when(upTo, tTotalSales)
                                                        .otherwise(BigDecimal.ZERO));
                final var runningPercent = cb.round(
                        cb.quot(cb.prod(runningTotal, BigDecimal.valueOf(100)),
                                cb.sum(tTotalSales)), 2);
                cq.select(cb.array(s, runningTotal, runningPercent))
                        // keeps nothing out; without a WHERE clause on both roots, EclipseLink
                        // renders the s inside the CASE as a third, unjoined table
                        .where(cb.isNotNull(totalSales), cb.isNotNull(tTotalSales))
                        .groupBy(s)
                        .orderBy(cb.desc(totalSales), cb.asc(category));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list).hasSize(16)
                    .extracting(r -> ((SalesByFilmCategory) r[0]).getCategory(),
                                r -> ((SalesByFilmCategory) r[0]).getTotalSales().toPlainString(),
                                r -> ((BigDecimal) r[1]).toPlainString(),
                                r -> ((Number) r[2]).doubleValue())
                    .startsWith(
                            tuple("Sports", "5314.21", "5314.21", 7.88d),
                            tuple("Sci-Fi", "4756.98", "10071.19", 14.94d),
                            tuple("Animation", "4656.30", "14727.49", 21.85d))
                    .endsWith(
                            tuple("Travel", "3549.64", "63988.84", 94.93d),
                            tuple("Music", "3417.72", "67406.56", 100.00d));
        }
    }

    @DisplayName("exercise 2: the three largest gaps between a category and the next one down")
    @Nested
    class Exercise2Test {

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
}
