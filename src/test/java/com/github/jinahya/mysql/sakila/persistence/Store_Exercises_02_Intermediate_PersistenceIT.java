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
                                                                                      SELECT st, s, c
                                                                                      FROM Store st
                                                                                               JOIN st.managerStaff s
                                                                                               JOIN st.address a
                                                                                               JOIN a.city c
                                                                                      ORDER BY st.storeId""",
                                                                              Object[].class)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Store) r[0]).getStoreId(),
                                r -> ((Staff) r[1]).getFirstName(),
                                r -> ((Staff) r[1]).getLastName(), r -> ((City) r[2]).getCity())
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
                cq.select(cb.array(st, s, c))
                        .orderBy(cb.asc(st.get(Store_.storeId)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Store) r[0]).getStoreId(),
                                r -> ((Staff) r[1]).getFirstName(),
                                r -> ((Staff) r[1]).getLastName(), r -> ((City) r[2]).getCity())
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
                                                             SELECT st, COUNT(c)
                                                             FROM Customer c
                                                                      JOIN c.store st
                                                             GROUP BY st""",
                                                     Object[].class)
                        .getResultStream()
                        .collect(Collectors.toMap(r -> ((Store) r[0]).getStoreId(), r -> r[1]));
                final var copies = em.createQuery("""
                                                          SELECT st, COUNT(i)
                                                          FROM Inventory i
                                                                   JOIN i.store st
                                                          GROUP BY st""", Object[].class)
                        .getResultStream()
                        .collect(Collectors.toMap(r -> ((Store) r[0]).getStoreId(), r -> r[1]));
                final var staff = em.createQuery("""
                                                         SELECT st, COUNT(s)
                                                         FROM Staff s
                                                                  JOIN s.store st
                                                         GROUP BY st""",
                                                 Object[].class)
                        .getResultStream()
                        .collect(Collectors.toMap(r -> ((Store) r[0]).getStoreId(), r -> r[1]));
                return em.createQuery("""
                                              SELECT st
                                              FROM Store st
                                              ORDER BY st.storeId""", Store.class)
                        .getResultStream()
                        .map(st -> new Object[]{st, customers.get(st.getStoreId()),
                                                copies.get(st.getStoreId()),
                                                staff.get(st.getStoreId())})
                        .toList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Store) r[0]).getStoreId(), r -> ((Number) r[1]).longValue(),
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
                final var customerStore = c.join(Customer_.store);
                customers.select(cb.array(customerStore, cb.count(c))).groupBy(customerStore);
                final var copies = cb.createQuery(Object[].class);
                final var i = copies.from(Inventory.class);
                final var copyStore = i.join(Inventory_.store);
                copies.select(cb.array(copyStore, cb.count(i))).groupBy(copyStore);
                final var staff = cb.createQuery(Object[].class);
                final var s = staff.from(Staff.class);
                final var staffStore = s.join(Staff_.store);
                staff.select(cb.array(staffStore, cb.count(s))).groupBy(staffStore);
                final var byCustomers = em.createQuery(customers).getResultStream()
                        .collect(Collectors.toMap(r -> ((Store) r[0]).getStoreId(), r -> r[1]));
                final var byCopies = em.createQuery(copies).getResultStream()
                        .collect(Collectors.toMap(r -> ((Store) r[0]).getStoreId(), r -> r[1]));
                final var byStaff = em.createQuery(staff).getResultStream()
                        .collect(Collectors.toMap(r -> ((Store) r[0]).getStoreId(), r -> r[1]));
                final var cq = cb.createQuery(Store.class);
                final var st = cq.from(Store.class);
                cq.select(st).orderBy(cb.asc(st.get(Store_.storeId)));
                return em.createQuery(cq).getResultStream()
                        .map(e -> new Object[]{e, byCustomers.get(e.getStoreId()),
                                               byCopies.get(e.getStoreId()),
                                               byStaff.get(e.getStoreId())})
                        .toList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Store) r[0]).getStoreId(), r -> ((Number) r[1]).longValue(),
                                r -> ((Number) r[2]).longValue(), r -> ((Number) r[3]).longValue())
                    .containsExactly(
                            tuple(1, 326L, 2270L, 1L),
                            tuple(2, 273L, 2311L, 1L));
        }
    }
}
