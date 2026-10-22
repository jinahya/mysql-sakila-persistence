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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Answers the exercises of {@code src/test/sql/actor_info_exercises_01_basic.sql}, against the
 * installed {@code sakila} database; a nested class per exercise, which answers it with JPQL and
 * with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class ActorInfo_Exercises_01_Basic_PersistenceIT {

    @DisplayName("exercise 1: the film_info of the actor 1")
    @Nested
    class Exercise1Test {

        @ParameterizedTest
        @ValueSource(ints = {1, 2, 3})
        void __QueryLanguage(final int actorId) {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                    SELECT i.actorId, i.firstName, i.lastName, LEFT(i.filmInfo, 80)
                    FROM ActorInfo i
                    WHERE i.actorId = :actorId""", Object[].class)
                    .setParameter("actorId", actorId)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list).singleElement().satisfies(r -> {
                assertThat(r[0]).isEqualTo(actorId);
                assertThat((String) r[3]).hasSizeLessThanOrEqualTo(80);
            });
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 2, 3})
        void __CriteriaApi(final int actorId) {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var i = cq.from(ActorInfo.class);
                cq.select(cb.array(i.get(ActorInfo_.actorId), i.get(ActorInfo_.firstName), i.get(ActorInfo_.lastName),
                               cb.left(i.get(ActorInfo_.filmInfo), 80)))
                        .where(cb.equal(i.get(ActorInfo_.actorId), cb.parameter(Integer.class, "actorId")));
                return em.createQuery(cq)
                        .setParameter("actorId", actorId)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list).singleElement().satisfies(r -> {
                assertThat(r[0]).isEqualTo(actorId);
                assertThat((String) r[3]).hasSizeLessThanOrEqualTo(80);
            });
        }
    }

    @DisplayName("exercise 2: how many actors appear in a Horror film, as film_info says")
    @Nested
    class Exercise2Test {

        @ParameterizedTest
        @ValueSource(strings = {"Horror", "Action", "Comedy"})
        void __QueryLanguage(final String category) {
            final var value = __Exercises_TestUtils.apply(em -> em.createQuery("""
                    SELECT COUNT(i)
                    FROM ActorInfo i
                    WHERE i.filmInfo LIKE :pattern""", Long.class)
                    .setParameter("pattern", "%" + category + ":%")
                    .getSingleResult());
            log.debug("{}", value);
            assertThat(value).isPositive();
        }

        @ParameterizedTest
        @ValueSource(strings = {"Horror", "Action", "Comedy"})
        void __CriteriaApi(final String category) {
            final var value = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Long.class);
                final var i = cq.from(ActorInfo.class);
                cq.select(cb.count(i))
                        .where(cb.like(i.get(ActorInfo_.filmInfo), cb.parameter(String.class, "pattern")));
                return em.createQuery(cq)
                        .setParameter("pattern", "%" + category + ":%")
                        .getSingleResult();
            });
            log.debug("{}", value);
            assertThat(value).isPositive();
        }
    }
}
