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
 * Answers the exercises of {@code src/test/sql/exercises/inventory_exercises_01_basic.sql}, against the
 * installed {@code sakila} database; a nested class per exercise, which answers it with JPQL and
 * with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Inventory_Exercises_01_Basic_PersistenceIT {

    @DisplayName("SAKILA_INVENTORY_B_01: how many copies each store has")
    @Nested
    class SAKILA_INVENTORY_B_01_Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT st, COUNT(i)
                                                                                      FROM Inventory i
                                                                                               JOIN i.store st
                                                                                      GROUP BY st
                                                                                      ORDER BY st.storeId""",
                                                                              Object[].class)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Store) r[0]).getStoreId(), r -> ((Number) r[1]).longValue())
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
                final var st = i.join(Inventory_.store);
                cq.select(cb.array(st, cb.count(i)))
                        .groupBy(st)
                        .orderBy(cb.asc(st.get(Store_.storeId)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Store) r[0]).getStoreId(), r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple(1, 2270L),
                            tuple(2, 2311L));
        }
    }

    @DisplayName("SAKILA_INVENTORY_B_02: the copies of the film 1")
    @Nested
    class SAKILA_INVENTORY_B_02_Test {

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

    @DisplayName("SAKILA_INVENTORY_B_03: how many copies of the films 1 to 5 each store has")
    @Nested
    class SAKILA_INVENTORY_B_03_Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT f, st, COUNT(i)
                                                                                      FROM Inventory i
                                                                                               JOIN i.film f
                                                                                               JOIN i.store st
                                                                                      WHERE f.filmId BETWEEN :from AND :to
                                                                                      GROUP BY f, st
                                                                                      ORDER BY f.filmId, st.storeId""",
                                                                              Object[].class)
                    .setParameter("from", 1)
                    .setParameter("to", 5)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Film) r[0]).getFilmId(), r -> ((Store) r[1]).getStoreId(),
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
                final var f = i.join(Inventory_.film);
                final var st = i.join(Inventory_.store);
                cq.select(cb.array(f, st, cb.count(i)))
                        .where(cb.between(f.get(Film_.filmId),
                                          cb.parameter(Integer.class, "from"),
                                          cb.parameter(Integer.class, "to")))
                        .groupBy(f, st)
                        .orderBy(cb.asc(f.get(Film_.filmId)), cb.asc(st.get(Store_.storeId)));
                return em.createQuery(cq)
                        .setParameter("from", 1)
                        .setParameter("to", 5)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Film) r[0]).getFilmId(), r -> ((Store) r[1]).getStoreId(),
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
