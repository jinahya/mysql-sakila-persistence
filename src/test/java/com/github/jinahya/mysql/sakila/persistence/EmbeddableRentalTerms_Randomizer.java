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

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A randomizer which produces randomized {@link EmbeddableRentalTerms} instances.
 * <p>
 * Nothing is excluded from randomization; {@link #get()} then puts every value within the range, and at the scale, its
 * column allows, as {@link Film_Randomizer} does for the same columns.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class EmbeddableRentalTerms_Randomizer
        extends PodamObjectRandomizer<EmbeddableRentalTerms> {

    EmbeddableRentalTerms_Randomizer() {
        super(EmbeddableRentalTerms.class, List.of(

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
    public EmbeddableRentalTerms get() {
        final var instance = super.get();
        final var random = ThreadLocalRandom.current();
        instance.setRentalDuration(
                random.nextInt(Film.ATTRIBUTE_MIN_RENTAL_DURATION, Film.ATTRIBUTE_MAX_RENTAL_DURATION + 1));
        instance.setRentalRate(BigDecimal.valueOf(random.nextInt(0, 10000), Film.COLUMN_SCALE_RENTAL_RATE));
        instance.setReplacementCost(
                BigDecimal.valueOf(random.nextInt(0, 100000), Film.COLUMN_SCALE_REPLACEMENT_COST));
        return instance;
    }
}
