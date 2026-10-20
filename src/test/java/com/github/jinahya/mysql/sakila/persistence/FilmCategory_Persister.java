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

import com.github.jinahya.persistence.test.util.AbstractEntityPersister;
import com.github.jinahya.persistence.test.util.EntityPersisterUtils;
import jakarta.persistence.EntityManager;

/**
 * A persister which persists {@link FilmCategory} instances.
 * <p>
 * Each instance is given a newly persisted {@link Film} and a newly persisted {@link Category} first; the two
 * {@code @Id} attributes are copied from them, so they are flushed first, for their identifiers to be assigned.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class FilmCategory_Persister extends AbstractEntityPersister<FilmCategory> {

    FilmCategory_Persister() {
        super(FilmCategory.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public FilmCategory apply(final EntityManager entityManager, final FilmCategory entityInstance) {
        final var film = EntityPersisterUtils.newPersistedInstanceOf(entityManager, Film.class);
        final var category = EntityPersisterUtils.newPersistedInstanceOf(entityManager, Category.class);
        entityManager.flush();
        entityInstance.setFilm(film);
        entityInstance.setCategory(category);
        return super.apply(entityManager, entityInstance);
    }
}
