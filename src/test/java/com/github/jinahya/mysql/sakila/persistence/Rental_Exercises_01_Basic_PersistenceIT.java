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
 * Answers the exercises of {@code src/test/sql/exercises/rental_exercises_01_basic.sql}, against the
 * installed {@code sakila} database; a nested class per exercise, which answers it with JPQL and
 * with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Rental_Exercises_01_Basic_PersistenceIT {

    @DisplayName("SAKILA_RENTAL_B_01: how many rentals are not returned yet")
    @Nested
    class SAKILA_RENTAL_B_01_Test {

        @Test
        void __QueryLanguage() {
            final var value = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                       SELECT COUNT(r)
                                                                                       FROM Rental r
                                                                                       WHERE r.returnDate IS NULL""",
                                                                               Long.class)
                    .getSingleResult());
            log.debug("{}", value);
            assertThat(value).isEqualTo(183L);
        }

        @Test
        void __CriteriaApi() {
            final var value = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Long.class);
                final var r = cq.from(Rental.class);
                cq.select(cb.count(r))
                        .where(cb.isNull(r.get(Rental_.returnDate)));
                return em.createQuery(cq)
                        .getSingleResult();
            });
            log.debug("{}", value);
            assertThat(value).isEqualTo(183L);
        }
    }

    @DisplayName("SAKILA_RENTAL_B_02: how many rentals were made in each month")
    @Nested
    class SAKILA_RENTAL_B_02_Test {

        // JPQL has no DATE_FORMAT; the year and the month are EXTRACTed, and written as one in Java
        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT EXTRACT(YEAR FROM r.rentalDate), EXTRACT(MONTH FROM r.rentalDate), COUNT(r)
                                                                                      FROM Rental r
                                                                                      GROUP BY EXTRACT(YEAR FROM r.rentalDate), EXTRACT(MONTH FROM r.rentalDate)
                                                                                      ORDER BY EXTRACT(YEAR FROM r.rentalDate), EXTRACT(MONTH FROM r.rentalDate)""",
                                                                              Object[].class)
                    .getResultStream()
                    // year and month, as mysql's DATE_FORMAT(..., '%Y-%m') writes them
                    .map(row -> new Object[]{String.format("%d-%02d", row[0], row[1]), row[2]})
                    .toList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple("2005-05", 1156L),
                            tuple("2005-06", 2311L),
                            tuple("2005-07", 6709L),
                            tuple("2005-08", 5686L),
                            tuple("2006-02", 182L));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var r = cq.from(Rental.class);
                final var year = cb.extract(LocalDateTimeField.YEAR, r.get(Rental_.rentalDate));
                final var month = cb.extract(LocalDateTimeField.MONTH, r.get(Rental_.rentalDate));
                cq.select(cb.array(year, month, cb.count(r)))
                        .groupBy(year, month)
                        .orderBy(cb.asc(year), cb.asc(month));
                return em.createQuery(cq).getResultStream()
                        // year and month, as mysql's DATE_FORMAT(..., '%Y-%m') writes them
                        .map(row -> new Object[]{String.format("%d-%02d", row[0], row[1]), row[2]})
                        .toList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple("2005-05", 1156L),
                            tuple("2005-06", 2311L),
                            tuple("2005-07", 6709L),
                            tuple("2005-08", 5686L),
                            tuple("2006-02", 182L));
        }
    }

    @DisplayName("SAKILA_RENTAL_B_03: how many days a returned rental lasts, on average")
    @Nested
    class SAKILA_RENTAL_B_03_Test {

        // JPQL has no difference of two dates; DATEDIFF is MySQL's own; the Criteria API has no
        // difference of two dates; DATEDIFF is MySQL's own
        @Test
        void nativeQuery() {
            final var list = __Exercises_TestUtils.apply(em -> (List<?>) em.createNativeQuery("""
                                                                                                      SELECT ROUND(AVG(DATEDIFF(return_date, rental_date)), 2) AS average_days
                                                                                                      FROM rental
                                                                                                      WHERE return_date IS NOT NULL""")
                    .getResultList());
            list.forEach(r -> log.debug("{}", r instanceof Object[] a ? Arrays.toString(a) : r));
            assertThat(list).singleElement()
                    .satisfies(v -> assertThat(((Number) v).doubleValue()).isEqualTo(5.03d));
        }
    }

    @DisplayName("SAKILA_RENTAL_B_04: on which day of the week the most rentals were made")
    @Nested
    class SAKILA_RENTAL_B_04_Test {

        // JPQL's EXTRACT has no day of the week; DAYNAME is MySQL's own; the Criteria API's extract
        // has no day of the week; DAYNAME is MySQL's own
        @Test
        void nativeQuery() {
            final var list = __Exercises_TestUtils.apply(em -> (List<?>) em.createNativeQuery("""
                                                                                                      SELECT DAYNAME(rental_date) AS day, COUNT(*) AS rentals
                                                                                                      FROM rental
                                                                                                      GROUP BY day
                                                                                                      ORDER BY rentals DESC
                                                                                                      LIMIT 1""")
                    .getResultList());
            list.forEach(r -> log.debug("{}", r instanceof Object[] a ? Arrays.toString(a) : r));
            assertThat(list).singleElement()
                    .satisfies(r -> assertThat((Object[]) r).containsExactly("Tuesday", 2463L));
        }
    }

    @DisplayName("SAKILA_RENTAL_B_05: how many rentals were returned on the day they were made")
    @Nested
    class SAKILA_RENTAL_B_05_Test {

        // a NULL return date equals nothing, so a rental not returned yet is left out
        @Test
        void __QueryLanguage() {
            final var value = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                       SELECT COUNT(r)
                                                                                       FROM Rental r
                                                                                       WHERE EXTRACT(DATE FROM r.returnDate) = EXTRACT(DATE FROM r.rentalDate)""",
                                                                               Long.class)
                    .getSingleResult());
            log.debug("{}", value);
            assertThat(value).isEqualTo(105L);
        }

        @Test
        void __CriteriaApi() {
            final var value = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Long.class);
                final var r = cq.from(Rental.class);
                cq.select(cb.count(r))
                        .where(cb.equal(
                                cb.extract(LocalDateTimeField.DATE, r.get(Rental_.returnDate)),
                                cb.extract(LocalDateTimeField.DATE, r.get(Rental_.rentalDate))));
                return em.createQuery(cq)
                        .getSingleResult();
            });
            log.debug("{}", value);
            assertThat(value).isEqualTo(105L);
        }
    }
}
