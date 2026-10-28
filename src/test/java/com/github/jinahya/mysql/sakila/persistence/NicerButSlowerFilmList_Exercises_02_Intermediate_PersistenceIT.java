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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers the exercises of
 * {@code src/test/sql/exercises/nicer_but_slower_film_list_exercises_02_intermediate.sql}, against the
 * installed {@code sakila} database; a nested class per exercise, which answers it with JPQL and
 * with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class NicerButSlowerFilmList_Exercises_02_Intermediate_PersistenceIT {

    @DisplayName("SAKILA_NICER_BUT_SLOWER_FILM_LIST_I_01: the five categories whose films are the longest, on average")
    @Nested
    class SAKILA_NICER_BUT_SLOWER_FILM_LIST_I_01_Test {

        // the view is not an entity; the NicerButSlowerFilmList class is a plain class, read with a
        // native query
        @Test
        void nativeQuery() {
            final var list = __Exercises_TestUtils.apply(em -> (List<?>) em.createNativeQuery("""
                                                                                                      SELECT category, COUNT(*) AS films, ROUND(AVG(length), 2) AS average_length
                                                                                                      FROM nicer_but_slower_film_list
                                                                                                      GROUP BY category
                                                                                                      ORDER BY average_length DESC, category
                                                                                                      LIMIT 5""")
                    .getResultList());
            list.forEach(r -> log.debug("{}", r instanceof Object[] a ? Arrays.toString(a) : r));
            assertThat(list)
                    .extracting(r -> ((Object[]) r)[0],
                                r -> ((Number) ((Object[]) r)[1]).longValue(),
                                r -> ((Number) ((Object[]) r)[2]).doubleValue())
                    .containsExactly(
                            tuple("Sports", 74L, 128.20d),
                            tuple("Games", 61L, 127.84d),
                            tuple("Foreign", 73L, 121.70d),
                            tuple("Drama", 62L, 120.84d),
                            tuple("Comedy", 58L, 115.83d));
        }
    }

    @DisplayName("SAKILA_NICER_BUT_SLOWER_FILM_LIST_I_02: how many films are short, medium and long")
    @Nested
    class SAKILA_NICER_BUT_SLOWER_FILM_LIST_I_02_Test {

        // the view is not an entity; the NicerButSlowerFilmList class is a plain class, read with a
        // native query
        @Test
        void nativeQuery() {
            final var list = __Exercises_TestUtils.apply(em -> (List<?>) em.createNativeQuery("""
                                                                                                      SELECT CASE WHEN length < 60 THEN 'short' WHEN length <= 120 THEN 'medium' ELSE 'long' END AS band,
                                                                                                             COUNT(*) AS films, MIN(length) AS shortest, MAX(length) AS longest
                                                                                                      FROM nicer_but_slower_film_list
                                                                                                      GROUP BY band
                                                                                                      ORDER BY shortest""")
                    .getResultList());
            list.forEach(r -> log.debug("{}", r instanceof Object[] a ? Arrays.toString(a) : r));
            assertThat(list)
                    .extracting(r -> ((Object[]) r)[0],
                                r -> ((Number) ((Object[]) r)[1]).longValue(),
                                r -> ((Number) ((Object[]) r)[2]).intValue(),
                                r -> ((Number) ((Object[]) r)[3]).intValue())
                    .containsExactly(
                            tuple("short", 96L, 46, 59),
                            tuple("medium", 447L, 60, 120),
                            tuple("long", 457L, 121, 185));
        }
    }
}
