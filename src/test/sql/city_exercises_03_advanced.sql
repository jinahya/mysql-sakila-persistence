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
-- +---------+---------------------+----------------+
-- | city_id | city                | country        |
-- +---------+---------------------+----------------+
-- |     251 | Kabul               | Afghanistan    |
-- |     516 | Tafuna              | American Samoa |
-- |     493 | South Hill          | Anguilla       |
-- |     586 | Yerevan             | Armenia        |
-- |     576 | Woodridge           | Australia      |
-- |      14 | al-Manama           | Bahrain        |
-- |      53 | Bandar Seri Begawan | Brunei         |
-- |     363 | NDjamna             | Chad           |
-- |     378 | Olomouc             | Czech Republic |
-- |     530 | Tartu               | Estonia        |
-- +---------+---------------------+----------------+
SELECT ci.city_id, ci.city, co.country
FROM city ci
         JOIN country co ON co.country_id = ci.country_id
WHERE NOT EXISTS (SELECT 1 FROM city other WHERE other.country_id = ci.country_id AND other.city_id <> ci.city_id)
ORDER BY co.country, ci.city_id
LIMIT 10
;

-- ---------------------------------------------------------------------------------------------------- SAKILA-CITY-A-02
-- the cities whose customers are all inactive.
-- +---------+-------------------+
-- | city_id | city              |
-- +---------+-------------------+
-- |      24 | Amroha            |
-- |      57 | Bat Yam           |
-- |     111 | Charlotte Amalie  |
-- |     125 | Coatzacoalcos     |
-- |     139 | Daxian            |
-- |     259 | Kamyšin           |
-- |     283 | Kumbakonam        |
-- |     281 | Kütahya           |
-- |     356 | Najafabad         |
-- |     407 | Pingxiang         |
-- |     495 | Southend-on-Sea   |
-- |     512 | Székesfehérvár    |
-- |     554 | Uluberia          |
-- |     577 | Wroclaw           |
-- |     578 | Xi´angfan         |
-- +---------+-------------------+
SELECT ci.city_id, ci.city
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
