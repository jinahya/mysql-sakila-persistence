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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A randomizer which produces randomized {@link EmbeddableRentalPeriod} instances.
 * <p>
 * Both dates are excluded from randomization and set by {@link #get()}: the rental date to now, and the return date to
 * either {@code null}, for a copy not returned yet, or a few days after the rental date.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class EmbeddableRentalPeriod_Randomizer
        extends PodamObjectRandomizer<EmbeddableRentalPeriod> {

    EmbeddableRentalPeriod_Randomizer() {
        super(EmbeddableRentalPeriod.class, List.of(
                EmbeddableRentalPeriod.ATTRIBUTE_NAME_RENTAL_DATE,
                EmbeddableRentalPeriod.ATTRIBUTE_NAME_RETURN_DATE
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
    public EmbeddableRentalPeriod get() {
        final var instance = super.get();
        final var random = ThreadLocalRandom.current();
        final var rentalDate = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        instance.setRentalDate(rentalDate);
        instance.setReturnDate(random.nextBoolean() ? null : rentalDate.plusDays(random.nextLong(1L, 10L)));
        return instance;
    }
}
