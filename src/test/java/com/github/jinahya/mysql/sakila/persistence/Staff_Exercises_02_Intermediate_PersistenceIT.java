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
 * Answers the exercises of {@code src/test/sql/exercises/staff_exercises_02_intermediate.sql}, against the
 * installed {@code sakila} database; a nested class per exercise, which answers it with JPQL and
 * with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Staff_Exercises_02_Intermediate_PersistenceIT {

    @DisplayName("SAKILA_STAFF_I_01: how many rentals each staff member handled")
    @Nested
    class SAKILA_STAFF_I_01_Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT s, COUNT(r)
                                                                                      FROM Rental r
                                                                                               JOIN r.staff s
                                                                                      GROUP BY s
                                                                                      ORDER BY s.staffId""",
                                                                              Object[].class)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Staff) r[0]).getStaffId(),
                                r -> ((Staff) r[0]).getFirstName(),
                                r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple(1, "Mike", 8040L),
                            tuple(2, "Jon", 8004L));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var r = cq.from(Rental.class);
                final var s = r.join(Rental_.staff);
                cq.select(cb.array(s, cb.count(r)))
                        .groupBy(s)
                        .orderBy(cb.asc(s.get(Staff_.staffId)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Staff) r[0]).getStaffId(),
                                r -> ((Staff) r[0]).getFirstName(),
                                r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple(1, "Mike", 8040L),
                            tuple(2, "Jon", 8004L));
        }
    }

    @DisplayName("SAKILA_STAFF_I_02: the staff members who manage the store they work at")
    @Nested
    class SAKILA_STAFF_I_02_Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT s, st
                                                                                      FROM Store st
                                                                                               JOIN st.managerStaff s
                                                                                      WHERE s.store = st
                                                                                      ORDER BY s.staffId""",
                                                                              Object[].class)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Staff) r[0]).getStaffId(),
                                r -> ((Staff) r[0]).getFirstName(),
                                r -> ((Staff) r[0]).getLastName(), r -> ((Store) r[1]).getStoreId())
                    .containsExactly(
                            tuple(1, "Mike", "Hillyer", 1),
                            tuple(2, "Jon", "Stephens", 2));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var st = cq.from(Store.class);
                final var s = st.join(Store_.managerStaff);
                cq.select(cb.array(s, st))
                        .where(cb.equal(s.get(Staff_.store), st))
                        .orderBy(cb.asc(s.get(Staff_.staffId)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Staff) r[0]).getStaffId(),
                                r -> ((Staff) r[0]).getFirstName(),
                                r -> ((Staff) r[0]).getLastName(), r -> ((Store) r[1]).getStoreId())
                    .containsExactly(
                            tuple(1, "Mike", "Hillyer", 1),
                            tuple(2, "Jon", "Stephens", 2));
        }
    }

    @DisplayName(
            "SAKILA_STAFF_I_03: how many rentals each staff member handled for their own store's copies, " +
            "and for the other's")
    @Nested
    class SAKILA_STAFF_I_03_Test {

        // JPQL sums no CASE; the rentals of the other store's copies are a second query
        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var otherStore = em.createQuery("""
                                                              SELECT s, COUNT(r)
                                                              FROM Rental r
                                                                       JOIN r.staff s
                                                                       JOIN r.inventory i
                                                              WHERE i.storeId <> s.store.storeId
                                                              GROUP BY s""", Object[].class)
                        .getResultStream()
                        .collect(Collectors.toMap(r -> ((Staff) r[0]).getStaffId(), r -> r[1]));
                return em.createQuery("""
                                              SELECT s, st, COUNT(r)
                                              FROM Rental r
                                                       JOIN r.staff s
                                                       JOIN s.store st
                                                       JOIN r.inventory i
                                              WHERE i.storeId = st.storeId
                                              GROUP BY s, st
                                              ORDER BY s.staffId""", Object[].class)
                        .getResultStream()
                        .map(r -> new Object[]{r[0], r[1], r[2],
                                               otherStore.get(((Staff) r[0]).getStaffId())})
                        .toList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Staff) r[0]).getStaffId(),
                                r -> ((Staff) r[0]).getFirstName(),
                                r -> ((Store) r[1]).getStoreId(), r -> ((Number) r[2]).longValue(),
                                r -> ((Number) r[3]).longValue())
                    .containsExactly(
                            tuple(1, "Mike", 1, 3991L, 4049L),
                            tuple(2, "Jon", 2, 4072L, 3932L));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var other = cb.createQuery(Object[].class);
                final var r1 = other.from(Rental.class);
                final var s1 = r1.join(Rental_.staff);
                final var i1 = r1.join(Rental_.inventory);
                other.select(cb.array(s1, cb.count(r1)))
                        .where(cb.notEqual(i1.get(Inventory_.storeId),
                                           s1.get(Staff_.store).get(Store_.storeId)))
                        .groupBy(s1);
                final var otherStore = em.createQuery(other).getResultStream()
                        .collect(Collectors.toMap(r -> ((Staff) r[0]).getStaffId(), r -> r[1]));
                final var cq = cb.createQuery(Object[].class);
                final var r = cq.from(Rental.class);
                final var s = r.join(Rental_.staff);
                final var i = r.join(Rental_.inventory);
                final var st = s.join(Staff_.store);
                cq.select(cb.array(s, st, cb.count(r)))
                        .where(cb.equal(i.get(Inventory_.storeId), st.get(Store_.storeId)))
                        .groupBy(s, st)
                        .orderBy(cb.asc(s.get(Staff_.staffId)));
                return em.createQuery(cq).getResultStream()
                        .map(e -> new Object[]{e[0], e[1], e[2],
                                               otherStore.get(((Staff) e[0]).getStaffId())})
                        .toList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Staff) r[0]).getStaffId(),
                                r -> ((Staff) r[0]).getFirstName(),
                                r -> ((Store) r[1]).getStoreId(), r -> ((Number) r[2]).longValue(),
                                r -> ((Number) r[3]).longValue())
                    .containsExactly(
                            tuple(1, "Mike", 1, 3991L, 4049L),
                            tuple(2, "Jon", 2, 4072L, 3932L));
        }
    }
}
