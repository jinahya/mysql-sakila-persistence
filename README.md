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

`store` and `staff` reference each other (see [Integrity checks during the load](#integrity-checks-during-the-load)),
so a persister gives a new row the first existing store or staff member, and the first existing language too: those
keys are `TINYINT UNSIGNED`, and MySQL does not give back an `AUTO_INCREMENT` value a rolled-back insert used, so new
ones would run out within a few runs. The schema the unit tests generate starts empty, so the `*_Persistence_Test` of
`Staff`, `Store`, `Customer`, `Inventory`, `Rental` and `Payment` are skipped there, and run as `*_Persistence_IT`
against the installed data; `Film_Persister` persists a language when there is none. Every IT run still uses up a few
of those keys; `./_docker_compose_up.sh` starts from an empty database each time.
