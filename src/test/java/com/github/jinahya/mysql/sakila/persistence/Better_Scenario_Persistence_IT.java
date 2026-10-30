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
import com.github.jinahya.persistence.util.JinahyaEntityManagerUtils;
import com.github.jinahya.persistence.util.JinahyaPersistenceUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Scenarios of the {@code better_*} entities, against the installed {@code sakila} database, with
 * {@code 88-better-employment.sql} applied.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class Better_Scenario_Persistence_IT {

    @DisplayName("addStaff(staff) to store1, and then to store2")
    @Nested
    class AddStaffStore1ThenStore2Test {

        @DisplayName("moves the staff to store2")
        @Test
        void _addStaff_Store1ThenStore2() {
            JinahyaPersistenceUtils.applyEntityManager(
                    _Entity_Persistence_IT.PERSISTENCE_UNIT_NAME,
                    m -> JinahyaEntityManagerUtils.applyInTransaction(m, em -> {
                        // one staff, and two stores
                        final var staff =
                                EntityPersisterUtils.newPersistedInstanceOf(em, BetterStaff.class);
                        final var store1 =
                                EntityPersisterUtils.newPersistedInstanceOf(em, BetterStore.class);
                        final var store2 =
                                EntityPersisterUtils.newPersistedInstanceOf(em, BetterStore.class);
                        em.flush(); // EclipseLink assigns an IDENTITY value only at the flush;
                        // setStaff copies it
                        // add the staff to store1
                        store1.addStaff(staff);
                        em.flush();
                        // and then to store2
                        store2.addStaff(staff);
                        em.flush();
                        // read the staff's row back from the database
                        em.clear();
                        final var employment = em
                                .createNamedQuery("BetterEmployment.selectOneByStaff",
                                                  BetterEmployment.class)
                                .setParameter("staff", em.find(BetterStaff.class, staff.getId()))
                                .getSingleResult();
                        assertThat(employment.getStore().getId())
                                .as("store id of %s", employment)
                                .isEqualTo(store2.getId());
                        return null;
                    }, true));
        }
    }

    @DisplayName("setManager(staff1), and then setManager(staff2)")
    @Nested
    class SetManagerStaff1ThenStaff2Test {

        @DisplayName("replaces staff1 with staff2 as the manager")
        @Test
        void _setManager_Staff1ThenStaff2() {
            JinahyaPersistenceUtils.applyEntityManager(
                    _Entity_Persistence_IT.PERSISTENCE_UNIT_NAME,
                    m -> JinahyaEntityManagerUtils.applyInTransaction(m, em -> {
                        // one store, and two staff of it
                        final var store =
                                EntityPersisterUtils.newPersistedInstanceOf(em, BetterStore.class);
                        final var staff1 =
                                EntityPersisterUtils.newPersistedInstanceOf(em, BetterStaff.class);
                        final var staff2 =
                                EntityPersisterUtils.newPersistedInstanceOf(em, BetterStaff.class);
                        em.flush(); // EclipseLink assigns an IDENTITY value only at the flush;
                        // setStaff copies it
                        store.addStaff(staff1);
                        store.addStaff(staff2);
                        em.flush();
                        // set staff1 as the manager
                        BetterStorePersistenceUtils.setManager(em, store, staff1);
                        em.flush();
                        // and then staff2
                        BetterStorePersistenceUtils.setManager(em, store, staff2);
                        em.flush();
                        // read the store's manager back from the database
                        em.clear();
                        final var found = em.find(BetterStore.class, store.getId());
                        assertThat(found.getManager().getId())
                                .as("staff id of the manager of %s", found)
                                .isEqualTo(staff2.getId());
                        return null;
                    }, true));
        }
    }
}
