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

/**
 * Verifies the mappings of {@link Staff} against the schema generated into the in-memory database.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class Staff_Persistence_Test
        extends _Entity_Persistence_Test<Staff> {

    Staff_Persistence_Test() {
        super(Staff.class);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Does nothing; a {@link Store} and its manager, a {@link Staff}, refer to each other through {@code NOT NULL}
     * columns, so neither is persisted; see {@link Store}.
     */
    @Disabled("a store and its manager refer to each other: neither is persisted")
    @Override
    @Test
    void _persist_RandomizedInstance() {
        // empty
    }

    /**
     * Does nothing; a {@link Store} and its manager, a {@link Staff}, refer to each other through {@code NOT NULL}
     * columns, so neither is persisted; see {@link Store}.
     */
    @Disabled("a store and its manager refer to each other: neither is persisted")
    @Override
    @Test
    void _persist_EqualsReference() {
        // empty
    }

    /**
     * Does nothing; a {@link Store} and its manager, a {@link Staff}, refer to each other through {@code NOT NULL}
     * columns, so neither is persisted; see {@link Store}.
     */
    @Disabled("a store and its manager refer to each other: neither is persisted")
    @Override
    @Test
    void _persist_ReferenceEquals() {
        // empty
    }

    /**
     * Does nothing; a {@link Store} and its manager, a {@link Staff}, refer to each other through {@code NOT NULL}
     * columns, so neither is persisted; see {@link Store}.
     */
    @Disabled("a store and its manager refer to each other: neither is persisted")
    @Override
    @Test
    void _persist_HashSetMembership() {
        // empty
    }
}
