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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers the exercises of {@code src/test/sql/payment_exercises_03_advanced.sql}, against the
 * installed {@code sakila} database; a nested class per exercise, which answers it with JPQL and
 * with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Payment_Exercises_03_Advanced_PersistenceIT {

    @DisplayName("exercise 1: the running total of the customer 1's payments, the first ten")
    @Nested
    class Exercise1Test {

        // JPQL has no window function; the running total sums, by a self-join, every payment up to
        // each one
        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT p.paymentId, p.paymentDate, p.amount, SUM(q.amount)
                                                                                      FROM Payment p, Payment q
                                                                                      WHERE p.customer.customerId = :customerId
                                                                                        AND q.customer = p.customer
                                                                                        AND (q.paymentDate < p.paymentDate
                                                                                             OR (q.paymentDate = p.paymentDate AND q.paymentId <= p.paymentId))
                                                                                      GROUP BY p.paymentId, p.paymentDate, p.amount
                                                                                      ORDER BY p.paymentDate, p.paymentId""",
                                                                              Object[].class)
                    .setParameter("customerId", 1)
                    .setMaxResults(10)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(),
                                r -> ((BigDecimal) r[2]).toPlainString(),
                                r -> ((BigDecimal) r[3]).toPlainString())
                    .containsExactly(
                            tuple(1, "2.99", "2.99"),
                            tuple(2, "0.99", "3.98"),
                            tuple(3, "5.99", "9.97"),
                            tuple(4, "0.99", "10.96"),
                            tuple(5, "9.99", "20.95"),
                            tuple(6, "4.99", "25.94"),
                            tuple(7, "4.99", "30.93"),
                            tuple(8, "0.99", "31.92"),
                            tuple(9, "3.99", "35.91"),
                            tuple(10, "5.99", "41.90"));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var p = cq.from(Payment.class);
                final var q = cq.from(Payment.class);
                final var paymentId = p.get(Payment_.paymentId);
                final var paymentDate = p.get(Payment_.paymentDate);
                final var amount = p.get(Payment_.amount);
                cq.select(cb.array(paymentId, paymentDate, amount, cb.sum(q.get(Payment_.amount))))
                        .where(cb.equal(p.get(Payment_.customer).get(Customer_.customerId),
                                        cb.parameter(Integer.class, "customerId")),
                               cb.equal(q.get(Payment_.customer), p.get(Payment_.customer)),
                               cb.or(cb.lessThan(q.get(Payment_.paymentDate), paymentDate),
                                     cb.and(cb.equal(q.get(Payment_.paymentDate), paymentDate),
                                            cb.le(q.get(Payment_.paymentId), paymentId))))
                        .groupBy(paymentId, paymentDate, amount)
                        .orderBy(cb.asc(paymentDate), cb.asc(paymentId));
                return em.createQuery(cq)
                        .setParameter("customerId", 1)
                        .setMaxResults(10)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(),
                                r -> ((BigDecimal) r[2]).toPlainString(),
                                r -> ((BigDecimal) r[3]).toPlainString())
                    .containsExactly(
                            tuple(1, "2.99", "2.99"),
                            tuple(2, "0.99", "3.98"),
                            tuple(3, "5.99", "9.97"),
                            tuple(4, "0.99", "10.96"),
                            tuple(5, "9.99", "20.95"),
                            tuple(6, "4.99", "25.94"),
                            tuple(7, "4.99", "30.93"),
                            tuple(8, "0.99", "31.92"),
                            tuple(9, "3.99", "35.91"),
                            tuple(10, "5.99", "41.90"));
        }
    }

    @DisplayName("exercise 2: the runs of consecutive days on which payments were made")
    @Nested
    class Exercise2Test {

        // JPQL has no window function, and cannot subtract a number of days from a date; the runs
        // are numbered with ROW_NUMBER, and DATE_SUB is MySQL's own
        @Test
        void nativeQuery() {
            final var list = __Exercises_TestUtils.apply(em -> (List<?>) em.createNativeQuery("""
                                                                                                      WITH days AS (SELECT DISTINCT DATE(payment_date) AS day FROM payment),
                                                                                                           islands AS (SELECT day, DATE_SUB(day, INTERVAL ROW_NUMBER() OVER (ORDER BY day) DAY) AS island FROM days)
                                                                                                      SELECT MIN(day) AS first_day, MAX(day) AS last_day, COUNT(*) AS days
                                                                                                      FROM islands
                                                                                                      GROUP BY island
                                                                                                      ORDER BY first_day""")
                    .getResultList());
            list.forEach(r -> log.debug("{}", r instanceof Object[] a ? Arrays.toString(a) : r));
            assertThat(list)
                    .extracting(r -> String.valueOf(((Object[]) r)[0]),
                                r -> String.valueOf(((Object[]) r)[1]),
                                r -> ((Number) ((Object[]) r)[2]).longValue())
                    .containsExactly(
                            tuple("2005-05-24", "2005-05-31", 8L),
                            tuple("2005-06-14", "2005-06-21", 8L),
                            tuple("2005-07-05", "2005-07-12", 8L),
                            tuple("2005-07-26", "2005-08-02", 8L),
                            tuple("2005-08-16", "2005-08-23", 8L),
                            tuple("2006-02-14", "2006-02-14", 1L));
        }
    }
}
