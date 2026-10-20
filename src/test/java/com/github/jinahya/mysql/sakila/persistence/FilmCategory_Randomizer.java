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

/**
 * A randomizer which produces randomized {@link FilmCategory} instances.
 * <p>
 * Every attribute but the {@code lastUpdate}, which the database fills, is a component of the key; all of them are
 * excluded from randomization, and {@link FilmCategory_Persister} supplies the two associations, which set the key with
 * them.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class FilmCategory_Randomizer extends PodamObjectRandomizer<FilmCategory> {

    FilmCategory_Randomizer() {
        super(FilmCategory.class, List.of(
                FilmCategory.ATTRIBUTE_NAME_FILM_ID,
                FilmCategory.ATTRIBUTE_NAME_FILM,
                FilmCategory.ATTRIBUTE_NAME_CATEGORY_ID,
                FilmCategory.ATTRIBUTE_NAME_CATEGORY,
                FilmCategory.ATTRIBUTE_NAME_LAST_UPDATE
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
    public FilmCategory get() {
        return super.get();
    }
}
