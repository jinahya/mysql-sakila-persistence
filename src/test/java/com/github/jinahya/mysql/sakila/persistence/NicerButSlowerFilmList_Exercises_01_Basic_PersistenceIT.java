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
 * Answers the exercises of {@code src/test/sql/exercises/nicer_but_slower_film_list_exercises_01_basic.sql},
 * against the installed {@code sakila} database; a nested class per exercise, which answers it with
 * JPQL and with the Criteria API, or with a native query where neither can, and logs what it
 * gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class NicerButSlowerFilmList_Exercises_01_Basic_PersistenceIT {

    @DisplayName("SAKILA_NICER_BUT_SLOWER_FILM_LIST_B_01: the actors of the film 1, as this view writes them")
    @Nested
    class SAKILA_NICER_BUT_SLOWER_FILM_LIST_B_01_Test {

        // the view is not an entity; the NicerButSlowerFilmList class is a plain class, read with a
        // native query
        @ParameterizedTest
        @ValueSource(ints = {1, 2, 3})
        void nativeQuery(final int fid) {
            final var list = __Exercises_TestUtils.apply(em -> (List<?>) em.createNativeQuery("""
                                                                                                      SELECT *
                                                                                                      FROM nicer_but_slower_film_list
                                                                                                      WHERE FID = ?1""")
                    .setParameter(1, fid)
                    .getResultList());
            list.forEach(r -> log.debug("{}", r instanceof Object[] a ? Arrays.toString(a) : r));
            assertThat(list).singleElement().satisfies(
                    r -> assertThat(((Number) ((Object[]) r)[0]).intValue()).isEqualTo(fid));
        }
    }

    @DisplayName("SAKILA_NICER_BUT_SLOWER_FILM_LIST_B_02: how many films PENELOPE GUINESS plays in, by the view's actors")
    @Nested
    class SAKILA_NICER_BUT_SLOWER_FILM_LIST_B_02_Test {

        // the view is not an entity; the NicerButSlowerFilmList class is a plain class, read with a
        // native query
        @Test
        void nativeQuery() {
            final var list = __Exercises_TestUtils.apply(em -> (List<?>) em.createNativeQuery("""
                                                                                                      SELECT COUNT(*) AS films
                                                                                                      FROM nicer_but_slower_film_list
                                                                                                      WHERE actors LIKE ?1""")
                    .setParameter(1, "%Penelope Guiness%")
                    .getResultList());
            list.forEach(r -> log.debug("{}", r instanceof Object[] a ? Arrays.toString(a) : r));
            assertThat(list).singleElement()
                    .satisfies(v -> assertThat(((Number) v).longValue()).isEqualTo(19L));
        }
    }
}
