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
import java.util.LinkedHashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers the exercises of {@code src/test/sql/actor_exercises_02_intermediate.sql}, against the
 * installed {@code sakila} database; a nested class per exercise, which answers it with JPQL and
 * with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Actor_Exercises_02_Intermediate_PersistenceIT {

    @DisplayName(
            "exercise 1: the last names shared by more than three actors, the most shared first")
    @Nested
    class Exercise1Test {

        @ParameterizedTest
        @ValueSource(longs = {2, 3, 4})
        void __QueryLanguage(final long count) {
            final var list = __Exercises_TestUtils.apply(
                    em -> em.createQuery(
                                    """
                                            SELECT a.lastName, COUNT(a)
                                            FROM Actor a
                                            GROUP BY a.lastName
                                            HAVING COUNT(a) > :count
                                            ORDER BY COUNT(a) DESC, a.lastName""",
                                    Object[].class
                            )
                            .setParameter("count", count)
                            .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list).isNotEmpty()
                    .allSatisfy(r -> assertThat(((Number) r[1]).longValue()).isGreaterThan(count));
        }

        @ParameterizedTest
        @ValueSource(longs = {2, 3, 4})
        void __CriteriaApi(final long count) {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var a = cq.from(Actor.class);
                cq.select(cb.array(a.get(Actor_.lastName), cb.count(a)))
                        .groupBy(a.get(Actor_.lastName))
                        .having(cb.gt(cb.count(a), cb.parameter(Long.class, "count")))
                        .orderBy(cb.desc(cb.count(a)), cb.asc(a.get(Actor_.lastName)));
                return em.createQuery(cq)
                        .setParameter("count", count)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list).isNotEmpty()
                    .allSatisfy(r -> assertThat(((Number) r[1]).longValue()).isGreaterThan(count));
        }
    }

    @DisplayName("exercise 2: the five actors who appear in the most films")
    @Nested
    class Exercise2Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(
                    em -> em.createQuery(
                                    """
                                            SELECT a, COUNT(fa)
                                            FROM Actor a
                                                     JOIN a.filmActors fa
                                            GROUP BY a
                                            ORDER BY COUNT(fa) DESC, a.actorId""",
                                    Object[].class
                            )
                            .setMaxResults(5)
                            .getResultList()
            );
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Actor) r[0]).getActorId(), r -> ((Actor) r[0]).getFirstName(),
                                r -> ((Actor) r[0]).getLastName(), r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple(107, "GINA", "DEGENERES", 42L),
                            tuple(102, "WALTER", "TORN", 41L),
                            tuple(198, "MARY", "KEITEL", 40L),
                            tuple(181, "MATTHEW", "CARREY", 39L),
                            tuple(23, "SANDRA", "KILMER", 37L));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var a = cq.from(Actor.class);
                final var fa = a.join(Actor_.filmActors);
                cq.select(cb.array(a, cb.count(fa)))
                        .groupBy(a)
                        .orderBy(cb.desc(cb.count(fa)), cb.asc(a.get(Actor_.actorId)));
                return em.createQuery(cq)
                        .setMaxResults(5)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Actor) r[0]).getActorId(), r -> ((Actor) r[0]).getFirstName(),
                                r -> ((Actor) r[0]).getLastName(), r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple(107, "GINA", "DEGENERES", 42L),
                            tuple(102, "WALTER", "TORN", 41L),
                            tuple(198, "MARY", "KEITEL", 40L),
                            tuple(181, "MATTHEW", "CARREY", 39L),
                            tuple(23, "SANDRA", "KILMER", 37L));
        }
    }

    @DisplayName("exercise 3: the actors who appear in no film")
    @Nested
    class Exercise3Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(
                    em -> em.createQuery(
                                    """
                                            SELECT a
                                            FROM Actor a
                                            WHERE a.filmActors IS EMPTY""",
                                    Actor.class
                            )
                            .getResultList()
            );
            list.forEach(e -> log.debug("{}", e));
            assertThat(list).isEmpty();
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Actor.class);
                final var a = cq.from(Actor.class);
                cq.select(a)
                        .where(cb.isEmpty(a.get(Actor_.filmActors)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(e -> log.debug("{}", e));
            assertThat(list).isEmpty();
        }
    }

    @DisplayName("exercise 4: the five most common first names")
    @Nested
    class Exercise4Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(
                    em -> em.createQuery(
                                    """
                                            SELECT a.firstName, COUNT(a)
                                            FROM Actor a
                                            GROUP BY a.firstName
                                            ORDER BY COUNT(a) DESC, a.firstName""",
                                    Object[].class
                            )
                            .setMaxResults(5)
                            .getResultList()
            );
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple("JULIA", 4L),
                            tuple("KENNETH", 4L),
                            tuple("PENELOPE", 4L),
                            tuple("BURT", 3L),
                            tuple("CAMERON", 3L));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var a = cq.from(Actor.class);
                cq.select(cb.array(a.get(Actor_.firstName), cb.count(a)))
                        .groupBy(a.get(Actor_.firstName))
                        .orderBy(cb.desc(cb.count(a)), cb.asc(a.get(Actor_.firstName)));
                return em.createQuery(cq)
                        .setMaxResults(5)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple("JULIA", 4L),
                            tuple("KENNETH", 4L),
                            tuple("PENELOPE", 4L),
                            tuple("BURT", 3L),
                            tuple("CAMERON", 3L));
        }
    }

    @DisplayName("exercise 5: the five most common last names")
    @Nested
    class Exercise5Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(
                    em -> em.createQuery(
                                    """
                                            SELECT a.lastName, COUNT(a)
                                            FROM Actor a
                                            GROUP BY a.lastName
                                            ORDER BY COUNT(a) DESC, a.lastName""",
                                    Object[].class
                            )
                            .setMaxResults(5)
                            .getResultList()
            );
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple("KILMER", 5L),
                            tuple("NOLTE", 4L),
                            tuple("TEMPLE", 4L),
                            tuple("AKROYD", 3L),
                            tuple("ALLEN", 3L));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var a = cq.from(Actor.class);
                cq.select(cb.array(a.get(Actor_.lastName), cb.count(a)))
                        .groupBy(a.get(Actor_.lastName))
                        .orderBy(cb.desc(cb.count(a)), cb.asc(a.get(Actor_.lastName)));
                return em.createQuery(cq)
                        .setMaxResults(5)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple("KILMER", 5L),
                            tuple("NOLTE", 4L),
                            tuple("TEMPLE", 4L),
                            tuple("AKROYD", 3L),
                            tuple("ALLEN", 3L));
        }
    }

    @DisplayName("exercise 6: the five actors whose films are, on average, the longest")
    @Nested
    class Exercise6Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(
                    em -> em.createQuery(
                                    """
                                            SELECT a, ROUND(AVG(f.length), 2)
                                            FROM Actor a
                                                     JOIN a.filmActors fa
                                                     JOIN fa.film f
                                            GROUP BY a
                                            ORDER BY AVG(f.length) DESC, a.actorId""",
                                    Object[].class
                            )
                            .setMaxResults(5)
                            .getResultList()
            );
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Actor) r[0]).getActorId(), r -> ((Actor) r[0]).getFirstName(),
                                r -> ((Actor) r[0]).getLastName(),
                                r -> ((Number) r[1]).doubleValue())
                    .containsExactly(
                            tuple(35, "JUDY", "DEAN", 137.73d),
                            tuple(156, "FAY", "WOOD", 133.82d),
                            tuple(16, "FRED", "COSTNER", 131.56d),
                            tuple(95, "DARYL", "WAHLBERG", 130.94d),
                            tuple(8, "MATTHEW", "JOHANSSON", 130.85d));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var a = cq.from(Actor.class);
                final var f = a.join(Actor_.filmActors).join(FilmActor_.film);
                cq.select(cb.array(a, cb.round(cb.avg(f.get(Film_.length)), 2)))
                        .groupBy(a)
                        .orderBy(cb.desc(cb.avg(f.get(Film_.length))),
                                 cb.asc(a.get(Actor_.actorId)));
                return em.createQuery(cq)
                        .setMaxResults(5)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Actor) r[0]).getActorId(), r -> ((Actor) r[0]).getFirstName(),
                                r -> ((Actor) r[0]).getLastName(),
                                r -> ((Number) r[1]).doubleValue())
                    .containsExactly(
                            tuple(35, "JUDY", "DEAN", 137.73d),
                            tuple(156, "FAY", "WOOD", 133.82d),
                            tuple(16, "FRED", "COSTNER", 131.56d),
                            tuple(95, "DARYL", "WAHLBERG", 130.94d),
                            tuple(8, "MATTHEW", "JOHANSSON", 130.85d));
        }
    }

    @DisplayName("exercise 7: the films of the first five actors, counted by rating")
    @Nested
    class Exercise7Test {

        // [actor, rating, count]s into [actor, g, pg, pg_13, r, nc_17]s
        private static List<Object[]> pivot(final List<Object[]> list) {
            final var rows = new LinkedHashMap<Object, Object[]>();
            list.forEach(r -> rows.computeIfAbsent(
                    ((Actor) r[0]).getActorId(), k -> new Object[]{r[0], 0L, 0L, 0L, 0L, 0L}
            )[1 + ((Film.Rating) r[1]).ordinal()] = ((Number) r[2]).longValue());
            return List.copyOf(rows.values());
        }

        // JPQL counts no CASE; the films are counted by actor and rating, and pivoted in Java
        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(
                    em -> pivot(em.createQuery(
                                    """
                                            SELECT a, f.rating, COUNT(f)
                                            FROM Actor a
                                                     JOIN a.filmActors fa
                                                     JOIN fa.film f
                                            WHERE a.actorId BETWEEN 1 AND 5
                                            GROUP BY a, f.rating
                                            ORDER BY a.actorId""",
                                    Object[].class
                            )
                            .getResultList())
            );
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Actor) r[0]).getActorId(), r -> ((Actor) r[0]).getFirstName(),
                                r -> ((Actor) r[0]).getLastName(), r -> r[1], r -> r[2], r -> r[3],
                                r -> r[4], r -> r[5])
                    .containsExactly(
                            tuple(1, "PENELOPE", "GUINESS", 4L, 6L, 1L, 3L, 5L),
                            tuple(2, "NICK", "WAHLBERG", 7L, 6L, 2L, 2L, 8L),
                            tuple(3, "ED", "CHASE", 2L, 5L, 5L, 5L, 5L),
                            tuple(4, "JENNIFER", "DAVIS", 3L, 5L, 5L, 4L, 5L),
                            tuple(5, "JOHNNY", "LOLLOBRIGIDA", 7L, 4L, 4L, 7L, 7L));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var a = cq.from(Actor.class);
                final var f = a.join(Actor_.filmActors).join(FilmActor_.film);
                cq.select(cb.array(a, f.get(Film_.rating), cb.count(f)))
                        .where(cb.between(a.get(Actor_.actorId), 1, 5))
                        .groupBy(a, f.get(Film_.rating))
                        .orderBy(cb.asc(a.get(Actor_.actorId)));
                return pivot(em.createQuery(cq)
                                     .getResultList());
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Actor) r[0]).getActorId(), r -> ((Actor) r[0]).getFirstName(),
                                r -> ((Actor) r[0]).getLastName(), r -> r[1], r -> r[2], r -> r[3],
                                r -> r[4], r -> r[5])
                    .containsExactly(
                            tuple(1, "PENELOPE", "GUINESS", 4L, 6L, 1L, 3L, 5L),
                            tuple(2, "NICK", "WAHLBERG", 7L, 6L, 2L, 2L, 8L),
                            tuple(3, "ED", "CHASE", 2L, 5L, 5L, 5L, 5L),
                            tuple(4, "JENNIFER", "DAVIS", 3L, 5L, 5L, 4L, 5L),
                            tuple(5, "JOHNNY", "LOLLOBRIGIDA", 7L, 4L, 4L, 7L, 7L));
        }
    }
}
