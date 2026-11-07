package com.github.jinahya.mysql.sakila.persistence.sakila.film_actor;

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

import com.github.jinahya.mysql.sakila.persistence.Actor;
import com.github.jinahya.mysql.sakila.persistence.Actor_;
import com.github.jinahya.mysql.sakila.persistence.FilmActor;
import com.github.jinahya.mysql.sakila.persistence.FilmActor_;
import com.github.jinahya.mysql.sakila.persistence.__Exercises_TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code SAKILA_FILM_ACTOR_I_03} of
 * {@code src/test/sql-exercises/film_actor/02_intermediate.sql} with JPQL and with the Criteria
 * API, or with a native query where neither can.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("SAKILA_FILM_ACTOR_I_03: the five actors who appear most often with the actor 1")
@Slf4j
class SAKILA_FILM_ACTOR_I_03_PersistenceIT {

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
