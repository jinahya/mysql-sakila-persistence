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
--   docker exec -i mysql-sakila mysql -uroot -p'Passw0rd!' -t sakila < src/test/sql-exercises/film_text/02_intermediate.sql
--


-- ----------------------------------------------------------------------------------------------- SAKILA_FILM_TEXT_I_01
-- whether film_text has a row for every film.
-- +-----------+------+--------------+--------------+
-- | film_text | film | without_text | without_film |
-- +-----------+------+--------------+--------------+
-- |      1000 | 1000 |            0 |            0 |
-- +-----------+------+--------------+--------------+
SELECT (SELECT COUNT(*) FROM film_text) AS film_text,
       (SELECT COUNT(*) FROM film)      AS film,
       (SELECT COUNT(*)
        FROM film f
        WHERE NOT EXISTS (SELECT 1 FROM film_text ft WHERE ft.film_id = f.film_id)) AS without_text,
       (SELECT COUNT(*)
        FROM film_text ft
        WHERE NOT EXISTS (SELECT 1 FROM film f WHERE f.film_id = ft.film_id)) AS without_film
;

-- ----------------------------------------------------------------------------------------------- SAKILA_FILM_TEXT_I_02
-- how many films mention a Drama, by the full-text index.
-- +-------+
-- | films |
-- +-------+
-- |   106 |
-- +-------+
SELECT COUNT(*) AS films
FROM film_text
WHERE MATCH(title, description) AGAINST('drama')
;

-- ----------------------------------------------------------------------------------------------- SAKILA_FILM_TEXT_I_03
-- the films whose film_text title differs from their title.
-- no rows.
SELECT f.*, ft.*
FROM film f
         JOIN film_text ft ON ft.film_id = f.film_id
WHERE ft.title COLLATE utf8mb4_bin <> f.title
ORDER BY f.film_id
;

-- ----------------------------------------------------------------------------------------------- SAKILA_FILM_TEXT_I_04
-- how many films mention a Boat but not a Shark, by the full-text index.
-- +-------+
-- | films |
-- +-------+
-- |   143 |
-- +-------+
SELECT COUNT(*) AS films
FROM film_text
WHERE MATCH(title, description) AGAINST('+boat -shark' IN BOOLEAN MODE)
;

-- ----------------------------------------------------------------------------------------------- SAKILA_FILM_TEXT_I_05
-- how many films mention a Crocodile or a Shark, and how many mention both.
-- +-------------+------------+
-- | either_word | both_words |
-- +-------------+------------+
-- |         203 |         10 |
-- +-------------+------------+
SELECT (SELECT COUNT(*)
        FROM film_text
        WHERE MATCH(title, description) AGAINST('crocodile shark'))                   AS either_word,
       (SELECT COUNT(*)
        FROM film_text
        WHERE MATCH(title, description) AGAINST('+crocodile +shark' IN BOOLEAN MODE)) AS both_words
;

-- ----------------------------------------------------------------------------------------------- SAKILA_FILM_TEXT_I_06
-- the films whose film_text description differs from their description.
-- no rows.
SELECT f.*, ft.*
FROM film f
         JOIN film_text ft ON ft.film_id = f.film_id
WHERE NOT (ft.description COLLATE utf8mb4_bin <=> f.description)
ORDER BY f.film_id
;

-- ----------------------------------------------------------------------------------------------- SAKILA_FILM_TEXT_I_07
-- the first words that start more than one title.
-- no rows.
SELECT SUBSTRING_INDEX(title, ' ', 1) AS first_word, COUNT(*) AS titles
FROM film_text
GROUP BY first_word
HAVING COUNT(*) > 1
ORDER BY first_word
;
