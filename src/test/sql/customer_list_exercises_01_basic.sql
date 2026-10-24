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
-- Exercises at the basic level on the customer_list view: one table: a filter, a sort, a count or an aggregate. Each
-- poses a question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/customer_list_exercises_01_basic.sql
--


-- ------------------------------------------------------------------------------------------- SAKILA_CUSTOMER_LIST_B_01
-- the customers in Japan.
-- +-----+-------------------+-----------------------------------+----------+--------------+--------------+---------+--------+-----+
-- | ID  | name              | address                           | zip code | phone        | city         | country | notes  | SID |
-- +-----+-------------------+-----------------------------------+----------+--------------+--------------+---------+--------+-----+
-- |   1 | MARY SMITH        | 1913 Hanoi Way                    | 35200    | 28303384290  | Sasebo       | Japan   | active |   1 |
-- |  11 | LISA ANDERSON     | 1542 Tarlac Parkway               | 1027     | 635297277345 | Sagamihara   | Japan   | active |   2 |
-- |  29 | ANGELA HERNANDEZ  | 786 Aurora Avenue                 | 65750    | 18461860151  | Shimonoseki  | Japan   | active |   2 |
-- |  34 | REBECCA SCOTT     | 61 Tama Street                    | 94065    | 708403338270 | Kurashiki    | Japan   | active |   2 |
-- |  53 | HEATHER MORRIS    | 17 Kabul Boulevard                | 38594    | 697760867968 | Nagareyama   | Japan   | active |   1 |
-- |  54 | TERESA ROGERS     | 1964 Allappuzha (Alleppey) Street | 48980    | 920811325222 | Iwakuni      | Japan   | active |   1 |
-- |  79 | RACHEL BARNES     | 586 Tete Way                      | 1079     | 18581624103  | Kamakura     | Japan   | active |   1 |
-- | 141 | DEBBIE REYES      | 928 Jaffna Loop                   | 93762    | 581852137991 | Fukuyama     | Japan   | active |   1 |
-- | 147 | JOANNE ROBERTSON  | 1337 Lincoln Parkway              | 99457    | 597815221267 | Urawa        | Japan   | active |   2 |
-- | 163 | CATHY SPENCER     | 1287 Xi´angfan Boulevard          | 57844    | 819416131190 | Kakamigahara | Japan   | active |   1 |
-- | 194 | KRISTEN CHAVEZ    | 345 Oshawa Boulevard              | 32114    | 104491201771 | Hino         | Japan   | active |   2 |
-- | 240 | MARLENE WELCH     | 1148 Saarbrücken Parkway          | 1921     | 137773001988 | Iwaki        | Japan   | active |   1 |
-- | 253 | TERRY CARLSON     | 752 Ondo Loop                     | 32474    | 134673576619 | Miyakonojo   | Japan   | active |   1 |
-- | 264 | GWENDOLYN MAY     | 446 Kirovo-Tšepetsk Lane          | 19428    | 303967439816 | Higashiosaka | Japan   | active |   1 |
-- | 285 | MIRIAM MCKINNEY   | 1336 Benin City Drive             | 46044    | 341242939532 | Otsu         | Japan   | active |   1 |
-- | 299 | JAMES GANNON      | 1635 Kuwana Boulevard             | 52137    | 710603868323 | Hiroshima    | Japan   | active |   2 |
-- | 337 | JERRY JORDON      | 124 al-Manama Way                 | 52368    | 647899404952 | Onomichi     | Japan   | active |   1 |
-- | 355 | TERRY GRISSOM     | 619 Huánuco Avenue                | 81508    | 142596392389 | Matsue       | Japan   | active |   2 |
-- | 365 | BRUCE SCHWARZ     | 1565 Tangail Manor                | 45750    | 634445428822 | Okinawa      | Japan   | active |   2 |
-- | 385 | PHILLIP HOLM      | 102 Chapra Drive                  | 14073    | 776031833752 | Tama         | Japan   | active |   1 |
-- | 391 | CLARENCE GAMEZ    | 767 Pyongyang Drive               | 83536    | 667736124769 | Izumisano    | Japan   | active |   1 |
-- | 396 | EARL SHANKS       | 168 Cianjur Manor                 | 73824    | 679095087143 | Iwatsuki     | Japan   | active |   1 |
-- | 401 | TONY CARRANZA     | 454 Patiala Lane                  | 13496    | 794553031307 | Koriyama     | Japan   | active |   2 |
-- | 404 | STANLEY SCROGGINS | 1266 Laredo Parkway               | 7664     | 1483365694   | Omiya        | Japan   | active |   2 |
-- | 429 | FREDERICK ISBELL  | 1404 Taguig Drive                 | 87212    | 572068624538 | Tsuyama      | Japan   | active |   2 |
-- | 489 | RICARDO MEADOR    | 591 Sungai Petani Drive           | 46400    | 37247325001  | Okayama      | Japan   | active |   1 |
-- | 503 | ANGEL BARCLAY     | 496 Celaya Drive                  | 90797    | 759586584889 | Ueda         | Japan   | active |   1 |
-- | 519 | RON DELUCA        | 1949 Sanya Street                 | 61244    | 132100972047 | Isesaki      | Japan   | active |   2 |
-- | 531 | JAMIE WAUGH       | 958 Sagamihara Lane               | 88408    | 427274926505 | Kuwana       | Japan   | active |   2 |
-- | 547 | KURT EMMONS       | 1421 Quilmes Lane                 | 19151    | 135407755975 | Kanazawa     | Japan   | active |   1 |
-- | 574 | JULIAN VEST       | 923 Tangail Boulevard             | 33384    | 315528269898 | Akishima     | Japan   | active |   2 |
-- +-----+-------------------+-----------------------------------+----------+--------------+--------------+---------+--------+-----+
SELECT *
FROM customer_list
WHERE country = 'Japan'
ORDER BY ID
;

-- ------------------------------------------------------------------------------------------- SAKILA_CUSTOMER_LIST_B_02
-- how many customers each store has, as the view's SID says.
-- +----------+------------------+------------+---------------------+-----------+
-- | store_id | manager_staff_id | address_id | last_update         | customers |
-- +----------+------------------+------------+---------------------+-----------+
-- |        1 |                1 |          1 | 2006-02-15 04:57:12 |       326 |
-- |        2 |                2 |          2 | 2006-02-15 04:57:12 |       273 |
-- +----------+------------------+------------+---------------------+-----------+
SELECT st.*, COUNT(*) AS customers
FROM customer_list l
         JOIN store st ON st.store_id = l.SID
GROUP BY st.store_id
ORDER BY st.store_id
;

-- ------------------------------------------------------------------------------------------- SAKILA_CUSTOMER_LIST_B_03
-- how many customers the view's notes call active.
-- +--------+-----------+
-- | notes  | customers |
-- +--------+-----------+
-- |        |        15 |
-- | active |       584 |
-- +--------+-----------+
SELECT notes, COUNT(*) AS customers
FROM customer_list
GROUP BY notes
ORDER BY notes
;

-- ------------------------------------------------------------------------------------------- SAKILA_CUSTOMER_LIST_B_04
-- how many customers live on an Avenue.
-- +-----------+
-- | customers |
-- +-----------+
-- |        59 |
-- +-----------+
SELECT COUNT(*) AS customers
FROM customer_list
WHERE address LIKE '% Avenue'
;
