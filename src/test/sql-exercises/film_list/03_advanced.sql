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
-- Exercises at the advanced level on film_list: a window function over the view's rows. Each poses a question, answers
-- it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -p'Passw0rd!' -t sakila < src/test/sql-exercises/film_list/03_advanced.sql
--


-- ----------------------------------------------------------------------------------------------- SAKILA_FILM_LIST_A_02
-- the films with the most actors in each category.
-- +-------------+-----+---------------------+-------------+
-- | category    | FID | title               | actor_count |
-- +-------------+-----+---------------------+-------------+
-- | Action      | 802 | SKY MIRACLE         |          12 |
-- | Animation   | 892 | TITANIC BOONDOCK    |          12 |
-- | Children    | 553 | MAKER GABLES        |          11 |
-- | Classics    | 249 | DRACULA CRYSTAL     |          13 |
-- | Comedy      | 188 | CRAZY HOME          |          13 |
-- | Documentary |   1 | ACADEMY DINOSAUR    |          10 |
-- | Documentary | 164 | COAST RAINBOW       |          10 |
-- | Documentary | 966 | WEDDING APOLLO      |          10 |
-- | Drama       | 146 | CHITTY LOCK         |          13 |
-- | Family      | 458 | INDIAN LOVE         |          10 |
-- | Family      | 832 | SPLASH GUMP         |          10 |
-- | Foreign     | 414 | HELLFIGHTERS SIERRA |          12 |
-- | Games       | 508 | LAMBS CINCINATTI    |          15 |
-- | Horror      |  34 | ARABIA DOGMA        |          12 |
-- | Music       | 540 | LUCKY FLYING        |          10 |
-- | Music       | 636 | OLEANDER CLUE       |          10 |
-- | New         | 561 | MASK PEACH          |          11 |
-- | Sci-Fi      | 714 | RANDOM GO           |          13 |
-- | Sports      | 606 | MUMMY CREATURES     |          13 |
-- | Travel      |  87 | BOONDOCK BALLROOM   |          13 |
-- +-------------+-----+---------------------+-------------+
WITH counted AS (SELECT category,
                        FID,
                        title,
                        CASE WHEN actors IS NULL THEN 0
                             ELSE LENGTH(actors) - LENGTH(REPLACE(actors, ',', '')) + 1 END AS actor_count
                 FROM film_list)
SELECT category, FID, title, actor_count
FROM (SELECT counted.*, RANK() OVER (PARTITION BY category ORDER BY actor_count DESC) AS actor_rank
      FROM counted) ranked
WHERE actor_rank = 1
ORDER BY category, FID
;
