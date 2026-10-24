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
-- Exercises at the intermediate level on the nicer_but_slower_film_list view: a join, a GROUP BY with HAVING, a
-- subquery, or a NOT EXISTS. Each poses a question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/nicer_but_slower_film_list_exercises_02_intermediate.sql
--


-- ------------------------------------------------------------------------------ SAKILA_NICER_BUT_SLOWER_FILM_LIST_I_01
-- the five categories whose films are the longest, on average.
-- +----------+-------+----------------+
-- | category | films | average_length |
-- +----------+-------+----------------+
-- | Sports   |    74 |         128.20 |
-- | Games    |    61 |         127.84 |
-- | Foreign  |    73 |         121.70 |
-- | Drama    |    62 |         120.84 |
-- | Comedy   |    58 |         115.83 |
-- +----------+-------+----------------+
SELECT category, COUNT(*) AS films, ROUND(AVG(length), 2) AS average_length
FROM nicer_but_slower_film_list
GROUP BY category
ORDER BY average_length DESC, category
LIMIT 5
;

-- ------------------------------------------------------------------------------ SAKILA_NICER_BUT_SLOWER_FILM_LIST_I_02
-- how many films are short, medium and long.
-- +--------+-------+----------+---------+
-- | band   | films | shortest | longest |
-- +--------+-------+----------+---------+
-- | short  |    96 |       46 |      59 |
-- | medium |   447 |       60 |     120 |
-- | long   |   457 |      121 |     185 |
-- +--------+-------+----------+---------+
SELECT CASE WHEN length < 60 THEN 'short' WHEN length <= 120 THEN 'medium' ELSE 'long' END AS band,
       COUNT(*) AS films, MIN(length) AS shortest, MAX(length) AS longest
FROM nicer_but_slower_film_list
GROUP BY band
ORDER BY shortest
;
