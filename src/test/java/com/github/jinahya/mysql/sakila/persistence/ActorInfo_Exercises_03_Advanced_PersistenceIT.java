package com.github.jinahya.mysql.sakila.persistence;

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

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Answers the exercises of {@code src/test/sql/actor_info_exercises_03_advanced.sql}, against the
 * installed {@code sakila} database; a nested class per exercise, which answers it with JPQL and
 * with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class ActorInfo_Exercises_03_Advanced_PersistenceIT {

    @DisplayName(
            "exercise 1: the actors whose film_info disagrees with the tables on the number of " +
            "categories")
    @Nested
    class Exercise1Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT i
                                                                                      FROM ActorInfo i
                                                                                      WHERE LENGTH(i.filmInfo) - LENGTH(REPLACE(i.filmInfo, ':', ''))
                                                                                                <> (SELECT COUNT(DISTINCT fc.categoryId)
                                                                                                    FROM FilmActor fa
                                                                                                             JOIN fa.film f
                                                                                                             JOIN f.filmCategories fc
                                                                                                    WHERE fa.actor.actorId = i.actorId)
                                                                                      ORDER BY i.actorId""",
                                                                              ActorInfo.class)
                    .getResultList());
            list.forEach(e -> log.debug("{}", e));
            assertThat(list).isEmpty();
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(ActorInfo.class);
                final var i = cq.from(ActorInfo.class);
                final var categories = cq.subquery(Long.class);
                final var fa = categories.from(FilmActor.class);
                final var fc = fa.join(FilmActor_.film).join(Film_.filmCategories);
                categories.select(cb.countDistinct(fc.get(FilmCategory_.categoryId)))
                        .where(cb.equal(fa.get(FilmActor_.actor).get(Actor_.actorId),
                                        i.get(ActorInfo_.actorId)));
                final var filmInfo = i.get(ActorInfo_.filmInfo);
                cq.select(i)
                        .where(cb.notEqual(cb.diff(cb.length(filmInfo),
                                                   cb.length(cb.replace(filmInfo, ":", ""))),
                                           categories))
                        .orderBy(cb.asc(i.get(ActorInfo_.actorId)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(e -> log.debug("{}", e));
            assertThat(list).isEmpty();
        }
    }
}
