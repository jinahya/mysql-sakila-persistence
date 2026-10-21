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
 * A persister which persists {@link FilmActor} instances.
 * <p>
 * A newly persisted {@link Actor} and a newly persisted {@link Film} are flushed first, for their identifiers to be
 * assigned, and what is persisted is {@link FilmActor#of(Actor, Film) FilmActor.of} the two, whose {@code id} is derived
 * from them; the instance given is not used, for {@code of} is the only way to set either association.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class FilmActor_Persister extends AbstractEntityPersister<FilmActor> {

    FilmActor_Persister() {
        super(FilmActor.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public FilmActor apply(final EntityManager entityManager, final FilmActor entityInstance) {
        final var actor = EntityPersisterUtils.newPersistedInstanceOf(entityManager, Actor.class);
        final var film = EntityPersisterUtils.newPersistedInstanceOf(entityManager, Film.class);
        entityManager.flush();
        return super.apply(entityManager, FilmActor.of(actor, film));
    }
}
