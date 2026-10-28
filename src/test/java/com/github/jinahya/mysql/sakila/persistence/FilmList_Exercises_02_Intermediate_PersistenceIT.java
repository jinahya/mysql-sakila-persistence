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
 * Answers the exercises of {@code src/test/sql/exercises/film_list_exercises_02_intermediate.sql}, against
 * the installed {@code sakila} database; a nested class per exercise, which answers it with JPQL
 * and with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class FilmList_Exercises_02_Intermediate_PersistenceIT {

    @DisplayName("SAKILA_FILM_LIST_I_01: whether the view has a row for every film which has a category")
    @Nested
    class SAKILA_FILM_LIST_I_01_Test {

        // the view is not an entity; the FilmList class is a plain class, read with a native query
        @Test
        void nativeQuery() {
            final var list = __Exercises_TestUtils.apply(em -> (List<?>) em.createNativeQuery("""
                                                                                                      SELECT (SELECT COUNT(*) FROM film_list) AS film_list,
                                                                                                             (SELECT COUNT(DISTINCT film_id) FROM film_category) AS films_with_a_category""")
                    .getResultList());
            list.forEach(r -> log.debug("{}", r instanceof Object[] a ? Arrays.toString(a) : r));
            assertThat(list).singleElement().satisfies(
                    r -> assertThat((Object[]) r).extracting(v -> ((Number) v).longValue())
                            .containsExactly(1000L, 1000L));
        }
    }

    @DisplayName("SAKILA_FILM_LIST_I_02: how many films of each rating there are at each price")
    @Nested
    class SAKILA_FILM_LIST_I_02_Test {

        // the view is not an entity; the FilmList class is a plain class, read with a native query
        @Test
        void nativeQuery() {
            final var list = __Exercises_TestUtils.apply(em -> (List<?>) em.createNativeQuery("""
                                                                                                      SELECT rating,
                                                                                                             SUM(CASE WHEN price = 0.99 THEN 1 ELSE 0 END) AS price_0_99,
                                                                                                             SUM(CASE WHEN price = 2.99 THEN 1 ELSE 0 END) AS price_2_99,
                                                                                                             SUM(CASE WHEN price = 4.99 THEN 1 ELSE 0 END) AS price_4_99
                                                                                                      FROM film_list
                                                                                                      GROUP BY rating
                                                                                                      ORDER BY rating""")
                    .getResultList());
            list.forEach(r -> log.debug("{}", r instanceof Object[] a ? Arrays.toString(a) : r));
            assertThat(list)
                    .extracting(r -> ((Object[]) r)[0],
                                r -> ((Number) ((Object[]) r)[1]).longValue(),
                                r -> ((Number) ((Object[]) r)[2]).longValue(),
                                r -> ((Number) ((Object[]) r)[3]).longValue())
                    .containsExactly(
                            tuple("G", 64L, 59L, 55L),
                            tuple("PG", 62L, 64L, 68L),
                            tuple("PG-13", 72L, 74L, 77L),
                            tuple("R", 70L, 60L, 65L),
                            tuple("NC-17", 73L, 66L, 71L));
        }
    }

    @DisplayName(
            "SAKILA_FILM_LIST_I_03: the films whose actors string lists a different number of actors than film_actor")
    @Nested
    class SAKILA_FILM_LIST_I_03_Test {

        // the view is not an entity; the FilmList class is a plain class, read with a native query
        @Test
        void nativeQuery() {
            final var list = __Exercises_TestUtils.apply(em -> (List<?>) em.createNativeQuery("""
                                                                                                      SELECT l.*
                                                                                                      FROM film_list l
                                                                                                      WHERE l.actors IS NOT NULL
                                                                                                        AND LENGTH(l.actors) - LENGTH(REPLACE(l.actors, ',', '')) + 1
                                                                                                                <> (SELECT COUNT(*) FROM film_actor fa WHERE fa.film_id = l.FID)
                                                                                                      ORDER BY l.FID""")
                    .getResultList());
            list.forEach(r -> log.debug("{}", r instanceof Object[] a ? Arrays.toString(a) : r));
            assertThat(list).isEmpty();
        }
    }
}
