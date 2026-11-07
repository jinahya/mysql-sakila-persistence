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
import com.github.jinahya.mysql.sakila.persistence.__Exercises_TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code SAKILA_CUSTOMER_I_03} of
 * {@code src/test/sql-exercises/customer/02_intermediate.sql} with JPQL and with the Criteria API,
 * or with a native query where neither can.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("SAKILA_CUSTOMER_I_03: the customers who rented forty times or more")
@Slf4j
class SAKILA_CUSTOMER_I_03_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                  SELECT c, COUNT(r)
                                                                                  FROM Customer c
                                                                                           JOIN c.rentals r
                                                                                  GROUP BY c
                                                                                  HAVING COUNT(r) >= 40
                                                                                  ORDER BY COUNT(r) DESC, c.customerId""",
                                                                          Object[].class)
                .getResultList());
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> ((Customer) r[0]).getCustomerId(),
                            r -> ((Customer) r[0]).getFirstName(),
                            r -> ((Customer) r[0]).getLastName(),
                            r -> ((Number) r[1]).longValue())
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
            cq.select(cb.array(c, cb.count(r)))
                    .groupBy(c)
                    .having(cb.ge(cb.count(r), 40L))
                    .orderBy(cb.desc(cb.count(r)), cb.asc(c.get(Customer_.customerId)));
            return em.createQuery(cq)
                    .getResultList();
        });
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> ((Customer) r[0]).getCustomerId(),
                            r -> ((Customer) r[0]).getFirstName(),
                            r -> ((Customer) r[0]).getLastName(),
                            r -> ((Number) r[1]).longValue())
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
