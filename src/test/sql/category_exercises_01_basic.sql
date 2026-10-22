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
-- Exercises at the basic level on category: one table: a filter, a sort, a count or an aggregate. Each poses a
-- question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/category_exercises_01_basic.sql
--


-- ---------------------------------------------------------------------------------------------------------- exercise 1
-- every category, by name.
-- +-------------+-------------+---------------------+
-- | category_id | name        | last_update         |
-- +-------------+-------------+---------------------+
-- |           1 | Action      | 2006-02-15 04:46:27 |
-- |           2 | Animation   | 2006-02-15 04:46:27 |
-- |           3 | Children    | 2006-02-15 04:46:27 |
-- |           4 | Classics    | 2006-02-15 04:46:27 |
-- |           5 | Comedy      | 2006-02-15 04:46:27 |
-- |           6 | Documentary | 2006-02-15 04:46:27 |
-- |           7 | Drama       | 2006-02-15 04:46:27 |
-- |           8 | Family      | 2006-02-15 04:46:27 |
-- |           9 | Foreign     | 2006-02-15 04:46:27 |
-- |          10 | Games       | 2006-02-15 04:46:27 |
-- |          11 | Horror      | 2006-02-15 04:46:27 |
-- |          12 | Music       | 2006-02-15 04:46:27 |
-- |          13 | New         | 2006-02-15 04:46:27 |
-- |          14 | Sci-Fi      | 2006-02-15 04:46:27 |
-- |          15 | Sports      | 2006-02-15 04:46:27 |
-- |          16 | Travel      | 2006-02-15 04:46:27 |
-- +-------------+-------------+---------------------+
SELECT *
FROM category
ORDER BY name
;
