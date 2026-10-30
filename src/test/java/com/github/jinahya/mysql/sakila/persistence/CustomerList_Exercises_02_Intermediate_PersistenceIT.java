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

import com.github.jinahya.persistence.util.JinahyaEntityManagerUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers the exercises of {@code src/test/sql/exercises/customer_list_exercises_02_intermediate.sql},
 * against the installed {@code sakila} database; a nested class per exercise, which answers it with
 * JPQL and with the Criteria API, or with a native query where neither can, and logs what it
 * gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class CustomerList_Exercises_02_Intermediate_PersistenceIT {

    @DisplayName("SAKILA_CUSTOMER_LIST_I_01: whether the view has a row for every customer")
    @Nested
    class SAKILA_CUSTOMER_LIST_I_01_Test {

        // JPQL has no subquery in the SELECT clause; the two counts are two queries
        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> List.<Object[]>of(new Object[]{
                    em.createQuery("SELECT COUNT(e) FROM CustomerList e",
                                   Long.class).getSingleResult(),
                    em.createQuery("SELECT COUNT(e) FROM Customer e",
                                   Long.class).getSingleResult()}));
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).longValue(), r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple(599L, 599L));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> List.<Object[]>of(new Object[]{
                    JinahyaEntityManagerUtils.selectEntityCount(em, CustomerList.class),
                    JinahyaEntityManagerUtils.selectEntityCount(em, Customer.class)}));
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).longValue(), r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple(599L, 599L));
        }
    }

    @DisplayName("SAKILA_CUSTOMER_LIST_I_02: the cities with more than one customer")
    @Nested
    class SAKILA_CUSTOMER_LIST_I_02_Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT l.city, l.country, COUNT(l)
                                                                                      FROM CustomerList l
                                                                                      GROUP BY l.city, l.country
                                                                                      HAVING COUNT(l) > 1
                                                                                      ORDER BY l.city, l.country""",
                                                                              Object[].class)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> r[1], r -> ((Number) r[2]).longValue())
                    .containsExactly(
                            tuple("Aurora", "United States", 2L),
                            tuple("London", "United Kingdom", 2L));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var l = cq.from(CustomerList.class);
                cq.select(cb.array(l.get(CustomerList_.city), l.get(CustomerList_.country),
                                   cb.count(l)))
                        .groupBy(l.get(CustomerList_.city), l.get(CustomerList_.country))
                        .having(cb.gt(cb.count(l), 1L))
                        .orderBy(cb.asc(l.get(CustomerList_.city)),
                                 cb.asc(l.get(CustomerList_.country)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> r[1], r -> ((Number) r[2]).longValue())
                    .containsExactly(
                            tuple("Aurora", "United States", 2L),
                            tuple("London", "United Kingdom", 2L));
        }
    }

    @DisplayName("SAKILA_CUSTOMER_LIST_I_03: the rows whose name is not the customer's first and last name")
    @Nested
    class SAKILA_CUSTOMER_LIST_I_03_Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT l, c
                                                                                      FROM CustomerList l,
                                                                                           Customer c
                                                                                      WHERE c.customerId = l.id
                                                                                        AND l.name <> CONCAT(c.firstName, ' ', c.lastName)
                                                                                      ORDER BY l.id""",
                                                                              Object[].class)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list).isEmpty();
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var l = cq.from(CustomerList.class);
                final var c = cq.from(Customer.class);
                cq.select(cb.array(l, c))
                        .where(cb.equal(c.get(Customer_.customerId), l.get(CustomerList_.id)),
                               cb.notEqual(l.get(CustomerList_.name),
                                           cb.concat(cb.concat(c.get(Customer_.firstName), " "),
                                                     c.get(Customer_.lastName))))
                        .orderBy(cb.asc(l.get(CustomerList_.id)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list).isEmpty();
        }
    }
}
