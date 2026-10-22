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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the mappings of {@link ActorInfo} against the installed {@code sakila} database.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class ActorInfo_Persistence_IT
        extends _Entity_Persistence_IT<ActorInfo> {

    ActorInfo_Persistence_IT() {
        super(ActorInfo.class);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Does nothing; {@value ActorInfo#TABLE_NAME} is a view, which MySQL does not let a row be
     * inserted into.
     */
    @Disabled("a view: nothing is inserted into it")
    @Override
    @Test
    void persistTargetInstance() {
        // empty
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @param instance {@inheritDoc}
     * @implNote Both names are there.
     */
    @Override
    void randomTargetInstanceSelected(final ActorInfo instance) {
        assertThat(instance.getFirstName()).as("first name of %s", instance).isNotBlank();
        assertThat(instance.getLastName()).as("last name of %s", instance).isNotBlank();
    }
}
