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
-- Exercises at the basic level on inventory: one table: a filter, a sort, a count or an aggregate. Each poses a
-- question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/inventory_exercises_01_basic.sql
--


-- ----------------------------------------------------------------------------------------------- SAKILA_INVENTORY_B_01
-- how many copies each store has.
-- +----------+------------------+------------+---------------------+--------+
-- | store_id | manager_staff_id | address_id | last_update         | copies |
-- +----------+------------------+------------+---------------------+--------+
-- |        1 |                1 |          1 | 2006-02-15 04:57:12 |   2270 |
-- |        2 |                2 |          2 | 2006-02-15 04:57:12 |   2311 |
-- +----------+------------------+------------+---------------------+--------+
SELECT st.*, COUNT(*) AS copies
FROM inventory i
         JOIN store st ON st.store_id = i.store_id
GROUP BY st.store_id
ORDER BY st.store_id
;

-- ----------------------------------------------------------------------------------------------- SAKILA_INVENTORY_B_02
-- the copies of the film 1.
-- +--------------+---------+----------+---------------------+
-- | inventory_id | film_id | store_id | last_update         |
-- +--------------+---------+----------+---------------------+
-- |            1 |       1 |        1 | 2006-02-15 05:09:17 |
-- |            2 |       1 |        1 | 2006-02-15 05:09:17 |
-- |            3 |       1 |        1 | 2006-02-15 05:09:17 |
-- |            4 |       1 |        1 | 2006-02-15 05:09:17 |
-- |            5 |       1 |        2 | 2006-02-15 05:09:17 |
-- |            6 |       1 |        2 | 2006-02-15 05:09:17 |
-- |            7 |       1 |        2 | 2006-02-15 05:09:17 |
-- |            8 |       1 |        2 | 2006-02-15 05:09:17 |
-- +--------------+---------+----------+---------------------+
SELECT *
FROM inventory
WHERE film_id = 1
ORDER BY inventory_id
;

-- ----------------------------------------------------------------------------------------------- SAKILA_INVENTORY_B_03
-- how many copies of the films 1 to 5 each store has.
-- +---------+------------------+-----------------------------------------------------------------------------------------------------------------------+--------------+-------------+----------------------+-----------------+-------------+--------+------------------+--------+----------------------------------+---------------------+----------+------------------+------------+---------------------+--------+
-- | film_id | title            | description                                                                                                           | release_year | language_id | original_language_id | rental_duration | rental_rate | length | replacement_cost | rating | special_features                 | last_update         | store_id | manager_staff_id | address_id | last_update         | copies |
-- +---------+------------------+-----------------------------------------------------------------------------------------------------------------------+--------------+-------------+----------------------+-----------------+-------------+--------+------------------+--------+----------------------------------+---------------------+----------+------------------+------------+---------------------+--------+
-- |       1 | ACADEMY DINOSAUR | A Epic Drama of a Feminist And a Mad Scientist who must Battle a Teacher in The Canadian Rockies                      |         2006 |           1 |                 NULL |               6 |        0.99 |     86 |            20.99 | PG     | Deleted Scenes,Behind the Scenes | 2006-02-15 05:03:42 |        1 |                1 |          1 | 2006-02-15 04:57:12 |      4 |
-- |       1 | ACADEMY DINOSAUR | A Epic Drama of a Feminist And a Mad Scientist who must Battle a Teacher in The Canadian Rockies                      |         2006 |           1 |                 NULL |               6 |        0.99 |     86 |            20.99 | PG     | Deleted Scenes,Behind the Scenes | 2006-02-15 05:03:42 |        2 |                2 |          2 | 2006-02-15 04:57:12 |      4 |
-- |       2 | ACE GOLDFINGER   | A Astounding Epistle of a Database Administrator And a Explorer who must Find a Car in Ancient China                  |         2006 |           1 |                 NULL |               3 |        4.99 |     48 |            12.99 | G      | Trailers,Deleted Scenes          | 2006-02-15 05:03:42 |        2 |                2 |          2 | 2006-02-15 04:57:12 |      3 |
-- |       3 | ADAPTATION HOLES | A Astounding Reflection of a Lumberjack And a Car who must Sink a Lumberjack in A Baloon Factory                      |         2006 |           1 |                 NULL |               7 |        2.99 |     50 |            18.99 | NC-17  | Trailers,Deleted Scenes          | 2006-02-15 05:03:42 |        2 |                2 |          2 | 2006-02-15 04:57:12 |      4 |
-- |       4 | AFFAIR PREJUDICE | A Fanciful Documentary of a Frisbee And a Lumberjack who must Chase a Monkey in A Shark Tank                          |         2006 |           1 |                 NULL |               5 |        2.99 |    117 |            26.99 | G      | Commentaries,Behind the Scenes   | 2006-02-15 05:03:42 |        1 |                1 |          1 | 2006-02-15 04:57:12 |      4 |
-- |       4 | AFFAIR PREJUDICE | A Fanciful Documentary of a Frisbee And a Lumberjack who must Chase a Monkey in A Shark Tank                          |         2006 |           1 |                 NULL |               5 |        2.99 |    117 |            26.99 | G      | Commentaries,Behind the Scenes   | 2006-02-15 05:03:42 |        2 |                2 |          2 | 2006-02-15 04:57:12 |      3 |
-- |       5 | AFRICAN EGG      | A Fast-Paced Documentary of a Pastry Chef And a Dentist who must Pursue a Forensic Psychologist in The Gulf of Mexico |         2006 |           1 |                 NULL |               6 |        2.99 |    130 |            22.99 | G      | Deleted Scenes                   | 2006-02-15 05:03:42 |        2 |                2 |          2 | 2006-02-15 04:57:12 |      3 |
-- +---------+------------------+-----------------------------------------------------------------------------------------------------------------------+--------------+-------------+----------------------+-----------------+-------------+--------+------------------+--------+----------------------------------+---------------------+----------+------------------+------------+---------------------+--------+
SELECT f.*, st.*, COUNT(*) AS copies
FROM inventory i
         JOIN film f ON f.film_id = i.film_id
         JOIN store st ON st.store_id = i.store_id
WHERE f.film_id BETWEEN 1 AND 5
GROUP BY f.film_id, st.store_id
ORDER BY f.film_id, st.store_id
;
