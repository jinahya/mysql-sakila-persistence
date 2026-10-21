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

import com.github.jinahya.object.randomizer.PodamObjectRandomizer;
import uk.co.jemos.podam.api.ClassInfoStrategy;
import uk.co.jemos.podam.api.DataProviderStrategy;
import uk.co.jemos.podam.api.PodamFactory;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A randomizer which produces randomized {@link Film} instances.
 * <p>
 * The {@code filmId}, which the database generates, the {@code lastUpdate}, which the database fills, and the
 * associations are excluded from randomization; {@link Film_Persister} supplies the {@code language}. The numeric
 * attributes and the {@code specialFeatures} are assigned after the fact, within what their columns hold.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class Film_Randomizer
        extends PodamObjectRandomizer<Film> {

    Film_Randomizer() {
        super(Film.class, List.of(
                Film.ATTRIBUTE_NAME_FILM_ID,
                Film.ATTRIBUTE_NAME_LANGUAGE,
                Film.ATTRIBUTE_NAME_ORIGINAL_LANGUAGE,
                Film.ATTRIBUTE_NAME_LAST_UPDATE,
                Film.ATTRIBUTE_NAME_FILM_ACTORS,
                Film.ATTRIBUTE_NAME_FILM_CATEGORIES,
                Film.ATTRIBUTE_NAME_INVENTORIES
        ));
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    protected DataProviderStrategy getDataProviderStrategy() {
        return super.getDataProviderStrategy();
    }

    @Override
    protected ClassInfoStrategy getClassInfoStrategy() {
        return super.getClassInfoStrategy();
    }

    @Override
    protected PodamFactory getPodamFactory() {
        return super.getPodamFactory();
    }

    @Override
    public Film get() {
        final var instance = super.get();
        final var random = ThreadLocalRandom.current();
        instance.setReleaseYear(
                random.nextInt(Film.ATTRIBUTE_MIN_RELEASE_YEAR, Film.ATTRIBUTE_MAX_RELEASE_YEAR + 1));
        instance.setRentalDuration(
                random.nextInt(Film.ATTRIBUTE_MIN_RENTAL_DURATION, Film.ATTRIBUTE_MAX_RENTAL_DURATION + 1));
        instance.setRentalRate(BigDecimal.valueOf(random.nextInt(0, 10000), Film.COLUMN_SCALE_RENTAL_RATE));
        instance.setLength(random.nextInt(Film.ATTRIBUTE_MIN_LENGTH, Film.ATTRIBUTE_MAX_LENGTH + 1));
        instance.setReplacementCost(
                BigDecimal.valueOf(random.nextInt(0, 100000), Film.COLUMN_SCALE_REPLACEMENT_COST));
        // any subset of the members; the empty one is formatted as null, which the column allows
        final var features = EnumSet.noneOf(Film.SpecialFeature.class);
        for (final var feature : Film.SpecialFeature.values()) {
            if (random.nextBoolean()) {
                features.add(feature);
            }
        }
        instance.setSpecialFeatures(Film.SpecialFeature.format(features));
        return instance;
    }
}
