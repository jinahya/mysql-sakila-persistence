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
-- Exercises at the advanced level on city: several of those together: a self-join, a relational division, correlated
-- subqueries, or one result checked against another. Each poses a question, answers it, and shows what the installed
-- data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/city_exercises_03_advanced.sql
--


-- ---------------------------------------------------------------------------------------------------- SAKILA-CITY-A-01
-- the first ten cities which are the only city of their country.
-- +---------+---------------------+------------+---------------------+------------+----------------+---------------------+
-- | city_id | city                | country_id | last_update         | country_id | country        | last_update         |
-- +---------+---------------------+------------+---------------------+------------+----------------+---------------------+
-- |     251 | Kabul               |          1 | 2006-02-15 04:45:25 |          1 | Afghanistan    | 2006-02-15 04:44:00 |
-- |     516 | Tafuna              |          3 | 2006-02-15 04:45:25 |          3 | American Samoa | 2006-02-15 04:44:00 |
-- |     493 | South Hill          |          5 | 2006-02-15 04:45:25 |          5 | Anguilla       | 2006-02-15 04:44:00 |
-- |     586 | Yerevan             |          7 | 2006-02-15 04:45:25 |          7 | Armenia        | 2006-02-15 04:44:00 |
-- |     576 | Woodridge           |          8 | 2006-02-15 04:45:25 |          8 | Australia      | 2006-02-15 04:44:00 |
-- |      14 | al-Manama           |         11 | 2006-02-15 04:45:25 |         11 | Bahrain        | 2006-02-15 04:44:00 |
-- |      53 | Bandar Seri Begawan |         16 | 2006-02-15 04:45:25 |         16 | Brunei         | 2006-02-15 04:44:00 |
-- |     363 | NDjamna             |         21 | 2006-02-15 04:45:25 |         21 | Chad           | 2006-02-15 04:44:00 |
-- |     378 | Olomouc             |         26 | 2006-02-15 04:45:25 |         26 | Czech Republic | 2006-02-15 04:44:00 |
-- |     530 | Tartu               |         30 | 2006-02-15 04:45:25 |         30 | Estonia        | 2006-02-15 04:44:00 |
-- +---------+---------------------+------------+---------------------+------------+----------------+---------------------+
SELECT ci.*, co.*
FROM city ci
         JOIN country co ON co.country_id = ci.country_id
WHERE NOT EXISTS (SELECT 1 FROM city other WHERE other.country_id = ci.country_id AND other.city_id <> ci.city_id)
ORDER BY co.country, ci.city_id
LIMIT 10
;

-- ---------------------------------------------------------------------------------------------------- SAKILA-CITY-A-02
-- the cities whose customers are all inactive.
-- +---------+-------------------+------------+---------------------+
-- | city_id | city              | country_id | last_update         |
-- +---------+-------------------+------------+---------------------+
-- |      24 | Amroha            |         44 | 2006-02-15 04:45:25 |
-- |      57 | Bat Yam           |         48 | 2006-02-15 04:45:25 |
-- |     111 | Charlotte Amalie  |        106 | 2006-02-15 04:45:25 |
-- |     125 | Coatzacoalcos     |         60 | 2006-02-15 04:45:25 |
-- |     139 | Daxian            |         23 | 2006-02-15 04:45:25 |
-- |     259 | Kamyšin           |         80 | 2006-02-15 04:45:25 |
-- |     283 | Kumbakonam        |         44 | 2006-02-15 04:45:25 |
-- |     281 | Kütahya           |         97 | 2006-02-15 04:45:25 |
-- |     356 | Najafabad         |         46 | 2006-02-15 04:45:25 |
-- |     407 | Pingxiang         |         23 | 2006-02-15 04:45:25 |
-- |     495 | Southend-on-Sea   |        102 | 2006-02-15 04:45:25 |
-- |     512 | Székesfehérvár    |         43 | 2006-02-15 04:45:25 |
-- |     554 | Uluberia          |         44 | 2006-02-15 04:45:25 |
-- |     577 | Wroclaw           |         76 | 2006-02-15 04:45:25 |
-- |     578 | Xi´angfan         |         23 | 2006-02-15 04:45:25 |
-- +---------+-------------------+------------+---------------------+
SELECT ci.*
FROM city ci
WHERE EXISTS (SELECT 1
              FROM address a
                       JOIN customer cu ON cu.address_id = a.address_id
              WHERE a.city_id = ci.city_id)
  AND NOT EXISTS (SELECT 1
                  FROM address a
                           JOIN customer cu ON cu.address_id = a.address_id
                  WHERE a.city_id = ci.city_id
                    AND cu.active = 1)
ORDER BY ci.city, ci.city_id
;
