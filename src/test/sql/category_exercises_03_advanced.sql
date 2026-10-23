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
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/category_exercises_03_advanced.sql
--


-- ------------------------------------------------------------------------------------------------ SAKILA-CATEGORY-A-01
-- the running total of the films, category by category.
-- +-------------+-------+---------------+-----------------+
-- | name        | films | running_films | running_percent |
-- +-------------+-------+---------------+-----------------+
-- | Sports      |    74 |            74 |             7.4 |
-- | Foreign     |    73 |           147 |            14.7 |
-- | Family      |    69 |           216 |            21.6 |
-- | Documentary |    68 |           284 |            28.4 |
-- | Animation   |    66 |           350 |            35.0 |
-- | Action      |    64 |           414 |            41.4 |
-- | New         |    63 |           477 |            47.7 |
-- | Drama       |    62 |           539 |            53.9 |
-- | Games       |    61 |           600 |            60.0 |
-- | Sci-Fi      |    61 |           661 |            66.1 |
-- | Children    |    60 |           721 |            72.1 |
-- | Comedy      |    58 |           779 |            77.9 |
-- | Classics    |    57 |           836 |            83.6 |
-- | Travel      |    57 |           893 |            89.3 |
-- | Horror      |    56 |           949 |            94.9 |
-- | Music       |    51 |          1000 |           100.0 |
-- +-------------+-------+---------------+-----------------+
SELECT c.name,
       COUNT(*)                                                         AS films,
       SUM(COUNT(*)) OVER (ORDER BY COUNT(*) DESC, c.name)              AS running_films,
       ROUND(100 * SUM(COUNT(*)) OVER (ORDER BY COUNT(*) DESC, c.name)
                 / SUM(COUNT(*)) OVER (), 1)                            AS running_percent
FROM category c
         JOIN film_category fc ON fc.category_id = c.category_id
GROUP BY c.category_id, c.name
ORDER BY films DESC, c.name
;

-- ------------------------------------------------------------------------------------------------ SAKILA-CATEGORY-A-02
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
