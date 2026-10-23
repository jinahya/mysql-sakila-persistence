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
-- Exercises at the basic level on film_category: one table: a filter, a sort, a count or an aggregate. Each poses a
-- question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/film_category_exercises_01_basic.sql
--


-- ------------------------------------------------------------------------------------------- SAKILA-FILM_CATEGORY-B-01
-- how many categories the films are in.
-- +------------+
-- | categories |
-- +------------+
-- |         16 |
-- +------------+
SELECT COUNT(DISTINCT category_id) AS categories
FROM film_category
;

-- ------------------------------------------------------------------------------------------- SAKILA-FILM_CATEGORY-B-02
-- the categories of the films 1 to 10.
-- +---------+-------------+---------------------+
-- | film_id | category_id | last_update         |
-- +---------+-------------+---------------------+
-- |       1 |           6 | 2006-02-15 05:07:09 |
-- |       2 |          11 | 2006-02-15 05:07:09 |
-- |       3 |           6 | 2006-02-15 05:07:09 |
-- |       4 |          11 | 2006-02-15 05:07:09 |
-- |       5 |           8 | 2006-02-15 05:07:09 |
-- |       6 |           9 | 2006-02-15 05:07:09 |
-- |       7 |           5 | 2006-02-15 05:07:09 |
-- |       8 |          11 | 2006-02-15 05:07:09 |
-- |       9 |          11 | 2006-02-15 05:07:09 |
-- |      10 |          15 | 2006-02-15 05:07:09 |
-- +---------+-------------+---------------------+
SELECT *
FROM film_category
WHERE film_id BETWEEN 1 AND 10
ORDER BY film_id
;
