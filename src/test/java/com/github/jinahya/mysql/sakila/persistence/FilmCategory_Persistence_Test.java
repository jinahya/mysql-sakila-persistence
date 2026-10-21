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

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the mappings of {@link FilmCategory} against the schema generated into the in-memory database.
 * <p>
 * The equality tests of the base class are overridden to compare an instance read back from the database, rather than
 * the one just persisted: the {@value FilmCategory#ATTRIBUTE_NAME_CATEGORY_ID}, which equality reads, is read-only, so
 * the instance just persisted has none, and equals nothing but itself, until it is read back.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class FilmCategory_Persistence_Test
        extends _Entity_Persistence_Test<FilmCategory> {

    FilmCategory_Persistence_Test() {
        super(FilmCategory.class);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @implNote Compares the instance read back from the database, which has its
     *         {@value FilmCategory#ATTRIBUTE_NAME_CATEGORY_ID}, with the reference to its row.
     */
    @DisplayName("loaded.equals(getReference(loaded))")
    @Test
    @Override
    protected void _persist_EqualsReference() {
        applyNewPersistedEntityInstanceAndRollback((em, v) -> {
            final var loaded = flushClearAndLoad(em, v);
            em.clear();
            final var reference = em.getReference(loaded);
            assertThat(loaded.equals(reference))
                    .as("loaded.equals(reference); reference: %s", reference.getClass())
                    .isTrue();
            assertThat(loaded.hashCode())
                    .as("loaded.hashCode() == reference.hashCode(); reference: %s", reference.getClass())
                    .isEqualTo(reference.hashCode());
            return reference;
        });
    }

    /**
     * {@inheritDoc}
     *
     * @implNote Compares the reference to the row with the instance read back from the database, which has its
     *         {@value FilmCategory#ATTRIBUTE_NAME_CATEGORY_ID}.
     */
    @DisplayName("getReference(loaded).equals(loaded)")
    @Test
    @Override
    protected void _persist_ReferenceEquals() {
        applyNewPersistedEntityInstanceAndRollback((em, v) -> {
            final var loaded = flushClearAndLoad(em, v);
            em.clear();
            final var reference = em.getReference(loaded);
            assertThat(reference.equals(loaded))
                    .as("reference.equals(loaded); reference: %s", reference.getClass())
                    .isTrue();
            return reference;
        });
    }

    /**
     * {@inheritDoc}
     *
     * @implNote Puts the instance read back from the database, which has its
     *         {@value FilmCategory#ATTRIBUTE_NAME_CATEGORY_ID}, in the set.
     */
    @DisplayName("a HashSet still contains the loaded instance across merge and getReference")
    @Test
    @Override
    protected void _persist_HashSetMembership() {
        applyNewPersistedEntityInstanceAndRollback((em, v) -> {
            final var loaded = flushClearAndLoad(em, v);
            final var set = new HashSet<FilmCategory>();
            set.add(loaded);
            em.clear();
            final var merged = em.merge(loaded);
            assertThat(set)
                    .as("the set, of the instance merged into a cleared context")
                    .contains(merged);
            em.clear();
            final var reference = em.getReference(loaded);
            assertThat(set)
                    .as("the set, of the reference to the row; reference: %s", reference.getClass())
                    .contains(reference);
            return set;
        });
    }

    /**
     * Flushes the specified instance, just persisted, clears the context, and reads the instance's row back.
     *
     * @param em the entity manager.
     * @param v  the instance just persisted.
     * @return the instance read back from the database.
     */
    private static FilmCategory flushClearAndLoad(final EntityManager em, final FilmCategory v) {
        em.flush();
        em.clear();
        return em.createNamedQuery("FilmCategory.selectOneByFilmIdAndCategoryId", FilmCategory.class)
                .setParameter("filmId", v.getFilmId())
                .setParameter("categoryId", v.getCategory().getCategoryId())
                .getSingleResult();
    }
}
