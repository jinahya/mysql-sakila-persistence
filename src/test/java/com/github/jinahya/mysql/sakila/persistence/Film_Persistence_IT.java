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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Verifies the mappings of {@link Film} against the installed {@code sakila} database.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class Film_Persistence_IT extends _Entity_Persistence_IT<Film> {

    Film_Persistence_IT() {
        super(Film.class);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @param instance {@inheritDoc}
     * @implNote Reading the instance at all already proves that {@link Film.RatingConverter} knows the installed
     * rating; this also checks that the installed special features are members {@link Film.SpecialFeature} knows,
     * and that the language, a lazy association, resolves.
     */
    @Override
    void randomSelected(final Film instance) {
        assertThatCode(() -> Film.SpecialFeature.parse(instance.getSpecialFeatures()))
                .as("special features of %s", instance)
                .doesNotThrowAnyException();
        assertThat(instance.getLanguage().getName())
                .as("name of the language of %s", instance)
                .isNotBlank();
    }
}
