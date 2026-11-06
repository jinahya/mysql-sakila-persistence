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
--   docker exec -i mysql-sakila mysql -uroot -p'Passw0rd!' -t sakila < src/test/sql-exercises/payment/01_basic.sql
--


-- ------------------------------------------------------------------------------------------------- SAKILA_PAYMENT_B_01
-- how much each staff member took.
-- +----------+------------+-----------+------------+------------------------------+----------+--------+----------+------------------------------------------+---------------------+----------+----------+
-- | staff_id | first_name | last_name | address_id | email                        | store_id | active | username | password                                 | last_update         | payments | total    |
-- +----------+------------+-----------+------------+------------------------------+----------+--------+----------+------------------------------------------+---------------------+----------+----------+
-- |        1 | Mike       | Hillyer   |          3 | Mike.Hillyer@sakilastaff.com |        1 |      1 | Mike     | 8cb2237d0679ca88db6464eac60da96345513964 | 2006-02-15 03:57:16 |     8054 | 33482.50 |
-- |        2 | Jon        | Stephens  |          4 | Jon.Stephens@sakilastaff.com |        2 |      1 | Jon      | NULL                                     | 2006-02-15 03:57:16 |     7990 | 33924.06 |
-- +----------+------------+-----------+------------+------------------------------+----------+--------+----------+------------------------------------------+---------------------+----------+----------+
SELECT s.staff_id,
       s.first_name,
       s.last_name,
       s.address_id,
       s.email,
       s.store_id,
       s.active,
       s.username,
       s.password,
       s.last_update,
       COUNT(*)      AS payments,
       SUM(p.amount) AS total -- every column but picture, a BLOB which does not print
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
-- NOT YET RUN
SELECT *
FROM payment
WHERE customer_id = 1
  AND payment_date >= '2005-07-01'
  AND payment_date < '2005-08-01'
ORDER BY payment_date, payment_id
;

-- ------------------------------------------------------------------------------------------------- SAKILA_PAYMENT_B_06
-- the rentals paid more than once.
-- NOT YET RUN
SELECT rental_id, COUNT(*) AS payments
FROM payment
WHERE rental_id IS NOT NULL
GROUP BY rental_id
HAVING COUNT(*) > 1
ORDER BY rental_id
;
