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
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/city_exercises_02_intermediate.sql
--


-- ---------------------------------------------------------------------------------------------------------- exercise 1
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

-- ---------------------------------------------------------------------------------------------------------- exercise 2
-- the five countries with the most cities.
-- +---------------+--------+
-- | country       | cities |
-- +---------------+--------+
-- | India         |     60 |
-- | China         |     53 |
-- | United States |     35 |
-- | Japan         |     31 |
-- | Mexico        |     30 |
-- +---------------+--------+
SELECT co.country, COUNT(*) AS cities
FROM country co
         JOIN city ci ON ci.country_id = co.country_id
GROUP BY co.country_id, co.country
ORDER BY cities DESC, co.country
LIMIT 5
;

-- ---------------------------------------------------------------------------------------------------------- exercise 3
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
