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
-- Profiling queries on customer, a section per column. Each says what it looks for and what the result should be;
-- 'no rows' marks a check that passes when nothing comes back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/customer.sql
--


-- --------------------------------------------------------------------------------------------------------- customer_id
-- how many rows, and the smallest and the largest customer_id; 599 customers, 1 to 599.
SELECT COUNT(*), MIN(customer_id), MAX(customer_id)
FROM customer
;

-- a customer_id whose next value is missing, below the largest.
-- no rows: customer_id runs from 1 without a gap.
SELECT a.customer_id + 1 AS missing_from
FROM customer a
WHERE a.customer_id < (SELECT MAX(customer_id) FROM customer)
  AND NOT EXISTS (SELECT 1
                  FROM customer b
                  WHERE b.customer_id = a.customer_id + 1)
;


-- ------------------------------------------------------------------------------------------------------------ store_id
-- a store_id which refers to no store; the foreign key forbids it.
-- no rows: every store_id refers to a store.
SELECT t.customer_id, t.store_id
FROM customer t
WHERE NOT EXISTS (SELECT 1
                  FROM store p
                  WHERE p.store_id = t.store_id)
;

-- how many customers each store_id has; 326 at store 1 and 273 at store 2.
SELECT store_id, COUNT(*) AS customers
FROM customer
GROUP BY store_id
ORDER BY store_id ASC
;


-- ---------------------------------------------------------------------------------------------------------- first_name
-- the shortest and the longest first_name; 2 and 11 characters.
SELECT MIN(CHAR_LENGTH(first_name)), MAX(CHAR_LENGTH(first_name))
FROM customer
;

-- a first_name which is blank, or carries leading or trailing spaces.
-- no rows: every first_name is trimmed and not blank.
SELECT customer_id, CONCAT('[', first_name, ']') AS first_name
FROM customer
WHERE TRIM(first_name) = ''
   OR CHAR_LENGTH(first_name) <> CHAR_LENGTH(TRIM(first_name))
;

-- the installed data writes first_name in upper case.
-- no rows: every first_name is upper case.
SELECT customer_id, first_name
FROM customer
WHERE NOT REGEXP_LIKE(first_name, '^[^a-z]*$', 'c')
;


-- ----------------------------------------------------------------------------------------------------------- last_name
-- the shortest and the longest last_name; 2 and 12 characters.
SELECT MIN(CHAR_LENGTH(last_name)), MAX(CHAR_LENGTH(last_name))
FROM customer
;

-- a last_name which is blank, or carries leading or trailing spaces.
-- no rows: every last_name is trimmed and not blank.
SELECT customer_id, CONCAT('[', last_name, ']') AS last_name
FROM customer
WHERE TRIM(last_name) = ''
   OR CHAR_LENGTH(last_name) <> CHAR_LENGTH(TRIM(last_name))
;

-- the installed data writes last_name in upper case.
-- no rows: every last_name is upper case.
SELECT customer_id, last_name
FROM customer
WHERE NOT REGEXP_LIKE(last_name, '^[^a-z]*$', 'c')
;


-- --------------------------------------------------------------------------------------------------------------- email
-- email is nullable, and has no unique key; how many rows have none. None; every customer has one.
SELECT COUNT(*) - COUNT(email) AS without, COUNT(email) AS `with`
FROM customer
;

-- an email more than one customer has.
-- no rows: every email is unique.
SELECT email, COUNT(*)
FROM customer
WHERE email IS NOT NULL
GROUP BY email
HAVING COUNT(*) > 1
;

-- the installed data builds each email from the names: FIRST.LAST@sakilacustomer.org.
-- no rows: every email is the names, then the domain.
SELECT customer_id, first_name, last_name, email
FROM customer
WHERE email <> CONCAT(first_name, '.', last_name, '@sakilacustomer.org')
;


-- ---------------------------------------------------------------------------------------------------------- address_id
-- an address_id which refers to no address; the foreign key forbids it.
-- no rows: every address_id refers to a address.
SELECT t.customer_id, t.address_id
FROM customer t
WHERE NOT EXISTS (SELECT 1
                  FROM address p
                  WHERE p.address_id = t.address_id)
;

-- an address more than one customer has.
-- no rows: every customer has an address of their own.
SELECT address_id, COUNT(*)
FROM customer
GROUP BY address_id
HAVING COUNT(*) > 1
;


-- -------------------------------------------------------------------------------------------------------------- active
-- active is a TINYINT(1) used as a boolean; the values it holds.

-- how many customers each active has; 15 inactive and 584 active.
SELECT active, COUNT(*) AS customers
FROM customer
GROUP BY active
ORDER BY active ASC
;


-- --------------------------------------------------------------------------------------------------------- create_date
-- a trigger, customer_create_date, sets create_date to NOW() on every insert, whatever value is given; the installed
-- rows were loaded before the trigger was created, so they keep theirs; 2006-02-14 22:04:36 to 22:04:37.
SELECT MIN(create_date), MAX(create_date)
FROM customer
;


-- --------------------------------------------------------------------------------------------------------- last_update
-- the database sets last_update on insert, and again on every update; the range the installed rows carry.
-- 2006-02-15 04:57:20, a single moment, as every row was loaded at once
SELECT MIN(last_update), MAX(last_update)
FROM customer
;

-- last_update is nullable here, unlike in most tables, though it has a default.
-- no rows: every row has a last_update.
SELECT *
FROM customer
WHERE last_update IS NULL
;


-- ---------------------------------------------------------------------------------------------------------------------
-- a customer who never rented.
-- no rows: every customer has at least one rental.
SELECT c.customer_id, c.first_name, c.last_name
FROM customer c
WHERE NOT EXISTS (SELECT 1
                  FROM rental r
                  WHERE r.customer_id = c.customer_id)
;
