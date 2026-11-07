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
import com.github.jinahya.mysql.sakila.persistence.Store;
import com.github.jinahya.mysql.sakila.persistence.__Exercises_TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code SAKILA_CUSTOMER_B_04} of {@code src/test/sql-exercises/customer/01_basic.sql} with
 * JPQL and with the Criteria API, or with a native query where neither can.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("SAKILA_CUSTOMER_B_04: the customers who are not active")
@Slf4j
class SAKILA_CUSTOMER_B_04_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                  SELECT c, s
                                                                                  FROM Customer c
                                                                                           JOIN c.store s
                                                                                  WHERE c.active = FALSE
                                                                                  ORDER BY c.customerId""",
                                                                          Object[].class)
                .getResultList());
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> ((Customer) r[0]).getCustomerId(),
                            r -> ((Customer) r[0]).getFirstName(),
                            r -> ((Customer) r[0]).getLastName(),
                            r -> ((Store) r[1]).getStoreId())
                .containsExactly(
                        tuple(16, "SANDRA", "MARTIN", 2),
                        tuple(64, "JUDITH", "COX", 2),
                        tuple(124, "SHEILA", "WELLS", 1),
                        tuple(169, "ERICA", "MATTHEWS", 2),
                        tuple(241, "HEIDI", "LARSON", 2),
                        tuple(271, "PENNY", "NEAL", 1),
                        tuple(315, "KENNETH", "GOODEN", 2),
                        tuple(368, "HARRY", "ARCE", 1),
                        tuple(406, "NATHAN", "RUNYON", 1),
                        tuple(446, "THEODORE", "CULP", 2),
                        tuple(482, "MAURICE", "CRAWLEY", 1),
                        tuple(510, "BEN", "EASTER", 2),
                        tuple(534, "CHRISTIAN", "JUNG", 1),
                        tuple(558, "JIMMIE", "EGGLESTON", 1),
                        tuple(592, "TERRANCE", "ROUSH", 1));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Object[].class);
            final var c = cq.from(Customer.class);
            final var s = c.join(Customer_.store);
            cq.select(cb.array(c, s))
                    .where(cb.isFalse(c.get(Customer_.active)))
                    .orderBy(cb.asc(c.get(Customer_.customerId)));
            return em.createQuery(cq)
                    .getResultList();
        });
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> ((Customer) r[0]).getCustomerId(),
                            r -> ((Customer) r[0]).getFirstName(),
                            r -> ((Customer) r[0]).getLastName(),
                            r -> ((Store) r[1]).getStoreId())
                .containsExactly(
                        tuple(16, "SANDRA", "MARTIN", 2),
                        tuple(64, "JUDITH", "COX", 2),
                        tuple(124, "SHEILA", "WELLS", 1),
                        tuple(169, "ERICA", "MATTHEWS", 2),
                        tuple(241, "HEIDI", "LARSON", 2),
                        tuple(271, "PENNY", "NEAL", 1),
                        tuple(315, "KENNETH", "GOODEN", 2),
                        tuple(368, "HARRY", "ARCE", 1),
                        tuple(406, "NATHAN", "RUNYON", 1),
                        tuple(446, "THEODORE", "CULP", 2),
                        tuple(482, "MAURICE", "CRAWLEY", 1),
                        tuple(510, "BEN", "EASTER", 2),
                        tuple(534, "CHRISTIAN", "JUNG", 1),
                        tuple(558, "JIMMIE", "EGGLESTON", 1),
                        tuple(592, "TERRANCE", "ROUSH", 1));
    }

}
