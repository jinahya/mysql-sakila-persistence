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

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A randomizer which produces randomized {@link EmbeddablePersonName} instances.
 * <p>
 * The class is a record, which PODAM cannot populate after construction, so {@link #get()}
 * constructs one itself, with a random first name and last name, each no longer than its column.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class EmbeddablePersonName_Randomizer
        extends PodamObjectRandomizer<EmbeddablePersonName> {

    EmbeddablePersonName_Randomizer() {
        super(EmbeddablePersonName.class, List.of(

        ));
    }

    // ---------------------------------------------------------------------------------------------
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
    public EmbeddablePersonName get() {
        final var random = ThreadLocalRandom.current();
        return new EmbeddablePersonName(
                randomAlphabetic(random, EmbeddablePersonName.SIZE_MAX_FIRST_NAME),
                randomAlphabetic(random, EmbeddablePersonName.SIZE_MAX_LAST_NAME)
        );
    }

    private static String randomAlphabetic(final ThreadLocalRandom random, final int maxLength) {
        final var length = random.nextInt(1, maxLength + 1);
        final var builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            builder.append((char) random.nextInt('a', 'z' + 1));
        }
        return builder.toString();
    }
}
