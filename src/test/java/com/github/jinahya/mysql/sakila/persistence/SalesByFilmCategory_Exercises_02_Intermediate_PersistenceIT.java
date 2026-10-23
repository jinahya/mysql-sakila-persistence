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
 * {@code src/test/sql/sales_by_film_category_exercises_02_intermediate.sql}, against the installed
 * {@code sakila} database; a nested class per exercise, which answers it with JPQL and with the
 * Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class SalesByFilmCategory_Exercises_02_Intermediate_PersistenceIT {

    @DisplayName("exercise 1: whether the view's sales add up to every payment")
    @Nested
    class Exercise1Test {

        // JPQL has no subquery in the SELECT clause; the two sums are two queries
        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> List.<Object[]>of(new Object[]{
                    em.createQuery("SELECT SUM(s.totalSales) FROM SalesByFilmCategory s",
                                   BigDecimal.class).getSingleResult(),
                    em.createQuery("SELECT SUM(p.amount) FROM Payment p",
                                   BigDecimal.class).getSingleResult()}));
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((BigDecimal) r[0]).toPlainString(),
                                r -> ((BigDecimal) r[1]).toPlainString())
                    .containsExactly(
                            tuple("67406.56", "67406.56"));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var byCategory = cb.createQuery(BigDecimal.class);
                final var s = byCategory.from(SalesByFilmCategory.class);
                byCategory.select(cb.sum(s.get(SalesByFilmCategory_.totalSales)));
                final var all = cb.createQuery(BigDecimal.class);
                final var p = all.from(Payment.class);
                all.select(cb.sum(p.get(Payment_.amount)));
                return List.<Object[]>of(new Object[]{
                        em.createQuery(byCategory).getSingleResult(), em.createQuery(
                        all).getSingleResult()});
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((BigDecimal) r[0]).toPlainString(),
                                r -> ((BigDecimal) r[1]).toPlainString())
                    .containsExactly(
                            tuple("67406.56", "67406.56"));
        }
    }

    @DisplayName(
            "exercise 2: the five categories which sold the most, and their share of all sales")
    @Nested
    class Exercise2Test {

        // JPQL has no subquery in the SELECT clause; the sum of all sales comes from a cross join
        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT s, ROUND(100 * s.totalSales / SUM(t.totalSales), 2)
                                                                                      FROM SalesByFilmCategory s, SalesByFilmCategory t
                                                                                      GROUP BY s
                                                                                      ORDER BY s.totalSales DESC, s.category""",
                                                                              Object[].class)
                    .setMaxResults(5)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((SalesByFilmCategory) r[0]).getCategory(),
                                r -> ((SalesByFilmCategory) r[0]).getTotalSales().toPlainString(),
                                r -> ((Number) r[1]).doubleValue())
                    .containsExactly(
                            tuple("Sports", "5314.21", 7.88d),
                            tuple("Sci-Fi", "4756.98", 7.06d),
                            tuple("Animation", "4656.30", 6.91d),
                            tuple("Drama", "4587.39", 6.81d),
                            tuple("Comedy", "4383.58", 6.50d));
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
                final var percent = cb.round(
                        cb.quot(cb.prod(totalSales, BigDecimal.valueOf(100)),
                                cb.sum(t.get(SalesByFilmCategory_.totalSales))), 2);
                cq.select(cb.array(s, percent))
                        .groupBy(s)
                        .orderBy(cb.desc(totalSales), cb.asc(category));
                return em.createQuery(cq)
                        .setMaxResults(5)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((SalesByFilmCategory) r[0]).getCategory(),
                                r -> ((SalesByFilmCategory) r[0]).getTotalSales().toPlainString(),
                                r -> ((Number) r[1]).doubleValue())
                    .containsExactly(
                            tuple("Sports", "5314.21", 7.88d),
                            tuple("Sci-Fi", "4756.98", 7.06d),
                            tuple("Animation", "4656.30", 6.91d),
                            tuple("Drama", "4587.39", 6.81d),
                            tuple("Comedy", "4383.58", 6.50d));
        }
    }

    @DisplayName("exercise 3: the five categories which sold the most per film")
    @Nested
    class Exercise3Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT s, COUNT(fc.filmId), ROUND(s.totalSales / COUNT(fc.filmId), 2) AS perFilm
                                                                                      FROM SalesByFilmCategory s, FilmCategory fc
                                                                                               JOIN fc.category c
                                                                                      WHERE c.name = s.category
                                                                                      GROUP BY s
                                                                                      ORDER BY perFilm DESC, s.category""",
                                                                              Object[].class)
                    .setMaxResults(5)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((SalesByFilmCategory) r[0]).getCategory(),
                                r -> ((SalesByFilmCategory) r[0]).getTotalSales().toPlainString(),
                                r -> ((Number) r[1]).longValue(),
                                r -> ((Number) r[2]).doubleValue())
                    .containsExactly(
                            tuple("Sci-Fi", "4756.98", 61L, 77.98d),
                            tuple("Comedy", "4383.58", 58L, 75.58d),
                            tuple("Drama", "4587.39", 62L, 73.99d),
                            tuple("Sports", "5314.21", 74L, 71.81d),
                            tuple("Animation", "4656.30", 66L, 70.55d));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var s = cq.from(SalesByFilmCategory.class);
                final var fc = cq.from(FilmCategory.class);
                final var c = fc.join(FilmCategory_.category);
                final var category = s.get(SalesByFilmCategory_.category);
                final var totalSales = s.get(SalesByFilmCategory_.totalSales);
                final var films = cb.count(fc.get(FilmCategory_.filmId));
                final var perFilm = cb.round(cb.quot(totalSales, films), 2);
                cq.select(cb.array(s, films, perFilm))
                        .where(cb.equal(c.get(Category_.name), category))
                        .groupBy(s)
                        .orderBy(cb.desc(perFilm), cb.asc(category));
                return em.createQuery(cq)
                        .setMaxResults(5)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((SalesByFilmCategory) r[0]).getCategory(),
                                r -> ((SalesByFilmCategory) r[0]).getTotalSales().toPlainString(),
                                r -> ((Number) r[1]).longValue(),
                                r -> ((Number) r[2]).doubleValue())
                    .containsExactly(
                            tuple("Sci-Fi", "4756.98", 61L, 77.98d),
                            tuple("Comedy", "4383.58", 58L, 75.58d),
                            tuple("Drama", "4587.39", 62L, 73.99d),
                            tuple("Sports", "5314.21", 74L, 71.81d),
                            tuple("Animation", "4656.30", 66L, 70.55d));
        }
    }
}
