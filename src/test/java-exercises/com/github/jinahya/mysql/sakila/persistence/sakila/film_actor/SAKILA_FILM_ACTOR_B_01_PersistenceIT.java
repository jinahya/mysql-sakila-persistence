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

import com.github.jinahya.mysql.sakila.persistence.Actor_;
import com.github.jinahya.mysql.sakila.persistence.FilmActor;
import com.github.jinahya.mysql.sakila.persistence.FilmActor_;
import com.github.jinahya.mysql.sakila.persistence.__Exercises_TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Answers {@code SAKILA_FILM_ACTOR_B_01} of {@code src/test/sql-exercises/film_actor/01_basic.sql}
 * with JPQL and with the Criteria API, or with a native query where neither can.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("SAKILA_FILM_ACTOR_B_01: how many films the actor 1 appears in")
@Slf4j
class SAKILA_FILM_ACTOR_B_01_PersistenceIT {

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
