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

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers the exercises of {@code src/test/sql/actor_info_exercises_02_intermediate.sql}, against
 * the installed {@code sakila} database; a nested class per exercise, which answers it with JPQL
 * and with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class ActorInfo_Exercises_02_Intermediate_PersistenceIT {

    @DisplayName("SAKILA_ACTOR_INFO_I_01: whether the view has a row for every actor")
    @Nested
    class SAKILA_ACTOR_INFO_I_01_Test {

        // JPQL has no subquery in the SELECT clause; the two counts are two queries
        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> List.<Object[]>of(new Object[]{
                    em.createQuery("SELECT COUNT(e) FROM ActorInfo e",
                                   Long.class).getSingleResult(),
                    em.createQuery("SELECT COUNT(e) FROM Actor e", Long.class).getSingleResult()}));
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).longValue(), r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple(200L, 200L));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> List.<Object[]>of(new Object[]{
                    __Persistence_TestUtils.selectEntityCount(em, ActorInfo.class),
                    __Persistence_TestUtils.selectEntityCount(em, Actor.class)}));
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).longValue(), r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple(200L, 200L));
        }
    }

    @DisplayName("SAKILA_ACTOR_INFO_I_02: the actors whose film_info covers every category")
    @Nested
    class SAKILA_ACTOR_INFO_I_02_Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT i
                                                                                      FROM ActorInfo i
                                                                                      WHERE LENGTH(i.filmInfo) - LENGTH(REPLACE(i.filmInfo, ':', '')) = (SELECT COUNT(c) FROM Category c)
                                                                                      ORDER BY i.actorId""",
                                                                              ActorInfo.class)
                    .getResultList());
            list.forEach(e -> log.debug("{}", e));
            assertThat(list)
                    .extracting(ActorInfo::getActorId, ActorInfo::getFirstName,
                                ActorInfo::getLastName)
                    .containsExactly(
                            tuple(13, "UMA", "WOOD"),
                            tuple(72, "SEAN", "WILLIAMS"),
                            tuple(95, "DARYL", "WAHLBERG"),
                            tuple(106, "GROUCHO", "DUNST"),
                            tuple(107, "GINA", "DEGENERES"),
                            tuple(127, "KEVIN", "GARLAND"),
                            tuple(139, "EWAN", "GOODING"),
                            tuple(155, "IAN", "TANDY"),
                            tuple(161, "HARVEY", "HOPE"),
                            tuple(185, "MICHAEL", "BOLGER"),
                            tuple(197, "REESE", "WEST"));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(ActorInfo.class);
                final var i = cq.from(ActorInfo.class);
                final var categories = cq.subquery(Long.class);
                final var c = categories.from(Category.class);
                categories.select(cb.count(c));
                final var filmInfo = i.get(ActorInfo_.filmInfo);
                cq.select(i)
                        .where(cb.equal(cb.diff(cb.length(filmInfo),
                                                cb.length(cb.replace(filmInfo, ":", ""))),
                                        categories))
                        .orderBy(cb.asc(i.get(ActorInfo_.actorId)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(e -> log.debug("{}", e));
            assertThat(list)
                    .extracting(ActorInfo::getActorId, ActorInfo::getFirstName,
                                ActorInfo::getLastName)
                    .containsExactly(
                            tuple(13, "UMA", "WOOD"),
                            tuple(72, "SEAN", "WILLIAMS"),
                            tuple(95, "DARYL", "WAHLBERG"),
                            tuple(106, "GROUCHO", "DUNST"),
                            tuple(107, "GINA", "DEGENERES"),
                            tuple(127, "KEVIN", "GARLAND"),
                            tuple(139, "EWAN", "GOODING"),
                            tuple(155, "IAN", "TANDY"),
                            tuple(161, "HARVEY", "HOPE"),
                            tuple(185, "MICHAEL", "BOLGER"),
                            tuple(197, "REESE", "WEST"));
        }
    }

    @DisplayName("SAKILA_ACTOR_INFO_I_03: how many actors cover how many categories")
    @Nested
    class SAKILA_ACTOR_INFO_I_03_Test {

        // JPQL groups by paths, not by an expression of one; the Criteria API's grouping by an
        // expression is not portable either
        @Test
        void nativeQuery() {
            final var list = __Exercises_TestUtils.apply(em -> (List<?>) em.createNativeQuery("""
                                                                                                      SELECT CHAR_LENGTH(film_info) - CHAR_LENGTH(REPLACE(film_info, ':', '')) AS categories, COUNT(*) AS actors
                                                                                                      FROM actor_info
                                                                                                      GROUP BY categories
                                                                                                      ORDER BY categories DESC""")
                    .getResultList());
            list.forEach(r -> log.debug("{}", r instanceof Object[] a ? Arrays.toString(a) : r));
            assertThat(list)
                    .extracting(r -> ((Number) ((Object[]) r)[0]).longValue(),
                                r -> ((Number) ((Object[]) r)[1]).longValue())
                    .containsExactly(
                            tuple(16L, 11L),
                            tuple(15L, 19L),
                            tuple(14L, 49L),
                            tuple(13L, 57L),
                            tuple(12L, 33L),
                            tuple(11L, 19L),
                            tuple(10L, 8L),
                            tuple(9L, 2L),
                            tuple(8L, 2L));
        }
    }
}
