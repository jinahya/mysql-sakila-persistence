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
import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Answers the exercises of {@code src/test/sql/actor_exercises_01_basic.sql}, against the installed
 * {@code sakila} database; a nested class per exercise, which answers it with JPQL and with the
 * Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Actor_Exercises_01_Basic_PersistenceIT {

    @DisplayName("exercise 1: the actors whose last name is GUINESS")
    @Nested
    class Exercise1Test {

        @ParameterizedTest
        @ValueSource(strings = {"GUINESS", "WAHLBERG", "KILMER"})
        void __QueryLanguage(final String lastName) {
            final var list = __Exercises_TestUtils.apply(
                    em -> em.createQuery(
                                    """
                                            SELECT a
                                            FROM Actor a
                                            WHERE a.lastName = :lastName
                                            ORDER BY a.actorId""",
                                    Actor.class
                            )
                            .setParameter("lastName", lastName
                            )
                            .getResultList()
            );
            list.forEach(e -> log.debug("{}", e));
            assertThat(list).isNotEmpty()
                    .allSatisfy(a -> assertThat(a.getLastName()).isEqualTo(lastName));
            assertThat(list).extracting(Actor::getActorId).isSorted();
        }

        @ParameterizedTest
        @ValueSource(strings = {"GUINESS", "WAHLBERG", "KILMER"})
        void __CriteriaApi(final String lastName) {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Actor.class);
                final var a = cq.from(Actor.class);
                cq.select(a)
                        .where(cb.equal(a.get(Actor_.lastName),
                                        cb.parameter(String.class, "lastName")))
                        .orderBy(cb.asc(a.get(Actor_.actorId)));
                return em.createQuery(cq)
                        .setParameter("lastName", lastName)
                        .getResultList();
            });
            list.forEach(e -> log.debug("{}", e));
            assertThat(list).isNotEmpty()
                    .allSatisfy(a -> assertThat(a.getLastName()).isEqualTo(lastName));
            assertThat(list).extracting(Actor::getActorId).isSorted();
        }
    }

    @DisplayName("exercise 2: the actors whose first name starts with JU, ordered by first name")
    @Nested
    class Exercise2Test {

        @ParameterizedTest
        @ValueSource(strings = {"JU", "PEN", "SA"})
        void __QueryLanguage(final String prefix) {
            final var list = __Exercises_TestUtils.apply(
                    em -> em.createQuery(
                                    """
                                            SELECT a
                                            FROM Actor a
                                            WHERE a.firstName LIKE :pattern
                                            ORDER BY a.firstName, a.actorId""",
                                    Actor.class
                            )
                            .setParameter("pattern", prefix + '%')
                            .getResultList()
            );
            list.forEach(e -> log.debug("{}", e));
            assertThat(list).isNotEmpty()
                    .allSatisfy(a -> assertThat(a.getFirstName()).startsWith(prefix))
                    .isSortedAccordingTo(Comparator.comparing(Actor::getFirstName)
                                                 .thenComparing(Actor::getActorId));
        }

        @ParameterizedTest
        @ValueSource(strings = {"JU", "PEN", "SA"})
        void __CriteriaApi(final String prefix) {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Actor.class);
                final var a = cq.from(Actor.class);
                cq.select(a)
                        .where(cb.like(a.get(Actor_.firstName),
                                       cb.parameter(String.class, "pattern")))
                        .orderBy(cb.asc(a.get(Actor_.firstName)), cb.asc(a.get(Actor_.actorId)));
                return em.createQuery(cq)
                        .setParameter("pattern", prefix + '%')
                        .getResultList();
            });
            list.forEach(e -> log.debug("{}", e));
            assertThat(list).isNotEmpty()
                    .allSatisfy(a -> assertThat(a.getFirstName()).startsWith(prefix))
                    .isSortedAccordingTo(Comparator.comparing(Actor::getFirstName)
                                                 .thenComparing(Actor::getActorId));
        }
    }

    @DisplayName("exercise 3: the actors whose last name starts with WA, ordered by last name")
    @Nested
    class Exercise3Test {

        @ParameterizedTest
        @ValueSource(strings = {"WA", "GU", "KI"})
        void __QueryLanguage(final String prefix) {
            final var list = __Exercises_TestUtils.apply(
                    em -> em.createQuery(
                                    """
                                            SELECT a
                                            FROM Actor a
                                            WHERE a.lastName LIKE :pattern
                                            ORDER BY a.lastName, a.actorId""",
                                    Actor.class
                            )
                            .setParameter("pattern", prefix + '%')
                            .getResultList()
            );
            list.forEach(e -> log.debug("{}", e));
            assertThat(list).isNotEmpty()
                    .allSatisfy(a -> assertThat(a.getLastName()).startsWith(prefix))
                    .isSortedAccordingTo(Comparator.comparing(Actor::getLastName)
                                                 .thenComparing(Actor::getActorId));
        }

        @ParameterizedTest
        @ValueSource(strings = {"WA", "GU", "KI"})
        void __CriteriaApi(final String prefix) {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Actor.class);
                final var a = cq.from(Actor.class);
                cq.select(a)
                        .where(cb.like(a.get(Actor_.lastName),
                                       cb.parameter(String.class, "pattern")))
                        .orderBy(cb.asc(a.get(Actor_.lastName)), cb.asc(a.get(Actor_.actorId)));
                return em.createQuery(cq)
                        .setParameter("pattern", prefix + '%')
                        .getResultList();
            });
            list.forEach(e -> log.debug("{}", e));
            assertThat(list).isNotEmpty()
                    .allSatisfy(a -> assertThat(a.getLastName()).startsWith(prefix))
                    .isSortedAccordingTo(Comparator.comparing(Actor::getLastName)
                                                 .thenComparing(Actor::getActorId));
        }
    }

    @DisplayName("exercise 4: the actors whose first and last names start with the same letter")
    @Nested
    class Exercise4Test {

        // eclipselink parses no LEFT(...) in the WHERE clause; SUBSTRING(..., 1, 1) is the first
        // letter, too
        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(
                    em -> em.createQuery(
                                    """
                                            SELECT a
                                            FROM Actor a
                                            WHERE SUBSTRING(a.firstName, 1, 1) = SUBSTRING(a.lastName, 1, 1)
                                            ORDER BY a.actorId""",
                                    Actor.class
                            )
                            .getResultList()
            );
            list.forEach(e -> log.debug("{}", e));
            assertThat(list).extracting(Actor::getActorId)
                    .containsExactly(31, 54, 70, 76, 114, 131, 135, 161, 168, 191, 200);
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Actor.class);
                final var a = cq.from(Actor.class);
                cq.select(a)
                        .where(cb.equal(cb.substring(a.get(Actor_.firstName), 1, 1),
                                        cb.substring(a.get(Actor_.lastName), 1, 1)))
                        .orderBy(cb.asc(a.get(Actor_.actorId)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(e -> log.debug("{}", e));
            assertThat(list).extracting(Actor::getActorId)
                    .containsExactly(31, 54, 70, 76, 114, 131, 135, 161, 168, 191, 200);
        }
    }

    @DisplayName("exercise 5: how many different first names and last names there are")
    @Nested
    class Exercise5Test {

        @Test
        void __QueryLanguage() {
            final var value = __Exercises_TestUtils.apply(
                    em -> em.createQuery(
                                    """
                                            SELECT COUNT(DISTINCT a.firstName), COUNT(DISTINCT a.lastName)
                                            FROM Actor a""",
                                    Object[].class
                            )
                            .getSingleResult()
            );
            log.debug("{}", Arrays.toString(value));
            assertThat(value).extracting(v -> ((Number) v).longValue())
                    .containsExactly(128L, 121L);
        }

        @Test
        void __CriteriaApi() {
            final var value = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var a = cq.from(Actor.class);
                cq.select(cb.array(cb.countDistinct(a.get(Actor_.firstName)),
                                   cb.countDistinct(a.get(Actor_.lastName))));
                return em.createQuery(cq)
                        .getSingleResult();
            });
            log.debug("{}", Arrays.toString(value));
            assertThat(value).extracting(v -> ((Number) v).longValue())
                    .containsExactly(128L, 121L);
        }
    }
}
