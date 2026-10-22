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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers the exercises of {@code src/test/sql/city_exercises_02_intermediate.sql}, against the
 * installed {@code sakila} database; a nested class per exercise, which answers it with JPQL and
 * with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class City_Exercises_02_Intermediate_PersistenceIT {

    @DisplayName("exercise 1: the cities of Canada")
    @Nested
    class Exercise1Test {

        @ParameterizedTest
        @ValueSource(strings = {"Canada", "Japan", "Brazil"})
        void __QueryLanguage(final String country) {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT c
                                                                                      FROM City c
                                                                                      WHERE c.country.country = :country
                                                                                      ORDER BY c.city""",
                                                                              City.class)
                    .setParameter("country", country)
                    .getResultList());
            list.forEach(e -> log.debug("{}", e));
            assertThat(list).isNotEmpty();
        }

        @ParameterizedTest
        @ValueSource(strings = {"Canada", "Japan", "Brazil"})
        void __CriteriaApi(final String country) {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(City.class);
                final var c = cq.from(City.class);
                cq.select(c)
                        .where(cb.equal(c.get(City_.country).get(Country_.country),
                                        cb.parameter(String.class, "country")))
                        .orderBy(cb.asc(c.get(City_.city)));
                return em.createQuery(cq)
                        .setParameter("country", country)
                        .getResultList();
            });
            list.forEach(e -> log.debug("{}", e));
            assertThat(list).isNotEmpty();
        }
    }

    @DisplayName("exercise 2: the five countries with the most cities")
    @Nested
    class Exercise2Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT co.country, COUNT(c)
                                                                                      FROM City c
                                                                                               JOIN c.country co
                                                                                      GROUP BY co.countryId, co.country
                                                                                      ORDER BY COUNT(c) DESC, co.country""",
                                                                              Object[].class)
                    .setMaxResults(5)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple("India", 60L),
                            tuple("China", 53L),
                            tuple("United States", 35L),
                            tuple("Japan", 31L),
                            tuple("Mexico", 30L));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var c = cq.from(City.class);
                final var co = c.join(City_.country);
                cq.select(cb.array(co.get(Country_.country), cb.count(c)))
                        .groupBy(co.get(Country_.countryId), co.get(Country_.country))
                        .orderBy(cb.desc(cb.count(c)), cb.asc(co.get(Country_.country)));
                return em.createQuery(cq)
                        .setMaxResults(5)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple("India", 60L),
                            tuple("China", 53L),
                            tuple("United States", 35L),
                            tuple("Japan", 31L),
                            tuple("Mexico", 30L));
        }
    }

    @DisplayName("exercise 3: how many cities have no address")
    @Nested
    class Exercise3Test {

        @Test
        void __QueryLanguage() {
            final var value = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                       SELECT COUNT(c)
                                                                                       FROM City c
                                                                                       WHERE c.addresses IS EMPTY""",
                                                                               Long.class)
                    .getSingleResult());
            log.debug("{}", value);
            assertThat(value).isEqualTo(1L);
        }

        @Test
        void __CriteriaApi() {
            final var value = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Long.class);
                final var c = cq.from(City.class);
                cq.select(cb.count(c))
                        .where(cb.isEmpty(c.get(City_.addresses)));
                return em.createQuery(cq)
                        .getSingleResult();
            });
            log.debug("{}", value);
            assertThat(value).isEqualTo(1L);
        }
    }
}
