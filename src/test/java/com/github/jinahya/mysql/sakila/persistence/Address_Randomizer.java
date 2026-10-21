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

import com.github.jinahya.object.randomizer.PodamObjectRandomizer;
import uk.co.jemos.podam.api.ClassInfoStrategy;
import uk.co.jemos.podam.api.DataProviderStrategy;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A randomizer which produces randomized {@link Address} instances.
 * <p>
 * The {@code addressId}, which the database generates, the {@code lastUpdate}, which the database fills, the
 * {@code city} association, and the {@code location} are excluded from randomization; {@link Address_Persister}
 * supplies the {@code city}, and {@link #get()} sets the {@code location} with a point, for MySQL rejects bytes which
 * are not a geometry.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class Address_Randomizer
        extends PodamObjectRandomizer<Address> {

    Address_Randomizer() {
        super(Address.class, List.of(
                Address.ATTRIBUTE_NAME_ADDRESS_ID,
                Address.ATTRIBUTE_NAME_CITY,
                Address.ATTRIBUTE_NAME_LOCATION,
                Address.ATTRIBUTE_NAME_LAST_UPDATE
        ));
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    protected DataProviderStrategy getDataProviderStrategy() {
        return super.getDataProviderStrategy();
    }

    @Override
    protected ClassInfoStrategy getClassInfoStrategy() {
        return super.getClassInfoStrategy();
    }

    @Override
    protected PodamFactory getPodamFactory() {
        return super.getPodamFactory();
    }

    @Override
    public Address get() {
        final var instance = super.get();
        final var random = ThreadLocalRandom.current();
        instance.setLocation(Address.newLocation(
                Address.COLUMN_SRID_LOCATION,
                random.nextDouble(Address.LOCATION_X_MIN, Address.LOCATION_X_MAX),
                random.nextDouble(Address.LOCATION_Y_MIN, Address.LOCATION_Y_MAX)
        ));
        return instance;
    }
}
