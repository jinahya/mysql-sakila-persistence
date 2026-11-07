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

/**
 * Answers {@code SAKILA_ACTOR_A_01} of {@code src/test/sql-exercises/actor/03_advanced.sql} with
 * JPQL and with the Criteria API, or with a native query where neither can.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("SAKILA_ACTOR_A_01: the actors who appear in a film of every rating")
@Slf4j
class SAKILA_ACTOR_A_01_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(
                em -> em.createQuery(
                                """
                                        SELECT a
                                        FROM Actor a
                                        WHERE (SELECT COUNT(DISTINCT fa.film.rating)
                                               FROM FilmActor fa
                                               WHERE fa.actor = a)
                                              = (SELECT COUNT(DISTINCT f.rating) FROM Film f)
                                        ORDER BY a.actorId""",
                                Actor.class
                        )
                        .setMaxResults(5)
                        .getResultList()
        );
        list.forEach(e -> log.debug("{}", e));
        assertThat(list).extracting(Actor::getActorId).containsExactly(1, 2, 3, 4, 5);
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Actor.class);
            final var a = cq.from(Actor.class);
            // eclipselink gives the first actor five times for groupBy(a) with select(a);
            // the ratings are
            // counted per actor, by a subquery
            final var ratingsOfActor = cq.subquery(Long.class);
            final var fa = ratingsOfActor.from(FilmActor.class);
            ratingsOfActor.select(cb.countDistinct(fa.get(FilmActor_.film).get(Film_.rating)))
                    .where(cb.equal(fa.get(FilmActor_.actor), a));
            final var ratings = cq.subquery(Long.class);
            final var f = ratings.from(Film.class);
            ratings.select(cb.countDistinct(f.get(Film_.rating)));
            cq.select(a)
                    .where(cb.equal(ratingsOfActor, ratings))
                    .orderBy(cb.asc(a.get(Actor_.actorId)));
            return em.createQuery(cq)
                    .setMaxResults(5)
                    .getResultList();
        });
        list.forEach(e -> log.debug("{}", e));
        assertThat(list).extracting(Actor::getActorId).containsExactly(1, 2, 3, 4, 5);
    }
}
