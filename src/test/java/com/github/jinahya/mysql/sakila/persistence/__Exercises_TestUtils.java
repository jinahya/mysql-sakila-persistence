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

import jakarta.persistence.EntityManager;

import java.util.function.Function;

/**
 * Helpers for the {@code *_Exercises_*_PersistenceIT} classes, which answer the exercises of {@code src/test/sql}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
final class __Exercises_TestUtils {

    /**
     * Applies the specified function to an entity manager of the installed {@code sakila} database, and returns the
     * result.
     *
     * @param function the function to apply.
     * @param <R>      the type of the result.
     * @return the result of the {@code function}.
     */
    static <R> R apply(final Function<? super EntityManager, ? extends R> function) {
        return __Persistence_TestUtils.applyEntityManager(_Entity_Persistence_IT.PERSISTENCE_UNIT_NAME, function);
    }

    // ---------------------------------------------------------------------------------------- CONSTRUCTORS
    private __Exercises_TestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
