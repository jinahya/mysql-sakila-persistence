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
-- Exercises at the intermediate level on country: a join, a GROUP BY with HAVING, a subquery, or a NOT EXISTS. Each
-- poses a question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/country_exercises_02_intermediate.sql
--


-- ------------------------------------------------------------------------------------------------- SAKILA-COUNTRY-I-01
-- the countries with no city.
-- no rows.
SELECT co.*
FROM country co
WHERE NOT EXISTS (SELECT 1 FROM city ci WHERE ci.country_id = co.country_id)
;

-- ------------------------------------------------------------------------------------------------- SAKILA-COUNTRY-I-02
-- the five countries with the most customers.
-- +------------+---------------+---------------------+-----------+
-- | country_id | country       | last_update         | customers |
-- +------------+---------------+---------------------+-----------+
-- |         44 | India         | 2006-02-15 04:44:00 |        60 |
-- |         23 | China         | 2006-02-15 04:44:00 |        53 |
-- |        103 | United States | 2006-02-15 04:44:00 |        36 |
-- |         50 | Japan         | 2006-02-15 04:44:00 |        31 |
-- |         60 | Mexico        | 2006-02-15 04:44:00 |        30 |
-- +------------+---------------+---------------------+-----------+
SELECT co.*, COUNT(*) AS customers
FROM country co
         JOIN city ci ON ci.country_id = co.country_id
         JOIN address a ON a.city_id = ci.city_id
         JOIN customer cu ON cu.address_id = a.address_id
GROUP BY co.country_id
ORDER BY customers DESC, co.country LIMIT 5
;

-- ------------------------------------------------------------------------------------------------- SAKILA-COUNTRY-I-03
-- how many countries have exactly one city.
-- +-----------+
-- | countries |
-- +-----------+
-- |        42 |
-- +-----------+
SELECT COUNT(*) AS countries
FROM country co
WHERE (SELECT COUNT(*) FROM city ci WHERE ci.country_id = co.country_id) = 1
;

-- ------------------------------------------------------------------------------------------------- SAKILA-COUNTRY-I-04
-- the countries which have a city but no customer.
-- +------------+-----------+---------------------+
-- | country_id | country   | last_update         |
-- +------------+-----------+---------------------+
-- |          8 | Australia | 2006-02-15 04:44:00 |
-- +------------+-----------+---------------------+
SELECT co.*
FROM country co
WHERE EXISTS (SELECT 1 FROM city ci WHERE ci.country_id = co.country_id)
  AND NOT EXISTS (SELECT 1
                  FROM city ci
                           JOIN address a ON a.city_id = ci.city_id
                           JOIN customer cu ON cu.address_id = a.address_id
                  WHERE ci.country_id = co.country_id)
ORDER BY co.country
;
