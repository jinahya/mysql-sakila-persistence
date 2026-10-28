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
-- Exercises at the advanced level on category: several of those together: a self-join, a relational division,
-- correlated subqueries, or one result checked against another. Each poses a question, answers it, and shows what the
-- installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/exercises/category_exercises_03_advanced.sql
--


-- ------------------------------------------------------------------------------------------------ SAKILA_CATEGORY_A_01
-- the running total of the films, category by category.
-- +-------------+-------------+---------------------+-------+---------------+-----------------+
-- | category_id | name        | last_update         | films | running_films | running_percent |
-- +-------------+-------------+---------------------+-------+---------------+-----------------+
-- |          15 | Sports      | 2006-02-15 04:46:27 |    74 |            74 |             7.4 |
-- |           9 | Foreign     | 2006-02-15 04:46:27 |    73 |           147 |            14.7 |
-- |           8 | Family      | 2006-02-15 04:46:27 |    69 |           216 |            21.6 |
-- |           6 | Documentary | 2006-02-15 04:46:27 |    68 |           284 |            28.4 |
-- |           2 | Animation   | 2006-02-15 04:46:27 |    66 |           350 |            35.0 |
-- |           1 | Action      | 2006-02-15 04:46:27 |    64 |           414 |            41.4 |
-- |          13 | New         | 2006-02-15 04:46:27 |    63 |           477 |            47.7 |
-- |           7 | Drama       | 2006-02-15 04:46:27 |    62 |           539 |            53.9 |
-- |          10 | Games       | 2006-02-15 04:46:27 |    61 |           600 |            60.0 |
-- |          14 | Sci-Fi      | 2006-02-15 04:46:27 |    61 |           661 |            66.1 |
-- |           3 | Children    | 2006-02-15 04:46:27 |    60 |           721 |            72.1 |
-- |           5 | Comedy      | 2006-02-15 04:46:27 |    58 |           779 |            77.9 |
-- |           4 | Classics    | 2006-02-15 04:46:27 |    57 |           836 |            83.6 |
-- |          16 | Travel      | 2006-02-15 04:46:27 |    57 |           893 |            89.3 |
-- |          11 | Horror      | 2006-02-15 04:46:27 |    56 |           949 |            94.9 |
-- |          12 | Music       | 2006-02-15 04:46:27 |    51 |          1000 |           100.0 |
-- +-------------+-------------+---------------------+-------+---------------+-----------------+
SELECT c.*,
       COUNT(*)                                                         AS films,
       SUM(COUNT(*)) OVER (ORDER BY COUNT(*) DESC, c.name)              AS running_films,
       ROUND(100 * SUM(COUNT(*)) OVER (ORDER BY COUNT(*) DESC, c.name)
                 / SUM(COUNT(*)) OVER (), 1)                            AS running_percent
FROM category c
         JOIN film_category fc ON fc.category_id = c.category_id
GROUP BY c.category_id
ORDER BY films DESC, c.name
;

-- ------------------------------------------------------------------------------------------------ SAKILA_CATEGORY_A_02
-- the film rented the most in each category.
-- +-------------+---------------------+---------+
-- | name        | title               | rentals |
-- +-------------+---------------------+---------+
-- | Action      | RUGRATS SHAKESPEARE |      30 |
-- | Animation   | JUGGLER HARDLY      |      32 |
-- | Children    | ROBBERS JOON        |      31 |
-- | Classics    | TIMBERLAND SKY      |      31 |
-- | Comedy      | ZORRO ARK           |      31 |
-- | Documentary | WIFE TURN           |      31 |
-- | Drama       | HOBBIT ALIEN        |      31 |
-- | Family      | APACHE DIVINE       |      31 |
-- | Foreign     | ROCKETEER MOTHER    |      33 |
-- | Games       | FORWARD TEMPLE      |      32 |
-- | Horror      | PULP BEVERLY        |      30 |
-- | Music       | SCALAWAG DUCK       |      32 |
-- | New         | RIDGEMONT SUBMARINE |      32 |
-- | Sci-Fi      | GOODFELLAS SALUTE   |      31 |
-- | Sports      | GLEAMING JAWBREAKER |      29 |
-- | Travel      | BUCKET BROTHERHOOD  |      34 |
-- +-------------+---------------------+---------+
SELECT name, title, rentals
FROM (SELECT c.name,
             f.title,
             COUNT(*)                                                                    AS rentals,
             ROW_NUMBER() OVER (PARTITION BY c.category_id ORDER BY COUNT(*) DESC, f.title) AS film_rank
      FROM category c
               JOIN film_category fc ON fc.category_id = c.category_id
               JOIN film f ON f.film_id = fc.film_id
               JOIN inventory i ON i.film_id = f.film_id
               JOIN rental r ON r.inventory_id = i.inventory_id
      GROUP BY c.category_id, c.name, f.film_id, f.title) ranked
WHERE film_rank = 1
ORDER BY name
;
