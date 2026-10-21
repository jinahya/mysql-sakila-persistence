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
 * A randomizer which produces randomized {@link Rental} instances.
 * <p>
 * The {@code rentalId}, which the database generates, the {@code rentalDate}, which a trigger sets to {@code NOW()},
 * the {@code lastUpdate}, which the database fills, and every association are excluded from randomization;
 * {@link Rental_Persister} supplies the {@code inventory}, the {@code customer} and the {@code staff}. The
 * {@code returnDate} is assigned after the fact: some days from now, after the rental the trigger dates, or not yet.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class Rental_Randomizer
        extends PodamObjectRandomizer<Rental> {

    Rental_Randomizer() {
        super(Rental.class, List.of(
                Rental.ATTRIBUTE_NAME_RENTAL_ID,
                Rental.ATTRIBUTE_NAME_RENTAL_DATE,
                Rental.ATTRIBUTE_NAME_INVENTORY,
                Rental.ATTRIBUTE_NAME_CUSTOMER,
                Rental.ATTRIBUTE_NAME_STAFF,
                Rental.ATTRIBUTE_NAME_LAST_UPDATE,
                Rental.ATTRIBUTE_NAME_PAYMENTS
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
    public Rental get() {
        final var instance = super.get();
        final var random = ThreadLocalRandom.current();
        instance.setReturnDate(
                random.nextBoolean()
                        ? null
                        : LocalDateTime.now().plusDays(random.nextLong(1L, 10L)).truncatedTo(ChronoUnit.SECONDS)
        );
        return instance;
    }
}
