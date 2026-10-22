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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests the static members and the nested types of {@link Film}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class Film_StaticTest {

    /**
     * Tests {@link Film.Rating} and {@link Film.RatingConverter}.
     */
    @Nested
    class Rating_Test {

        @DisplayName("the column values are the members of the rating ENUM, in order")
        @Test
        void columnValues_AreTheEnumMembers() {
            assertThat(EnumSet.allOf(Film.Rating.class))
                    .extracting(Film.Rating::columnValue)
                    .containsExactly("G", "PG", "PG-13", "R", "NC-17");
        }

        @DisplayName("valueOfColumnValue(columnValue()) is the constant itself")
        @EnumSource(Film.Rating.class)
        @ParameterizedTest
        void valueOfColumnValue_Same_(final Film.Rating rating) {
            assertThat(Film.Rating.valueOfColumnValue(rating.columnValue())).isSameAs(rating);
        }

        @DisplayName("valueOfColumnValue throws for a value which is not a member")
        @Test
        void valueOfColumnValue_Throws_Unknown() {
            assertThatThrownBy(() -> Film.Rating.valueOfColumnValue("PG_13"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("the converter round-trips every constant, and null")
        @EnumSource(Film.Rating.class)
        @ParameterizedTest
        void converter_RoundTrips_(final Film.Rating rating) {
            final var converter = new Film.RatingConverter();
            assertThat(
                    converter.convertToEntityAttribute(converter.convertToDatabaseColumn(rating)))
                    .isSameAs(rating);
            assertThat(converter.convertToDatabaseColumn(null)).isNull();
            assertThat(converter.convertToEntityAttribute(null)).isNull();
        }
    }

    /**
     * Tests {@link Film.SpecialFeature}.
     */
    @Nested
    class SpecialFeature_Test {

        @DisplayName("the column values are the members of the special_features SET, in order")
        @Test
        void columnValues_AreTheSetMembers() {
            assertThat(EnumSet.allOf(Film.SpecialFeature.class))
                    .extracting(Film.SpecialFeature::columnValue)
                    .containsExactly("Trailers", "Commentaries", "Deleted Scenes",
                                     "Behind the Scenes");
        }

        @DisplayName("parse(null) and parse(\"\") are empty")
        @Test
        void parse_Empty_NullOrEmpty() {
            assertThat(Film.SpecialFeature.parse(null)).isEmpty();
            assertThat(Film.SpecialFeature.parse("")).isEmpty();
        }

        @DisplayName("parse reads a value the way MySQL writes it")
        @Test
        void parse_() {
            assertThat(Film.SpecialFeature.parse("Deleted Scenes,Behind the Scenes"))
                    .containsExactly(Film.SpecialFeature.DELETED_SCENES,
                                     Film.SpecialFeature.BEHIND_THE_SCENES);
        }

        @DisplayName("parse throws for a member which is not one")
        @Test
        void parse_Throws_Unknown() {
            assertThatThrownBy(() -> Film.SpecialFeature.parse("Trailers,Bloopers"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("format lists the members in the order MySQL does, whatever the order given")
        @Test
        void format_InDeclarationOrder() {
            final var formatted = Film.SpecialFeature.format(
                    List.of(Film.SpecialFeature.BEHIND_THE_SCENES, Film.SpecialFeature.TRAILERS));
            assertThat(formatted).isEqualTo("Trailers,Behind the Scenes");
        }

        @DisplayName("format of no constant is null")
        @Test
        void format_Null_Empty() {
            assertThat(Film.SpecialFeature.format(Set.of())).isNull();
        }

        @DisplayName("parse(format(all)) is all")
        @Test
        void parse_format_RoundTrips() {
            final var all = EnumSet.allOf(Film.SpecialFeature.class);
            assertThat(Film.SpecialFeature.parse(Film.SpecialFeature.format(all))).isEqualTo(all);
        }
    }
}
