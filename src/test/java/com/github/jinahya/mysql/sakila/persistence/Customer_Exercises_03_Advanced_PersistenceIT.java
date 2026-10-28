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
 * Answers the exercises of {@code src/test/sql/exercises/customer_exercises_03_advanced.sql}, against the
 * installed {@code sakila} database; a nested class per exercise, which answers it with JPQL and
 * with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Customer_Exercises_03_Advanced_PersistenceIT {

    @DisplayName("SAKILA_CUSTOMER_A_01: the three customers who paid the most at each store")
    @Nested
    class SAKILA_CUSTOMER_A_01_Test {

        // JPQL has no window functions, nor a subquery in the FROM clause; ROW_NUMBER() OVER
        // (PARTITION BY ...) is SQL's
        @Test
        void nativeQuery() {
            final var list = __Exercises_TestUtils.apply(em -> (List<?>) em.createNativeQuery("""
                                                                                                      SELECT store_id, customer_id, first_name, last_name, paid
                                                                                                      FROM (SELECT c.store_id, c.customer_id, c.first_name, c.last_name, SUM(p.amount) AS paid,
                                                                                                                   ROW_NUMBER() OVER (PARTITION BY c.store_id ORDER BY SUM(p.amount) DESC, c.customer_id) AS n
                                                                                                            FROM customer c
                                                                                                                     JOIN payment p ON p.customer_id = c.customer_id
                                                                                                            GROUP BY c.store_id, c.customer_id, c.first_name, c.last_name) t
                                                                                                      WHERE n <= 3
                                                                                                      ORDER BY store_id, n""")
                    .getResultList());
            list.forEach(r -> log.debug("{}", r instanceof Object[] a ? Arrays.toString(a) : r));
            assertThat(list)
                    .extracting(r -> ((Number) ((Object[]) r)[0]).intValue(),
                                r -> ((Number) ((Object[]) r)[1]).intValue(),
                                r -> ((BigDecimal) ((Object[]) r)[4]).toPlainString())
                    .containsExactly(
                            tuple(1, 148, "216.54"),
                            tuple(1, 144, "195.58"),
                            tuple(1, 459, "186.62"),
                            tuple(2, 526, "221.55"),
                            tuple(2, 137, "194.61"),
                            tuple(2, 178, "194.61"));
        }
    }

    @DisplayName(
            "SAKILA_CUSTOMER_A_02: how many customers of each store paid more than that store's average " +
            "customer")
    @Nested
    class SAKILA_CUSTOMER_A_02_Test {

        // JPQL has no common table expression; the totals are correlated subqueries instead
        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT s,
                                                                                             COUNT(c),
                                                                                             ROUND((SELECT SUM(p3.amount) FROM Payment p3 WHERE p3.customer.store = s)
                                                                                                   / (SELECT COUNT(c3) FROM Customer c3 WHERE c3.store = s AND c3.payments IS NOT EMPTY), 2)
                                                                                      FROM Customer c
                                                                                               JOIN c.store s
                                                                                      WHERE (SELECT SUM(p.amount) FROM Payment p WHERE p.customer = c)
                                                                                            > (SELECT SUM(p2.amount) FROM Payment p2 WHERE p2.customer.store = s)
                                                                                              / (SELECT COUNT(c2) FROM Customer c2 WHERE c2.store = s AND c2.payments IS NOT EMPTY)
                                                                                      GROUP BY s
                                                                                      ORDER BY s.storeId""",
                                                                              Object[].class)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Store) r[0]).getStoreId(), r -> ((Number) r[1]).longValue(),
                                r -> ((Number) r[2]).doubleValue())
                    .containsExactly(
                            tuple(1, 154L, 113.49),
                            tuple(2, 126L, 111.39));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var c = cq.from(Customer.class);
                final var s = c.join(Customer_.store);
                final var paid = cq.subquery(BigDecimal.class);
                final var p = paid.from(Payment.class);
                paid.select(cb.sum(p.get(Payment_.amount)))
                        .where(cb.equal(p.get(Payment_.customer), c));
                final var storePaid1 = cq.subquery(BigDecimal.class);
                final var p2 = storePaid1.from(Payment.class);
                storePaid1.select(cb.sum(p2.get(Payment_.amount)))
                        .where(cb.equal(p2.get(Payment_.customer).get(Customer_.store), s));
                final var storeCustomers1 = cq.subquery(Long.class);
                final var c2 = storeCustomers1.from(Customer.class);
                storeCustomers1.select(cb.count(c2))
                        .where(cb.equal(c2.get(Customer_.store), s),
                               cb.isNotEmpty(c2.get(Customer_.payments)));
                final var storePaid2 = cq.subquery(BigDecimal.class);
                final var p3 = storePaid2.from(Payment.class);
                storePaid2.select(cb.sum(p3.get(Payment_.amount)))
                        .where(cb.equal(p3.get(Payment_.customer).get(Customer_.store), s));
                final var storeCustomers2 = cq.subquery(Long.class);
                final var c3 = storeCustomers2.from(Customer.class);
                storeCustomers2.select(cb.count(c3))
                        .where(cb.equal(c3.get(Customer_.store), s),
                               cb.isNotEmpty(c3.get(Customer_.payments)));
                cq.select(cb.array(s, cb.count(c),
                                   cb.round(cb.quot(storePaid2, storeCustomers2), 2)))
                        .where(cb.gt(paid, cb.quot(storePaid1, storeCustomers1)))
                        .groupBy(s)
                        .orderBy(cb.asc(s.get(Store_.storeId)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Store) r[0]).getStoreId(), r -> ((Number) r[1]).longValue(),
                                r -> ((Number) r[2]).doubleValue())
                    .containsExactly(
                            tuple(1, 154L, 113.49),
                            tuple(2, 126L, 111.39));
        }
    }
}
