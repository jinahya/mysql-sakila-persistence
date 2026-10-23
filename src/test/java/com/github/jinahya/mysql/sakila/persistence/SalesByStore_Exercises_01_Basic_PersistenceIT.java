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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Answers the exercises of {@code src/test/sql/sales_by_store_exercises_01_basic.sql}, against the
 * installed {@code sakila} database; a nested class per exercise, which answers it with JPQL and
 * with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class SalesByStore_Exercises_01_Basic_PersistenceIT {

    @DisplayName("exercise 1: every store's sales")
    @Nested
    class Exercise1Test {

        // the view is not an entity; the SalesByStore class is a plain class, read with a native
        // query
        @Test
        void nativeQuery() {
            final var list = __Exercises_TestUtils.apply(em -> (List<?>) em.createNativeQuery("""
                                                                                                      SELECT store, manager, total_sales
                                                                                                      FROM sales_by_store""")
                    .getResultList());
            list.forEach(r -> log.debug("{}", r instanceof Object[] a ? Arrays.toString(a) : r));
            assertThat(list).extracting(r -> ((Object[]) r)[0])
                    .containsExactly("Woodridge,Australia", "Lethbridge,Canada");
        }
    }

    @DisplayName("exercise 2: how far apart the two stores' sales are")
    @Nested
    class Exercise2Test {

        // the view is not an entity; the SalesByStore class is a plain class, read with a native
        // query
        @Test
        void nativeQuery() {
            final var list = __Exercises_TestUtils.apply(em -> (List<?>) em.createNativeQuery("""
                                                                                                      SELECT MAX(total_sales) - MIN(total_sales) AS difference
                                                                                                      FROM sales_by_store""")
                    .getResultList());
            list.forEach(r -> log.debug("{}", r instanceof Object[] a ? Arrays.toString(a) : r));
            assertThat(list).singleElement()
                    .satisfies(v -> assertThat(((BigDecimal) v).toPlainString())
                            .isEqualTo("46.98"));
        }
    }
}
