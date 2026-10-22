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

/**
 * A randomizer which produces randomized {@link BetterStaff} instances.
 * <p>
 * The {@code id}, which the database generates, and the {@code employments}, which only a
 * {@link BetterEmployment} row sets, are excluded from randomization.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class BetterStaff_Randomizer
        extends PodamObjectRandomizer<BetterStaff> {

    BetterStaff_Randomizer() {
        super(BetterStaff.class, List.of(
                BetterStaff.ATTRIBUTE_NAME_ID,
                BetterStaff.ATTRIBUTE_NAME_EMPLOYMENTS
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
    public BetterStaff get() {
        return super.get();
    }
}
