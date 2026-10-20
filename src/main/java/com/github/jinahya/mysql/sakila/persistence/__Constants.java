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

/**
 * Constants shared across the {@code sakila} database's classes.
 * <p>
 * Only what is shared goes here; what belongs to a single entity stays in that entity.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
final class __Constants {

    // ----------------------------------------------------------------------------------------------------- last_update

    /**
     * The column definition of every {@code last_update} column. The value is {@value}.
     * <p>
     * Every table of the database but {@code film_text} has a {@code last_update TIMESTAMP} column which the database
     * fills itself, {@code DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP}, so the attributes which map them are
     * neither insertable nor updatable. A schema generated from the mappings -- the in-memory database of the unit
     * tests -- would otherwise have the column without its default, and every {@code INSERT}, which leaves the column
     * out, would fail on its {@code NOT NULL}.
     * <p>
     * The definition is read only by schema generation; the physical database's own {@code DDL} is what the
     * integration tests run against. The {@code ON UPDATE} clause is left out because it is MySQL's own, and the
     * attribute is not updatable anyway. The nullability is left to {@link jakarta.persistence.Column#nullable()}.
     */
    static final String COLUMN_DEFINITION_LAST_UPDATE = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP";

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    private __Constants() {
        throw new AssertionError("instantiation is not allowed");
    }
}
