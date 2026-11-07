package com.github.jinahya.mysql.sakila.persistence.sakila.inventory;

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

import com.github.jinahya.mysql.sakila.persistence.Film;
import com.github.jinahya.mysql.sakila.persistence.Film_;
import com.github.jinahya.mysql.sakila.persistence.Inventory;
import com.github.jinahya.mysql.sakila.persistence.Inventory_;
import com.github.jinahya.mysql.sakila.persistence.Store;
import com.github.jinahya.mysql.sakila.persistence.Store_;
import com.github.jinahya.mysql.sakila.persistence.__Exercises_TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code SAKILA_INVENTORY_B_03} of {@code src/test/sql-exercises/inventory/01_basic.sql}
 * with JPQL and with the Criteria API, or with a native query where neither can.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("SAKILA_INVENTORY_B_03: how many copies of the films 1 to 5 each store has")
@Slf4j
class SAKILA_INVENTORY_B_03_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                  SELECT f, st, COUNT(i)
                                                                                  FROM Inventory i
                                                                                           JOIN i.film f
                                                                                           JOIN i.store st
                                                                                  WHERE f.filmId BETWEEN :from AND :to
                                                                                  GROUP BY f, st
                                                                                  ORDER BY f.filmId, st.storeId""",
                                                                          Object[].class)
                .setParameter("from", 1)
                .setParameter("to", 5)
                .getResultList());
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> ((Film) r[0]).getFilmId(), r -> ((Store) r[1]).getStoreId(),
                            r -> ((Number) r[2]).longValue())
                .containsExactly(
                        tuple(1, 1, 4L),
                        tuple(1, 2, 4L),
                        tuple(2, 2, 3L),
                        tuple(3, 2, 4L),
                        tuple(4, 1, 4L),
                        tuple(4, 2, 3L),
                        tuple(5, 2, 3L));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Object[].class);
            final var i = cq.from(Inventory.class);
            final var f = i.join(Inventory_.film);
            final var st = i.join(Inventory_.store);
            cq.select(cb.array(f, st, cb.count(i)))
                    .where(cb.between(f.get(Film_.filmId),
                                      cb.parameter(Integer.class, "from"),
                                      cb.parameter(Integer.class, "to")))
                    .groupBy(f, st)
                    .orderBy(cb.asc(f.get(Film_.filmId)), cb.asc(st.get(Store_.storeId)));
            return em.createQuery(cq)
                    .setParameter("from", 1)
                    .setParameter("to", 5)
                    .getResultList();
        });
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> ((Film) r[0]).getFilmId(), r -> ((Store) r[1]).getStoreId(),
                            r -> ((Number) r[2]).longValue())
                .containsExactly(
                        tuple(1, 1, 4L),
                        tuple(1, 2, 4L),
                        tuple(2, 2, 3L),
                        tuple(3, 2, 4L),
                        tuple(4, 1, 4L),
                        tuple(4, 2, 3L),
                        tuple(5, 2, 3L));
    }

}
