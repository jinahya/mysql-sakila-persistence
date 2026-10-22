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

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the mappings of {@link FilmText} against the installed {@code sakila} database.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class FilmText_Persistence_IT
        extends _Entity_Persistence_IT<FilmText> {

    FilmText_Persistence_IT() {
        super(FilmText.class);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Does nothing; the triggers of the {@value Film#TABLE_NAME} table insert, update, and delete
     * every row of {@value FilmText#TABLE_NAME}, so nothing else inserts into it.
     */
    @Disabled("written by the film table's triggers: nothing else inserts into it")
    @Override
    @Test
    void persistTargetInstance() {
        // empty
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @param instance {@inheritDoc}
     * @implNote The title is there, and the film, a lazy association, resolves to the one
     *         of the same {@code filmId}, with the same title.
     */
    @Override
    void randomTargetInstanceSelected(final FilmText instance) {
        assertThat(instance.getTitle()).as("title of %s", instance).isNotBlank();
        assertThat(instance.getFilm().getFilmId()).as("film id of the film of %s", instance)
                .isEqualTo(instance.getFilmId());
        assertThat(instance.getFilm().getTitle()).as("title of the film of %s", instance)
                .isEqualTo(instance.getTitle());
    }
}
