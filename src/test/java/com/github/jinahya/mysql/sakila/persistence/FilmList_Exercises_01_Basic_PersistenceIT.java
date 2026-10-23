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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers the exercises of {@code src/test/sql/film_list_exercises_01_basic.sql}, against the
 * installed {@code sakila} database; a nested class per exercise, which answers it with JPQL and
 * with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class FilmList_Exercises_01_Basic_PersistenceIT {

    @DisplayName("exercise 1: how many Action films there are")
    @Nested
    class Exercise1Test {

        // the view is not an entity; the FilmList class is a plain class, read with a native query
        @ParameterizedTest
        @ValueSource(strings = {"Action", "Comedy", "Horror"})
        void nativeQuery(final String category) {
            final var list = __Exercises_TestUtils.apply(em -> (List<?>) em.createNativeQuery("""
                                                                                                      SELECT COUNT(*) AS films
                                                                                                      FROM film_list
                                                                                                      WHERE category = ?1""")
                    .setParameter(1, category)
                    .getResultList());
            list.forEach(r -> log.debug("{}", r instanceof Object[] a ? Arrays.toString(a) : r));
            assertThat(list).singleElement()
                    .satisfies(v -> assertThat(((Number) v).longValue()).isPositive());
        }
    }

    @DisplayName("exercise 2: the actors of the film 1")
    @Nested
    class Exercise2Test {

        // the view is not an entity; the FilmList class is a plain class, read with a native query
        @ParameterizedTest
        @ValueSource(ints = {1, 2, 3})
        void nativeQuery(final int fid) {
            final var list = __Exercises_TestUtils.apply(em -> (List<?>) em.createNativeQuery("""
                                                                                                      SELECT *
                                                                                                      FROM film_list
                                                                                                      WHERE FID = ?1""")
                    .setParameter(1, fid)
                    .getResultList());
            list.forEach(r -> log.debug("{}", r instanceof Object[] a ? Arrays.toString(a) : r));
            assertThat(list).singleElement().satisfies(
                    r -> assertThat(((Number) ((Object[]) r)[0]).intValue()).isEqualTo(fid));
        }
    }

    @DisplayName("exercise 3: the Children films rated G")
    @Nested
    class Exercise3Test {

        // the view is not an entity; the FilmList class is a plain class, read with a native query
        @Test
        void nativeQuery() {
            final var list = __Exercises_TestUtils.apply(em -> (List<?>) em.createNativeQuery("""
                                                                                                      SELECT *
                                                                                                      FROM film_list
                                                                                                      WHERE category = ?1
                                                                                                        AND rating = ?2
                                                                                                      ORDER BY title, FID""")
                    .setParameter(1, "Children")
                    .setParameter(2, "G")
                    .getResultList());
            list.forEach(r -> log.debug("{}", r instanceof Object[] a ? Arrays.toString(a) : r));
            assertThat(list)
                    .extracting(r -> ((Number) ((Object[]) r)[0]).intValue(),
                                r -> ((Object[]) r)[1],
                                r -> ((Number) ((Object[]) r)[5]).intValue())
                    .containsExactly(
                            tuple(238, "DOCTOR GRAIL", 57),
                            tuple(280, "EMPIRE MALKOVICH", 177),
                            tuple(304, "FARGO GANDHI", 130),
                            tuple(354, "GHOST GROUNDHOG", 85),
                            tuple(373, "GRADUATE LORD", 156),
                            tuple(409, "HEARTBREAKERS BRIGHT", 59),
                            tuple(626, "NOON PAPI", 57),
                            tuple(853, "STRANGER STRANGERS", 139),
                            tuple(873, "SWEETHEARTS SUSPECTS", 108),
                            tuple(959, "WARLOCK WEREWOLF", 83));
        }
    }

    @DisplayName("exercise 4: the films with no actor")
    @Nested
    class Exercise4Test {

        // the view is not an entity; the FilmList class is a plain class, read with a native query
        @Test
        void nativeQuery() {
            final var list = __Exercises_TestUtils.apply(em -> (List<?>) em.createNativeQuery("""
                                                                                                      SELECT *
                                                                                                      FROM film_list
                                                                                                      WHERE actors IS NULL
                                                                                                      ORDER BY FID""")
                    .getResultList());
            list.forEach(r -> log.debug("{}", r instanceof Object[] a ? Arrays.toString(a) : r));
            assertThat(list)
                    .extracting(r -> ((Number) ((Object[]) r)[0]).intValue(),
                                r -> ((Object[]) r)[1],
                                r -> ((Object[]) r)[3])
                    .containsExactly(
                            tuple(257, "DRUMLINE CYCLONE", "Travel"),
                            tuple(323, "FLIGHT LIES", "Sports"),
                            tuple(803, "SLACKER LIAISONS", "Drama"));
        }
    }
}
