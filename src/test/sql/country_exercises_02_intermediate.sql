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


-- ---------------------------------------------------------------------------------------------------------- exercise 1
-- the countries with no city.
-- no rows.
SELECT co.*
FROM country co
WHERE NOT EXISTS (SELECT 1 FROM city ci WHERE ci.country_id = co.country_id)
;

-- ---------------------------------------------------------------------------------------------------------- exercise 2
-- the five countries with the most customers.
-- +---------------+-----------+
-- | country       | customers |
-- +---------------+-----------+
-- | India         |        60 |
-- | China         |        53 |
-- | United States |        36 |
-- | Japan         |        31 |
-- | Mexico        |        30 |
-- +---------------+-----------+
SELECT co.country, COUNT(*) AS customers
FROM country co
         JOIN city ci ON ci.country_id = co.country_id
         JOIN address a ON a.city_id = ci.city_id
         JOIN customer cu ON cu.address_id = a.address_id
GROUP BY co.country_id, co.country
ORDER BY customers DESC, co.country LIMIT 5
;
