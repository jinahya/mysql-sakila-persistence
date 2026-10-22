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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Answers the exercises of {@code src/test/sql/address_exercises_03_advanced.sql}, against the
 * installed {@code sakila} database; a nested class per exercise, which answers it with JPQL and
 * with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Address_Exercises_03_Advanced_PersistenceIT {

    @DisplayName("exercise 1: the addresses no customer, staff member or store has")
    @Nested
    class Exercise1Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT a
                                                                                      FROM Address a
                                                                                      WHERE NOT EXISTS (SELECT c FROM Customer c WHERE c.address = a)
                                                                                        AND NOT EXISTS (SELECT s FROM Staff s WHERE s.address = a)
                                                                                        AND NOT EXISTS (SELECT s FROM Store s WHERE s.address = a)
                                                                                      ORDER BY a.addressId""",
                                                                              Address.class)
                    .getResultList());
            list.forEach(e -> log.debug("{}", e));
            assertThat(list).isEmpty();
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Address.class);
                final var a = cq.from(Address.class);
                final var customers = cq.subquery(Customer.class);
                final var c = customers.from(Customer.class);
                customers.select(c).where(cb.equal(c.get(Customer_.address), a));
                final var staffs = cq.subquery(Staff.class);
                final var s = staffs.from(Staff.class);
                staffs.select(s).where(cb.equal(s.get(Staff_.address), a));
                final var stores = cq.subquery(Store.class);
                final var st = stores.from(Store.class);
                stores.select(st).where(cb.equal(st.get(Store_.address), a));
                cq.select(a)
                        .where(cb.not(cb.exists(customers)), cb.not(cb.exists(staffs)),
                               cb.not(cb.exists(stores)))
                        .orderBy(cb.asc(a.get(Address_.addressId)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(e -> log.debug("{}", e));
            assertThat(list).isEmpty();
        }
    }
}
