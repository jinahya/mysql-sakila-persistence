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
 * A randomizer which produces randomized {@link Payment} instances.
 * <p>
 * The {@code paymentId}, which the database generates, the {@code paymentDate}, which a trigger
 * sets to {@code NOW()}, the {@code lastUpdate}, which the database fills, and every association
 * are excluded from randomization; {@link Payment_Persister} supplies the associations. The
 * {@code amount} is assigned after the fact, within what its column holds.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class Payment_Randomizer
        extends PodamObjectRandomizer<Payment> {

    Payment_Randomizer() {
        super(Payment.class, List.of(
                Payment.ATTRIBUTE_NAME_PAYMENT_ID,
                Payment.ATTRIBUTE_NAME_CUSTOMER,
                Payment.ATTRIBUTE_NAME_STAFF,
                Payment.ATTRIBUTE_NAME_RENTAL,
                Payment.ATTRIBUTE_NAME_PAYMENT_DATE,
                Payment.ATTRIBUTE_NAME_LAST_UPDATE
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
    public Payment get() {
        final var instance = super.get();
        final var random = ThreadLocalRandom.current();
        instance.setAmount(
                BigDecimal.valueOf(random.nextInt(0, 100000), Payment.COLUMN_SCALE_AMOUNT));
        return instance;
    }
}
