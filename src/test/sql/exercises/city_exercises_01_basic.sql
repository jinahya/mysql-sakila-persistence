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
-- Exercises at the basic level on city: one table: a filter, a sort, a count or an aggregate. Each poses a question,
-- answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/exercises/city_exercises_01_basic.sql
--


-- ---------------------------------------------------------------------------------------------------- SAKILA_CITY_B_01
-- the cities whose name starts with Q.
-- +---------+-------------+------------+---------------------+
-- | city_id | city        | country_id | last_update         |
-- +---------+-------------+------------+---------------------+
-- |     421 | Qalyub      |         29 | 2006-02-15 04:45:25 |
-- |     422 | Qinhuangdao |         23 | 2006-02-15 04:45:25 |
-- |     423 | Qomsheh     |         46 | 2006-02-15 04:45:25 |
-- |     424 | Quilmes     |          6 | 2006-02-15 04:45:25 |
-- +---------+-------------+------------+---------------------+
SELECT *
FROM city
WHERE city LIKE 'Q%'
ORDER BY city
;

-- ---------------------------------------------------------------------------------------------------- SAKILA_CITY_B_02
-- how many city names have more than one word.
-- +--------+
-- | cities |
-- +--------+
-- |     92 |
-- +--------+
SELECT COUNT(*) AS cities
FROM city
WHERE city LIKE '% %'
;

-- ---------------------------------------------------------------------------------------------------- SAKILA_CITY_B_03
-- the five longest city names.
-- +---------+----------------------------+------------+---------------------+-------------+
-- | city_id | city                       | country_id | last_update         | city_length |
-- +---------+----------------------------+------------+---------------------+-------------+
-- |     450 | San Felipe de Puerto Plata |         27 | 2006-02-15 04:45:25 |          26 |
-- |     452 | San Juan Bautista Tuxtepec |         60 | 2006-02-15 04:45:25 |          26 |
-- |     460 | Santiago de los Caballeros |         27 | 2006-02-15 04:45:25 |          26 |
-- |      41 | Augusta-Richmond County    |        103 | 2006-02-15 04:45:25 |          23 |
-- |      71 | Berhampore (Baharampur)    |         44 | 2006-02-15 04:45:25 |          23 |
-- +---------+----------------------------+------------+---------------------+-------------+
SELECT *, CHAR_LENGTH(city) AS city_length
FROM city
ORDER BY city_length DESC, city
LIMIT 5
;
