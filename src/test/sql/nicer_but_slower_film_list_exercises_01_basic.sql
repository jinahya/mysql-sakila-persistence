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
-- Exercises at the basic level on the nicer_but_slower_film_list view: one table: a filter, a sort, a count or an
-- aggregate. Each poses a question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/nicer_but_slower_film_list_exercises_01_basic.sql
--


-- ------------------------------------------------------------------------------ SAKILA-NICER_BUT_SLOWER_FILM_LIST-B-01
-- the actors of the film 1, as this view writes them.
-- +-----+------------------+------------------------------------------------------------------------------------------------------------------------------------------------+
-- | FID | title            | actors                                                                                                                                         |
-- +-----+------------------+------------------------------------------------------------------------------------------------------------------------------------------------+
-- |   1 | ACADEMY DINOSAUR | Penelope Guiness, Christian Gable, Lucille Tracy, Sandra Peck, Johnny Cage, Mena Temple, Warren Nolte, Oprah Kilmer, Rock Dukakis, Mary Keitel |
-- +-----+------------------+------------------------------------------------------------------------------------------------------------------------------------------------+
SELECT FID, title, actors
FROM nicer_but_slower_film_list
WHERE FID = 1
;

-- ------------------------------------------------------------------------------ SAKILA-NICER_BUT_SLOWER_FILM_LIST-B-02
-- how many films PENELOPE GUINESS plays in, by the view's actors.
-- +-------+
-- | films |
-- +-------+
-- |    19 |
-- +-------+
SELECT COUNT(*) AS films
FROM nicer_but_slower_film_list
WHERE actors LIKE '%Penelope Guiness%'
;

-- ------------------------------------------------------------------------------ SAKILA-NICER_BUT_SLOWER_FILM_LIST-B-03
-- the films which have no actors.
-- +-----+------------------+----------+
-- | FID | title            | category |
-- +-----+------------------+----------+
-- | 257 | DRUMLINE CYCLONE | Travel   |
-- | 323 | FLIGHT LIES      | Sports   |
-- | 803 | SLACKER LIAISONS | Drama    |
-- +-----+------------------+----------+
SELECT FID, title, category
FROM nicer_but_slower_film_list
WHERE actors IS NULL
ORDER BY FID
;
