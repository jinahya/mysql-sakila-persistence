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
-- Profiling queries on inventory, a section per column. Each says what it looks for and what the result should be;
-- 'no rows' marks a check that passes when nothing comes back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/inventory.sql
--


-- -------------------------------------------------------------------------------------------------------- inventory_id
-- how many rows, and the smallest and the largest inventory_id; 4581 copies, 1 to 4581.
SELECT COUNT(*), MIN(inventory_id), MAX(inventory_id)
FROM inventory
;

-- an inventory_id whose next value is missing, below the largest.
-- no rows: inventory_id runs from 1 without a gap.
SELECT a.inventory_id + 1 AS missing_from
FROM inventory a
WHERE a.inventory_id < (SELECT MAX(inventory_id) FROM inventory)
  AND NOT EXISTS (SELECT 1
                  FROM inventory b
                  WHERE b.inventory_id = a.inventory_id + 1)
;


-- ------------------------------------------------------------------------------------------------------------- film_id
-- a film_id which refers to no film; the foreign key forbids it.
-- no rows: every film_id refers to a film.
SELECT t.inventory_id, t.film_id
FROM inventory t
WHERE NOT EXISTS (SELECT 1
                  FROM film p
                  WHERE p.film_id = t.film_id)
;

-- how many copies of a film a store has, at least and at most; 2 and 4.
SELECT MIN(copies), MAX(copies)
FROM (SELECT film_id, store_id, COUNT(*) AS copies
      FROM inventory
      GROUP BY film_id, store_id) t
;


-- ------------------------------------------------------------------------------------------------------------ store_id
-- a store_id which refers to no store; the foreign key forbids it.
-- no rows: every store_id refers to a store.
SELECT t.inventory_id, t.store_id
FROM inventory t
WHERE NOT EXISTS (SELECT 1
                  FROM store p
                  WHERE p.store_id = t.store_id)
;

-- how many copies each store_id has; 2270 at store 1 and 2311 at store 2.
SELECT store_id, COUNT(*) AS copies
FROM inventory
GROUP BY store_id
ORDER BY store_id ASC
;


-- --------------------------------------------------------------------------------------------------------- last_update
-- the database sets last_update on insert, and again on every update; the range the installed rows carry.
-- 2006-02-15 05:09:17, a single moment, as every row was loaded at once
SELECT MIN(last_update), MAX(last_update)
FROM inventory
;


-- ---------------------------------------------------------------------------------------------------------------------
-- a copy never rented; one row, copy 5.
SELECT i.inventory_id, i.film_id, i.store_id
FROM inventory i
WHERE NOT EXISTS (SELECT 1
                  FROM rental r
                  WHERE r.inventory_id = i.inventory_id)
;
