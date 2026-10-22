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
 * Answers the exercises of {@code src/test/sql/film_actor_exercises_02_intermediate.sql}, against
 * the installed {@code sakila} database; a nested class per exercise, which answers it with JPQL
 * and with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class FilmActor_Exercises_02_Intermediate_PersistenceIT {

    @DisplayName("exercise 1: the five films with the most actors")
    @Nested
    class Exercise1Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT f.filmId, f.title, COUNT(fa)
                                                                                      FROM Film f
                                                                                               JOIN f.filmActors fa
                                                                                      GROUP BY f.filmId, f.title
                                                                                      ORDER BY COUNT(fa) DESC, f.filmId""",
                                                                              Object[].class)
                    .setMaxResults(5)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> r[1],
                                r -> ((Number) r[2]).longValue())
                    .containsExactly(
                            tuple(508, "LAMBS CINCINATTI", 15L),
                            tuple(87, "BOONDOCK BALLROOM", 13L),
                            tuple(146, "CHITTY LOCK", 13L),
                            tuple(188, "CRAZY HOME", 13L),
                            tuple(249, "DRACULA CRYSTAL", 13L));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var f = cq.from(Film.class);
                final var fa = f.join(Film_.filmActors);
                cq.select(cb.array(f.get(Film_.filmId), f.get(Film_.title), cb.count(fa)))
                        .groupBy(f.get(Film_.filmId), f.get(Film_.title))
                        .orderBy(cb.desc(cb.count(fa)), cb.asc(f.get(Film_.filmId)));
                return em.createQuery(cq)
                        .setMaxResults(5)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> r[1],
                                r -> ((Number) r[2]).longValue())
                    .containsExactly(
                            tuple(508, "LAMBS CINCINATTI", 15L),
                            tuple(87, "BOONDOCK BALLROOM", 13L),
                            tuple(146, "CHITTY LOCK", 13L),
                            tuple(188, "CRAZY HOME", 13L),
                            tuple(249, "DRACULA CRYSTAL", 13L));
        }
    }

    @DisplayName("exercise 2: how many actors a film has, on average")
    @Nested
    class Exercise2Test {

        // JPQL has no subquery in the FROM clause; the average is the number of rows over the
        // number
        // of films
        @Test
        void __QueryLanguage() {
            final var value = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                       SELECT ROUND(COUNT(fa) * 1.0 / COUNT(DISTINCT fa.film), 2)
                                                                                       FROM FilmActor fa""",
                                                                               Number.class)
                    .getSingleResult());
            log.debug("{}", value);
            assertThat(value.doubleValue()).isEqualTo(5.48d);
        }

        @Test
        void __CriteriaApi() {
            final var value = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Number.class);
                final var fa = cq.from(FilmActor.class);
                cq.select(cb.round(cb.quot(cb.prod(cb.count(fa), 1.0),
                                           cb.countDistinct(fa.get(FilmActor_.film))), 2));
                return em.createQuery(cq)
                        .getSingleResult();
            });
            log.debug("{}", value);
            assertThat(value.doubleValue()).isEqualTo(5.48d);
        }
    }
}
