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
-- Exercises at the intermediate level on film_text: a join, a GROUP BY with HAVING, a subquery, or a NOT EXISTS. Each
-- poses a question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/film_text_exercises_02_intermediate.sql
--


-- ---------------------------------------------------------------------------------------------------------- exercise 1
-- whether film_text has a row for every film.
-- +-----------+------+
-- | film_text | film |
-- +-----------+------+
-- |      1000 | 1000 |
-- +-----------+------+
SELECT (SELECT COUNT(*) FROM film_text) AS film_text, (SELECT COUNT(*) FROM film) AS film
;

-- ---------------------------------------------------------------------------------------------------------- exercise 2
-- how many films mention a Drama, by the full-text index.
-- +-------+
-- | films |
-- +-------+
-- |   106 |
-- +-------+
SELECT COUNT(*) AS films
FROM film_text
WHERE MATCH (title, description) AGAINST ('drama')
;

-- ---------------------------------------------------------------------------------------------------------- exercise 3
-- the films whose film_text title differs from their title.
-- no rows.
SELECT f.film_id, f.title, ft.title AS film_text_title
FROM film f
         JOIN film_text ft ON ft.film_id = f.film_id
WHERE ft.title <> f.title
;
