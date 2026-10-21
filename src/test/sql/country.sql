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
-- Profiling queries on country, a section per column. Each says what it looks for and what the result should be;
-- 'no rows' marks a check that passes when nothing comes back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/country.sql
--


-- ---------------------------------------------------------------------------------------------------------- country_id
-- how many rows, and the smallest and the largest country_id; 109 countries, 1 to 109.
SELECT COUNT(*), MIN(country_id), MAX(country_id)
FROM country
;

-- a country_id whose next value is missing, below the largest.
-- no rows: country_id runs from 1 without a gap.
SELECT a.country_id + 1 AS missing_from
FROM country a
WHERE a.country_id < (SELECT MAX(country_id) FROM country)
  AND NOT EXISTS (SELECT 1
                  FROM country b
                  WHERE b.country_id = a.country_id + 1)
;


-- ------------------------------------------------------------------------------------------------------------- country
-- the shortest and the longest country; 4 and 37 characters.
SELECT MIN(CHAR_LENGTH(country)), MAX(CHAR_LENGTH(country))
FROM country
;

-- a country which is blank, or carries leading or trailing spaces.
-- no rows: every country is trimmed and not blank.
SELECT country_id, CONCAT('[', country, ']') AS country
FROM country
WHERE TRIM(country) = ''
   OR CHAR_LENGTH(country) <> CHAR_LENGTH(TRIM(country))
;

-- the schema has no unique key on country; a name more than one row has.
-- no rows: every country is unique.
SELECT country, COUNT(*)
FROM country
GROUP BY country
HAVING COUNT(*) > 1
;


-- --------------------------------------------------------------------------------------------------------- last_update
-- the database sets last_update on insert, and again on every update; the range the installed rows carry.
-- 2006-02-15 04:44:00, a single moment, as every row was loaded at once
SELECT MIN(last_update), MAX(last_update)
FROM country
;


-- ---------------------------------------------------------------------------------------------------------------------
-- a country with no city.
-- no rows: every country has at least one city.
SELECT c.country_id, c.country
FROM country c
WHERE NOT EXISTS (SELECT 1
                  FROM city t
                  WHERE t.country_id = c.country_id)
;
