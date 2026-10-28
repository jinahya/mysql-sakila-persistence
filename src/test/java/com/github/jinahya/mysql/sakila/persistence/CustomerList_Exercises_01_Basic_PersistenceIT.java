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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers the exercises of {@code src/test/sql/exercises/customer_list_exercises_01_basic.sql}, against the
 * installed {@code sakila} database; a nested class per exercise, which answers it with JPQL and
 * with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class CustomerList_Exercises_01_Basic_PersistenceIT {

    @DisplayName("SAKILA_CUSTOMER_LIST_B_01: the customers in Japan")
    @Nested
    class SAKILA_CUSTOMER_LIST_B_01_Test {

        @ParameterizedTest
        @ValueSource(strings = {"Japan", "Canada", "Brazil"})
        void __QueryLanguage(final String country) {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT l
                                                                                      FROM CustomerList l
                                                                                      WHERE l.country = :country
                                                                                      ORDER BY l.id""",
                                                                              CustomerList.class)
                    .setParameter("country", country)
                    .getResultList());
            list.forEach(e -> log.debug("{}", e));
            assertThat(list).isNotEmpty()
                    .allSatisfy(l -> assertThat(l.getCountry()).isEqualTo(country));
            assertThat(list).extracting(CustomerList::getId).isSorted();
        }

        @ParameterizedTest
        @ValueSource(strings = {"Japan", "Canada", "Brazil"})
        void __CriteriaApi(final String country) {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(CustomerList.class);
                final var l = cq.from(CustomerList.class);
                cq.select(l)
                        .where(cb.equal(l.get(CustomerList_.country),
                                        cb.parameter(String.class, "country")))
                        .orderBy(cb.asc(l.get(CustomerList_.id)));
                return em.createQuery(cq)
                        .setParameter("country", country)
                        .getResultList();
            });
            list.forEach(e -> log.debug("{}", e));
            assertThat(list).isNotEmpty()
                    .allSatisfy(l -> assertThat(l.getCountry()).isEqualTo(country));
            assertThat(list).extracting(CustomerList::getId).isSorted();
        }
    }

    @DisplayName("SAKILA_CUSTOMER_LIST_B_02: how many customers each store has, as the view's SID says")
    @Nested
    class SAKILA_CUSTOMER_LIST_B_02_Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT s, COUNT(l)
                                                                                      FROM CustomerList l,
                                                                                           Store s
                                                                                      WHERE s.storeId = l.sid
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
                final var l = cq.from(CustomerList.class);
                final var s = cq.from(Store.class);
                cq.select(cb.array(s, cb.count(l)))
                        .where(cb.equal(s.get(Store_.storeId), l.get(CustomerList_.sid)))
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

    @DisplayName("SAKILA_CUSTOMER_LIST_B_03: how many customers the view's notes call active")
    @Nested
    class SAKILA_CUSTOMER_LIST_B_03_Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT l.notes, COUNT(l)
                                                                                      FROM CustomerList l
                                                                                      GROUP BY l.notes
                                                                                      ORDER BY l.notes""",
                                                                              Object[].class)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple("", 15L),
                            tuple("active", 584L));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var l = cq.from(CustomerList.class);
                cq.select(cb.array(l.get(CustomerList_.notes), cb.count(l)))
                        .groupBy(l.get(CustomerList_.notes))
                        .orderBy(cb.asc(l.get(CustomerList_.notes)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple("", 15L),
                            tuple("active", 584L));
        }
    }

    @DisplayName("SAKILA_CUSTOMER_LIST_B_04: how many customers live on an Avenue")
    @Nested
    class SAKILA_CUSTOMER_LIST_B_04_Test {

        @Test
        void __QueryLanguage() {
            final var value = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                       SELECT COUNT(l)
                                                                                       FROM CustomerList l
                                                                                       WHERE l.address LIKE :pattern""",
                                                                               Long.class)
                    .setParameter("pattern", "% Avenue")
                    .getSingleResult());
            log.debug("{}", value);
            assertThat(value).isEqualTo(59L);
        }

        @Test
        void __CriteriaApi() {
            final var value = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Long.class);
                final var l = cq.from(CustomerList.class);
                cq.select(cb.count(l))
                        .where(cb.like(l.get(CustomerList_.address),
                                       cb.parameter(String.class, "pattern")));
                return em.createQuery(cq)
                        .setParameter("pattern", "% Avenue")
                        .getSingleResult();
            });
            log.debug("{}", value);
            assertThat(value).isEqualTo(59L);
        }
    }
}
