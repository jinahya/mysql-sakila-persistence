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
 * Answers the exercises of {@code src/test/sql/inventory_exercises_02_intermediate.sql}, against
 * the installed {@code sakila} database; a nested class per exercise, which answers it with JPQL
 * and with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Inventory_Exercises_02_Intermediate_PersistenceIT {

    @DisplayName("exercise 1: the five films with the most copies")
    @Nested
    class Exercise1Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                    SELECT f.filmId, f.title, COUNT(i)
                    FROM Film f
                             JOIN f.inventories i
                    GROUP BY f.filmId, f.title
                    ORDER BY COUNT(i) DESC, f.filmId""", Object[].class)
                    .setMaxResults(5)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> r[1], r -> ((Number) r[2]).longValue())
                    .containsExactly(
                            tuple(1, "ACADEMY DINOSAUR", 8L),
                            tuple(31, "APACHE DIVINE", 8L),
                            tuple(69, "BEVERLY OUTLAW", 8L),
                            tuple(73, "BINGO TALENTED", 8L),
                            tuple(86, "BOOGIE AMELIE", 8L));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var f = cq.from(Film.class);
                final var i = f.join(Film_.inventories);
                cq.select(cb.array(f.get(Film_.filmId), f.get(Film_.title), cb.count(i)))
                        .groupBy(f.get(Film_.filmId), f.get(Film_.title))
                        .orderBy(cb.desc(cb.count(i)), cb.asc(f.get(Film_.filmId)));
                return em.createQuery(cq)
                        .setMaxResults(5)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> r[1], r -> ((Number) r[2]).longValue())
                    .containsExactly(
                            tuple(1, "ACADEMY DINOSAUR", 8L),
                            tuple(31, "APACHE DIVINE", 8L),
                            tuple(69, "BEVERLY OUTLAW", 8L),
                            tuple(73, "BINGO TALENTED", 8L),
                            tuple(86, "BOOGIE AMELIE", 8L));
        }
    }

    @DisplayName("exercise 2: how many copies were never rented")
    @Nested
    class Exercise2Test {

        @Test
        void __QueryLanguage() {
            final var value = __Exercises_TestUtils.apply(em -> em.createQuery("""
                    SELECT COUNT(i)
                    FROM Inventory i
                    WHERE i.rentals IS EMPTY""", Long.class)
                    .getSingleResult());
            log.debug("{}", value);
            assertThat(value).isEqualTo(1L);
        }

        @Test
        void __CriteriaApi() {
            final var value = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Long.class);
                final var i = cq.from(Inventory.class);
                cq.select(cb.count(i))
                        .where(cb.isEmpty(i.get(Inventory_.rentals)));
                return em.createQuery(cq)
                        .getSingleResult();
            });
            log.debug("{}", value);
            assertThat(value).isEqualTo(1L);
        }
    }
}
