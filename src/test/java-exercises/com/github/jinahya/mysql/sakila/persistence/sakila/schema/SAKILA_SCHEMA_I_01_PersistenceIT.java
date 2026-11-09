package com.github.jinahya.mysql.sakila.persistence.sakila.schema;

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

import com.github.jinahya.mysql.sakila.persistence.Inventory_;
import com.github.jinahya.mysql.sakila.persistence.Payment;
import com.github.jinahya.mysql.sakila.persistence.Payment_;
import com.github.jinahya.mysql.sakila.persistence.Rental_;
import com.github.jinahya.mysql.sakila.persistence.Store;
import com.github.jinahya.mysql.sakila.persistence.Store_;
import com.github.jinahya.mysql.sakila.persistence.__Exercises_TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code SAKILA_SCHEMA_I_01} of {@code src/test/sql-exercises/__/02_intermediate.sql} with
 * JPQL and with the Criteria API, or with a native query where neither can.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("SAKILA_SCHEMA_I_01: how much each store took, through the copies it rented out")
@Slf4j
class SAKILA_SCHEMA_I_01_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(em -> em.createQuery(
                        """
                                SELECT st, COUNT(p), SUM(p.amount)
                                FROM Payment p
                                         JOIN p.rental r
                                         JOIN r.inventory i
                                         JOIN i.store st
                                GROUP BY st
                                ORDER BY st.storeId""",
                        Object[].class
                )
                .getResultList());
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> ((Store) r[0]).getStoreId(), r -> ((Number) r[1]).longValue(),
                            r -> ((BigDecimal) r[2]).toPlainString())
                .containsExactly(
                        tuple(1, 7923L, "33679.79"),
                        tuple(2, 8121L, "33726.77"));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Object[].class);
            final var p = cq.from(Payment.class);
            final var st = p.join(Payment_.rental)
                    .join(Rental_.inventory)
                    .join(Inventory_.store);
            cq.select(cb.array(st, cb.count(p), cb.sum(p.get(Payment_.amount))))
                    .groupBy(st)
                    .orderBy(cb.asc(st.get(Store_.storeId)));
            return em.createQuery(cq)
                    .getResultList();
        });
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> ((Store) r[0]).getStoreId(),
                            r -> ((Number) r[1]).longValue(),
                            r -> ((BigDecimal) r[2]).toPlainString()
                )
                .containsExactly(
                        tuple(1, 7923L, "33679.79"),
                        tuple(2, 8121L, "33726.77")
                );
    }
}
