-- -
-- #%L
-- mysql-sakila-persistence
-- %%
-- Copyright (C) 2026 Jinahya, Inc.
-- %%
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
-- 
--      http://www.apache.org/licenses/LICENSE-2.0
-- 
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
-- #L%
-- -
--
-- Exercises at the intermediate level on the business of the schema, across several tables, not on a single table; the
-- exercises on each table and view are in its own {table}_exercises.sql. Each poses a question, answers it, and shows
-- what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/_schema_02_intermediate_exercises.sql
--


-- ---------------------------------------------------------------------------------------------------------- exercise 1
-- how much each store took, through the copies it rented out.
-- +----------+----------+----------+
-- | store_id | payments | total    |
-- +----------+----------+----------+
-- |        1 |     7923 | 33679.79 |
-- |        2 |     8121 | 33726.77 |
-- +----------+----------+----------+
SELECT i.store_id, COUNT(*) AS payments, SUM(p.amount) AS total
FROM payment p
         JOIN rental r ON r.rental_id = p.rental_id
         JOIN inventory i ON i.inventory_id = r.inventory_id
GROUP BY i.store_id
ORDER BY i.store_id
;

-- ---------------------------------------------------------------------------------------------------------- exercise 2
-- how many rentals each category had, the most first.
-- +-------------+---------+
-- | name        | rentals |
-- +-------------+---------+
-- | Sports      |    1179 |
-- | Animation   |    1166 |
-- | Action      |    1112 |
-- | Sci-Fi      |    1101 |
-- | Family      |    1096 |
-- +-------------+---------+
-- ... 16 rows in all.
SELECT c.name, COUNT(*) AS rentals
FROM rental r
         JOIN inventory i ON i.inventory_id = r.inventory_id
         JOIN film_category fc ON fc.film_id = i.film_id
         JOIN category c ON c.category_id = fc.category_id
GROUP BY c.category_id, c.name
ORDER BY rentals DESC, c.name
;

-- ---------------------------------------------------------------------------------------------------------- exercise 3
-- the five countries whose customers paid the most.
-- +---------------+-----------+---------+
-- | country       | customers | total   |
-- +---------------+-----------+---------+
-- | India         |        60 | 6628.28 |
-- | China         |        53 | 5798.74 |
-- | United States |        36 | 4110.32 |
-- | Japan         |        31 | 3470.75 |
-- | Mexico        |        30 | 3307.04 |
-- +---------------+-----------+---------+
SELECT co.country, COUNT(DISTINCT cu.customer_id) AS customers, SUM(p.amount) AS total
FROM payment p
         JOIN customer cu ON cu.customer_id = p.customer_id
         JOIN address a ON a.address_id = cu.address_id
         JOIN city ci ON ci.city_id = a.city_id
         JOIN country co ON co.country_id = ci.country_id
GROUP BY co.country_id, co.country
ORDER BY total DESC LIMIT 5
;

-- ---------------------------------------------------------------------------------------------------------- exercise 4
-- the average payment of each rating.
-- +--------+-----------------+
-- | rating | average_payment |
-- +--------+-----------------+
-- | G      |            4.21 |
-- | PG     |            4.15 |
-- | PG-13  |            4.26 |
-- | R      |            4.17 |
-- | NC-17  |            4.21 |
-- +--------+-----------------+
SELECT f.rating, ROUND(AVG(p.amount), 2) AS average_payment
FROM payment p
         JOIN rental r ON r.rental_id = p.rental_id
         JOIN inventory i ON i.inventory_id = r.inventory_id
         JOIN film f ON f.film_id = i.film_id
GROUP BY f.rating
ORDER BY f.rating
;

-- ---------------------------------------------------------------------------------------------------------- exercise 5
-- the five actors whose films took the most.
-- +----------+------------+-----------+---------+
-- | actor_id | first_name | last_name | total   |
-- +----------+------------+-----------+---------+
-- |      107 | GINA       | DEGENERES | 3442.49 |
-- |      181 | MATTHEW    | CARREY    | 2742.19 |
-- |      198 | MARY       | KEITEL    | 2689.25 |
-- |       81 | SCARLETT   | DAMON     | 2655.28 |
-- |      102 | WALTER     | TORN      | 2620.62 |
-- +----------+------------+-----------+---------+
SELECT a.actor_id, a.first_name, a.last_name, SUM(p.amount) AS total
FROM actor a
         JOIN film_actor fa ON fa.actor_id = a.actor_id
         JOIN inventory i ON i.film_id = fa.film_id
         JOIN rental r ON r.inventory_id = i.inventory_id
         JOIN payment p ON p.rental_id = r.rental_id
GROUP BY a.actor_id, a.first_name, a.last_name
ORDER BY total DESC, a.actor_id LIMIT 5
;

-- ---------------------------------------------------------------------------------------------------------- exercise 6
-- the customers who have returned everything they rented, how many.
-- +-----------+
-- | customers |
-- +-----------+
-- |       440 |
-- +-----------+
SELECT COUNT(*) AS customers
FROM customer c
WHERE NOT EXISTS (SELECT 1
                  FROM rental r
                  WHERE r.customer_id = c.customer_id AND r.return_date IS NULL)
;

-- ---------------------------------------------------------------------------------------------------------- exercise 7
-- how many rentals, and how much was paid, in each month.
-- +---------+---------+----------+
-- | month   | rentals | total    |
-- +---------+---------+----------+
-- | 2005-05 |    1156 |  4823.44 |
-- | 2005-06 |    2311 |  9629.89 |
-- | 2005-07 |    6709 | 28368.91 |
-- | 2005-08 |    5686 | 24070.14 |
-- | 2006-02 |     182 |   514.18 |
-- +---------+---------+----------+
SELECT DATE_FORMAT(r.rental_date, '%Y-%m') AS month, COUNT(*) AS rentals, SUM(p.amount) AS total
FROM rental r
    LEFT JOIN payment p
ON p.rental_id = r.rental_id
GROUP BY month
ORDER BY month
;

-- ---------------------------------------------------------------------------------------------------------- exercise 8
-- the customers who rented from a store other than their own, how many.
-- +-----------+
-- | customers |
-- +-----------+
-- |       599 |
-- +-----------+
SELECT COUNT(DISTINCT c.customer_id) AS customers
FROM customer c
         JOIN rental r ON r.customer_id = c.customer_id
         JOIN inventory i ON i.inventory_id = r.inventory_id
WHERE i.store_id <> c.store_id
;
