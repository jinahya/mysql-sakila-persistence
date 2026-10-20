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

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Verifies the mappings of {@link Payment} against the schema generated into the in-memory database.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class Payment_Persistence_Test extends _Entity_Persistence_Test<Payment> {

    Payment_Persistence_Test() {
        super(Payment.class);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Aborts, rather than fails, when the in-memory database lacks a {@link Store} or a {@link Staff}.
     *
     * @implNote A store and its manager refer to each other through {@code NOT NULL} columns whose values are both
     * generated, so neither can be persisted first, and the generated schema starts empty. The persister of this
     * entity gives it an existing store or staff member -- see {@link Store_Persister#selectFirst(EntityManager)} and
     * {@link Staff_Persister#selectFirst(EntityManager)} -- which only the installed database has; the
     * {@code *_Persistence_IT} counterpart is what runs these against it.
     */
    @BeforeEach
    void assumeStoreAndStaffExist() {
        final Boolean present = applyEntityManagerInTransactionAndRollback(
                em -> Store_Persister.selectFirst(em).isPresent() && Staff_Persister.selectFirst(em).isPresent()
        );
        assumeTrue(present, "no store, or no staff, in the in-memory database");
    }
}
