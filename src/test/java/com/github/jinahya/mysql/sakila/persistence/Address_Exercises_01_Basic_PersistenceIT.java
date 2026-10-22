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
 * Answers the exercises of {@code src/test/sql/address_exercises_01_basic.sql}, against the
 * installed {@code sakila} database; a nested class per exercise, which answers it with JPQL and
 * with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Address_Exercises_01_Basic_PersistenceIT {

    @DisplayName("exercise 1: how many addresses are in the district of California")
    @Nested
    class Exercise1Test {

        @ParameterizedTest
        @ValueSource(strings = {"California", "Alberta", "Buenos Aires"})
        void __QueryLanguage(final String district) {
            final var value = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                       SELECT COUNT(a)
                                                                                       FROM Address a
                                                                                       WHERE a.district = :district""",
                                                                               Long.class)
                    .setParameter("district", district)
                    .getSingleResult());
            log.debug("{}", value);
            assertThat(value).isPositive();
        }

        @ParameterizedTest
        @ValueSource(strings = {"California", "Alberta", "Buenos Aires"})
        void __CriteriaApi(final String district) {
            final var value = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Long.class);
                final var a = cq.from(Address.class);
                cq.select(cb.count(a))
                        .where(cb.equal(a.get(Address_.district),
                                        cb.parameter(String.class, "district")));
                return em.createQuery(cq)
                        .setParameter("district", district)
                        .getSingleResult();
            });
            log.debug("{}", value);
            assertThat(value).isPositive();
        }
    }

    @DisplayName("exercise 2: the five districts with the most addresses")
    @Nested
    class Exercise2Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT a.district, COUNT(a)
                                                                                      FROM Address a
                                                                                      GROUP BY a.district
                                                                                      ORDER BY COUNT(a) DESC, a.district""",
                                                                              Object[].class)
                    .setMaxResults(5)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple("Buenos Aires", 10L),
                            tuple("California", 9L),
                            tuple("Shandong", 9L),
                            tuple("West Bengali", 9L),
                            tuple("São Paulo", 8L));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var a = cq.from(Address.class);
                cq.select(cb.array(a.get(Address_.district), cb.count(a)))
                        .groupBy(a.get(Address_.district))
                        .orderBy(cb.desc(cb.count(a)), cb.asc(a.get(Address_.district)));
                return em.createQuery(cq)
                        .setMaxResults(5)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple("Buenos Aires", 10L),
                            tuple("California", 9L),
                            tuple("Shandong", 9L),
                            tuple("West Bengali", 9L),
                            tuple("São Paulo", 8L));
        }
    }
}
