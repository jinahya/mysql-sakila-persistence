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
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HexFormat;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the static members of {@link Address}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class Address_StaticTest {

    @DisplayName("newLocation(0, 1, 2) is the bytes MySQL stores POINT(1 2) as")
    @Test
    void newLocation_SameAsMysql_() {
        // SELECT HEX(ST_GeomFromText('POINT(1 2)', 0));
        final var expected = HexFormat.of().parseHex("000000000101000000000000000000F03F0000000000000040");
        assertThat(Address.newLocation(Address.COLUMN_SRID_LOCATION, 1.0d, 2.0d)).isEqualTo(expected);
    }

    @DisplayName("newLocation puts the SRID and the coordinates where MySQL reads them")
    @Test
    void newLocation_() {
        final var location = Address.newLocation(4326, -73.98d, 40.75d);
        assertThat(location).hasSize(Address.LOCATION_POINT_BYTES);
        final var buffer = ByteBuffer.wrap(location).order(ByteOrder.LITTLE_ENDIAN);
        assertThat(buffer.getInt()).isEqualTo(4326);
        assertThat(buffer.get()).isEqualTo((byte) 1);
        assertThat(buffer.getInt()).isEqualTo(1);
        assertThat(buffer.getDouble()).isEqualTo(-73.98d);
        assertThat(buffer.getDouble()).isEqualTo(40.75d);
    }
}
