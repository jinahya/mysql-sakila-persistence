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
 * Answers the exercises of {@code src/test/sql/payment_exercises_02_intermediate.sql}, against the
 * installed {@code sakila} database; a nested class per exercise, which answers it with JPQL and
 * with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Payment_Exercises_02_Intermediate_PersistenceIT {

    @DisplayName("exercise 1: the largest payment, and how many payments are that large")
    @Nested
    class Exercise1Test {

        // JPQL has no subquery in the SELECT clause; the largest amount and its count are two
        // queries
        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> List.<Object[]>of(new Object[]{
                    em.createQuery("SELECT MAX(p.amount) FROM Payment p",
                                   BigDecimal.class).getSingleResult(),
                    em.createQuery("""
                                           SELECT COUNT(p)
                                           FROM Payment p
                                           WHERE p.amount = (SELECT MAX(p2.amount) FROM Payment p2)""",
                                   Long.class).getSingleResult()}));
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((BigDecimal) r[0]).toPlainString(),
                                r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple("11.99", 10L));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var largest = cb.createQuery(BigDecimal.class);
                final var p = largest.from(Payment.class);
                largest.select(cb.max(p.get(Payment_.amount)));
                final var count = cb.createQuery(Long.class);
                final var p1 = count.from(Payment.class);
                final var max = count.subquery(BigDecimal.class);
                final var p2 = max.from(Payment.class);
                max.select(cb.max(p2.get(Payment_.amount)));
                count.select(cb.count(p1)).where(cb.equal(p1.get(Payment_.amount), max));
                return List.<Object[]>of(new Object[]{
                        em.createQuery(largest).getSingleResult(), em.createQuery(
                        count).getSingleResult()});
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((BigDecimal) r[0]).toPlainString(),
                                r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple("11.99", 10L));
        }
    }

    @DisplayName("exercise 2: the customers who paid more than 180 in all")
    @Nested
    class Exercise2Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT c, SUM(p.amount) AS total
                                                                                      FROM Payment p
                                                                                               JOIN p.customer c
                                                                                      GROUP BY c
                                                                                      HAVING SUM(p.amount) > :total
                                                                                      ORDER BY total DESC, c.customerId""",
                                                                              Object[].class)
                    .setParameter("total", BigDecimal.valueOf(180))
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Customer) r[0]).getCustomerId(),
                                r -> ((Customer) r[0]).getFirstName(),
                                r -> ((Customer) r[0]).getLastName(),
                                r -> ((BigDecimal) r[1]).toPlainString())
                    .containsExactly(
                            tuple(526, "KARL", "SEAL", "221.55"),
                            tuple(148, "ELEANOR", "HUNT", "216.54"),
                            tuple(144, "CLARA", "SHAW", "195.58"),
                            tuple(137, "RHONDA", "KENNEDY", "194.61"),
                            tuple(178, "MARION", "SNYDER", "194.61"),
                            tuple(459, "TOMMY", "COLLAZO", "186.62"));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var p = cq.from(Payment.class);
                final var c = p.join(Payment_.customer);
                final var total = cb.sum(p.get(Payment_.amount));
                cq.select(cb.array(c, total))
                        .groupBy(c)
                        .having(cb.gt(total, cb.parameter(BigDecimal.class, "total")))
                        .orderBy(cb.desc(total), cb.asc(c.get(Customer_.customerId)));
                return em.createQuery(cq)
                        .setParameter("total", BigDecimal.valueOf(180))
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Customer) r[0]).getCustomerId(),
                                r -> ((Customer) r[0]).getFirstName(),
                                r -> ((Customer) r[0]).getLastName(),
                                r -> ((BigDecimal) r[1]).toPlainString())
                    .containsExactly(
                            tuple(526, "KARL", "SEAL", "221.55"),
                            tuple(148, "ELEANOR", "HUNT", "216.54"),
                            tuple(144, "CLARA", "SHAW", "195.58"),
                            tuple(137, "RHONDA", "KENNEDY", "194.61"),
                            tuple(178, "MARION", "SNYDER", "194.61"),
                            tuple(459, "TOMMY", "COLLAZO", "186.62"));
        }
    }

    @DisplayName(
            "exercise 3: how many payments were less than, equal to, or more than the film's " +
            "rental rate")
    @Nested
    class Exercise3Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT CASE
                                                                                                 WHEN p.amount < f.rentalRate THEN 'less'
                                                                                                 WHEN p.amount = f.rentalRate THEN 'the rate'
                                                                                                 ELSE 'more' END AS paid,
                                                                                             COUNT(p)
                                                                                      FROM Payment p
                                                                                               JOIN p.rental r
                                                                                               JOIN r.inventory i
                                                                                               JOIN i.film f
                                                                                      GROUP BY CASE
                                                                                                   WHEN p.amount < f.rentalRate THEN 'less'
                                                                                                   WHEN p.amount = f.rentalRate THEN 'the rate'
                                                                                                   ELSE 'more' END
                                                                                      ORDER BY paid""",
                                                                              Object[].class)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple("less", 24L),
                            tuple("more", 7293L),
                            tuple("the rate", 8727L));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var p = cq.from(Payment.class);
                final var f = p.join(Payment_.rental).join(Rental_.inventory).join(Inventory_.film);
                final var amount = p.get(Payment_.amount);
                final var rentalRate = f.get(Film_.rentalRate);
                final var paid = cb.<String>selectCase()
                        .when(cb.lt(amount, rentalRate), "less")
                        .when(cb.equal(amount, rentalRate), "the rate")
                        .otherwise("more");
                cq.select(cb.array(paid, cb.count(p)))
                        .groupBy(paid)
                        .orderBy(cb.asc(paid));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple("less", 24L),
                            tuple("more", 7293L),
                            tuple("the rate", 8727L));
        }
    }
}
