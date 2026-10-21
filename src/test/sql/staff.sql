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
-- Profiling queries on staff, a section per column. Each says what it looks for and what the result should be;
-- 'no rows' marks a check that passes when nothing comes back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/staff.sql
--


-- ------------------------------------------------------------------------------------------------------------ staff_id
-- 2 rows: Mike Hillyer, of store 1, and Jon Stephens, of store 2.
SELECT staff_id,
       first_name,
       last_name,
       address_id,
       email,
       store_id,
       active,
       username,
       last_update
FROM staff
ORDER BY staff_id ASC
;


-- ---------------------------------------------------------------------------------------------------------- first_name
-- the shortest and the longest first_name; 3 and 4 characters.
SELECT MIN(CHAR_LENGTH(first_name)), MAX(CHAR_LENGTH(first_name))
FROM staff
;

-- a first_name which is blank, or carries leading or trailing spaces.
-- no rows: every first_name is trimmed and not blank.
SELECT staff_id, CONCAT('[', first_name, ']') AS first_name
FROM staff
WHERE TRIM(first_name) = ''
   OR CHAR_LENGTH(first_name) <> CHAR_LENGTH(TRIM(first_name))
;


-- ----------------------------------------------------------------------------------------------------------- last_name
-- the shortest and the longest last_name; 7 and 8 characters.
SELECT MIN(CHAR_LENGTH(last_name)), MAX(CHAR_LENGTH(last_name))
FROM staff
;

-- a last_name which is blank, or carries leading or trailing spaces.
-- no rows: every last_name is trimmed and not blank.
SELECT staff_id, CONCAT('[', last_name, ']') AS last_name
FROM staff
WHERE TRIM(last_name) = ''
   OR CHAR_LENGTH(last_name) <> CHAR_LENGTH(TRIM(last_name))
;


-- ---------------------------------------------------------------------------------------------------------- address_id
-- an address_id which refers to no address; the foreign key forbids it.
-- no rows: every address_id refers to a address.
SELECT t.staff_id, t.address_id
FROM staff t
WHERE NOT EXISTS (SELECT 1
                  FROM address p
                  WHERE p.address_id = t.address_id)
;


-- ------------------------------------------------------------------------------------------------------------- picture
-- picture is a nullable BLOB; who has one, and how big. Staff 1 alone, of 36365 bytes.
SELECT staff_id, LENGTH(picture) AS bytes
FROM staff
ORDER BY staff_id ASC
;


-- --------------------------------------------------------------------------------------------------------------- email
-- email is nullable, and has no unique key; both have one, in the form First.Last@sakilastaff.com.
SELECT staff_id, email
FROM staff
ORDER BY staff_id ASC
;


-- ------------------------------------------------------------------------------------------------------------ store_id
-- a store_id which refers to no store; the foreign key forbids it.
-- no rows: every store_id refers to a store.
SELECT t.staff_id, t.store_id
FROM staff t
WHERE NOT EXISTS (SELECT 1
                  FROM store p
                  WHERE p.store_id = t.store_id)
;


-- -------------------------------------------------------------------------------------------------------------- active
-- active is a TINYINT(1) used as a boolean.

-- how many staff each active has; both are active.
SELECT active, COUNT(*) AS staff
FROM staff
GROUP BY active
ORDER BY active ASC
;


-- ------------------------------------------------------------------------------------------------------------ username
-- username has no unique key; a username more than one staff member has.
-- no rows: every username is unique.
SELECT username, COUNT(*)
FROM staff
GROUP BY username
HAVING COUNT(*) > 1
;


-- ------------------------------------------------------------------------------------------------------------ password
-- password is a nullable VARCHAR(40), the length of a hex SHA-1; who has one, and how long. Staff 1 alone, of 40.
SELECT staff_id, CHAR_LENGTH(password) AS length
FROM staff
ORDER BY staff_id ASC
;


-- --------------------------------------------------------------------------------------------------------- last_update
-- the database sets last_update on insert, and again on every update; the range the installed rows carry.
-- 2006-02-15 03:57:16, a single moment, as every row was loaded at once
SELECT MIN(last_update), MAX(last_update)
FROM staff
;
