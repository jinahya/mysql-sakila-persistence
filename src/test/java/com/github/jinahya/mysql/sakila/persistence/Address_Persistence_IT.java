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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the mappings of {@link Address} against the installed {@code sakila} database.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class Address_Persistence_IT extends _Entity_Persistence_IT<Address> {

    Address_Persistence_IT() {
        super(Address.class);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @param instance {@inheritDoc}
     * @implNote Every installed location is a point of {@code SRID} {@value Address#COLUMN_SRID_LOCATION}, in
     * the internal format {@link Address#newLocation(int, double, double)} writes.
     */
    @Override
    protected void randomSelected(final Address instance) {
        final var location = instance.getLocation();
        assertThat(location)
                .as("location of %s", instance)
                .hasSize(Address.LOCATION_POINT_BYTES);
        final var buffer = ByteBuffer.wrap(location).order(ByteOrder.LITTLE_ENDIAN);
        assertThat(buffer.getInt()).as("SRID of the location of %s", instance).isEqualTo(Address.COLUMN_SRID_LOCATION);
        buffer.get(); // byte order
        assertThat(buffer.getInt()).as("geometry type of the location of %s", instance).isEqualTo(1);
    }
}
