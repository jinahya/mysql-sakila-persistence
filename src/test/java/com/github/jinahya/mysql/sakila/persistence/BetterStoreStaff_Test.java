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

/**
 * A class for testing the {@link BetterStoreStaff} entity class.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class BetterStoreStaff_Test
        extends _Entity_Test<BetterStoreStaff> {

    BetterStoreStaff_Test() {
        super(BetterStoreStaff.class);
    }

    // ------------------------------------------------------------------------------------------------- equals/hashCode

    /**
     * {@inheritDoc}
     *
     * @implNote {@link BetterStoreStaff#equals(Object) equals} compares the business key -- the read-only
     *         {@value BetterStoreStaff#ATTRIBUTE_NAME_STAFF_ID}, which is never updated -- rather than the surrogate
     *         {@code @Id}, which is null until the row is inserted.
     */
    @Override
    public SingleTypeEqualsVerifierApi<BetterStoreStaff> equalsVerifier() {
        return super.equalsVerifier()
                .withOnlyTheseFields(BetterStoreStaff.ATTRIBUTE_NAME_STAFF_ID);
    }
}
