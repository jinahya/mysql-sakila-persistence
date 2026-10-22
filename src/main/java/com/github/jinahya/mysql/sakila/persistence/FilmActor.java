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
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * An entity class for mapping the {@value FilmActor#TABLE_NAME} table, whose composite primary key
 * is mapped with an {@link EmbeddedId @EmbeddedId}.
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
 * {@link MapsId @MapsId} to its component of the {@value #ATTRIBUTE_NAME_ID}, and it is the
 * association which writes the column. {@link FilmCategory}, the other link table of the database,
 * maps its key with an {@link jakarta.persistence.IdClass @IdClass} instead.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://dev.mysql.com/doc/sakila/en/sakila-structure-tables-film_actor.html">The
 *         film_actor Table</a>
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

    // ------------------------------------------------------------------------------------ actor_id

    /**
     * The name of the table column to which the {@value FilmActorId#ATTRIBUTE_NAME_ACTOR_ID}
     * attribute of {@link FilmActorId} maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_ACTOR_ID = "actor_id";

    /**
     * The path of the attribute which maps the {@value #COLUMN_NAME_ACTOR_ID} column -- a path into
     * the {@link EmbeddedId @EmbeddedId}, which is where the column actually lives. The value is
     * {@value}.
     */
    public static final String ATTRIBUTE_PATH_ID_ACTOR_ID = "id.actorId";

    /**
     * The name of the {@link ManyToOne @ManyToOne} association which maps the
     * {@value #COLUMN_NAME_ACTOR_ID} column. The association is {@link MapsId @MapsId} to the
     * {@value #ATTRIBUTE_PATH_ID_ACTOR_ID} attribute. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_ACTOR = "actor";

    // ------------------------------------------------------------------------------------- film_id

    /**
     * The name of the table column to which the {@value FilmActorId#ATTRIBUTE_NAME_FILM_ID}
     * attribute of {@link FilmActorId} maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_FILM_ID = "film_id";

    /**
     * The path of the attribute which maps the {@value #COLUMN_NAME_FILM_ID} column -- a path into
     * the {@link EmbeddedId @EmbeddedId}. The value is {@value}.
     */
    public static final String ATTRIBUTE_PATH_ID_FILM_ID = "id.filmId";

    /**
     * The name of the {@link ManyToOne @ManyToOne} association which maps the
     * {@value #COLUMN_NAME_FILM_ID} column. The association is {@link MapsId @MapsId} to the
     * {@value #ATTRIBUTE_PATH_ID_FILM_ID} attribute. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_FILM = "film";

    // -------------------------------------------------------------------------- actor_id / film_id

    /**
     * The name of the attribute which maps both the {@value #COLUMN_NAME_ACTOR_ID} and the
     * {@value #COLUMN_NAME_FILM_ID} columns, as an {@link EmbeddedId @EmbeddedId}. The value is
     * {@value}.
     */
    public static final String ATTRIBUTE_NAME_ID = "id";

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

    // ---------------------------------------------------------------------- STATIC FACTORY METHODS

    /**
     * Returns a new instance which links the specified actor and the specified film.
     * <p>
     * This is the only way to set the {@value #ATTRIBUTE_NAME_ACTOR} and the
     * {@value #ATTRIBUTE_NAME_FILM} from outside the package. Both associations are
     * {@link MapsId @MapsId} to the {@value #ATTRIBUTE_NAME_ID}, so changing either of them would
     * change the primary key; to link another actor, or another film, remove the instance and
     * persist a new one.
     *
     * @param actor a value for the {@value #ATTRIBUTE_NAME_ACTOR} attribute.
     * @param film  a value for the {@value #ATTRIBUTE_NAME_FILM} attribute.
     * @return a new instance of {@code actor} and {@code film}.
     */
    public static FilmActor of(final Actor actor, final Film film) {
        final var instance = new FilmActor();
        instance.actor = actor;
        instance.film = film;
        return instance;
    }

    // -------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected FilmActor() {
        super();
    }

    // ---------------------------------------------------------------------------- java.lang.Object

    @Override
    public final String toString() {
        return super.toString() + '{' +
               "id=" + id +
               ",lastUpdate=" + lastUpdate +
               '}';
    }

    /**
     * {@inheritDoc}
     *
     * @param obj {@inheritDoc}
     * @return {@inheritDoc}
     * @implSpec Equality is by the {@link EmbeddedId @EmbeddedId} alone, and only once it
     *         is assigned; an instance without one equals nothing but itself.
     */
    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof FilmActor that)) {
            return false;
        }
        final var thisId = getId();
        return thisId != null && thisId.equals(that.getId());
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @implSpec The hash is that of the class, the same for every instance, so that it does
     *         not change as the {@link EmbeddedId @EmbeddedId} is assigned; it is consistent with
     *         {@link #equals(Object)}.
     */
    @Override
    public final int hashCode() {
        return FilmActor.class.hashCode();
    }

    // ------------------------------------------------------------------------------------------ id

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_ID} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_ID} attribute.
     */
    public FilmActorId getId() {
        return id;
    }

    // --------------------------------------------------------------------------------------- actor

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_ACTOR} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_ACTOR} attribute.
     */
    public Actor getActor() {
        return actor;
    }

    // ---------------------------------------------------------------------------------------- film

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_FILM} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_FILM} attribute.
     */
    public Film getFilm() {
        return film;
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

    // ---------------------------------------------------------------------------------------------
    @EmbeddedId
    private FilmActorId id;

    // ---------------------------------------------------------------------------------------------
    @Valid
    @NotNull
    @MapsId(FilmActorId.ATTRIBUTE_NAME_ACTOR_ID)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = COLUMN_NAME_ACTOR_ID, referencedColumnName = Actor.COLUMN_NAME_ACTOR_ID,
                nullable = false, insertable = true, updatable = true)
    private Actor actor;

    // ---------------------------------------------------------------------------------------------
    @Valid
    @NotNull
    @MapsId(FilmActorId.ATTRIBUTE_NAME_FILM_ID)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = COLUMN_NAME_FILM_ID, referencedColumnName = Film.COLUMN_NAME_FILM_ID,
                nullable = false, insertable = true, updatable = true)
    private Film film;

    // ---------------------------------------------------------------------------------------------
    @Basic
    @Column(name = COLUMN_NAME_LAST_UPDATE, nullable = false, insertable = false, updatable = false,
            columnDefinition = _Constants.COLUMN_DEFINITION_LAST_UPDATE)
    private LocalDateTime lastUpdate;
}
