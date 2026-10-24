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
 * Answers the exercises of {@code src/test/sql/inventory_exercises_02_intermediate.sql}, against
 * the installed {@code sakila} database; a nested class per exercise, which answers it with JPQL
 * and with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Inventory_Exercises_02_Intermediate_PersistenceIT {

    @DisplayName("SAKILA_INVENTORY_I_01: the five films with the most copies")
    @Nested
    class SAKILA_INVENTORY_I_01_Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT f, COUNT(i)
                                                                                      FROM Film f
                                                                                               JOIN f.inventories i
                                                                                      GROUP BY f
                                                                                      ORDER BY COUNT(i) DESC, f.filmId""",
                                                                              Object[].class)
                    .setMaxResults(5)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Film) r[0]).getFilmId(), r -> ((Film) r[0]).getTitle(),
                                r -> ((Number) r[1]).longValue())
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
                cq.select(cb.array(f, cb.count(i)))
                        .groupBy(f)
                        .orderBy(cb.desc(cb.count(i)), cb.asc(f.get(Film_.filmId)));
                return em.createQuery(cq)
                        .setMaxResults(5)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Film) r[0]).getFilmId(), r -> ((Film) r[0]).getTitle(),
                                r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple(1, "ACADEMY DINOSAUR", 8L),
                            tuple(31, "APACHE DIVINE", 8L),
                            tuple(69, "BEVERLY OUTLAW", 8L),
                            tuple(73, "BINGO TALENTED", 8L),
                            tuple(86, "BOOGIE AMELIE", 8L));
        }
    }

    @DisplayName("SAKILA_INVENTORY_I_02: how many copies were never rented")
    @Nested
    class SAKILA_INVENTORY_I_02_Test {

        @Test
        void __QueryLanguage() {
            final var value = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                       SELECT COUNT(i)
                                                                                       FROM Inventory i
                                                                                       WHERE i.rentals IS EMPTY""",
                                                                               Long.class)
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

    @DisplayName("SAKILA_INVENTORY_I_03: how many copies each store has out right now")
    @Nested
    class SAKILA_INVENTORY_I_03_Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT st, COUNT(r)
                                                                                      FROM Inventory i
                                                                                               JOIN i.store st
                                                                                               JOIN i.rentals r
                                                                                      WHERE r.returnDate IS NULL
                                                                                      GROUP BY st
                                                                                      ORDER BY st.storeId""",
                                                                              Object[].class)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Store) r[0]).getStoreId(), r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple(1, 92L),
                            tuple(2, 91L));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var i = cq.from(Inventory.class);
                final var st = i.join(Inventory_.store);
                final var r = i.join(Inventory_.rentals);
                cq.select(cb.array(st, cb.count(r)))
                        .where(cb.isNull(r.get(Rental_.returnDate)))
                        .groupBy(st)
                        .orderBy(cb.asc(st.get(Store_.storeId)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Store) r[0]).getStoreId(), r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple(1, 92L),
                            tuple(2, 91L));
        }
    }

    @DisplayName("SAKILA_INVENTORY_I_04: how many copies of each rating each store has")
    @Nested
    class SAKILA_INVENTORY_I_04_Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT st, f.rating, COUNT(i)
                                                                                      FROM Inventory i
                                                                                               JOIN i.store st
                                                                                               JOIN i.film f
                                                                                      GROUP BY st, f.rating
                                                                                      ORDER BY st.storeId, f.rating""",
                                                                              Object[].class)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Store) r[0]).getStoreId(), r -> r[1],
                                r -> ((Number) r[2]).longValue())
                    .containsExactly(
                            tuple(1, Film.Rating.G, 394L),
                            tuple(1, Film.Rating.PG, 444L),
                            tuple(1, Film.Rating.PG_13, 525L),
                            tuple(1, Film.Rating.R, 442L),
                            tuple(1, Film.Rating.NC_17, 465L),
                            tuple(2, Film.Rating.G, 397L),
                            tuple(2, Film.Rating.PG, 480L),
                            tuple(2, Film.Rating.PG_13, 493L),
                            tuple(2, Film.Rating.R, 462L),
                            tuple(2, Film.Rating.NC_17, 479L));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var i = cq.from(Inventory.class);
                final var st = i.join(Inventory_.store);
                final var f = i.join(Inventory_.film);
                cq.select(cb.array(st, f.get(Film_.rating), cb.count(i)))
                        .groupBy(st, f.get(Film_.rating))
                        .orderBy(cb.asc(st.get(Store_.storeId)), cb.asc(f.get(Film_.rating)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Store) r[0]).getStoreId(), r -> r[1],
                                r -> ((Number) r[2]).longValue())
                    .containsExactly(
                            tuple(1, Film.Rating.G, 394L),
                            tuple(1, Film.Rating.PG, 444L),
                            tuple(1, Film.Rating.PG_13, 525L),
                            tuple(1, Film.Rating.R, 442L),
                            tuple(1, Film.Rating.NC_17, 465L),
                            tuple(2, Film.Rating.G, 397L),
                            tuple(2, Film.Rating.PG, 480L),
                            tuple(2, Film.Rating.PG_13, 493L),
                            tuple(2, Film.Rating.R, 462L),
                            tuple(2, Film.Rating.NC_17, 479L));
        }
    }
}
