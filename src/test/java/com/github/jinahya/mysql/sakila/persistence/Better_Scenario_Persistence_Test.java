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

import com.github.jinahya.persistence.test.util.EntityPersisterUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Scenarios of the {@link Better} entities, against the schema generated into the in-memory database.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class Better_Scenario_Persistence_Test {

    @DisplayName("addStaff(staff) to store1, and then to store2")
    @Nested
    class AddStaffStore1ThenStore2Test {

        @DisplayName("moves the staff to store2")
        @Test
        void _addStaff_Store1ThenStore2() {
            __Persistence_TestUtils.applyEntityManagerInTransactionAndRollback(
                    _Entity_Persistence_Test.PERSISTENCE_UNIT_NAME, em -> {
                        // one staff, and two stores
                        final var staff = EntityPersisterUtils.newPersistedInstanceOf(em, BetterStaff.class);
                        final var store1 = EntityPersisterUtils.newPersistedInstanceOf(em, BetterStore.class);
                        final var store2 = EntityPersisterUtils.newPersistedInstanceOf(em, BetterStore.class);
                        em.flush(); // EclipseLink assigns an IDENTITY value only at the flush; setStaff copies it
                        // add the staff to store1
                        store1.addStaff(staff);
                        em.flush();
                        // and then to store2
                        store2.addStaff(staff);
                        em.flush();
                        // read the staff's row back from the database
                        em.clear();
                        final var storeStaff = em
                                .createNamedQuery("BetterStoreStaff.selectOneByStaff", BetterStoreStaff.class)
                                .setParameter("staff", em.find(BetterStaff.class, staff.getId()))
                                .getSingleResult();
                        assertThat(storeStaff.getStore().getId())
                                .as("store id of %s", storeStaff)
                                .isEqualTo(store2.getId());
                        return null;
                    });
        }
    }
}
