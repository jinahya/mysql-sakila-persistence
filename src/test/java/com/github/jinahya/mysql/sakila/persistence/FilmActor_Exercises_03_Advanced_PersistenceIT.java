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
 * Answers the exercises of {@code src/test/sql/film_actor_exercises_03_advanced.sql}, against the
 * installed {@code sakila} database; a nested class per exercise, which answers it with JPQL and
 * with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class FilmActor_Exercises_03_Advanced_PersistenceIT {

    @DisplayName("SAKILA_FILM_ACTOR_A_01: the three pairs of actors who appear together the most")
    @Nested
    class SAKILA_FILM_ACTOR_A_01_Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT a1, a2, COUNT(fa1)
                                                                                      FROM FilmActor fa1
                                                                                               JOIN fa1.actor a1,
                                                                                           FilmActor fa2
                                                                                               JOIN fa2.actor a2
                                                                                      WHERE fa2.film = fa1.film
                                                                                        AND a2.actorId > a1.actorId
                                                                                      GROUP BY a1, a2
                                                                                      ORDER BY COUNT(fa1) DESC, a1.actorId, a2.actorId""",
                                                                              Object[].class)
                    .setMaxResults(3)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Actor) r[0]).getActorId(),
                                r -> ((Actor) r[1]).getActorId(),
                                r -> ((Number) r[2]).longValue())
                    .containsExactly(
                            tuple(27, 60, 7L),
                            tuple(15, 198, 6L),
                            tuple(21, 108, 6L));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var fa1 = cq.from(FilmActor.class);
                final var fa2 = cq.from(FilmActor.class);
                final var a1 = fa1.join(FilmActor_.actor);
                final var a2 = fa2.join(FilmActor_.actor);
                final var actorId1 = a1.get(Actor_.actorId);
                final var actorId2 = a2.get(Actor_.actorId);
                cq.select(cb.array(a1, a2, cb.count(fa1)))
                        .where(cb.equal(fa2.get(FilmActor_.film), fa1.get(FilmActor_.film)),
                               cb.gt(actorId2, actorId1))
                        .groupBy(a1, a2)
                        .orderBy(cb.desc(cb.count(fa1)), cb.asc(actorId1), cb.asc(actorId2));
                return em.createQuery(cq)
                        .setMaxResults(3)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Actor) r[0]).getActorId(),
                                r -> ((Actor) r[1]).getActorId(),
                                r -> ((Number) r[2]).longValue())
                    .containsExactly(
                            tuple(27, 60, 7L),
                            tuple(15, 198, 6L),
                            tuple(21, 108, 6L));
        }
    }

    @DisplayName("SAKILA_FILM_ACTOR_A_02: the actor who appears in the most films of each category")
    @Nested
    class SAKILA_FILM_ACTOR_A_02_Test {

        // JPQL has no window functions; the actor of each category is the one whom no actor
        // outnumbers, and whom no actor with a smaller id equals
        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT c, a, COUNT(fa)
                                                                                      FROM FilmActor fa
                                                                                               JOIN fa.actor a,
                                                                                           FilmCategory fc
                                                                                               JOIN fc.category c
                                                                                      WHERE fc.film = fa.film
                                                                                      GROUP BY c, a
                                                                                      HAVING COUNT(fa) >= ALL (SELECT COUNT(fa2)
                                                                                                               FROM FilmActor fa2, FilmCategory fc2
                                                                                                               WHERE fc2.film = fa2.film
                                                                                                                 AND fc2.category = c
                                                                                                               GROUP BY fa2.actor)
                                                                                         AND COUNT(fa) > ALL (SELECT COUNT(fa3)
                                                                                                              FROM FilmActor fa3, FilmCategory fc3
                                                                                                              WHERE fc3.film = fa3.film
                                                                                                                AND fc3.category = c
                                                                                                                AND fa3.actor.actorId < a.actorId
                                                                                                              GROUP BY fa3.actor)
                                                                                      ORDER BY c.name""",
                                                                              Object[].class)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Category) r[0]).getName(), r -> ((Actor) r[1]).getActorId(),
                                r -> ((Actor) r[1]).getFirstName(), r -> ((Actor) r[1]).getLastName(),
                                r -> ((Number) r[2]).longValue())
                    .containsExactly(
                            tuple("Action", 50, "NATALIE", "HOPKINS", 6L),
                            tuple("Animation", 137, "MORGAN", "WILLIAMS", 6L),
                            tuple("Children", 17, "HELEN", "VOIGHT", 7L),
                            tuple("Classics", 86, "GREG", "CHAPLIN", 6L),
                            tuple("Comedy", 196, "BELA", "WALKEN", 6L),
                            tuple("Documentary", 3, "ED", "CHASE", 6L),
                            tuple("Drama", 7, "GRACE", "MOSTEL", 7L),
                            tuple("Family", 79, "MAE", "HOFFMAN", 6L),
                            tuple("Foreign", 164, "HUMPHREY", "WILLIS", 7L),
                            tuple("Games", 68, "RIP", "WINSLET", 5L),
                            tuple("Horror", 27, "JULIA", "MCQUEEN", 7L),
                            tuple("Music", 108, "WARREN", "NOLTE", 5L),
                            tuple("New", 105, "SIDNEY", "CROWE", 7L),
                            tuple("Sci-Fi", 107, "GINA", "DEGENERES", 7L),
                            tuple("Sports", 83, "BEN", "WILLIS", 9L),
                            tuple("Travel", 44, "NICK", "STALLONE", 5L));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var fa = cq.from(FilmActor.class);
                final var a = fa.join(FilmActor_.actor);
                final var fc = cq.from(FilmCategory.class);
                final var c = fc.join(FilmCategory_.category);
                final var others = cq.subquery(Long.class);
                final var fa2 = others.from(FilmActor.class);
                final var fc2 = others.from(FilmCategory.class);
                others.select(cb.count(fa2))
                        .where(cb.equal(fc2.get(FilmCategory_.film), fa2.get(FilmActor_.film)),
                               cb.equal(fc2.get(FilmCategory_.category), c))
                        .groupBy(fa2.get(FilmActor_.actor));
                final var smaller = cq.subquery(Long.class);
                final var fa3 = smaller.from(FilmActor.class);
                final var fc3 = smaller.from(FilmCategory.class);
                smaller.select(cb.count(fa3))
                        .where(cb.equal(fc3.get(FilmCategory_.film), fa3.get(FilmActor_.film)),
                               cb.equal(fc3.get(FilmCategory_.category), c),
                               cb.lt(fa3.get(FilmActor_.actor).get(Actor_.actorId),
                                     a.get(Actor_.actorId)))
                        .groupBy(fa3.get(FilmActor_.actor));
                cq.select(cb.array(c, a, cb.count(fa)))
                        .where(cb.equal(fc.get(FilmCategory_.film), fa.get(FilmActor_.film)))
                        .groupBy(c, a)
                        .having(cb.ge(cb.count(fa), cb.all(others)),
                                cb.gt(cb.count(fa), cb.all(smaller)))
                        .orderBy(cb.asc(c.get(Category_.name)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Category) r[0]).getName(), r -> ((Actor) r[1]).getActorId(),
                                r -> ((Actor) r[1]).getFirstName(), r -> ((Actor) r[1]).getLastName(),
                                r -> ((Number) r[2]).longValue())
                    .containsExactly(
                            tuple("Action", 50, "NATALIE", "HOPKINS", 6L),
                            tuple("Animation", 137, "MORGAN", "WILLIAMS", 6L),
                            tuple("Children", 17, "HELEN", "VOIGHT", 7L),
                            tuple("Classics", 86, "GREG", "CHAPLIN", 6L),
                            tuple("Comedy", 196, "BELA", "WALKEN", 6L),
                            tuple("Documentary", 3, "ED", "CHASE", 6L),
                            tuple("Drama", 7, "GRACE", "MOSTEL", 7L),
                            tuple("Family", 79, "MAE", "HOFFMAN", 6L),
                            tuple("Foreign", 164, "HUMPHREY", "WILLIS", 7L),
                            tuple("Games", 68, "RIP", "WINSLET", 5L),
                            tuple("Horror", 27, "JULIA", "MCQUEEN", 7L),
                            tuple("Music", 108, "WARREN", "NOLTE", 5L),
                            tuple("New", 105, "SIDNEY", "CROWE", 7L),
                            tuple("Sci-Fi", 107, "GINA", "DEGENERES", 7L),
                            tuple("Sports", 83, "BEN", "WILLIS", 9L),
                            tuple("Travel", 44, "NICK", "STALLONE", 5L));
        }
    }

    @DisplayName("SAKILA_FILM_ACTOR_A_03: the pairs of films which have exactly the same actors")
    @Nested
    class SAKILA_FILM_ACTOR_A_03_Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT f1, f2, COUNT(fa1)
                                                                                      FROM FilmActor fa1
                                                                                               JOIN fa1.film f1,
                                                                                           FilmActor fa2
                                                                                               JOIN fa2.film f2
                                                                                      WHERE fa2.actor = fa1.actor
                                                                                        AND f2.filmId > f1.filmId
                                                                                      GROUP BY f1, f2
                                                                                      HAVING COUNT(fa1) = (SELECT COUNT(x) FROM FilmActor x WHERE x.film.filmId = f1.filmId)
                                                                                         AND COUNT(fa1) = (SELECT COUNT(y) FROM FilmActor y WHERE y.film.filmId = f2.filmId)
                                                                                      ORDER BY f1.filmId, f2.filmId""",
                                                                              Object[].class)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Film) r[0]).getFilmId(), r -> ((Film) r[1]).getFilmId(),
                                r -> ((Number) r[2]).longValue())
                    .containsExactly(
                            tuple(97, 556, 1L));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var fa1 = cq.from(FilmActor.class);
                final var fa2 = cq.from(FilmActor.class);
                final var f1 = fa1.join(FilmActor_.film);
                final var f2 = fa2.join(FilmActor_.film);
                final var filmId1 = f1.get(Film_.filmId);
                final var filmId2 = f2.get(Film_.filmId);
                final var actors1 = cq.subquery(Long.class);
                final var x = actors1.from(FilmActor.class);
                actors1.select(cb.count(x))
                        .where(cb.equal(x.get(FilmActor_.film).get(Film_.filmId), filmId1));
                final var actors2 = cq.subquery(Long.class);
                final var y = actors2.from(FilmActor.class);
                actors2.select(cb.count(y))
                        .where(cb.equal(y.get(FilmActor_.film).get(Film_.filmId), filmId2));
                cq.select(cb.array(f1, f2, cb.count(fa1)))
                        .where(cb.equal(fa2.get(FilmActor_.actor), fa1.get(FilmActor_.actor)),
                               cb.gt(filmId2, filmId1))
                        .groupBy(f1, f2)
                        .having(cb.equal(cb.count(fa1), actors1),
                                cb.equal(cb.count(fa1), actors2))
                        .orderBy(cb.asc(filmId1), cb.asc(filmId2));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Film) r[0]).getFilmId(), r -> ((Film) r[1]).getFilmId(),
                                r -> ((Number) r[2]).longValue())
                    .containsExactly(
                            tuple(97, 556, 1L));
        }
    }
}
