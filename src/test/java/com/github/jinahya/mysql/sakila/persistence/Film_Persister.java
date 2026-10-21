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
 * A persister which persists {@link Film} instances.
 * <p>
 * Each instance is given the first {@link Language} of the database, rather than a new one, because {@code language_id}
 * is a {@code TINYINT UNSIGNED}: MySQL does not give back an {@code AUTO_INCREMENT} value a rolled-back insert used, so
 * a new language for every film would exhaust the column within a few runs. A new one is persisted only when the
 * database has none, as the generated, in-memory one does. The {@code originalLanguage} is left {@code null}, which the
 * column allows.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class Film_Persister
        extends AbstractEntityPersister<Film> {

    Film_Persister() {
        super(Film.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public Film apply(final EntityManager entityManager, final Film entityInstance) {
        entityInstance.setLanguage(Language_Persister.selectFirst(entityManager).orElseGet(
                () -> EntityPersisterUtils.newPersistedInstanceOf(entityManager, Language.class)));
        return super.apply(entityManager, entityInstance);
    }
}
