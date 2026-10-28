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
-- Exercises at the intermediate level on film_actor: a join, a GROUP BY with HAVING, a subquery, or a NOT EXISTS. Each
-- poses a question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/exercises/film_actor_exercises_02_intermediate.sql
--


-- ---------------------------------------------------------------------------------------------- SAKILA_FILM_ACTOR_I_01
-- the five films with the most actors.
-- +---------+-------------------+-------------------------------------------------------------------------------------------------------------------------+--------------+-------------+----------------------+-----------------+-------------+--------+------------------+--------+-----------------------------+---------------------+--------+
-- | film_id | title             | description                                                                                                             | release_year | language_id | original_language_id | rental_duration | rental_rate | length | replacement_cost | rating | special_features            | last_update         | actors |
-- +---------+-------------------+-------------------------------------------------------------------------------------------------------------------------+--------------+-------------+----------------------+-----------------+-------------+--------+------------------+--------+-----------------------------+---------------------+--------+
-- |     508 | LAMBS CINCINATTI  | A Insightful Story of a Man And a Feminist who must Fight a Composer in Australia                                       |         2006 |           1 |                 NULL |               6 |        4.99 |    144 |            18.99 | PG-13  | Trailers,Behind the Scenes  | 2006-02-15 05:03:42 |     15 |
-- |      87 | BOONDOCK BALLROOM | A Fateful Panorama of a Crocodile And a Boy who must Defeat a Monkey in The Gulf of Mexico                              |         2006 |           1 |                 NULL |               7 |        0.99 |     76 |            14.99 | NC-17  | Behind the Scenes           | 2006-02-15 05:03:42 |     13 |
-- |     146 | CHITTY LOCK       | A Boring Epistle of a Boat And a Database Administrator who must Kill a Sumo Wrestler in The First Manned Space Station |         2006 |           1 |                 NULL |               6 |        2.99 |    107 |            24.99 | G      | Commentaries                | 2006-02-15 05:03:42 |     13 |
-- |     188 | CRAZY HOME        | A Fanciful Panorama of a Boy And a Woman who must Vanquish a Database Administrator in The Outback                      |         2006 |           1 |                 NULL |               7 |        2.99 |    136 |            24.99 | PG     | Commentaries,Deleted Scenes | 2006-02-15 05:03:42 |     13 |
-- |     249 | DRACULA CRYSTAL   | A Thrilling Reflection of a Feminist And a Cat who must Find a Frisbee in An Abandoned Fun House                        |         2006 |           1 |                 NULL |               7 |        0.99 |    176 |            26.99 | G      | Commentaries                | 2006-02-15 05:03:42 |     13 |
-- +---------+-------------------+-------------------------------------------------------------------------------------------------------------------------+--------------+-------------+----------------------+-----------------+-------------+--------+------------------+--------+-----------------------------+---------------------+--------+
SELECT f.*, COUNT(*) AS actors
FROM film f
         JOIN film_actor fa ON fa.film_id = f.film_id
GROUP BY f.film_id
ORDER BY actors DESC, f.film_id LIMIT 5
;

-- ---------------------------------------------------------------------------------------------- SAKILA_FILM_ACTOR_I_02
-- how many actors a film has, on average.
-- +----------------+
-- | average_actors |
-- +----------------+
-- |           5.48 |
-- +----------------+
SELECT ROUND(AVG(actors), 2) AS average_actors
FROM (SELECT COUNT(*) AS actors FROM film_actor GROUP BY film_id) t
;

-- ---------------------------------------------------------------------------------------------- SAKILA_FILM_ACTOR_I_03
-- the five actors who appear most often with the actor 1.
-- +----------+------------+-----------+---------------------+-------+
-- | actor_id | first_name | last_name | last_update         | films |
-- +----------+------------+-----------+---------------------+-------+
-- |        4 | JENNIFER   | DAVIS     | 2006-02-15 04:34:33 |     4 |
-- |       20 | LUCILLE    | TRACY     | 2006-02-15 04:34:33 |     3 |
-- |       64 | RAY        | JOHANSSON | 2006-02-15 04:34:33 |     3 |
-- |        6 | BETTE      | NICHOLSON | 2006-02-15 04:34:33 |     2 |
-- |       10 | CHRISTIAN  | GABLE     | 2006-02-15 04:34:33 |     2 |
-- +----------+------------+-----------+---------------------+-------+
SELECT a.*, COUNT(*) AS films
FROM film_actor fa1
         JOIN film_actor fa2 ON fa2.film_id = fa1.film_id AND fa2.actor_id <> fa1.actor_id
         JOIN actor a ON a.actor_id = fa2.actor_id
WHERE fa1.actor_id = 1
GROUP BY a.actor_id
ORDER BY films DESC, a.actor_id
LIMIT 5
;

-- ---------------------------------------------------------------------------------------------- SAKILA_FILM_ACTOR_I_04
-- how many films have one actor, two actors, and so on.
-- +--------+-------+
-- | actors | films |
-- +--------+-------+
-- |      1 |    21 |
-- |      2 |    69 |
-- |      3 |   119 |
-- |      4 |   137 |
-- |      5 |   195 |
-- |      6 |   150 |
-- |      7 |   119 |
-- |      8 |    90 |
-- |      9 |    49 |
-- |     10 |    21 |
-- |     11 |    14 |
-- |     12 |     6 |
-- |     13 |     6 |
-- |     15 |     1 |
-- +--------+-------+
SELECT actors, COUNT(*) AS films
FROM (SELECT film_id, COUNT(*) AS actors FROM film_actor GROUP BY film_id) t
GROUP BY actors
ORDER BY actors
;
