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
-- Exercises at the intermediate level on film: a join, a GROUP BY with HAVING, a subquery, or a NOT EXISTS. Each poses
-- a question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/film_exercises_02_intermediate.sql
--


-- ---------------------------------------------------------------------------------------------------- SAKILA_FILM_I_01
-- how many films have no copy in the inventory.
-- +-------+
-- | films |
-- +-------+
-- |    42 |
-- +-------+
SELECT COUNT(*) AS films
FROM film f
WHERE NOT EXISTS (SELECT 1 FROM inventory i WHERE i.film_id = f.film_id)
;

-- ---------------------------------------------------------------------------------------------------- SAKILA_FILM_I_02
-- the five films rented the most.
-- +---------+--------------------+--------------------------------------------------------------------------------------------------------------+--------------+-------------+----------------------+-----------------+-------------+--------+------------------+--------+-----------------------------------------+---------------------+---------+
-- | film_id | title              | description                                                                                                  | release_year | language_id | original_language_id | rental_duration | rental_rate | length | replacement_cost | rating | special_features                        | last_update         | rentals |
-- +---------+--------------------+--------------------------------------------------------------------------------------------------------------+--------------+-------------+----------------------+-----------------+-------------+--------+------------------+--------+-----------------------------------------+---------------------+---------+
-- |     103 | BUCKET BROTHERHOOD | A Amazing Display of a Girl And a Womanizer who must Succumb a Lumberjack in A Baloon Factory                |         2006 |           1 |                 NULL |               7 |        4.99 |    133 |            27.99 | PG     | Commentaries,Deleted Scenes             | 2006-02-15 05:03:42 |      34 |
-- |     738 | ROCKETEER MOTHER   | A Awe-Inspiring Character Study of a Robot And a Sumo Wrestler who must Discover a Womanizer in A Shark Tank |         2006 |           1 |                 NULL |               3 |        0.99 |    178 |            27.99 | PG-13  | Deleted Scenes,Behind the Scenes        | 2006-02-15 05:03:42 |      33 |
-- |     331 | FORWARD TEMPLE     | A Astounding Display of a Forensic Psychologist And a Mad Scientist who must Challenge a Girl in New Orleans |         2006 |           1 |                 NULL |               6 |        2.99 |     90 |            25.99 | NC-17  | Trailers,Commentaries,Behind the Scenes | 2006-02-15 05:03:42 |      32 |
-- |     382 | GRIT CLOCKWORK     | A Thoughtful Display of a Dentist And a Squirrel who must Confront a Lumberjack in A Shark Tank              |         2006 |           1 |                 NULL |               3 |        0.99 |    137 |            21.99 | PG     | Trailers,Deleted Scenes                 | 2006-02-15 05:03:42 |      32 |
-- |     489 | JUGGLER HARDLY     | A Epic Story of a Mad Cow And a Astronaut who must Challenge a Car in California                             |         2006 |           1 |                 NULL |               4 |        0.99 |     54 |            14.99 | PG-13  | Trailers,Commentaries                   | 2006-02-15 05:03:42 |      32 |
-- +---------+--------------------+--------------------------------------------------------------------------------------------------------------+--------------+-------------+----------------------+-----------------+-------------+--------+------------------+--------+-----------------------------------------+---------------------+---------+
SELECT f.*, COUNT(*) AS rentals
FROM film f
         JOIN inventory i ON i.film_id = f.film_id
         JOIN rental r ON r.inventory_id = i.inventory_id
GROUP BY f.film_id
ORDER BY rentals DESC, f.film_id LIMIT 5
;

-- ---------------------------------------------------------------------------------------------------- SAKILA_FILM_I_03
-- how many films are short, medium and long.
-- +--------+-------+
-- | size   | films |
-- +--------+-------+
-- | short  |    96 |
-- | medium |   447 |
-- | long   |   457 |
-- +--------+-------+
SELECT CASE WHEN length < 60 THEN 'short' WHEN length <= 120 THEN 'medium' ELSE 'long' END AS size,
       COUNT(*) AS films
FROM film
GROUP BY size
ORDER BY MIN(length)
;

-- ---------------------------------------------------------------------------------------------------- SAKILA_FILM_I_04
-- the ratings whose films are longer than the average film.
-- +--------+----------------+
-- | rating | average_length |
-- +--------+----------------+
-- | PG-13  |         120.44 |
-- | R      |         118.66 |
-- +--------+----------------+
SELECT rating, ROUND(AVG(length), 2) AS average_length
FROM film
GROUP BY rating
HAVING AVG(length) > (SELECT AVG(length) FROM film)
ORDER BY rating
;
