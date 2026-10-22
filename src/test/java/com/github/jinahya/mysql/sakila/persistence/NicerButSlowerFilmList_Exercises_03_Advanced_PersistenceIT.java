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

/**
 * Answers the exercises of
 * {@code src/test/sql/nicer_but_slower_film_list_exercises_03_advanced.sql}, against the installed
 * {@code sakila} database; a nested class per exercise, which answers it with JPQL and with the
 * Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class NicerButSlowerFilmList_Exercises_03_Advanced_PersistenceIT {

    @DisplayName(
            "exercise 1: how many rows differ from film_list, column by column, but for the actors")
    @Nested
    class Exercise1Test {

        // the view is not an entity; the NicerButSlowerFilmList class is a plain class, read with a
        // native query
        @Test
        void nativeQuery() {
            final var list = __Exercises_TestUtils.apply(em -> (List<?>) em.createNativeQuery("""
                                                                                                      SELECT COUNT(*) AS differing
                                                                                                      FROM nicer_but_slower_film_list n
                                                                                                               JOIN film_list l ON l.FID = n.FID AND l.category = n.category
                                                                                                      WHERE NOT (n.title <=> l.title AND n.description <=> l.description AND n.price <=> l.price
                                                                                                                 AND n.length <=> l.length AND n.rating <=> l.rating)""")
                    .getResultList());
            list.forEach(r -> log.debug("{}", r instanceof Object[] a ? Arrays.toString(a) : r));
            assertThat(list).singleElement()
                    .satisfies(v -> assertThat(((Number) v).longValue()).isEqualTo(0L));
        }
    }
}
