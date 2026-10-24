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
-- Exercises at the advanced level on film_text: several of those together: a self-join, a relational division,
-- correlated subqueries, or one result checked against another. Each poses a question, answers it, and shows what the
-- installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/film_text_exercises_03_advanced.sql
--


-- ----------------------------------------------------------------------------------------------- SAKILA_FILM_TEXT_A_01
-- the most common kind of story in each category.
-- +-------------+-------------+-------+
-- | category    | kind        | films |
-- +-------------+-------------+-------+
-- | Action      | Reflection  |    10 |
-- | Animation   | Epistle     |     9 |
-- | Children    | Documentary |     9 |
-- | Classics    | Epistle     |     8 |
-- | Comedy      | Panorama    |     9 |
-- | Documentary | Story       |    10 |
-- | Drama       | Display     |    12 |
-- | Family      | Reflection  |    11 |
-- | Foreign     | Drama       |    12 |
-- | Games       | Display     |    10 |
-- | Horror      | Display     |     8 |
-- | Music       | Drama       |     7 |
-- | New         | Story       |    11 |
-- | Sci-Fi      | Character   |     8 |
-- | Sports      | Character   |    11 |
-- | Travel      | Drama       |     9 |
-- +-------------+-------------+-------+
WITH kinds AS (SELECT c.name AS category,
                      SUBSTRING_INDEX(SUBSTRING_INDEX(ft.description, ' ', 3), ' ', -1) AS kind,
                      COUNT(*) AS films
               FROM film_text ft
                        JOIN film_category fc ON fc.film_id = ft.film_id
                        JOIN category c ON c.category_id = fc.category_id
               GROUP BY c.name, kind),
     ranked AS (SELECT category, kind, films,
                       ROW_NUMBER() OVER (PARTITION BY category ORDER BY films DESC, kind) AS rn
                FROM kinds)
SELECT category, kind, films
FROM ranked
WHERE rn = 1
ORDER BY category
;

-- ----------------------------------------------------------------------------------------------- SAKILA_FILM_TEXT_A_02
-- the chain of titles from ACADEMY DINOSAUR.
-- +------+---------+--------------------+
-- | step | film_id | title              |
-- +------+---------+--------------------+
-- |    1 |       1 | ACADEMY DINOSAUR   |
-- |    2 |     231 | DINOSAUR SECRETARY |
-- |    3 |     777 | SECRETARY ROUGE    |
-- |    4 |     746 | ROUGE SQUAD        |
-- |    5 |     836 | SQUAD FISH         |
-- |    6 |     319 | FISH OPUS          |
-- |    7 |     640 | OPUS ICE           |
-- |    8 |     447 | ICE CROSSING       |
-- |    9 |     192 | CROSSING DIVORCE   |
-- |   10 |     237 | DIVORCE SHINING    |
-- +------+---------+--------------------+
WITH RECURSIVE chain AS (SELECT 1 AS step, film_id, title
                         FROM film_text
                         WHERE film_id = 1
                         UNION ALL
                         SELECT c.step + 1, ft.film_id, ft.title
                         FROM chain c
                                  JOIN film_text ft ON SUBSTRING_INDEX(ft.title, ' ', 1) = SUBSTRING_INDEX(c.title, ' ', -1)
                         WHERE c.step < 10)
SELECT step, film_id, title
FROM chain
ORDER BY step
;
