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
import java.util.Optional;

/**
 * A composite primary key class for mapping the {@value FilmActor#COLUMN_NAME_ACTOR_ID} column and
 * the {@value FilmActor#COLUMN_NAME_FILM_ID} column, of the {@value FilmActor#TABLE_NAME} table;
 * the {@link jakarta.persistence.EmbeddedId @EmbeddedId} of {@link FilmActor}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see FilmActor
 * @see <a href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#composite-primary-keys"> 2.4.1. Composite primary keys</a> (Jakarta Persistence 3.2
 *         Specification Document)
 */
@Embeddable
public class FilmActorId {

    /**
     * The name of the attribute which maps the {@value FilmActor#COLUMN_NAME_ACTOR_ID} column. The
     * value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_ACTOR_ID = "actorId";

    /**
     * The name of the attribute which maps the {@value FilmActor#COLUMN_NAME_FILM_ID} column. The
     * value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_FILM_ID = "filmId";

    // ---------------------------------------------------------------------- STATIC_FACTORY_METHODS

    /**
     * Returns a new instance of the specified values.
     * <p>
     * An application needs this to look up a {@link FilmActor} by its primary key, as with
     * {@link jakarta.persistence.EntityManager#find(Class, Object)}; the class has no other way to
     * set the values.
     *
     * @param actorId a value for the {@value #ATTRIBUTE_NAME_ACTOR_ID} attribute.
     * @param filmId  a value for the {@value #ATTRIBUTE_NAME_FILM_ID} attribute.
     * @return a new instance of {@code actorId} and {@code filmId}.
     */
    public static FilmActorId of(final Integer actorId, final Integer filmId) {
        final var instance = new FilmActorId();
        instance.actorId = actorId;
        instance.filmId = filmId;
        return instance;
    }

    /**
     * Returns a new instance of the identifiers of the specified entities.
     *
     * @param actor the actor whose {@link Actor#getActorId() actorId} is the
     *              {@value #ATTRIBUTE_NAME_ACTOR_ID} attribute.
     * @param film  the film whose {@link Film#getFilmId() filmId} is the
     *              {@value #ATTRIBUTE_NAME_FILM_ID} attribute.
     * @return a new instance of {@code actor}'s and {@code film}'s identifiers.
     * @see #of(Integer, Integer)
     */
    public static FilmActorId from(final Actor actor, final Film film) {
        return of(
                Optional.ofNullable(actor).map(Actor::getActorId).orElse(null),
                Optional.ofNullable(film).map(Film::getFilmId).orElse(null)
        );
    }

    /**
     * Returns a new instance of the identifiers of the actor and the film of the specified entity.
     * <p>
     * The values are taken through the {@value FilmActor#ATTRIBUTE_NAME_ACTOR} and the
     * {@value FilmActor#ATTRIBUTE_NAME_FILM} associations, as {@link #from(Actor, Film)} takes
     * them; a {@code null} entity, or a {@code null} association, gives a {@code null} value.
     *
     * @param filmActor the entity whose {@link FilmActor#getActor() actor} and
     *                  {@link FilmActor#getFilm() film} give the values; may be {@code null}.
     * @return a new instance of {@code filmActor}'s actor's and film's identifiers.
     * @see #from(Actor, Film)
     */
    public static FilmActorId from(final FilmActor filmActor) {
        return from(
                Optional.ofNullable(filmActor).map(FilmActor::getActor).orElse(null),
                Optional.ofNullable(filmActor).map(FilmActor::getFilm).orElse(null)
        );
    }

    // -------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     * <p>
     * The constructor is {@code public}, rather than {@code protected}, because the specification
     * requires a primary key class to have a public no-arg constructor (Jakarta Persistence 3.2,
     * §2.4); EclipseLink relies on it, through {@link Class#getConstructor(Class[])}, to answer
     * {@link jakarta.persistence.PersistenceUnitUtil#getIdentifier}.
     */
    public FilmActorId() {
        super();
    }

    // ---------------------------------------------------------------------------- java.lang.Object

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
     * @implSpec Equality is by both {@value #ATTRIBUTE_NAME_ACTOR_ID} and
     *         {@value #ATTRIBUTE_NAME_FILM_ID}.
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
     * @implSpec The hash is over both {@value #ATTRIBUTE_NAME_ACTOR_ID} and
     *         {@value #ATTRIBUTE_NAME_FILM_ID}, consistent with {@link #equals(Object)}.
     */
    @Override
    public final int hashCode() {
        return Objects.hash(actorId, filmId);
    }

    // ------------------------------------------------------------------------------------- actorId

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_ACTOR_ID} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_ACTOR_ID} attribute.
     */
    public Integer getActorId() {
        return actorId;
    }

    // -------------------------------------------------------------------------------------- filmId

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_FILM_ID} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_FILM_ID} attribute.
     */
    public Integer getFilmId() {
        return filmId;
    }

    // ---------------------------------------------------------------------------------------------
    // no @NotNull: FilmActor derives this, through @MapsId, from FilmActor.actor, which carries
    // the constraint. When the provider derives it, relative to the PrePersist validation, is
    // the provider's to say: Hibernate ORM derives it first, EclipseLink validates first. The
    // column stays NOT NULL.
    @Basic(optional = false)
    @Column(name = FilmActor.COLUMN_NAME_ACTOR_ID, nullable = false, insertable = true,
            updatable = false)
    private Integer actorId;

    // no @NotNull: FilmActor derives this, through @MapsId, from FilmActor.film, as above
    @Basic(optional = false)
    @Column(name = FilmActor.COLUMN_NAME_FILM_ID, nullable = false, insertable = true,
            updatable = false)
    private Integer filmId;
}
