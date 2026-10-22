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

import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Reads the installed {@value NicerButSlowerFilmList#TABLE_NAME} view into {@link NicerButSlowerFilmList} instances.
 * <p>
 * {@link NicerButSlowerFilmList} is not an entity, so there is no persistence test to extend; every row is read with a
 * native query, and handed to the public constructor in the order the view selects its columns.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class NicerButSlowerFilmList_IT {

    private static @Nullable Integer integerOf(final @Nullable Object value) {
        return value == null ? null : ((Number) value).intValue();
    }

    private static NicerButSlowerFilmList newInstanceOf(final Object[] row) {
        return new NicerButSlowerFilmList(
                ((Number) row[0]).intValue(),
                (String) row[1],
                (String) row[2],
                (String) row[3],
                (BigDecimal) row[4],
                integerOf(row[5]),
                row[6] == null ? null : Film.Rating.valueOfColumnValue((String) row[6]),
                (String) row[7]
        );
    }

    // -----------------------------------------------------------------------------------------------------------------

    @DisplayName("every installed row reads into a NicerButSlowerFilmList")
    @Test
    void readAll_() {
        @SuppressWarnings("unchecked")
        final List<Object[]> rows = __Persistence_TestUtils.applyEntityManager(
                _Entity_Persistence_IT.PERSISTENCE_UNIT_NAME, em -> em.createNativeQuery(
                "SELECT " + String.join(", ",
                                        NicerButSlowerFilmList.COLUMN_NAME_FID,
                                        NicerButSlowerFilmList.COLUMN_NAME_TITLE,
                                        NicerButSlowerFilmList.COLUMN_NAME_DESCRIPTION,
                                        NicerButSlowerFilmList.COLUMN_NAME_CATEGORY,
                                        NicerButSlowerFilmList.COLUMN_NAME_PRICE,
                                        NicerButSlowerFilmList.COLUMN_NAME_LENGTH,
                                        NicerButSlowerFilmList.COLUMN_NAME_RATING,
                                        NicerButSlowerFilmList.COLUMN_NAME_ACTORS)
                + " FROM " + NicerButSlowerFilmList.TABLE_NAME
        ).getResultList());
        assertThat(rows).as("rows of %s", NicerButSlowerFilmList.TABLE_NAME).isNotEmpty();
        rows.stream().map(NicerButSlowerFilmList_IT::newInstanceOf).forEach(v -> {
            assertThat(v.getFid()).as("fid of %s", v).isNotNull();
            assertThat(v.getTitle()).as("title of %s", v).isNotBlank()
                    .hasSizeLessThanOrEqualTo(NicerButSlowerFilmList.SIZE_MAX_TITLE);
            assertThat(v.getPrice()).as("price of %s", v).isNotNull();
        });
    }
}
