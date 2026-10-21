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
-- Profiling queries on address, a section per column. Each says what it looks for and what the result should be;
-- 'no rows' marks a check that passes when nothing comes back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/address.sql
--


-- ---------------------------------------------------------------------------------------------------------- address_id
-- how many rows, and the smallest and the largest address_id; 603 addresses, 1 to 605.
SELECT COUNT(*), MIN(address_id), MAX(address_id)
FROM address
;

-- an address_id whose next value is missing, below the largest.
-- 257 and 518: the installed data has no address of either id.
SELECT a.address_id + 1 AS missing_from
FROM address a
WHERE a.address_id < (SELECT MAX(address_id) FROM address)
  AND NOT EXISTS (SELECT 1
                  FROM address b
                  WHERE b.address_id = a.address_id + 1)
;


-- ------------------------------------------------------------------------------------------------------------- address
-- the shortest and the longest address; 9 and 38 characters.
SELECT MIN(CHAR_LENGTH(address)), MAX(CHAR_LENGTH(address))
FROM address
;

-- an address which is blank, or carries leading or trailing spaces.
-- no rows: every address is trimmed and not blank.
SELECT address_id, CONCAT('[', address, ']') AS address
FROM address
WHERE TRIM(address) = ''
   OR CHAR_LENGTH(address) <> CHAR_LENGTH(TRIM(address))
;


-- ------------------------------------------------------------------------------------------------------------ address2
-- address2 is nullable; how many rows have none, how many have an empty one, and how many have a value.
-- 4 have none and 599 an empty one; no address has an address2.
SELECT CASE
           WHEN address2 IS NULL THEN 'null'
           WHEN address2 = '' THEN 'empty'
           ELSE 'value'
           END  AS address2,
       COUNT(*) AS addresses
FROM address
GROUP BY 1
ORDER BY 1 ASC
;


-- ------------------------------------------------------------------------------------------------------------ district
-- the shortest and the longest district; 0 and 20 characters.
SELECT MIN(CHAR_LENGTH(district)), MAX(CHAR_LENGTH(district))
FROM address
;

-- a district which is blank, or carries leading or trailing spaces.
-- 3 rows, 30, 386 and 519, each with an empty district; none is padded.
SELECT address_id, CONCAT('[', district, ']') AS district
FROM address
WHERE TRIM(district) = ''
   OR CHAR_LENGTH(district) <> CHAR_LENGTH(TRIM(district))
;

-- the schema has no unique key on (district, address); an address a district has more than once.
-- no rows: no district has the same address twice.
SELECT district, address, COUNT(*) AS addresses
FROM address
GROUP BY district, address
HAVING COUNT(*) > 1
;


-- ------------------------------------------------------------------------------------------------------------- city_id
-- a city_id which refers to no city; the foreign key forbids it.
-- no rows: every city_id refers to a city.
SELECT t.address_id, t.city_id
FROM address t
WHERE NOT EXISTS (SELECT 1
                  FROM city p
                  WHERE p.city_id = t.city_id)
;

-- how many addresses a city has, at most; 2.
SELECT MAX(addresses)
FROM (SELECT city_id, COUNT(*) AS addresses
      FROM address
      GROUP BY city_id) t
;


-- --------------------------------------------------------------------------------------------------------- postal_code
-- postal_code is nullable, and free text; how many rows have none, an empty one, or a value.
-- 4 have an empty one and 599 a value; none is NULL.
SELECT CASE
           WHEN postal_code IS NULL THEN 'null'
           WHEN postal_code = '' THEN 'empty'
           ELSE 'value'
           END  AS postal_code,
       COUNT(*) AS addresses
FROM address
GROUP BY 1
ORDER BY 1 ASC
;

-- a postal_code which is not all digits.
-- no rows: every postal_code that is not empty is digits.
SELECT address_id, postal_code
FROM address
WHERE postal_code <> ''
  AND NOT REGEXP_LIKE(postal_code, '^[0-9]+$')
;


-- --------------------------------------------------------------------------------------------------------------- phone
SELECT MIN(CHAR_LENGTH(phone)), MAX(CHAR_LENGTH(phone))
FROM address
;

-- a phone which is not all digits; phone is not nullable, but may be empty.
-- 2 rows, 1 and 2, each with an empty phone; every other phone is digits.
SELECT address_id, CONCAT('[', phone, ']') AS phone
FROM address
WHERE NOT REGEXP_LIKE(phone, '^[0-9]+$')
;


-- ------------------------------------------------------------------------------------------------------------ location
-- location is a GEOMETRY with a spatial index; what kinds of geometry, and in which SRID.
-- every location is a POINT, in SRID 0.
SELECT ST_GeometryType(location) AS type, ST_SRID(location) AS srid, COUNT(*) AS addresses
FROM address
GROUP BY 1, 2
;

-- the SRID the column declares; a geometry in any other SRID is rejected.
-- 0: the column is a NOT NULL geometry restricted to SRID 0.
SELECT column_name, data_type, is_nullable, srs_id
FROM information_schema.columns
WHERE table_schema = DATABASE()
  AND table_name = 'address'
  AND column_name = 'location'
;

-- the smallest and the largest of each coordinate; SRID 0 gives them no geographic meaning.
-- x from -719213205 to 766456911, y from -37.7833313 to 489535594.
SELECT MIN(ST_X(location)), MAX(ST_X(location)), MIN(ST_Y(location)), MAX(ST_Y(location))
FROM address
;

-- a location whose x is not a longitude, or whose y is not a latitude.
-- 6 rows, 63, 97, 140, 166, 255 and 521, each with one coordinate far out of the range.
SELECT address_id, ST_X(location) AS x, ST_Y(location) AS y
FROM address
WHERE ST_X(location) NOT BETWEEN -180 AND 180
   OR ST_Y(location) NOT BETWEEN -90 AND 90
ORDER BY address_id ASC
;

-- a location more than one address has.
-- 1 row: 144 addresses are at POINT(0 0); every other location is distinct.
SELECT ST_AsText(location) AS location, COUNT(*) AS addresses
FROM address
GROUP BY location
HAVING COUNT(*) > 1
;


-- --------------------------------------------------------------------------------------------------------- last_update
-- the database sets last_update on insert, and again on every update; the range the installed rows carry.
-- 2014-09-25 22:29:59 to 22:34:11, later than the other tables
SELECT MIN(last_update), MAX(last_update)
FROM address
;


-- ---------------------------------------------------------------------------------------------------------------------
-- an address no customer, staff member, or store uses.
-- no rows: every address is used.
SELECT a.address_id, a.address, a.district
FROM address a
WHERE NOT EXISTS (SELECT 1 FROM customer c WHERE c.address_id = a.address_id)
  AND NOT EXISTS (SELECT 1 FROM staff s WHERE s.address_id = a.address_id)
  AND NOT EXISTS (SELECT 1 FROM store s WHERE s.address_id = a.address_id)
;
