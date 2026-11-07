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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Verifies the mappings of {@link Customer} against the schema generated into the in-memory
 * database.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Customer_PersistenceTest
        extends _Entity_PersistenceTest<Customer, Integer> {

    Customer_PersistenceTest() {
        super(Customer.class, Integer.class);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Does nothing; the persister of {@link Customer} takes an existing {@link Store} or
     * {@link Staff}, which the in-memory database never has, for neither is persisted; see
     * {@link Store}.
     */
    @Disabled("needs an existing store or staff, and neither is persisted")
    @Override
    @Test
    protected void persistRandomizedEntityInstance__() {
        // empty
    }

    /**
     * Does nothing; the persister of {@link Customer} takes an existing {@link Store} or
     * {@link Staff}, which the in-memory database never has, for neither is persisted; see
     * {@link Store}.
     */
    @Disabled("needs an existing store or staff, and neither is persisted")
    @Override
    @Test
    void _persist_EqualsReference() {
        // empty
    }

    /**
     * Does nothing; the persister of {@link Customer} takes an existing {@link Store} or
     * {@link Staff}, which the in-memory database never has, for neither is persisted; see
     * {@link Store}.
     */
    @Disabled("needs an existing store or staff, and neither is persisted")
    @Override
    @Test
    void _persist_ReferenceEquals() {
        // empty
    }

    /**
     * Does nothing; the persister of {@link Customer} takes an existing {@link Store} or
     * {@link Staff}, which the in-memory database never has, for neither is persisted; see
     * {@link Store}.
     */
    @Disabled("needs an existing store or staff, and neither is persisted")
    @Override
    @Test
    void _persist_HashSetMembership() {
        // empty
    }
}
