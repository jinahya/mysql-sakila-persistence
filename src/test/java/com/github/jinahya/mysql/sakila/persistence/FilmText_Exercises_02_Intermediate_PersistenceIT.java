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
 * Answers the exercises of {@code src/test/sql/exercises/film_text_exercises_02_intermediate.sql}, against
 * the installed {@code sakila} database; a nested class per exercise, which answers it with JPQL
 * and with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class FilmText_Exercises_02_Intermediate_PersistenceIT {

    @DisplayName("SAKILA_FILM_TEXT_I_01: whether film_text has a row for every film")
    @Nested
    class SAKILA_FILM_TEXT_I_01_Test {

        // JPQL has no subquery in the SELECT clause; the two counts are two queries
        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> List.<Object[]>of(new Object[]{
                    em.createQuery("SELECT COUNT(e) FROM FilmText e", Long.class).getSingleResult(),
                    em.createQuery("SELECT COUNT(e) FROM Film e", Long.class).getSingleResult()}));
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).longValue(), r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple(1000L, 1000L));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> List.<Object[]>of(new Object[]{
                    __Persistence_TestUtils.selectEntityCount(em, FilmText.class),
                    __Persistence_TestUtils.selectEntityCount(em, Film.class)}));
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).longValue(), r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple(1000L, 1000L));
        }
    }

    @DisplayName("SAKILA_FILM_TEXT_I_02: how many films mention a Drama, by the full-text index")
    @Nested
    class SAKILA_FILM_TEXT_I_02_Test {

        // JPQL has no full-text search; MATCH ... AGAINST is MySQL's own; the Criteria API has no
        // full-text search; MATCH ... AGAINST is MySQL's own
        @ParameterizedTest
        @ValueSource(strings = {"drama", "epic", "documentary"})
        void nativeQuery(final String word) {
            final var list = __Exercises_TestUtils.apply(em -> (List<?>) em.createNativeQuery("""
                                                                                                      SELECT COUNT(*) AS films
                                                                                                      FROM film_text
                                                                                                      WHERE MATCH(title, description) AGAINST (?1)""")
                    .setParameter(1, word)
                    .getResultList());
            list.forEach(r -> log.debug("{}", r instanceof Object[] a ? Arrays.toString(a) : r));
            assertThat(list).singleElement()
                    .satisfies(v -> assertThat(((Number) v).longValue()).isNotNegative());
        }
    }

    @DisplayName("SAKILA_FILM_TEXT_I_03: the films whose film_text title differs from their title")
    @Nested
    class SAKILA_FILM_TEXT_I_03_Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT f, ft
                                                                                      FROM FilmText ft
                                                                                               JOIN ft.film f
                                                                                      WHERE ft.title <> f.title""",
                                                                              Object[].class)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list).isEmpty();
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var ft = cq.from(FilmText.class);
                final var f = ft.join(FilmText_.film);
                cq.select(cb.array(f, ft))
                        .where(cb.notEqual(ft.get(FilmText_.title), f.get(Film_.title)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list).isEmpty();
        }
    }

    @DisplayName(
            "SAKILA_FILM_TEXT_I_04: how many films mention a Boat but not a Shark, by the full-text index")
    @Nested
    class SAKILA_FILM_TEXT_I_04_Test {

        // JPQL has no full-text search; MATCH ... AGAINST ... IN BOOLEAN MODE is MySQL's own
        @Test
        void nativeQuery() {
            final var list = __Exercises_TestUtils.apply(em -> (List<?>) em.createNativeQuery("""
                                                                                                      SELECT COUNT(*) AS films
                                                                                                      FROM film_text
                                                                                                      WHERE MATCH(title, description) AGAINST (?1 IN BOOLEAN MODE)""")
                    .setParameter(1, "+boat -shark")
                    .getResultList());
            list.forEach(r -> log.debug("{}", r instanceof Object[] a ? Arrays.toString(a) : r));
            assertThat(list).singleElement()
                    .satisfies(v -> assertThat(((Number) v).longValue()).isEqualTo(143L));
        }
    }

    @DisplayName(
            "SAKILA_FILM_TEXT_I_05: how many films mention a Crocodile or a Shark, and how many mention both")
    @Nested
    class SAKILA_FILM_TEXT_I_05_Test {

        // JPQL has no full-text search; MATCH ... AGAINST is MySQL's own
        @Test
        void nativeQuery() {
            final var list = __Exercises_TestUtils.apply(em -> (List<?>) em.createNativeQuery("""
                                                                                                      SELECT (SELECT COUNT(*) FROM film_text WHERE MATCH(title, description) AGAINST (?1)) AS either_word,
                                                                                                             (SELECT COUNT(*)
                                                                                                              FROM film_text
                                                                                                              WHERE MATCH(title, description) AGAINST (?2 IN BOOLEAN MODE)) AS both_words""")
                    .setParameter(1, "crocodile shark")
                    .setParameter(2, "+crocodile +shark")
                    .getResultList());
            list.forEach(r -> log.debug("{}", r instanceof Object[] a ? Arrays.toString(a) : r));
            assertThat(list).singleElement().satisfies(
                    r -> assertThat((Object[]) r).extracting(v -> ((Number) v).longValue())
                            .containsExactly(203L, 10L));
        }
    }
}
