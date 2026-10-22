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
 * Answers the exercises of {@code src/test/sql/staff_exercises_02_intermediate.sql}, against the
 * installed {@code sakila} database; a nested class per exercise, which answers it with JPQL and
 * with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Staff_Exercises_02_Intermediate_PersistenceIT {

    @DisplayName("exercise 1: how many rentals each staff member handled")
    @Nested
    class Exercise1Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                    SELECT s.staffId, s.firstName, COUNT(r)
                    FROM Rental r
                             JOIN r.staff s
                    GROUP BY s.staffId, s.firstName
                    ORDER BY s.staffId""", Object[].class)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> r[1], r -> ((Number) r[2]).longValue())
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
                cq.select(cb.array(s.get(Staff_.staffId), s.get(Staff_.firstName), cb.count(r)))
                        .groupBy(s.get(Staff_.staffId), s.get(Staff_.firstName))
                        .orderBy(cb.asc(s.get(Staff_.staffId)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> r[1], r -> ((Number) r[2]).longValue())
                    .containsExactly(
                            tuple(1, "Mike", 8040L),
                            tuple(2, "Jon", 8004L));
        }
    }

    @DisplayName("exercise 2: the staff members who manage the store they work at")
    @Nested
    class Exercise2Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                    SELECT s.staffId, s.firstName, s.lastName, st.storeId
                    FROM Store st
                             JOIN st.managerStaff s
                    WHERE s.store = st
                    ORDER BY s.staffId""", Object[].class)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> r[1], r -> r[2], r -> ((Number) r[3]).intValue())
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
                cq.select(cb.array(s.get(Staff_.staffId), s.get(Staff_.firstName), s.get(Staff_.lastName), st.get(Store_.storeId)))
                        .where(cb.equal(s.get(Staff_.store), st))
                        .orderBy(cb.asc(s.get(Staff_.staffId)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> r[1], r -> r[2], r -> ((Number) r[3]).intValue())
                    .containsExactly(
                            tuple(1, "Mike", "Hillyer", 1),
                            tuple(2, "Jon", "Stephens", 2));
        }
    }
}
