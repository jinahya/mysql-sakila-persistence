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
 * Answers the exercises of {@code src/test/sql/exercises/sales_by_film_category_exercises_01_basic.sql},
 * against the installed {@code sakila} database; a nested class per exercise, which answers it with
 * JPQL and with the Criteria API, or with a native query where neither can, and logs what it
 * gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class SalesByFilmCategory_Exercises_01_Basic_PersistenceIT {

    @DisplayName("SAKILA_SALES_BY_FILM_CATEGORY_B_01: the three categories which sold the most")
    @Nested
    class SAKILA_SALES_BY_FILM_CATEGORY_B_01_Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT s
                                                                                      FROM SalesByFilmCategory s
                                                                                      ORDER BY s.totalSales DESC""",
                                                                              SalesByFilmCategory.class)
                    .setMaxResults(3)
                    .getResultList());
            list.forEach(e -> log.debug("{}", e));
            assertThat(list)
                    .extracting(SalesByFilmCategory::getCategory,
                                s -> s.getTotalSales().toPlainString())
                    .containsExactly(
                            tuple("Sports", "5314.21"),
                            tuple("Sci-Fi", "4756.98"),
                            tuple("Animation", "4656.30"));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(SalesByFilmCategory.class);
                final var s = cq.from(SalesByFilmCategory.class);
                cq.select(s)
                        .orderBy(cb.desc(s.get(SalesByFilmCategory_.totalSales)));
                return em.createQuery(cq)
                        .setMaxResults(3)
                        .getResultList();
            });
            list.forEach(e -> log.debug("{}", e));
            assertThat(list)
                    .extracting(SalesByFilmCategory::getCategory,
                                s -> s.getTotalSales().toPlainString())
                    .containsExactly(
                            tuple("Sports", "5314.21"),
                            tuple("Sci-Fi", "4756.98"),
                            tuple("Animation", "4656.30"));
        }
    }

    @DisplayName("SAKILA_SALES_BY_FILM_CATEGORY_B_02: the categories which sold between 4000 and 4500")
    @Nested
    class SAKILA_SALES_BY_FILM_CATEGORY_B_02_Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT s
                                                                                      FROM SalesByFilmCategory s
                                                                                      WHERE s.totalSales BETWEEN :low AND :high
                                                                                      ORDER BY s.category""",
                                                                              SalesByFilmCategory.class)
                    .setParameter("low", BigDecimal.valueOf(4000))
                    .setParameter("high", BigDecimal.valueOf(4500))
                    .getResultList());
            list.forEach(e -> log.debug("{}", e));
            assertThat(list)
                    .extracting(SalesByFilmCategory::getCategory,
                                s -> s.getTotalSales().toPlainString())
                    .containsExactly(
                            tuple("Action", "4375.85"),
                            tuple("Comedy", "4383.58"),
                            tuple("Documentary", "4217.52"),
                            tuple("Family", "4226.07"),
                            tuple("Foreign", "4270.67"),
                            tuple("Games", "4281.33"),
                            tuple("New", "4351.62"));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(SalesByFilmCategory.class);
                final var s = cq.from(SalesByFilmCategory.class);
                final var totalSales = s.get(SalesByFilmCategory_.totalSales);
                cq.select(s)
                        .where(cb.between(totalSales, cb.parameter(BigDecimal.class, "low"),
                                          cb.parameter(BigDecimal.class, "high")))
                        .orderBy(cb.asc(s.get(SalesByFilmCategory_.category)));
                return em.createQuery(cq)
                        .setParameter("low", BigDecimal.valueOf(4000))
                        .setParameter("high", BigDecimal.valueOf(4500))
                        .getResultList();
            });
            list.forEach(e -> log.debug("{}", e));
            assertThat(list)
                    .extracting(SalesByFilmCategory::getCategory,
                                s -> s.getTotalSales().toPlainString())
                    .containsExactly(
                            tuple("Action", "4375.85"),
                            tuple("Comedy", "4383.58"),
                            tuple("Documentary", "4217.52"),
                            tuple("Family", "4226.07"),
                            tuple("Foreign", "4270.67"),
                            tuple("Games", "4281.33"),
                            tuple("New", "4351.62"));
        }
    }

    @DisplayName(
            "SAKILA_SALES_BY_FILM_CATEGORY_B_03: how many categories sold less than 4000, and how much they sold together")
    @Nested
    class SAKILA_SALES_BY_FILM_CATEGORY_B_03_Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT COUNT(s), SUM(s.totalSales)
                                                                                      FROM SalesByFilmCategory s
                                                                                      WHERE s.totalSales < :total""",
                                                                              Object[].class)
                    .setParameter("total", BigDecimal.valueOf(4000))
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).longValue(),
                                r -> ((BigDecimal) r[1]).toPlainString())
                    .containsExactly(
                            tuple(5L, "17985.04"));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var s = cq.from(SalesByFilmCategory.class);
                final var totalSales = s.get(SalesByFilmCategory_.totalSales);
                cq.select(cb.array(cb.count(s), cb.sum(totalSales)))
                        .where(cb.lessThan(totalSales, cb.parameter(BigDecimal.class, "total")));
                return em.createQuery(cq)
                        .setParameter("total", BigDecimal.valueOf(4000))
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).longValue(),
                                r -> ((BigDecimal) r[1]).toPlainString())
                    .containsExactly(
                            tuple(5L, "17985.04"));
        }
    }
}
