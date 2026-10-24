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

import jakarta.persistence.criteria.LocalDateTimeField;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers the exercises of {@code src/test/sql/payment_exercises_01_basic.sql}, against the
 * installed {@code sakila} database; a nested class per exercise, which answers it with JPQL and
 * with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Payment_Exercises_01_Basic_PersistenceIT {

    @DisplayName("SAKILA_PAYMENT_B_01: how much each staff member took")
    @Nested
    class SAKILA_PAYMENT_B_01_Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT s, COUNT(p), SUM(p.amount)
                                                                                      FROM Payment p
                                                                                               JOIN p.staff s
                                                                                      GROUP BY s
                                                                                      ORDER BY s.staffId""",
                                                                              Object[].class)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Staff) r[0]).getStaffId(), r -> ((Number) r[1]).longValue(),
                                r -> ((BigDecimal) r[2]).toPlainString())
                    .containsExactly(
                            tuple(1, 8054L, "33482.50"),
                            tuple(2, 7990L, "33924.06"));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var p = cq.from(Payment.class);
                final var s = p.join(Payment_.staff);
                cq.select(cb.array(s, cb.count(p), cb.sum(p.get(Payment_.amount))))
                        .groupBy(s)
                        .orderBy(cb.asc(s.get(Staff_.staffId)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Staff) r[0]).getStaffId(), r -> ((Number) r[1]).longValue(),
                                r -> ((BigDecimal) r[2]).toPlainString())
                    .containsExactly(
                            tuple(1, 8054L, "33482.50"),
                            tuple(2, 7990L, "33924.06"));
        }
    }

    @DisplayName("SAKILA_PAYMENT_B_02: how much was paid in each month")
    @Nested
    class SAKILA_PAYMENT_B_02_Test {

        // JPQL has no DATE_FORMAT; the year and the month are EXTRACTed, and written as one in Java
        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT EXTRACT(YEAR FROM p.paymentDate), EXTRACT(MONTH FROM p.paymentDate), COUNT(p), SUM(p.amount)
                                                                                      FROM Payment p
                                                                                      GROUP BY EXTRACT(YEAR FROM p.paymentDate), EXTRACT(MONTH FROM p.paymentDate)
                                                                                      ORDER BY EXTRACT(YEAR FROM p.paymentDate), EXTRACT(MONTH FROM p.paymentDate)""",
                                                                              Object[].class)
                    .getResultStream()
                    // year and month, as mysql's DATE_FORMAT(..., '%Y-%m') writes them
                    .map(row -> new Object[]{String.format("%d-%02d", row[0],
                                                           row[1]), row[2], row[3]})
                    .toList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).longValue(),
                                r -> ((BigDecimal) r[2]).toPlainString())
                    .containsExactly(
                            tuple("2005-05", 1156L, "4823.44"),
                            tuple("2005-06", 2311L, "9629.89"),
                            tuple("2005-07", 6709L, "28368.91"),
                            tuple("2005-08", 5686L, "24070.14"),
                            tuple("2006-02", 182L, "514.18"));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var p = cq.from(Payment.class);
                final var year = cb.extract(LocalDateTimeField.YEAR, p.get(Payment_.paymentDate));
                final var month = cb.extract(LocalDateTimeField.MONTH, p.get(Payment_.paymentDate));
                cq.select(cb.array(year, month, cb.count(p), cb.sum(p.get(Payment_.amount))))
                        .groupBy(year, month)
                        .orderBy(cb.asc(year), cb.asc(month));
                return em.createQuery(cq).getResultStream()
                        // year and month, as mysql's DATE_FORMAT(..., '%Y-%m') writes them
                        .map(row -> new Object[]{String.format("%d-%02d", row[0],
                                                               row[1]), row[2], row[3]})
                        .toList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).longValue(),
                                r -> ((BigDecimal) r[2]).toPlainString())
                    .containsExactly(
                            tuple("2005-05", 1156L, "4823.44"),
                            tuple("2005-06", 2311L, "9629.89"),
                            tuple("2005-07", 6709L, "28368.91"),
                            tuple("2005-08", 5686L, "24070.14"),
                            tuple("2006-02", 182L, "514.18"));
        }
    }

    @DisplayName("SAKILA_PAYMENT_B_03: how many payments have no rental")
    @Nested
    class SAKILA_PAYMENT_B_03_Test {

        @Test
        void __QueryLanguage() {
            final var value = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                       SELECT COUNT(p)
                                                                                       FROM Payment p
                                                                                       WHERE p.rental IS NULL""",
                                                                               Long.class)
                    .getSingleResult());
            log.debug("{}", value);
            assertThat(value).isEqualTo(0L);
        }

        @Test
        void __CriteriaApi() {
            final var value = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Long.class);
                final var p = cq.from(Payment.class);
                cq.select(cb.count(p))
                        .where(cb.isNull(p.get(Payment_.rental)));
                return em.createQuery(cq)
                        .getSingleResult();
            });
            log.debug("{}", value);
            assertThat(value).isEqualTo(0L);
        }
    }

    @DisplayName("SAKILA_PAYMENT_B_04: the five amounts paid most often")
    @Nested
    class SAKILA_PAYMENT_B_04_Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT p.amount, COUNT(p) AS payments
                                                                                      FROM Payment p
                                                                                      GROUP BY p.amount
                                                                                      ORDER BY payments DESC, p.amount""",
                                                                              Object[].class)
                    .setMaxResults(5)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((BigDecimal) r[0]).toPlainString(),
                                r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple("4.99", 3789L),
                            tuple("2.99", 3542L),
                            tuple("0.99", 2977L),
                            tuple("5.99", 1299L),
                            tuple("6.99", 1119L));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var p = cq.from(Payment.class);
                final var amount = p.get(Payment_.amount);
                final var payments = cb.count(p);
                cq.select(cb.array(amount, payments))
                        .groupBy(amount)
                        .orderBy(cb.desc(payments), cb.asc(amount));
                return em.createQuery(cq)
                        .setMaxResults(5)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((BigDecimal) r[0]).toPlainString(),
                                r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple("4.99", 3789L),
                            tuple("2.99", 3542L),
                            tuple("0.99", 2977L),
                            tuple("5.99", 1299L),
                            tuple("6.99", 1119L));
        }
    }

    @DisplayName("SAKILA_PAYMENT_B_05: the payments of the customer 1 in July 2005")
    @Nested
    class SAKILA_PAYMENT_B_05_Test {

        private static final LocalDateTime FROM = LocalDateTime.of(2005, 7, 1, 0, 0, 0);

        private static final LocalDateTime TO = LocalDateTime.of(2005, 7, 31, 23, 59, 59);

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT p
                                                                                      FROM Payment p
                                                                                      WHERE p.customer.customerId = :customerId
                                                                                        AND p.paymentDate BETWEEN :from AND :to
                                                                                      ORDER BY p.paymentDate, p.paymentId""",
                                                                              Payment.class)
                    .setParameter("customerId", 1)
                    .setParameter("from", FROM)
                    .setParameter("to", TO)
                    .getResultList());
            list.forEach(e -> log.debug("{}", e));
            assertThat(list).hasSize(12)
                    .extracting(Payment::getPaymentId, p -> p.getAmount().toPlainString())
                    .startsWith(
                            tuple(10, "5.99"),
                            tuple(11, "5.99"),
                            tuple(12, "4.99"))
                    .endsWith(
                            tuple(21, "2.99"));
            assertThat(list).extracting(Payment::getPaymentDate)
                    .isSorted()
                    .first().isEqualTo(LocalDateTime.of(2005, 7, 8, 3, 17, 5));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Payment.class);
                final var p = cq.from(Payment.class);
                final var paymentDate = p.get(Payment_.paymentDate);
                cq.select(p)
                        .where(cb.equal(p.get(Payment_.customer).get(Customer_.customerId),
                                        cb.parameter(Integer.class, "customerId")),
                               cb.between(paymentDate,
                                          cb.parameter(LocalDateTime.class, "from"),
                                          cb.parameter(LocalDateTime.class, "to")))
                        .orderBy(cb.asc(paymentDate), cb.asc(p.get(Payment_.paymentId)));
                return em.createQuery(cq)
                        .setParameter("customerId", 1)
                        .setParameter("from", FROM)
                        .setParameter("to", TO)
                        .getResultList();
            });
            list.forEach(e -> log.debug("{}", e));
            assertThat(list).hasSize(12)
                    .extracting(Payment::getPaymentId, p -> p.getAmount().toPlainString())
                    .startsWith(
                            tuple(10, "5.99"),
                            tuple(11, "5.99"),
                            tuple(12, "4.99"))
                    .endsWith(
                            tuple(21, "2.99"));
            assertThat(list).extracting(Payment::getPaymentDate)
                    .isSorted()
                    .first().isEqualTo(LocalDateTime.of(2005, 7, 8, 3, 17, 5));
        }
    }
}
