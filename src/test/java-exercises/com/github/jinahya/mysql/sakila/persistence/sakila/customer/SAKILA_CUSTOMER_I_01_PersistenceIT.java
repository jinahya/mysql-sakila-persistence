package com.github.jinahya.mysql.sakila.persistence.sakila.customer;

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

import com.github.jinahya.mysql.sakila.persistence.Customer;
import com.github.jinahya.mysql.sakila.persistence.Customer_;
import com.github.jinahya.mysql.sakila.persistence.Payment_;
import com.github.jinahya.mysql.sakila.persistence.__Exercises_TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code SAKILA_CUSTOMER_I_01} of
 * {@code src/test/sql-exercises/customer/02_intermediate.sql} with JPQL and with the Criteria API,
 * or with a native query where neither can.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("SAKILA_CUSTOMER_I_01: the five customers who paid the most")
@Slf4j
class SAKILA_CUSTOMER_I_01_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                  SELECT c, SUM(p.amount)
                                                                                  FROM Customer c
                                                                                           JOIN c.payments p
                                                                                  GROUP BY c
                                                                                  ORDER BY SUM(p.amount) DESC, c.customerId""",
                                                                          Object[].class)
                .setMaxResults(5)
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
                        tuple(178, "MARION", "SNYDER", "194.61"));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Object[].class);
            final var c = cq.from(Customer.class);
            final var p = c.join(Customer_.payments);
            cq.select(cb.array(c, cb.sum(p.get(Payment_.amount))))
                    .groupBy(c)
                    .orderBy(cb.desc(cb.sum(p.get(Payment_.amount))),
                             cb.asc(c.get(Customer_.customerId)));
            return em.createQuery(cq)
                    .setMaxResults(5)
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
                        tuple(178, "MARION", "SNYDER", "194.61"));
    }

}
