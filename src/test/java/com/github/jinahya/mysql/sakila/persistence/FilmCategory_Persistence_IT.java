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

/**
 * Verifies the mappings of {@link FilmCategory} against the installed {@code sakila} database.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class FilmCategory_Persistence_IT
        extends _Entity_Persistence_IT<FilmCategory> {

    FilmCategory_Persistence_IT() {
        super(FilmCategory.class);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Not a test here; {@link FilmCategory} is mapped with an
     * {@link jakarta.persistence.IdClass IdClass}, which the generic test does not assemble.
     */
    @Override
    void find_RandomlySelected_ContextCleared() {
        // empty
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @param instance {@inheritDoc}
     * @implNote The two basic attributes, one written and one read-only, are the
     *         identifiers of the film and of the category, lazy associations which resolve.
     */
    @Override
    void randomTargetInstanceSelected(final FilmCategory instance) {
        assertThat(instance.getFilmId())
                .as("film id of %s", instance)
                .isEqualTo(instance.getFilm().getFilmId());
        assertThat(instance.getCategoryId())
                .as("category id of %s", instance)
                .isEqualTo(instance.getCategory().getCategoryId());
    }
}
