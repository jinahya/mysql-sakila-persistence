package com.github.jinahya.mysql.sakila.persistence;

/*-
 * #%L
 * mysql-sakila-persistence
 * %%
 * Copyright (C) 2026 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utilities shared across the {@code sakila} database's classes.
 * <p>
 * Only what is shared goes here; what belongs to a single entity stays in that entity.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
final class __Utils {

    @Deprecated(forRemoval = true)
    static class OfJavaUtilLocale {

        private static final Map<String, Locale> COUNTRY_LOCALES = new ConcurrentHashMap<>();

        private static final Map<String, Locale> COUNTRY_LOCALES_CUSTOM =
                Collections.unmodifiableMap(new HashMap<String, Locale>() {{
                    // the sakila's names which the JDK spells differently; the JDK's own spelling follows each
                    put("Congo, The Democratic Republic of the", new Locale("", "CD")); // Congo - Kinshasa
                    put("Czech Republic", new Locale("", "CZ")); // Czechia
                    put("Holy See (Vatican City State)", new Locale("", "VA")); // Vatican City
                    put("Hong Kong", new Locale("", "HK")); // Hong Kong SAR China
                    put("Kazakstan", new Locale("", "KZ")); // Kazakhstan
                    put("Myanmar", new Locale("", "MM")); // Myanmar (Burma)
                    put("Russian Federation", new Locale("", "RU")); // Russia
                    put("Saint Vincent and the Grenadines", new Locale("", "VC")); // St. Vincent & Grenadines
                    put("Turkey", new Locale("", "TR")); // Türkiye
                    put("Virgin Islands, U.S.", new Locale("", "VI")); // U.S. Virgin Islands
                    // Yugoslavia, YU, is no longer an ISO 3166 country
                }});

        static Locale of(final String displayCountryInEnglish) {
            Objects.requireNonNull(displayCountryInEnglish, "displayCountryInEnglish is null");
            final var custom = COUNTRY_LOCALES_CUSTOM.get(displayCountryInEnglish);
            if (custom != null) {
                return custom;
            }
            return COUNTRY_LOCALES.computeIfAbsent(
                    displayCountryInEnglish,
                    k -> {
                        return Arrays.stream(Locale.getAvailableLocales())
                                .filter(v -> displayCountryInEnglish.equals(v.getDisplayCountry(Locale.ENGLISH)))
                                .findFirst()
                                .orElse(null);
                    });
        }

        private OfJavaUtilLocale() {
            throw new AssertionError("instantiation is not allowed");
        }
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    private __Utils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
