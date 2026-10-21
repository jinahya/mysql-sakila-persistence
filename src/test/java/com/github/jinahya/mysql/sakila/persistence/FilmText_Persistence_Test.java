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

import static org.junit.jupiter.api.Assumptions.abort;

/**
 * Verifies the mappings of {@link FilmText} against the schema generated into the in-memory database.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class FilmText_Persistence_Test
        extends _Entity_Persistence_Test<FilmText> {

    FilmText_Persistence_Test() {
        super(FilmText.class);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Does nothing; the triggers of the {@value Film#TABLE_NAME} table insert, update, and delete every row of
     * {@value FilmText#TABLE_NAME}, so nothing else inserts into it.
     */
    @Disabled("written by the film table's triggers: nothing else inserts into it")
    @Override
    @Test
    protected void _persist_RandomizedInstance() {
        // empty
    }

    /**
     * Does nothing; the triggers of the {@value Film#TABLE_NAME} table insert, update, and delete every row of
     * {@value FilmText#TABLE_NAME}, so nothing else inserts into it.
     */
    @Disabled("written by the film table's triggers: nothing else inserts into it")
    @Override
    @Test
    protected void _persist_EqualsReference() {
        // empty
    }

    /**
     * Does nothing; the triggers of the {@value Film#TABLE_NAME} table insert, update, and delete every row of
     * {@value FilmText#TABLE_NAME}, so nothing else inserts into it.
     */
    @Disabled("written by the film table's triggers: nothing else inserts into it")
    @Override
    @Test
    protected void _persist_ReferenceEquals() {
        // empty
    }

    /**
     * Does nothing; the triggers of the {@value Film#TABLE_NAME} table insert, update, and delete every row of
     * {@value FilmText#TABLE_NAME}, so nothing else inserts into it.
     */
    @Disabled("written by the film table's triggers: nothing else inserts into it")
    @Override
    @Test
    protected void _persist_HashSetMembership() {
        // empty
    }

    /**
     * Aborts; the triggers of the {@value Film#TABLE_NAME} table insert, update, and delete every row of
     * {@value FilmText#TABLE_NAME}, so nothing else inserts into it, and there is nothing persisted to find.
     */
    @Override
    protected void assumeTargetClassIsFindable() {
        abort("written by the film table's triggers: nothing else inserts into it");
    }
}
