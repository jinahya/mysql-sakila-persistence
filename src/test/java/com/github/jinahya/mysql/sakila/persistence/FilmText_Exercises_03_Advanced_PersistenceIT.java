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
 * Answers the exercises of {@code src/test/sql/exercises/film_text_exercises_03_advanced.sql}, against the
 * installed {@code sakila} database; a nested class per exercise, which answers it with JPQL and
 * with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class FilmText_Exercises_03_Advanced_PersistenceIT {

    @DisplayName("SAKILA_FILM_TEXT_A_01: the most common kind of story in each category")
    @Nested
    class SAKILA_FILM_TEXT_A_01_Test {

        // JPQL has no window functions, nor a subquery in the FROM clause; ROW_NUMBER() OVER
        // picks one kind per category, ties broken by the kind
        @Test
        void nativeQuery() {
            final var list = __Exercises_TestUtils.apply(em -> (List<?>) em.createNativeQuery("""
                                                                                                      WITH kinds AS (SELECT c.name AS category,
                                                                                                                            SUBSTRING_INDEX(SUBSTRING_INDEX(ft.description, ' ', 3), ' ', -1) AS kind,
                                                                                                                            COUNT(*) AS films
                                                                                                                     FROM film_text ft
                                                                                                                              JOIN film_category fc ON fc.film_id = ft.film_id
                                                                                                                              JOIN category c ON c.category_id = fc.category_id
                                                                                                                     GROUP BY c.name, kind),
                                                                                                           ranked AS (SELECT category, kind, films,
                                                                                                                             ROW_NUMBER() OVER (PARTITION BY category ORDER BY films DESC, kind) AS rn
                                                                                                                      FROM kinds)
                                                                                                      SELECT category, kind, films
                                                                                                      FROM ranked
                                                                                                      WHERE rn = 1
                                                                                                      ORDER BY category""")
                    .getResultList());
            list.forEach(r -> log.debug("{}", r instanceof Object[] a ? Arrays.toString(a) : r));
            assertThat(list)
                    .extracting(r -> ((Object[]) r)[0], r -> ((Object[]) r)[1],
                                r -> ((Number) ((Object[]) r)[2]).longValue())
                    .containsExactly(
                            tuple("Action", "Reflection", 10L),
                            tuple("Animation", "Epistle", 9L),
                            tuple("Children", "Documentary", 9L),
                            tuple("Classics", "Epistle", 8L),
                            tuple("Comedy", "Panorama", 9L),
                            tuple("Documentary", "Story", 10L),
                            tuple("Drama", "Display", 12L),
                            tuple("Family", "Reflection", 11L),
                            tuple("Foreign", "Drama", 12L),
                            tuple("Games", "Display", 10L),
                            tuple("Horror", "Display", 8L),
                            tuple("Music", "Drama", 7L),
                            tuple("New", "Story", 11L),
                            tuple("Sci-Fi", "Character", 8L),
                            tuple("Sports", "Character", 11L),
                            tuple("Travel", "Drama", 9L));
        }
    }

    @DisplayName("SAKILA_FILM_TEXT_A_02: the chain of titles from ACADEMY DINOSAUR")
    @Nested
    class SAKILA_FILM_TEXT_A_02_Test {

        // JPQL has no recursive common table expression; WITH RECURSIVE follows the chain
        @Test
        void nativeQuery() {
            final var list = __Exercises_TestUtils.apply(em -> (List<?>) em.createNativeQuery("""
                                                                                                      WITH RECURSIVE chain AS (SELECT 1 AS step, film_id, title
                                                                                                                               FROM film_text
                                                                                                                               WHERE film_id = ?1
                                                                                                                               UNION ALL
                                                                                                                               SELECT c.step + 1, ft.film_id, ft.title
                                                                                                                               FROM chain c
                                                                                                                                        JOIN film_text ft ON SUBSTRING_INDEX(ft.title, ' ', 1) = SUBSTRING_INDEX(c.title, ' ', -1)
                                                                                                                               WHERE c.step < ?2)
                                                                                                      SELECT step, film_id, title
                                                                                                      FROM chain
                                                                                                      ORDER BY step""")
                    .setParameter(1, 1)
                    .setParameter(2, 10)
                    .getResultList());
            list.forEach(r -> log.debug("{}", r instanceof Object[] a ? Arrays.toString(a) : r));
            assertThat(list)
                    .extracting(r -> ((Number) ((Object[]) r)[0]).intValue(),
                                r -> ((Number) ((Object[]) r)[1]).intValue(),
                                r -> ((Object[]) r)[2])
                    .containsExactly(
                            tuple(1, 1, "ACADEMY DINOSAUR"),
                            tuple(2, 231, "DINOSAUR SECRETARY"),
                            tuple(3, 777, "SECRETARY ROUGE"),
                            tuple(4, 746, "ROUGE SQUAD"),
                            tuple(5, 836, "SQUAD FISH"),
                            tuple(6, 319, "FISH OPUS"),
                            tuple(7, 640, "OPUS ICE"),
                            tuple(8, 447, "ICE CROSSING"),
                            tuple(9, 192, "CROSSING DIVORCE"),
                            tuple(10, 237, "DIVORCE SHINING"));
        }
    }
}
