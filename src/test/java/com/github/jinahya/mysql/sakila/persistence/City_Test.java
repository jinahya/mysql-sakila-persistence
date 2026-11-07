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

import lombok.extern.slf4j.Slf4j;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * A class for testing the {@link City} entity class.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class City_Test
        extends _Entity_Test<City, Integer> {

    City_Test() {
        super(City.class, Integer.class);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Tests {@link City#of(Country)}.
     */
    @DisplayName("of(country)")
    @Nested
    class OfTest {

        /**
         * Verifies that {@link City#of(Country) of} returns an instance whose
         * {@value City#ATTRIBUTE_NAME_COUNTRY} is the very instance it was given.
         */
        @Test
        void __() {
            final var country = mock(Country.class);
            final var instance = City.of(country);
            assertThat(instance).isNotNull().satisfies(v -> {
                assertThat(v.getCountry()).isSameAs(country);
                assertThat(v.getCityId()).isNull();
                assertThat(v.getCity()).isNull();
            });
        }
    }

    // ----------------------------------------------------------------------------- equals/hashCode

    /**
     * {@inheritDoc}
     *
     * @implNote {@link City#equals(Object) equals} compares only the {@code @Id}, because
     *         every other attribute is mutable state.
     */
    @Override
    public SingleTypeEqualsVerifierApi<City> equals_verifier_() {
        return super.equals_verifier_()
                .suppress(Warning.SURROGATE_KEY,
                          Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY,
                          Warning.STRICT_HASHCODE);
    }
}
