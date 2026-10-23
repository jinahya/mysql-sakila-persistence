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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers the exercises of {@code src/test/sql/film_category_exercises_01_basic.sql}, against the
 * installed {@code sakila} database; a nested class per exercise, which answers it with JPQL and
 * with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class FilmCategory_Exercises_01_Basic_PersistenceIT {

    @DisplayName("exercise 1: how many categories the films are in")
    @Nested
    class Exercise1Test {

        @Test
        void __QueryLanguage() {
            final var value = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                       SELECT COUNT(DISTINCT fc.categoryId)
                                                                                       FROM FilmCategory fc""",
                                                                               Long.class)
                    .getSingleResult());
            log.debug("{}", value);
            assertThat(value).isEqualTo(16L);
        }

        @Test
        void __CriteriaApi() {
            final var value = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Long.class);
                final var fc = cq.from(FilmCategory.class);
                cq.select(cb.countDistinct(fc.get(FilmCategory_.categoryId)));
                return em.createQuery(cq)
                        .getSingleResult();
            });
            log.debug("{}", value);
            assertThat(value).isEqualTo(16L);
        }
    }

    @DisplayName("exercise 2: the categories of the films 1 to 10")
    @Nested
    class Exercise2Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT fc.filmId, fc.categoryId
                                                                                      FROM FilmCategory fc
                                                                                      WHERE fc.filmId BETWEEN :min AND :max
                                                                                      ORDER BY fc.filmId""",
                                                                              Object[].class)
                    .setParameter("min", 1)
                    .setParameter("max", 10)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> ((Number) r[1]).intValue())
                    .containsExactly(
                            tuple(1, 6),
                            tuple(2, 11),
                            tuple(3, 6),
                            tuple(4, 11),
                            tuple(5, 8),
                            tuple(6, 9),
                            tuple(7, 5),
                            tuple(8, 11),
                            tuple(9, 11),
                            tuple(10, 15));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var fc = cq.from(FilmCategory.class);
                cq.select(cb.array(fc.get(FilmCategory_.filmId), fc.get(FilmCategory_.categoryId)))
                        .where(cb.between(fc.get(FilmCategory_.filmId),
                                          cb.parameter(Integer.class, "min"),
                                          cb.parameter(Integer.class, "max")))
                        .orderBy(cb.asc(fc.get(FilmCategory_.filmId)));
                return em.createQuery(cq)
                        .setParameter("min", 1)
                        .setParameter("max", 10)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> ((Number) r[1]).intValue())
                    .containsExactly(
                            tuple(1, 6),
                            tuple(2, 11),
                            tuple(3, 6),
                            tuple(4, 11),
                            tuple(5, 8),
                            tuple(6, 9),
                            tuple(7, 5),
                            tuple(8, 11),
                            tuple(9, 11),
                            tuple(10, 15));
        }
    }
}
