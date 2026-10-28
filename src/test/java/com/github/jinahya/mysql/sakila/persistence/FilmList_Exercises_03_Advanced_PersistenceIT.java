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
 * Answers the exercises of {@code src/test/sql/exercises/film_list_exercises_03_advanced.sql}, against the
 * installed {@code sakila} database; a nested class per exercise, which answers it with JPQL and
 * with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class FilmList_Exercises_03_Advanced_PersistenceIT {

    @DisplayName(
            "SAKILA_FILM_LIST_A_01: how many films of each category are longer than their category's average")
    @Nested
    class SAKILA_FILM_LIST_A_01_Test {

        // the view is not an entity; the FilmList class is a plain class, read with a native query
        @Test
        void nativeQuery() {
            final var list = __Exercises_TestUtils.apply(em -> (List<?>) em.createNativeQuery("""
                                                                                                      SELECT l.category, COUNT(*) AS longer_than_average
                                                                                                      FROM film_list l
                                                                                                      WHERE l.length > (SELECT AVG(o.length) FROM film_list o WHERE o.category = l.category)
                                                                                                      GROUP BY l.category
                                                                                                      ORDER BY longer_than_average DESC, l.category""")
                    .getResultList());
            list.forEach(r -> log.debug("{}", r instanceof Object[] a ? Arrays.toString(a) : r));
            assertThat(list)
                    .extracting(r -> ((Object[]) r)[0],
                                r -> ((Number) ((Object[]) r)[1]).longValue())
                    .containsExactly(
                            tuple("Sports", 42L),
                            tuple("Foreign", 37L),
                            tuple("Family", 36L),
                            tuple("New", 34L),
                            tuple("Animation", 32L),
                            tuple("Documentary", 31L),
                            tuple("Drama", 31L),
                            tuple("Sci-Fi", 31L),
                            tuple("Action", 30L),
                            tuple("Games", 30L),
                            tuple("Travel", 30L),
                            tuple("Children", 28L),
                            tuple("Comedy", 28L),
                            tuple("Horror", 28L),
                            tuple("Classics", 27L),
                            tuple("Music", 25L));
        }
    }
}
