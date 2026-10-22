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
-- Exercises at the basic level on payment: one table: a filter, a sort, a count or an aggregate. Each poses a question,
-- answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/payment_exercises_01_basic.sql
--


-- ---------------------------------------------------------------------------------------------------------- exercise 1
-- how much each staff member took.
-- +----------+----------+----------+
-- | staff_id | payments | total    |
-- +----------+----------+----------+
-- |        1 |     8054 | 33482.50 |
-- |        2 |     7990 | 33924.06 |
-- +----------+----------+----------+
SELECT staff_id, COUNT(*) AS payments, SUM(amount) AS total
FROM payment
GROUP BY staff_id
ORDER BY staff_id
;

-- ---------------------------------------------------------------------------------------------------------- exercise 2
-- how much was paid in each month.
-- +---------+----------+----------+
-- | month   | payments | total    |
-- +---------+----------+----------+
-- | 2005-05 |     1156 |  4823.44 |
-- | 2005-06 |     2311 |  9629.89 |
-- | 2005-07 |     6709 | 28368.91 |
-- | 2005-08 |     5686 | 24070.14 |
-- | 2006-02 |      182 |   514.18 |
-- +---------+----------+----------+
SELECT DATE_FORMAT(payment_date, '%Y-%m') AS month, COUNT(*) AS payments, SUM(amount) AS total
FROM payment
GROUP BY month
ORDER BY month
;

-- ---------------------------------------------------------------------------------------------------------- exercise 3
-- how many payments have no rental.
-- +----------+
-- | payments |
-- +----------+
-- |        0 |
-- +----------+
SELECT COUNT(*) AS payments
FROM payment
WHERE rental_id IS NULL
;
