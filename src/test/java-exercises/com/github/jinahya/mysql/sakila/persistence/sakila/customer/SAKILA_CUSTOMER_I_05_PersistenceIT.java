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
import com.github.jinahya.mysql.sakila.persistence.Rental_;
import com.github.jinahya.mysql.sakila.persistence.Store_;
import com.github.jinahya.mysql.sakila.persistence.__Exercises_TestUtils;
import jakarta.persistence.criteria.JoinType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code SAKILA_CUSTOMER_I_05} of
 * {@code src/test/sql-exercises/customer/02_intermediate.sql} with JPQL and with the Criteria API,
 * or with a native query where neither can.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName(
        "SAKILA_CUSTOMER_I_05: the first ten customers of the store 1, each with the rentals " +
        "they have out, zero included")
@Slf4j
class SAKILA_CUSTOMER_I_05_PersistenceIT {

    // the condition on the rental is in the ON of the outer join; in WHERE it would drop the
    // customers with nothing out
    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(
                em -> em.createQuery(
                                """
                                        SELECT c.customerId, c.firstName, c.lastName, COUNT(r)
                                        FROM Customer c
                                                 LEFT JOIN c.rentals r ON r.returnDate IS NULL
                                        WHERE c.store.storeId = :storeId
                                        GROUP BY c.customerId, c.firstName, c.lastName
                                        ORDER BY c.customerId""",
                                Object[].class
                        )
                        .setParameter("storeId", 1)
                        .setMaxResults(10)
                        .getResultList()
        );
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertRows(list);
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Object[].class);
            final var c = cq.from(Customer.class);
            final var r = c.join(Customer_.rentals, JoinType.LEFT);
            r.on(cb.isNull(r.get(Rental_.returnDate)));
            cq.select(cb.array(c.get(Customer_.customerId), c.get(Customer_.firstName),
                               c.get(Customer_.lastName), cb.count(r)))
                    .where(cb.equal(c.get(Customer_.store).get(Store_.storeId),
                                    cb.parameter(Integer.class, "storeId")))
                    .groupBy(c.get(Customer_.customerId), c.get(Customer_.firstName),
                             c.get(Customer_.lastName))
                    .orderBy(cb.asc(c.get(Customer_.customerId)));
            return em.createQuery(cq)
                    .setParameter("storeId", 1)
                    .setMaxResults(10)
                    .getResultList();
        });
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertRows(list);
    }

    private static void assertRows(final List<Object[]> list) {
        assertThat(list)
                .extracting(r -> ((Number) r[0]).intValue(), r -> r[1], r -> r[2],
                            r -> ((Number) r[3]).longValue())
                .containsExactly(
                        tuple(1, "MARY", "SMITH", 0L),
                        tuple(2, "PATRICIA", "JOHNSON", 0L),
                        tuple(3, "LINDA", "WILLIAMS", 0L),
                        tuple(5, "ELIZABETH", "BROWN", 1L),
                        tuple(7, "MARIA", "MILLER", 0L),
                        tuple(10, "DOROTHY", "TAYLOR", 0L),
                        tuple(12, "NANCY", "THOMAS", 0L),
                        tuple(15, "HELEN", "HARRIS", 2L),
                        tuple(17, "DONNA", "THOMPSON", 0L),
                        tuple(19, "RUTH", "MARTINEZ", 0L));
    }

}
