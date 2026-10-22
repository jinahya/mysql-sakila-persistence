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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers the exercises of {@code src/test/sql/customer_list_exercises_01_basic.sql}, against the
 * installed {@code sakila} database; a nested class per exercise, which answers it with JPQL and
 * with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class CustomerList_Exercises_01_Basic_PersistenceIT {

    @DisplayName("exercise 1: the customers in Japan")
    @Nested
    class Exercise1Test {

        @ParameterizedTest
        @ValueSource(strings = {"Japan", "Canada", "Brazil"})
        void __QueryLanguage(final String country) {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                    SELECT l
                    FROM CustomerList l
                    WHERE l.country = :country
                    ORDER BY l.id""", CustomerList.class)
                    .setParameter("country", country)
                    .getResultList());
            list.forEach(e -> log.debug("{}", e));
            assertThat(list).isNotEmpty().allSatisfy(l -> assertThat(l.getCountry()).isEqualTo(country));
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
                        .where(cb.equal(l.get(CustomerList_.country), cb.parameter(String.class, "country")))
                        .orderBy(cb.asc(l.get(CustomerList_.id)));
                return em.createQuery(cq)
                        .setParameter("country", country)
                        .getResultList();
            });
            list.forEach(e -> log.debug("{}", e));
            assertThat(list).isNotEmpty().allSatisfy(l -> assertThat(l.getCountry()).isEqualTo(country));
            assertThat(list).extracting(CustomerList::getId).isSorted();
        }
    }

    @DisplayName("exercise 2: how many customers each store has, as the view's SID says")
    @Nested
    class Exercise2Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                    SELECT l.sid, COUNT(l)
                    FROM CustomerList l
                    GROUP BY l.sid
                    ORDER BY l.sid""", Object[].class)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> ((Number) r[1]).longValue())
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
                cq.select(cb.array(l.get(CustomerList_.sid), cb.count(l)))
                        .groupBy(l.get(CustomerList_.sid))
                        .orderBy(cb.asc(l.get(CustomerList_.sid)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple(1, 326L),
                            tuple(2, 273L));
        }
    }
}
