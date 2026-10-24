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
 * Answers the exercises of {@code src/test/sql/country_exercises_01_basic.sql}, against the
 * installed {@code sakila} database; a nested class per exercise, which answers it with JPQL and
 * with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Country_Exercises_01_Basic_PersistenceIT {

    @DisplayName("SAKILA_COUNTRY_B_01: how many countries there are")
    @Nested
    class SAKILA_COUNTRY_B_01_Test {

        @Test
        void __QueryLanguage() {
            final var value = __Exercises_TestUtils.apply(
                    em -> em.createQuery(
                                    """
                                            SELECT COUNT(c)
                                            FROM Country c""",
                                    Long.class
                            )
                            .getSingleResult()
            );
            log.debug("{}", value);
            assertThat(value).isEqualTo(109L);
        }

        @Test
        void __CriteriaApi() {
            final var value = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Long.class);
                final var c = cq.from(Country.class);
                cq.select(cb.count(c));
                return em.createQuery(cq).getSingleResult();
            });
            log.debug("{}", value);
            assertThat(value).isEqualTo(109L);
        }
    }

    @DisplayName("SAKILA_COUNTRY_B_02: the countries whose name starts with S, ordered by name")
    @Nested
    class SAKILA_COUNTRY_B_02_Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(
                    em -> em.createQuery(
                                    """
                                            SELECT c
                                            FROM Country c
                                            WHERE c.country LIKE :pattern
                                            ORDER BY c.country""",
                                    Country.class
                            )
                            .setParameter("pattern", "S%")
                            .getResultList()
            );
            list.forEach(e -> log.debug("{}", e));
            assertThat(list)
                    .extracting(Country::getCountryId)
                    .containsExactly(81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91);
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Country.class);
                final var c = cq.from(Country.class);
                cq.select(c)
                        .where(cb.like(c.get(Country_.country),
                                       cb.parameter(String.class, "pattern")))
                        .orderBy(cb.asc(c.get(Country_.country)));
                return em.createQuery(cq)
                        .setParameter("pattern", "S%")
                        .getResultList();
            });
            list.forEach(e -> log.debug("{}", e));
            assertThat(list)
                    .extracting(Country::getCountryId)
                    .containsExactly(81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91);
        }
    }

    @DisplayName("SAKILA_COUNTRY_B_03: the five countries with the longest names")
    @Nested
    class SAKILA_COUNTRY_B_03_Test {

        // EclipseLink renders LENGTH as MySQL's LENGTH, which counts bytes, not characters; the
        // name is measured with CHAR_LENGTH, by FUNCTION
        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(
                    em -> em.createQuery(
                                    """
                                            SELECT c, FUNCTION('CHAR_LENGTH', c.country) AS nameLength
                                            FROM Country c
                                            ORDER BY nameLength DESC, c.country""",
                                    Object[].class
                            )
                            .setMaxResults(5)
                            .getResultList()
            );
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Country) r[0]).getCountry(), r -> ((Number) r[1]).intValue())
                    .containsExactly(
                            tuple("Congo, The Democratic Republic of the", 37),
                            tuple("Saint Vincent and the Grenadines", 32),
                            tuple("Holy See (Vatican City State)", 29),
                            tuple("United Arab Emirates", 20),
                            tuple("Virgin Islands, U.S.", 20));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var c = cq.from(Country.class);
                final var nameLength =
                        cb.function("CHAR_LENGTH", Integer.class, c.get(Country_.country));
                cq.select(cb.array(c, nameLength))
                        .orderBy(cb.desc(nameLength), cb.asc(c.get(Country_.country)));
                return em.createQuery(cq)
                        .setMaxResults(5)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Country) r[0]).getCountry(), r -> ((Number) r[1]).intValue())
                    .containsExactly(
                            tuple("Congo, The Democratic Republic of the", 37),
                            tuple("Saint Vincent and the Grenadines", 32),
                            tuple("Holy See (Vatican City State)", 29),
                            tuple("United Arab Emirates", 20),
                            tuple("Virgin Islands, U.S.", 20));
        }
    }
}
