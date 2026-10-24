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
 * Answers the exercises of {@code src/test/sql/customer_exercises_01_basic.sql}, against the
 * installed {@code sakila} database; a nested class per exercise, which answers it with JPQL and
 * with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Customer_Exercises_01_Basic_PersistenceIT {

    @DisplayName("SAKILA_CUSTOMER_B_01: how many customers are active, and how many are not")
    @Nested
    class SAKILA_CUSTOMER_B_01_Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT c.active, COUNT(c)
                                                                                      FROM Customer c
                                                                                      GROUP BY c.active
                                                                                      ORDER BY c.active""",
                                                                              Object[].class)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple(false, 15L),
                            tuple(true, 584L));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var c = cq.from(Customer.class);
                cq.select(cb.array(c.get(Customer_.active), cb.count(c)))
                        .groupBy(c.get(Customer_.active))
                        .orderBy(cb.asc(c.get(Customer_.active)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple(false, 15L),
                            tuple(true, 584L));
        }
    }

    @DisplayName("SAKILA_CUSTOMER_B_02: how many customers each store has")
    @Nested
    class SAKILA_CUSTOMER_B_02_Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT s, COUNT(c)
                                                                                      FROM Customer c
                                                                                               JOIN c.store s
                                                                                      GROUP BY s
                                                                                      ORDER BY s.storeId""",
                                                                              Object[].class)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Store) r[0]).getStoreId(), r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple(1, 326L),
                            tuple(2, 273L));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var c = cq.from(Customer.class);
                final var s = c.join(Customer_.store);
                cq.select(cb.array(s, cb.count(c)))
                        .groupBy(s)
                        .orderBy(cb.asc(s.get(Store_.storeId)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Store) r[0]).getStoreId(), r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple(1, 326L),
                            tuple(2, 273L));
        }
    }

    @DisplayName("SAKILA_CUSTOMER_B_03: the domains of the customers' emails")
    @Nested
    class SAKILA_CUSTOMER_B_03_Test {

        // JPQL groups by paths, not by a function of one; SUBSTRING_INDEX has no JPQL counterpart
        // either; the Criteria API has no SUBSTRING_INDEX, and grouping by a function of a path
        // is not
        // portable
        @Test
        void nativeQuery() {
            final var list = __Exercises_TestUtils.apply(em -> (List<?>) em.createNativeQuery("""
                                                                                                      SELECT SUBSTRING_INDEX(email, '@', -1) AS domain, COUNT(*) AS customers
                                                                                                      FROM customer
                                                                                                      GROUP BY domain""")
                    .getResultList());
            list.forEach(r -> log.debug("{}", r instanceof Object[] a ? Arrays.toString(a) : r));
            assertThat(list).singleElement().satisfies(
                    r -> assertThat((Object[]) r).containsExactly("sakilacustomer.org", 599L));
        }
    }

    @DisplayName("SAKILA_CUSTOMER_B_04: the customers who are not active")
    @Nested
    class SAKILA_CUSTOMER_B_04_Test {

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

    @DisplayName("SAKILA_CUSTOMER_B_05: how many customers have the same initial in both names")
    @Nested
    class SAKILA_CUSTOMER_B_05_Test {

        // EclipseLink's JPQL parser does not know LEFT; SUBSTRING(x, 1, 1) takes the initial
        @Test
        void __QueryLanguage() {
            final var value = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                       SELECT COUNT(c)
                                                                                       FROM Customer c
                                                                                       WHERE SUBSTRING(c.firstName, 1, 1) = SUBSTRING(c.lastName, 1, 1)""",
                                                                               Long.class)
                    .getSingleResult());
            log.debug("{}", value);
            assertThat(value).isEqualTo(30L);
        }

        @Test
        void __CriteriaApi() {
            final var value = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Long.class);
                final var c = cq.from(Customer.class);
                cq.select(cb.count(c))
                        .where(cb.equal(cb.substring(c.get(Customer_.firstName), 1, 1),
                                        cb.substring(c.get(Customer_.lastName), 1, 1)));
                return em.createQuery(cq).getSingleResult();
            });
            log.debug("{}", value);
            assertThat(value).isEqualTo(30L);
        }
    }
}
