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
import org.geolatte.geom.GeometryType;
import org.geolatte.geom.Point;
import org.geolatte.geom.codec.Wkb;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the mappings of {@link Address} against the installed {@code sakila} database.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Address_Persistence_IT
        extends _Entity_Persistence_IT<Address> {

    Address_Persistence_IT() {
        super(Address.class);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @param instance {@inheritDoc}
     * @implNote Every installed location is a point of {@code SRID} {@value Address#COLUMN_SRID_LOCATION}, in
     *         the internal format {@link Address#newLocation(int, double, double)} writes, which is decoded with
     *         {@link Wkb.Dialect#MYSQL_WKB}. The {@code SRID} is read from the first four bytes, for the decoder takes
     *         {@code 0} for an undefined reference system, whose {@code SRID} is {@code -1}. The coordinates are not
     *         checked: 597 of the 603 installed points are a longitude and a latitude, but the other six have one
     *         coordinate in the hundreds of millions.
     */
    @Override
    void randomSelected(final Address instance) {
        {
            final var location = instance.getLocation();
            assertThat(location)
                    .as("location of %s", instance)
                    .hasSize(Address.LOCATION_POINT_BYTES);
            final var srid = instance.getLocationAs(v -> ByteBuffer.wrap(v).order(ByteOrder.LITTLE_ENDIAN).getInt());
            assertThat(srid)
                    .as("SRID of the location of %s", instance)
                    .isEqualTo(Address.COLUMN_SRID_LOCATION);
            final var geometry = instance.getLocationAs(
                    v -> Wkb.fromWkb(org.geolatte.geom.ByteBuffer.from(v), Wkb.Dialect.MYSQL_WKB)
            );
            log.debug("geometry: {}", geometry);
            assertThat(geometry.getGeometryType())
                    .as("geometry type of the location of %s", instance)
                    .isEqualTo(GeometryType.POINT);
            assertThat(geometry)
                    .as("location of %s", instance)
                    .isInstanceOf(Point.class)
                    .satisfies(g -> assertThat(g.isEmpty()).as("empty?").isFalse());
        }
    }
}
