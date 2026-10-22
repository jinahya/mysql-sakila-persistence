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


-- ---------------------------------------------------------------------------------------------------------- exercise 1
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

-- ---------------------------------------------------------------------------------------------------------- exercise 2
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
