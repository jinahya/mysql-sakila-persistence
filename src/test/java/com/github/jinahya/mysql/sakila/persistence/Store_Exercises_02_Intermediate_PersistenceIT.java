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
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers the exercises of {@code src/test/sql/store_exercises_02_intermediate.sql}, against the
 * installed {@code sakila} database; a nested class per exercise, which answers it with JPQL and
 * with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Store_Exercises_02_Intermediate_PersistenceIT {

    @DisplayName("exercise 1: every store, with its manager and city")
    @Nested
    class Exercise1Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT st.storeId, s.firstName, s.lastName, c.city
                                                                                      FROM Store st
                                                                                               JOIN st.managerStaff s
                                                                                               JOIN st.address a
                                                                                               JOIN a.city c
                                                                                      ORDER BY st.storeId""",
                                                                              Object[].class)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> r[1], r -> r[2], r -> r[3])
                    .containsExactly(
                            tuple(1, "Mike", "Hillyer", "Lethbridge"),
                            tuple(2, "Jon", "Stephens", "Woodridge"));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var st = cq.from(Store.class);
                final var s = st.join(Store_.managerStaff);
                final var c = st.join(Store_.address).join(Address_.city);
                cq.select(cb.array(st.get(Store_.storeId), s.get(Staff_.firstName),
                                   s.get(Staff_.lastName), c.get(City_.city)))
                        .orderBy(cb.asc(st.get(Store_.storeId)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> r[1], r -> r[2], r -> r[3])
                    .containsExactly(
                            tuple(1, "Mike", "Hillyer", "Lethbridge"),
                            tuple(2, "Jon", "Stephens", "Woodridge"));
        }
    }

    @DisplayName("exercise 2: how many customers, copies and staff members each store has")
    @Nested
    class Exercise2Test {

        // the three counts are three grouped queries, put together by the store
        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> {
                // eclipselink counts SIZE(st.customers) and SIZE(st.inventories) of one SELECT
                // over their cross product;
                // each count is a grouped query
                final var customers = em.createQuery("""
                                                             SELECT c.store.storeId, COUNT(c)
                                                             FROM Customer c
                                                             GROUP BY c.store.storeId""",
                                                     Object[].class)
                        .getResultStream().collect(Collectors.toMap(r -> r[0], r -> r[1]));
                final var copies = em.createQuery("""
                                                          SELECT i.storeId, COUNT(i)
                                                          FROM Inventory i
                                                          GROUP BY i.storeId""", Object[].class)
                        .getResultStream().collect(Collectors.toMap(r -> r[0], r -> r[1]));
                final var staff = em.createQuery("""
                                                         SELECT s.store.storeId, COUNT(s)
                                                         FROM Staff s
                                                         GROUP BY s.store.storeId""",
                                                 Object[].class)
                        .getResultStream().collect(Collectors.toMap(r -> r[0], r -> r[1]));
                return em.createQuery("""
                                              SELECT st.storeId
                                              FROM Store st
                                              ORDER BY st.storeId""", Integer.class)
                        .getResultStream()
                        .map(id -> new Object[]{id, customers.get(id), copies.get(id), staff.get(
                                id)})
                        .toList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> ((Number) r[1]).longValue(),
                                r -> ((Number) r[2]).longValue(), r -> ((Number) r[3]).longValue())
                    .containsExactly(
                            tuple(1, 326L, 2270L, 1L),
                            tuple(2, 273L, 2311L, 1L));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                // eclipselink fails cb.size(st.get(Store_.customers)) with a ClassCastException;
                // each count is a grouped query
                final var customers = cb.createQuery(Object[].class);
                final var c = customers.from(Customer.class);
                final var customerStoreId = c.get(Customer_.store).get(Store_.storeId);
                customers.select(cb.array(customerStoreId, cb.count(c))).groupBy(customerStoreId);
                final var copies = cb.createQuery(Object[].class);
                final var i = copies.from(Inventory.class);
                copies.select(cb.array(i.get(Inventory_.storeId), cb.count(i)))
                        .groupBy(i.get(Inventory_.storeId));
                final var staff = cb.createQuery(Object[].class);
                final var s = staff.from(Staff.class);
                final var staffStoreId = s.get(Staff_.store).get(Store_.storeId);
                staff.select(cb.array(staffStoreId, cb.count(s))).groupBy(staffStoreId);
                final var byCustomers = em.createQuery(customers).getResultStream()
                        .collect(Collectors.toMap(r -> r[0], r -> r[1]));
                final var byCopies = em.createQuery(copies).getResultStream()
                        .collect(Collectors.toMap(r -> r[0], r -> r[1]));
                final var byStaff = em.createQuery(staff).getResultStream()
                        .collect(Collectors.toMap(r -> r[0], r -> r[1]));
                final var cq = cb.createQuery(Object.class);
                final var st = cq.from(Store.class);
                cq.select(st.get(Store_.storeId)).orderBy(cb.asc(st.get(Store_.storeId)));
                return em.createQuery(cq).getResultStream()
                        .map(id -> new Object[]{id, byCustomers.get(id), byCopies.get(
                                id), byStaff.get(id)})
                        .toList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> ((Number) r[1]).longValue(),
                                r -> ((Number) r[2]).longValue(), r -> ((Number) r[3]).longValue())
                    .containsExactly(
                            tuple(1, 326L, 2270L, 1L),
                            tuple(2, 273L, 2311L, 1L));
        }
    }
}
