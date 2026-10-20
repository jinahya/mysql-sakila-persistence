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
import jakarta.annotation.Nullable;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Objects;

/**
 * An entity class for mapping the {@value ActorInfo#TABLE_NAME} view.
 * <p>
 * {@snippet lang = "sql":
 * CREATE DEFINER=CURRENT_USER SQL SECURITY INVOKER VIEW actor_info
 * AS
 * SELECT
 * a.actor_id,
 * a.first_name,
 * a.last_name,
 * GROUP_CONCAT(DISTINCT CONCAT(c.name, ': ',
 *         (SELECT GROUP_CONCAT(f.title ORDER BY f.title SEPARATOR ', ')
 *                     FROM sakila.film f
 *                     INNER JOIN sakila.film_category fc
 *                       ON f.film_id = fc.film_id
 *                     INNER JOIN sakila.film_actor fa
 *                       ON f.film_id = fa.film_id
 *                     WHERE fc.category_id = c.category_id
 *                     AND fa.actor_id = a.actor_id
 *                  )
 *              )
 *              ORDER BY c.name SEPARATOR '; ')
 * AS film_info
 * FROM sakila.actor a
 * LEFT JOIN sakila.film_actor fa
 *   ON a.actor_id = fa.actor_id
 * LEFT JOIN sakila.film_category fc
 *   ON fa.film_id = fc.film_id
 * LEFT JOIN sakila.category c
 *   ON fc.category_id = c.category_id
 * GROUP BY a.actor_id, a.first_name, a.last_name
 *}
 * <p>
 * The view has a row per {@link Actor}, so the {@value #COLUMN_NAME_ACTOR_ID} column identifies a row.
 * <p>
 * Every column is mapped read-only; only the identifier stays insertable, which EclipseLink requires of an
 * {@link Id @Id}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@NamedQuery(name = "ActorInfo.selectOneByActorId",
            query = """
                    SELECT e
                    FROM ActorInfo e
                    WHERE e.actorId = :actorId"""
)
@NamedQuery(name = "ActorInfo.selectListOrderByActorIdAsc",
            query = """
                    SELECT e
                    FROM ActorInfo e
                    ORDER BY e.actorId ASC"""
)
@NamedQuery(name = "ActorInfo.selectListOrderByActorIdAscActorIdGt",
            query = """
                    SELECT e
                    FROM ActorInfo e
                    WHERE e.actorId > :actorIdMinExclusive
                    ORDER BY e.actorId ASC"""
)
@NamedQuery(name = "ActorInfo.countAll",
            query = """
                    SELECT COUNT(e)
                    FROM ActorInfo e"""
)
@Entity
@Table(name = ActorInfo.TABLE_NAME)
public class ActorInfo {

    /**
     * The name of the database view to which this entity class maps. The value is {@value}.
     */
    public static final String TABLE_NAME = "actor_info";

    // -------------------------------------------------------------------------------------------------------- actor_id

    /**
     * The name of the view column to which the {@value #ATTRIBUTE_NAME_ACTOR_ID} attribute maps. The value is
     * {@value}.
     */
    public static final String COLUMN_NAME_ACTOR_ID = "actor_id";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_ACTOR_ID} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_ACTOR_ID = "actorId";

    // ------------------------------------------------------------------------------------------------------ first_name

    /**
     * The name of the view column to which the {@value #ATTRIBUTE_NAME_FIRST_NAME} attribute maps. The value is
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
     * The name of the view column to which the {@value #ATTRIBUTE_NAME_LAST_NAME} attribute maps. The value is
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

    // ------------------------------------------------------------------------------------------------------- film_info

    /**
     * The name of the view column to which the {@value #ATTRIBUTE_NAME_FILM_INFO} attribute maps. The value is
     * {@value}.
     */
    public static final String COLUMN_NAME_FILM_INFO = "film_info";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_FILM_INFO} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_FILM_INFO = "filmInfo";

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected ActorInfo() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    @Override
    public String toString() {
        return super.toString() + '{' +
               "actorId=" + actorId +
               ",firstName=" + firstName +
               ",lastName=" + lastName +
               ",filmInfo=" + filmInfo +
               '}';
    }

    /**
     * {@inheritDoc}
     *
     * @param obj {@inheritDoc}
     * @return {@inheritDoc}
     * @implSpec Equality is by the {@code @Id} alone.
     */
    @Override
    public final boolean equals(final Object obj) {
        if (!(obj instanceof ActorInfo that)) {
            return false;
        }
        return Objects.equals(getActorId(), that.getActorId());
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @implSpec The hash is over the {@code @Id}, consistent with {@link #equals(Object)}.
     */
    @Override
    public final int hashCode() {
        return Objects.hashCode(getActorId());
    }

    // --------------------------------------------------------------------------------------------------------- actorId

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_ACTOR_ID} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_ACTOR_ID} attribute.
     */
    @Nonnull
    public Integer getActorId() {
        return actorId;
    }

    // ------------------------------------------------------------------------------------------------------- firstName

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_FIRST_NAME} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_FIRST_NAME} attribute.
     */
    @Nonnull
    public String getFirstName() {
        return firstName;
    }

    // -------------------------------------------------------------------------------------------------------- lastName

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_LAST_NAME} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_LAST_NAME} attribute.
     */
    @Nonnull
    public String getLastName() {
        return lastName;
    }

    // -------------------------------------------------------------------------------------------------------- filmInfo

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_FILM_INFO} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_FILM_INFO} attribute.
     */
    @Nullable
    public String getFilmInfo() {
        return filmInfo;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    @NotNull
    @Id
    // insertable=true is the JPA default; it is spelled out because EclipseLink rejects insertable=false on an
    // @Id -- "There should be one non-read-only mapping defined for the primary key field". Nothing writes to a
    // view anyway.
    @Column(name = COLUMN_NAME_ACTOR_ID, nullable = false, insertable = true, updatable = false)
    private Integer actorId;

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    @NotNull
    @Size(max = SIZE_MAX_FIRST_NAME)
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_FIRST_NAME, nullable = false, insertable = false, updatable = false,
            length = COLUMN_LENGTH_FIRST_NAME)
    private String firstName;

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    @NotNull
    @Size(max = SIZE_MAX_LAST_NAME)
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_LAST_NAME, nullable = false, insertable = false, updatable = false,
            length = COLUMN_LENGTH_LAST_NAME)
    private String lastName;

    // -----------------------------------------------------------------------------------------------------------------
    @Nullable
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_FILM_INFO, nullable = true, insertable = false, updatable = false)
    private String filmInfo;
}
