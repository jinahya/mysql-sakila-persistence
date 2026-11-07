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
import com.github.jinahya.mysql.sakila.persistence.__Exercises_TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code SAKILA_INVENTORY_A_02} of {@code src/test/sql-exercises/inventory/03_advanced.sql}
 * with JPQL and with the Criteria API, or with a native query where neither can.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("SAKILA_INVENTORY_A_02: how many films have had every one of their copies rented")
@Slf4j
class SAKILA_INVENTORY_A_02_PersistenceIT {

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
