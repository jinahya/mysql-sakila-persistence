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
-- Exercises at the advanced level on actor: several of those together: a self-join, a relational division, correlated
-- subqueries, or one result checked against another. Each poses a question, answers it, and shows what the installed
-- data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/actor_exercises_03_advanced.sql
--


-- ---------------------------------------------------------------------------------------------------------- exercise 1
-- the actors who appear in a film of every rating.
-- +----------+------------+--------------+---------------------+
-- | actor_id | first_name | last_name    | last_update         |
-- +----------+------------+--------------+---------------------+
-- |        1 | PENELOPE   | GUINESS      | 2006-02-15 04:34:33 |
-- |        2 | NICK       | WAHLBERG     | 2006-02-15 04:34:33 |
-- |        3 | ED         | CHASE        | 2006-02-15 04:34:33 |
-- |        4 | JENNIFER   | DAVIS        | 2006-02-15 04:34:33 |
-- |        5 | JOHNNY     | LOLLOBRIGIDA | 2006-02-15 04:34:33 |
-- +----------+------------+--------------+---------------------+
SELECT a.*
FROM actor a
         JOIN film_actor fa ON fa.actor_id = a.actor_id
         JOIN film f ON f.film_id = fa.film_id
GROUP BY a.actor_id, a.first_name, a.last_name
HAVING COUNT(DISTINCT f.rating) = (SELECT COUNT(DISTINCT rating) FROM film)
ORDER BY a.actor_id
LIMIT 5
;
