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
 * A randomizer which produces randomized {@link Staff} instances.
 * <p>
 * The {@code staffId}, which the database generates, the {@code lastUpdate}, which the database
 * fills, and the {@code address} and {@code store} associations are excluded from randomization;
 * {@link Staff_Persister} supplies both associations. The {@code picture} is replaced with a few
 * random bytes, rather than as many as the column holds.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class Staff_Randomizer
        extends PodamObjectRandomizer<Staff> {

    Staff_Randomizer() {
        super(Staff.class, List.of(
                Staff.ATTRIBUTE_NAME_STAFF_ID,
                Staff.ATTRIBUTE_NAME_ADDRESS,
                Staff.ATTRIBUTE_NAME_STORE,
                Staff.ATTRIBUTE_NAME_LAST_UPDATE
        ));
    }

    // ---------------------------------------------------------------------------------------------
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
    public Staff get() {
        final var instance = super.get();
        final var random = ThreadLocalRandom.current();
        if (random.nextBoolean()) {
            final var picture = new byte[random.nextInt(1, 128)];
            random.nextBytes(picture);
            instance.setPicture(picture);
        } else {
            instance.setPicture(null);
        }
        return instance;
    }
}
