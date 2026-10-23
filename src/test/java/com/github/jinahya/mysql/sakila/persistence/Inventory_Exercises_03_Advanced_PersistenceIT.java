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
 * Answers the exercises of {@code src/test/sql/inventory_exercises_03_advanced.sql}, against the
 * installed {@code sakila} database; a nested class per exercise, which answers it with JPQL and
 * with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Inventory_Exercises_03_Advanced_PersistenceIT {

    @DisplayName("exercise 1: the five longest times a copy sat on the shelf between two rentals")
    @Nested
    class Exercise1Test {

        // JPQL has no window functions, nor a difference of two dates; LEAD() OVER finds the next
        // rental of the same copy, and DATEDIFF counts the days in between
        @Test
        void nativeQuery() {
            final var list = __Exercises_TestUtils.apply(em -> (List<?>) em.createNativeQuery("""
                                                                                                      WITH rentals AS (SELECT r.inventory_id, r.rental_date, r.return_date,
                                                                                                                              LEAD(r.rental_date) OVER (PARTITION BY r.inventory_id ORDER BY r.rental_date, r.rental_id) AS next_rental_date
                                                                                                                       FROM rental r
                                                                                                                       WHERE r.rental_date < ?1)
                                                                                                      SELECT inventory_id, return_date, next_rental_date,
                                                                                                             DATEDIFF(next_rental_date, return_date) AS idle_days
                                                                                                      FROM rentals
                                                                                                      WHERE return_date IS NOT NULL
                                                                                                        AND next_rental_date IS NOT NULL
                                                                                                      ORDER BY idle_days DESC, inventory_id, return_date
                                                                                                      LIMIT 5""")
                    .setParameter(1, "2006-01-01")
                    .getResultList());
            list.forEach(r -> log.debug("{}", r instanceof Object[] a ? Arrays.toString(a) : r));
            assertThat(list)
                    .extracting(r -> ((Number) ((Object[]) r)[0]).intValue(),
                                r -> ((Number) ((Object[]) r)[3]).longValue())
                    .containsExactly(
                            tuple(977, 45L),
                            tuple(3921, 45L),
                            tuple(506, 39L),
                            tuple(517, 39L),
                            tuple(934, 39L));
        }
    }

    @DisplayName("exercise 2: how many films have had every one of their copies rented")
    @Nested
    class Exercise2Test {

        // JPQL has no SELECT without a FROM; the two counts are two queries
        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> List.<Object[]>of(new Object[]{
                    em.createQuery("SELECT COUNT(DISTINCT i.filmId) FROM Inventory i", Long.class)
                            .getSingleResult(),
                    em.createQuery("""
                                           SELECT COUNT(f)
                                           FROM Film f
                                           WHERE f.inventories IS NOT EMPTY
                                             AND NOT EXISTS (SELECT o FROM Inventory o WHERE o.film = f AND o.rentals IS EMPTY)""",
                                   Long.class)
                            .getSingleResult()}));
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).longValue(), r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple(958L, 957L));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var inventory = cb.createQuery(Long.class);
                final var i = inventory.from(Inventory.class);
                inventory.select(cb.countDistinct(i.get(Inventory_.filmId)));
                final var rented = cb.createQuery(Long.class);
                final var f = rented.from(Film.class);
                final var never = rented.subquery(Inventory.class);
                final var o = never.from(Inventory.class);
                never.select(o).where(cb.equal(o.get(Inventory_.film), f),
                                      cb.isEmpty(o.get(Inventory_.rentals)));
                rented.select(cb.count(f))
                        .where(cb.isNotEmpty(f.get(Film_.inventories)), cb.not(cb.exists(never)));
                return List.<Object[]>of(new Object[]{
                        em.createQuery(inventory).getSingleResult(),
                        em.createQuery(rented).getSingleResult()});
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).longValue(), r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple(958L, 957L));
        }
    }
}
