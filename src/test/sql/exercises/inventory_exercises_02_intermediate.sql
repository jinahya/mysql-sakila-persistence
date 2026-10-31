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
-- Exercises at the intermediate level on inventory: a join, a GROUP BY with HAVING, a subquery, or a NOT EXISTS. Each
-- poses a question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/exercises/inventory_exercises_02_intermediate.sql
--


-- ----------------------------------------------------------------------------------------------- SAKILA_INVENTORY_I_01
-- the five films with the most copies.
-- +---------+------------------+--------------------------------------------------------------------------------------------------------------------------+--------------+-------------+----------------------+-----------------+-------------+--------+------------------+--------+-----------------------------------------------+---------------------+--------+
-- | film_id | title            | description                                                                                                              | release_year | language_id | original_language_id | rental_duration | rental_rate | length | replacement_cost | rating | special_features                              | last_update         | copies |
-- +---------+------------------+--------------------------------------------------------------------------------------------------------------------------+--------------+-------------+----------------------+-----------------+-------------+--------+------------------+--------+-----------------------------------------------+---------------------+--------+
-- |       1 | ACADEMY DINOSAUR | A Epic Drama of a Feminist And a Mad Scientist who must Battle a Teacher in The Canadian Rockies                         |         2006 |           1 |                 NULL |               6 |        0.99 |     86 |            20.99 | PG     | Deleted Scenes,Behind the Scenes              | 2006-02-15 05:03:42 |      8 |
-- |      31 | APACHE DIVINE    | A Awe-Inspiring Reflection of a Pastry Chef And a Teacher who must Overcome a Sumo Wrestler in A U-Boat                  |         2006 |           1 |                 NULL |               5 |        4.99 |     92 |            16.99 | NC-17  | Commentaries,Deleted Scenes,Behind the Scenes | 2006-02-15 05:03:42 |      8 |
-- |      69 | BEVERLY OUTLAW   | A Fanciful Documentary of a Womanizer And a Boat who must Defeat a Madman in The First Manned Space Station              |         2006 |           1 |                 NULL |               3 |        2.99 |     85 |            21.99 | R      | Trailers                                      | 2006-02-15 05:03:42 |      8 |
-- |      73 | BINGO TALENTED   | A Touching Tale of a Girl And a Crocodile who must Discover a Waitress in Nigeria                                        |         2006 |           1 |                 NULL |               5 |        2.99 |    150 |            22.99 | PG-13  | Trailers,Commentaries                         | 2006-02-15 05:03:42 |      8 |
-- |      86 | BOOGIE AMELIE    | A Lacklusture Character Study of a Husband And a Sumo Wrestler who must Succumb a Technical Writer in The Gulf of Mexico |         2006 |           1 |                 NULL |               6 |        4.99 |    121 |            11.99 | R      | Commentaries,Behind the Scenes                | 2006-02-15 05:03:42 |      8 |
-- +---------+------------------+--------------------------------------------------------------------------------------------------------------------------+--------------+-------------+----------------------+-----------------+-------------+--------+------------------+--------+-----------------------------------------------+---------------------+--------+
SELECT f.*, COUNT(*) AS copies
FROM film f
         JOIN inventory i ON i.film_id = f.film_id
GROUP BY f.film_id
ORDER BY copies DESC, f.film_id LIMIT 5
;

-- ----------------------------------------------------------------------------------------------- SAKILA_INVENTORY_I_02
-- how many copies were never rented.
-- +--------+
-- | copies |
-- +--------+
-- |      1 |
-- +--------+
SELECT COUNT(*) AS copies
FROM inventory i
WHERE NOT EXISTS (SELECT 1 FROM rental r WHERE r.inventory_id = i.inventory_id)
;

-- ----------------------------------------------------------------------------------------------- SAKILA_INVENTORY_I_03
-- how many copies each store has out right now.
-- +----------+------------------+------------+---------------------+------------+
-- | store_id | manager_staff_id | address_id | last_update         | copies_out |
-- +----------+------------------+------------+---------------------+------------+
-- |        1 |                1 |          1 | 2006-02-15 04:57:12 |         92 |
-- |        2 |                2 |          2 | 2006-02-15 04:57:12 |         91 |
-- +----------+------------------+------------+---------------------+------------+
SELECT st.*, COUNT(*) AS copies_out
FROM inventory i
         JOIN store st ON st.store_id = i.store_id
         JOIN rental r ON r.inventory_id = i.inventory_id
WHERE r.return_date IS NULL
GROUP BY st.store_id
ORDER BY st.store_id
;

-- ----------------------------------------------------------------------------------------------- SAKILA_INVENTORY_I_04
-- how many copies of each rating each store has.
-- +----------+------------------+------------+---------------------+--------+--------+
-- | store_id | manager_staff_id | address_id | last_update         | rating | copies |
-- +----------+------------------+------------+---------------------+--------+--------+
-- |        1 |                1 |          1 | 2006-02-15 04:57:12 | G      |    394 |
-- |        1 |                1 |          1 | 2006-02-15 04:57:12 | PG     |    444 |
-- |        1 |                1 |          1 | 2006-02-15 04:57:12 | PG-13  |    525 |
-- |        1 |                1 |          1 | 2006-02-15 04:57:12 | R      |    442 |
-- |        1 |                1 |          1 | 2006-02-15 04:57:12 | NC-17  |    465 |
-- |        2 |                2 |          2 | 2006-02-15 04:57:12 | G      |    397 |
-- |        2 |                2 |          2 | 2006-02-15 04:57:12 | PG     |    480 |
-- |        2 |                2 |          2 | 2006-02-15 04:57:12 | PG-13  |    493 |
-- |        2 |                2 |          2 | 2006-02-15 04:57:12 | R      |    462 |
-- |        2 |                2 |          2 | 2006-02-15 04:57:12 | NC-17  |    479 |
-- +----------+------------------+------------+---------------------+--------+--------+
SELECT st.*, f.rating, COUNT(*) AS copies
FROM inventory i
         JOIN store st ON st.store_id = i.store_id
         JOIN film f ON f.film_id = i.film_id
GROUP BY st.store_id, f.rating
ORDER BY st.store_id, f.rating
;

-- ----------------------------------------------------------------------------------------------- SAKILA_INVENTORY_I_05
-- the copies of the film 1 on the shelf right now.
-- +--------------+---------+----------+---------------------+
-- | inventory_id | film_id | store_id | last_update         |
-- +--------------+---------+----------+---------------------+
-- |            1 |       1 |        1 | 2006-02-15 05:09:17 |
-- |            2 |       1 |        1 | 2006-02-15 05:09:17 |
-- |            3 |       1 |        1 | 2006-02-15 05:09:17 |
-- |            4 |       1 |        1 | 2006-02-15 05:09:17 |
-- |            5 |       1 |        2 | 2006-02-15 05:09:17 |
-- |            7 |       1 |        2 | 2006-02-15 05:09:17 |
-- |            8 |       1 |        2 | 2006-02-15 05:09:17 |
-- +--------------+---------+----------+---------------------+
SELECT i.*
FROM inventory i
WHERE i.film_id = 1
  AND NOT EXISTS (SELECT 1 FROM rental r WHERE r.inventory_id = i.inventory_id AND r.return_date IS NULL)
ORDER BY i.store_id, i.inventory_id
;
