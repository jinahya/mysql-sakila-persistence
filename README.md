# mysql-sakila-persistence

Jakarta Persistence mappings for the [MySQL Sakila Sample Database](https://dev.mysql.com/doc/sakila/en/).

## The database

```shell
$ ./_docker_compose_up.sh
```

Brings up `mysql:9.7` (the current LTS) on `localhost:3306`, as `root` / `password`, and follows its log; Ctrl-C
takes it down again. Every run starts from an empty database: the image loads
[`docker-entrypoint-initdb.d`](docker-entrypoint-initdb.d) in name order,

| file                   | from                                                                         |
|------------------------|------------------------------------------------------------------------------|
| `01-sakila-schema.sql` | `sakila-db/sakila-schema.sql` of https://downloads.mysql.com/docs/sakila-db.tar.gz |
| `02-sakila-data.sql`   | `sakila-db/sakila-data.sql` of the same                                      |

both unchanged.

```shell
$ docker exec -it mysql-sakila mysql -uroot -ppassword -Dsakila
```

### Integrity checks during the load

Both scripts wrap their whole body in

```sql
SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
-- ...
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
```

and need it, for different reasons:

- `01-sakila-schema.sql` creates tables whose foreign keys reference tables created after them -- `address`
  references `city`, say -- which MySQL allows only with the checks off.
- `02-sakila-data.sql` inserts the tables in alphabetical order, so many rows arrive before the rows they reference
  (`address` before `city`, `customer` before `store`, `payment` before `rental`). Another order would cure all of
  those but one.

That one is a cycle in the schema itself. `staff.store_id` is a `NOT NULL` reference to `store`, and
`store.manager_staff_id` is a `NOT NULL` (and unique) reference to `staff`; both keys are `AUTO_INCREMENT`. The first
store needs a staff member who already exists, and the first staff member needs a store which already exists, so
neither can be inserted first, and MySQL has no deferred constraints to let both in within one transaction. The data
script inserts `staff` (with `store_id = 1`) and then `store` (with `manager_staff_id = 1`) with the checks off; there
is no other way in.

Jakarta Persistence cannot turn the checks off, so it can add a store or a staff member only once both already
exist: a new staff member is given an existing store, and a new store a new manager who works at an existing store.
On an empty database -- the schema the unit tests generate -- there is no first pair to start from. The two ways
around that:

- seed one store and its manager into the generated schema with H2's `SET REFERENTIAL_INTEGRITY FALSE`, the way the
  data script does; the mappings stay as the DDL is, or
- map one side of the cycle -- `Store.managerStaff`, say -- as optional, without `@NotNull`, so that a store can be
  persisted first and given its manager by an `UPDATE`; the mapping is then looser than the DDL, Bean Validation no
  longer catches a store without a manager, and the generated schema differs from the installed one.

Neither is in place: the generated schema starts empty, and the unit tests which need a store or a staff member are
skipped (see [The tests](#the-tests)).

The seed this project once carried, as `src/test/resources/META-INF/sakila/load-test.sql`, named by the `sakila-test`
unit's `jakarta.persistence.sql-load-script-source` beside
`jakarta.persistence.schema-generation.database.action=drop-and-create`. It ran right after the schema was generated,
into an empty database, so every generated key is `1`; one statement per line, and no comments, for both providers'
script readers. The `language` row is no longer needed: `Film_Persister` persists one when there is none.

```sql
SET REFERENTIAL_INTEGRITY FALSE;
INSERT INTO country (country) VALUES ('Seedland');
INSERT INTO city (city, country_id) VALUES ('Seed City', 1);
INSERT INTO address (address, district, city_id, phone, location) VALUES ('1 Seed Way', 'Seed', 1, '000', X'000000000101000000000000000000F03F0000000000000040');
INSERT INTO store (manager_staff_id, address_id) VALUES (1, 1);
INSERT INTO staff (first_name, last_name, address_id, store_id, active, username) VALUES ('Seed', 'Manager', 1, 1, TRUE, 'seed');
INSERT INTO language (name) VALUES ('English');
SET REFERENTIAL_INTEGRITY TRUE;
```

## What the data implies

[`src/test/sql`](src/test/sql) holds a profiling script per table and view, a section per column; each query says
what it looks for and what the installed data gives back.

```shell
$ docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/payment.sql
```

Some of what they find is a rule of the schema, and some only a habit of the generated data; the mappings follow the
former and leave the latter alone.

The exercises are listed, with their ids, in [EXERCISES.asciidoc](EXERCISES.asciidoc), and registered in
[EXERCISES.yaml](EXERCISES.yaml); the `*_exercises_*.sql` files answer them in SQL, and the
`*_Exercises_*_PersistenceIT` classes in JPQL and with the Criteria API.

### A payment, and its rental

`payment.customer_id`, `staff_id` and `payment_date` are `NOT NULL`; `rental_id` is nullable. The foreign keys say
why:

| constraint            | `ON DELETE`  | `ON UPDATE` |
|-----------------------|--------------|-------------|
| `fk_payment_rental`   | `SET NULL`   | `CASCADE`   |
| `fk_payment_customer` | `RESTRICT`   | `CASCADE`   |
| `fk_payment_staff`    | `RESTRICT`   | `CASCADE`   |

A customer, or a staff member, cannot be deleted while a payment refers to them; a rental can, and its payments stay,
with `rental_id` set to `NULL`. A payment keeps its own `customer_id`, rather than reaching the customer through the
rental, so that it still has one then. In the installed data every payment has a rental, every rental is paid for
once, and the customer of every payment is the customer of its rental.

So a payment is created with a rental, or, for an outstanding fee, with a customer alone, and `rental_id` otherwise
goes `NULL` only when the rental is deleted. `Payment.of(staff, rental)`, whose customer is the one of the rental,
and `Payment.of(staff, customer)`, without a rental, are the only ways to set the customer, the staff and the rental
from outside the package, so the customer of a payment cannot differ from the one of its rental; `Payment.rental`
stays optional, for the rows whose rental is gone. The amount is set on its own, and the `payment_date` trigger sets
the payment date.

### Three dates

For every payment with a rental,

```text
rental_date = payment_date < return_date        15861 payments
rental_date = payment_date, not returned (NULL)   183 payments
```

to the second. Taken literally, a rental is paid for once, up front, at the moment it is made. The amounts say
otherwise. Compared with the `rental_rate` of the rented film, by how the rental ended -- *in time* when
`return_date` is within `film.rental_duration` days of `rental_date`, *late* when it is not, and *not returned* when
`return_date` is `NULL` --

| rental       | payments | `amount` = rate | `amount` > rate | `amount` < rate |
|--------------|----------|-----------------|-----------------|-----------------|
| in time      | 8592     | 8592            | 0               | 0               |
| late         | 7269     | 0               | 7269            | 0               |
| not returned | 183      | 135             | 24              | 24, all `0.00`  |

Every late return is charged more than the rate -- a late fee, which is known only once the copy is back -- and yet
the payment is dated at the rental. A copy not returned yet is charged the rate, nothing, or more than the rate, a
late fee for a return which has not happened. The data was generated with one payment per rental, carrying the
whole amount and the rental's timestamp; `payment_date = rental_date` is a habit of that generator, not a rule of the
business, where a late fee, or an outstanding payment without a rental, is paid at or after the return. The mappings
check none of it: no constraint ties `payment_date` to `rental_date` or `return_date`. `payment.sql` keeps the
queries, as a description of the installed data.

### The amount

Nothing in the schema changes a payment once it is inserted: the only trigger on `payment` sets `payment_date` before
an insert, and no routine writes to it. `get_customer_balance` reads payments as a ledger -- the rates of the rentals,
plus a dollar for every day overdue, less every payment made -- so a late fee would be a payment of its own, not a
change to an earlier one. Nor does the data show a change: `last_update`, which MySQL sets on every `UPDATE`, falls
within the twelve minutes of the original load for every payment.

And yet, of a payment, only the amount is left to change. The customer, the staff and the rental are set once, by
the factories; the trigger sets the payment date; and the `SET NULL` of a deleted rental does not touch
`last_update` -- a cascaded foreign key action never does. Were the amount fixed too, `last_update` would always be
the time of the insert. So `Payment.amount` stays updatable, with a public `setAmount`, for now and for no concrete
reason: the most a change could be is the correction of a mistyped amount, and `last_update` would tell when. Every
table in the schema has a `last_update`, though, so its presence here may say nothing about the amount.

## Building

Two persistence providers, one profile each; `jakarta-ee-11-hibernate-orm` is active by default.

```shell
$ ./_mvn_jakarta_ee_11_test_hibernate_orm.sh              # unit tests, in-memory H2
$ ./_mvn_jakarta_ee_11_test_eclipselink.sh
$ ./_mvn_jakarta_ee_11_verify_hibernate_orm.sh            # + integration tests, against the database above
$ ./_mvn_jakarta_ee_11_verify_eclipselink.sh
$ ./_mvn_jakarta_ee_11_verify_notests_hibernate_orm.sh    # no database needed
$ ./_mvn_jakarta_ee_11_verify_notests_eclipselink.sh
```

## The providers

What one provider does and the other does not, as far as these mappings and tests run into it.

### Hibernate ORM

- `getReference`, and a lazy to-one association, give a proxy: a generated subclass of the entity
  (`FilmCategory$HibernateProxy`), whose own fields are never populated. It intercepts every method which is neither
  `private` nor `final`, loads the row, and delegates; the `final` `equals` and `hashCode` it does not intercept, so
  whatever those read off a proxy, they read through a getter which is not `private`. That is why
  `FilmCategory.getFilmId()` and `getCategoryId()` are package-private: made `private`, they answer the proxy's `null`,
  and `persisted.equals(reference)` and `reference.equals(persisted)` both come out `false`, with no exception.
  `_Entity_Persistence_Test#_persist_EqualsReference` and `#_persist_ReferenceEquals` check both directions, against a
  real proxy, for every entity with a randomizer and a persister; no bytecode enhancement is configured, which would
  change this.
- An identifier from an `IDENTITY` column is written, and readable, as `persist` is called.

### EclipseLink

- No subclass proxies. With weaving off, `eclipselink.woven=false`, the default here, the two tests above pass even
  with `FilmCategory`'s getters `private`; the package-private ones are for Hibernate ORM.
- An identifier from an `IDENTITY` column is readable only once the instance is flushed.
- `PrePersist` validation runs before an identifier derived through `@MapsId` is filled; Hibernate ORM derives it
  first. So `FilmActor.id`, and the components of `FilmActorId`, carry no `@Valid` or `@NotNull`: under EclipseLink
  they would fail as `null` every time. The constraints are on `FilmActor.actor` and `film`, from which the identifier
  is derived.
- `PersistenceUnitUtil.getIdentifier` instantiates an id class through its public constructors only; one with a
  `protected` no-arg constructor fails with `EclipseLink-222`, a `NoSuchMethodException`. Hibernate ORM hands back
  the identifier the entity already holds. The specification requires a primary key class to have a public no-arg
  constructor (Jakarta Persistence 3.2, §2.4), so `FilmActorId` and `FilmCategoryId` declare it `public`.

## The mappings

Package `com.github.jinahya.mysql.sakila.persistence`, one class per table or view, each standalone (no shared entity
superclass), in the member order of the sibling `oracle-sample-schemas-persistence`.

| tables                                                                             | views (`@Entity`)                     | views (plain classes)                                  |
|------------------------------------------------------------------------------------|---------------------------------------|--------------------------------------------------------|
| `Actor` `Address` `Category` `City` `Country` `Customer` `Film` `FilmText`          | `ActorInfo` (`actor_id`)              | `FilmList`                                             |
| `Inventory` `Language` `Payment` `Rental` `Staff` `Store`                           | `CustomerList` (`ID`)                 | `NicerButSlowerFilmList`                               |
| `FilmActor` (`@EmbeddedId` `FilmActorId`, two `@MapsId`)                            | `StaffList` (`ID`)                    | `SalesByStore`                                         |
| `FilmCategory` (`@IdClass` `FilmCategoryId`, read-only associations)                | `SalesByFilmCategory` (`category`)    |                                                        |

A view is an entity only when its definition makes a column unique: `film_list` groups by film *and* category, and
`sales_by_store` groups by a store id it does not select, so those three are plain read-only classes, with a public
constructor taking every column in the view's order. Equality is by the primary key, or by the business key where the
table declares one (`Rental`, `Store`); every `UNIQUE` key is declared, unnamed, on `@Table`.

`FilmActor.of(actor, film)` sets the two associations and nothing else; both providers fill the `@EmbeddedId` from
them, through `@MapsId`, as the instance is persisted, so the identifier is never set by hand.

Notes on the column types Jakarta Persistence has no type for:

- `film.rating` (`ENUM`) is `Film.Rating`, through `Film.RatingConverter`; `film.special_features` (`SET`) is a
  `String`, which `Film.SpecialFeature.parse` / `format` read and write.
- `address.location` (`GEOMETRY`) is a `byte[]` in MySQL's internal format, `SRID` then `WKB`;
  `Address.newLocation(srid, x, y)` builds a point.
- `release_year` (`YEAR`) is an `Integer`; the IT unit's URL carries `yearIsDateType=false` for it.
- every `last_update` is read-only, filled by the database; its `columnDefinition` exists only for the schema the
  unit tests generate.

## The tests

Each mapped class has a `*_Test` (`toString`, `equals`/`hashCode`, accessors), a `*_Randomizer`, and, for an entity, a
`*_Persistence_Test` against the `sakila-test` unit (H2, schema generated from the mappings) and a
`*_Persistence_IT` against the `sakila-it` unit (the database above). A table also has a `*_Persister`, which persists
whatever the instance references first. Every test runs in a transaction which is rolled back.

There is no CDI container. `__Persistence_TestUtils` bootstraps a unit, by name, the Java SE way, through
`Persistence.createEntityManagerFactory`; each of its `apply...` methods opens a factory and an entity manager, and
closes both, around the work it is given. `_Entity_Persistence_Test` and `_Entity_Persistence_IT` call them with their
own unit's name, and a test which extends neither, `FilmList_IT` say, calls them directly.

`store` and `staff` reference each other (see [Integrity checks during the load](#integrity-checks-during-the-load)),
so a persister gives a new row the first existing store or staff member, and the first existing language too: those
keys are `TINYINT UNSIGNED`, and MySQL does not give back an `AUTO_INCREMENT` value a rolled-back insert used, so new
ones would run out within a few runs. The schema the unit tests generate starts empty, so the `*_Persistence_Test` of
`Staff`, `Store`, `Customer`, `Inventory`, `Rental` and `Payment` are skipped there, and run as `*_Persistence_IT`
against the installed data; `Film_Persister` persists a language when there is none. Every IT run still uses up a few
of those keys; `./_docker_compose_up.sh` starts from an empty database each time.
