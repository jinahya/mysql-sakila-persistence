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
-- Exercises at the advanced level on payment: several of those together: a self-join, a relational division, correlated
-- subqueries, or one result checked against another. Each poses a question, answers it, and shows what the installed
-- data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/exercises/payment_exercises_03_advanced.sql
--


-- ------------------------------------------------------------------------------------------------- SAKILA_PAYMENT_A_01
-- the running total of the customer 1's payments, the first ten.
-- +------------+-------------+----------+-----------+--------+---------------------+---------------------+---------------+
-- | payment_id | customer_id | staff_id | rental_id | amount | payment_date        | last_update         | running_total |
-- +------------+-------------+----------+-----------+--------+---------------------+---------------------+---------------+
-- |          1 |           1 |        1 |        76 |   2.99 | 2005-05-25 11:30:37 | 2006-02-15 22:12:30 |          2.99 |
-- |          2 |           1 |        1 |       573 |   0.99 | 2005-05-28 10:35:23 | 2006-02-15 22:12:30 |          3.98 |
-- |          3 |           1 |        1 |      1185 |   5.99 | 2005-06-15 00:54:12 | 2006-02-15 22:12:30 |          9.97 |
-- |          4 |           1 |        2 |      1422 |   0.99 | 2005-06-15 18:02:53 | 2006-02-15 22:12:30 |         10.96 |
-- |          5 |           1 |        2 |      1476 |   9.99 | 2005-06-15 21:08:46 | 2006-02-15 22:12:30 |         20.95 |
-- |          6 |           1 |        1 |      1725 |   4.99 | 2005-06-16 15:18:57 | 2006-02-15 22:12:30 |         25.94 |
-- |          7 |           1 |        1 |      2308 |   4.99 | 2005-06-18 08:41:48 | 2006-02-15 22:12:30 |         30.93 |
-- |          8 |           1 |        2 |      2363 |   0.99 | 2005-06-18 13:33:59 | 2006-02-15 22:12:30 |         31.92 |
-- |          9 |           1 |        1 |      3284 |   3.99 | 2005-06-21 06:24:45 | 2006-02-15 22:12:30 |         35.91 |
-- |         10 |           1 |        2 |      4526 |   5.99 | 2005-07-08 03:17:05 | 2006-02-15 22:12:30 |         41.90 |
-- +------------+-------------+----------+-----------+--------+---------------------+---------------------+---------------+
SELECT *,
       SUM(amount) OVER (ORDER BY payment_date, payment_id) AS running_total
FROM payment
WHERE customer_id = 1
ORDER BY payment_date, payment_id
LIMIT 10
;

-- ------------------------------------------------------------------------------------------------- SAKILA_PAYMENT_A_02
-- the runs of consecutive days on which payments were made.
-- +------------+------------+------+
-- | first_day  | last_day   | days |
-- +------------+------------+------+
-- | 2005-05-24 | 2005-05-31 |    8 |
-- | 2005-06-14 | 2005-06-21 |    8 |
-- | 2005-07-05 | 2005-07-12 |    8 |
-- | 2005-07-26 | 2005-08-02 |    8 |
-- | 2005-08-16 | 2005-08-23 |    8 |
-- | 2006-02-14 | 2006-02-14 |    1 |
-- +------------+------------+------+
WITH days AS (SELECT DISTINCT DATE(payment_date) AS day FROM payment),
     islands AS (SELECT day, DATE_SUB(day, INTERVAL ROW_NUMBER() OVER (ORDER BY day) DAY) AS island FROM days)
SELECT MIN(day) AS first_day, MAX(day) AS last_day, COUNT(*) AS days
FROM islands
GROUP BY island
ORDER BY first_day
;
