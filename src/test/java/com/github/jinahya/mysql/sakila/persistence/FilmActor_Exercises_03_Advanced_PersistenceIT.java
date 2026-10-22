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
                    ORDER BY COUNT(fa1) DESC, fa1.actor.actorId, fa2.actor.actorId""", Object[].class)
                    .setMaxResults(3)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> ((Number) r[1]).intValue(), r -> ((Number) r[2]).longValue())
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
                        .where(cb.equal(fa2.get(FilmActor_.film), fa1.get(FilmActor_.film)), cb.gt(actorId2, actorId1))
                        .groupBy(actorId1, actorId2)
                        .orderBy(cb.desc(cb.count(fa1)), cb.asc(actorId1), cb.asc(actorId2));
                return em.createQuery(cq)
                        .setMaxResults(3)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> ((Number) r[1]).intValue(), r -> ((Number) r[2]).longValue())
                    .containsExactly(
                            tuple(27, 60, 7L),
                            tuple(15, 198, 6L),
                            tuple(21, 108, 6L));
        }
    }
}
