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
-- Exercises at the basic level on staff: one table: a filter, a sort, a count or an aggregate. Each poses a question,
-- answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/exercises/staff_exercises_01_basic.sql
--


-- --------------------------------------------------------------------------------------------------- SAKILA_STAFF_B_01
-- every staff member, with their store.
-- +----------+------------+-----------+------------+------------------------------+----------+--------+----------+------------------------------------------+---------------------+
-- | staff_id | first_name | last_name | address_id | email                        | store_id | active | username | password                                 | last_update         |
-- +----------+------------+-----------+------------+------------------------------+----------+--------+----------+------------------------------------------+---------------------+
-- |        1 | Mike       | Hillyer   |          3 | Mike.Hillyer@sakilastaff.com |        1 |      1 | Mike     | 8cb2237d0679ca88db6464eac60da96345513964 | 2006-02-15 03:57:16 |
-- |        2 | Jon        | Stephens  |          4 | Jon.Stephens@sakilastaff.com |        2 |      1 | Jon      | NULL                                     | 2006-02-15 03:57:16 |
-- +----------+------------+-----------+------------+------------------------------+----------+--------+----------+------------------------------------------+---------------------+
SELECT s.staff_id, s.first_name, s.last_name, s.address_id, s.email, s.store_id, s.active, s.username, s.password, s.last_update -- every column but picture, a BLOB which does not print
FROM staff s
ORDER BY s.staff_id
;

-- --------------------------------------------------------------------------------------------------- SAKILA_STAFF_B_02
-- the staff members who have no password.
-- +----------+------------+-----------+------------+------------------------------+----------+--------+----------+----------+---------------------+
-- | staff_id | first_name | last_name | address_id | email                        | store_id | active | username | password | last_update         |
-- +----------+------------+-----------+------------+------------------------------+----------+--------+----------+----------+---------------------+
-- |        2 | Jon        | Stephens  |          4 | Jon.Stephens@sakilastaff.com |        2 |      1 | Jon      | NULL     | 2006-02-15 03:57:16 |
-- +----------+------------+-----------+------------+------------------------------+----------+--------+----------+----------+---------------------+
SELECT s.staff_id, s.first_name, s.last_name, s.address_id, s.email, s.store_id, s.active, s.username, s.password, s.last_update -- every column but picture, a BLOB which does not print
FROM staff s
WHERE s.password IS NULL
ORDER BY s.staff_id
;
