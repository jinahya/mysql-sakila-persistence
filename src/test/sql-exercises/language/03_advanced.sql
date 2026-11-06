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
-- Exercises at the advanced level on language: references turned into rows with UNION ALL, and counted under
-- conditions. Each poses a question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -p'Passw0rd!' -t sakila < src/test/sql-exercises/language/03_advanced.sql
--


-- ------------------------------------------------------------------------------------------------ SAKILA_LANGUAGE_A_01
-- how many films use each language, as language and as original language.
-- NOT YET RUN
SELECT l.*,
       COUNT(CASE WHEN u.role = 'language' THEN 1 END) AS as_language,
       COUNT(CASE WHEN u.role = 'original' THEN 1 END) AS as_original_language
FROM language l
         LEFT JOIN (SELECT language_id, 'language' AS role
                    FROM film
                    UNION ALL
                    SELECT original_language_id, 'original'
                    FROM film
                    WHERE original_language_id IS NOT NULL) u ON u.language_id = l.language_id
GROUP BY l.language_id
ORDER BY l.language_id
;
