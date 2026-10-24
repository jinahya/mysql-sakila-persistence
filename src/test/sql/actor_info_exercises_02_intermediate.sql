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
-- Exercises at the intermediate level on the actor_info view: a join, a GROUP BY with HAVING, a subquery, or a NOT
-- EXISTS. Each poses a question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/actor_info_exercises_02_intermediate.sql
--


-- ---------------------------------------------------------------------------------------------- SAKILA_ACTOR_INFO_I_01
-- whether the view has a row for every actor.
-- +------------+-------+
-- | actor_info | actor |
-- +------------+-------+
-- |        200 |   200 |
-- +------------+-------+
SELECT (SELECT COUNT(*) FROM actor_info) AS actor_info, (SELECT COUNT(*) FROM actor) AS actor
;

-- ---------------------------------------------------------------------------------------------- SAKILA_ACTOR_INFO_I_02
-- the actors whose film_info covers every category.
-- +----------+------------+-----------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
-- | actor_id | first_name | last_name | film_info                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
-- +----------+------------+-----------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
-- |       13 | UMA        | WOOD      | Action: ANTITRUST TOMATOES, CLUELESS BUCKET; Animation: CLASH FREDDY, INCH JET, SNOWMAN ROLLERCOASTER; Children: CABIN FLASH, LEGALLY SECRETARY, MODEL FISH, SPLENDOR PATTON; Classics: GALAXY SWEETHEARTS, STEEL SANTA; Comedy: GROUNDHOG UNCUT, LIFE TWISTED, LION UNCUT; Documentary: HOMEWARD CIDER, METAL ARMAGEDDON, WEDDING APOLLO; Drama: TORQUE BOUND; Family: LOLITA WORLD, SPLASH GUMP; Foreign: FRIDA SLIPPER; Games: GRINCH MASSAGE, MOONWALKER FOOL; Horror: MOTIONS DETAILS, ZHIVAGO CORE; Music: ALONE TRIP, REBEL AIRPORT; New: ATTRACTION NEWTON, CHINATOWN GLADIATOR; Sci-Fi: DAISY MENAGERIE, SOLDIERS EVOLUTION; Sports: DRIVER ANNIE, RIDER CADDYSHACK; Travel: BOONDOCK BALLROOM, LEATHERNECKS DWARFS                                                                                                                                   |
-- |       72 | SEAN       | WILLIAMS  | Action: WATERFRONT DELIVERANCE; Animation: FLOATS GARDEN, ROOM ROMAN, SUNRISE LEAGUE; Children: IDOLS SNATCHERS; Classics: DRACULA CRYSTAL; Comedy: VELVET TERMINATOR; Documentary: SAGEBRUSH CLUELESS; Drama: WEST LION; Family: MAGUIRE APACHE; Foreign: NEWTON LABYRINTH, OPUS ICE; Games: FORWARD TEMPLE, WANDA CHAMBER; Horror: ARABIA DOGMA, PATTON INTERVIEW, STRANGERS GRAFFITI; Music: OLEANDER CLUE, REBEL AIRPORT; New: CHINATOWN GLADIATOR; Sci-Fi: EXPRESS LONELY; Sports: DIVORCE SHINING, SEABISCUIT PUNK; Travel: ENOUGH RAGING, HAUNTED ANTITRUST, MUSCLE BRIGHT                                                                                                                                                                                                                                                                              |
-- |       95 | DARYL      | WAHLBERG  | Action: CASUALTIES ENCINO, FORREST SONS, GRAIL FRANKENSTEIN, RUGRATS SHAKESPEARE; Animation: DOUBLE WRATH, POND SEATTLE; Children: HOLOCAUST HIGHBALL; Classics: SPIKING ELEMENT; Comedy: LIFE TWISTED; Documentary: NORTH TEQUILA, STREAK RIDGEMONT; Drama: UNFAITHFUL KILL; Family: KILLER INNOCENT; Foreign: BABY HALL, BALLROOM MOCKINGBIRD, DANGEROUS UPTOWN, GENTLEMEN STAGE, HOOSIERS BIRDCAGE; Games: MADRE GABLES; Horror: ARACHNOPHOBIA ROLLERCOASTER, BEHAVIOR RUNAWAY, FAMILY SWEET; Music: BIRCH ANTITRUST; New: AMISTAD MIDSUMMER, MAIDEN HOME; Sci-Fi: FIDDLER LOST; Sports: HOLES BRANNIGAN, NEIGHBORS CHARADE, SMOOCHY CONTROL; Travel: EXPECATIONS NATURAL, TRAFFIC HOBBIT                                                                                                                                                                   |
-- |      106 | GROUCHO    | DUNST     | Action: CASUALTIES ENCINO, CROW GREASE, DEVIL DESIRE, PARK CITIZEN; Animation: DONNIE ALLEY; Children: LABYRINTH LEAGUE; Classics: COLOR PHILADELPHIA, IRON MOON; Comedy: GOLD RIVER, SUBMARINE BED; Documentary: INDEPENDENCE HOTEL, POCUS PULP; Drama: DALMATIONS SWEDEN, HUNCHBACK IMPOSSIBLE, MOB DUFFEL; Family: BLUES INSTINCT, GABLES METROPOLIS, OPPOSITE NECKLACE, SENSIBILITY REAR, SUNSET RACER; Foreign: HAPPINESS UNITED, KISS GLORY, MAUDE MOD; Games: CREATURES SHAKESPEARE, PANKY SUBMARINE, VANILLA DAY; Horror: TEMPLE ATTRACTION; Music: RUNNER MADIGAN; New: BUTCH PANTHER, CHAPLIN LICENSE, PHANTOM GLORY; Sci-Fi: ATTACKS HATE, MARRIED GO; Sports: SATISFACTION CONFIDENTIAL; Travel: TOMATOES HELLFIGHTERS                                                                                                                             |
-- |      107 | GINA       | DEGENERES | Action: CLUELESS BUCKET, EXCITEMENT EVE, MIDNIGHT WESTWARD; Animation: DOGMA FAMILY, HOOK CHARIOTS, STORM HAPPINESS, SUGAR WONKA; Children: HALL CASSIDY, HEARTBREAKERS BRIGHT; Classics: HYDE DOCTOR, MAGNIFICENT CHITTY; Comedy: GUNFIGHT MOON, SEARCHERS WAIT, TRAINSPOTTING STRANGERS; Documentary: BED HIGHBALL, METAL ARMAGEDDON, WIFE TURN; Drama: CONEHEADS SMOOCHY, DARKNESS WAR; Family: GANDHI KWAI, INDIAN LOVE, LOUISIANA HARRY, MOVIE SHAKESPEARE; Foreign: CALENDAR GUNFIGHT, INTRIGUE WORST; Games: SEVEN SWARM; Horror: ELEPHANT TROJAN; Music: CHAMBER ITALIAN, DEER VIRGINIAN, IMPACT ALADDIN, TELEGRAPH VOYAGE; New: CHAPLIN LICENSE; Sci-Fi: CHARIOTS CONSPIRACY, COLDBLOODED DARLING, FRISCO FORREST, GOODFELLAS SALUTE, LICENSE WEEKEND, OPEN AFRICAN, SPIRITED CASUALTIES; Sports: MUMMY CREATURES, SIERRA DIVIDE; Travel: WINDOW SIDE |
-- |      127 | KEVIN      | GARLAND   | Action: TRIP NEWTON; Animation: ARGONAUTS TOWN, WAIT CIDER; Children: BACKLASH UNDEFEATED, CLOCKWORK PARADISE, GHOST GROUNDHOG, MAKER GABLES, MICROCOSMOS PARADISE; Classics: WESTWARD SEABISCUIT; Comedy: CAPER MOTIONS, DADDY PITTSBURGH, GUNFIGHT MOON, MYSTIC TRUMAN; Documentary: SHOW LORD; Drama: BLADE POLISH, GOLDFINGER SENSIBILITY, SCORPION APOLLO; Family: NATURAL STOCK, RAGE GAMES, RESURRECTION SILVERADO; Foreign: BABY HALL, INFORMER DOUBLE, ORANGE GRAPES; Games: CHICAGO NORTH, GRIT CLOCKWORK; Horror: RULES HUMAN; Music: FREAKY POCUS, HEAVENLY GUN, SONG HEDWIG; New: STING PERSONAL; Sci-Fi: STALLION SUNDANCE; Sports: SATISFACTION CONFIDENTIAL; Travel: ENOUGH RAGING                                                                                                                                                             |
-- |      139 | EWAN       | GOODING   | Action: DEVIL DESIRE; Animation: JUGGLER HARDLY, TITANIC BOONDOCK; Children: GRADUATE LORD; Classics: DYNAMITE TARZAN, ROOTS REMEMBER, SUMMER SCARFACE; Comedy: LONELY ELEPHANT; Documentary: DUFFEL APOCALYPSE; Drama: BUNCH MINDS, ENCOUNTERS CURTAIN, QUEEN LUKE; Family: DUMBO LUST; Foreign: COMMAND DARLING, MULHOLLAND BEAST; Games: CURTAIN VIDEOTAPE, ICE CROSSING, ROUGE SQUAD; Horror: ARACHNOPHOBIA ROLLERCOASTER; Music: AMELIE HELLFIGHTERS, BIRCH ANTITRUST, ENEMY ODDS, LUCKY FLYING, OLEANDER CLUE; New: BOULEVARD MOB, CLEOPATRA DEVIL; Sci-Fi: MOURNING PURPLE; Sports: MERMAID INSECTS, POSEIDON FOREVER, RECORDS ZORRO; Travel: BASIC EASY, CONTACT ANONYMOUS, DISCIPLE MOTHER                                                                                                                                                            |
-- |      155 | IAN        | TANDY     | Action: BERETS AGENT, FANTASY TROOPERS; Animation: STORM HAPPINESS; Children: POLISH BROOKLYN, SCARFACE BANG; Classics: DRACULA CRYSTAL; Comedy: ZORRO ARK; Documentary: HAWK CHILL, HOMEWARD CIDER, SAGEBRUSH CLUELESS, WEDDING APOLLO; Drama: NOTTING SPEAKEASY; Family: GLADIATOR WESTWARD; Foreign: CATCH AMISTAD, INFORMER DOUBLE, MIXED DOORS; Games: GUN BONNIE, VIDEOTAPE ARSENIC; Horror: TEXAS WATCH; Music: AMELIE HELLFIGHTERS, DEER VIRGINIAN, WORDS HUNTER, YOUTH KICK; New: MONEY HAROLD; Sci-Fi: CITIZEN SHREK, FIDDLER LOST; Sports: GLEAMING JAWBREAKER, GROOVE FICTION, TIGHTS DAWN; Travel: LEATHERNECKS DWARFS, SHAWSHANK BUBBLE                                                                                                                                                                                                          |
-- |      161 | HARVEY     | HOPE      | Action: SPEAKEASY DATE, STORY SIDE, UPRISING UPTOWN; Animation: BORROWERS BEDAZZLED, OSCAR GOLD, TELEMARK HEARTBREAKERS; Children: MODEL FISH; Classics: HOLY TADPOLE, IRON MOON; Comedy: CRAZY HOME, DOWNHILL ENOUGH; Documentary: BEACH HEARTBREAKERS; Drama: HAROLD FRENCH; Family: ATLANTIS CAUSE, EARRING INSTINCT; Foreign: GENTLEMEN STAGE, HELLFIGHTERS SIERRA, PEARL DESTINY; Games: ENCINO ELF, PIZZA JUMANJI, SLEUTH ORIENT; Horror: PANIC CLUB, RULES HUMAN; Music: UNCUT SUICIDES; New: BOULEVARD MOB, FRONTIER CABIN, UNBREAKABLE KARATE; Sci-Fi: PANTHER REDS, RANDOM GO, UNFORGIVEN ZOOLANDER; Sports: CARIBBEAN LIBERTY; Travel: LOCK REAR                                                                                                                                                                                                    |
-- |      185 | MICHAEL    | BOLGER    | Action: GLASS DYING; Animation: LAWLESS VISION, TELEMARK HEARTBREAKERS; Children: SANTA PARIS; Classics: MALKOVICH PET, PREJUDICE OLEANDER; Comedy: AIRPLANE SIERRA, DYING MAKER; Documentary: HOMEWARD CIDER, METAL ARMAGEDDON, WIFE TURN; Drama: SOMETHING DUCK, TENENBAUMS COMMAND; Family: HOUSE DYNAMITE; Foreign: MIXED DOORS, STEPMOM DREAM; Games: MARS ROMAN, TYCOON GATHERING; Horror: KARATE MOON, PATTON INTERVIEW, WATERSHIP FRONTIER; Music: HEAVENLY GUN; New: BREAKFAST GOLDFINGER, IDAHO LOVE, RIDGEMONT SUBMARINE, UNBREAKABLE KARATE; Sci-Fi: CHARIOTS CONSPIRACY; Sports: NOVOCAINE FLIGHT; Travel: ENOUGH RAGING, LIAISONS SWEET                                                                                                                                                                                                          |
-- |      197 | REESE      | WEST      | Action: ANTITRUST TOMATOES; Animation: DOORS PRESIDENT, TURN STAR; Children: GORGEOUS BINGO; Classics: DRACULA CRYSTAL, MALKOVICH PET, SNATCHERS MONTEZUMA, VOLUME HOUSE; Comedy: LIFE TWISTED; Documentary: CAUSE DATE, COAST RAINBOW; Drama: HOBBIT ALIEN, SAINTS BRIDE, WITCHES PANIC; Family: BEDAZZLED MARRIED, CHOCOLAT HARRY, REMEMBER DIARY; Foreign: AGENT TRUMAN, LOSE INCH, SCHOOL JACKET; Games: CREATURES SHAKESPEARE, ROUGE SQUAD; Horror: DRUMS DYNAMITE, YENTL IDAHO; Music: RUNNER MADIGAN; New: GODFATHER DIARY, WILD APOLLO; Sci-Fi: GOODFELLAS SALUTE, WEEKEND PERSONAL; Sports: MOSQUITO ARMAGEDDON, NORTHWEST POLISH, POSEIDON FOREVER; Travel: CASABLANCA SUPER                                                                                                                                                                         |
-- +----------+------------+-----------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
SELECT *
FROM actor_info
WHERE CHAR_LENGTH(film_info) - CHAR_LENGTH(REPLACE(film_info, ':', '')) = (SELECT COUNT(*) FROM category)
ORDER BY actor_id
;

-- ---------------------------------------------------------------------------------------------- SAKILA_ACTOR_INFO_I_03
-- how many actors cover how many categories.
-- +------------+--------+
-- | categories | actors |
-- +------------+--------+
-- |         16 |     11 |
-- |         15 |     19 |
-- |         14 |     49 |
-- |         13 |     57 |
-- |         12 |     33 |
-- |         11 |     19 |
-- |         10 |      8 |
-- |          9 |      2 |
-- |          8 |      2 |
-- +------------+--------+
SELECT CHAR_LENGTH(film_info) - CHAR_LENGTH(REPLACE(film_info, ':', '')) AS categories, COUNT(*) AS actors
FROM actor_info
GROUP BY categories
ORDER BY categories DESC
;
