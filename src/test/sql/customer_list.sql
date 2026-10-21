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
-- Profiling queries on customer_list, a view, a section per column. Each says what it looks for and what the result should be;
-- 'no rows' marks a check that passes when nothing comes back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/customer_list.sql
--


-- ---------------------------------------------------------------------------------------------------------------------
-- the view joins customer with address, city and country; a row per customer.
SELECT view_definition
FROM information_schema.views
WHERE table_schema = DATABASE()
  AND table_name = 'customer_list'
;


-- ------------------------------------------------------------------------------------------------------------------ ID
-- how many rows, and the smallest and the largest ID; 599 rows, 1 to 599, as many as customer has.
SELECT COUNT(*), MIN(ID), MAX(ID), (SELECT COUNT(*) FROM customer) AS customers
FROM customer_list
;

-- an ID more than one row has; the view has no key, and the entity takes ID as its identifier.
-- no rows: ID is unique.
SELECT ID, COUNT(*) AS rows_
FROM customer_list
GROUP BY ID
HAVING COUNT(*) > 1
;


-- ---------------------------------------------------------------------------------------------------------------- name
-- name is nullable, CONCAT(first_name, ' ', last_name); the shortest and the longest, and how many are NULL.
-- 7 and 21 characters; none is NULL.
SELECT MIN(CHAR_LENGTH(name)), MAX(CHAR_LENGTH(name)), SUM(name IS NULL)
FROM customer_list
;

-- a row whose name, SID, or notes does not follow its customer.
-- no rows: the view takes each from the customer.
SELECT v.ID, v.name, v.SID, v.notes
FROM customer_list v
         JOIN customer c ON c.customer_id = v.ID
WHERE CAST(v.name AS BINARY) <> CAST(CONCAT(c.first_name, ' ', c.last_name) AS BINARY)
   OR v.SID <> c.store_id
   OR (v.notes = 'active') <> (c.active = 1)
;


-- ------------------------------------------------------------------------------------------------------------- address
-- the shortest and the longest address; 9 and 38 characters.
SELECT MIN(CHAR_LENGTH(address)), MAX(CHAR_LENGTH(address))
FROM customer_list
;


-- ------------------------------------------------------------------------------------------------------------ zip code
-- `zip code` is nullable, the address' postal_code; how many rows have none, an empty one, or a value.
-- 599 have a value; the 4 empty postal_codes of address belong to no customer.
SELECT CASE
           WHEN `zip code` IS NULL THEN 'null'
           WHEN `zip code` = '' THEN 'empty'
           ELSE 'value'
           END  AS `zip code`,
       COUNT(*) AS customers
FROM customer_list
GROUP BY 1
ORDER BY 1 ASC
;

-- how many characters a `zip code` has; leading zeros are not kept.
-- 1 of 1, 5 of 3, 59 of 4, and 534 of 5 characters.
SELECT CHAR_LENGTH(`zip code`) AS length, COUNT(*) AS customers
FROM customer_list
GROUP BY 1
ORDER BY 1 ASC
;


-- --------------------------------------------------------------------------------------------------------------- phone
-- the shortest and the longest phone; 9 and 12 characters.
SELECT MIN(CHAR_LENGTH(phone)), MAX(CHAR_LENGTH(phone))
FROM customer_list
;

-- a phone which is not all digits.
-- no rows: the 2 empty phones of address belong to no customer.
SELECT ID, CONCAT('[', phone, ']') AS phone
FROM customer_list
WHERE NOT REGEXP_LIKE(phone, '^[0-9]+$')
;


-- ---------------------------------------------------------------------------------------------------------------- city
-- the shortest and the longest city, and how many distinct; 3 and 26 characters, 597 cities.
SELECT MIN(CHAR_LENGTH(city)), MAX(CHAR_LENGTH(city)), COUNT(DISTINCT city)
FROM customer_list
;


-- ------------------------------------------------------------------------------------------------------------- country
-- the shortest and the longest country, and how many distinct; 4 and 37 characters, 108 countries.
SELECT MIN(CHAR_LENGTH(country)), MAX(CHAR_LENGTH(country)), COUNT(DISTINCT country)
FROM customer_list
;


-- --------------------------------------------------------------------------------------------------------------- notes
-- notes is IF(active, 'active', ''); how many rows have each.
-- 15 have an empty one, and 584 'active'.
SELECT CONCAT('[', notes, ']') AS notes, COUNT(*) AS customers
FROM customer_list
GROUP BY 1
ORDER BY 1 ASC
;


-- ----------------------------------------------------------------------------------------------------------------- SID
-- SID is the customer's store_id; how many customers each store has.
-- 326 of store 1, and 273 of store 2.
SELECT SID, COUNT(*) AS customers
FROM customer_list
GROUP BY SID
ORDER BY SID ASC
;
