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

    @DisplayName("exercise 1: the three pairs of actors who appear together the most")
    @Nested
    class Exercise1Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT fa1.actor.actorId, fa2.actor.actorId, COUNT(fa1)
                                                                                      FROM FilmActor fa1, FilmActor fa2
                                                                                      WHERE fa2.film = fa1.film
                                                                                        AND fa2.actor.actorId > fa1.actor.actorId
                                                                                      GROUP BY fa1.actor.actorId, fa2.actor.actorId
                                                                                      ORDER BY COUNT(fa1) DESC, fa1.actor.actorId, fa2.actor.actorId""",
                                                                              Object[].class)
                    .setMaxResults(3)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> ((Number) r[1]).intValue(),
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
                final var actorId1 = fa1.get(FilmActor_.actor).get(Actor_.actorId);
                final var actorId2 = fa2.get(FilmActor_.actor).get(Actor_.actorId);
                cq.select(cb.array(actorId1, actorId2, cb.count(fa1)))
                        .where(cb.equal(fa2.get(FilmActor_.film), fa1.get(FilmActor_.film)),
                               cb.gt(actorId2, actorId1))
                        .groupBy(actorId1, actorId2)
                        .orderBy(cb.desc(cb.count(fa1)), cb.asc(actorId1), cb.asc(actorId2));
                return em.createQuery(cq)
                        .setMaxResults(3)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> ((Number) r[1]).intValue(),
                                r -> ((Number) r[2]).longValue())
                    .containsExactly(
                            tuple(27, 60, 7L),
                            tuple(15, 198, 6L),
                            tuple(21, 108, 6L));
        }
    }

    @DisplayName("exercise 2: the actor who appears in the most films of each category")
    @Nested
    class Exercise2Test {

        // JPQL has no window functions; the actor of each category is the one whom no actor
        // outnumbers, and whom no actor with a smaller id equals
        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT c.name, a.actorId, a.firstName, a.lastName, COUNT(fa)
                                                                                      FROM FilmActor fa
                                                                                               JOIN fa.actor a,
                                                                                           FilmCategory fc
                                                                                               JOIN fc.category c
                                                                                      WHERE fc.film = fa.film
                                                                                      GROUP BY c.categoryId, c.name, a.actorId, a.firstName, a.lastName
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
                    .extracting(r -> r[0], r -> ((Number) r[1]).intValue(), r -> r[2], r -> r[3],
                                r -> ((Number) r[4]).longValue())
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
                cq.select(cb.array(c.get(Category_.name), a.get(Actor_.actorId),
                                   a.get(Actor_.firstName), a.get(Actor_.lastName), cb.count(fa)))
                        .where(cb.equal(fc.get(FilmCategory_.film), fa.get(FilmActor_.film)))
                        .groupBy(c.get(Category_.categoryId), c.get(Category_.name),
                                 a.get(Actor_.actorId), a.get(Actor_.firstName),
                                 a.get(Actor_.lastName))
                        .having(cb.ge(cb.count(fa), cb.all(others)),
                                cb.gt(cb.count(fa), cb.all(smaller)))
                        .orderBy(cb.asc(c.get(Category_.name)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).intValue(), r -> r[2], r -> r[3],
                                r -> ((Number) r[4]).longValue())
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

    @DisplayName("exercise 3: the pairs of films which have exactly the same actors")
    @Nested
    class Exercise3Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT fa1.film.filmId, fa2.film.filmId, COUNT(fa1)
                                                                                      FROM FilmActor fa1, FilmActor fa2
                                                                                      WHERE fa2.actor = fa1.actor
                                                                                        AND fa2.film.filmId > fa1.film.filmId
                                                                                      GROUP BY fa1.film.filmId, fa2.film.filmId
                                                                                      HAVING COUNT(fa1) = (SELECT COUNT(x) FROM FilmActor x WHERE x.film.filmId = fa1.film.filmId)
                                                                                         AND COUNT(fa1) = (SELECT COUNT(y) FROM FilmActor y WHERE y.film.filmId = fa2.film.filmId)
                                                                                      ORDER BY fa1.film.filmId, fa2.film.filmId""",
                                                                              Object[].class)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> ((Number) r[1]).intValue(),
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
                final var filmId1 = fa1.get(FilmActor_.film).get(Film_.filmId);
                final var filmId2 = fa2.get(FilmActor_.film).get(Film_.filmId);
                final var actors1 = cq.subquery(Long.class);
                final var x = actors1.from(FilmActor.class);
                actors1.select(cb.count(x))
                        .where(cb.equal(x.get(FilmActor_.film).get(Film_.filmId), filmId1));
                final var actors2 = cq.subquery(Long.class);
                final var y = actors2.from(FilmActor.class);
                actors2.select(cb.count(y))
                        .where(cb.equal(y.get(FilmActor_.film).get(Film_.filmId), filmId2));
                cq.select(cb.array(filmId1, filmId2, cb.count(fa1)))
                        .where(cb.equal(fa2.get(FilmActor_.actor), fa1.get(FilmActor_.actor)),
                               cb.gt(filmId2, filmId1))
                        .groupBy(filmId1, filmId2)
                        .having(cb.equal(cb.count(fa1), actors1),
                                cb.equal(cb.count(fa1), actors2))
                        .orderBy(cb.asc(filmId1), cb.asc(filmId2));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> ((Number) r[1]).intValue(),
                                r -> ((Number) r[2]).longValue())
                    .containsExactly(
                            tuple(97, 556, 1L));
        }
    }
}
