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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers the exercises of {@code src/test/sql/exercises/film_category_exercises_02_intermediate.sql},
 * against the installed {@code sakila} database; a nested class per exercise, which answers it with
 * JPQL and with the Criteria API, or with a native query where neither can, and logs what it
 * gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class FilmCategory_Exercises_02_Intermediate_PersistenceIT {

    @DisplayName("SAKILA_FILM_CATEGORY_I_01: the films with more than one category")
    @Nested
    class SAKILA_FILM_CATEGORY_I_01_Test {

        @ParameterizedTest
        @ValueSource(longs = {0, 1})
        void __QueryLanguage(final long count) {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT f, COUNT(fc)
                                                                                      FROM FilmCategory fc
                                                                                               JOIN fc.film f
                                                                                      GROUP BY f
                                                                                      HAVING COUNT(fc) > :count""",
                                                                              Object[].class)
                    .setParameter("count", count)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list).allSatisfy(
                    r -> assertThat(((Number) r[1]).longValue()).isGreaterThan(count));
        }

        @ParameterizedTest
        @ValueSource(longs = {0, 1})
        void __CriteriaApi(final long count) {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var fc = cq.from(FilmCategory.class);
                final var f = fc.join(FilmCategory_.film);
                cq.select(cb.array(f, cb.count(fc)))
                        .groupBy(f)
                        .having(cb.gt(cb.count(fc), cb.parameter(Long.class, "count")));
                return em.createQuery(cq)
                        .setParameter("count", count)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list).allSatisfy(
                    r -> assertThat(((Number) r[1]).longValue()).isGreaterThan(count));
        }
    }

    @DisplayName("SAKILA_FILM_CATEGORY_I_02: how many films have no category")
    @Nested
    class SAKILA_FILM_CATEGORY_I_02_Test {

        @Test
        void __QueryLanguage() {
            final var value = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                       SELECT COUNT(f)
                                                                                       FROM Film f
                                                                                       WHERE f.filmCategories IS EMPTY""",
                                                                               Long.class)
                    .getSingleResult());
            log.debug("{}", value);
            assertThat(value).isEqualTo(0L);
        }

        @Test
        void __CriteriaApi() {
            final var value = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Long.class);
                final var f = cq.from(Film.class);
                cq.select(cb.count(f))
                        .where(cb.isEmpty(f.get(Film_.filmCategories)));
                return em.createQuery(cq)
                        .getSingleResult();
            });
            log.debug("{}", value);
            assertThat(value).isEqualTo(0L);
        }
    }

    @DisplayName("SAKILA_FILM_CATEGORY_I_03: how many films each category has, and how many of them are rated R")
    @Nested
    class SAKILA_FILM_CATEGORY_I_03_Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT c, COUNT(fc), SUM(CASE WHEN f.rating = :rating THEN 1 ELSE 0 END)
                                                                                      FROM FilmCategory fc
                                                                                               JOIN fc.category c
                                                                                               JOIN fc.film f
                                                                                      GROUP BY c
                                                                                      ORDER BY c.name""",
                                                                              Object[].class)
                    .setParameter("rating", Film.Rating.R)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Category) r[0]).getName(), r -> ((Number) r[1]).longValue(),
                                r -> ((Number) r[2]).longValue())
                    .containsExactly(
                            tuple("Action", 64L, 14L),
                            tuple("Animation", 66L, 8L),
                            tuple("Children", 60L, 9L),
                            tuple("Classics", 57L, 14L),
                            tuple("Comedy", 58L, 8L),
                            tuple("Documentary", 68L, 13L),
                            tuple("Drama", 62L, 6L),
                            tuple("Family", 69L, 16L),
                            tuple("Foreign", 73L, 15L),
                            tuple("Games", 61L, 15L),
                            tuple("Horror", 56L, 14L),
                            tuple("Music", 51L, 11L),
                            tuple("New", 63L, 9L),
                            tuple("Sci-Fi", 61L, 17L),
                            tuple("Sports", 74L, 16L),
                            tuple("Travel", 57L, 10L));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var fc = cq.from(FilmCategory.class);
                final var c = fc.join(FilmCategory_.category);
                final var f = fc.join(FilmCategory_.film);
                final var rated = cb.<Integer>selectCase()
                        .when(cb.equal(f.get(Film_.rating),
                                       cb.parameter(Film.Rating.class, "rating")), 1)
                        .otherwise(0);
                cq.select(cb.array(c, cb.count(fc), cb.sum(rated)))
                        .groupBy(c)
                        .orderBy(cb.asc(c.get(Category_.name)));
                return em.createQuery(cq)
                        .setParameter("rating", Film.Rating.R)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Category) r[0]).getName(), r -> ((Number) r[1]).longValue(),
                                r -> ((Number) r[2]).longValue())
                    .containsExactly(
                            tuple("Action", 64L, 14L),
                            tuple("Animation", 66L, 8L),
                            tuple("Children", 60L, 9L),
                            tuple("Classics", 57L, 14L),
                            tuple("Comedy", 58L, 8L),
                            tuple("Documentary", 68L, 13L),
                            tuple("Drama", 62L, 6L),
                            tuple("Family", 69L, 16L),
                            tuple("Foreign", 73L, 15L),
                            tuple("Games", 61L, 15L),
                            tuple("Horror", 56L, 14L),
                            tuple("Music", 51L, 11L),
                            tuple("New", 63L, 9L),
                            tuple("Sci-Fi", 61L, 17L),
                            tuple("Sports", 74L, 16L),
                            tuple("Travel", 57L, 10L));
        }
    }
}
