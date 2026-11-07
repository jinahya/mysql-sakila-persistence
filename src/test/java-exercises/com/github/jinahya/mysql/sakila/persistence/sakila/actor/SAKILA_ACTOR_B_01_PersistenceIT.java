package com.github.jinahya.mysql.sakila.persistence.sakila.actor;

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
import com.github.jinahya.mysql.sakila.persistence.__Exercises_TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code SAKILA_ACTOR_B_01} of {@code src/test/sql-exercises/actor/01_basic.sql} with JPQL
 * and with the Criteria API, or with a native query where neither can.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("SAKILA_ACTOR_B_01: the actors of a given last name, ordered by actor_id")
@Slf4j
class SAKILA_ACTOR_B_01_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                  SELECT a
                                                                                  FROM Actor a
                                                                                  WHERE a.lastName = :lastName
                                                                                  ORDER BY a.actorId""",
                                                                          Actor.class)
                .setParameter("lastName", "GUINESS")
                .getResultList());
        list.forEach(e -> log.debug("{}", e));
        assertThat(list)
                .extracting(Actor::getActorId, Actor::getFirstName, Actor::getLastName)
                .containsExactly(
                        tuple(1, "PENELOPE", "GUINESS"),
                        tuple(90, "SEAN", "GUINESS"),
                        tuple(179, "ED", "GUINESS"));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Actor.class);
            final var a = cq.from(Actor.class);
            cq.select(a)
                    .where(cb.equal(a.get(Actor_.lastName),
                                    cb.parameter(String.class, "lastName")))
                    .orderBy(cb.asc(a.get(Actor_.actorId)));
            return em.createQuery(cq)
                    .setParameter("lastName", "GUINESS")
                    .getResultList();
        });
        list.forEach(e -> log.debug("{}", e));
        assertThat(list)
                .extracting(Actor::getActorId, Actor::getFirstName, Actor::getLastName)
                .containsExactly(
                        tuple(1, "PENELOPE", "GUINESS"),
                        tuple(90, "SEAN", "GUINESS"),
                        tuple(179, "ED", "GUINESS"));
    }

}
