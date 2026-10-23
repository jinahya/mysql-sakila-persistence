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
-- Exercises at the advanced level on staff: several of those together: a self-join, a relational division, correlated
-- subqueries, or one result checked against another. Each poses a question, answers it, and shows what the installed
-- data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/staff_exercises_03_advanced.sql
--


-- --------------------------------------------------------------------------------------------------- SAKILA-STAFF-A-01
-- the day on which each staff member took the most.
-- +----------+------------+----------+---------+
-- | staff_id | day        | payments | total   |
-- +----------+------------+----------+---------+
-- |        1 | 2005-08-01 |      345 | 1442.55 |
-- |        2 | 2005-07-31 |      351 | 1517.49 |
-- +----------+------------+----------+---------+
SELECT staff_id, day, payments, total
FROM (SELECT staff_id, DATE(payment_date) AS day, COUNT(*) AS payments, SUM(amount) AS total,
             ROW_NUMBER() OVER (PARTITION BY staff_id ORDER BY SUM(amount) DESC, DATE(payment_date)) AS rn
      FROM payment
      GROUP BY staff_id, DATE(payment_date)) d
WHERE rn = 1
ORDER BY staff_id
;
