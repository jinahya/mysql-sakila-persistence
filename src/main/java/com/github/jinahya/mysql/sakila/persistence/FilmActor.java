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
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * An entity class for mapping the {@value FilmActor#TABLE_NAME} table, whose composite primary key is mapped with an
 * {@link EmbeddedId @EmbeddedId}.
 * <p>
 * {@snippet lang = "sql":
 * CREATE TABLE film_actor (
 *   actor_id SMALLINT UNSIGNED NOT NULL,
 *   film_id SMALLINT UNSIGNED NOT NULL,
 *   last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 *   PRIMARY KEY  (actor_id,film_id),
 *   KEY idx_fk_film_id (`film_id`),
 *   CONSTRAINT fk_film_actor_actor FOREIGN KEY (actor_id) REFERENCES actor (actor_id)
 *       ON DELETE RESTRICT ON UPDATE CASCADE,
 *   CONSTRAINT fk_film_actor_film FOREIGN KEY (film_id) REFERENCES film (film_id)
 *       ON DELETE RESTRICT ON UPDATE CASCADE
 * )
 *}
 * <p>
 * Both components of the key are foreign keys, so both are derived: each association is
 * {@link MapsId @MapsId} to its component of the {@value #ATTRIBUTE_NAME_ID}, and it is the association which writes
 * the column. {@link FilmCategory}, the other link table of the database, maps its key with an
 * {@link jakarta.persistence.IdClass @IdClass} instead.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see FilmActorId
 */
@NamedQuery(name = "FilmActor.selectOneByActorIdAndFilmId",
            query = """
                    SELECT e
                    FROM FilmActor e
                    WHERE e.id.actorId = :actorId
                      AND e.id.filmId = :filmId"""
)
@NamedQuery(name = "FilmActor.selectListOrderByActorIdAscFilmIdAsc",
            query = """
                    SELECT e
                    FROM FilmActor e
                    ORDER BY e.id.actorId ASC, e.id.filmId ASC"""
)
@NamedQuery(name = "FilmActor.selectListByActorOrderByFilmIdAsc",
            query = """
                    SELECT e
                    FROM FilmActor e
                    WHERE e.actor = :actor
                    ORDER BY e.id.filmId ASC"""
)
@NamedQuery(name = "FilmActor.selectListByFilmOrderByActorIdAsc",
            query = """
                    SELECT e
                    FROM FilmActor e
                    WHERE e.film = :film
                    ORDER BY e.id.actorId ASC"""
)
@NamedQuery(name = "FilmActor.countAll",
            query = """
                    SELECT COUNT(e)
                    FROM FilmActor e"""
)
@NamedQuery(name = "FilmActor.countByActor",
            query = """
                    SELECT COUNT(e)
                    FROM FilmActor e
                    WHERE e.actor = :actor"""
)
@NamedQuery(name = "FilmActor.countByFilm",
            query = """
                    SELECT COUNT(e)
                    FROM FilmActor e
                    WHERE e.film = :film"""
)
@Entity
@Table(name = FilmActor.TABLE_NAME)
public class FilmActor {

    /**
     * The name of the database table to which this entity class maps. The value is {@value}.
     */
    public static final String TABLE_NAME = "film_actor";

    // -------------------------------------------------------------------------------------------------------- actor_id

    /**
     * The name of the table column to which the {@value FilmActorId#ATTRIBUTE_NAME_ACTOR_ID} attribute of
     * {@link FilmActorId} maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_ACTOR_ID = "actor_id";

    /**
     * The path of the attribute which maps the {@value #COLUMN_NAME_ACTOR_ID} column -- a path into the
     * {@link EmbeddedId @EmbeddedId}, which is where the column actually lives. The value is {@value}.
     */
    public static final String ATTRIBUTE_PATH_ID_ACTOR_ID = "id.actorId";

    /**
     * The name of the {@link ManyToOne @ManyToOne} association which maps the {@value #COLUMN_NAME_ACTOR_ID} column.
     * The association is {@link MapsId @MapsId} to the {@value #ATTRIBUTE_PATH_ID_ACTOR_ID} attribute. The value is
     * {@value}.
     */
    public static final String ATTRIBUTE_NAME_ACTOR = "actor";

    // --------------------------------------------------------------------------------------------------------- film_id

    /**
     * The name of the table column to which the {@value FilmActorId#ATTRIBUTE_NAME_FILM_ID} attribute of
     * {@link FilmActorId} maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_FILM_ID = "film_id";

    /**
     * The path of the attribute which maps the {@value #COLUMN_NAME_FILM_ID} column -- a path into the
     * {@link EmbeddedId @EmbeddedId}. The value is {@value}.
     */
    public static final String ATTRIBUTE_PATH_ID_FILM_ID = "id.filmId";

    /**
     * The name of the {@link ManyToOne @ManyToOne} association which maps the {@value #COLUMN_NAME_FILM_ID} column.
     * The association is {@link MapsId @MapsId} to the {@value #ATTRIBUTE_PATH_ID_FILM_ID} attribute. The value is
     * {@value}.
     */
    public static final String ATTRIBUTE_NAME_FILM = "film";

    // ------------------------------------------------------------------------------------------- actor_id / film_id

    /**
     * The name of the attribute which maps both the {@value #COLUMN_NAME_ACTOR_ID} and the
     * {@value #COLUMN_NAME_FILM_ID} columns, as an {@link EmbeddedId @EmbeddedId}. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_ID = "id";

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

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected FilmActor() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    @Override
    public String toString() {
        return super.toString() + '{' +
               "id=" + id +
//               ",actor=" + actor +
//               ",film=" + film +
               ",lastUpdate=" + lastUpdate +
               '}';
    }

    /**
     * {@inheritDoc}
     *
     * @param obj {@inheritDoc}
     * @return {@inheritDoc}
     * @implSpec Equality is by the {@link EmbeddedId @EmbeddedId} alone.
     */
    @Override
    public final boolean equals(final Object obj) {
        if (!(obj instanceof FilmActor that)) {
            return false;
        }
        return Objects.equals(getId(), that.getId());
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @implSpec The hash is over the {@link EmbeddedId @EmbeddedId}, consistent with {@link #equals(Object)}.
     */
    @Override
    public final int hashCode() {
        return Objects.hashCode(getId());
    }

    // -------------------------------------------------------------------------------------------------------------- id

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_ID} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_ID} attribute.
     */
    public FilmActorId getId() {
        return id;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_ID} attribute with the specified value.
     *
     * @param id new value for {@value #ATTRIBUTE_NAME_ID} attribute.
     */
    protected void setId(final FilmActorId id) {
        this.id = id;
    }

    /**
     * Returns current value of {@value #ATTRIBUTE_PATH_ID_ACTOR_ID} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_PATH_ID_ACTOR_ID} attribute; {@code null} when the
     * {@value #ATTRIBUTE_NAME_ID} attribute is {@code null}.
     */
    @Transient
    public Integer getActorId() {
        return Optional.ofNullable(getId()).map(FilmActorId::getActorId).orElse(null);
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_PATH_ID_ACTOR_ID} attribute with the specified value, creating the
     * {@value #ATTRIBUTE_NAME_ID} attribute first when it is {@code null}.
     *
     * @param actorId new value for {@value #ATTRIBUTE_PATH_ID_ACTOR_ID} attribute.
     */
    protected void setActorId(final Integer actorId) {
        idOrNew().setActorId(actorId);
    }

    /**
     * Returns current value of {@value #ATTRIBUTE_PATH_ID_FILM_ID} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_PATH_ID_FILM_ID} attribute; {@code null} when the
     * {@value #ATTRIBUTE_NAME_ID} attribute is {@code null}.
     */
    @Transient
    public Integer getFilmId() {
        return Optional.ofNullable(getId()).map(FilmActorId::getFilmId).orElse(null);
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_PATH_ID_FILM_ID} attribute with the specified value, creating the
     * {@value #ATTRIBUTE_NAME_ID} attribute first when it is {@code null}.
     *
     * @param filmId new value for {@value #ATTRIBUTE_PATH_ID_FILM_ID} attribute.
     */
    protected void setFilmId(final Integer filmId) {
        idOrNew().setFilmId(filmId);
    }

    private FilmActorId idOrNew() {
        if (getId() == null) {
            setId(new FilmActorId());
        }
        return getId();
    }

    // ----------------------------------------------------------------------------------------------------------- actor

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_ACTOR} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_ACTOR} attribute.
     */
    @Nonnull
    public Actor getActor() {
        return actor;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_ACTOR} attribute with the specified value, along with the
     * {@value #ATTRIBUTE_PATH_ID_ACTOR_ID} attribute.
     *
     * @param actor new value for {@value #ATTRIBUTE_NAME_ACTOR} attribute.
     */
    public void setActor(@Nonnull final Actor actor) {
        this.actor = actor;
        setActorId(Optional.ofNullable(this.actor).map(Actor::getActorId).orElse(null));
    }

    // ------------------------------------------------------------------------------------------------------------ film

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_FILM} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_FILM} attribute.
     */
    @Nonnull
    public Film getFilm() {
        return film;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_FILM} attribute with the specified value, along with the
     * {@value #ATTRIBUTE_PATH_ID_FILM_ID} attribute.
     *
     * @param film new value for {@value #ATTRIBUTE_NAME_FILM} attribute.
     */
    public void setFilm(@Nonnull final Film film) {
        this.film = film;
        setFilmId(Optional.ofNullable(this.film).map(Film::getFilmId).orElse(null));
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

    // -----------------------------------------------------------------------------------------------------------------
    @EmbeddedId
    private FilmActorId id;

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    @Valid
    @NotNull
    @MapsId(FilmActorId.ATTRIBUTE_NAME_ACTOR_ID)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = COLUMN_NAME_ACTOR_ID, referencedColumnName = Actor.COLUMN_NAME_ACTOR_ID, nullable = false)
    private Actor actor;

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    @Valid
    @NotNull
    @MapsId(FilmActorId.ATTRIBUTE_NAME_FILM_ID)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = COLUMN_NAME_FILM_ID, referencedColumnName = Film.COLUMN_NAME_FILM_ID, nullable = false)
    private Film film;

    // -----------------------------------------------------------------------------------------------------------------
    @Nullable
    @Basic
    @Column(name = COLUMN_NAME_LAST_UPDATE, nullable = false, insertable = false, updatable = false,
            columnDefinition = __Constants.COLUMN_DEFINITION_LAST_UPDATE)
    private LocalDateTime lastUpdate;
}
