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
---
--
-- Profiling queries on payment, a section per column. Each says what it looks for and what the result should be;
-- 'no rows' marks a check that passes when nothing comes back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/payment.sql
--


-- ---------------------------------------------------------------------------------------------------------- payment_id
-- how many rows, and the smallest and the largest payment_id; 16044 payments, 1 to 16049.
SELECT COUNT(*), MIN(payment_id), MAX(payment_id)
FROM payment
;

-- a payment_id whose next value is missing, below the largest.
-- 424, 7011, 10840, 14675 and 15458: five single ids the installed data skips.
SELECT a.payment_id + 1 AS missing_from
FROM payment a
WHERE a.payment_id < (SELECT MAX(payment_id) FROM payment)
  AND NOT EXISTS (SELECT 1
                  FROM payment b
                  WHERE b.payment_id = a.payment_id + 1)
;


-- --------------------------------------------------------------------------------------------------------- customer_id
-- a customer_id which refers to no customer; the foreign key forbids it.
-- no rows: every customer_id refers to a customer.
SELECT t.payment_id, t.customer_id
FROM payment t
WHERE NOT EXISTS (SELECT 1
                  FROM customer p
                  WHERE p.customer_id = t.customer_id)
;

-- a payment whose customer is not the customer of its rental.
-- no rows: every payment is made by the customer who rented.
SELECT p.payment_id, p.customer_id, r.customer_id
FROM payment p
         JOIN rental r ON r.rental_id = p.rental_id
WHERE p.customer_id <> r.customer_id
;


-- ------------------------------------------------------------------------------------------------------------ staff_id
-- a staff_id which refers to no staff; the foreign key forbids it.
-- no rows: every staff_id refers to a staff.
SELECT t.payment_id, t.staff_id
FROM payment t
WHERE NOT EXISTS (SELECT 1
                  FROM staff p
                  WHERE p.staff_id = t.staff_id)
;

-- how many payments each staff_id has; 8054 taken by staff 1 and 7990 by staff 2.
SELECT staff_id, COUNT(*) AS payments
FROM payment
GROUP BY staff_id
ORDER BY staff_id ASC
;


-- ----------------------------------------------------------------------------------------------------------- rental_id
-- rental_id is nullable, and has no unique key.

-- a rental_id which refers to no rental; the foreign key forbids it.
-- no rows: every rental_id refers to a rental.
SELECT t.payment_id, t.rental_id
FROM payment t
WHERE NOT EXISTS (SELECT 1
                  FROM rental p
                  WHERE p.rental_id = t.rental_id)
  AND t.rental_id IS NOT NULL
;

-- how many payments have no rental; none.
SELECT COUNT(*) - COUNT(rental_id) AS without, COUNT(rental_id) AS `with`
FROM payment
;

-- a rental paid for more than once.
-- no rows: every rental is paid for once.
SELECT rental_id, COUNT(*) AS payments
FROM payment
WHERE rental_id IS NOT NULL
GROUP BY rental_id
HAVING COUNT(*) > 1
;


-- -------------------------------------------------------------------------------------------------------------- amount
-- 0.00 to 11.99, in 19 values.
SELECT MIN(amount), MAX(amount), COUNT(DISTINCT amount)
FROM payment
;

-- a negative amount.
-- no rows: every amount is zero or more.
SELECT payment_id, amount
FROM payment
WHERE amount < 0
;


-- -------------------------------------------------------------------------------------------------------- payment_date
-- 2005-05-24 22:53:30 to 2006-02-14 15:16:03.
SELECT MIN(payment_date), MAX(payment_date)
FROM payment
;

-- a payment made before its rental.
-- no rows: no payment precedes its rental.
SELECT p.payment_id, p.payment_date, r.rental_date
FROM payment p
         JOIN rental r ON r.rental_id = p.rental_id
WHERE p.payment_date < r.rental_date
;


-- --------------------------------------------------------------------------------------------------------- last_update
-- the database sets last_update on insert, and again on every update; the range the installed rows carry.
-- 2006-02-15 22:12:30 to 22:24:13
SELECT MIN(last_update), MAX(last_update)
FROM payment
;

-- last_update is nullable here, unlike in most tables, though it has a default.
-- no rows: every row has a last_update.
SELECT *
FROM payment
WHERE last_update IS NULL
;
