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

import java.util.Optional;

/**
 * A persister which persists {@link Language} instances.
 * <p>
 * A language references nothing, so an instance is persisted as it arrives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class Language_Persister
        extends AbstractEntityPersister<Language> {

    Language_Persister() {
        super(Language.class);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Selects the {@link Language} with the smallest {@code languageId} in the database.
     *
     * @param entityManager the entity manager to select with.
     * @return the {@code Language} with the smallest {@code languageId}; {@link Optional#empty() empty} when the
     *         database holds none, as the generated, in-memory one does.
     */
    static Optional<Language> selectFirst(final EntityManager entityManager) {
        return entityManager
                .createNamedQuery("Language.selectListOrderByLanguageIdAsc", Language.class)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public Language apply(final EntityManager entityManager, final Language entityInstance) {
        return super.apply(entityManager, entityInstance);
    }
}
