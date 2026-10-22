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

    @DisplayName("exercise 1: the first five addresses, with their city and country")
    @Nested
    class Exercise1Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT a.addressId, a.address, c.city, co.country
                                                                                      FROM Address a
                                                                                               JOIN a.city c
                                                                                               JOIN c.country co
                                                                                      ORDER BY a.addressId""",
                                                                              Object[].class)
                    .setMaxResults(5)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> r[1], r -> r[2], r -> r[3])
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
                cq.select(cb.array(a.get(Address_.addressId), a.get(Address_.address),
                                   c.get(City_.city), co.get(Country_.country)))
                        .orderBy(cb.asc(a.get(Address_.addressId)));
                return em.createQuery(cq)
                        .setMaxResults(5)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> r[1], r -> r[2], r -> r[3])
                    .containsExactly(
                            tuple(1, "47 MySakila Drive", "Lethbridge", "Canada"),
                            tuple(2, "28 MySQL Boulevard", "Woodridge", "Australia"),
                            tuple(3, "23 Workhaven Lane", "Lethbridge", "Canada"),
                            tuple(4, "1411 Lillydale Drive", "Woodridge", "Australia"),
                            tuple(5, "1913 Hanoi Way", "Sasebo", "Japan"));
        }
    }
}
