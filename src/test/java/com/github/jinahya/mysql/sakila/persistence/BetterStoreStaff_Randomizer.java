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
 * A randomizer which produces randomized {@link BetterStoreStaff} instances.
 * <p>
 * The {@code id}, which the database generates, and the {@code store} and {@code staff} associations are excluded
 * from randomization; {@link BetterStoreStaff_Persister} supplies both associations. The {@code manager} is, at random,
 * {@link Boolean#TRUE} or {@code null}, never {@link Boolean#FALSE}, as the table's check constraint has it.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class BetterStoreStaff_Randomizer
        extends PodamObjectRandomizer<BetterStoreStaff> {

    BetterStoreStaff_Randomizer() {
        super(BetterStoreStaff.class, List.of(
                BetterStoreStaff.ATTRIBUTE_NAME_ID,
                BetterStoreStaff.ATTRIBUTE_NAME_STORE,
                BetterStoreStaff.ATTRIBUTE_NAME_STAFF,
                BetterStoreStaff.ATTRIBUTE_NAME_MANAGER
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
    public BetterStoreStaff get() {
        final var instance = super.get();
        instance.setManager(ThreadLocalRandom.current().nextBoolean() ? Boolean.TRUE : null);
        return instance;
    }
}
