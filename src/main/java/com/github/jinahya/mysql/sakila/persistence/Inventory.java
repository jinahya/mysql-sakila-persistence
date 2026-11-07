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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * An entity class for mapping the {@value Inventory#TABLE_NAME} table.
 * <p>
 * {@snippet lang = "sql":
 * CREATE TABLE inventory (
 *   inventory_id MEDIUMINT UNSIGNED NOT NULL AUTO_INCREMENT,
 *   film_id SMALLINT UNSIGNED NOT NULL,
 *   store_id TINYINT UNSIGNED NOT NULL,
 *   last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 *   PRIMARY KEY  (inventory_id),
 *   KEY idx_fk_film_id (film_id),
 *   KEY idx_store_id_film_id (store_id,film_id),
 *   CONSTRAINT fk_inventory_store FOREIGN KEY (store_id) REFERENCES store (store_id)
 *       ON DELETE RESTRICT ON UPDATE CASCADE,
 *   CONSTRAINT fk_inventory_film FOREIGN KEY (film_id) REFERENCES film (film_id)
 *       ON DELETE RESTRICT ON UPDATE CASCADE
 * )
 *}
 * <p>
 * A row is one copy of a film at a store; a store may hold any number of copies of a film.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://dev.mysql.com/doc/sakila/en/sakila-structure-tables-inventory.html">The
 *         inventory Table</a>
 */
@Entity
@Table(name = Inventory.TABLE_NAME)
public class Inventory {

    /**
     * The name of the database table to which this entity class maps. The value is {@value}.
     */
    public static final String TABLE_NAME = "inventory";

    // -------------------------------------------------------------------------------- inventory_id

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_INVENTORY_ID} attribute
     * maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_INVENTORY_ID = "inventory_id";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_INVENTORY_ID} column. The value
     * is {@value}.
     */
    public static final String ATTRIBUTE_NAME_INVENTORY_ID = "inventoryId";

    // ------------------------------------------------------------------------------------- film_id

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_FILM} attribute maps. The
     * value is {@value}.
     */
    public static final String COLUMN_NAME_FILM_ID = "film_id";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_FILM_ID} column. The value is
     * {@value}.
     */
    public static final String ATTRIBUTE_NAME_FILM = "film";

    /**
     * The name of the read-only attribute which maps the {@value #COLUMN_NAME_FILM_ID} column. The
     * value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_FILM_ID = "filmId";

    // ------------------------------------------------------------------------------------ store_id

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_STORE} attribute maps. The
     * value is {@value}.
     */
    public static final String COLUMN_NAME_STORE_ID = "store_id";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_STORE_ID} column. The value is
     * {@value}.
     */
    public static final String ATTRIBUTE_NAME_STORE = "store";

    /**
     * The name of the read-only attribute which maps the {@value #COLUMN_NAME_STORE_ID} column. The
     * value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_STORE_ID = "storeId";

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

    // ------------------------------------------------------------------------------------- rentals

    /**
     * The name of the attribute which maps the {@link Rental rentals} of this inventory. The value
     * is {@value}.
     *
     * @see Rental#ATTRIBUTE_NAME_INVENTORY
     */
    public static final String ATTRIBUTE_NAME_RENTALS = "rentals";

    // ---------------------------------------------------------------------- STATIC_FACTORY_METHODS

    /**
     * Returns a new instance of the specified film in the specified store.
     * <p>
     * This is the only way to set the {@value #ATTRIBUTE_NAME_FILM} and the
     * {@value #ATTRIBUTE_NAME_STORE} from outside the package. An inventory is a copy of a film in
     * a store, and neither changes once it is made; for another film, or another store, persist a
     * new instance.
     *
     * @param film  a value for the {@value #ATTRIBUTE_NAME_FILM} attribute.
     * @param store a value for the {@value #ATTRIBUTE_NAME_STORE} attribute.
     * @return a new instance of {@code film} and {@code store}.
     */
    public static Inventory of(final Film film, final Store store) {
        final var instance = new Inventory();
        instance.setFilm(film);
        instance.setStore(store);
        return instance;
    }

    // -------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected Inventory() {
        super();
    }

    // ---------------------------------------------------------------------------- java.lang.Object

    @Override
    public final String toString() {
        return super.toString() + '{' +
               "inventoryId=" + inventoryId +
               ",filmId=" + filmId +
               ",storeId=" + storeId +
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
        if (!(obj instanceof Inventory that)) {
            return false;
        }
        final var thisId = getInventoryId();
        return thisId != null && thisId.equals(that.getInventoryId());
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
        return Inventory.class.hashCode();
    }

    // --------------------------------------------------------------------------------- inventoryId

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_INVENTORY_ID} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_INVENTORY_ID} attribute; {@code null} until
     *         the instance is persisted.
     */
    public Integer getInventoryId() {
        return inventoryId;
    }

    // -------------------------------------------------------------------------------------- filmId

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_FILM_ID} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_FILM_ID} attribute; {@code null} until the
     *         {@value #ATTRIBUTE_NAME_FILM} is set, or the instance is read from the database.
     * @see #setFilm(Film)
     */
    public Integer getFilmId() {
        return filmId;
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

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_FILM} attribute with the specified value,
     * along with the {@value #ATTRIBUTE_NAME_FILM_ID} attribute, which is read-only.
     *
     * @param film new value for {@value #ATTRIBUTE_NAME_FILM} attribute.
     */
    void setFilm(final Film film) {
        this.film = film;
        this.filmId = Optional.ofNullable(film)
                .map(Film::getFilmId)
                .orElse(null);
    }

    // ------------------------------------------------------------------------------------- storeId

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_STORE_ID} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_STORE_ID} attribute; {@code null} until the
     *         {@value #ATTRIBUTE_NAME_STORE} is set, or the instance is read from the database.
     * @see #setStore(Store)
     */
    public Integer getStoreId() {
        return storeId;
    }

    // --------------------------------------------------------------------------------------- store

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_STORE} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_STORE} attribute.
     */
    public Store getStore() {
        return store;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_STORE} attribute with the specified value,
     * along with the {@value #ATTRIBUTE_NAME_STORE_ID} attribute, which is read-only.
     *
     * @param store new value for {@value #ATTRIBUTE_NAME_STORE} attribute.
     */
    void setStore(final Store store) {
        this.store = store;
        this.storeId = Optional.ofNullable(store)
                .map(Store::getStoreId)
                .orElse(null);
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

    // ------------------------------------------------------------------------------------- rentals

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_RENTALS} attribute.
     * <p>
     * The attribute is the inverse side of {@link Rental#getInventory()}, mapped by
     * {@value Rental#ATTRIBUTE_NAME_INVENTORY}, so a change to the list does not reach the
     * database; a rental is linked to an inventory through the rental.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_RENTALS} attribute; {@code null} for an
     *         instance which is not read from the database.
     */
    public List<Rental> getRentals() {
        return rentals;
    }

    void setRentals(final List<Rental> rentals) {
        this.rentals = rentals;
    }

    // ---------------------------------------------------------------------------------------------
    // no @NotNull: AUTO_INCREMENT; the value is null when the provider validates at pre-persist
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COLUMN_NAME_INVENTORY_ID, nullable = false, insertable = true, updatable = false)
    private Integer inventoryId;

    // ---------------------------------------------------------------------------------------------
    // no @NotNull: read-only, null until the instance is read from the database; the film
    // carries the constraint
    @Column(name = COLUMN_NAME_FILM_ID, nullable = false, insertable = false, updatable = false)
    private Integer filmId;

    @Valid
    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = COLUMN_NAME_FILM_ID, referencedColumnName = Film.COLUMN_NAME_FILM_ID,
                nullable = false, insertable = true, updatable = true)
    private Film film;

    // ---------------------------------------------------------------------------------------------
    // no @NotNull: read-only, null until the instance is read from the database; the store
    // carries the constraint
    @Column(name = COLUMN_NAME_STORE_ID, nullable = false, insertable = false, updatable = false)
    private Integer storeId;

    @Valid
    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = COLUMN_NAME_STORE_ID, referencedColumnName = Store.COLUMN_NAME_STORE_ID,
                nullable = false, insertable = true, updatable = true)
    private Store store;

    // ---------------------------------------------------------------------------------------------
    @Basic
    @Column(name = COLUMN_NAME_LAST_UPDATE, nullable = false, insertable = false, updatable = false,
            columnDefinition = _Constants.COLUMN_DEFINITION_LAST_UPDATE)
    private LocalDateTime lastUpdate;

    // ---------------------------------------------------------------------------------------------
    @OneToMany(mappedBy = Rental.ATTRIBUTE_NAME_INVENTORY, fetch = FetchType.LAZY)
    private List<@Valid @NotNull Rental> rentals;
}
