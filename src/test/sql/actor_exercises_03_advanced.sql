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


-- --------------------------------------------------------------------------------------------------- SAKILA-ACTOR-A-01
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
ORDER BY a.actor_id LIMIT 5
;

-- --------------------------------------------------------------------------------------------------- SAKILA-ACTOR-A-02
-- the actors in the top seven film counts, ties included.
-- +----------+------------+-------------+-------+-----------+
-- | actor_id | first_name | last_name   | films | film_rank |
-- +----------+------------+-------------+-------+-----------+
-- |      107 | GINA       | DEGENERES   |    42 |         1 |
-- |      102 | WALTER     | TORN        |    41 |         2 |
-- |      198 | MARY       | KEITEL      |    40 |         3 |
-- |      181 | MATTHEW    | CARREY      |    39 |         4 |
-- |       23 | SANDRA     | KILMER      |    37 |         5 |
-- |       81 | SCARLETT   | DAMON       |    36 |         6 |
-- |       13 | UMA        | WOOD        |    35 |         7 |
-- |       37 | VAL        | BOLGER      |    35 |         7 |
-- |       60 | HENRY      | BERRY       |    35 |         7 |
-- |      106 | GROUCHO    | DUNST       |    35 |         7 |
-- |      144 | ANGELA     | WITHERSPOON |    35 |         7 |
-- |      158 | VIVIEN     | BASINGER    |    35 |         7 |
-- +----------+------------+-------------+-------+-----------+
SELECT actor_id, first_name, last_name, films, film_rank
FROM (SELECT a.actor_id,
             a.first_name,
             a.last_name,
             COUNT(*)                                   AS films,
             DENSE_RANK() OVER (ORDER BY COUNT(*) DESC) AS film_rank
      FROM actor a
               JOIN film_actor fa ON fa.actor_id = a.actor_id
      GROUP BY a.actor_id, a.first_name, a.last_name) ranked
WHERE film_rank <= 7
ORDER BY film_rank, actor_id
;

-- --------------------------------------------------------------------------------------------------- SAKILA-ACTOR-A-03
-- the longest film of each of the first five actors.
-- +----------+------------+--------------+--------------------+--------+
-- | actor_id | first_name | last_name    | title              | length |
-- +----------+------------+--------------+--------------------+--------+
-- |        1 | PENELOPE   | GUINESS      | KING EVOLUTION     |    184 |
-- |        2 | NICK       | WAHLBERG     | WARDROBE PHANTOM   |    178 |
-- |        3 | ED         | CHASE        | YOUNG LANGUAGE     |    183 |
-- |        4 | JENNIFER   | DAVIS        | REDS POCUS         |    182 |
-- |        5 | JOHNNY     | LOLLOBRIGIDA | SOLDIERS EVOLUTION |    185 |
-- +----------+------------+--------------+--------------------+--------+
SELECT a.actor_id, a.first_name, a.last_name, f.title, f.length
FROM actor a
         JOIN film_actor fa ON fa.actor_id = a.actor_id
         JOIN film f ON f.film_id = fa.film_id
WHERE a.actor_id BETWEEN 1 AND 5
  AND f.length = (SELECT MAX(f2.length)
                  FROM film_actor fa2
                           JOIN film f2 ON f2.film_id = fa2.film_id
                  WHERE fa2.actor_id = a.actor_id)
ORDER BY a.actor_id, f.title
;
