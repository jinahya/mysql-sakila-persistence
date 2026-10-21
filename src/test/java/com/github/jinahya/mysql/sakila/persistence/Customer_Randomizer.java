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
 * A randomizer which produces randomized {@link Customer} instances.
 * <p>
 * The {@code customerId}, which the database generates, the {@code createDate}, which a trigger sets, the
 * {@code lastUpdate}, which the database fills, and every association are excluded from randomization;
 * {@link Customer_Persister} supplies the {@code store} and the {@code address}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class Customer_Randomizer extends PodamObjectRandomizer<Customer> {

    Customer_Randomizer() {
        super(Customer.class, List.of(
                Customer.ATTRIBUTE_NAME_CUSTOMER_ID,
                Customer.ATTRIBUTE_NAME_STORE,
                Customer.ATTRIBUTE_NAME_ADDRESS,
                Customer.ATTRIBUTE_NAME_CREATE_DATE,
                Customer.ATTRIBUTE_NAME_LAST_UPDATE,
                Customer.ATTRIBUTE_NAME_RENTALS,
                Customer.ATTRIBUTE_NAME_PAYMENTS
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
}
