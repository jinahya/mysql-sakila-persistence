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
 * Answers the exercises of {@code src/test/sql/film_actor_exercises_02_intermediate.sql}, against
 * the installed {@code sakila} database; a nested class per exercise, which answers it with JPQL
 * and with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class FilmActor_Exercises_02_Intermediate_PersistenceIT {

    @DisplayName("SAKILA_FILM_ACTOR_I_01: the five films with the most actors")
    @Nested
    class SAKILA_FILM_ACTOR_I_01_Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT f, COUNT(fa)
                                                                                      FROM Film f
                                                                                               JOIN f.filmActors fa
                                                                                      GROUP BY f
                                                                                      ORDER BY COUNT(fa) DESC, f.filmId""",
                                                                              Object[].class)
                    .setMaxResults(5)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Film) r[0]).getFilmId(), r -> ((Film) r[0]).getTitle(),
                                r -> ((Number) r[1]).longValue())
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
                cq.select(cb.array(f, cb.count(fa)))
                        .groupBy(f)
                        .orderBy(cb.desc(cb.count(fa)), cb.asc(f.get(Film_.filmId)));
                return em.createQuery(cq)
                        .setMaxResults(5)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Film) r[0]).getFilmId(), r -> ((Film) r[0]).getTitle(),
                                r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple(508, "LAMBS CINCINATTI", 15L),
                            tuple(87, "BOONDOCK BALLROOM", 13L),
                            tuple(146, "CHITTY LOCK", 13L),
                            tuple(188, "CRAZY HOME", 13L),
                            tuple(249, "DRACULA CRYSTAL", 13L));
        }
    }

    @DisplayName("SAKILA_FILM_ACTOR_I_02: how many actors a film has, on average")
    @Nested
    class SAKILA_FILM_ACTOR_I_02_Test {

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

    @DisplayName("SAKILA_FILM_ACTOR_I_03: the five actors who appear most often with the actor 1")
    @Nested
    class SAKILA_FILM_ACTOR_I_03_Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT a, COUNT(fa2)
                                                                                      FROM FilmActor fa1, FilmActor fa2
                                                                                               JOIN fa2.actor a
                                                                                      WHERE fa2.film = fa1.film
                                                                                        AND fa2.actor <> fa1.actor
                                                                                        AND fa1.actor.actorId = :actorId
                                                                                      GROUP BY a
                                                                                      ORDER BY COUNT(fa2) DESC, a.actorId""",
                                                                              Object[].class)
                    .setParameter("actorId", 1)
                    .setMaxResults(5)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Actor) r[0]).getActorId(),
                                r -> ((Actor) r[0]).getFirstName(),
                                r -> ((Actor) r[0]).getLastName(),
                                r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple(4, "JENNIFER", "DAVIS", 4L),
                            tuple(20, "LUCILLE", "TRACY", 3L),
                            tuple(64, "RAY", "JOHANSSON", 3L),
                            tuple(6, "BETTE", "NICHOLSON", 2L),
                            tuple(10, "CHRISTIAN", "GABLE", 2L));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var fa1 = cq.from(FilmActor.class);
                final var fa2 = cq.from(FilmActor.class);
                final var a = fa2.join(FilmActor_.actor);
                cq.select(cb.array(a, cb.count(fa2)))
                        .where(cb.equal(fa2.get(FilmActor_.film), fa1.get(FilmActor_.film)),
                               cb.notEqual(fa2.get(FilmActor_.actor), fa1.get(FilmActor_.actor)),
                               cb.equal(fa1.get(FilmActor_.actor).get(Actor_.actorId),
                                        cb.parameter(Integer.class, "actorId")))
                        .groupBy(a)
                        .orderBy(cb.desc(cb.count(fa2)), cb.asc(a.get(Actor_.actorId)));
                return em.createQuery(cq)
                        .setParameter("actorId", 1)
                        .setMaxResults(5)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Actor) r[0]).getActorId(),
                                r -> ((Actor) r[0]).getFirstName(),
                                r -> ((Actor) r[0]).getLastName(),
                                r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple(4, "JENNIFER", "DAVIS", 4L),
                            tuple(20, "LUCILLE", "TRACY", 3L),
                            tuple(64, "RAY", "JOHANSSON", 3L),
                            tuple(6, "BETTE", "NICHOLSON", 2L),
                            tuple(10, "CHRISTIAN", "GABLE", 2L));
        }
    }

    @DisplayName("SAKILA_FILM_ACTOR_I_04: how many films have one actor, two actors, and so on")
    @Nested
    class SAKILA_FILM_ACTOR_I_04_Test {

        // JPQL has no subquery in the FROM clause, nor a GROUP BY of a SIZE; the films are counted
        // by a count of their own
        @Test
        void nativeQuery() {
            final var list = __Exercises_TestUtils.apply(em -> (List<?>) em.createNativeQuery("""
                                                                                                      SELECT actors, COUNT(*) AS films
                                                                                                      FROM (SELECT film_id, COUNT(*) AS actors FROM film_actor GROUP BY film_id) t
                                                                                                      GROUP BY actors
                                                                                                      ORDER BY actors""")
                    .getResultList());
            list.forEach(r -> log.debug("{}", r instanceof Object[] a ? Arrays.toString(a) : r));
            assertThat(list)
                    .extracting(r -> ((Number) ((Object[]) r)[0]).intValue(),
                                r -> ((Number) ((Object[]) r)[1]).longValue())
                    .containsExactly(
                            tuple(1, 21L),
                            tuple(2, 69L),
                            tuple(3, 119L),
                            tuple(4, 137L),
                            tuple(5, 195L),
                            tuple(6, 150L),
                            tuple(7, 119L),
                            tuple(8, 90L),
                            tuple(9, 49L),
                            tuple(10, 21L),
                            tuple(11, 14L),
                            tuple(12, 6L),
                            tuple(13, 6L),
                            tuple(15, 1L));
        }
    }
}
