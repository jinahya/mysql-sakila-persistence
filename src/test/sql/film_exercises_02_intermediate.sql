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


-- ---------------------------------------------------------------------------------------------------- SAKILA-FILM-I-01
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

-- ---------------------------------------------------------------------------------------------------- SAKILA-FILM-I-02
-- the five films rented the most.
-- +---------+--------------------+---------+
-- | film_id | title              | rentals |
-- +---------+--------------------+---------+
-- |     103 | BUCKET BROTHERHOOD |      34 |
-- |     738 | ROCKETEER MOTHER   |      33 |
-- |     331 | FORWARD TEMPLE     |      32 |
-- |     382 | GRIT CLOCKWORK     |      32 |
-- |     489 | JUGGLER HARDLY     |      32 |
-- +---------+--------------------+---------+
SELECT f.film_id, f.title, COUNT(*) AS rentals
FROM film f
         JOIN inventory i ON i.film_id = f.film_id
         JOIN rental r ON r.inventory_id = i.inventory_id
GROUP BY f.film_id, f.title
ORDER BY rentals DESC, f.film_id LIMIT 5
;

-- ---------------------------------------------------------------------------------------------------- SAKILA-FILM-I-03
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

-- ---------------------------------------------------------------------------------------------------- SAKILA-FILM-I-04
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
