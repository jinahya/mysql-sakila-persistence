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
 * Answers the exercises of {@code src/test/sql/exercises/actor_exercises_03_advanced.sql}, against the
 * installed {@code sakila} database; a nested class per exercise, which answers it with JPQL and
 * with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Actor_Exercises_03_Advanced_PersistenceIT {

    @DisplayName("SAKILA_ACTOR_A_01: the actors who appear in a film of every rating")
    @Nested
    class SAKILA_ACTOR_A_01_Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(
                    em -> em.createQuery(
                                    """
                                            SELECT a
                                            FROM Actor a
                                            WHERE (SELECT COUNT(DISTINCT fa.film.rating) FROM FilmActor fa WHERE fa.actor = a)
                                                  = (SELECT COUNT(DISTINCT f.rating) FROM Film f)
                                            ORDER BY a.actorId""",
                                    Actor.class
                            )
                            .setMaxResults(5)
                            .getResultList()
            );
            list.forEach(e -> log.debug("{}", e));
            assertThat(list).extracting(Actor::getActorId).containsExactly(1, 2, 3, 4, 5);
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Actor.class);
                final var a = cq.from(Actor.class);
                // eclipselink gives the first actor five times for groupBy(a) with select(a);
                // the ratings are
                // counted per actor, by a subquery
                final var ratingsOfActor = cq.subquery(Long.class);
                final var fa = ratingsOfActor.from(FilmActor.class);
                ratingsOfActor.select(cb.countDistinct(fa.get(FilmActor_.film).get(Film_.rating)))
                        .where(cb.equal(fa.get(FilmActor_.actor), a));
                final var ratings = cq.subquery(Long.class);
                final var f = ratings.from(Film.class);
                ratings.select(cb.countDistinct(f.get(Film_.rating)));
                cq.select(a)
                        .where(cb.equal(ratingsOfActor, ratings))
                        .orderBy(cb.asc(a.get(Actor_.actorId)));
                return em.createQuery(cq)
                        .setMaxResults(5)
                        .getResultList();
            });
            list.forEach(e -> log.debug("{}", e));
            assertThat(list).extracting(Actor::getActorId).containsExactly(1, 2, 3, 4, 5);
        }
    }

    @DisplayName("SAKILA_ACTOR_A_02: the actors in the top seven film counts, ties included")
    @Nested
    class SAKILA_ACTOR_A_02_Test {

        // JPQL has no DENSE_RANK(); an actor's rank is the number of the distinct film counts at
        // or above the actor's, each counted once, by the first actor who has it
        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(
                    em -> em.createQuery(
                                    """
                                            SELECT a, (SELECT COUNT(fa) FROM FilmActor fa WHERE fa.actor = a), COUNT(b)
                                            FROM Actor a, Actor b
                                            WHERE SIZE(b.filmActors) >= SIZE(a.filmActors)
                                              AND NOT EXISTS (SELECT b2
                                                              FROM Actor b2
                                                              WHERE SIZE(b2.filmActors) = SIZE(b.filmActors)
                                                                AND b2.actorId < b.actorId)
                                            GROUP BY a
                                            HAVING COUNT(b) <= 7
                                            ORDER BY COUNT(b), a.actorId""",
                                    Object[].class
                            )
                            .getResultList()
            );
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Actor) r[0]).getActorId(), r -> ((Actor) r[0]).getFirstName(),
                                r -> ((Actor) r[0]).getLastName(), r -> ((Number) r[1]).longValue(),
                                r -> ((Number) r[2]).longValue())
                    .containsExactly(
                            tuple(107, "GINA", "DEGENERES", 42L, 1L),
                            tuple(102, "WALTER", "TORN", 41L, 2L),
                            tuple(198, "MARY", "KEITEL", 40L, 3L),
                            tuple(181, "MATTHEW", "CARREY", 39L, 4L),
                            tuple(23, "SANDRA", "KILMER", 37L, 5L),
                            tuple(81, "SCARLETT", "DAMON", 36L, 6L),
                            tuple(13, "UMA", "WOOD", 35L, 7L),
                            tuple(37, "VAL", "BOLGER", 35L, 7L),
                            tuple(60, "HENRY", "BERRY", 35L, 7L),
                            tuple(106, "GROUCHO", "DUNST", 35L, 7L),
                            tuple(144, "ANGELA", "WITHERSPOON", 35L, 7L),
                            tuple(158, "VIVIEN", "BASINGER", 35L, 7L));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var a = cq.from(Actor.class);
                final var b = cq.from(Actor.class);
                final var films = cq.subquery(Long.class);
                final var fa = films.from(FilmActor.class);
                films.select(cb.count(fa)).where(cb.equal(fa.get(FilmActor_.actor), a));
                final var earlier = cq.subquery(Actor.class);
                final var b2 = earlier.from(Actor.class);
                earlier.select(b2)
                        .where(cb.equal(cb.size(b2.get(Actor_.filmActors)),
                                        cb.size(b.get(Actor_.filmActors))),
                               cb.lt(b2.get(Actor_.actorId), b.get(Actor_.actorId)));
                // eclipselink selects no bare subquery (a ClassCastException); it does select a
                // COALESCE of one
                cq.select(cb.array(a, cb.coalesce(films, 0L), cb.count(b)))
                        .where(cb.ge(cb.size(b.get(Actor_.filmActors)),
                                     cb.size(a.get(Actor_.filmActors))),
                               cb.not(cb.exists(earlier)))
                        .groupBy(a)
                        .having(cb.le(cb.count(b), 7L))
                        .orderBy(cb.asc(cb.count(b)), cb.asc(a.get(Actor_.actorId)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Actor) r[0]).getActorId(), r -> ((Actor) r[0]).getFirstName(),
                                r -> ((Actor) r[0]).getLastName(), r -> ((Number) r[1]).longValue(),
                                r -> ((Number) r[2]).longValue())
                    .containsExactly(
                            tuple(107, "GINA", "DEGENERES", 42L, 1L),
                            tuple(102, "WALTER", "TORN", 41L, 2L),
                            tuple(198, "MARY", "KEITEL", 40L, 3L),
                            tuple(181, "MATTHEW", "CARREY", 39L, 4L),
                            tuple(23, "SANDRA", "KILMER", 37L, 5L),
                            tuple(81, "SCARLETT", "DAMON", 36L, 6L),
                            tuple(13, "UMA", "WOOD", 35L, 7L),
                            tuple(37, "VAL", "BOLGER", 35L, 7L),
                            tuple(60, "HENRY", "BERRY", 35L, 7L),
                            tuple(106, "GROUCHO", "DUNST", 35L, 7L),
                            tuple(144, "ANGELA", "WITHERSPOON", 35L, 7L),
                            tuple(158, "VIVIEN", "BASINGER", 35L, 7L));
        }
    }

    @DisplayName("SAKILA_ACTOR_A_03: the longest film of each of the first five actors")
    @Nested
    class SAKILA_ACTOR_A_03_Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(
                    em -> em.createQuery(
                                    """
                                            SELECT a, f
                                            FROM Actor a
                                                     JOIN a.filmActors fa
                                                     JOIN fa.film f
                                            WHERE a.actorId BETWEEN 1 AND 5
                                              AND f.length = (SELECT MAX(f2.length)
                                                              FROM FilmActor fa2
                                                                       JOIN fa2.film f2
                                                              WHERE fa2.actor = a)
                                            ORDER BY a.actorId, f.title""",
                                    Object[].class
                            )
                            .getResultList()
            );
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Actor) r[0]).getActorId(), r -> ((Actor) r[0]).getFirstName(),
                                r -> ((Actor) r[0]).getLastName(), r -> ((Film) r[1]).getTitle(),
                                r -> ((Film) r[1]).getLength())
                    .containsExactly(
                            tuple(1, "PENELOPE", "GUINESS", "KING EVOLUTION", 184),
                            tuple(2, "NICK", "WAHLBERG", "WARDROBE PHANTOM", 178),
                            tuple(3, "ED", "CHASE", "YOUNG LANGUAGE", 183),
                            tuple(4, "JENNIFER", "DAVIS", "REDS POCUS", 182),
                            tuple(5, "JOHNNY", "LOLLOBRIGIDA", "SOLDIERS EVOLUTION", 185));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var a = cq.from(Actor.class);
                final var f = a.join(Actor_.filmActors).join(FilmActor_.film);
                final var longest = cq.subquery(Integer.class);
                final var fa2 = longest.from(FilmActor.class);
                final var f2 = fa2.join(FilmActor_.film);
                longest.select(cb.max(f2.get(Film_.length)))
                        .where(cb.equal(fa2.get(FilmActor_.actor), a));
                cq.select(cb.array(a, f))
                        .where(cb.between(a.get(Actor_.actorId), 1, 5),
                               cb.equal(f.get(Film_.length), longest))
                        .orderBy(cb.asc(a.get(Actor_.actorId)), cb.asc(f.get(Film_.title)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Actor) r[0]).getActorId(), r -> ((Actor) r[0]).getFirstName(),
                                r -> ((Actor) r[0]).getLastName(), r -> ((Film) r[1]).getTitle(),
                                r -> ((Film) r[1]).getLength())
                    .containsExactly(
                            tuple(1, "PENELOPE", "GUINESS", "KING EVOLUTION", 184),
                            tuple(2, "NICK", "WAHLBERG", "WARDROBE PHANTOM", 178),
                            tuple(3, "ED", "CHASE", "YOUNG LANGUAGE", 183),
                            tuple(4, "JENNIFER", "DAVIS", "REDS POCUS", 182),
                            tuple(5, "JOHNNY", "LOLLOBRIGIDA", "SOLDIERS EVOLUTION", 185));
        }
    }
}
