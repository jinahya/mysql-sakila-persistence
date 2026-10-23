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
-- Exercises at the advanced level on the schema as a whole, with window functions, relational division and graphs, not
-- on a single table; the exercises on each table and view are in its own {table}_exercises_*.sql. Each poses a
-- question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/_schema_exercises_03_advanced.sql
--


-- -------------------------------------------------------------------------------------------------- SAKILA-SCHEMA-A-01
-- the customer who paid the most in each of the five countries which paid the most.
-- +---------------+-------------+------------+-----------+--------+---------------+
-- | country       | customer_id | first_name | last_name | total  | country_total |
-- +---------------+-------------+------------+-----------+--------+---------------+
-- | India         |         468 | TIM        | CARY      | 175.61 |       6628.28 |
-- | China         |         550 | GUY        | BROWNLEE  | 159.68 |       5798.74 |
-- | United States |         526 | KARL       | SEAL      | 221.55 |       4110.32 |
-- | Japan         |         337 | JERRY      | JORDON    | 143.71 |       3470.75 |
-- | Mexico        |         273 | PRISCILLA  | LOWE      | 157.65 |       3307.04 |
-- +---------------+-------------+------------+-----------+--------+---------------+
WITH paid AS (SELECT co.country, cu.customer_id, cu.first_name, cu.last_name, SUM(p.amount) AS total
              FROM payment p
                       JOIN customer cu ON cu.customer_id = p.customer_id
                       JOIN address a ON a.address_id = cu.address_id
                       JOIN city ci ON ci.city_id = a.city_id
                       JOIN country co ON co.country_id = ci.country_id
              GROUP BY co.country, cu.customer_id, cu.first_name, cu.last_name),
     ranked AS (SELECT paid.*,
                       RANK() OVER (PARTITION BY country ORDER BY total DESC) AS rank_in_country, SUM(total) OVER (PARTITION BY country) AS country_total
                FROM paid)
SELECT country, customer_id, first_name, last_name, total, country_total
FROM ranked
WHERE rank_in_country = 1
ORDER BY country_total DESC LIMIT 5
;

-- -------------------------------------------------------------------------------------------------- SAKILA-SCHEMA-A-02
-- the customers who rented a film of every category.
-- +-------------+------------+-----------+
-- | customer_id | first_name | last_name |
-- +-------------+------------+-----------+
-- |          28 | CYNTHIA    | YOUNG     |
-- |          42 | CAROLYN    | PEREZ     |
-- |          75 | TAMMY      | SANDERS   |
-- |         113 | CINDY      | FISHER    |
-- |         144 | CLARA      | SHAW      |
-- +-------------+------------+-----------+
SELECT c.customer_id, c.first_name, c.last_name
FROM customer c
         JOIN rental r ON r.customer_id = c.customer_id
         JOIN inventory i ON i.inventory_id = r.inventory_id
         JOIN film_category fc ON fc.film_id = i.film_id
GROUP BY c.customer_id, c.first_name, c.last_name
HAVING COUNT(DISTINCT fc.category_id) = (SELECT COUNT(*) FROM category)
ORDER BY c.customer_id LIMIT 5
;

-- -------------------------------------------------------------------------------------------------- SAKILA-SCHEMA-A-03
-- the running total of the payments, on the first five days.
-- +------------+--------+---------------+
-- | day        | total  | running_total |
-- +------------+--------+---------------+
-- | 2005-05-24 |  29.92 |         29.92 |
-- | 2005-05-25 | 573.63 |        603.55 |
-- | 2005-05-26 | 754.26 |       1357.81 |
-- | 2005-05-27 | 684.34 |       2042.15 |
-- | 2005-05-28 | 804.04 |       2846.19 |
-- +------------+--------+---------------+
SELECT day, total, SUM (total) OVER (ORDER BY day) AS running_total
FROM (SELECT DATE (payment_date) AS day, SUM (amount) AS total FROM payment GROUP BY day) t
ORDER BY day
    LIMIT 5
;

-- -------------------------------------------------------------------------------------------------- SAKILA-SCHEMA-A-04
-- each month's payments, and the change from the month before.
-- +---------+----------+----------------------+
-- | month   | total    | change_from_previous |
-- +---------+----------+----------------------+
-- | 2005-05 |  4823.44 |                 NULL |
-- | 2005-06 |  9629.89 |              4806.45 |
-- | 2005-07 | 28368.91 |             18739.02 |
-- | 2005-08 | 24070.14 |             -4298.77 |
-- | 2006-02 |   514.18 |            -23555.96 |
-- +---------+----------+----------------------+
SELECT month, total, total - LAG(total) OVER (ORDER BY month) AS change_from_previous
FROM (SELECT DATE_FORMAT(payment_date, '%Y-%m') AS month, SUM (amount) AS total FROM payment GROUP BY month) t
ORDER BY month
;

-- -------------------------------------------------------------------------------------------------- SAKILA-SCHEMA-A-05
-- the three films rented the most at each store.
-- +----------+---------+----------------------+---------+
-- | store_id | film_id | title                | rentals |
-- +----------+---------+----------------------+---------+
-- |        1 |     535 | LOVE SUICIDES        |      20 |
-- |        1 |      55 | BARBARELLA STREETCAR |      18 |
-- |        1 |     489 | JUGGLER HARDLY       |      18 |
-- |        2 |     450 | IDOLS SNATCHERS      |      20 |
-- |        2 |     228 | DETECTIVE VISION     |      19 |
-- |        2 |     396 | HANGING DEEP         |      19 |
-- +----------+---------+----------------------+---------+
SELECT store_id, film_id, title, rentals
FROM (SELECT i.store_id,
             f.film_id,
             f.title,
             COUNT(*) AS  rentals,
             ROW_NUMBER() OVER (PARTITION BY i.store_id ORDER BY COUNT(*) DESC, f.film_id) AS n
      FROM rental r
               JOIN inventory i ON i.inventory_id = r.inventory_id
               JOIN film f ON f.film_id = i.film_id
      GROUP BY i.store_id, f.film_id, f.title) t
