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
-- Exercises at the basic level on language: one table: a filter, a sort, a count or an aggregate. Each poses a
-- question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/language_exercises_01_basic.sql
--


-- ---------------------------------------------------------------------------------------------------------- exercise 1
-- every language.
-- +-------------+----------+---------------------+
-- | language_id | name     | last_update         |
-- +-------------+----------+---------------------+
-- |           1 | English  | 2006-02-15 05:02:19 |
-- |           2 | Italian  | 2006-02-15 05:02:19 |
-- |           3 | Japanese | 2006-02-15 05:02:19 |
-- |           4 | Mandarin | 2006-02-15 05:02:19 |
-- |           5 | French   | 2006-02-15 05:02:19 |
-- |           6 | German   | 2006-02-15 05:02:19 |
-- +-------------+----------+---------------------+
SELECT *
FROM language
ORDER BY language_id
;

-- ---------------------------------------------------------------------------------------------------------- exercise 2
-- how many films have an original language.
-- +-------+
-- | films |
-- +-------+
-- |     0 |
-- +-------+
SELECT COUNT(*) AS films
FROM film
WHERE original_language_id IS NOT NULL
;
