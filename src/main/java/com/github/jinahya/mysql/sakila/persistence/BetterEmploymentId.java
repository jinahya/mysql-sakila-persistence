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

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;

/**
 * The composite primary key class of {@link BetterEmployment}, an {@link jakarta.persistence.EmbeddedId EmbeddedId} of
 * the {@value BetterEmployment#COLUMN_NAME_STORE_ID} and {@value BetterEmployment#COLUMN_NAME_STAFF_ID} columns.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Embeddable
public class BetterEmploymentId {

    /**
     * The name of the attribute which maps the {@value BetterEmployment#COLUMN_NAME_STORE_ID} column. The value is
     * {@value}.
     */
    public static final String ATTRIBUTE_NAME_STORE_ID = "storeId";

    /**
     * The name of the attribute which maps the {@value BetterEmployment#COLUMN_NAME_STAFF_ID} column. The value is
     * {@value}.
     */
    public static final String ATTRIBUTE_NAME_STAFF_ID = "staffId";

    // ------------------------------------------------------------------------------------------ STATIC_FACTORY_METHODS

    static BetterEmploymentId of(final Long storeId, final Long staffId) {
        final var instance = new BetterEmploymentId();
        instance.setStoreId(storeId);
        instance.setStaffId(staffId);
        return instance;
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     * <p>
     * The constructor is {@code public}, rather than {@code protected}, because the specification requires a primary
     * key class to have a public no-arg constructor (Jakarta Persistence 3.2, §2.4).
     */
    public BetterEmploymentId() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    @Override
    public final String toString() {
        return super.toString() + '{' +
               "storeId=" + storeId +
               ",staffId=" + staffId +
               '}';
    }

    @Override
    public final boolean equals(final Object obj) {
        if (!(obj instanceof BetterEmploymentId that)) {
            return false;
        }
        return Objects.equals(storeId, that.storeId)
               && Objects.equals(staffId, that.staffId);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(storeId, staffId);
    }

    // --------------------------------------------------------------------------------------------------------- storeId

    public Long getStoreId() {
        return storeId;
    }

    void setStoreId(final Long storeId) {
        this.storeId = storeId;
    }

    // --------------------------------------------------------------------------------------------------------- staffId

    public Long getStaffId() {
        return staffId;
    }

    void setStaffId(final Long staffId) {
        this.staffId = staffId;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // no @NotNull: BetterEmployment derives this, through @MapsId, from BetterEmployment.store, which carries the
    // constraint; EclipseLink validates before it derives it
    @Basic(optional = false)
    @Column(name = BetterEmployment.COLUMN_NAME_STORE_ID, nullable = false, insertable = true, updatable = false)
    private Long storeId;

    // no @NotNull: BetterEmployment derives this, through @MapsId, from BetterEmployment.staff, as above
    @Basic(optional = false)
    @Column(name = BetterEmployment.COLUMN_NAME_STAFF_ID, nullable = false, insertable = true, updatable = false)
    private Long staffId;
}
