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

import com.github.jinahya.mysql.sakila.persistence.Inventory;
import com.github.jinahya.mysql.sakila.persistence.Inventory_;
import com.github.jinahya.mysql.sakila.persistence.__Exercises_TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code SAKILA_INVENTORY_B_02} of {@code src/test/sql-exercises/inventory/01_basic.sql}
 * with JPQL and with the Criteria API, or with a native query where neither can.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("SAKILA_INVENTORY_B_02: the copies of the film 1")
@Slf4j
class SAKILA_INVENTORY_B_02_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                  SELECT i
                                                                                  FROM Inventory i
                                                                                  WHERE i.filmId = :filmId
                                                                                  ORDER BY i.inventoryId""",
                                                                          Inventory.class)
                .setParameter("filmId", 1)
                .getResultList());
        list.forEach(e -> log.debug("{}", e));
        assertThat(list)
                .extracting(Inventory::getInventoryId, Inventory::getStoreId)
                .containsExactly(
                        tuple(1, 1), tuple(2, 1), tuple(3, 1), tuple(4, 1),
                        tuple(5, 2), tuple(6, 2), tuple(7, 2), tuple(8, 2));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Inventory.class);
            final var i = cq.from(Inventory.class);
            cq.select(i)
                    .where(cb.equal(i.get(Inventory_.filmId),
                                    cb.parameter(Integer.class, "filmId")))
                    .orderBy(cb.asc(i.get(Inventory_.inventoryId)));
            return em.createQuery(cq)
                    .setParameter("filmId", 1)
                    .getResultList();
        });
        list.forEach(e -> log.debug("{}", e));
        assertThat(list)
                .extracting(Inventory::getInventoryId, Inventory::getStoreId)
                .containsExactly(
                        tuple(1, 1), tuple(2, 1), tuple(3, 1), tuple(4, 1),
                        tuple(5, 2), tuple(6, 2), tuple(7, 2), tuple(8, 2));
    }

}
