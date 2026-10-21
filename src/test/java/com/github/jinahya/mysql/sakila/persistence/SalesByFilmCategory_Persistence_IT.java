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

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the mappings of {@link SalesByFilmCategory} against the installed {@code sakila} database.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class SalesByFilmCategory_Persistence_IT extends _Entity_Persistence_IT<SalesByFilmCategory> {

    SalesByFilmCategory_Persistence_IT() {
        super(SalesByFilmCategory.class);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Does nothing; {@value SalesByFilmCategory#TABLE_NAME} is a view, which MySQL does not let a row be inserted into.
     */
    @Disabled("a view: nothing is inserted into it")
    @Override
    @Test
    protected void _persist_RandomizedInstance() {
        // empty
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @param instance {@inheritDoc}
     * @implNote The category is there, and its total is not negative.
     */
    @Override
    void randomSelected(final SalesByFilmCategory instance) {
        assertThat(instance.getCategory()).as("category of %s", instance).isNotBlank();
        assertThat(instance.getTotalSales()).as("total sales of %s", instance).isNotNegative();
    }
}
