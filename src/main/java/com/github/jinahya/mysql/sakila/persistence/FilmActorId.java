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

import java.util.Objects;

/**
 * A composite primary key class for mapping the {@value FilmActor#COLUMN_NAME_ACTOR_ID} column and the
 * {@value FilmActor#COLUMN_NAME_FILM_ID} column, of the {@value FilmActor#TABLE_NAME} table; the
 * {@link jakarta.persistence.EmbeddedId @EmbeddedId} of {@link FilmActor}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see FilmActor
 * @see <a
 *         href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3
 *         .2#composite-primary-keys">2.4.1.
 *         Composite primary keys</a> (Jakarta Persistence 3.2 Specification Document)
 */
@Embeddable
public class FilmActorId {

    /**
     * The name of the attribute which maps the {@value FilmActor#COLUMN_NAME_ACTOR_ID} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_ACTOR_ID = "actorId";

    /**
     * The name of the attribute which maps the {@value FilmActor#COLUMN_NAME_FILM_ID} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_FILM_ID = "filmId";

    // ------------------------------------------------------------------------------------------ STATIC_FACTORY_METHODS

    /**
     * Returns a new instance of the specified values.
     *
     * @param actorId a value for the {@value #ATTRIBUTE_NAME_ACTOR_ID} attribute.
     * @param filmId  a value for the {@value #ATTRIBUTE_NAME_FILM_ID} attribute.
     * @return a new instance of {@code actorId} and {@code filmId}.
     * @deprecated Nothing uses this, and Jakarta Persistence does not require an embeddable identifier to have one.
     */
    @Deprecated(forRemoval = true)
    static FilmActorId of(@Nonnull final Integer actorId, @Nonnull final Integer filmId) {
        final var instance = new FilmActorId();
        instance.setActorId(actorId);
        instance.setFilmId(filmId);
        return instance;
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected FilmActorId() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    @Override
    public final String toString() {
        return super.toString() + '{' +
               "actorId=" + actorId +
               ",filmId=" + filmId +
               '}';
    }

    /**
     * {@inheritDoc}
     *
     * @param obj {@inheritDoc}
     * @return {@inheritDoc}
     * @implSpec Equality is by both {@value #ATTRIBUTE_NAME_ACTOR_ID} and {@value #ATTRIBUTE_NAME_FILM_ID}.
     */
    @Override
    public final boolean equals(final Object obj) {
        if (!(obj instanceof FilmActorId that)) {
            return false;
        }
        return Objects.equals(actorId, that.actorId)
               && Objects.equals(filmId, that.filmId);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @implSpec The hash is over both {@value #ATTRIBUTE_NAME_ACTOR_ID} and {@value #ATTRIBUTE_NAME_FILM_ID},
     *         consistent with {@link #equals(Object)}.
     */
    @Override
    public final int hashCode() {
        return Objects.hash(actorId, filmId);
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

    void setActorId(@Nonnull final Integer actorId) {
        this.actorId = actorId;
    }

    // ---------------------------------------------------------------------------------------------------------- filmId

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_FILM_ID} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_FILM_ID} attribute.
     */
    @Nonnull
    public Integer getFilmId() {
        return filmId;
    }

    void setFilmId(@Nonnull final Integer filmId) {
        this.filmId = filmId;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // no @NotNull: FilmActor derives this, through @MapsId, from FilmActor.actor, which carries the constraint. When the
    // provider derives it, relative to the PrePersist validation, is the provider's to say: Hibernate ORM derives it
    // first, EclipseLink validates first. The column stays NOT NULL.
    @Basic(optional = false)
    @Column(name = FilmActor.COLUMN_NAME_ACTOR_ID, nullable = false, insertable = true, updatable = false)
    private Integer actorId;

    // no @NotNull: FilmActor derives this, through @MapsId, from FilmActor.film, as above
    @Basic(optional = false)
    @Column(name = FilmActor.COLUMN_NAME_FILM_ID, nullable = false, insertable = true, updatable = false)
    private Integer filmId;
}
