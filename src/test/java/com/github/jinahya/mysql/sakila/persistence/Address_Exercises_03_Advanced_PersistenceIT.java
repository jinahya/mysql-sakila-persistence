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

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers the exercises of {@code src/test/sql/address_exercises_03_advanced.sql}, against the
 * installed {@code sakila} database; a nested class per exercise, which answers it with JPQL and
 * with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Address_Exercises_03_Advanced_PersistenceIT {

    @DisplayName("SAKILA_ADDRESS_A_01: the addresses no customer, staff member or store has")
    @Nested
    class SAKILA_ADDRESS_A_01_Test {

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

    @DisplayName("SAKILA_ADDRESS_A_02: the pairs of addresses which share a postal code")
    @Nested
    class SAKILA_ADDRESS_A_02_Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT a1, a2
                                                                                      FROM Address a1, Address a2
                                                                                      WHERE a2.postalCode = a1.postalCode
                                                                                        AND a2.addressId > a1.addressId
                                                                                        AND a1.postalCode <> ''
                                                                                      ORDER BY a1.addressId, a2.addressId""",
                                                                              Object[].class)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Address) r[0]).getAddressId(),
                                r -> ((Address) r[0]).getAddress(),
                                r -> ((Address) r[1]).getAddressId(),
                                r -> ((Address) r[1]).getAddress(),
                                r -> ((Address) r[0]).getPostalCode())
                    .containsExactly(
                            tuple(221, "866 Shivapuri Manor", 595, "1074 Sanaa Parkway", "22474"),
                            tuple(304, "1635 Kuwana Boulevard", 603, "1103 Quilmes Boulevard",
                                  "52137"),
                            tuple(313, "1191 Sungai Petani Boulevard", 476, "270 Tambaram Parkway",
                                  "9668"));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var a1 = cq.from(Address.class);
                final var a2 = cq.from(Address.class);
                cq.select(cb.array(a1, a2))
                        .where(cb.equal(a2.get(Address_.postalCode), a1.get(Address_.postalCode)),
                               cb.gt(a2.get(Address_.addressId), a1.get(Address_.addressId)),
                               cb.notEqual(a1.get(Address_.postalCode), ""))
                        .orderBy(cb.asc(a1.get(Address_.addressId)),
                                 cb.asc(a2.get(Address_.addressId)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Address) r[0]).getAddressId(),
                                r -> ((Address) r[0]).getAddress(),
                                r -> ((Address) r[1]).getAddressId(),
                                r -> ((Address) r[1]).getAddress(),
                                r -> ((Address) r[0]).getPostalCode())
                    .containsExactly(
                            tuple(221, "866 Shivapuri Manor", 595, "1074 Sanaa Parkway", "22474"),
                            tuple(304, "1635 Kuwana Boulevard", 603, "1103 Quilmes Boulevard",
                                  "52137"),
                            tuple(313, "1191 Sungai Petani Boulevard", 476, "270 Tambaram Parkway",
                                  "9668"));
        }
    }

    @DisplayName(
            "SAKILA_ADDRESS_A_03: the three districts with the most addresses in each of India, China and " +
            "the United States")
    @Nested
    class SAKILA_ADDRESS_A_03_Test {

        // JPQL has no window functions, nor a subquery in the FROM clause; ROW_NUMBER() OVER
        // (PARTITION BY ...) is SQL's
        @Test
        void nativeQuery() {
            final var list = __Exercises_TestUtils.apply(em -> (List<?>) em.createNativeQuery("""
                                                                                                      SELECT country, district, addresses
                                                                                                      FROM (SELECT co.country,
                                                                                                                   a.district,
                                                                                                                   COUNT(*)                                                                          AS addresses,
                                                                                                                   ROW_NUMBER() OVER (PARTITION BY co.country_id ORDER BY COUNT(*) DESC, a.district) AS district_rank
                                                                                                            FROM address a
                                                                                                                     JOIN city ci ON ci.city_id = a.city_id
                                                                                                                     JOIN country co ON co.country_id = ci.country_id
                                                                                                            WHERE co.country IN ('India', 'China', 'United States')
                                                                                                            GROUP BY co.country_id, co.country, a.district) ranked
                                                                                                      WHERE district_rank <= 3
                                                                                                      ORDER BY country, district_rank""")
                    .getResultList());
            list.forEach(r -> log.debug("{}", r instanceof Object[] a ? Arrays.toString(a) : r));
            assertThat(list)
                    .extracting(r -> ((Object[]) r)[0], r -> ((Object[]) r)[1],
                                r -> ((Number) ((Object[]) r)[2]).longValue())
                    .containsExactly(
                            tuple("China", "Shandong", 9L),
                            tuple("China", "Hubei", 5L),
                            tuple("China", "Guangdong", 4L),
                            tuple("India", "West Bengali", 9L),
                            tuple("India", "Uttar Pradesh", 8L),
                            tuple("India", "Maharashtra", 7L),
                            tuple("United States", "California", 9L),
                            tuple("United States", "Texas", 5L),
                            tuple("United States", "Illinois", 4L));
        }
    }
}
