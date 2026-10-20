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

import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;
import org.junit.platform.commons.util.ReflectionUtils;

/**
 * A class for testing the {@link Rental} entity class.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class Rental_Test extends _Entity_Test<Rental> {

    Rental_Test() {
        super(Rental.class);
    }

    // ------------------------------------------------------------------------------------------------- equals/hashCode

    /**
     * {@inheritDoc}
     *
     * @implNote {@link Rental#equals(Object) equals} compares the business key -- the
     * {@value Rental#ATTRIBUTE_NAME_RENTAL_DATE}, and the identifiers of the {@value Rental#ATTRIBUTE_NAME_INVENTORY}
     * and the {@value Rental#ATTRIBUTE_NAME_CUSTOMER} -- rather than the surrogate
     * {@value Rental#ATTRIBUTE_NAME_RENTAL_ID}, which is null until the row is inserted.
     */
    @Override
    public SingleTypeEqualsVerifierApi<Rental> equals_verifier_() {
        return super.equals_verifier_()
                .withPrefabValues(Inventory.class,
                                  newInstanceWithId(Inventory.class, Inventory.ATTRIBUTE_NAME_INVENTORY_ID, 1),
                                  newInstanceWithId(Inventory.class, Inventory.ATTRIBUTE_NAME_INVENTORY_ID, 2))
                .withPrefabValues(Customer.class,
                                  newInstanceWithId(Customer.class, Customer.ATTRIBUTE_NAME_CUSTOMER_ID, 1),
                                  newInstanceWithId(Customer.class, Customer.ATTRIBUTE_NAME_CUSTOMER_ID, 2))
                .withOnlyTheseFields(Rental.ATTRIBUTE_NAME_RENTAL_DATE,
                                     Rental.ATTRIBUTE_NAME_INVENTORY,
                                     Rental.ATTRIBUTE_NAME_CUSTOMER);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns a new instance of the specified entity class whose identifier field, of the specified name, holds the
     * specified value.
     *
     * @param entityClass the entity class to instantiate.
     * @param idName      the name of the identifier field.
     * @param id          the identifier.
     * @param <E>         entity type parameter.
     * @return a new instance of {@code entityClass}.
     * @implNote The equality reads the identifier of an association, which a generated id assigns and no setter
     * writes; EqualsVerifier would otherwise stand in a mock, whose getter it cannot call.
     */
    private static <E> E newInstanceWithId(final Class<E> entityClass, final String idName, final Integer id) {
        final var instance = ReflectionUtils.newInstance(entityClass);
        try {
            final var field = entityClass.getDeclaredField(idName);
            field.setAccessible(true);
            field.set(instance, id);
        } catch (final ReflectiveOperationException roe) {
            throw new AssertionError("failed to set " + entityClass.getSimpleName() + "." + idName, roe);
        }
        return instance;
    }
}
