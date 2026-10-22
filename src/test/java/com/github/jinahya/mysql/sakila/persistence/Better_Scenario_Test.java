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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Scenarios of the {@link Better} entities, without a persistence context.
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
            assertThat(staff.getStoreStaff().getStore()).isSameAs(store1);
            assertThat(store1.getStoreStaffs()).extracting(BetterStoreStaff::getStaff).containsExactly(staff);
            // and then to store2
            store2.addStaff(staff);
            // which fired the staff at store1 first
            verify(store1, times(1)).removeStaff(staff);
            assertThat(staff.getStoreStaff().getStore()).isSameAs(store2);
            assertThat(store1.getStoreStaffs()).isEmpty();
            assertThat(store2.getStoreStaffs()).extracting(BetterStoreStaff::getStaff).containsExactly(staff);
        }
    }
}
