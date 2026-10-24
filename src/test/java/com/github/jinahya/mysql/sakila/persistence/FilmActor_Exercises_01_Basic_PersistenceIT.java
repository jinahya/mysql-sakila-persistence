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

/**
 * Answers the exercises of {@code src/test/sql/film_actor_exercises_01_basic.sql}, against the
 * installed {@code sakila} database; a nested class per exercise, which answers it with JPQL and
 * with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class FilmActor_Exercises_01_Basic_PersistenceIT {

    @DisplayName("SAKILA_FILM_ACTOR_B_01: how many films the actor 1 appears in")
    @Nested
    class SAKILA_FILM_ACTOR_B_01_Test {

        @Test
        void __QueryLanguage() {
            final var value = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                       SELECT COUNT(fa)
                                                                                       FROM FilmActor fa
                                                                                       WHERE fa.actor.actorId = :actorId""",
                                                                               Long.class)
                    .setParameter("actorId", 1)
                    .getSingleResult());
            log.debug("{}", value);
            assertThat(value).isEqualTo(19L);
        }

        @Test
        void __CriteriaApi() {
            final var value = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Long.class);
                final var fa = cq.from(FilmActor.class);
                cq.select(cb.count(fa))
                        .where(cb.equal(fa.get(FilmActor_.actor).get(Actor_.actorId),
                                        cb.parameter(Integer.class, "actorId")));
                return em.createQuery(cq)
                        .setParameter("actorId", 1)
                        .getSingleResult();
            });
            log.debug("{}", value);
            assertThat(value).isEqualTo(19L);
        }
    }

    @DisplayName("SAKILA_FILM_ACTOR_B_02: how many castings, actors and films the table has")
    @Nested
    class SAKILA_FILM_ACTOR_B_02_Test {

        @Test
        void __QueryLanguage() {
            final var value = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                       SELECT COUNT(fa), COUNT(DISTINCT fa.actor.actorId), COUNT(DISTINCT fa.film.filmId)
                                                                                       FROM FilmActor fa""",
                                                                               Object[].class)
                    .getSingleResult());
            log.debug("{}", Arrays.toString(value));
            assertThat(value)
                    .extracting(v -> ((Number) v).longValue())
                    .containsExactly(5462L, 200L, 997L);
        }

        @Test
        void __CriteriaApi() {
            final var value = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var fa = cq.from(FilmActor.class);
                cq.select(cb.array(cb.count(fa),
                                   cb.countDistinct(fa.get(FilmActor_.actor).get(Actor_.actorId)),
                                   cb.countDistinct(fa.get(FilmActor_.film).get(Film_.filmId))));
                return em.createQuery(cq)
                        .getSingleResult();
            });
            log.debug("{}", Arrays.toString(value));
            assertThat(value)
                    .extracting(v -> ((Number) v).longValue())
                    .containsExactly(5462L, 200L, 997L);
        }
    }
}
