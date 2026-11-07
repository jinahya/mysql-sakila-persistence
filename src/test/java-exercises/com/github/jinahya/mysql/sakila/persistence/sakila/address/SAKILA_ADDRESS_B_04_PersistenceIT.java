package com.github.jinahya.mysql.sakila.persistence.sakila.address;

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

import com.github.jinahya.mysql.sakila.persistence.Address;
import com.github.jinahya.mysql.sakila.persistence.Address_;
import com.github.jinahya.mysql.sakila.persistence.__Exercises_TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code SAKILA_ADDRESS_B_04} of {@code src/test/sql-exercises/address/01_basic.sql} with
 * JPQL and with the Criteria API, or with a native query where neither can.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("SAKILA_ADDRESS_B_04: the addresses with an empty district")
@Slf4j
class SAKILA_ADDRESS_B_04_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                  SELECT a
                                                                                  FROM Address a
                                                                                  WHERE a.district = ''
                                                                                  ORDER BY a.addressId""",
                                                                          Address.class)
                .getResultList());
        list.forEach(e -> log.debug("{}", e));
        assertThat(list)
                .extracting(Address::getAddressId, Address::getAddress, Address::getPostalCode,
                            Address::getPhone)
                .containsExactly(
                        tuple(30, "18 Duisburg Boulevard", "58327", "998009777982"),
                        tuple(386, "1368 Maracaíbo Boulevard", "32716", "934352415130"),
                        tuple(519, "962 Tama Loop", "65952", "282667506728"));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Address.class);
            final var a = cq.from(Address.class);
            cq.select(a)
                    .where(cb.equal(a.get(Address_.district), ""))
                    .orderBy(cb.asc(a.get(Address_.addressId)));
            return em.createQuery(cq)
                    .getResultList();
        });
        list.forEach(e -> log.debug("{}", e));
        assertThat(list)
                .extracting(Address::getAddressId, Address::getAddress, Address::getPostalCode,
                            Address::getPhone)
                .containsExactly(
                        tuple(30, "18 Duisburg Boulevard", "58327", "998009777982"),
                        tuple(386, "1368 Maracaíbo Boulevard", "32716", "934352415130"),
                        tuple(519, "962 Tama Loop", "65952", "282667506728"));
    }

}
