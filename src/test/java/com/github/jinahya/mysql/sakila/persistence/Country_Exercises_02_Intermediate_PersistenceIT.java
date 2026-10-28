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

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers the exercises of {@code src/test/sql/exercises/country_exercises_02_intermediate.sql}, against the
 * installed {@code sakila} database; a nested class per exercise, which answers it with JPQL and
 * with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Country_Exercises_02_Intermediate_PersistenceIT {

    @DisplayName("SAKILA_COUNTRY_I_01: the countries with no city")
    @Nested
    class SAKILA_COUNTRY_I_01_Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(
                    em -> em.createQuery(
                                    """
                                            SELECT c
                                            FROM Country c
                                            WHERE c.cities IS EMPTY""",
                                    Country.class
                            )
                            .getResultList()
            );
            list.forEach(e -> log.debug("{}", e));
            assertThat(list).isEmpty();
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Country.class);
                final var c = cq.from(Country.class);
                cq.select(c)
                        .where(cb.isEmpty(c.get(Country_.cities)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(e -> log.debug("{}", e));
            assertThat(list).isEmpty();
        }
    }

    @DisplayName("SAKILA_COUNTRY_I_02: the five countries with the most customers")
    @Nested
    class SAKILA_COUNTRY_I_02_Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT co, COUNT(cu)
                                                                                      FROM Customer cu
                                                                                               JOIN cu.address a
                                                                                               JOIN a.city c
                                                                                               JOIN c.country co
                                                                                      GROUP BY co
                                                                                      ORDER BY COUNT(cu) DESC, co.country""",
                                                                              Object[].class)
                    .setMaxResults(5)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Country) r[0]).getCountry(),
                                r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple("India", 60L),
                            tuple("China", 53L),
                            tuple("United States", 36L),
                            tuple("Japan", 31L),
                            tuple("Mexico", 30L));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var cu = cq.from(Customer.class);
                final var co = cu.join(Customer_.address).join(Address_.city).join(City_.country);
                cq.select(cb.array(co, cb.count(cu)))
                        .groupBy(co)
                        .orderBy(cb.desc(cb.count(cu)), cb.asc(co.get(Country_.country)));
                return em.createQuery(cq)
                        .setMaxResults(5)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Country) r[0]).getCountry(),
                                r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple("India", 60L),
                            tuple("China", 53L),
                            tuple("United States", 36L),
                            tuple("Japan", 31L),
                            tuple("Mexico", 30L));
        }
    }

    @DisplayName("SAKILA_COUNTRY_I_03: how many countries have exactly one city")
    @Nested
    class SAKILA_COUNTRY_I_03_Test {

        @Test
        void __QueryLanguage() {
            final var value = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                       SELECT COUNT(c)
                                                                                       FROM Country c
                                                                                       WHERE (SELECT COUNT(ci) FROM City ci WHERE ci.country = c) = 1""",
                                                                               Long.class)
                    .getSingleResult());
            log.debug("{}", value);
            assertThat(value).isEqualTo(42L);
        }

        @Test
        void __CriteriaApi() {
            final var value = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Long.class);
                final var c = cq.from(Country.class);
                final var cities = cq.subquery(Long.class);
                final var ci = cities.from(City.class);
                cities.select(cb.count(ci)).where(cb.equal(ci.get(City_.country), c));
                cq.select(cb.count(c)).where(cb.equal(cities, 1L));
                return em.createQuery(cq).getSingleResult();
            });
            log.debug("{}", value);
            assertThat(value).isEqualTo(42L);
        }
    }

    @DisplayName("SAKILA_COUNTRY_I_04: the countries which have a city but no customer")
    @Nested
    class SAKILA_COUNTRY_I_04_Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT c
                                                                                      FROM Country c
                                                                                      WHERE c.cities IS NOT EMPTY
                                                                                        AND NOT EXISTS (SELECT cu FROM Customer cu WHERE cu.address.city.country = c)
                                                                                      ORDER BY c.country""",
                                                                              Country.class)
                    .getResultList());
            list.forEach(e -> log.debug("{}", e));
            assertThat(list)
                    .extracting(Country::getCountryId, Country::getCountry)
                    .containsExactly(
                            tuple(8, "Australia"));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Country.class);
                final var c = cq.from(Country.class);
                final var customers = cq.subquery(Customer.class);
                final var cu = customers.from(Customer.class);
                customers.select(cu)
                        .where(cb.equal(cu.get(Customer_.address).get(Address_.city)
                                                .get(City_.country), c));
                cq.select(c)
                        .where(cb.isNotEmpty(c.get(Country_.cities)), cb.not(cb.exists(customers)))
                        .orderBy(cb.asc(c.get(Country_.country)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(e -> log.debug("{}", e));
            assertThat(list)
                    .extracting(Country::getCountryId, Country::getCountry)
                    .containsExactly(
                            tuple(8, "Australia"));
        }
    }
}
