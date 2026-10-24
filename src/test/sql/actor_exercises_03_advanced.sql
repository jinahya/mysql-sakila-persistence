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


-- --------------------------------------------------------------------------------------------------- SAKILA_ACTOR_A_01
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

-- --------------------------------------------------------------------------------------------------- SAKILA_ACTOR_A_02
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

-- --------------------------------------------------------------------------------------------------- SAKILA_ACTOR_A_03
-- the longest film of each of the first five actors.
-- +----------+------------+--------------+---------------------+---------+--------------------+-------------------------------------------------------------------------------------------------------------------+--------------+-------------+----------------------+-----------------+-------------+--------+------------------+--------+--------------------------------------------------------+---------------------+
-- | actor_id | first_name | last_name    | last_update         | film_id | title              | description                                                                                                       | release_year | language_id | original_language_id | rental_duration | rental_rate | length | replacement_cost | rating | special_features                                       | last_update         |
-- +----------+------------+--------------+---------------------+---------+--------------------+-------------------------------------------------------------------------------------------------------------------+--------------+-------------+----------------------+-----------------+-------------+--------+------------------+--------+--------------------------------------------------------+---------------------+
-- |        1 | PENELOPE   | GUINESS      | 2006-02-15 04:34:33 |     499 | KING EVOLUTION     | A Action-Packed Tale of a Boy And a Lumberjack who must Chase a Madman in A Baloon                                |         2006 |           1 |                 NULL |               3 |        4.99 |    184 |            24.99 | NC-17  | Trailers,Deleted Scenes,Behind the Scenes              | 2006-02-15 05:03:42 |
-- |        2 | NICK       | WAHLBERG     | 2006-02-15 04:34:33 |     958 | WARDROBE PHANTOM   | A Action-Packed Display of a Mad Cow And a Astronaut who must Kill a Car in Ancient India                         |         2006 |           1 |                 NULL |               6 |        2.99 |    178 |            19.99 | G      | Trailers,Commentaries                                  | 2006-02-15 05:03:42 |
-- |        3 | ED         | CHASE        | 2006-02-15 04:34:33 |     996 | YOUNG LANGUAGE     | A Unbelieveable Yarn of a Boat And a Database Administrator who must Meet a Boy in The First Manned Space Station |         2006 |           1 |                 NULL |               6 |        0.99 |    183 |             9.99 | G      | Trailers,Behind the Scenes                             | 2006-02-15 05:03:42 |
-- |        4 | JENNIFER   | DAVIS        | 2006-02-15 04:34:33 |     721 | REDS POCUS         | A Lacklusture Yarn of a Sumo Wrestler And a Squirrel who must Redeem a Monkey in Soviet Georgia                   |         2006 |           1 |                 NULL |               7 |        4.99 |    182 |            23.99 | PG-13  | Trailers,Behind the Scenes                             | 2006-02-15 05:03:42 |
-- |        5 | JOHNNY     | LOLLOBRIGIDA | 2006-02-15 04:34:33 |     817 | SOLDIERS EVOLUTION | A Lacklusture Panorama of a A Shark And a Pioneer who must Confront a Student in The First Manned Space Station   |         2006 |           1 |                 NULL |               7 |        4.99 |    185 |            27.99 | R      | Trailers,Commentaries,Deleted Scenes,Behind the Scenes | 2006-02-15 05:03:42 |
-- +----------+------------+--------------+---------------------+---------+--------------------+-------------------------------------------------------------------------------------------------------------------+--------------+-------------+----------------------+-----------------+-------------+--------+------------------+--------+--------------------------------------------------------+---------------------+
SELECT a.*, f.*
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
