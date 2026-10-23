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
-- Exercises at the basic level on country: one table: a filter, a sort, a count or an aggregate. Each poses a question,
-- answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/country_exercises_01_basic.sql
--


-- ------------------------------------------------------------------------------------------------- SAKILA-COUNTRY-B-01
-- how many countries there are.
-- +-----------+
-- | countries |
-- +-----------+
-- |       109 |
-- +-----------+
SELECT COUNT(*) AS countries
FROM country
;

-- ------------------------------------------------------------------------------------------------- SAKILA-COUNTRY-B-02
-- the countries whose name starts with S, ordered by name.
-- +------------+----------------------------------+---------------------+
-- | country_id | country                          | last_update         |
-- +------------+----------------------------------+---------------------+
-- |         81 | Saint Vincent and the Grenadines | 2006-02-15 04:44:00 |
-- |         82 | Saudi Arabia                     | 2006-02-15 04:44:00 |
-- |         83 | Senegal                          | 2006-02-15 04:44:00 |
-- |         84 | Slovakia                         | 2006-02-15 04:44:00 |
-- |         85 | South Africa                     | 2006-02-15 04:44:00 |
-- |         86 | South Korea                      | 2006-02-15 04:44:00 |
-- |         87 | Spain                            | 2006-02-15 04:44:00 |
-- |         88 | Sri Lanka                        | 2006-02-15 04:44:00 |
-- |         89 | Sudan                            | 2006-02-15 04:44:00 |
-- |         90 | Sweden                           | 2006-02-15 04:44:00 |
-- |         91 | Switzerland                      | 2006-02-15 04:44:00 |
-- +------------+----------------------------------+---------------------+
SELECT *
FROM country
WHERE country LIKE 'S%'
ORDER BY country
;

-- ------------------------------------------------------------------------------------------------- SAKILA-COUNTRY-B-03
-- the five countries with the longest names.
-- +---------------------------------------+--------+
-- | country                               | length |
-- +---------------------------------------+--------+
-- | Congo, The Democratic Republic of the |     37 |
-- | Saint Vincent and the Grenadines      |     32 |
-- | Holy See (Vatican City State)         |     29 |
-- | United Arab Emirates                  |     20 |
-- | Virgin Islands, U.S.                  |     20 |
-- +---------------------------------------+--------+
SELECT country, CHAR_LENGTH(country) AS length
FROM country
ORDER BY length DESC, country
LIMIT 5
;
