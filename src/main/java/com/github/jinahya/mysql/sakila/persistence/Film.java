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
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * An entity class for mapping the {@value Film#TABLE_NAME} table.
 * <p>
 * {@snippet lang = "sql":
 * CREATE TABLE film (
 *   film_id SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT,
 *   title VARCHAR(128) NOT NULL,
 *   description TEXT DEFAULT NULL,
 *   release_year YEAR DEFAULT NULL,
 *   language_id TINYINT UNSIGNED NOT NULL,
 *   original_language_id TINYINT UNSIGNED DEFAULT NULL,
 *   rental_duration TINYINT UNSIGNED NOT NULL DEFAULT 3,
 *   rental_rate DECIMAL(4,2) NOT NULL DEFAULT 4.99,
 *   length SMALLINT UNSIGNED DEFAULT NULL,
 *   replacement_cost DECIMAL(5,2) NOT NULL DEFAULT 19.99,
 *   rating ENUM('G','PG','PG-13','R','NC-17') DEFAULT 'G',
 *   special_features SET('Trailers','Commentaries','Deleted Scenes','Behind the Scenes') DEFAULT NULL,
 *   last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 *   PRIMARY KEY  (film_id),
 *   KEY idx_title (title),
 *   KEY idx_fk_language_id (language_id),
 *   KEY idx_fk_original_language_id (original_language_id),
 *   CONSTRAINT fk_film_language FOREIGN KEY (language_id) REFERENCES language (language_id)
 *       ON DELETE RESTRICT ON UPDATE CASCADE,
 *   CONSTRAINT fk_film_language_original FOREIGN KEY (original_language_id) REFERENCES language (language_id)
 *       ON DELETE RESTRICT ON UPDATE CASCADE
 * )
 *}
 * <p>
 * The {@code ins_film}, {@code upd_film} and {@code del_film} triggers of the installed database keep
 * {@value FilmText#TABLE_NAME} in step with this table; persisting a film also inserts its {@link FilmText}.
 * <p>
 * Every column with a {@code DEFAULT} is written with whatever the instance holds, {@code null} included; the defaults
 * apply only to an {@code INSERT} which leaves the column out, which a provider never does.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@NamedQuery(name = "Film.selectOneByFilmId",
            query = """
                    SELECT e
                    FROM Film e
                    WHERE e.filmId = :filmId"""
)
@NamedQuery(name = "Film.selectListOrderByFilmIdAsc",
            query = """
                    SELECT e
                    FROM Film e
                    ORDER BY e.filmId ASC"""
)
@NamedQuery(name = "Film.selectListOrderByFilmIdAscFilmIdGt",
            query = """
                    SELECT e
                    FROM Film e
                    WHERE e.filmId > :filmIdMinExclusive
                    ORDER BY e.filmId ASC"""
)
@NamedQuery(name = "Film.selectListByTitleOrderByFilmIdAsc",
            query = """
                    SELECT e
                    FROM Film e
                    WHERE e.title = :title
                    ORDER BY e.filmId ASC"""
)
@NamedQuery(name = "Film.selectListByLanguageOrderByFilmIdAsc",
            query = """
                    SELECT e
                    FROM Film e
                    WHERE e.language = :language
                    ORDER BY e.filmId ASC"""
)
@NamedQuery(name = "Film.selectListByRatingOrderByFilmIdAsc",
            query = """
                    SELECT e
                    FROM Film e
                    WHERE e.rating = :rating
                    ORDER BY e.filmId ASC"""
)
@NamedQuery(name = "Film.countAll",
            query = """
                    SELECT COUNT(e)
                    FROM Film e"""
)
@NamedQuery(name = "Film.countByLanguage",
            query = """
                    SELECT COUNT(e)
                    FROM Film e
                    WHERE e.language = :language"""
)
@Entity
@Table(name = Film.TABLE_NAME)
public class Film {

    /**
     * The name of the database table to which this entity class maps. The value is {@value}.
     */
    public static final String TABLE_NAME = "film";

    // --------------------------------------------------------------------------------------------------------- film_id

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_FILM_ID} attribute maps. The value is
     * {@value}.
     */
    public static final String COLUMN_NAME_FILM_ID = "film_id";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_FILM_ID} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_FILM_ID = "filmId";

    // ----------------------------------------------------------------------------------------------------------- title

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_TITLE} attribute maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_TITLE = "title";

    /**
     * The length of the {@value #COLUMN_NAME_TITLE} column. The value is {@value}.
     */
    public static final int COLUMN_LENGTH_TITLE = 128;

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_TITLE} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_TITLE = "title";

    /**
     * The maximum size of the {@value #ATTRIBUTE_NAME_TITLE} attribute. The value is {@value}.
     */
    public static final int SIZE_MAX_TITLE = COLUMN_LENGTH_TITLE;

    // ----------------------------------------------------------------------------------------------------- description

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_DESCRIPTION} attribute maps. The value is
     * {@value}.
     */
    public static final String COLUMN_NAME_DESCRIPTION = "description";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_DESCRIPTION} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_DESCRIPTION = "description";

    // ---------------------------------------------------------------------------------------------------- release_year

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_RELEASE_YEAR} attribute maps. The value is
     * {@value}.
     */
    public static final String COLUMN_NAME_RELEASE_YEAR = "release_year";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_RELEASE_YEAR} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_RELEASE_YEAR = "releaseYear";

    /**
     * The minimum value of the {@value #ATTRIBUTE_NAME_RELEASE_YEAR} attribute; the smallest year a {@code YEAR} column
     * holds. The value is {@value}.
     */
    public static final int ATTRIBUTE_MIN_RELEASE_YEAR = 1901;

    /**
     * The maximum value of the {@value #ATTRIBUTE_NAME_RELEASE_YEAR} attribute; the largest year a {@code YEAR} column
     * holds. The value is {@value}.
     */
    public static final int ATTRIBUTE_MAX_RELEASE_YEAR = 2155;

    // ----------------------------------------------------------------------------------------------------- language_id

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_LANGUAGE} attribute maps. The value is
     * {@value}.
     */
    public static final String COLUMN_NAME_LANGUAGE_ID = "language_id";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_LANGUAGE_ID} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_LANGUAGE = "language";

    // -------------------------------------------------------------------------------------------- original_language_id

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_ORIGINAL_LANGUAGE} attribute maps. The value
     * is {@value}.
     */
    public static final String COLUMN_NAME_ORIGINAL_LANGUAGE_ID = "original_language_id";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_ORIGINAL_LANGUAGE_ID} column. The value is
     * {@value}.
     */
    public static final String ATTRIBUTE_NAME_ORIGINAL_LANGUAGE = "originalLanguage";

    // ------------------------------------------------------------------------------------------------- rental_duration

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_RENTAL_DURATION} attribute maps. The value is
     * {@value}.
     */
    public static final String COLUMN_NAME_RENTAL_DURATION = "rental_duration";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_RENTAL_DURATION} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_RENTAL_DURATION = "rentalDuration";

    /**
     * The minimum value of the {@value #ATTRIBUTE_NAME_RENTAL_DURATION} attribute; the column is a
     * {@code TINYINT UNSIGNED}. The value is {@value}.
     */
    public static final int ATTRIBUTE_MIN_RENTAL_DURATION = 0;

    /**
     * The maximum value of the {@value #ATTRIBUTE_NAME_RENTAL_DURATION} attribute; the column is a
     * {@code TINYINT UNSIGNED}. The value is {@value}.
     */
    public static final int ATTRIBUTE_MAX_RENTAL_DURATION = 255;

    // ----------------------------------------------------------------------------------------------------- rental_rate

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_RENTAL_RATE} attribute maps. The value is
     * {@value}.
     */
    public static final String COLUMN_NAME_RENTAL_RATE = "rental_rate";

    /**
     * The precision of the {@value #COLUMN_NAME_RENTAL_RATE} column. The value is {@value}.
     */
    public static final int COLUMN_PRECISION_RENTAL_RATE = 4;

    /**
     * The scale of the {@value #COLUMN_NAME_RENTAL_RATE} column. The value is {@value}.
     */
    public static final int COLUMN_SCALE_RENTAL_RATE = 2;

    /**
     * The minimum value of the {@value #COLUMN_NAME_RENTAL_RATE} column. The value is {@value}.
     */
    public static final String COLUMN_MIN_RENTAL_RATE = "-99.99";

    /**
     * The maximum value of the {@value #COLUMN_NAME_RENTAL_RATE} column. The value is {@value}.
     */
    public static final String COLUMN_MAX_RENTAL_RATE = "99.99";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_RENTAL_RATE} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_RENTAL_RATE = "rentalRate";

    // ---------------------------------------------------------------------------------------------------------- length

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_LENGTH} attribute maps. The value is
     * {@value}.
     */
    public static final String COLUMN_NAME_LENGTH = "length";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_LENGTH} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_LENGTH = "length";

    /**
     * The minimum value of the {@value #ATTRIBUTE_NAME_LENGTH} attribute; the column is a {@code SMALLINT UNSIGNED}.
     * The value is {@value}.
     */
    public static final int ATTRIBUTE_MIN_LENGTH = 0;

    /**
     * The maximum value of the {@value #ATTRIBUTE_NAME_LENGTH} attribute; the column is a {@code SMALLINT UNSIGNED}.
     * The value is {@value}.
     */
    public static final int ATTRIBUTE_MAX_LENGTH = 65535;

    // ------------------------------------------------------------------------------------------------ replacement_cost

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_REPLACEMENT_COST} attribute maps. The value
     * is {@value}.
     */
    public static final String COLUMN_NAME_REPLACEMENT_COST = "replacement_cost";

    /**
     * The precision of the {@value #COLUMN_NAME_REPLACEMENT_COST} column. The value is {@value}.
     */
    public static final int COLUMN_PRECISION_REPLACEMENT_COST = 5;

    /**
     * The scale of the {@value #COLUMN_NAME_REPLACEMENT_COST} column. The value is {@value}.
     */
    public static final int COLUMN_SCALE_REPLACEMENT_COST = 2;

    /**
     * The minimum value of the {@value #COLUMN_NAME_REPLACEMENT_COST} column. The value is {@value}.
     */
    public static final String COLUMN_MIN_REPLACEMENT_COST = "-999.99";

    /**
     * The maximum value of the {@value #COLUMN_NAME_REPLACEMENT_COST} column. The value is {@value}.
     */
    public static final String COLUMN_MAX_REPLACEMENT_COST = "999.99";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_REPLACEMENT_COST} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_REPLACEMENT_COST = "replacementCost";

    // ---------------------------------------------------------------------------------------------------------- rating

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_RATING} attribute maps. The value is
     * {@value}.
     */
    public static final String COLUMN_NAME_RATING = "rating";

    /**
     * The length of the longest value of the {@value #COLUMN_NAME_RATING} column, an {@code ENUM}; used only by schema
     * generation. The value is {@value}.
     */
    public static final int COLUMN_LENGTH_RATING = 5;

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_RATING} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_RATING = "rating";

    // ------------------------------------------------------------------------------------------------ special_features

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_SPECIAL_FEATURES} attribute maps. The value
     * is {@value}.
     */
    public static final String COLUMN_NAME_SPECIAL_FEATURES = "special_features";

    /**
     * The length of the longest value of the {@value #COLUMN_NAME_SPECIAL_FEATURES} column, a {@code SET} -- every
     * member, joined by commas; used only by schema generation. The value is {@value}.
     */
    public static final int COLUMN_LENGTH_SPECIAL_FEATURES = 54;

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_SPECIAL_FEATURES} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_SPECIAL_FEATURES = "specialFeatures";

    /**
     * The maximum size of the {@value #ATTRIBUTE_NAME_SPECIAL_FEATURES} attribute. The value is {@value}.
     */
    public static final int SIZE_MAX_SPECIAL_FEATURES = COLUMN_LENGTH_SPECIAL_FEATURES;

    // ----------------------------------------------------------------------------------------------------- last_update

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_LAST_UPDATE} attribute maps. The value is
     * {@value}.
     */
    public static final String COLUMN_NAME_LAST_UPDATE = "last_update";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_LAST_UPDATE} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_LAST_UPDATE = "lastUpdate";

    // ------------------------------------------------------------------------------------------------------ filmActors

    /**
     * The name of the attribute which maps the {@link FilmActor film-actors} of this film. The value is {@value}.
     *
     * @see FilmActor#ATTRIBUTE_NAME_FILM
     */
    public static final String ATTRIBUTE_NAME_FILM_ACTORS = "filmActors";

    // -------------------------------------------------------------------------------------------------- filmCategories

    /**
     * The name of the attribute which maps the {@link FilmCategory film-categories} of this film. The value is
     * {@value}.
     *
     * @see FilmCategory#ATTRIBUTE_NAME_FILM
     */
    public static final String ATTRIBUTE_NAME_FILM_CATEGORIES = "filmCategories";

    // ----------------------------------------------------------------------------------------------------- inventories

    /**
     * The name of the attribute which maps the {@link Inventory inventories} of this film. The value is {@value}.
     *
     * @see Inventory#ATTRIBUTE_NAME_FILM
     */
    public static final String ATTRIBUTE_NAME_INVENTORIES = "inventories";

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Constants for the values of the {@value #COLUMN_NAME_RATING} column.
     */
    public enum Rating {

        /**
         * A constant for the {@code G} value.
         */
        G,

        /**
         * A constant for the {@code PG} value.
         */
        PG,

        /**
         * A constant for the {@code PG-13} value. Note that the column value is not this constant's
         * {@link Enum#name() name}.
         */
        PG_13("PG-13"),

        /**
         * A constant for the {@code R} value.
         */
        R,

        /**
         * A constant for the {@code NC-17} value. Note that the column value is not this constant's
         * {@link Enum#name() name}.
         */
        NC_17("NC-17");

        // -------------------------------------------------------------------------------------------------------------

        /**
         * Returns the constant whose {@link #columnValue() column value} is the specified value.
         *
         * @param columnValue the column value to match.
         * @return the constant whose column value is {@code columnValue}.
         * @throws IllegalArgumentException if no constant has {@code columnValue} as its column value.
         */
        public static Rating valueOfColumnValue(final String columnValue) {
            for (final var value : values()) {
                if (value.columnValue().equals(columnValue)) {
                    return value;
                }
            }
            throw new IllegalArgumentException("no rating for '" + columnValue + "'");
        }

        // -------------------------------------------------------------------------------------------------------------
        Rating(final String columnValue) {
            this.columnValue = columnValue;
        }

        Rating() {
            this(null);
        }

        // -------------------------------------------------------------------------------------------------------------

        /**
         * Returns the value of the {@value Film#COLUMN_NAME_RATING} column which this constant represents.
         *
         * @return the column value of this constant; the constant's {@link Enum#name() name} unless it declares its
         * own.
         */
        public String columnValue() {
            if (columnValue != null) {
                return columnValue;
            }
            return name();
        }

        // -------------------------------------------------------------------------------------------------------------
        private final String columnValue;
    }

    /**
     * A converter between {@link Rating} and the {@value #COLUMN_NAME_RATING} column, by {@link Rating#columnValue()}
     * rather than {@link Enum#name()}.
     *
     * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
     */
    @Converter(autoApply = false)
    public static class RatingConverter implements AttributeConverter<Rating, String> {

        /**
         * Creates a new instance.
         */
        public RatingConverter() {
            super();
        }

        @Override
        public String convertToDatabaseColumn(final Rating attribute) {
            if (attribute == null) {
                return null;
            }
            return attribute.columnValue();
        }

        /**
         * {@inheritDoc}
         *
         * @param dbData {@inheritDoc}
         * @return {@inheritDoc}
         * @throws IllegalArgumentException if {@code dbData} is the column value of no {@link Rating} constant.
         */
        @Override
        public Rating convertToEntityAttribute(final String dbData) {
            if (dbData == null) {
                return null;
            }
            return Rating.valueOfColumnValue(dbData);
        }
    }

    /**
     * Constants for the members of the {@value #COLUMN_NAME_SPECIAL_FEATURES} column, a {@code SET}, whose value is
     * any number of its members joined by commas.
     */
    public enum SpecialFeature {

        /**
         * A constant for the {@code Trailers} member.
         */
        TRAILERS("Trailers"),

        /**
         * A constant for the {@code Commentaries} member.
         */
        COMMENTARIES("Commentaries"),

        /**
         * A constant for the {@code Deleted Scenes} member.
         */
        DELETED_SCENES("Deleted Scenes"),

        /**
         * A constant for the {@code Behind the Scenes} member.
         */
        BEHIND_THE_SCENES("Behind the Scenes");

        // -------------------------------------------------------------------------------------------------------------

        /**
         * The separator of the members in a value of the {@value Film#COLUMN_NAME_SPECIAL_FEATURES} column. The value
         * is {@value}.
         */
        public static final String SEPARATOR = ",";

        /**
         * Returns the constant whose {@link #columnValue() column value} is the specified value.
         *
         * @param columnValue the column value to match.
         * @return the constant whose column value is {@code columnValue}.
         * @throws IllegalArgumentException if no constant has {@code columnValue} as its column value.
         */
        public static SpecialFeature valueOfColumnValue(final String columnValue) {
            for (final var value : values()) {
                if (value.columnValue().equals(columnValue)) {
                    return value;
                }
            }
            throw new IllegalArgumentException("no special feature for '" + columnValue + "'");
        }

        /**
         * Parses the specified value of the {@value Film#COLUMN_NAME_SPECIAL_FEATURES} column into the set of the
         * constants it holds.
         *
         * @param columnValue the column value to parse; may be {@code null} or empty.
         * @return a new set of the constants {@code columnValue} holds; empty when {@code columnValue} is {@code null}
         * or empty.
         * @throws IllegalArgumentException if {@code columnValue} holds a member no constant represents.
         */
        public static Set<SpecialFeature> parse(@Nullable final String columnValue) {
            final var parsed = EnumSet.noneOf(SpecialFeature.class);
            if (columnValue == null || columnValue.isEmpty()) {
                return parsed;
            }
            Arrays.stream(columnValue.split(SEPARATOR, -1))
                    .map(SpecialFeature::valueOfColumnValue)
                    .forEach(parsed::add);
            return parsed;
        }

        /**
         * Formats the specified constants into a value of the {@value Film#COLUMN_NAME_SPECIAL_FEATURES} column.
         *
         * @param constants the constants to format.
         * @return the column value of {@code constants}, in the order MySQL itself lists the members; {@code null}
         * when {@code constants} is empty.
         */
        @Nullable
        public static String format(@Nonnull final Collection<SpecialFeature> constants) {
            Objects.requireNonNull(constants, "constants is null");
            if (constants.isEmpty()) {
                return null;
            }
            return EnumSet.copyOf(constants).stream()
                    .map(SpecialFeature::columnValue)
                    .collect(Collectors.joining(SEPARATOR));
        }

        // -------------------------------------------------------------------------------------------------------------
        SpecialFeature(final String columnValue) {
            this.columnValue = Objects.requireNonNull(columnValue, "columnValue is null");
        }

        // -------------------------------------------------------------------------------------------------------------

        /**
         * Returns the member of the {@value Film#COLUMN_NAME_SPECIAL_FEATURES} column which this constant represents.
         *
         * @return the column value of this constant.
         */
        public String columnValue() {
            return columnValue;
        }

        // -------------------------------------------------------------------------------------------------------------
        private final String columnValue;
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected Film() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    @Override
    public final String toString() {
        return super.toString() + '{' +
               "filmId=" + filmId +
               ",title=" + title +
               ",description=" + description +
               ",releaseYear=" + releaseYear +
//               ",language=" + language +
//               ",originalLanguage=" + originalLanguage +
               ",rentalDuration=" + rentalDuration +
               ",rentalRate=" + rentalRate +
               ",length=" + length +
               ",replacementCost=" + replacementCost +
               ",rating=" + rating +
               ",specialFeatures=" + specialFeatures +
               ",lastUpdate=" + lastUpdate +
               '}';
    }

    /**
     * {@inheritDoc}
     *
     * @param obj {@inheritDoc}
     * @return {@inheritDoc}
     * @implSpec Equality is by the {@code @Id} alone, and only once it is assigned; an instance without one
     * equals nothing but itself.
     */
    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Film that)) {
            return false;
        }
        final var thisId = getFilmId();
        return thisId != null && thisId.equals(that.getFilmId());
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @implSpec The hash is that of the class, the same for every instance, so that it does not change as the
     * {@code @Id} is assigned; it is consistent with {@link #equals(Object)}.
     */
    @Override
    public final int hashCode() {
        return Film.class.hashCode();
    }

    // ---------------------------------------------------------------------------------------------------------- filmId

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_FILM_ID} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_FILM_ID} attribute; {@code null} until the instance is
     * persisted.
     */
    public Integer getFilmId() {
        return filmId;
    }

    // ----------------------------------------------------------------------------------------------------------- title

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_TITLE} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_TITLE} attribute.
     */
    @Nonnull
    public String getTitle() {
        return title;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_TITLE} attribute with the specified value.
     *
     * @param title new value for {@value #ATTRIBUTE_NAME_TITLE} attribute.
     */
    public void setTitle(@Nonnull final String title) {
        this.title = title;
    }

    // ----------------------------------------------------------------------------------------------------- description

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_DESCRIPTION} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_DESCRIPTION} attribute.
     */
    @Nullable
    public String getDescription() {
        return description;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_DESCRIPTION} attribute with the specified value.
     *
     * @param description new value for {@value #ATTRIBUTE_NAME_DESCRIPTION} attribute.
     */
    public void setDescription(@Nullable final String description) {
        this.description = description;
    }

    // ----------------------------------------------------------------------------------------------------- releaseYear

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_RELEASE_YEAR} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_RELEASE_YEAR} attribute.
     */
    @Nullable
    public Integer getReleaseYear() {
        return releaseYear;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_RELEASE_YEAR} attribute with the specified value.
     *
     * @param releaseYear new value for {@value #ATTRIBUTE_NAME_RELEASE_YEAR} attribute.
     */
    public void setReleaseYear(@Nullable final Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    // -------------------------------------------------------------------------------------------------------- language

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_LANGUAGE} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_LANGUAGE} attribute.
     */
    @Nonnull
    public Language getLanguage() {
        return language;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_LANGUAGE} attribute with the specified value.
     *
     * @param language new value for {@value #ATTRIBUTE_NAME_LANGUAGE} attribute.
     */
    public void setLanguage(@Nonnull final Language language) {
        this.language = language;
    }

    // ------------------------------------------------------------------------------------------------ originalLanguage

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_ORIGINAL_LANGUAGE} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_ORIGINAL_LANGUAGE} attribute.
     */
    @Nullable
    public Language getOriginalLanguage() {
        return originalLanguage;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_ORIGINAL_LANGUAGE} attribute with the specified value.
     *
     * @param originalLanguage new value for {@value #ATTRIBUTE_NAME_ORIGINAL_LANGUAGE} attribute.
     */
    public void setOriginalLanguage(@Nullable final Language originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    // -------------------------------------------------------------------------------------------------- rentalDuration

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_RENTAL_DURATION} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_RENTAL_DURATION} attribute.
     */
    @Nonnull
    public Integer getRentalDuration() {
        return rentalDuration;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_RENTAL_DURATION} attribute with the specified value.
     *
     * @param rentalDuration new value for {@value #ATTRIBUTE_NAME_RENTAL_DURATION} attribute.
     */
    public void setRentalDuration(@Nonnull final Integer rentalDuration) {
        this.rentalDuration = rentalDuration;
    }

    // ------------------------------------------------------------------------------------------------------ rentalRate

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_RENTAL_RATE} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_RENTAL_RATE} attribute.
     */
    @Nonnull
    public BigDecimal getRentalRate() {
        return rentalRate;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_RENTAL_RATE} attribute with the specified value.
     *
     * @param rentalRate new value for {@value #ATTRIBUTE_NAME_RENTAL_RATE} attribute.
     */
    public void setRentalRate(@Nonnull final BigDecimal rentalRate) {
        this.rentalRate = rentalRate;
    }

    // ---------------------------------------------------------------------------------------------------------- length

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_LENGTH} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_LENGTH} attribute.
     */
    @Nullable
    public Integer getLength() {
        return length;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_LENGTH} attribute with the specified value.
     *
     * @param length new value for {@value #ATTRIBUTE_NAME_LENGTH} attribute.
     */
    public void setLength(@Nullable final Integer length) {
        this.length = length;
    }

    // ------------------------------------------------------------------------------------------------- replacementCost

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_REPLACEMENT_COST} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_REPLACEMENT_COST} attribute.
     */
    @Nonnull
    public BigDecimal getReplacementCost() {
        return replacementCost;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_REPLACEMENT_COST} attribute with the specified value.
     *
     * @param replacementCost new value for {@value #ATTRIBUTE_NAME_REPLACEMENT_COST} attribute.
     */
    public void setReplacementCost(@Nonnull final BigDecimal replacementCost) {
        this.replacementCost = replacementCost;
    }

    // ---------------------------------------------------------------------------------------------------------- rating

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_RATING} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_RATING} attribute.
     */
    @Nullable
    public Rating getRating() {
        return rating;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_RATING} attribute with the specified value.
     *
     * @param rating new value for {@value #ATTRIBUTE_NAME_RATING} attribute.
     */
    public void setRating(@Nullable final Rating rating) {
        this.rating = rating;
    }

    // ------------------------------------------------------------------------------------------------- specialFeatures

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_SPECIAL_FEATURES} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_SPECIAL_FEATURES} attribute; any number of the members of the
     * column joined by commas.
     * @see SpecialFeature#parse(String)
     */
    @Nullable
    public String getSpecialFeatures() {
        return specialFeatures;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_SPECIAL_FEATURES} attribute with the specified value.
     *
     * @param specialFeatures new value for {@value #ATTRIBUTE_NAME_SPECIAL_FEATURES} attribute; any number of the
     *                        members of the column joined by commas.
     * @see SpecialFeature#format(Collection)
     */
    public void setSpecialFeatures(@Nullable final String specialFeatures) {
        this.specialFeatures = specialFeatures;
    }

    // ------------------------------------------------------------------------------------------------------ lastUpdate

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_LAST_UPDATE} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_LAST_UPDATE} attribute; {@code null} until the instance is
     * read from the database.
     */
    @Nullable
    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    // ------------------------------------------------------------------------------------------------------ filmActors
    List<FilmActor> getFilmActors() {
        return filmActors;
    }

    void setFilmActors(final List<FilmActor> filmActors) {
        this.filmActors = filmActors;
    }

    // -------------------------------------------------------------------------------------------------- filmCategories
    List<FilmCategory> getFilmCategories() {
        return filmCategories;
    }

    void setFilmCategories(final List<FilmCategory> filmCategories) {
        this.filmCategories = filmCategories;
    }

    // ----------------------------------------------------------------------------------------------------- inventories
    List<Inventory> getInventories() {
        return inventories;
    }

    void setInventories(final List<Inventory> inventories) {
        this.inventories = inventories;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // no @NotNull: AUTO_INCREMENT; the value is null when the provider validates at pre-persist
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COLUMN_NAME_FILM_ID, nullable = false, insertable = true, updatable = false)
    private Integer filmId;

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    @NotNull
    @Size(max = SIZE_MAX_TITLE)
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_TITLE, nullable = false, insertable = true, updatable = true,
            length = COLUMN_LENGTH_TITLE)
    private String title;

    // -----------------------------------------------------------------------------------------------------------------
    @Nullable
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_DESCRIPTION, nullable = true, insertable = true, updatable = true)
    private String description;

    // -----------------------------------------------------------------------------------------------------------------
    @Nullable
    @Max(ATTRIBUTE_MAX_RELEASE_YEAR)
    @Min(ATTRIBUTE_MIN_RELEASE_YEAR)
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_RELEASE_YEAR, nullable = true, insertable = true, updatable = true)
    private Integer releaseYear;

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    @Valid
    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = COLUMN_NAME_LANGUAGE_ID, referencedColumnName = Language.COLUMN_NAME_LANGUAGE_ID,
                nullable = false, insertable = true, updatable = true)
    private Language language;

    // -----------------------------------------------------------------------------------------------------------------
    @Nullable
    @Valid
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = COLUMN_NAME_ORIGINAL_LANGUAGE_ID, referencedColumnName = Language.COLUMN_NAME_LANGUAGE_ID,
                nullable = true, insertable = true, updatable = true)
    private Language originalLanguage;

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    @NotNull
    @Max(ATTRIBUTE_MAX_RENTAL_DURATION)
    @Min(ATTRIBUTE_MIN_RENTAL_DURATION)
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_RENTAL_DURATION, nullable = false, insertable = true, updatable = true)
    private Integer rentalDuration;

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    @NotNull
    // @Digits counts digits differently by provider; https://github.com/jakartaee/validation-spec/issues/346
    // @Digits(integer = COLUMN_PRECISION_RENTAL_RATE - COLUMN_SCALE_RENTAL_RATE, fraction = COLUMN_SCALE_RENTAL_RATE)
    @DecimalMin(COLUMN_MIN_RENTAL_RATE)
    @DecimalMax(COLUMN_MAX_RENTAL_RATE)
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_RENTAL_RATE, nullable = false, insertable = true, updatable = true,
            precision = COLUMN_PRECISION_RENTAL_RATE, scale = COLUMN_SCALE_RENTAL_RATE)
    private BigDecimal rentalRate;

    // -----------------------------------------------------------------------------------------------------------------
    @Nullable
    @Max(ATTRIBUTE_MAX_LENGTH)
    @Min(ATTRIBUTE_MIN_LENGTH)
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_LENGTH, nullable = true, insertable = true, updatable = true)
    private Integer length;

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    @NotNull
    // @Digits counts digits differently by provider; https://github.com/jakartaee/validation-spec/issues/346
    // @Digits(integer = COLUMN_PRECISION_REPLACEMENT_COST - COLUMN_SCALE_REPLACEMENT_COST,
    //         fraction = COLUMN_SCALE_REPLACEMENT_COST)
    @DecimalMin(COLUMN_MIN_REPLACEMENT_COST)
    @DecimalMax(COLUMN_MAX_REPLACEMENT_COST)
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_REPLACEMENT_COST, nullable = false, insertable = true, updatable = true,
            precision = COLUMN_PRECISION_REPLACEMENT_COST, scale = COLUMN_SCALE_REPLACEMENT_COST)
    private BigDecimal replacementCost;

    // -----------------------------------------------------------------------------------------------------------------
    @Nullable
    @Convert(converter = RatingConverter.class)
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_RATING, nullable = true, insertable = true, updatable = true,
            length = COLUMN_LENGTH_RATING)
    private Rating rating;

    // -----------------------------------------------------------------------------------------------------------------
    @Nullable
    @Size(max = SIZE_MAX_SPECIAL_FEATURES)
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_SPECIAL_FEATURES, nullable = true, insertable = true, updatable = true,
            length = COLUMN_LENGTH_SPECIAL_FEATURES)
    private String specialFeatures;

    // -----------------------------------------------------------------------------------------------------------------
    @Nullable
    @Basic
    @Column(name = COLUMN_NAME_LAST_UPDATE, nullable = false, insertable = false, updatable = false,
            columnDefinition = __Constants.COLUMN_DEFINITION_LAST_UPDATE)
    private LocalDateTime lastUpdate;

    // -----------------------------------------------------------------------------------------------------------------
    @OneToMany(mappedBy = FilmActor.ATTRIBUTE_NAME_FILM, fetch = FetchType.LAZY)
    private List<@Valid @NotNull FilmActor> filmActors;

    @OneToMany(mappedBy = FilmCategory.ATTRIBUTE_NAME_FILM, fetch = FetchType.LAZY)
    private List<@Valid @NotNull FilmCategory> filmCategories;

    @OneToMany(mappedBy = Inventory.ATTRIBUTE_NAME_FILM, fetch = FetchType.LAZY)
    private List<@Valid @NotNull Inventory> inventories;
}
