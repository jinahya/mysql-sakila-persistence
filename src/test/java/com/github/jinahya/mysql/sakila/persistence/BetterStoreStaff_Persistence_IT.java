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
 * Verifies the mappings of {@link BetterStoreStaff} against the installed {@code sakila} database, with
 * {@code 88-better-store-staff.sql} applied.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class BetterStoreStaff_Persistence_IT
        extends _Entity_Persistence_IT<BetterStoreStaff> {

    BetterStoreStaff_Persistence_IT() {
        super(BetterStoreStaff.class);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @param instance {@inheritDoc}
     * @implNote The store and the staff member, lazy associations, resolve, and the manager flag is
     *         {@code TRUE} or {@code null}, never {@code FALSE}.
     */
    @Override
    void randomTargetInstanceSelected(final BetterStoreStaff instance) {
        assertThat(instance.getStore().getId()).as("id of the store of %s", instance).isNotNull();
        assertThat(instance.getStaff().getId()).as("id of the staff of %s", instance).isNotNull();
        assertThat(instance.getManager()).as("manager of %s", instance).isNotEqualTo(Boolean.FALSE);
    }
}
