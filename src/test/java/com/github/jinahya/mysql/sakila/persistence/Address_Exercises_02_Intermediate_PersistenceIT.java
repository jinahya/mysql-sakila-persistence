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

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import jakarta.persistence.metamodel.SingularAttribute;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers the exercises of {@code src/test/sql/address_exercises_02_intermediate.sql}, against the
 * installed {@code sakila} database; a nested class per exercise, which answers it with JPQL and
 * with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Address_Exercises_02_Intermediate_PersistenceIT {

    @DisplayName("SAKILA_ADDRESS_I_01: the first five addresses, with their city and country")
    @Nested
    class SAKILA_ADDRESS_I_01_Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT a, c, co
                                                                                      FROM Address a
                                                                                               JOIN a.city c
                                                                                               JOIN c.country co
                                                                                      ORDER BY a.addressId""",
                                                                              Object[].class)
                    .setMaxResults(5)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Address) r[0]).getAddressId(),
                                r -> ((Address) r[0]).getAddress(),
                                r -> ((City) r[1]).getCity(), r -> ((Country) r[2]).getCountry())
                    .containsExactly(
                            tuple(1, "47 MySakila Drive", "Lethbridge", "Canada"),
                            tuple(2, "28 MySQL Boulevard", "Woodridge", "Australia"),
                            tuple(3, "23 Workhaven Lane", "Lethbridge", "Canada"),
                            tuple(4, "1411 Lillydale Drive", "Woodridge", "Australia"),
                            tuple(5, "1913 Hanoi Way", "Sasebo", "Japan"));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var a = cq.from(Address.class);
                final var c = a.join(Address_.city);
                final var co = c.join(City_.country);
                cq.select(cb.array(a, c, co))
                        .orderBy(cb.asc(a.get(Address_.addressId)));
                return em.createQuery(cq)
                        .setMaxResults(5)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Address) r[0]).getAddressId(),
                                r -> ((Address) r[0]).getAddress(),
                                r -> ((City) r[1]).getCity(), r -> ((Country) r[2]).getCountry())
                    .containsExactly(
                            tuple(1, "47 MySakila Drive", "Lethbridge", "Canada"),
                            tuple(2, "28 MySQL Boulevard", "Woodridge", "Australia"),
                            tuple(3, "23 Workhaven Lane", "Lethbridge", "Canada"),
                            tuple(4, "1411 Lillydale Drive", "Woodridge", "Australia"),
                            tuple(5, "1913 Hanoi Way", "Sasebo", "Japan"));
        }
    }

    @DisplayName("SAKILA_ADDRESS_I_02: the district names found in more than one country")
    @Nested
    class SAKILA_ADDRESS_I_02_Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT a.district, COUNT(DISTINCT c.country)
                                                                                      FROM Address a
                                                                                               JOIN a.city c
                                                                                      WHERE a.district <> ''
                                                                                      GROUP BY a.district
                                                                                      HAVING COUNT(DISTINCT c.country) > 1
                                                                                      ORDER BY COUNT(DISTINCT c.country) DESC, a.district""",
                                                                              Object[].class)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple("Central", 3L),
                            tuple("al-Sharqiya", 2L),
                            tuple("Distrito Federal", 2L),
                            tuple("Ontario", 2L),
                            tuple("Punjab", 2L),
                            tuple("Sucre", 2L));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var a = cq.from(Address.class);
                final var c = a.join(Address_.city);
                final var countries = cb.countDistinct(c.get(City_.country));
                cq.select(cb.array(a.get(Address_.district), countries))
                        .where(cb.notEqual(a.get(Address_.district), ""))
                        .groupBy(a.get(Address_.district))
                        .having(cb.gt(countries, 1L))
                        .orderBy(cb.desc(countries), cb.asc(a.get(Address_.district)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple("Central", 3L),
                            tuple("al-Sharqiya", 2L),
                            tuple("Distrito Federal", 2L),
                            tuple("Ontario", 2L),
                            tuple("Punjab", 2L),
                            tuple("Sucre", 2L));
        }
    }

    @DisplayName("SAKILA_ADDRESS_I_03: how many addresses belong to a store, a staff member or a customer")
    @Nested
    class SAKILA_ADDRESS_I_03_Test {

        // the most addresses first, as the SQL orders them; a UNION has no ORDER BY of its own
        private static final Comparator<Object[]> ORDER =
                Comparator.<Object[], Long>comparing(r -> ((Number) r[1]).longValue()).reversed()
                        .thenComparing(r -> (String) r[0]);

        private static <T> Subquery<T> using(final CriteriaQuery<?> cq,
                                             final CriteriaBuilder cb, final Root<Address> a,
                                             final Class<T> type,
                                             final SingularAttribute<? super T, Address> address) {
            final var subquery = cq.subquery(type);
            final var r = subquery.from(type);
            return subquery.select(r).where(cb.equal(r.get(address), a));
        }

        // the addresses which a store uses, which a staff member, not a store, uses, and so on
        private static CriteriaQuery<Object[]> addresses(final CriteriaBuilder cb,
                                                         final String usedBy) {
            final var cq = cb.createQuery(Object[].class);
            final var a = cq.from(Address.class);
            final var store = cb.exists(using(cq, cb, a, Store.class, Store_.address));
            final var staff = cb.exists(using(cq, cb, a, Staff.class, Staff_.address));
            final var customer = cb.exists(using(cq, cb, a, Customer.class, Customer_.address));
            final var where = switch (usedBy) {
                case "store" -> store;
                case "staff" -> cb.and(staff, cb.not(store));
                case "customer" -> cb.and(customer, cb.not(store), cb.not(staff));
                default -> cb.and(cb.not(customer), cb.not(store), cb.not(staff));
            };
            return cq.select(cb.array(cb.literal(usedBy), cb.count(a)))
                    .where(where);
        }

        // JPQL groups by paths, not by a CASE; each use counts its addresses, and UNION ALL puts
        // the counts together; a use no address has, which the SQL does not group, is dropped in
        // Java, as hibernate takes no HAVING without a GROUP BY
        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT 'store', COUNT(a)
                                                                                      FROM Address a
                                                                                      WHERE EXISTS (SELECT s FROM Store s WHERE s.address = a)
                                                                                      UNION ALL
                                                                                      SELECT 'staff', COUNT(a)
                                                                                      FROM Address a
                                                                                      WHERE EXISTS (SELECT s FROM Staff s WHERE s.address = a)
                                                                                        AND NOT EXISTS (SELECT s FROM Store s WHERE s.address = a)
                                                                                      UNION ALL
                                                                                      SELECT 'customer', COUNT(a)
                                                                                      FROM Address a
                                                                                      WHERE EXISTS (SELECT c FROM Customer c WHERE c.address = a)
                                                                                        AND NOT EXISTS (SELECT s FROM Store s WHERE s.address = a)
                                                                                        AND NOT EXISTS (SELECT s FROM Staff s WHERE s.address = a)
                                                                                      UNION ALL
                                                                                      SELECT 'none', COUNT(a)
                                                                                      FROM Address a
                                                                                      WHERE NOT EXISTS (SELECT c FROM Customer c WHERE c.address = a)
                                                                                        AND NOT EXISTS (SELECT s FROM Store s WHERE s.address = a)
                                                                                        AND NOT EXISTS (SELECT s FROM Staff s WHERE s.address = a)""",
                                                                              Object[].class)
                    .getResultStream()
                    .filter(r -> ((Number) r[1]).longValue() > 0)
                    .sorted(ORDER)
                    .toList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple("customer", 599L),
                            tuple("staff", 2L),
                            tuple("store", 2L));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var union = cb.unionAll(
                        cb.unionAll(cb.unionAll(addresses(cb, "store"), addresses(cb, "staff")),
                                    addresses(cb, "customer")),
                        addresses(cb, "none"));
                return em.createQuery(union)
                        .getResultStream()
                        .filter(r -> ((Number) r[1]).longValue() > 0)
                        .sorted(ORDER)
                        .toList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple("customer", 599L),
                            tuple("staff", 2L),
                            tuple("store", 2L));
        }
    }
}
