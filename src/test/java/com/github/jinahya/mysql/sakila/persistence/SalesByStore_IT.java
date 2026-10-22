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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Reads the installed {@value SalesByStore#TABLE_NAME} view into {@link SalesByStore} instances.
 * <p>
 * {@link SalesByStore} is not an entity, so there is no persistence test to extend; every row is
 * read with a native query, and handed to the public constructor in the order the view selects its
 * columns.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class SalesByStore_IT {

    private static SalesByStore newInstanceOf(final Object[] row) {
        return new SalesByStore((String) row[0], (String) row[1], (BigDecimal) row[2]);
    }

    // ---------------------------------------------------------------------------------------------

    @DisplayName("every installed row reads into a SalesByStore")
    @Test
    void readAll_() {
        @SuppressWarnings("unchecked")
        final List<Object[]> rows = __Persistence_TestUtils.applyEntityManager(
                _Entity_Persistence_IT.PERSISTENCE_UNIT_NAME, em -> em.createNativeQuery(
                        "SELECT " + SalesByStore.COLUMN_NAME_STORE + ", " +
                        SalesByStore.COLUMN_NAME_MANAGER
                        + ", " + SalesByStore.COLUMN_NAME_TOTAL_SALES
                        + " FROM " + SalesByStore.TABLE_NAME
                ).getResultList());
        assertThat(rows).as("rows of %s", SalesByStore.TABLE_NAME).isNotEmpty();
        rows.stream().map(SalesByStore_IT::newInstanceOf).forEach(v -> {
            assertThat(v.getStore()).as("store of %s", v).isNotBlank()
                    .hasSizeLessThanOrEqualTo(SalesByStore.SIZE_MAX_STORE);
            assertThat(v.getTotalSales()).as("total sales of %s", v).isPositive();
        });
    }
}
