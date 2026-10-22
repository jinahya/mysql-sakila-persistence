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

/**
 * Operations on a {@link BetterStore} which need an entity manager.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public final class BetterStorePersistenceUtils {

    /**
     * Makes the specified staff, who works at the specified store, the manager of it.
     *
     * @param entityManager the entity manager which manages the store.
     * @param store         the store.
     * @param staff         the staff to make the manager; {@code null} for no manager.
     * @throws IllegalArgumentException when the {@code staff} does not work at the {@code store}.
     */
    public static void setManager(final EntityManager entityManager, final BetterStore store,
                                  final BetterStaff staff) {
        // the staff, unless null, has to work at the store
        if (staff != null && (staff.getEmployment() == null || !store.equals(staff.getEmployment().getStore()))) {
            throw new IllegalArgumentException("not a staff of " + store + ": " + staff);
        }
        // unflag the current manager's row, if any, and write it before the new manager's row is flagged; otherwise
        // the unique key of (manager, store_id) could see two managers of the store, depending on the order the
        // provider writes the two rows in
        final var manager = store.getManager();
        if (manager != null) {
            manager.getEmployment().setManager(null);
            entityManager.flush();
        }
        if (staff == null) {
            // no manager
            return;
        }
        // flag the new manager's row
        staff.getEmployment().setManager(Boolean.TRUE);
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    private BetterStorePersistenceUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
