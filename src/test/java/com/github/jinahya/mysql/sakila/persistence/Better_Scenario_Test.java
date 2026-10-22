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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Scenarios of the {@code better_*} entities, without a persistence context.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class Better_Scenario_Test {

    @DisplayName("addStaff(staff) to store1, and then to store2")
    @Nested
    class AddStaffStore1ThenStore2Test {

        @DisplayName("moves the staff to store2")
        @Test
        void __() {
            // one staff, and two stores; spies, which run the real methods and record the calls
            final var staff = spy(new BetterStaff());
            final var store1 = spy(new BetterStore());
            final var store2 = spy(new BetterStore());
            // add the staff to store1
            store1.addStaff(staff);
            assertThat(staff.getEmployment().getStore()).isSameAs(store1);
            assertThat(store1.getEmployments()).extracting(BetterEmployment::getStaff).containsExactly(staff);
            // and then to store2
            store2.addStaff(staff);
            assertThat(staff.getEmployment().getStore()).isSameAs(store2);
            assertThat(store1.getEmployments()).isEmpty();
            assertThat(store2.getEmployments()).extracting(BetterEmployment::getStaff).containsExactly(staff);
        }
    }

    @DisplayName("setManager(staff1), and then setManager(staff2)")
    @Nested
    class SetManagerStaff1ThenStaff2Test {

        @DisplayName("replaces staff1 with staff2 as the manager")
        @Test
        void _setManager_Staff1ThenStaff2() {
            // one store, and two staff of it
            final var store = new BetterStore();
            final var staff1 = new BetterStaff();
            final var staff2 = new BetterStaff();
            store.addStaff(staff1);
            store.addStaff(staff2);
            // a mock, which only records the flushes
            final var entityManager = mock(EntityManager.class);
            // set staff1 as the manager; no manager to unflag, so no flush
            BetterStorePersistenceUtils.setManager(entityManager, store, staff1);
            assertThat(store.getManager()).isSameAs(staff1);
            verify(entityManager, never()).flush();
            // and then staff2; staff1's row is unflagged, and flushed, first
            BetterStorePersistenceUtils.setManager(entityManager, store, staff2);
            assertThat(store.getManager()).isSameAs(staff2);
            assertThat(staff1.getEmployment().getManager()).isNull();
            verify(entityManager, times(1)).flush();
        }
    }
}
