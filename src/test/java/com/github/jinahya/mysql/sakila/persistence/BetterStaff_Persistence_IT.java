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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the mappings of {@link BetterStaff} against the installed {@code sakila} database, with
 * {@code 88-better-employment.sql} applied.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class BetterStaff_Persistence_IT
        extends _Entity_Persistence_IT<BetterStaff> {

    BetterStaff_Persistence_IT() {
        super(BetterStaff.class);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @param instance {@inheritDoc}
     * @implNote The identifier and the name are there.
     */
    @Override
    void randomTargetInstanceSelected(final BetterStaff instance) {
        assertThat(instance.getId()).as("id of %s", instance).isNotNull();
        assertThat(instance.getName()).as("name of %s", instance).isNotBlank();
    }
}
