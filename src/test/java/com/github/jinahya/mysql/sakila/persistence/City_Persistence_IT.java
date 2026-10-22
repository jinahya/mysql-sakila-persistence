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
 * Verifies the mappings of {@link City} against the installed {@code sakila} database.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class City_Persistence_IT
        extends _Entity_Persistence_IT<City> {

    City_Persistence_IT() {
        super(City.class);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @param instance {@inheritDoc}
     * @implNote The name is there, and the country, a lazy association, resolves.
     */
    @Override
    void randomTargetInstanceSelected(final City instance) {
        assertThat(instance.getCity()).as("name of %s", instance).isNotBlank();
        assertThat(instance.getCountry().getCountry()).as("name of the country of %s", instance).isNotBlank();
    }
}
