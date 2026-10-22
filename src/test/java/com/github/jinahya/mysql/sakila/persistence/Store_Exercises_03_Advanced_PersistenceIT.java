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
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers the exercises of {@code src/test/sql/store_exercises_03_advanced.sql}, against the
 * installed {@code sakila} database; a nested class per exercise, which answers it with JPQL and
 * with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Store_Exercises_03_Advanced_PersistenceIT {

    @DisplayName("exercise 1: how many films each store has a copy of, and how many of them only that store has")
    @Nested
    class Exercise1Test {

        // JPQL counts no CASE; the films only that store has are a second query
        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var onlyHere = em.createQuery("""
                        SELECT i.storeId, COUNT(DISTINCT i.film)
                        FROM Inventory i
                        WHERE NOT EXISTS (SELECT o FROM Inventory o WHERE o.film = i.film AND o.storeId <> i.storeId)
                        GROUP BY i.storeId""", Object[].class)
                        .getResultStream().collect(Collectors.toMap(r -> r[0], r -> r[1]));
                return em.createQuery("""
                                SELECT i.storeId, COUNT(DISTINCT i.film)
                                FROM Inventory i
                                GROUP BY i.storeId
                                ORDER BY i.storeId""", Object[].class)
                        .getResultStream()
                        .map(r -> new Object[]{r[0], r[1], onlyHere.get(r[0])})
                        .toList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> ((Number) r[1]).longValue(), r -> ((Number) r[2]).longValue())
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
                                       cb.notEqual(o.get(Inventory_.storeId), i1.get(Inventory_.storeId)));
                only.select(cb.array(i1.get(Inventory_.storeId), cb.countDistinct(i1.get(Inventory_.film))))
                        .where(cb.not(cb.exists(others)))
                        .groupBy(i1.get(Inventory_.storeId));
                final var onlyHere = em.createQuery(only).getResultStream().collect(Collectors.toMap(r -> r[0], r -> r[1]));
                final var cq = cb.createQuery(Object[].class);
                final var i = cq.from(Inventory.class);
                cq.select(cb.array(i.get(Inventory_.storeId), cb.countDistinct(i.get(Inventory_.film))))
                        .groupBy(i.get(Inventory_.storeId))
                        .orderBy(cb.asc(i.get(Inventory_.storeId)));
                return em.createQuery(cq).getResultStream()
                        .map(r -> new Object[]{r[0], r[1], onlyHere.get(r[0])})
                        .toList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> ((Number) r[1]).longValue(), r -> ((Number) r[2]).longValue())
                    .containsExactly(
                            tuple(1, 759L, 196L),
                            tuple(2, 762L, 199L));
        }
    }
}
