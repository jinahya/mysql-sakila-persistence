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


-- ------------------------------------------------------------------------------------------------- SAKILA-PAYMENT-B-01
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

-- ------------------------------------------------------------------------------------------------- SAKILA-PAYMENT-B-02
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

-- ------------------------------------------------------------------------------------------------- SAKILA-PAYMENT-B-03
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

-- ------------------------------------------------------------------------------------------------- SAKILA-PAYMENT-B-04
-- the five amounts paid most often.
-- +--------+----------+
-- | amount | payments |
-- +--------+----------+
-- |   4.99 |     3789 |
-- |   2.99 |     3542 |
-- |   0.99 |     2977 |
-- |   5.99 |     1299 |
-- |   6.99 |     1119 |
-- +--------+----------+
SELECT amount, COUNT(*) AS payments
FROM payment
GROUP BY amount
ORDER BY payments DESC, amount
LIMIT 5
;

-- ------------------------------------------------------------------------------------------------- SAKILA-PAYMENT-B-05
-- the payments of the customer 1 in July 2005.
-- +------------+--------+---------------------+
-- | payment_id | amount | payment_date        |
-- +------------+--------+---------------------+
-- |         10 |   5.99 | 2005-07-08 03:17:05 |
-- |         11 |   5.99 | 2005-07-08 07:33:56 |
-- |         12 |   4.99 | 2005-07-09 13:24:07 |
-- |         13 |   4.99 | 2005-07-09 16:38:01 |
-- |         14 |   7.99 | 2005-07-11 10:13:46 |
-- |         15 |   2.99 | 2005-07-27 11:31:22 |
-- |         16 |   4.99 | 2005-07-28 09:04:45 |
-- |         17 |   4.99 | 2005-07-28 16:18:23 |
-- |         18 |   0.99 | 2005-07-28 17:33:39 |
-- |         19 |   0.99 | 2005-07-28 19:20:07 |
-- |         20 |   2.99 | 2005-07-29 03:58:49 |
-- |         21 |   2.99 | 2005-07-31 02:42:18 |
-- +------------+--------+---------------------+
SELECT payment_id, amount, payment_date
FROM payment
WHERE customer_id = 1
  AND payment_date BETWEEN '2005-07-01 00:00:00' AND '2005-07-31 23:59:59'
ORDER BY payment_date, payment_id
;
