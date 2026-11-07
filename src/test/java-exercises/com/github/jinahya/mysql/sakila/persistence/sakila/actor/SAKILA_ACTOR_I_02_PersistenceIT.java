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
 * Answers {@code SAKILA_ACTOR_I_02} of {@code src/test/sql-exercises/actor/02_intermediate.sql}
 * with JPQL and with the Criteria API, or with a native query where neither can.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("SAKILA_ACTOR_I_02: the top n actors by their number of films")
@Slf4j
class SAKILA_ACTOR_I_02_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(
                em -> em.createQuery(
                                """
                                        SELECT a, COUNT(fa)
                                        FROM Actor a
                                                 JOIN a.filmActors fa
                                        GROUP BY a
                                        ORDER BY COUNT(fa) DESC, a.actorId""",
                                Object[].class
                        )
                        .setMaxResults(5)
                        .getResultList()
        );
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> ((Actor) r[0]).getActorId(),
                            r -> ((Actor) r[0]).getFirstName(),
                            r -> ((Actor) r[0]).getLastName(), r -> ((Number) r[1]).longValue())
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
            cq.select(cb.array(a, cb.count(fa)))
                    .groupBy(a)
                    .orderBy(cb.desc(cb.count(fa)), cb.asc(a.get(Actor_.actorId)));
            return em.createQuery(cq)
                    .setMaxResults(5)
                    .getResultList();
        });
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> ((Actor) r[0]).getActorId(),
                            r -> ((Actor) r[0]).getFirstName(),
                            r -> ((Actor) r[0]).getLastName(), r -> ((Number) r[1]).longValue())
                .containsExactly(
                        tuple(107, "GINA", "DEGENERES", 42L),
                        tuple(102, "WALTER", "TORN", 41L),
                        tuple(198, "MARY", "KEITEL", 40L),
                        tuple(181, "MATTHEW", "CARREY", 39L),
                        tuple(23, "SANDRA", "KILMER", 37L));
    }

}
