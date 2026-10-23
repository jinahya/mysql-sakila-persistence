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
-- Exercises at the advanced level on store: several of those together: a self-join, a relational division, correlated
-- subqueries, or one result checked against another. Each poses a question, answers it, and shows what the installed
-- data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/store_exercises_03_advanced.sql
--


-- --------------------------------------------------------------------------------------------------- SAKILA-STORE-A-01
-- how many films each store has a copy of, and how many of them only that store has.
-- +----------+-------+-----------------+
-- | store_id | films | films_only_here |
-- +----------+-------+-----------------+
-- |        1 |   759 |             196 |
-- |        2 |   762 |             199 |
-- +----------+-------+-----------------+
SELECT st.store_id,
       COUNT(DISTINCT i.film_id)                  AS films,
       COUNT(DISTINCT CASE
                          WHEN NOT EXISTS (SELECT 1
                                           FROM inventory o
                                           WHERE o.film_id = i.film_id
                                             AND o.store_id <> st.store_id)
                              THEN i.film_id END) AS films_only_here
FROM store st
         JOIN inventory i ON i.store_id = st.store_id
GROUP BY st.store_id
ORDER BY st.store_id
;

-- --------------------------------------------------------------------------------------------------- SAKILA-STORE-A-02
-- each store's rentals in each month, and its share of the month.
-- +---------+----------+---------+---------+
-- | month   | store_id | rentals | percent |
-- +---------+----------+---------+---------+
-- | 2005-05 |        1 |     575 |   49.74 |
-- | 2005-05 |        2 |     581 |   50.26 |
-- | 2005-06 |        1 |    1121 |   48.51 |
-- | 2005-06 |        2 |    1190 |   51.49 |
-- | 2005-07 |        1 |    3334 |   49.69 |
-- | 2005-07 |        2 |    3375 |   50.31 |
-- | 2005-08 |        1 |    2801 |   49.26 |
-- | 2005-08 |        2 |    2885 |   50.74 |
-- | 2006-02 |        1 |      92 |   50.55 |
-- | 2006-02 |        2 |      90 |   49.45 |
-- +---------+----------+---------+---------+
SELECT DATE_FORMAT(r.rental_date, '%Y-%m') AS month, i.store_id, COUNT(*) AS rentals,
       ROUND(100 * COUNT(*) / SUM(COUNT(*)) OVER (PARTITION BY DATE_FORMAT(r.rental_date, '%Y-%m')), 2) AS percent
FROM rental r
         JOIN inventory i ON i.inventory_id = r.inventory_id
GROUP BY month, i.store_id
ORDER BY month, i.store_id
;
