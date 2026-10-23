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
 * Answers the exercises of {@code src/test/sql/staff_exercises_01_basic.sql}, against the installed
 * {@code sakila} database; a nested class per exercise, which answers it with JPQL and with the
 * Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Staff_Exercises_01_Basic_PersistenceIT {

    @DisplayName("exercise 1: every staff member, with their store")
    @Nested
    class Exercise1Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT s, st
                                                                                      FROM Staff s
                                                                                               JOIN s.store st
                                                                                      ORDER BY s.staffId""",
                                                                              Object[].class)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Staff) r[0]).getStaffId(),
                                r -> ((Staff) r[0]).getFirstName(),
                                r -> ((Staff) r[0]).getLastName(),
                                r -> ((Store) r[1]).getStoreId(),
                                r -> ((Staff) r[0]).getActive())
                    .containsExactly(
                            tuple(1, "Mike", "Hillyer", 1, true),
                            tuple(2, "Jon", "Stephens", 2, true));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var s = cq.from(Staff.class);
                final var st = s.join(Staff_.store);
                cq.select(cb.array(s, st))
                        .orderBy(cb.asc(s.get(Staff_.staffId)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Staff) r[0]).getStaffId(),
                                r -> ((Staff) r[0]).getFirstName(),
                                r -> ((Staff) r[0]).getLastName(),
                                r -> ((Store) r[1]).getStoreId(),
                                r -> ((Staff) r[0]).getActive())
                    .containsExactly(
                            tuple(1, "Mike", "Hillyer", 1, true),
                            tuple(2, "Jon", "Stephens", 2, true));
        }
    }

    @DisplayName("exercise 2: the staff members who have no password")
    @Nested
    class Exercise2Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT s
                                                                                      FROM Staff s
                                                                                      WHERE s.password IS NULL
                                                                                      ORDER BY s.staffId""",
                                                                              Staff.class)
                    .getResultList());
            list.forEach(e -> log.debug("{}", e));
            assertThat(list)
                    .extracting(Staff::getStaffId, Staff::getFirstName, Staff::getLastName,
                                Staff::getUsername)
                    .containsExactly(
                            tuple(2, "Jon", "Stephens", "Jon"));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Staff.class);
                final var s = cq.from(Staff.class);
                cq.select(s)
                        .where(cb.isNull(s.get(Staff_.password)))
                        .orderBy(cb.asc(s.get(Staff_.staffId)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(e -> log.debug("{}", e));
            assertThat(list)
                    .extracting(Staff::getStaffId, Staff::getFirstName, Staff::getLastName,
                                Staff::getUsername)
                    .containsExactly(
                            tuple(2, "Jon", "Stephens", "Jon"));
        }
    }
}
