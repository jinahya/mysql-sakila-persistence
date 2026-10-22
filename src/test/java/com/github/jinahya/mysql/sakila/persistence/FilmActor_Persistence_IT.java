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
 * Verifies the mappings of {@link FilmActor} against the installed {@code sakila} database.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class FilmActor_Persistence_IT
        extends _Entity_Persistence_IT<FilmActor> {

    FilmActor_Persistence_IT() {
        super(FilmActor.class);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @param instance {@inheritDoc}
     * @implNote The two components of the identifier, which the provider derives, are the identifiers of the
     *         actor and of the film, lazy associations which resolve.
     */
    @Override
    void randomTargetInstanceSelected(final FilmActor instance) {
        assertThat(instance.getId().getActorId())
                .as("actor id of the id of %s", instance)
                .isEqualTo(instance.getActor().getActorId());
        assertThat(instance.getId().getFilmId())
                .as("film id of the id of %s", instance)
                .isEqualTo(instance.getFilm().getFilmId());
    }
}
