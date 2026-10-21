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

/**
 * A class for testing the {@link Language} entity class.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class Language_Test extends _Entity_Test<Language> {

    Language_Test() {
        super(Language.class);
    }

    // ------------------------------------------------------------------------------------------------- equals/hashCode

    /**
     * {@inheritDoc}
     *
     * @implNote {@link Language#equals(Object) equals} compares only the {@code @Id},
     * because every other attribute is mutable state.
     */
    @Override
    public SingleTypeEqualsVerifierApi<Language> equals_verifier_() {
        return super.equals_verifier_()
                .suppress(Warning.SURROGATE_KEY,
                          Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY,
                          Warning.STRICT_HASHCODE);
    }
}
