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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers the exercises of {@code src/test/sql/inventory_exercises_01_basic.sql}, against the
 * installed {@code sakila} database; a nested class per exercise, which answers it with JPQL and
 * with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Inventory_Exercises_01_Basic_PersistenceIT {

    @DisplayName("exercise 1: how many copies each store has")
    @Nested
    class Exercise1Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT i.storeId, COUNT(i)
                                                                                      FROM Inventory i
                                                                                      GROUP BY i.storeId
                                                                                      ORDER BY i.storeId""",
                                                                              Object[].class)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple(1, 2270L),
                            tuple(2, 2311L));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var i = cq.from(Inventory.class);
                cq.select(cb.array(i.get(Inventory_.storeId), cb.count(i)))
                        .groupBy(i.get(Inventory_.storeId))
                        .orderBy(cb.asc(i.get(Inventory_.storeId)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple(1, 2270L),
                            tuple(2, 2311L));
        }
    }

    @DisplayName("exercise 2: the copies of the film 1")
    @Nested
    class Exercise2Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT i
                                                                                      FROM Inventory i
                                                                                      WHERE i.filmId = :filmId
                                                                                      ORDER BY i.inventoryId""",
                                                                              Inventory.class)
                    .setParameter("filmId", 1)
                    .getResultList());
            list.forEach(e -> log.debug("{}", e));
            assertThat(list)
                    .extracting(Inventory::getInventoryId, Inventory::getStoreId)
                    .containsExactly(
                            tuple(1, 1), tuple(2, 1), tuple(3, 1), tuple(4, 1),
                            tuple(5, 2), tuple(6, 2), tuple(7, 2), tuple(8, 2));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Inventory.class);
                final var i = cq.from(Inventory.class);
                cq.select(i)
                        .where(cb.equal(i.get(Inventory_.filmId),
                                        cb.parameter(Integer.class, "filmId")))
                        .orderBy(cb.asc(i.get(Inventory_.inventoryId)));
                return em.createQuery(cq)
                        .setParameter("filmId", 1)
                        .getResultList();
            });
            list.forEach(e -> log.debug("{}", e));
            assertThat(list)
                    .extracting(Inventory::getInventoryId, Inventory::getStoreId)
                    .containsExactly(
                            tuple(1, 1), tuple(2, 1), tuple(3, 1), tuple(4, 1),
                            tuple(5, 2), tuple(6, 2), tuple(7, 2), tuple(8, 2));
        }
    }

    @DisplayName("exercise 3: how many copies of the films 1 to 5 each store has")
    @Nested
    class Exercise3Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT i.filmId, i.storeId, COUNT(i)
                                                                                      FROM Inventory i
                                                                                      WHERE i.filmId BETWEEN :from AND :to
                                                                                      GROUP BY i.filmId, i.storeId
                                                                                      ORDER BY i.filmId, i.storeId""",
                                                                              Object[].class)
                    .setParameter("from", 1)
                    .setParameter("to", 5)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> ((Number) r[1]).intValue(),
                                r -> ((Number) r[2]).longValue())
                    .containsExactly(
                            tuple(1, 1, 4L),
                            tuple(1, 2, 4L),
                            tuple(2, 2, 3L),
                            tuple(3, 2, 4L),
                            tuple(4, 1, 4L),
                            tuple(4, 2, 3L),
                            tuple(5, 2, 3L));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var i = cq.from(Inventory.class);
                cq.select(cb.array(i.get(Inventory_.filmId), i.get(Inventory_.storeId),
                                   cb.count(i)))
                        .where(cb.between(i.get(Inventory_.filmId),
                                          cb.parameter(Integer.class, "from"),
                                          cb.parameter(Integer.class, "to")))
                        .groupBy(i.get(Inventory_.filmId), i.get(Inventory_.storeId))
                        .orderBy(cb.asc(i.get(Inventory_.filmId)),
                                 cb.asc(i.get(Inventory_.storeId)));
                return em.createQuery(cq)
                        .setParameter("from", 1)
                        .setParameter("to", 5)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> ((Number) r[1]).intValue(),
                                r -> ((Number) r[2]).longValue())
                    .containsExactly(
                            tuple(1, 1, 4L),
                            tuple(1, 2, 4L),
                            tuple(2, 2, 3L),
                            tuple(3, 2, 4L),
                            tuple(4, 1, 4L),
                            tuple(4, 2, 3L),
                            tuple(5, 2, 3L));
        }
    }
}
