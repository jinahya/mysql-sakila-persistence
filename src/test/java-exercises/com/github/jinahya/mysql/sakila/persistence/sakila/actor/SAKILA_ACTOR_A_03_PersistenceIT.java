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

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code SAKILA_ACTOR_A_03} of {@code src/test/sql-exercises/actor/03_advanced.sql} with
 * JPQL and with the Criteria API, or with a native query where neither can.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("SAKILA_ACTOR_A_03: the longest film of each of the first five actors")
@Slf4j
class SAKILA_ACTOR_A_03_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(
                em -> em.createQuery(
                                """
                                        SELECT a, f
                                        FROM Actor a
                                                 JOIN a.filmActors fa
                                                 JOIN fa.film f
                                        WHERE a.actorId BETWEEN 1 AND 5
                                          AND f.length = (SELECT MAX(f2.length)
                                                          FROM FilmActor fa2
                                                                   JOIN fa2.film f2
                                                          WHERE fa2.actor = a)
                                        ORDER BY a.actorId, f.title""",
                                Object[].class
                        )
                        .getResultList()
        );
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> ((Actor) r[0]).getActorId(),
                            r -> ((Actor) r[0]).getFirstName(),
                            r -> ((Actor) r[0]).getLastName(), r -> ((Film) r[1]).getTitle(),
                            r -> ((Film) r[1]).getLength())
                .containsExactly(
                        tuple(1, "PENELOPE", "GUINESS", "KING EVOLUTION", 184),
                        tuple(2, "NICK", "WAHLBERG", "WARDROBE PHANTOM", 178),
                        tuple(3, "ED", "CHASE", "YOUNG LANGUAGE", 183),
                        tuple(4, "JENNIFER", "DAVIS", "REDS POCUS", 182),
                        tuple(5, "JOHNNY", "LOLLOBRIGIDA", "SOLDIERS EVOLUTION", 185));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Object[].class);
            final var a = cq.from(Actor.class);
            final var f = a.join(Actor_.filmActors).join(FilmActor_.film);
            final var longest = cq.subquery(Integer.class);
            final var fa2 = longest.from(FilmActor.class);
            final var f2 = fa2.join(FilmActor_.film);
            longest.select(cb.max(f2.get(Film_.length)))
                    .where(cb.equal(fa2.get(FilmActor_.actor), a));
            cq.select(cb.array(a, f))
                    .where(cb.between(a.get(Actor_.actorId), 1, 5),
                           cb.equal(f.get(Film_.length), longest))
                    .orderBy(cb.asc(a.get(Actor_.actorId)), cb.asc(f.get(Film_.title)));
            return em.createQuery(cq)
                    .getResultList();
        });
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> ((Actor) r[0]).getActorId(),
                            r -> ((Actor) r[0]).getFirstName(),
                            r -> ((Actor) r[0]).getLastName(), r -> ((Film) r[1]).getTitle(),
                            r -> ((Film) r[1]).getLength())
                .containsExactly(
                        tuple(1, "PENELOPE", "GUINESS", "KING EVOLUTION", 184),
                        tuple(2, "NICK", "WAHLBERG", "WARDROBE PHANTOM", 178),
                        tuple(3, "ED", "CHASE", "YOUNG LANGUAGE", 183),
                        tuple(4, "JENNIFER", "DAVIS", "REDS POCUS", 182),
                        tuple(5, "JOHNNY", "LOLLOBRIGIDA", "SOLDIERS EVOLUTION", 185));
    }

}
