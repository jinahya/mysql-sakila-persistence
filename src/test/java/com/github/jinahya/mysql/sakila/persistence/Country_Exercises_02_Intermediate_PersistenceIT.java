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
 * Answers the exercises of {@code src/test/sql/country_exercises_02_intermediate.sql}, against the
 * installed {@code sakila} database; a nested class per exercise, which answers it with JPQL and
 * with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Country_Exercises_02_Intermediate_PersistenceIT {

    @DisplayName("exercise 1: the countries with no city")
    @Nested
    class Exercise1Test {

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

    @DisplayName("exercise 2: the five countries with the most customers")
    @Nested
    class Exercise2Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT co.country, COUNT(cu)
                                                                                      FROM Customer cu
                                                                                               JOIN cu.address a
                                                                                               JOIN a.city c
                                                                                               JOIN c.country co
                                                                                      GROUP BY co.countryId, co.country
                                                                                      ORDER BY COUNT(cu) DESC, co.country""",
                                                                              Object[].class)
                    .setMaxResults(5)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).longValue())
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
                cq.select(cb.array(co.get(Country_.country), cb.count(cu)))
                        .groupBy(co.get(Country_.countryId), co.get(Country_.country))
                        .orderBy(cb.desc(cb.count(cu)), cb.asc(co.get(Country_.country)));
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
                            tuple("United States", 36L),
                            tuple("Japan", 31L),
                            tuple("Mexico", 30L));
        }
    }
}
