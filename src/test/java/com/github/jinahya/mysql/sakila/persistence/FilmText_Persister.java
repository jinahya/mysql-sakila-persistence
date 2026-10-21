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
import jakarta.persistence.EntityManager;

/**
 * A persister which persists {@link FilmText} instances.
 * <p>
 * Each instance is given a {@code filmId} one greater than the greatest one any row holds, so that it collides with
 * none.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class FilmText_Persister extends AbstractEntityPersister<FilmText> {

    FilmText_Persister() {
        super(FilmText.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public FilmText apply(final EntityManager entityManager, final FilmText entityInstance) {
        final var max =entityManager.createNamedQuery("FilmText.selectMaxFilmId", Integer.class).getSingleResult();
        entityInstance.setFilmId(max == null ? 1 : max + 1);
        return super.apply(entityManager, entityInstance);
    }
}
