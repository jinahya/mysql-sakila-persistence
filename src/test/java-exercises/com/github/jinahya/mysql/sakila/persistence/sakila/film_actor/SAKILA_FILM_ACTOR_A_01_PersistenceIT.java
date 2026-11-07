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
 * Answers {@code SAKILA_FILM_ACTOR_A_01} of
 * {@code src/test/sql-exercises/film_actor/03_advanced.sql} with JPQL and with the Criteria API, or
 * with a native query where neither can.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("SAKILA_FILM_ACTOR_A_01: the three pairs of actors who appear together the most")
@Slf4j
class SAKILA_FILM_ACTOR_A_01_PersistenceIT {

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
