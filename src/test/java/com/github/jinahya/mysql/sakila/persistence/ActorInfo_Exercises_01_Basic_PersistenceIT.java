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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers the exercises of {@code src/test/sql/actor_info_exercises_01_basic.sql}, against the
 * installed {@code sakila} database; a nested class per exercise, which answers it with JPQL and
 * with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class ActorInfo_Exercises_01_Basic_PersistenceIT {

    @DisplayName("exercise 1: the film_info of the actor 1")
    @Nested
    class Exercise1Test {

        @ParameterizedTest
        @ValueSource(ints = {1, 2, 3})
        void __QueryLanguage(final int actorId) {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT i.actorId, i.firstName, i.lastName, LEFT(i.filmInfo, 80)
                                                                                      FROM ActorInfo i
                                                                                      WHERE i.actorId = :actorId""",
                                                                              Object[].class)
                    .setParameter("actorId", actorId)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list).singleElement().satisfies(r -> {
                assertThat(r[0]).isEqualTo(actorId);
                assertThat((String) r[3]).hasSizeLessThanOrEqualTo(80);
            });
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 2, 3})
        void __CriteriaApi(final int actorId) {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var i = cq.from(ActorInfo.class);
                cq.select(cb.array(i.get(ActorInfo_.actorId), i.get(ActorInfo_.firstName),
                                   i.get(ActorInfo_.lastName),
                                   cb.left(i.get(ActorInfo_.filmInfo), 80)))
                        .where(cb.equal(i.get(ActorInfo_.actorId),
                                        cb.parameter(Integer.class, "actorId")));
                return em.createQuery(cq)
                        .setParameter("actorId", actorId)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list).singleElement().satisfies(r -> {
                assertThat(r[0]).isEqualTo(actorId);
                assertThat((String) r[3]).hasSizeLessThanOrEqualTo(80);
            });
        }
    }

    @DisplayName("exercise 2: how many actors appear in a Horror film, as film_info says")
    @Nested
    class Exercise2Test {

        @ParameterizedTest
        @ValueSource(strings = {"Horror", "Action", "Comedy"})
        void __QueryLanguage(final String category) {
            final var value = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                       SELECT COUNT(i)
                                                                                       FROM ActorInfo i
                                                                                       WHERE i.filmInfo LIKE :pattern""",
                                                                               Long.class)
                    .setParameter("pattern", "%" + category + ":%")
                    .getSingleResult());
            log.debug("{}", value);
            assertThat(value).isPositive();
        }

        @ParameterizedTest
        @ValueSource(strings = {"Horror", "Action", "Comedy"})
        void __CriteriaApi(final String category) {
            final var value = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Long.class);
                final var i = cq.from(ActorInfo.class);
                cq.select(cb.count(i))
                        .where(cb.like(i.get(ActorInfo_.filmInfo),
                                       cb.parameter(String.class, "pattern")));
                return em.createQuery(cq)
                        .setParameter("pattern", "%" + category + ":%")
                        .getSingleResult();
            });
            log.debug("{}", value);
            assertThat(value).isPositive();
        }
    }

    @DisplayName("exercise 3: the actors of ACADEMY DINOSAUR, as film_info says")
    @Nested
    class Exercise3Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT i.actorId, i.firstName, i.lastName
                                                                                      FROM ActorInfo i
                                                                                      WHERE i.filmInfo LIKE :pattern
                                                                                      ORDER BY i.actorId""",
                                                                              Object[].class)
                    .setParameter("pattern", "%ACADEMY DINOSAUR%")
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> r[1], r -> r[2])
                    .containsExactly(
                            tuple(1, "PENELOPE", "GUINESS"),
                            tuple(10, "CHRISTIAN", "GABLE"),
                            tuple(20, "LUCILLE", "TRACY"),
                            tuple(30, "SANDRA", "PECK"),
                            tuple(40, "JOHNNY", "CAGE"),
                            tuple(53, "MENA", "TEMPLE"),
                            tuple(108, "WARREN", "NOLTE"),
                            tuple(162, "OPRAH", "KILMER"),
                            tuple(188, "ROCK", "DUKAKIS"),
                            tuple(198, "MARY", "KEITEL"));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var i = cq.from(ActorInfo.class);
                cq.select(cb.array(i.get(ActorInfo_.actorId), i.get(ActorInfo_.firstName),
                                   i.get(ActorInfo_.lastName)))
                        .where(cb.like(i.get(ActorInfo_.filmInfo),
                                       cb.parameter(String.class, "pattern")))
                        .orderBy(cb.asc(i.get(ActorInfo_.actorId)));
                return em.createQuery(cq)
                        .setParameter("pattern", "%ACADEMY DINOSAUR%")
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> r[1], r -> r[2])
                    .containsExactly(
                            tuple(1, "PENELOPE", "GUINESS"),
                            tuple(10, "CHRISTIAN", "GABLE"),
                            tuple(20, "LUCILLE", "TRACY"),
                            tuple(30, "SANDRA", "PECK"),
                            tuple(40, "JOHNNY", "CAGE"),
                            tuple(53, "MENA", "TEMPLE"),
                            tuple(108, "WARREN", "NOLTE"),
                            tuple(162, "OPRAH", "KILMER"),
                            tuple(188, "ROCK", "DUKAKIS"),
                            tuple(198, "MARY", "KEITEL"));
        }
    }

    @DisplayName("exercise 4: the five actors with the longest film_info")
    @Nested
    class Exercise4Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT i.actorId, i.firstName, i.lastName, LENGTH(i.filmInfo)
                                                                                      FROM ActorInfo i
                                                                                      ORDER BY LENGTH(i.filmInfo) DESC, i.actorId""",
                                                                              Object[].class)
                    .setMaxResults(5)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> r[1], r -> r[2],
                                r -> ((Number) r[3]).intValue())
                    .containsExactly(
                            tuple(107, "GINA", "DEGENERES", 830),
                            tuple(102, "WALTER", "TORN", 774),
                            tuple(198, "MARY", "KEITEL", 774),
                            tuple(23, "SANDRA", "KILMER", 746),
                            tuple(181, "MATTHEW", "CARREY", 728));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var i = cq.from(ActorInfo.class);
                cq.select(cb.array(i.get(ActorInfo_.actorId), i.get(ActorInfo_.firstName),
                                   i.get(ActorInfo_.lastName),
                                   cb.length(i.get(ActorInfo_.filmInfo))))
                        .orderBy(cb.desc(cb.length(i.get(ActorInfo_.filmInfo))),
                                 cb.asc(i.get(ActorInfo_.actorId)));
                return em.createQuery(cq)
                        .setMaxResults(5)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> r[1], r -> r[2],
                                r -> ((Number) r[3]).intValue())
                    .containsExactly(
                            tuple(107, "GINA", "DEGENERES", 830),
                            tuple(102, "WALTER", "TORN", 774),
                            tuple(198, "MARY", "KEITEL", 774),
                            tuple(23, "SANDRA", "KILMER", 746),
                            tuple(181, "MATTHEW", "CARREY", 728));
        }
    }
}
