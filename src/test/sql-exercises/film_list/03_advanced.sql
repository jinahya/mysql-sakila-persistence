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
-- NOT YET RUN
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
