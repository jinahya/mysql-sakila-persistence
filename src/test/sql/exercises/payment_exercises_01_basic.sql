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
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/exercises/payment_exercises_01_basic.sql
--


-- ------------------------------------------------------------------------------------------------- SAKILA_PAYMENT_B_01
-- how much each staff member took.
-- +----------+------------+-----------+------------+------------------------------+----------+--------+----------+------------------------------------------+---------------------+----------+----------+
-- | staff_id | first_name | last_name | address_id | email                        | store_id | active | username | password                                 | last_update         | payments | total    |
-- +----------+------------+-----------+------------+------------------------------+----------+--------+----------+------------------------------------------+---------------------+----------+----------+
-- |        1 | Mike       | Hillyer   |          3 | Mike.Hillyer@sakilastaff.com |        1 |      1 | Mike     | 8cb2237d0679ca88db6464eac60da96345513964 | 2006-02-15 03:57:16 |     8054 | 33482.50 |
-- |        2 | Jon        | Stephens  |          4 | Jon.Stephens@sakilastaff.com |        2 |      1 | Jon      | NULL                                     | 2006-02-15 03:57:16 |     7990 | 33924.06 |
-- +----------+------------+-----------+------------+------------------------------+----------+--------+----------+------------------------------------------+---------------------+----------+----------+
SELECT s.staff_id, s.first_name, s.last_name, s.address_id, s.email, s.store_id, s.active, s.username, s.password, s.last_update, COUNT(*) AS payments, SUM(p.amount) AS total -- every column but picture, a BLOB which does not print
FROM payment p
         JOIN staff s ON s.staff_id = p.staff_id
GROUP BY s.staff_id
ORDER BY s.staff_id
;

-- ------------------------------------------------------------------------------------------------- SAKILA_PAYMENT_B_02
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

-- ------------------------------------------------------------------------------------------------- SAKILA_PAYMENT_B_03
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

-- ------------------------------------------------------------------------------------------------- SAKILA_PAYMENT_B_04
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

-- ------------------------------------------------------------------------------------------------- SAKILA_PAYMENT_B_05
-- the payments of the customer 1 in July 2005.
-- +------------+-------------+----------+-----------+--------+---------------------+---------------------+
-- | payment_id | customer_id | staff_id | rental_id | amount | payment_date        | last_update         |
-- +------------+-------------+----------+-----------+--------+---------------------+---------------------+
-- |         10 |           1 |        2 |      4526 |   5.99 | 2005-07-08 03:17:05 | 2006-02-15 22:12:30 |
-- |         11 |           1 |        1 |      4611 |   5.99 | 2005-07-08 07:33:56 | 2006-02-15 22:12:30 |
-- |         12 |           1 |        1 |      5244 |   4.99 | 2005-07-09 13:24:07 | 2006-02-15 22:12:30 |
-- |         13 |           1 |        1 |      5326 |   4.99 | 2005-07-09 16:38:01 | 2006-02-15 22:12:30 |
-- |         14 |           1 |        1 |      6163 |   7.99 | 2005-07-11 10:13:46 | 2006-02-15 22:12:30 |
-- |         15 |           1 |        2 |      7273 |   2.99 | 2005-07-27 11:31:22 | 2006-02-15 22:12:30 |
-- |         16 |           1 |        1 |      7841 |   4.99 | 2005-07-28 09:04:45 | 2006-02-15 22:12:30 |
-- |         17 |           1 |        2 |      8033 |   4.99 | 2005-07-28 16:18:23 | 2006-02-15 22:12:30 |
-- |         18 |           1 |        1 |      8074 |   0.99 | 2005-07-28 17:33:39 | 2006-02-15 22:12:30 |
-- |         19 |           1 |        2 |      8116 |   0.99 | 2005-07-28 19:20:07 | 2006-02-15 22:12:30 |
-- |         20 |           1 |        2 |      8326 |   2.99 | 2005-07-29 03:58:49 | 2006-02-15 22:12:30 |
-- |         21 |           1 |        2 |      9571 |   2.99 | 2005-07-31 02:42:18 | 2006-02-15 22:12:30 |
-- +------------+-------------+----------+-----------+--------+---------------------+---------------------+
SELECT *
FROM payment
WHERE customer_id = 1
  AND payment_date BETWEEN '2005-07-01 00:00:00' AND '2005-07-31 23:59:59'
ORDER BY payment_date, payment_id
;
