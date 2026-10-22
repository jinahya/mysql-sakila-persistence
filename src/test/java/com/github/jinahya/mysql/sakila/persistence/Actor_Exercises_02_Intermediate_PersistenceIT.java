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

    @DisplayName("exercise 1: the last names shared by more than three actors, the most shared first")
    @Nested
    class Exercise1Test {

        @ParameterizedTest
        @ValueSource(longs = {2, 3, 4})
        void __QueryLanguage(final long count) {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                    SELECT a.lastName, COUNT(a)
                    FROM Actor a
                    GROUP BY a.lastName
                    HAVING COUNT(a) > :count
                    ORDER BY COUNT(a) DESC, a.lastName""", Object[].class)
                    .setParameter("count", count)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list).isNotEmpty().allSatisfy(r -> assertThat(((Number) r[1]).longValue()).isGreaterThan(count));
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
            assertThat(list).isNotEmpty().allSatisfy(r -> assertThat(((Number) r[1]).longValue()).isGreaterThan(count));
        }
    }

    @DisplayName("exercise 2: the five actors who appear in the most films")
    @Nested
    class Exercise2Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                    SELECT a.actorId, a.firstName, a.lastName, COUNT(fa)
                    FROM Actor a
                             JOIN a.filmActors fa
                    GROUP BY a.actorId, a.firstName, a.lastName
                    ORDER BY COUNT(fa) DESC, a.actorId""", Object[].class)
                    .setMaxResults(5)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> r[1], r -> r[2], r -> ((Number) r[3]).longValue())
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
                cq.select(cb.array(a.get(Actor_.actorId), a.get(Actor_.firstName), a.get(Actor_.lastName), cb.count(fa)))
                        .groupBy(a.get(Actor_.actorId), a.get(Actor_.firstName), a.get(Actor_.lastName))
                        .orderBy(cb.desc(cb.count(fa)), cb.asc(a.get(Actor_.actorId)));
                return em.createQuery(cq)
                        .setMaxResults(5)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> r[1], r -> r[2], r -> ((Number) r[3]).longValue())
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
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                    SELECT a
                    FROM Actor a
                    WHERE a.filmActors IS EMPTY""", Actor.class)
                    .getResultList());
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
}
