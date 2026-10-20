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

Every base table has a primary key, which is unique as well.

| Table           | Columns                   |
|-----------------|---------------------------|
| `actor`         | `actor_id`                |
| `address`       | `address_id`              |
| `category`      | `category_id`             |
| `city`          | `city_id`                 |
| `country`       | `country_id`              |
| `customer`      | `customer_id`             |
| `film`          | `film_id`                 |
| `film_actor`    | `actor_id`, `film_id`     |
| `film_category` | `film_id`, `category_id`  |
| `film_text`     | `film_id`                 |
| `inventory`     | `inventory_id`            |
| `language`      | `language_id`             |
| `payment`       | `payment_id`              |
| `rental`        | `rental_id`               |
| `staff`         | `staff_id`                |
| `store`         | `store_id`                |

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

## Query

```sql
SELECT s.table_name, s.index_name, GROUP_CONCAT(s.column_name ORDER BY s.seq_in_index) AS columns
FROM information_schema.statistics s
WHERE s.table_schema = 'sakila'
  AND s.non_unique = 0
GROUP BY s.table_name, s.index_name
ORDER BY s.table_name, s.index_name = 'PRIMARY' DESC, s.index_name;
```
