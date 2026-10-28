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
 * Answers the exercises of {@code src/test/sql/exercises/city_exercises_01_basic.sql}, against the installed
 * {@code sakila} database; a nested class per exercise, which answers it with JPQL and with the
 * Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class City_Exercises_01_Basic_PersistenceIT {

    @DisplayName("SAKILA_CITY_B_01: the cities whose name starts with Q")
    @Nested
    class SAKILA_CITY_B_01_Test {

        @ParameterizedTest
        @ValueSource(strings = {"Q", "X", "Z"})
        void __QueryLanguage(final String letter) {
            final var list = __Exercises_TestUtils.apply(
                    em -> em.createQuery(
                                    """
                                            SELECT c
                                            FROM City c
                                            WHERE c.city LIKE :pattern
                                            ORDER BY c.city""",
                                    City.class
                            )
                            .setParameter("pattern", letter + "%")
                            .getResultList()
            );
            list.forEach(e -> log.debug("{}", e));
            assertThat(list).allSatisfy(c -> assertThat(c.getCity()).startsWith(letter));
        }

        @ParameterizedTest
        @ValueSource(strings = {"Q", "X", "Z"})
        void __CriteriaApi(final String letter) {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(City.class);
                final var c = cq.from(City.class);
                cq.select(c)
                        .where(cb.like(c.get(City_.city), cb.parameter(String.class, "pattern")))
                        .orderBy(cb.asc(c.get(City_.city)));
                return em.createQuery(cq)
                        .setParameter("pattern", letter + "%")
                        .getResultList();
            });
            list.forEach(e -> log.debug("{}", e));
            assertThat(list).allSatisfy(c -> assertThat(c.getCity()).startsWith(letter));
        }
    }

    @DisplayName("SAKILA_CITY_B_02: how many city names have more than one word")
    @Nested
    class SAKILA_CITY_B_02_Test {

        @Test
        void __QueryLanguage() {
            final var value = __Exercises_TestUtils.apply(
                    em -> em.createQuery(
                                    """
                                            SELECT COUNT(c)
                                            FROM City c
                                            WHERE c.city LIKE :pattern""",
                                    Long.class
                            )
                            .setParameter("pattern", "% %")
                            .getSingleResult()
            );
            log.debug("{}", value);
            assertThat(value).isEqualTo(92L);
        }

        @Test
        void __CriteriaApi() {
            final var value = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Long.class);
                final var c = cq.from(City.class);
                cq.select(cb.count(c))
                        .where(cb.like(c.get(City_.city), cb.parameter(String.class, "pattern")));
                return em.createQuery(cq)
                        .setParameter("pattern", "% %")
                        .getSingleResult();
            });
            log.debug("{}", value);
            assertThat(value).isEqualTo(92L);
        }
    }

    @DisplayName("SAKILA_CITY_B_03: the five longest city names")
    @Nested
    class SAKILA_CITY_B_03_Test {

        // EclipseLink renders LENGTH as MySQL's LENGTH, which counts bytes, not characters; the
        // name is measured with CHAR_LENGTH, by FUNCTION
        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(
                    em -> em.createQuery(
                                    """
                                            SELECT c, FUNCTION('CHAR_LENGTH', c.city) AS cityLength
                                            FROM City c
                                            ORDER BY cityLength DESC, c.city""",
                                    Object[].class
                            )
                            .setMaxResults(5)
                            .getResultList()
            );
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((City) r[0]).getCityId(), r -> ((City) r[0]).getCity(),
                                r -> ((Number) r[1]).intValue())
                    .containsExactly(
                            tuple(450, "San Felipe de Puerto Plata", 26),
                            tuple(452, "San Juan Bautista Tuxtepec", 26),
                            tuple(460, "Santiago de los Caballeros", 26),
                            tuple(41, "Augusta-Richmond County", 23),
                            tuple(71, "Berhampore (Baharampur)", 23));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var c = cq.from(City.class);
                final var cityLength =
                        cb.function("CHAR_LENGTH", Integer.class, c.get(City_.city));
                cq.select(cb.array(c, cityLength))
                        .orderBy(cb.desc(cityLength), cb.asc(c.get(City_.city)));
                return em.createQuery(cq)
                        .setMaxResults(5)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((City) r[0]).getCityId(), r -> ((City) r[0]).getCity(),
                                r -> ((Number) r[1]).intValue())
                    .containsExactly(
                            tuple(450, "San Felipe de Puerto Plata", 26),
                            tuple(452, "San Juan Bautista Tuxtepec", 26),
                            tuple(460, "Santiago de los Caballeros", 26),
                            tuple(41, "Augusta-Richmond County", 23),
                            tuple(71, "Berhampore (Baharampur)", 23));
        }
    }
}
