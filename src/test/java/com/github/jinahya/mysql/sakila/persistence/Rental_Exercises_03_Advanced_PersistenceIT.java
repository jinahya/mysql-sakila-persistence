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
 * Answers the exercises of {@code src/test/sql/rental_exercises_03_advanced.sql}, against the
 * installed {@code sakila} database; a nested class per exercise, which answers it with JPQL and
 * with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Rental_Exercises_03_Advanced_PersistenceIT {

    @DisplayName(
            "exercise 1: how many rentals came back later than the film's rental duration allows")
    @Nested
    class Exercise1Test {

        // JPQL cannot add a number of days to a date; INTERVAL is MySQL's own; the Criteria API
        // cannot
        // add a number of days to a date; INTERVAL is MySQL's own
        @Test
        void nativeQuery() {
            final var list = __Exercises_TestUtils.apply(em -> (List<?>) em.createNativeQuery("""
                                                                                                      SELECT COUNT(*) AS late
                                                                                                      FROM rental r
                                                                                                               JOIN inventory i ON i.inventory_id = r.inventory_id
                                                                                                               JOIN film f ON f.film_id = i.film_id
                                                                                                      WHERE r.return_date > r.rental_date + INTERVAL f.rental_duration DAY""")
                    .getResultList());
            list.forEach(r -> log.debug("{}", r instanceof Object[] a ? Arrays.toString(a) : r));
            assertThat(list).singleElement()
                    .satisfies(v -> assertThat(((Number) v).longValue()).isEqualTo(8121L));
        }
    }

    @DisplayName("exercise 2: the five longest waits between a customer's two rentals in 2005")
    @Nested
    class Exercise2Test {

        // JPQL has no LAG and no difference of two dates; DATEDIFF is MySQL's own; the Criteria API
        // has neither
        @Test
        void nativeQuery() {
            final var list = __Exercises_TestUtils.apply(em -> (List<?>) em.createNativeQuery("""
                                                                                                      SELECT customer_id, previous_rental, rental_date, DATEDIFF(rental_date, previous_rental) AS days
                                                                                                      FROM (SELECT customer_id, rental_date,
                                                                                                                   LAG(rental_date) OVER (PARTITION BY customer_id ORDER BY rental_date, rental_id) AS previous_rental
                                                                                                            FROM rental
                                                                                                            WHERE rental_date < '2006-01-01') g
                                                                                                      WHERE previous_rental IS NOT NULL
                                                                                                      ORDER BY days DESC, customer_id, rental_date
                                                                                                      LIMIT 5""")
                    .getResultList());
            list.forEach(r -> log.debug("{}", r instanceof Object[] a ? Arrays.toString(a) : r));
            assertThat(list)
                    .extracting(r -> ((Number) ((Object[]) r)[0]).intValue(),
                                r -> ((Number) ((Object[]) r)[3]).longValue())
                    .containsExactly(
                            tuple(61, 57L),
                            tuple(101, 43L),
                            tuple(4, 39L),
                            tuple(401, 39L),
                            tuple(433, 39L));
        }
    }

    @DisplayName("exercise 3: whether a copy was ever rented out twice at the same time")
    @Nested
    class Exercise3Test {

        @Test
        void __QueryLanguage() {
            final var value = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                       SELECT COUNT(a)
                                                                                       FROM Rental a, Rental b
                                                                                       WHERE b.inventory = a.inventory
                                                                                         AND b.rentalId <> a.rentalId
                                                                                         AND b.rentalDate >= a.rentalDate
                                                                                         AND (a.returnDate IS NULL OR b.rentalDate < a.returnDate)
                                                                                         AND (b.rentalDate > a.rentalDate OR b.rentalId > a.rentalId)""",
                                                                               Long.class)
                    .getSingleResult());
            log.debug("{}", value);
            assertThat(value).isEqualTo(0L);
        }

        @Test
        void __CriteriaApi() {
            final var value = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Long.class);
                final var a = cq.from(Rental.class);
                final var b = cq.from(Rental.class);
                final var aRentalDate = a.get(Rental_.rentalDate);
                final var bRentalDate = b.get(Rental_.rentalDate);
                final var aReturnDate = a.get(Rental_.returnDate);
                cq.select(cb.count(a))
                        .where(cb.equal(b.get(Rental_.inventory), a.get(Rental_.inventory)),
                               cb.notEqual(b.get(Rental_.rentalId), a.get(Rental_.rentalId)),
                               cb.greaterThanOrEqualTo(bRentalDate, aRentalDate),
                               cb.or(cb.isNull(aReturnDate), cb.lessThan(bRentalDate, aReturnDate)),
                               cb.or(cb.greaterThan(bRentalDate, aRentalDate),
                                     cb.gt(b.get(Rental_.rentalId), a.get(Rental_.rentalId))));
                return em.createQuery(cq)
                        .getSingleResult();
            });
            log.debug("{}", value);
            assertThat(value).isEqualTo(0L);
        }
    }
}
