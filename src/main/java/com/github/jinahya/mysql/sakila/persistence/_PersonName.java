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

import jakarta.annotation.Nonnull;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * An embeddable for mapping the {@value #COLUMN_NAME_FIRST_NAME} column and the {@value #COLUMN_NAME_LAST_NAME} column,
 * which the {@value Actor#TABLE_NAME} table, the {@value Customer#TABLE_NAME} table, and the {@value Staff#TABLE_NAME}
 * table declare alike.
 * <p>
 * {@snippet lang = "sql":
 * first_name VARCHAR(45) NOT NULL,
 * last_name VARCHAR(45) NOT NULL,
 *}
 * <p>
 * This is a record, for a name is replaced as a whole; changing either part means a new instance, and the owning entity
 * sees the change only when the new instance is set to it.
 *
 * @param firstName a value for the {@value #ATTRIBUTE_NAME_FIRST_NAME} attribute.
 * @param lastName  a value for the {@value #ATTRIBUTE_NAME_LAST_NAME} attribute.
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Embeddable
public record _PersonName(

        @Nonnull
        @NotNull
        @Size(max = _PersonName.SIZE_MAX_FIRST_NAME)
        @Basic(optional = false)
        @Column(name = _PersonName.COLUMN_NAME_FIRST_NAME, nullable = false, insertable = true, updatable = true,
                length = _PersonName.COLUMN_LENGTH_FIRST_NAME)
        String firstName,

        @Nonnull
        @NotNull
        @Size(max = _PersonName.SIZE_MAX_LAST_NAME)
        @Basic(optional = false)
        @Column(name = _PersonName.COLUMN_NAME_LAST_NAME, nullable = false, insertable = true, updatable = true,
                length = _PersonName.COLUMN_LENGTH_LAST_NAME)
        String lastName
) {

    // ------------------------------------------------------------------------------------------------------ first_name

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_FIRST_NAME} attribute maps. The value is
     * {@value}.
     */
    public static final String COLUMN_NAME_FIRST_NAME = "first_name";

    /**
     * The length of the {@value #COLUMN_NAME_FIRST_NAME} column. The value is {@value}.
     */
    public static final int COLUMN_LENGTH_FIRST_NAME = 45;

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_FIRST_NAME} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_FIRST_NAME = "firstName";

    /**
     * The maximum size of the {@value #ATTRIBUTE_NAME_FIRST_NAME} attribute. The value is {@value}.
     */
    public static final int SIZE_MAX_FIRST_NAME = COLUMN_LENGTH_FIRST_NAME;

    // ------------------------------------------------------------------------------------------------------- last_name

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_LAST_NAME} attribute maps. The value is
     * {@value}.
     */
    public static final String COLUMN_NAME_LAST_NAME = "last_name";

    /**
     * The length of the {@value #COLUMN_NAME_LAST_NAME} column. The value is {@value}.
     */
    public static final int COLUMN_LENGTH_LAST_NAME = 45;

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_LAST_NAME} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_LAST_NAME = "lastName";

    /**
     * The maximum size of the {@value #ATTRIBUTE_NAME_LAST_NAME} attribute. The value is {@value}.
     */
    public static final int SIZE_MAX_LAST_NAME = COLUMN_LENGTH_LAST_NAME;
}
