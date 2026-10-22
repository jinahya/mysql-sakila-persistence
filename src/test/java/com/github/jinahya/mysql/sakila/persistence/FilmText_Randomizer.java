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
 * A randomizer which produces randomized {@link FilmText} instances.
 * <p>
 * The {@code filmId} is excluded from randomization; {@link FilmText_Persister} assigns one which
 * no row holds yet. The {@code title} and the {@code description}, which the triggers of the
 * {@value Film#TABLE_NAME} table write, and which are neither insertable nor updatable, are
 * excluded too.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class FilmText_Randomizer
        extends PodamObjectRandomizer<FilmText> {

    FilmText_Randomizer() {
        super(FilmText.class, List.of(
                FilmText.ATTRIBUTE_NAME_FILM_ID,
                FilmText.ATTRIBUTE_NAME_TITLE,
                FilmText.ATTRIBUTE_NAME_DESCRIPTION
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
    public FilmText get() {
        return super.get();
    }
}
