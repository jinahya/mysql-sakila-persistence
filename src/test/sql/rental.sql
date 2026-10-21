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
-- Profiling queries on rental, a section per column. Each says what it looks for and what the result should be;
-- 'no rows' marks a check that passes when nothing comes back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/rental.sql
--


-- ----------------------------------------------------------------------------------------------------------- rental_id
-- how many rows, and the smallest and the largest rental_id; 16044 rentals, 1 to 16049.
SELECT COUNT(*), MIN(rental_id), MAX(rental_id)
FROM rental
;

-- a rental_id whose next value is missing, below the largest.
-- 321, 2247, 6579, 9426 and 15592: five single ids the installed data skips.
SELECT a.rental_id + 1 AS missing_from
FROM rental a
WHERE a.rental_id < (SELECT MAX(rental_id) FROM rental)
  AND NOT EXISTS (SELECT 1
                  FROM rental b
                  WHERE b.rental_id = a.rental_id + 1)
;


-- --------------------------------------------------------------------------------------------------------- rental_date
-- 2005-05-24 22:53:30 to 2006-02-14 15:16:03, the same as the payments.
SELECT MIN(rental_date), MAX(rental_date)
FROM rental
;

-- (rental_date, inventory_id, customer_id) is the unique key; a copy rented twice at the same moment.
-- no rows: no copy is rented twice at once.
SELECT rental_date, inventory_id, COUNT(*)
FROM rental
GROUP BY rental_date, inventory_id
HAVING COUNT(*) > 1
;


-- -------------------------------------------------------------------------------------------------------- inventory_id
-- an inventory_id which refers to no inventory; the foreign key forbids it.
-- no rows: every inventory_id refers to a inventory.
SELECT t.rental_id, t.inventory_id
FROM rental t
WHERE NOT EXISTS (SELECT 1
                  FROM inventory p
                  WHERE p.inventory_id = t.inventory_id)
;

-- a copy rented again before it came back.
-- no rows: no two rentals of a copy overlap.
SELECT a.rental_id, b.rental_id, a.inventory_id
FROM rental a
         JOIN rental b
              ON b.inventory_id = a.inventory_id
                  AND b.rental_id <> a.rental_id
                  AND b.rental_date > a.rental_date
                  AND b.rental_date < COALESCE(a.return_date, '9999-12-31')
;


-- --------------------------------------------------------------------------------------------------------- customer_id
-- a customer_id which refers to no customer; the foreign key forbids it.
-- no rows: every customer_id refers to a customer.
SELECT t.rental_id, t.customer_id
FROM rental t
WHERE NOT EXISTS (SELECT 1
                  FROM customer p
                  WHERE p.customer_id = t.customer_id)
;


-- --------------------------------------------------------------------------------------------------------- return_date
-- return_date is nullable: a copy not returned yet. 183 are out, and 15861 returned.
SELECT COUNT(*) - COUNT(return_date) AS out_now, COUNT(return_date) AS returned
FROM rental
;

-- a copy returned before it was rented; a null return_date, not returned yet, is left out.
-- no rows: every return_date, if any, is not before its rental_date.
SELECT rental_id, rental_date, return_date
FROM rental
WHERE return_date IS NOT NULL
  AND return_date < rental_date
;


-- ------------------------------------------------------------------------------------------------------------ staff_id
-- a staff_id which refers to no staff; the foreign key forbids it.
-- no rows: every staff_id refers to a staff.
SELECT t.rental_id, t.staff_id
FROM rental t
WHERE NOT EXISTS (SELECT 1
                  FROM staff p
                  WHERE p.staff_id = t.staff_id)
;

-- how many rentals each staff_id has; 8040 by staff 1 and 8004 by staff 2.
SELECT staff_id, COUNT(*) AS rentals
FROM rental
GROUP BY staff_id
ORDER BY staff_id ASC
;


-- --------------------------------------------------------------------------------------------------------- last_update
-- the database sets last_update on insert, and again on every update; the range the installed rows carry.
-- 2006-02-15 21:30:53 to 2006-02-23 04:12:08
SELECT MIN(last_update), MAX(last_update)
FROM rental
;


-- ---------------------------------------------------------------------------------------------------------------------
-- a rental with no payment; 0.
SELECT COUNT(*)
FROM rental r
WHERE NOT EXISTS (SELECT 1
                  FROM payment p
                  WHERE p.rental_id = r.rental_id)
;
