package com.github.jinahya.mysql.sakila.persistence.sakila.actor_info;

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

import com.github.jinahya.mysql.sakila.persistence.ActorInfo;
import com.github.jinahya.mysql.sakila.persistence.ActorInfo_;
import com.github.jinahya.mysql.sakila.persistence.__Exercises_TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code SAKILA_ACTOR_INFO_B_01} of {@code src/test/sql-exercises/actor_info/01_basic.sql}
 * with JPQL and with the Criteria API, or with a native query where neither can.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("SAKILA_ACTOR_INFO_B_01: the film_info of the actor 1")
@Slf4j
class SAKILA_ACTOR_INFO_B_01_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                  SELECT a, LEFT(a.filmInfo, 80)
                                                                                  FROM ActorInfo a
                                                                                  WHERE a.actorId = :actorId""",
                                                                          Object[].class)
                .setParameter("actorId", 1)
                .getResultList());
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> ((ActorInfo) r[0]).getActorId(),
                            r -> ((ActorInfo) r[0]).getFirstName(),
                            r -> ((ActorInfo) r[0]).getLastName(), r -> r[1])
                .containsExactly(
                        tuple(1, "PENELOPE", "GUINESS",
                              "Animation: ANACONDA CONFESSIONS; Children: LANGUAGE COWBOY; " +
                              "Classics: COLOR PHIL"));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Object[].class);
            final var a = cq.from(ActorInfo.class);
            cq.select(cb.array(a, cb.left(a.get(ActorInfo_.filmInfo), 80)))
                    .where(cb.equal(a.get(ActorInfo_.actorId),
                                    cb.parameter(Integer.class, "actorId")));
            return em.createQuery(cq)
                    .setParameter("actorId", 1)
                    .getResultList();
        });
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> ((ActorInfo) r[0]).getActorId(),
                            r -> ((ActorInfo) r[0]).getFirstName(),
                            r -> ((ActorInfo) r[0]).getLastName(), r -> r[1])
                .containsExactly(
                        tuple(1, "PENELOPE", "GUINESS",
                              "Animation: ANACONDA CONFESSIONS; Children: LANGUAGE COWBOY; " +
                              "Classics: COLOR PHIL"));
    }

}
