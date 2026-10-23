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
-- Exercises at the basic level on film: one table: a filter, a sort, a count or an aggregate. Each poses a question,
-- answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/film_exercises_01_basic.sql
--


-- ---------------------------------------------------------------------------------------------------- SAKILA-FILM-B-01
-- how many films each rating has.
-- +--------+-------+
-- | rating | films |
-- +--------+-------+
-- | G      |   178 |
-- | PG     |   194 |
-- | PG-13  |   223 |
-- | R      |   195 |
-- | NC-17  |   210 |
-- +--------+-------+
SELECT rating, COUNT(*) AS films
FROM film
GROUP BY rating
ORDER BY rating
;

-- ---------------------------------------------------------------------------------------------------- SAKILA-FILM-B-02
-- the films longer than three hours, the longest first.
-- +---------+----------------+----------------------------------------------------------------------------------------------------+--------------+-------------+----------------------+-----------------+-------------+--------+------------------+--------+-----------------------------------------+---------------------+
-- | film_id | title          | description                                                                                        | release_year | language_id | original_language_id | rental_duration | rental_rate | length | replacement_cost | rating | special_features                        | last_update         |
-- +---------+----------------+----------------------------------------------------------------------------------------------------+--------------+-------------+----------------------+-----------------+-------------+--------+------------------+--------+-----------------------------------------+---------------------+
-- |     141 | CHICAGO NORTH  | A Fateful Yarn of a Mad Cow And a Waitress who must Battle a Student in California                 |         2006 |           1 |                 NULL |               6 |        4.99 |    185 |            11.99 | PG-13  | Deleted Scenes,Behind the Scenes        | 2006-02-15 05:03:42 |
-- |     182 | CONTROL ANTHEM | A Fateful Documentary of a Robot And a Student who must Battle a Cat in A Monastery                |         2006 |           1 |                 NULL |               7 |        4.99 |    185 |             9.99 | G      | Commentaries                            | 2006-02-15 05:03:42 |
-- |     212 | DARN FORRESTER | A Fateful Story of a A Shark And a Explorer who must Succumb a Technical Writer in A Jet Boat      |         2006 |           1 |                 NULL |               7 |        4.99 |    185 |            14.99 | G      | Deleted Scenes                          | 2006-02-15 05:03:42 |
-- |     349 | GANGS PRIDE    | A Taut Character Study of a Woman And a A Shark who must Confront a Frisbee in Berlin              |         2006 |           1 |                 NULL |               4 |        2.99 |    185 |            27.99 | PG-13  | Behind the Scenes                       | 2006-02-15 05:03:42 |
-- |     426 | HOME PITY      | A Touching Panorama of a Man And a Secret Agent who must Challenge a Teacher in A MySQL Convention |         2006 |           1 |                 NULL |               7 |        4.99 |    185 |            15.99 | R      | Trailers,Commentaries,Behind the Scenes | 2006-02-15 05:03:42 |
-- +---------+----------------+----------------------------------------------------------------------------------------------------+--------------+-------------+----------------------+-----------------+-------------+--------+------------------+--------+-----------------------------------------+---------------------+
SELECT *
FROM film
WHERE length > 180
ORDER BY length DESC, title LIMIT 5
;

-- ---------------------------------------------------------------------------------------------------- SAKILA-FILM-B-03
-- the average rental rate of each rating.
-- +--------+---------------------+
-- | rating | average_rental_rate |
-- +--------+---------------------+
-- | G      |                2.89 |
-- | PG     |                3.05 |
-- | PG-13  |                3.03 |
-- | R      |                2.94 |
-- | NC-17  |                2.97 |
-- +--------+---------------------+
SELECT rating, ROUND(AVG(rental_rate), 2) AS average_rental_rate
FROM film
GROUP BY rating
ORDER BY rating
;

-- ---------------------------------------------------------------------------------------------------- SAKILA-FILM-B-04
-- how many films have Behind the Scenes among their special features.
-- +-------+
-- | films |
-- +-------+
-- |   538 |
-- +-------+
SELECT COUNT(*) AS films
FROM film
WHERE FIND_IN_SET('Behind the Scenes', special_features) > 0
;

-- ---------------------------------------------------------------------------------------------------- SAKILA-FILM-B-05
-- the five shortest films.
-- +---------+---------------------+--------+
-- | film_id | title               | length |
-- +---------+---------------------+--------+
-- |      15 | ALIEN CENTER        |     46 |
-- |     469 | IRON MOON           |     46 |
-- |     504 | KWAI HOMEWARD       |     46 |
-- |     505 | LABYRINTH LEAGUE    |     46 |
-- |     730 | RIDGEMONT SUBMARINE |     46 |
-- +---------+---------------------+--------+
SELECT film_id, title, length
FROM film
ORDER BY length, title
LIMIT 5
;

-- ---------------------------------------------------------------------------------------------------- SAKILA-FILM-B-06
-- the films cheapest to rent and dearest to replace.
-- +---------+----------------------+-------------+------------------+
-- | film_id | title                | rental_rate | replacement_cost |
-- +---------+----------------------+-------------+------------------+
-- |      34 | ARABIA DOGMA         |        0.99 |            29.99 |
-- |      52 | BALLROOM MOCKINGBIRD |        0.99 |            29.99 |
-- |      85 | BONNIE HOLOCAUST     |        0.99 |            29.99 |
-- |     157 | CLOCKWORK PARADISE   |        0.99 |            29.99 |
-- |     163 | CLYDE THEORY         |        0.99 |            29.99 |
-- |     196 | CRUELTY UNFORGIVEN   |        0.99 |            29.99 |
-- |     270 | EARTH VISION         |        0.99 |            29.99 |
-- |     290 | EVERYONE CRAFT       |        0.99 |            29.99 |
-- |     309 | FEUD FROGMEN         |        0.99 |            29.99 |
-- |     358 | GILMORE BOILED       |        0.99 |            29.99 |
-- +---------+----------------------+-------------+------------------+
SELECT film_id, title, rental_rate, replacement_cost
FROM film
WHERE rental_rate = 0.99
  AND replacement_cost = 29.99
ORDER BY title
LIMIT 10
;
