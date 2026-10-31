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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers the exercises of {@code src/test/sql/exercises/rental_exercises_02_intermediate.sql}, against the
 * installed {@code sakila} database; a nested class per exercise, which answers it with JPQL and
 * with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Rental_Exercises_02_Intermediate_PersistenceIT {

    @DisplayName("SAKILA_RENTAL_I_02: on which days the rentals not returned yet were made")
    @Nested
    class SAKILA_RENTAL_I_02_Test {

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

    @DisplayName("SAKILA_RENTAL_I_03: the rentals overdue at the end of the data")
    @Nested
    class SAKILA_RENTAL_I_03_Test {

        // JPQL and the Criteria API have no difference of two dates; DATEDIFF is MySQL's own
        @Test
        void nativeQuery() {
            final var list = __Exercises_TestUtils.apply(em -> (List<?>) em.createNativeQuery("""
                    SELECT r.*, f.title, f.rental_duration, DATEDIFF('2006-02-15', r.rental_date) - f.rental_duration AS days_overdue
                    FROM rental r
                             JOIN inventory i ON i.inventory_id = r.inventory_id
                             JOIN film f ON f.film_id = i.film_id
                    WHERE r.return_date IS NULL
                      AND DATEDIFF('2006-02-15', r.rental_date) > f.rental_duration
                    ORDER BY days_overdue DESC, r.rental_id""")
                    .getResultList());
            list.forEach(r -> log.debug("{}", r instanceof Object[] a ? Arrays.toString(a) : r));
            assertThat(list)
                    .extracting(r -> ((Number) ((Object[]) r)[0]).intValue(),
                                r -> ((Number) ((Object[]) r)[3]).intValue(),
                                r -> ((Object[]) r)[7],
                                r -> ((Number) ((Object[]) r)[9]).longValue())
                    .containsExactly(tuple(14098, 554, "ACADEMY DINOSAUR", 172L));
        }
    }
}
