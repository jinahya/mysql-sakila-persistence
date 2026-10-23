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

import java.math.BigDecimal;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers the exercises of {@code src/test/sql/customer_exercises_02_intermediate.sql}, against the
 * installed {@code sakila} database; a nested class per exercise, which answers it with JPQL and
 * with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Customer_Exercises_02_Intermediate_PersistenceIT {

    @DisplayName("exercise 1: the five customers who paid the most")
    @Nested
    class Exercise1Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT c.customerId, c.firstName, c.lastName, SUM(p.amount)
                                                                                      FROM Customer c
                                                                                               JOIN c.payments p
                                                                                      GROUP BY c.customerId, c.firstName, c.lastName
                                                                                      ORDER BY SUM(p.amount) DESC, c.customerId""",
                                                                              Object[].class)
                    .setMaxResults(5)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> r[1], r -> r[2],
                                r -> ((BigDecimal) r[3]).toPlainString())
                    .containsExactly(
                            tuple(526, "KARL", "SEAL", "221.55"),
                            tuple(148, "ELEANOR", "HUNT", "216.54"),
                            tuple(144, "CLARA", "SHAW", "195.58"),
                            tuple(137, "RHONDA", "KENNEDY", "194.61"),
                            tuple(178, "MARION", "SNYDER", "194.61"));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var c = cq.from(Customer.class);
                final var p = c.join(Customer_.payments);
                cq.select(cb.array(c.get(Customer_.customerId), c.get(Customer_.firstName),
                                   c.get(Customer_.lastName),
                                   cb.sum(p.get(Payment_.amount))))
                        .groupBy(c.get(Customer_.customerId), c.get(Customer_.firstName),
                                 c.get(Customer_.lastName))
                        .orderBy(cb.desc(cb.sum(p.get(Payment_.amount))),
                                 cb.asc(c.get(Customer_.customerId)));
                return em.createQuery(cq)
                        .setMaxResults(5)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> r[1], r -> r[2],
                                r -> ((BigDecimal) r[3]).toPlainString())
                    .containsExactly(
                            tuple(526, "KARL", "SEAL", "221.55"),
                            tuple(148, "ELEANOR", "HUNT", "216.54"),
                            tuple(144, "CLARA", "SHAW", "195.58"),
                            tuple(137, "RHONDA", "KENNEDY", "194.61"),
                            tuple(178, "MARION", "SNYDER", "194.61"));
        }
    }

    @DisplayName("exercise 2: the customers who never rented")
    @Nested
    class Exercise2Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT c
                                                                                      FROM Customer c
                                                                                      WHERE c.rentals IS EMPTY""",
                                                                              Customer.class)
                    .getResultList());
            list.forEach(e -> log.debug("{}", e));
            assertThat(list).isEmpty();
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Customer.class);
                final var c = cq.from(Customer.class);
                cq.select(c)
                        .where(cb.isEmpty(c.get(Customer_.rentals)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(e -> log.debug("{}", e));
            assertThat(list).isEmpty();
        }
    }

    @DisplayName("exercise 3: the customers who rented forty times or more")
    @Nested
    class Exercise3Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT c.customerId, c.firstName, c.lastName, COUNT(r)
                                                                                      FROM Customer c
                                                                                               JOIN c.rentals r
                                                                                      GROUP BY c.customerId, c.firstName, c.lastName
                                                                                      HAVING COUNT(r) >= 40
                                                                                      ORDER BY COUNT(r) DESC, c.customerId""",
                                                                              Object[].class)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> r[1], r -> r[2],
                                r -> ((Number) r[3]).longValue())
                    .containsExactly(
                            tuple(148, "ELEANOR", "HUNT", 46L),
                            tuple(526, "KARL", "SEAL", 45L),
                            tuple(144, "CLARA", "SHAW", 42L),
                            tuple(236, "MARCIA", "DEAN", 42L),
                            tuple(75, "TAMMY", "SANDERS", 41L),
                            tuple(197, "SUE", "PETERS", 40L),
                            tuple(469, "WESLEY", "BULL", 40L));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var c = cq.from(Customer.class);
                final var r = c.join(Customer_.rentals);
                cq.select(cb.array(c.get(Customer_.customerId), c.get(Customer_.firstName),
                                   c.get(Customer_.lastName), cb.count(r)))
                        .groupBy(c.get(Customer_.customerId), c.get(Customer_.firstName),
                                 c.get(Customer_.lastName))
                        .having(cb.ge(cb.count(r), 40L))
                        .orderBy(cb.desc(cb.count(r)), cb.asc(c.get(Customer_.customerId)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> r[1], r -> r[2],
                                r -> ((Number) r[3]).longValue())
                    .containsExactly(
                            tuple(148, "ELEANOR", "HUNT", 46L),
                            tuple(526, "KARL", "SEAL", 45L),
                            tuple(144, "CLARA", "SHAW", 42L),
                            tuple(236, "MARCIA", "DEAN", 42L),
                            tuple(75, "TAMMY", "SANDERS", 41L),
                            tuple(197, "SUE", "PETERS", 40L),
                            tuple(469, "WESLEY", "BULL", 40L));
        }
    }

    @DisplayName("exercise 4: the five customers holding the most rentals not returned yet")
    @Nested
    class Exercise4Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT c.customerId, c.firstName, c.lastName, COUNT(r)
                                                                                      FROM Customer c
                                                                                               JOIN c.rentals r
                                                                                      WHERE r.returnDate IS NULL
                                                                                      GROUP BY c.customerId, c.firstName, c.lastName
                                                                                      ORDER BY COUNT(r) DESC, c.customerId""",
                                                                              Object[].class)
                    .setMaxResults(5)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> r[1], r -> r[2],
                                r -> ((Number) r[3]).longValue())
                    .containsExactly(
                            tuple(75, "TAMMY", "SANDERS", 3L),
                            tuple(15, "HELEN", "HARRIS", 2L),
                            tuple(42, "CAROLYN", "PEREZ", 2L),
                            tuple(43, "CHRISTINE", "ROBERTS", 2L),
                            tuple(53, "HEATHER", "MORRIS", 2L));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var c = cq.from(Customer.class);
                final var r = c.join(Customer_.rentals);
                cq.select(cb.array(c.get(Customer_.customerId), c.get(Customer_.firstName),
                                   c.get(Customer_.lastName), cb.count(r)))
                        .where(cb.isNull(r.get(Rental_.returnDate)))
                        .groupBy(c.get(Customer_.customerId), c.get(Customer_.firstName),
                                 c.get(Customer_.lastName))
                        .orderBy(cb.desc(cb.count(r)), cb.asc(c.get(Customer_.customerId)));
                return em.createQuery(cq)
                        .setMaxResults(5)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> r[1], r -> r[2],
                                r -> ((Number) r[3]).longValue())
                    .containsExactly(
                            tuple(75, "TAMMY", "SANDERS", 3L),
                            tuple(15, "HELEN", "HARRIS", 2L),
                            tuple(42, "CAROLYN", "PEREZ", 2L),
                            tuple(43, "CHRISTINE", "ROBERTS", 2L),
                            tuple(53, "HEATHER", "MORRIS", 2L));
        }
    }
}
