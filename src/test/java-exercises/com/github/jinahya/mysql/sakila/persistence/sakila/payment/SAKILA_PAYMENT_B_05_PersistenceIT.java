package com.github.jinahya.mysql.sakila.persistence.sakila.payment;

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

import com.github.jinahya.mysql.sakila.persistence.Customer_;
import com.github.jinahya.mysql.sakila.persistence.Payment;
import com.github.jinahya.mysql.sakila.persistence.Payment_;
import com.github.jinahya.mysql.sakila.persistence.__Exercises_TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code SAKILA_PAYMENT_B_05} of {@code src/test/sql-exercises/payment/01_basic.sql} with
 * JPQL and with the Criteria API, or with a native query where neither can.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("SAKILA_PAYMENT_B_05: the payments of the customer 1 in July 2005")
@Slf4j
class SAKILA_PAYMENT_B_05_PersistenceIT {

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
