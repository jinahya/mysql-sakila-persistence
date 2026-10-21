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
-- Profiling queries on store, a section per column. Each says what it looks for and what the result should be;
-- 'no rows' marks a check that passes when nothing comes back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/store.sql
--


-- ------------------------------------------------------------------------------------------------------------ store_id
-- 2 rows: store 1, managed by staff 1, and store 2, managed by staff 2.
SELECT store_id, manager_staff_id, address_id, last_update
FROM store
ORDER BY store_id ASC
;


-- ---------------------------------------------------------------------------------------------------- manager_staff_id
-- a manager_staff_id which refers to no staff; the foreign key forbids it.
-- no rows: every manager_staff_id refers to a staff.
SELECT t.store_id, t.manager_staff_id
FROM store t
WHERE NOT EXISTS (SELECT 1
                  FROM staff p
                  WHERE p.staff_id = t.manager_staff_id)
;

-- a manager who works at another store.
-- no rows: every manager works at the store they manage.
SELECT t.store_id, t.manager_staff_id, s.store_id AS staff_store_id
FROM store t
         JOIN staff s ON s.staff_id = t.manager_staff_id
WHERE s.store_id <> t.store_id
;


-- ---------------------------------------------------------------------------------------------------------- address_id
-- an address_id which refers to no address; the foreign key forbids it.
-- no rows: every address_id refers to a address.
SELECT t.store_id, t.address_id
FROM store t
WHERE NOT EXISTS (SELECT 1
                  FROM address p
                  WHERE p.address_id = t.address_id)
;


-- --------------------------------------------------------------------------------------------------------- last_update
-- the database sets last_update on insert, and again on every update; the range the installed rows carry.
-- 2006-02-15 04:57:12, a single moment, as every row was loaded at once
SELECT MIN(last_update), MAX(last_update)
FROM store
;
