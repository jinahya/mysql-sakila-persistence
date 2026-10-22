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

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

/**
 * An abstract mapped superclass of the {@code better_*} entities, which maps the identifier they share.
 * <p>
 * Every {@code better_*} table has the same primary key,
 * {@snippet lang = "sql":
 *   id BIGINT NOT NULL AUTO_INCREMENT,
 *   PRIMARY KEY (id)
 *}
 * which this class maps once, along with an identity, by it, which each entity narrows to its own type.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see BetterStore
 * @see BetterStaff
 * @see BetterStoreStaff
 */
@MappedSuperclass
public abstract class Better {

    // -------------------------------------------------------------------------------------------------------------- id

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_ID} attribute maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_ID = "id";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_ID} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_ID = "id";

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected Better() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    @Override
    public String toString() {
        return super.toString() + '{' +
               "id=" + id +
               '}';
    }

    /**
     * {@inheritDoc}
     *
     * @param obj {@inheritDoc}
     * @return {@inheritDoc}
     * @implSpec Equality is by the {@code @Id} alone, and only once it is assigned; an instance without one
     *         equals nothing but itself. The identifier is read through {@link #getId()}, which a provider's proxy
     *         answers, rather than off the field, which a proxy leaves unset.
     * @implNote This alone would make two instances of different entities, with the same identifier, equal;
     *         every subclass overrides it to compare only to its own type, and defers to it for the rest.
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Better that)) {
            return false;
        }
        final var thisId = getId();
        return thisId != null && thisId.equals(that.getId());
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @implSpec Every subclass returns the hash of its own class, the same for every instance, so that it does not
     *         change as the {@code @Id} is assigned, and is consistent with its own {@code equals}; not that of
     *         {@link #getClass()}, which is a proxy's own class for an instance a provider loaded lazily.
     */
    @Override
    public abstract int hashCode();

    // -------------------------------------------------------------------------------------------------------------- id

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_ID} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_ID} attribute; {@code null} until the instance is persisted.
     */
    public Long getId() {
        return id;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // no @NotNull: AUTO_INCREMENT; the value is null when the provider validates at pre-persist
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COLUMN_NAME_ID, nullable = false, insertable = true, updatable = false)
    private Long id;
}
