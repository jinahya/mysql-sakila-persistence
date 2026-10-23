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
-- Exercises at the intermediate level on language: a join, a GROUP BY with HAVING, a subquery, or a NOT EXISTS. Each
-- poses a question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/language_exercises_02_intermediate.sql
--


-- ------------------------------------------------------------------------------------------------ SAKILA-LANGUAGE-I-01
-- how many films each language has.
-- +-------------+----------+---------------------+-------+
-- | language_id | name     | last_update         | films |
-- +-------------+----------+---------------------+-------+
-- |           1 | English  | 2006-02-15 05:02:19 |  1000 |
-- |           2 | Italian  | 2006-02-15 05:02:19 |     0 |
-- |           3 | Japanese | 2006-02-15 05:02:19 |     0 |
-- |           4 | Mandarin | 2006-02-15 05:02:19 |     0 |
-- |           5 | French   | 2006-02-15 05:02:19 |     0 |
-- |           6 | German   | 2006-02-15 05:02:19 |     0 |
-- +-------------+----------+---------------------+-------+
SELECT l.*, COUNT(f.film_id) AS films
FROM language l
         LEFT JOIN film f ON f.language_id = l.language_id
GROUP BY l.language_id
ORDER BY l.language_id
;

-- ------------------------------------------------------------------------------------------------ SAKILA-LANGUAGE-I-02
-- the languages no film uses, either as its language or as its original language.
-- +-------------+----------+---------------------+
-- | language_id | name     | last_update         |
-- +-------------+----------+---------------------+
-- |           2 | Italian  | 2006-02-15 05:02:19 |
-- |           3 | Japanese | 2006-02-15 05:02:19 |
-- |           4 | Mandarin | 2006-02-15 05:02:19 |
-- |           5 | French   | 2006-02-15 05:02:19 |
-- |           6 | German   | 2006-02-15 05:02:19 |
-- +-------------+----------+---------------------+
SELECT l.*
FROM language l
WHERE NOT EXISTS (SELECT 1
                  FROM film f
                  WHERE f.language_id = l.language_id
                     OR f.original_language_id = l.language_id)
ORDER BY l.language_id
;
