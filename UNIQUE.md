# Unique constraints

The unique constraints of the `sakila` schema, as `information_schema.table_constraints` and
`information_schema.statistics` (`non_unique = 0`) report them on the running database.

## Unique keys

Two tables declare a unique key besides their primary key.

| Table    | Constraint (index) name | Columns                                   | Mapped by                                                                       |
|----------|-------------------------|-------------------------------------------|---------------------------------------------------------------------------------|
| `rental` | `rental_date`           | `rental_date`, `inventory_id`, `customer_id` | `@Table(uniqueConstraints = ...)` on `Rental` (`Rental.java:137`)            |
| `store`  | `idx_unique_manager`    | `manager_staff_id`                        | `@Table(uniqueConstraints = ...)` on `Store` (`Store.java:100`)                 |

Both are declared without a name, so each provider names the constraint it generates; the names above are the ones
the Sakila DDL gives.

Each key is also the business key of its entity:

- `Rental` is equal by `rentalDate`, the inventory's id, and the customer's id.
- `Store` is equal by its manager staff's id. The key makes the `store` → `staff` association a one-to-one, which is
  why `Store.managerStaff` is a `@OneToOne`.

## Primary keys

Every base table has a primary key, which is unique as well. The last column counts the primary key columns which
are also foreign keys.

| Table           | Columns                   | Foreign key columns |
|-----------------|---------------------------|---------------------|
| `actor`         | `actor_id`                | 0 of 1              |
| `address`       | `address_id`              | 0 of 1              |
| `category`      | `category_id`             | 0 of 1              |
| `city`          | `city_id`                 | 0 of 1              |
| `country`       | `country_id`              | 0 of 1              |
| `customer`      | `customer_id`             | 0 of 1              |
| `film`          | `film_id`                 | 0 of 1              |
| `film_actor`    | `actor_id`, `film_id`     | 2 of 2              |
| `film_category` | `film_id`, `category_id`  | 2 of 2              |
| `film_text`     | `film_id`                 | 0 of 1              |
| `inventory`     | `inventory_id`            | 0 of 1              |
| `language`      | `language_id`             | 0 of 1              |
| `payment`       | `payment_id`              | 0 of 1              |
| `rental`        | `rental_id`               | 0 of 1              |
| `staff`         | `staff_id`                | 0 of 1              |
| `store`         | `store_id`                | 0 of 1              |

### Primary keys which refer to other tables

A primary key refers to other tables when some, or all, of its columns are also foreign keys; the entity's identity
then includes the identity of another entity. Two tables have one, and in both of them it is all of the columns, so
each row is unique per pair of the rows it refers to. No table has a primary key only partly made of foreign keys.

| Table           | PK column     | Foreign key                 | Refers to                   | Mapped by                                                       |
|-----------------|---------------|-----------------------------|-----------------------------|-----------------------------------------------------------------|
| `film_actor`    | `actor_id`    | `fk_film_actor_actor`       | `actor` (`actor_id`)        | `FilmActorId.actorId`, written through `@MapsId` `FilmActor.actor` |
| `film_actor`    | `film_id`     | `fk_film_actor_film`        | `film` (`film_id`)          | `FilmActorId.filmId`, written through `@MapsId` `FilmActor.film`   |
| `film_category` | `film_id`     | `fk_film_category_film`     | `film` (`film_id`)          | `@Id FilmCategory.filmId`; `FilmCategory.film` is read-only       |
| `film_category` | `category_id` | `fk_film_category_category` | `category` (`category_id`)  | `@Id` `FilmCategory.category` (a derived identity); `FilmCategory.categoryId` is read-only |

`film_text.film_id` is the odd one: it holds the `film_id` of `film`, one row per film, but the schema declares no
foreign key for it. The `ins_film`, `upd_film`, and `del_film` triggers on `film` keep the two tables in step instead,
and `FilmText` maps it as a plain `@Id` with no association.

## Not unique

These columns may look unique, but the schema does not say so.

| Table      | Column(s)                  | Note                                                         |
|------------|----------------------------|--------------------------------------------------------------|
| `staff`    | `username`                 | No unique key; two staff members may share a username.       |
| `customer` | `email`                    | No unique key, and nullable.                                 |
| `staff`    | `email`                    | No unique key, and nullable.                                 |
| `country`  | `country`                  | No unique key.                                               |
| `city`     | `city`, `country_id`       | No unique key; only `country_id` is indexed, for its foreign key. |
| `category` | `name`                     | No unique key.                                               |
| `language` | `name`                     | No unique key.                                               |
| `payment`  | `rental_id`                | Indexed for its foreign key only; nullable.                  |

The views have no constraints at all.

## Queries

```sql
SELECT s.table_name, s.index_name, GROUP_CONCAT(s.column_name ORDER BY s.seq_in_index) AS columns
FROM information_schema.statistics s
WHERE s.table_schema = 'sakila'
  AND s.non_unique = 0
GROUP BY s.table_name, s.index_name
ORDER BY s.table_name, s.index_name = 'PRIMARY' DESC, s.index_name;
```

```sql
SELECT p.table_name, p.column_name, f.constraint_name, f.referenced_table_name, f.referenced_column_name
FROM information_schema.key_column_usage p
         JOIN information_schema.key_column_usage f
              ON f.table_schema = p.table_schema
                  AND f.table_name = p.table_name
                  AND f.column_name = p.column_name
                  AND f.referenced_table_name IS NOT NULL
WHERE p.table_schema = 'sakila'
  AND p.constraint_name = 'PRIMARY'
ORDER BY p.table_name, p.ordinal_position;
```
