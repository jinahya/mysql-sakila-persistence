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
-- Exercises at the advanced level on rental: several of those together: a self-join, a relational division, correlated
-- subqueries, or one result checked against another. Each poses a question, answers it, and shows what the installed
-- data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/rental_exercises_03_advanced.sql
--


-- -------------------------------------------------------------------------------------------------- SAKILA-RENTAL-A-01
-- how many rentals came back later than the film's rental duration allows.
-- +------+
-- | late |
-- +------+
-- | 8121 |
-- +------+
SELECT COUNT(*) AS late
FROM rental r
         JOIN inventory i ON i.inventory_id = r.inventory_id
         JOIN film f ON f.film_id = i.film_id
WHERE r.return_date > r.rental_date + INTERVAL f.rental_duration DAY
;

-- -------------------------------------------------------------------------------------------------- SAKILA-RENTAL-A-02
-- the five longest waits between a customer's two rentals in 2005.
-- +-------------+---------------------+---------------------+------+
-- | customer_id | previous_rental     | rental_date         | days |
-- +-------------+---------------------+---------------------+------+
-- |          61 | 2005-05-31 22:47:45 | 2005-07-27 02:50:15 |   57 |
-- |         101 | 2005-05-27 21:13:10 | 2005-07-09 00:02:46 |   43 |
-- |           4 | 2005-06-19 09:39:01 | 2005-07-28 02:10:10 |   39 |
-- |         401 | 2005-05-29 19:30:42 | 2005-07-07 04:04:26 |   39 |
-- |         433 | 2005-05-29 01:01:26 | 2005-07-07 05:30:56 |   39 |
-- +-------------+---------------------+---------------------+------+
SELECT customer_id, previous_rental, rental_date, DATEDIFF(rental_date, previous_rental) AS days
FROM (SELECT customer_id, rental_date,
             LAG(rental_date) OVER (PARTITION BY customer_id ORDER BY rental_date, rental_id) AS previous_rental
      FROM rental
      WHERE rental_date < '2006-01-01') g
WHERE previous_rental IS NOT NULL
ORDER BY days DESC, customer_id, rental_date
LIMIT 5
;

-- -------------------------------------------------------------------------------------------------- SAKILA-RENTAL-A-03
-- whether a copy was ever rented out twice at the same time.
-- +-------------+
-- | overlapping |
-- +-------------+
-- |           0 |
-- +-------------+
SELECT COUNT(*) AS overlapping
FROM rental a
         JOIN rental b ON b.inventory_id = a.inventory_id AND b.rental_id <> a.rental_id
WHERE b.rental_date >= a.rental_date
  AND b.rental_date < COALESCE(a.return_date, '9999-12-31')
  AND (b.rental_date > a.rental_date OR b.rental_id > a.rental_id)
;
