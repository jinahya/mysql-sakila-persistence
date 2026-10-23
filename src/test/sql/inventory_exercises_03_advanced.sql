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
-- Exercises at the advanced level on inventory: several of those together: a self-join, a relational division,
-- correlated subqueries, or one result checked against another. Each poses a question, answers it, and shows what the
-- installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/inventory_exercises_03_advanced.sql
--


-- ----------------------------------------------------------------------------------------------- SAKILA-INVENTORY-A-01
-- the five longest times a copy sat on the shelf between two rentals.
-- +--------------+---------------------+---------------------+-----------+
-- | inventory_id | return_date         | next_rental_date    | idle_days |
-- +--------------+---------------------+---------------------+-----------+
-- |          977 | 2005-07-08 20:16:03 | 2005-08-22 17:53:06 |        45 |
-- |         3921 | 2005-07-06 21:40:55 | 2005-08-20 07:48:10 |        45 |
-- |          506 | 2005-06-23 20:13:30 | 2005-08-01 09:54:41 |        39 |
-- |          517 | 2005-07-14 15:12:22 | 2005-08-22 04:10:49 |        39 |
-- |          934 | 2005-07-12 12:13:44 | 2005-08-20 20:29:46 |        39 |
-- +--------------+---------------------+---------------------+-----------+
WITH rentals AS (SELECT r.inventory_id, r.rental_date, r.return_date,
                        LEAD(r.rental_date) OVER (PARTITION BY r.inventory_id ORDER BY r.rental_date, r.rental_id) AS next_rental_date
                 FROM rental r
                 WHERE r.rental_date < '2006-01-01')
SELECT inventory_id, return_date, next_rental_date,
       DATEDIFF(next_rental_date, return_date) AS idle_days
FROM rentals
WHERE return_date IS NOT NULL
  AND next_rental_date IS NOT NULL
ORDER BY idle_days DESC, inventory_id, return_date
LIMIT 5
;

-- ----------------------------------------------------------------------------------------------- SAKILA-INVENTORY-A-02
-- how many films have had every one of their copies rented.
-- +--------------------+-------------------+
-- | films_in_inventory | every_copy_rented |
-- +--------------------+-------------------+
-- |                958 |               957 |
-- +--------------------+-------------------+
SELECT (SELECT COUNT(DISTINCT film_id) FROM inventory) AS films_in_inventory,
       (SELECT COUNT(DISTINCT i.film_id)
        FROM inventory i
        WHERE NOT EXISTS (SELECT 1
                          FROM inventory o
                          WHERE o.film_id = i.film_id
                            AND NOT EXISTS (SELECT 1 FROM rental r WHERE r.inventory_id = o.inventory_id))) AS every_copy_rented
;
