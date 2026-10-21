--
-- Profiling queries on staff_list, a view, a section per column. Each says what it looks for and what the result should be;
-- 'no rows' marks a check that passes when nothing comes back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/staff_list.sql
--


-- ---------------------------------------------------------------------------------------------------------------------
-- the view joins staff with address, city and country; a row per staff member.
SELECT view_definition
FROM information_schema.views
WHERE table_schema = DATABASE()
  AND table_name = 'staff_list'
;


-- ------------------------------------------------------------------------------------------------------------------ ID
-- the rows; 2, as many as staff has.
-- 1, Mike Hillyer of Lethbridge, Canada, at store 1, and 2, Jon Stephens of Woodridge, Australia, at store 2.
SELECT *
FROM staff_list
ORDER BY ID ASC
;

-- an ID more than one row has; the view has no key, and the entity takes ID as its identifier.
-- no rows: ID is unique.
SELECT ID, COUNT(*) AS rows_
FROM staff_list
GROUP BY ID
HAVING COUNT(*) > 1
;


-- ---------------------------------------------------------------------------------------------------------------- name
-- a row whose name or SID does not follow its staff member.
-- no rows: the view takes both from the staff.
SELECT v.ID, v.name, v.SID
FROM staff_list v
         JOIN staff s ON s.staff_id = v.ID
WHERE CAST(v.name AS BINARY) <> CAST(CONCAT(s.first_name, ' ', s.last_name) AS BINARY)
   OR v.SID <> s.store_id
;


-- ------------------------------------------------------------------------------------------------------------ zip code
-- `zip code` is nullable, the address' postal_code; how many rows have none, an empty one, or a value.
-- 2 have an empty one: the addresses of both staff members have no postal_code.
SELECT CASE
           WHEN `zip code` IS NULL THEN 'null'
           WHEN `zip code` = '' THEN 'empty'
           ELSE 'value'
           END  AS `zip code`,
       COUNT(*) AS staff
FROM staff_list
GROUP BY 1
ORDER BY 1 ASC
;


-- --------------------------------------------------------------------------------------------------------------- phone
-- a phone which is not all digits.
-- no rows: 14033335568 and 6172235589.
SELECT ID, CONCAT('[', phone, ']') AS phone
FROM staff_list
WHERE NOT REGEXP_LIKE(phone, '^[0-9]+$')
;
