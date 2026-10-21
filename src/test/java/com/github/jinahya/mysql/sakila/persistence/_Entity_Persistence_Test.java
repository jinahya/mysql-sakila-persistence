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

import com.github.jinahya.persistence.test.util.EntityPersisterUtils;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.metamodel.ManagedType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.github.jinahya.mysql.sakila.persistence._Entity_Persistence_Test_Producer.__TestPU;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * An abstract base class for tests which need a persistence context, against a module's unit-test persistence unit.
 * <p>
 * The container is started by weld-testing, with {@link _Entity_Persistence_Test_Producer} as its bean class; a test
 * then gets an entity manager on an in-memory database whose schema the provider generates.
 * <p>
 * Nothing here reaches the MySQL database; a test which needs the real data extends {@link _Entity_Persistence_IT} and
 * is named {@code *_IT} so that failsafe, not surefire, runs it.
 *
 * @param <T> the type of the entity under test.
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see _Entity_Persistence_Test_Producer
 */
@AddBeanClasses(_Entity_Persistence_Test_Producer.class)
@EnableAutoWeld
@Slf4j
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S118"  // Abstract class names should comply with a naming convention
})
abstract class _Entity_Persistence_Test<T>
        extends __Test<T> {

    /**
     * The prefix of the constants {@link #_SameValue_ATTRIBUTE_NAME_()} checks. The value is {@value}.
     */
    private static final String ATTRIBUTE_NAME_CONSTANT_PREFIX = "ATTRIBUTE_NAME_";

    /**
     * The prefix of the constants {@link #_Resolvable_ATTRIBUTE_PATH_()} checks. The value is {@value}.
     */
    private static final String ATTRIBUTE_PATH_CONSTANT_PREFIX = "ATTRIBUTE_PATH_";

    /**
     * Returns the {@code UPPER_SNAKE_CASE} form of the specified attribute name.
     *
     * @param attributeName the attribute name to convert; {@code countryIsoCode}, say.
     * @return the converted name; {@code COUNTRY_ISO_CODE} for the example above.
     * @implNote An underscore goes wherever a lower-case letter or a digit is followed by an upper-case one,
     *         which is the boundary the constants in this project are spelled with.
     */
    private static String toUpperSnakeCase(final String attributeName) {
        return attributeName.replaceAll("(?<=[a-z0-9])(?=[A-Z])", "_").toUpperCase(Locale.ROOT);
    }

    /**
     * Creates a new instance for the specified persistence class.
     *
     * @param targetClass the class of the entity under test.
     */
    protected _Entity_Persistence_Test(final Class<T> targetClass) {
        super(targetClass);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns every {@code static final String} field, declared by {@link #targetClass} or inherited by it, whose name
     * starts with the specified prefix, with its value.
     */
    private Map<String, String> constantsPrefixed(final String prefix) {
        final var constants = new TreeMap<String, String>();
        for (Class<?> c = targetClass; c != null; c = c.getSuperclass()) {
            for (final var field : c.getDeclaredFields()) {
                if (!field.getName().startsWith(prefix) || constants.containsKey(field.getName())) {
                    continue;
                }
                final var modifiers = field.getModifiers();
                assertThat(Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)
                           && field.getType() == String.class)
                        .as("%s.%s is a static final String", c.getSimpleName(), field.getName())
                        .isTrue();
                field.setAccessible(true);
                try {
                    constants.put(field.getName(), (String) field.get(null));
                } catch (final IllegalAccessException iae) {
                    throw new AssertionError("failed to read " + c.getSimpleName() + '.' + field.getName(), iae);
                }
            }
        }
        return constants;
    }

    /**
     * Verifies the {@code ATTRIBUTE_NAME_*} constants of {@link #targetClass} both ways: every managed attribute whose
     * constant exists finds that constant holding the attribute's name, and every such constant names a managed
     * attribute of the type.
     *
     * @implNote The first direction runs from the attribute to the constant, so an attribute with no constant
     *         is skipped; whether every attribute ought to have one is a different question. The second runs from the
     *         constant to the attribute, so a constant holding a name the provider does not manage the type by -- one
     *         left behind by a renamed field, or one naming an attribute of some other class -- fails. A path into an
     *         embedded attribute is not an attribute name; it is declared as an {@code ATTRIBUTE_PATH_*} constant and
     *         checked by {@link #_Resolvable_ATTRIBUTE_PATH_()}.
     */
    @DisplayName("every ATTRIBUTE_NAME_* constant names a managed attribute, and vice versa")
    @Test
    protected void _SameValue_ATTRIBUTE_NAME_() {
        final var managedType = entityManagerFactory.getMetamodel().managedType(targetClass);
        final var constants = constantsPrefixed(ATTRIBUTE_NAME_CONSTANT_PREFIX);
        for (final var attribute : managedType.getAttributes()) {
            final var attributeName = attribute.getName();
            final var constantName = ATTRIBUTE_NAME_CONSTANT_PREFIX + toUpperSnakeCase(attributeName);
            if (!constants.containsKey(constantName)) {
                continue; // existence is not this direction's concern
            }
            assertThat(constants.get(constantName))
                    .as("%s.%s, for the managed attribute %s", targetClass.getSimpleName(), constantName,
                        attributeName)
                    .isEqualTo(attributeName);
        }
        constants.forEach((constantName, value) -> assertThatCode(() -> managedType.getAttribute(value))
                .as("%s.%s = \"%s\" names a managed attribute of %s", targetClass.getSimpleName(), constantName, value,
                    managedType.getJavaType().getSimpleName())
                .doesNotThrowAnyException());
    }

    /**
     * Verifies that every {@code ATTRIBUTE_PATH_*} constant of {@link #targetClass} holds a dotted path which resolves
     * through the metamodel, segment by segment, to an attribute whose name is the path's last segment.
     *
     * @see ___Persistence_TestUtils#resolveAttributePath(ManagedType, String)
     */
    @DisplayName("every ATTRIBUTE_PATH_* constant resolves through the metamodel")
    @Test
    protected void _Resolvable_ATTRIBUTE_PATH_() {
        final var managedType = entityManagerFactory.getMetamodel().managedType(targetClass);
        constantsPrefixed(ATTRIBUTE_PATH_CONSTANT_PREFIX).forEach((constantName, path) -> {
            assertThat(path)
                    .as("%s.%s is a dotted path", targetClass.getSimpleName(), constantName)
                    .contains(".");
            final var resolved = ___Persistence_TestUtils.resolveAttributePath(managedType, path);
            assertThat(resolved.getName())
                    .as("%s.%s = \"%s\" resolves to its last segment", targetClass.getSimpleName(), constantName,
                        path)
                    .isEqualTo(path.substring(path.lastIndexOf('.') + 1));
        });
    }

    /**
     * Verifies that a randomized instance of {@link #targetClass} can be persisted and flushed.
     *
     * @implNote The work happens in a transaction which is always rolled back, so the row never survives the
     *         test.
     * @implSpec A subclass whose entity is not to be persisted overrides this method, without
     *         {@link Test @Test}.
     */
    @Test
    void _persist_RandomizedInstance() {
        applyNewPersistedEntityInstanceAndRollback((em, v) -> {
            em.flush();
            log.debug("persisted: {}", v);
            return v;
        });
    }

    /**
     * Verifies that a newly persisted instance of {@link #targetClass} equals, and hashes the same as, the reference to
     * its row, taken once the instance is detached.
     *
     * @implNote The context is cleared so that {@link EntityManager#getReference(Object) getReference} does not
     *         answer the managed instance itself; what comes back is a lazy proxy, under a provider which makes one.
     *         The reference is taken from the detached instance, so no identifier has to be assembled, which keeps an
     *         {@link jakarta.persistence.IdClass IdClass} in reach.
     */
    @DisplayName("persisted.equals(getReference(persisted))")
    @Test
    void _persist_EqualsReference() {
        applyNewPersistedEntityInstanceAndRollback((em, v) -> {
            em.flush();
            em.clear();
            final var reference = em.getReference(v);
            assertThat(v.equals(reference))
                    .as("persisted.equals(reference); reference: %s", reference.getClass())
                    .isTrue();
            assertThat(v.hashCode())
                    .as("persisted.hashCode() == reference.hashCode(); reference: %s", reference.getClass())
                    .isEqualTo(reference.hashCode());
            return reference;
        });
    }

    /**
     * Verifies that the reference to the row of a newly persisted instance of {@link #targetClass}, taken once the
     * instance is detached, equals the instance.
     *
     * @implNote This is the other direction of {@link #_persist_EqualsReference()}; here {@code equals} runs on
     *         the reference itself.
     */
    @DisplayName("getReference(persisted).equals(persisted)")
    @Test
    void _persist_ReferenceEquals() {
        applyNewPersistedEntityInstanceAndRollback((em, v) -> {
            em.flush();
            em.clear();
            final var reference = em.getReference(v);
            assertThat(reference.equals(v))
                    .as("reference.equals(persisted); reference: %s", reference.getClass())
                    .isTrue();
            return reference;
        });
    }

    /**
     * Verifies that a newly persisted instance of {@link #targetClass}, put in a {@link HashSet} before it is flushed,
     * is still found there once it is flushed, and that the instance {@code merge} answers for it, detached, and the
     * reference to its row, are found there too.
     *
     * @implNote The flush is where an identifier from an {@code IDENTITY} column is assigned under EclipseLink,
     *         so an {@code equals}/{@code hashCode} which followed the identifier would lose the instance there;
     *         Hibernate ORM assigns it as {@code persist} is called, earlier than the instance reaches this test.
     *         {@code merge} stands in for {@code find}, which would need the identifier, and
     *         {@link jakarta.persistence.PersistenceUnitUtil#getIdentifier} does not assemble an id class under
     *         EclipseLink.
     */
    @DisplayName("a HashSet still contains the persisted instance across flush, merge, and getReference")
    @Test
    void _persist_HashSetMembership() {
        applyNewPersistedEntityInstanceAndRollback((em, v) -> {
            final var set = new HashSet<T>();
            set.add(v);
            em.flush();
            assertThat(set)
                    .as("the set, after the flush, of the persisted %s", targetClass.getSimpleName())
                    .contains(v);
            em.clear();
            final var merged = em.merge(v);
            assertThat(set)
                    .as("the set, of the %s merged into a cleared context", targetClass.getSimpleName())
                    .contains(merged);
            em.clear();
            final var reference = em.getReference(v);
            assertThat(set)
                    .as("the set, of the reference to the %s; reference: %s", targetClass.getSimpleName(),
                        reference.getClass())
                    .contains(reference);
            return set;
        });
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Persists a new randomized instance of {@link #targetClass}, and applies it, along with the entity manager, to the
     * specified function, within a transaction which is always rolled back.
     *
     * @param function the function to apply the entity manager and the persisted instance to.
     * @param <R>      the type of the result.
     * @return the result of the {@code function}.
     * @throws IllegalArgumentException when {@link #targetClass} has no usable randomizer or no usable persister; see
     *                                  {@link EntityPersisterUtils#newPersistedInstanceOf(EntityManager, Class)}.
     * @implNote The instance is persisted inside the same transaction the {@code function} runs in, so it is
     *         still managed when the function sees it, and it is gone when the transaction rolls back.
     */
    <R extends @Nullable Object> R applyNewPersistedEntityInstanceAndRollback(
            final BiFunction<? super EntityManager, ? super T, ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return applyEntityManagerInTransactionAndRollback(em -> {
            final var persisted = EntityPersisterUtils.newPersistedInstanceOf(em, targetClass);
            return function.apply(em, persisted);
        });
    }

    /**
     * Persists a new randomized instance of {@link #targetClass}, and applies it, along with the entity manager, to the
     * specified function, within a transaction which is committed when the function returns, and rolled back when it
     * throws.
     *
     * @param function the function to apply the entity manager and the persisted instance to.
     * @param <R>      the type of the result.
     * @return the result of the {@code function}.
     * @throws IllegalArgumentException when {@link #targetClass} has no usable randomizer or no usable persister; see
     *                                  {@link EntityPersisterUtils#newPersistedInstanceOf(EntityManager, Class)}.
     * @implNote The persisted row outlives the call, on a database which is thrown away with the container; a
     *         test which only needs the instance for the length of the {@code function} wants the rolled-back one.
     */
    <R extends @Nullable Object> R applyNewPersistedEntityInstanceAndCommit(
            final BiFunction<? super EntityManager, ? super T, ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return applyEntityManagerInTransactionAndCommit(em -> {
            final var persisted = EntityPersisterUtils.newPersistedInstanceOf(em, targetClass);
            return function.apply(em, persisted);
        });
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Applies the entity manager, of this instance, to the specified function, within a transaction which is always
     * rolled back.
     *
     * @param function the function to apply the entity manager to.
     * @param <R>      the type of the result.
     * @return the result of the {@code function}.
     * @implNote The rollback is what keeps one test from being visible to the next, on a schema which is
     *         recreated per container anyway.
     * @see ___Persistence_TestUtils#applyInTransactionAndRollback(EntityManager, Function)
     */
    <R extends @Nullable Object> R applyEntityManagerInTransactionAndRollback(
            final Function<? super EntityManager, ? extends R> function) {
        return ___Persistence_TestUtils.applyInTransactionAndRollback(entityManager, function);
    }

    /**
     * Applies the entity manager, of this instance, to the specified function, within a transaction which is committed
     * when the function returns, and rolled back when it throws.
     *
     * @param function the function to apply the entity manager to.
     * @param <R>      the type of the result.
     * @return the result of the {@code function}.
     * @implNote What this writes survives the transaction, but not the container: the schema is generated per
     *         container and the in-memory database goes with it. Still the exception -- a test whose writes nothing
     *         later has to read wants the rolled-back one.
     * @see ___Persistence_TestUtils#applyInTransactionAndCommit(EntityManager, Function)
     */
    <R extends @Nullable Object> R applyEntityManagerInTransactionAndCommit(
            final Function<? super EntityManager, ? extends R> function) {
        return ___Persistence_TestUtils.applyInTransactionAndCommit(entityManager, function);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * A new entity manager, for each test instance; {@link jakarta.enterprise.context.Dependent Dependent}.
     */
    @__TestPU
    @Inject
    @Accessors(fluent = true)
    @Getter(AccessLevel.PROTECTED)
    private EntityManager entityManager;

    /**
     * The one factory of the container; {@link jakarta.enterprise.context.ApplicationScoped ApplicationScoped}.
     */
    @__TestPU
    @Inject
    @Accessors(fluent = true)
    @Getter(AccessLevel.PROTECTED)
    private EntityManagerFactory entityManagerFactory;

    /**
     * For obtaining entity managers by hand, rather than at an injection point.
     */
    @__TestPU
    @Inject
    @Accessors(fluent = true)
    @Getter(AccessLevel.PROTECTED)
    private Instance<EntityManager> entityManagers;
}
