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
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/staff_exercises_01_basic.sql
--


-- --------------------------------------------------------------------------------------------------- SAKILA-STAFF-B-01
-- every staff member, with their store.
-- +----------+------------+-----------+----------+--------+
-- | staff_id | first_name | last_name | store_id | active |
-- +----------+------------+-----------+----------+--------+
-- |        1 | Mike       | Hillyer   |        1 |      1 |
-- |        2 | Jon        | Stephens  |        2 |      1 |
-- +----------+------------+-----------+----------+--------+
SELECT staff_id, first_name, last_name, store_id, active
FROM staff
ORDER BY staff_id
;

-- --------------------------------------------------------------------------------------------------- SAKILA-STAFF-B-02
-- the staff members who have no password.
-- +----------+------------+-----------+----------+
-- | staff_id | first_name | last_name | username |
-- +----------+------------+-----------+----------+
-- |        2 | Jon        | Stephens  | Jon      |
-- +----------+------------+-----------+----------+
SELECT staff_id, first_name, last_name, username
FROM staff
WHERE password IS NULL
ORDER BY staff_id
;
