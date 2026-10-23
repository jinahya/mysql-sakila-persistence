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

import jakarta.persistence.criteria.LocalDateTimeField;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers the exercises of {@code src/test/sql/rental_exercises_02_intermediate.sql}, against the
 * installed {@code sakila} database; a nested class per exercise, which answers it with JPQL and
 * with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Rental_Exercises_02_Intermediate_PersistenceIT {

    @DisplayName("exercise 1: how many rentals are not returned yet, at each store")
    @Nested
    class Exercise1Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT i.storeId, COUNT(r)
                                                                                      FROM Rental r
                                                                                               JOIN r.inventory i
                                                                                      WHERE r.returnDate IS NULL
                                                                                      GROUP BY i.storeId
                                                                                      ORDER BY i.storeId""",
                                                                              Object[].class)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple(1, 92L),
                            tuple(2, 91L));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var r = cq.from(Rental.class);
                final var storeId = r.join(Rental_.inventory).get(Inventory_.storeId);
                cq.select(cb.array(storeId, cb.count(r)))
                        .where(cb.isNull(r.get(Rental_.returnDate)))
                        .groupBy(storeId)
                        .orderBy(cb.asc(storeId));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple(1, 92L),
                            tuple(2, 91L));
        }
    }

    @DisplayName("exercise 2: on which days the rentals not returned yet were made")
    @Nested
    class Exercise2Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT EXTRACT(DATE FROM r.rentalDate), COUNT(r)
                                                                                      FROM Rental r
                                                                                      WHERE r.returnDate IS NULL
                                                                                      GROUP BY EXTRACT(DATE FROM r.rentalDate)
                                                                                      ORDER BY EXTRACT(DATE FROM r.rentalDate)""",
                                                                              Object[].class)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    // the day, as a LocalDate or as the LocalDateTime of its midnight
                    .extracting(r -> String.valueOf(r[0]).substring(0, 10),
                                r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple("2005-08-21", 1L),
                            tuple("2006-02-14", 182L));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var r = cq.from(Rental.class);
                final var day = cb.extract(LocalDateTimeField.DATE, r.get(Rental_.rentalDate));
                cq.select(cb.array(day, cb.count(r)))
                        .where(cb.isNull(r.get(Rental_.returnDate)))
                        .groupBy(day)
                        .orderBy(cb.asc(day));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    // the day, as a LocalDate or as the LocalDateTime of its midnight
                    .extracting(r -> String.valueOf(r[0]).substring(0, 10),
                                r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple("2005-08-21", 1L),
                            tuple("2006-02-14", 182L));
        }
    }
}
