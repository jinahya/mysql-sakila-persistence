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
-- Exercises at the advanced level on the film_list view: several of those together: a self-join, a relational division,
-- correlated subqueries, or one result checked against another. Each poses a question, answers it, and shows what the
-- installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/exercises/film_list_exercises_03_advanced.sql
--


-- ----------------------------------------------------------------------------------------------- SAKILA_FILM_LIST_A_01
-- how many films of each category are longer than their category's average.
-- +-------------+---------------------+
-- | category    | longer_than_average |
-- +-------------+---------------------+
-- | Sports      |                  42 |
-- | Foreign     |                  37 |
-- | Family      |                  36 |
-- | New         |                  34 |
-- | Animation   |                  32 |
-- | Documentary |                  31 |
-- | Drama       |                  31 |
-- | Sci-Fi      |                  31 |
-- | Action      |                  30 |
-- | Games       |                  30 |
-- | Travel      |                  30 |
-- | Children    |                  28 |
-- | Comedy      |                  28 |
-- | Horror      |                  28 |
-- | Classics    |                  27 |
-- | Music       |                  25 |
-- +-------------+---------------------+
SELECT l.category, COUNT(*) AS longer_than_average
FROM film_list l
WHERE l.length > (SELECT AVG(o.length) FROM film_list o WHERE o.category = l.category)
GROUP BY l.category
ORDER BY longer_than_average DESC, l.category
;