WHERE n <= 3
ORDER BY store_id, n
;

-- -------------------------------------------------------------------------------------------------- SAKILA-SCHEMA-A-06
-- how many actors are one film, and how many two films, away from KEVIN BLOOM; a Bacon number, Sakila having no KEVIN
-- BACON.
-- +--------+--------+
-- | number | actors |
-- +--------+--------+
-- |      1 |     99 |
-- |      2 |    100 |
-- +--------+--------+
WITH RECURSIVE bacon (actor_id, number) AS (SELECT actor_id, 0
                                            FROM actor
                                            WHERE first_name = 'KEVIN'
                                              AND last_name = 'BLOOM'
                                            UNION DISTINCT
SELECT fa2.actor_id, b.number + 1
FROM bacon b
         JOIN film_actor fa1 ON fa1.actor_id = b.actor_id
         JOIN film_actor fa2 ON fa2.film_id = fa1.film_id
WHERE b.number < 2)
SELECT number, COUNT(*) AS actors
FROM (SELECT actor_id, MIN(number) AS number FROM bacon GROUP BY actor_id) t
WHERE number > 0
GROUP BY number
ORDER BY number
;

-- -------------------------------------------------------------------------------------------------- SAKILA-SCHEMA-A-07
-- the films none of whose copies is in, right now.
-- no rows.
SELECT f.film_id, f.title, COUNT(*) AS copies
FROM film f
         JOIN inventory i ON i.film_id = f.film_id
GROUP BY f.film_id, f.title
HAVING SUM(EXISTS (SELECT 1
                   FROM rental r
                   WHERE r.inventory_id = i.inventory_id AND r.return_date IS NULL)) = COUNT(*)
ORDER BY f.film_id
;

-- -------------------------------------------------------------------------------------------------- SAKILA-SCHEMA-A-08
-- the five longest runs of days on which a customer rented every day.
-- +-------------+------------+-----------+------------+------------+------+
-- | customer_id | first_name | last_name | first_day  | last_day   | days |
-- +-------------+------------+-----------+------------+------------+------+
-- |          42 | CAROLYN    | PEREZ     | 2005-07-26 | 2005-08-02 |    8 |
-- |         181 | ANA        | BRADLEY   | 2005-07-26 | 2005-08-02 |    8 |
-- |         290 | KRISTINA   | CHAMBERS  | 2005-07-26 | 2005-08-02 |    8 |
-- |         306 | CHARLES    | KOWALSKI  | 2005-07-26 | 2005-08-02 |    8 |
-- |         373 | LOUIS      | LEONE     | 2005-07-26 | 2005-08-02 |    8 |
-- +-------------+------------+-----------+------------+------------+------+
WITH days AS (SELECT DISTINCT customer_id, DATE(rental_date) AS day
              FROM rental),
     islands AS (SELECT customer_id, day,
                        DATE_SUB(day, INTERVAL ROW_NUMBER() OVER (PARTITION BY customer_id ORDER BY day) DAY) AS island
                 FROM days)
SELECT c.customer_id, c.first_name, c.last_name, MIN(i.day) AS first_day, MAX(i.day) AS last_day, COUNT(*) AS days
FROM islands i
         JOIN customer c ON c.customer_id = i.customer_id
GROUP BY c.customer_id, c.first_name, c.last_name, i.island
ORDER BY days DESC, c.customer_id, first_day
LIMIT 5
;

-- -------------------------------------------------------------------------------------------------- SAKILA-SCHEMA-A-09
-- the countries none of whose customers rented a Music film.
-- +------------------+-----------+
-- | country          | customers |
-- +------------------+-----------+
-- | Afghanistan      |         1 |
-- | Bahrain          |         1 |
-- | Finland          |         1 |
-- | French Polynesia |         2 |
-- | Iraq             |         1 |
-- | Liechtenstein    |         1 |
-- | Madagascar       |         1 |
-- | New Zealand      |         1 |
-- | Slovakia         |         1 |
-- | Sri Lanka        |         1 |
-- | Turkmenistan     |         1 |
-- +------------------+-----------+
SELECT co.country, COUNT(*) AS customers
FROM country co
         JOIN city ci ON ci.country_id = co.country_id
         JOIN address a ON a.city_id = ci.city_id
         JOIN customer cu ON cu.address_id = a.address_id
WHERE NOT EXISTS (SELECT 1
                  FROM customer cu2
                           JOIN address a2 ON a2.address_id = cu2.address_id
                           JOIN city ci2 ON ci2.city_id = a2.city_id
                           JOIN rental r ON r.customer_id = cu2.customer_id
                           JOIN inventory i ON i.inventory_id = r.inventory_id
                           JOIN film_category fc ON fc.film_id = i.film_id
                           JOIN category c ON c.category_id = fc.category_id
                  WHERE ci2.country_id = co.country_id
                    AND c.name = 'Music')
GROUP BY co.country_id, co.country
ORDER BY co.country
;
