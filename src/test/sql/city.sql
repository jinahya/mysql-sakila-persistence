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
---
--
-- Profiling queries on city, a section per column. Each says what it looks for and what the result should be;
-- 'no rows' marks a check that passes when nothing comes back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/city.sql
--


-- ------------------------------------------------------------------------------------------------------------- city_id
-- how many rows, and the smallest and the largest city_id; 600 cities, 1 to 600.
SELECT COUNT(*), MIN(city_id), MAX(city_id)
FROM city
;

-- a city_id whose next value is missing, below the largest.
-- no rows: city_id runs from 1 without a gap.
SELECT a.city_id + 1 AS missing_from
FROM city a
WHERE a.city_id < (SELECT MAX(city_id) FROM city)
  AND NOT EXISTS (SELECT 1
                  FROM city b
                  WHERE b.city_id = a.city_id + 1)
;


-- ---------------------------------------------------------------------------------------------------------------- city
-- the shortest and the longest city; 3 and 26 characters.
SELECT MIN(CHAR_LENGTH(city)), MAX(CHAR_LENGTH(city))
FROM city
;

-- a city which is blank, or carries leading or trailing spaces.
-- no rows: every city is trimmed and not blank.
SELECT city_id, CONCAT('[', city, ']') AS city
FROM city
WHERE TRIM(city) = ''
   OR CHAR_LENGTH(city) <> CHAR_LENGTH(TRIM(city))
;

-- the schema has no unique key on (city, country_id); a city name a country has more than once.
-- no rows: no country has two cities of a name.
SELECT country_id, city, COUNT(*) AS cities
FROM city
GROUP BY country_id, city
HAVING COUNT(*) > 1
;

-- a city name shared by different countries; London alone, of the United Kingdom and of Canada.
SELECT city, COUNT(*) AS cities
FROM city
GROUP BY city
HAVING COUNT(*) > 1
ORDER BY city ASC
;


-- ---------------------------------------------------------------------------------------------------------- country_id
-- a country_id which refers to no country; the foreign key forbids it.
-- no rows: every country_id refers to a country.
SELECT t.city_id, t.country_id
FROM city t
WHERE NOT EXISTS (SELECT 1
                  FROM country p
                  WHERE p.country_id = t.country_id)
;

-- the countries with the most cities; country 44, India, has 60.
SELECT country_id, COUNT(*) AS cities
FROM city
GROUP BY country_id
ORDER BY cities DESC, country_id ASC
LIMIT 5
;


-- --------------------------------------------------------------------------------------------------------- last_update
-- the database sets last_update on insert, and again on every update; the range the installed rows carry.
-- 2006-02-15 04:45:25, a single moment, as every row was loaded at once
SELECT MIN(last_update), MAX(last_update)
FROM city
;


-- ---------------------------------------------------------------------------------------------------------------------
-- a city no address is in.
-- one row: city 313, the London of Canada.
SELECT c.city_id, c.city
FROM city c
WHERE NOT EXISTS (SELECT 1
                  FROM address a
                  WHERE a.city_id = c.city_id)
;
