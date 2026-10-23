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
-- Exercises at the intermediate level on the actor_info view: a join, a GROUP BY with HAVING, a subquery, or a NOT
-- EXISTS. Each poses a question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/actor_info_exercises_02_intermediate.sql
--


-- ---------------------------------------------------------------------------------------------- SAKILA-ACTOR_INFO-I-01
-- whether the view has a row for every actor.
-- +------------+-------+
-- | actor_info | actor |
-- +------------+-------+
-- |        200 |   200 |
-- +------------+-------+
SELECT (SELECT COUNT(*) FROM actor_info) AS actor_info, (SELECT COUNT(*) FROM actor) AS actor
;

-- ---------------------------------------------------------------------------------------------- SAKILA-ACTOR_INFO-I-02
-- the actors whose film_info covers every category.
-- +----------+------------+-----------+
-- | actor_id | first_name | last_name |
-- +----------+------------+-----------+
-- |       13 | UMA        | WOOD      |
-- |       72 | SEAN       | WILLIAMS  |
-- |       95 | DARYL      | WAHLBERG  |
-- |      106 | GROUCHO    | DUNST     |
-- |      107 | GINA       | DEGENERES |
-- |      127 | KEVIN      | GARLAND   |
-- |      139 | EWAN       | GOODING   |
-- |      155 | IAN        | TANDY     |
-- |      161 | HARVEY     | HOPE      |
-- |      185 | MICHAEL    | BOLGER    |
-- |      197 | REESE      | WEST      |
-- +----------+------------+-----------+
SELECT actor_id, first_name, last_name
FROM actor_info
WHERE CHAR_LENGTH(film_info) - CHAR_LENGTH(REPLACE(film_info, ':', '')) = (SELECT COUNT(*) FROM category)
ORDER BY actor_id
;

-- ---------------------------------------------------------------------------------------------- SAKILA-ACTOR_INFO-I-03
-- how many actors cover how many categories.
-- +------------+--------+
-- | categories | actors |
-- +------------+--------+
-- |         16 |     11 |
-- |         15 |     19 |
-- |         14 |     49 |
-- |         13 |     57 |
-- |         12 |     33 |
-- |         11 |     19 |
-- |         10 |      8 |
-- |          9 |      2 |
-- |          8 |      2 |
-- +------------+--------+
SELECT CHAR_LENGTH(film_info) - CHAR_LENGTH(REPLACE(film_info, ':', '')) AS categories, COUNT(*) AS actors
FROM actor_info
GROUP BY categories
ORDER BY categories DESC
;
