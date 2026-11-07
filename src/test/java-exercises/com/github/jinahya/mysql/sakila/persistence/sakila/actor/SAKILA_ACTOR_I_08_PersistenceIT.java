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
import com.github.jinahya.mysql.sakila.persistence.Film;
import com.github.jinahya.mysql.sakila.persistence.FilmActor;
import com.github.jinahya.mysql.sakila.persistence.FilmActor_;
import com.github.jinahya.mysql.sakila.persistence.Film_;
import com.github.jinahya.mysql.sakila.persistence.__Exercises_TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code SAKILA_ACTOR_I_08} of {@code src/test/sql-exercises/actor/02_intermediate.sql}
 * with JPQL and with the Criteria API, or with a native query where neither can.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("SAKILA_ACTOR_I_08: the actors who never appeared in a G-rated film")
@Slf4j
class SAKILA_ACTOR_I_08_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(
                em -> em.createQuery(
                                """
                                        SELECT a
                                        FROM Actor a
                                        WHERE NOT EXISTS (SELECT fa
                                                          FROM FilmActor fa
                                                                   JOIN fa.film f
                                                          WHERE fa.actor = a
                                                            AND f.rating = :rating)
                                        ORDER BY a.actorId""",
                                Actor.class
                        )
                        .setParameter("rating", Film.Rating.G)
                        .getResultList()
        );
        list.forEach(e -> log.debug("{}", e));
        assertThat(list)
                .extracting(Actor::getActorId, Actor::getFirstName, Actor::getLastName)
                .containsExactly(tuple(194, "MERYL", "ALLEN"));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Actor.class);
            final var a = cq.from(Actor.class);
            final var g = cq.subquery(FilmActor.class);
            final var fa = g.from(FilmActor.class);
            final var f = fa.join(FilmActor_.film);
            g.select(fa)
                    .where(cb.equal(fa.get(FilmActor_.actor), a),
                           cb.equal(f.get(Film_.rating),
                                    cb.parameter(Film.Rating.class, "rating")));
            cq.select(a)
                    .where(cb.not(cb.exists(g)))
                    .orderBy(cb.asc(a.get(Actor_.actorId)));
            return em.createQuery(cq)
                    .setParameter("rating", Film.Rating.G)
                    .getResultList();
        });
        list.forEach(e -> log.debug("{}", e));
        assertThat(list)
                .extracting(Actor::getActorId, Actor::getFirstName, Actor::getLastName)
                .containsExactly(tuple(194, "MERYL", "ALLEN"));
    }

}
