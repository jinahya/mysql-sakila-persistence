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
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

/**
 * An entity class for mapping the {@value Language#TABLE_NAME} table.
 * <p>
 * {@snippet lang = "sql":
 * CREATE TABLE language (
 *   language_id TINYINT UNSIGNED NOT NULL AUTO_INCREMENT,
 *   name CHAR(20) NOT NULL,
 *   last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 *   PRIMARY KEY (language_id)
 * )
 *}
 * <p>
 * The {@value #COLUMN_NAME_NAME} column is a {@code CHAR}, which MySQL pads with spaces as it
 * stores a value and trims as it reads one back, so a value read from the database never ends with
 * a space.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://dev.mysql.com/doc/sakila/en/sakila-structure-tables-language.html">The
 *         language Table</a>
 */
@NamedQuery(name = "Language.selectOneByLanguageId",
            query = """
                    SELECT e
                    FROM Language e
                    WHERE e.languageId = :languageId"""
)
@NamedQuery(name = "Language.selectListOrderByLanguageIdAsc",
            query = """
                    SELECT e
                    FROM Language e
                    ORDER BY e.languageId ASC"""
)
@NamedQuery(name = "Language.selectListByNameOrderByLanguageIdAsc",
            query = """
                    SELECT e
                    FROM Language e
                    WHERE e.name = :name
                    ORDER BY e.languageId ASC"""
)
@NamedQuery(name = "Language.countAll",
            query = """
                    SELECT COUNT(e)
                    FROM Language e"""
)
@Entity
@Table(name = Language.TABLE_NAME)
public class Language {

    /**
     * The name of the database table to which this entity class maps. The value is {@value}.
     */
    public static final String TABLE_NAME = "language";

    // --------------------------------------------------------------------------------- language_id

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_LANGUAGE_ID} attribute
     * maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_LANGUAGE_ID = "language_id";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_LANGUAGE_ID} column. The value
     * is {@value}.
     */
    public static final String ATTRIBUTE_NAME_LANGUAGE_ID = "languageId";

    // ---------------------------------------------------------------------------------------- name

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_NAME} attribute maps. The
     * value is {@value}.
     */
    public static final String COLUMN_NAME_NAME = "name";

    /**
     * The length of the {@value #COLUMN_NAME_NAME} column. The value is {@value}.
     */
    public static final int COLUMN_LENGTH_NAME = 20;

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_NAME} column. The value is
     * {@value}.
     */
    public static final String ATTRIBUTE_NAME_NAME = "name";

    /**
     * The maximum size of the {@value #ATTRIBUTE_NAME_NAME} attribute. The value is {@value}.
     */
    public static final int SIZE_MAX_NAME = COLUMN_LENGTH_NAME;

    // --------------------------------------------------------------------------------- last_update

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_LAST_UPDATE} attribute
     * maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_LAST_UPDATE = "last_update";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_LAST_UPDATE} column. The value
     * is {@value}.
     */
    public static final String ATTRIBUTE_NAME_LAST_UPDATE = "lastUpdate";

    // --------------------------------------------------------------------------------------- films

    /**
     * The name of the attribute which maps the {@link Film films} in this language. The value is
     * {@value}.
     *
     * @see Film#ATTRIBUTE_NAME_LANGUAGE
     */
    public static final String ATTRIBUTE_NAME_FILMS = "films";

    // -------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected Language() {
        super();
    }

    // ---------------------------------------------------------------------------- java.lang.Object

    @Override
    public final String toString() {
        return super.toString() + '{' +
               "languageId=" + languageId +
               ",name=" + name +
               ",lastUpdate=" + lastUpdate +
               '}';
    }

    /**
     * {@inheritDoc}
     *
     * @param obj {@inheritDoc}
     * @return {@inheritDoc}
     * @implSpec Equality is by the {@code @Id} alone, and only once it is assigned; an
     *         instance without one equals nothing but itself.
     */
    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Language that)) {
            return false;
        }
        final var thisId = getLanguageId();
        return thisId != null && thisId.equals(that.getLanguageId());
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @implSpec The hash is that of the class, the same for every instance, so that it does
     *         not change as the {@code @Id} is assigned; it is consistent with
     *         {@link #equals(Object)}.
     */
    @Override
    public final int hashCode() {
        return Language.class.hashCode();
    }

    // ---------------------------------------------------------------------------------- languageId

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_LANGUAGE_ID} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_LANGUAGE_ID} attribute; {@code null} until
     *         the instance is persisted.
     */
    public Integer getLanguageId() {
        return languageId;
    }

    // ---------------------------------------------------------------------------------------- name

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_NAME} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_NAME} attribute.
     */
    public String getName() {
        return name;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_NAME} attribute with the specified value.
     *
     * @param name new value for {@value #ATTRIBUTE_NAME_NAME} attribute.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_NAME} attribute with the display name of
     * the specified locale, in {@link Locale#ENGLISH English}.
     *
     * @param locale the locale whose display name, in {@link Locale#ENGLISH English}, is set.
     * @apiNote The display name of a locale with a country, or a variant, may be longer
     *         than {@value #SIZE_MAX_NAME} characters; {@link Locale#US}, for one, is
     *         {@code "English (United States)"}.
     * @see Locale#getDisplayName(Locale)
     * @see #setName(String)
     */
    @Transient
    public void setNameFrom(final Locale locale) {
        setName(locale.getDisplayName(Locale.ENGLISH));
    }

    // ---------------------------------------------------------------------------------- lastUpdate

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_LAST_UPDATE} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_LAST_UPDATE} attribute; {@code null} until
     *         the instance is read from the database.
     */
    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    // --------------------------------------------------------------------------------------- films
    List<Film> getFilms() {
        return films;
    }

    void setFilms(final List<Film> films) {
        this.films = films;
    }

    // ---------------------------------------------------------------------------------------------
    // no @NotNull: AUTO_INCREMENT; the value is null when the provider validates at pre-persist
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COLUMN_NAME_LANGUAGE_ID, nullable = false, insertable = true, updatable = false)
    private Integer languageId;

    // ---------------------------------------------------------------------------------------------
    @NotNull
    @Size(max = SIZE_MAX_NAME)
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_NAME, nullable = false, insertable = true, updatable = true,
            length = COLUMN_LENGTH_NAME)
    private String name;

    // ---------------------------------------------------------------------------------------------
    @Basic
    @Column(name = COLUMN_NAME_LAST_UPDATE, nullable = false, insertable = false, updatable = false,
            columnDefinition = _Constants.COLUMN_DEFINITION_LAST_UPDATE)
    private LocalDateTime lastUpdate;

    // ---------------------------------------------------------------------------------------------
    @OneToMany(mappedBy = Film.ATTRIBUTE_NAME_LANGUAGE, fetch = FetchType.LAZY)
    private List<@Valid @NotNull Film> films;
}
