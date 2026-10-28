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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers the exercises of {@code src/test/sql/exercises/store_exercises_03_advanced.sql}, against the
 * installed {@code sakila} database; a nested class per exercise, which answers it with JPQL and
 * with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Store_Exercises_03_Advanced_PersistenceIT {

    @DisplayName(
            "SAKILA_STORE_A_01: how many films each store has a copy of, and how many of them only that " +
            "store has")
    @Nested
    class SAKILA_STORE_A_01_Test {

        // JPQL counts no CASE; the films only that store has are a second query
        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var onlyHere = em.createQuery("""
                                                            SELECT st, COUNT(DISTINCT i.film)
                                                            FROM Inventory i
                                                                     JOIN i.store st
                                                            WHERE NOT EXISTS (SELECT o FROM Inventory o WHERE o.film = i.film AND o.storeId <> i.storeId)
                                                            GROUP BY st""", Object[].class)
                        .getResultStream()
                        .collect(Collectors.toMap(r -> ((Store) r[0]).getStoreId(), r -> r[1]));
                return em.createQuery("""
                                              SELECT st, COUNT(DISTINCT i.film)
                                              FROM Inventory i
                                                       JOIN i.store st
                                              GROUP BY st
                                              ORDER BY st.storeId""", Object[].class)
                        .getResultStream()
                        .map(r -> new Object[]{r[0], r[1],
                                               onlyHere.get(((Store) r[0]).getStoreId())})
                        .toList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Store) r[0]).getStoreId(), r -> ((Number) r[1]).longValue(),
                                r -> ((Number) r[2]).longValue())
                    .containsExactly(
                            tuple(1, 759L, 196L),
                            tuple(2, 762L, 199L));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var only = cb.createQuery(Object[].class);
                final var i1 = only.from(Inventory.class);
                final var others = only.subquery(Inventory.class);
                final var o = others.from(Inventory.class);
                others.select(o).where(cb.equal(o.get(Inventory_.film), i1.get(Inventory_.film)),
                                       cb.notEqual(o.get(Inventory_.storeId),
                                                   i1.get(Inventory_.storeId)));
                final var st1 = i1.join(Inventory_.store);
                only.select(cb.array(st1, cb.countDistinct(i1.get(Inventory_.film))))
                        .where(cb.not(cb.exists(others)))
                        .groupBy(st1);
                final var onlyHere = em.createQuery(only).getResultStream()
                        .collect(Collectors.toMap(r -> ((Store) r[0]).getStoreId(), r -> r[1]));
                final var cq = cb.createQuery(Object[].class);
                final var i = cq.from(Inventory.class);
                final var st = i.join(Inventory_.store);
                cq.select(cb.array(st, cb.countDistinct(i.get(Inventory_.film))))
                        .groupBy(st)
                        .orderBy(cb.asc(st.get(Store_.storeId)));
                return em.createQuery(cq).getResultStream()
                        .map(r -> new Object[]{r[0], r[1],
                                               onlyHere.get(((Store) r[0]).getStoreId())})
                        .toList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Store) r[0]).getStoreId(), r -> ((Number) r[1]).longValue(),
                                r -> ((Number) r[2]).longValue())
                    .containsExactly(
                            tuple(1, 759L, 196L),
                            tuple(2, 762L, 199L));
        }
    }

    @DisplayName("SAKILA_STORE_A_02: each store's rentals in each month, and its share of the month")
    @Nested
    class SAKILA_STORE_A_02_Test {

        // JPQL has no window functions; each month's rentals are a second query, and the share is
        // worked out in Java
        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var months = em.createQuery("""
                                                          SELECT EXTRACT(YEAR FROM r.rentalDate), EXTRACT(MONTH FROM r.rentalDate), COUNT(r)
                                                          FROM Rental r
                                                          GROUP BY EXTRACT(YEAR FROM r.rentalDate), EXTRACT(MONTH FROM r.rentalDate)""",
                                                  Object[].class)
                        .getResultStream()
                        .collect(Collectors.toMap(r -> String.format("%d-%02d", r[0], r[1]),
                                                  r -> ((Number) r[2]).longValue()));
                return em.createQuery("""
                                              SELECT EXTRACT(YEAR FROM r.rentalDate), EXTRACT(MONTH FROM r.rentalDate), st, COUNT(r)
                                              FROM Rental r
                                                       JOIN r.inventory i
                                                       JOIN i.store st
                                              GROUP BY EXTRACT(YEAR FROM r.rentalDate), EXTRACT(MONTH FROM r.rentalDate), st
                                              ORDER BY EXTRACT(YEAR FROM r.rentalDate), EXTRACT(MONTH FROM r.rentalDate), st.storeId""",
                                      Object[].class)
                        .getResultStream()
                        .map(r -> share(String.format("%d-%02d", r[0], r[1]), r[2], r[3], months))
                        .toList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Store) r[1]).getStoreId(),
                                r -> ((Number) r[2]).longValue(),
                                r -> ((BigDecimal) r[3]).toPlainString())
                    .containsExactly(
                            tuple("2005-05", 1, 575L, "49.74"),
                            tuple("2005-05", 2, 581L, "50.26"),
                            tuple("2005-06", 1, 1121L, "48.51"),
                            tuple("2005-06", 2, 1190L, "51.49"),
                            tuple("2005-07", 1, 3334L, "49.69"),
                            tuple("2005-07", 2, 3375L, "50.31"),
                            tuple("2005-08", 1, 2801L, "49.26"),
                            tuple("2005-08", 2, 2885L, "50.74"),
                            tuple("2006-02", 1, 92L, "50.55"),
                            tuple("2006-02", 2, 90L, "49.45"));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var mq = cb.createQuery(Object[].class);
                final var r1 = mq.from(Rental.class);
                final var year1 = cb.extract(LocalDateTimeField.YEAR, r1.get(Rental_.rentalDate));
                final var month1 = cb.extract(LocalDateTimeField.MONTH,
                                              r1.get(Rental_.rentalDate));
                mq.select(cb.array(year1, month1, cb.count(r1)))
                        .groupBy(year1, month1);
                final var months = em.createQuery(mq).getResultStream()
                        .collect(Collectors.toMap(r -> String.format("%d-%02d", r[0], r[1]),
                                                  r -> ((Number) r[2]).longValue()));
                final var cq = cb.createQuery(Object[].class);
                final var r = cq.from(Rental.class);
                final var st = r.join(Rental_.inventory).join(Inventory_.store);
                final var year = cb.extract(LocalDateTimeField.YEAR, r.get(Rental_.rentalDate));
                final var month = cb.extract(LocalDateTimeField.MONTH, r.get(Rental_.rentalDate));
                cq.select(cb.array(year, month, st, cb.count(r)))
                        .groupBy(year, month, st)
                        .orderBy(cb.asc(year), cb.asc(month), cb.asc(st.get(Store_.storeId)));
                return em.createQuery(cq).getResultStream()
                        .map(e -> share(String.format("%d-%02d", e[0], e[1]), e[2], e[3], months))
                        .toList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Store) r[1]).getStoreId(),
                                r -> ((Number) r[2]).longValue(),
                                r -> ((BigDecimal) r[3]).toPlainString())
                    .containsExactly(
                            tuple("2005-05", 1, 575L, "49.74"),
                            tuple("2005-05", 2, 581L, "50.26"),
                            tuple("2005-06", 1, 1121L, "48.51"),
                            tuple("2005-06", 2, 1190L, "51.49"),
                            tuple("2005-07", 1, 3334L, "49.69"),
                            tuple("2005-07", 2, 3375L, "50.31"),
                            tuple("2005-08", 1, 2801L, "49.26"),
                            tuple("2005-08", 2, 2885L, "50.74"),
                            tuple("2006-02", 1, 92L, "50.55"),
                            tuple("2006-02", 2, 90L, "49.45"));
        }

        // the month, the store, its rentals, and its percentage of the month's rentals
        private static Object[] share(final String month, final Object store,
                                      final Object rentals, final Map<String, Long> months) {
            final var count = ((Number) rentals).longValue();
            return new Object[]{month, store, count,
                    BigDecimal.valueOf(100L * count)
                            .divide(BigDecimal.valueOf(months.get(month)), 2,
                                    RoundingMode.HALF_UP)};
        }
    }
}
