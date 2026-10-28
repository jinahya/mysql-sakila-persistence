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
-- Exercises at the intermediate level on city: a join, a GROUP BY with HAVING, a subquery, or a NOT EXISTS. Each poses
-- a question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/exercises/city_exercises_02_intermediate.sql
--


-- ---------------------------------------------------------------------------------------------------- SAKILA_CITY_I_01
-- the cities of Canada.
-- +---------+---------------+------------+---------------------+
-- | city_id | city          | country_id | last_update         |
-- +---------+---------------+------------+---------------------+
-- |     179 | Gatineau      |         20 | 2006-02-15 04:45:25 |
-- |     196 | Halifax       |         20 | 2006-02-15 04:45:25 |
-- |     300 | Lethbridge    |         20 | 2006-02-15 04:45:25 |
-- |     313 | London        |         20 | 2006-02-15 04:45:25 |
-- |     383 | Oshawa        |         20 | 2006-02-15 04:45:25 |
-- |     430 | Richmond Hill |         20 | 2006-02-15 04:45:25 |
-- |     565 | Vancouver     |         20 | 2006-02-15 04:45:25 |
-- +---------+---------------+------------+---------------------+
SELECT ci.*
FROM city ci
         JOIN country co ON co.country_id = ci.country_id
WHERE co.country = 'Canada'
ORDER BY ci.city
;

-- ---------------------------------------------------------------------------------------------------- SAKILA_CITY_I_02
-- the five countries with the most cities.
-- +------------+---------------+---------------------+--------+
-- | country_id | country       | last_update         | cities |
-- +------------+---------------+---------------------+--------+
-- |         44 | India         | 2006-02-15 04:44:00 |     60 |
-- |         23 | China         | 2006-02-15 04:44:00 |     53 |
-- |        103 | United States | 2006-02-15 04:44:00 |     35 |
-- |         50 | Japan         | 2006-02-15 04:44:00 |     31 |
-- |         60 | Mexico        | 2006-02-15 04:44:00 |     30 |
-- +------------+---------------+---------------------+--------+
SELECT co.*, COUNT(*) AS cities
FROM country co
         JOIN city ci ON ci.country_id = co.country_id
GROUP BY co.country_id
ORDER BY cities DESC, co.country LIMIT 5
;

-- ---------------------------------------------------------------------------------------------------- SAKILA_CITY_I_03
-- how many cities have no address.
-- +--------+
-- | cities |
-- +--------+
-- |      1 |
-- +--------+
SELECT COUNT(*) AS cities
FROM city ci
WHERE NOT EXISTS (SELECT 1 FROM address a WHERE a.city_id = ci.city_id)
;

-- ---------------------------------------------------------------------------------------------------- SAKILA_CITY_I_04
-- the cities with more than one address.
-- +---------+------------+------------+---------------------+-----------+
-- | city_id | city       | country_id | last_update         | addresses |
-- +---------+------------+------------+---------------------+-----------+
-- |      42 | Aurora     |        103 | 2006-02-15 04:45:25 |         2 |
-- |     300 | Lethbridge |         20 | 2006-02-15 04:45:25 |         2 |
-- |     312 | London     |        102 | 2006-02-15 04:45:25 |         2 |
-- |     576 | Woodridge  |          8 | 2006-02-15 04:45:25 |         2 |
-- +---------+------------+------------+---------------------+-----------+
SELECT ci.*, COUNT(*) AS addresses
FROM city ci
         JOIN address a ON a.city_id = ci.city_id
GROUP BY ci.city_id
HAVING COUNT(*) > 1
ORDER BY addresses DESC, ci.city
;

-- ---------------------------------------------------------------------------------------------------- SAKILA_CITY_I_05
-- the city names found in more than one country.
-- +--------+-----------+
-- | city   | countries |
-- +--------+-----------+
-- | London |         2 |
-- +--------+-----------+
SELECT city, COUNT(DISTINCT country_id) AS countries
FROM city
GROUP BY city
HAVING COUNT(DISTINCT country_id) > 1
ORDER BY countries DESC, city
;
