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

import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * A class for testing the {@link Inventory} entity class.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class Inventory_Test
        extends _Entity_Test<Inventory, Integer> {

    Inventory_Test() {
        super(Inventory.class, Integer.class);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Tests {@link Inventory#of(Film, Store)}.
     */
    @DisplayName("of(film, store)")
    @Nested
    class OfTest {

        /**
         * Verifies that {@link Inventory#of(Film, Store) of} returns an instance whose
         * {@value Inventory#ATTRIBUTE_NAME_FILM} and {@value Inventory#ATTRIBUTE_NAME_STORE} are
         * the very instances it was given, and whose {@value Inventory#ATTRIBUTE_NAME_FILM_ID} and
         * {@value Inventory#ATTRIBUTE_NAME_STORE_ID} are their identifiers.
         */
        @Test
        void __() {
            final var film = mock(Film.class);
            doReturn(1).when(film).getFilmId();
            final var store = mock(Store.class);
            doReturn(2).when(store).getStoreId();
            final var instance = Inventory.of(film, store);
            assertThat(instance).isNotNull().satisfies(v -> {
                assertThat(v.getFilm()).isSameAs(film);
                assertThat(v.getStore()).isSameAs(store);
                assertThat(v.getFilmId()).isEqualTo(1);
                assertThat(v.getStoreId()).isEqualTo(2);
                assertThat(v.getInventoryId()).isNull();
            });
        }
    }

    // ----------------------------------------------------------------------------- equals/hashCode

    /**
     * {@inheritDoc}
     *
     * @implNote {@link Inventory#equals(Object) equals} compares only the {@code @Id},
     *         because every other attribute is mutable state.
     */
    @Override
    public SingleTypeEqualsVerifierApi<Inventory> equals_verifier_() {
        return super.equals_verifier_()
                .suppress(Warning.SURROGATE_KEY,
                          Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY,
                          Warning.STRICT_HASHCODE);
    }
}
