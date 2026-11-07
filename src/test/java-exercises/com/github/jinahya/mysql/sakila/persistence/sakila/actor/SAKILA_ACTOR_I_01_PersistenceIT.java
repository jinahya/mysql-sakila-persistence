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

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code SAKILA_ACTOR_I_01} of {@code src/test/sql-exercises/actor/02_intermediate.sql}
 * with JPQL and with the Criteria API, or with a native query where neither can.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName(
        "SAKILA_ACTOR_I_01: the last names shared by more than three actors, the most shared " +
        "first")
@Slf4j
class SAKILA_ACTOR_I_01_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                  SELECT a.lastName, COUNT(a)
                                                                                  FROM Actor a
                                                                                  GROUP BY a.lastName
                                                                                  HAVING COUNT(a) > :actors
                                                                                  ORDER BY COUNT(a) DESC, a.lastName""",
                                                                          Object[].class)
                .setParameter("actors", 3L)
                .getResultList());
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> r[0], r -> ((Number) r[1]).longValue())
                .containsExactly(
                        tuple("KILMER", 5L),
                        tuple("NOLTE", 4L),
                        tuple("TEMPLE", 4L));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Object[].class);
            final var a = cq.from(Actor.class);
            final var actors = cb.count(a);
            cq.select(cb.array(a.get(Actor_.lastName), actors))
                    .groupBy(a.get(Actor_.lastName))
                    .having(cb.gt(actors, cb.parameter(Long.class, "actors")))
                    .orderBy(cb.desc(actors), cb.asc(a.get(Actor_.lastName)));
            return em.createQuery(cq)
                    .setParameter("actors", 3L)
                    .getResultList();
        });
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> r[0], r -> ((Number) r[1]).longValue())
                .containsExactly(
                        tuple("KILMER", 5L),
                        tuple("NOLTE", 4L),
                        tuple("TEMPLE", 4L));
    }

}
