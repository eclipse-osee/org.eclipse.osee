/*********************************************************************
 * Copyright (c) 2022 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.define.operations.synchronization.forest;

import java.util.Optional;
import java.util.stream.Stream;
import org.eclipse.osee.define.operations.synchronization.forest.morphology.DuplicateGroveEntryException;
import org.eclipse.osee.define.operations.synchronization.identifier.Identifier;
import org.eclipse.osee.define.operations.synchronization.identifier.IdentifierType;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * Implements a map like data store of objects implementing the {@link GroveThing} interface.
 * <p>
 * <h2>Stores</h2>
 * <p>
 * All {@link Grove} implementations contain a primary map used to associate the primary key sets with
 * {@link GroveThing} implementations. A {@link Grove} may also contain a native map used to associate the native key
 * sets with the {@link GroveThing} implementations. {@link GroveThing} implementations must be able to provide a set of
 * primary keys. When a {@link Grove} contains a native map the {@link GroveThing} implementations must also be able to
 * provide native key sets.
 * <p>
 * <h2>Key Sets</h2>
 * <p>
 * Each key set must contain enough keys to be able to uniquely identify the {@link GroveThing}. However, key sets may
 * contain more keys than what is sufficient to uniquely identify the the {@link GroveThing}. For example, a key set may
 * contain additional keys to organize the {@link GroveThing} implementations within the store. The lowest rank key in a
 * primary key set must uniquely identify the {@link GroveThing}.
 * <p>
 * <h2>Rank</h2>
 * <p>
 * The number of keys in the primary key set is the primary rank and the number of keys in the native key set is the
 * native rank. The primary rank and native rank of the {@link Grove} implementation and the {@link GroveThing}
 * implementations stored in the {@link Grove} must match.
 * <p>
 *
 * @implNote The data store implementations generally assume {@link Grove} and {@link GroveThing} implementations are
 * compatible. Bad results or exceptions may occur but the code does not check for incompatibilities unless assertions
 * are enabled.
 * <p>
 * @author Loren K. Ashley
 * <p>
 */

public interface Grove extends ToMessage {

   /**
    * Adds an association of a {@link GroveThing} and its primary keys to the {@link Grove} and an association of the
    * {@link GroveThing} and its native keys to the {@link Grove} when the {@link Grove} implements a native store. The
    * key sets are obtained using the {@link GroveThing} interface.
    *
    * @param groveThing the {@link GroveThing} object to be added to the {@link Grove}.
    * @return the {@link GroveThing} added to the grove.
    * @throws NullPointerException when the provided {@link GroveThing} is <code>null</code>.
    * @throws DuplicateGroveEntryException when the grove already contains an association for the primary or native keys
    * extracted from the {@link GroveThing}.
    */

   GroveThing add(GroveThing groveThing);

   /**
    * Predicate to determine if the {@link Grove} contains a {@link GroveThing} associated with the specified primary
    * key set.
    *
    * @param primaryKeys an array of the primary keys. The number of keys specified should match the rank of the grove.
    * @return <code>true</code>, when the {@link Grove} contains a {@link GroveThing} associated with the provided keys;
    * otherwise, <code>false</code>.
    * @throws IllegalStateException when the {@link Grove} was created for {@link GroveThing} implementations that do
    * not provide native keys.
    */

   boolean containsByPrimaryKeys(Identifier... primaryKeys);

   /**
    * Predicate to determine if the {@link Grove} contains a {@link GroveThing} associated with the specified native key
    * set.
    *
    * @param nativeKeys an array of the native keys. The number of keys specified should match the rank of the grove.
    * @return <code>true</code>, when the {@link Grove} contains a {@link GroveThing} associated with the provided keys;
    * otherwise, <code>false</code>.
    */

   boolean containsByNativeKeys(Object... nativeKeys);

   /**
    * Get the {@link GroveThing} object associated with the specified primary key set. Although the highest index key
    * may uniquely identify the {@link GroveThing}, this method may still return an empty {@link Optional} when the
    * {@link GroveThing} is not also associated with or organizationally under the lower index keys.
    *
    * @param primaryKeys an array of the primary keys. The number of keys specified should match the rank of the grove.
    * @return when the map contains a {@link GroveThing} associated with the provided primary key set, an
    * {@link Optional} containing the associated {@link GroveThing}; otherwise, and empty {@link Optional}.
    */

   Optional<GroveThing> getByPrimaryKeys(Identifier... primaryKeys);

   /**
    * Get the {@link GroveThing} associated with the specified native key set. Although the highest index key may
    * uniquely identify the {@link GroveThing}, this method may still return an empty {@link Optional} when the
    * {@link GroveThing} is not also associated with or organizationally under the lower index keys.
    *
    * @param nativeKeys an array of the native keys. The number of keys specified should match the rank of the grove.
    * @return when the map contains a {@link GroveThing} associated with the provided native key set, an
    * {@link Optional} containing the associated {@link GroveThing}; otherwise, and empty {@link Optional}.
    * @throws IllegalStateException when the {@link Grove} was created for {@link GroveThing} implementations that do
    * not provide native keys.
    */

   Optional<GroveThing> getByNativeKeys(Object... nativeKeys);

   /**
    * Get the {@link GroveThing} associated with the specified native key set. Although the highest index key may
    * uniquely identify the {@link GroveThing}, this method may still throw an exception when the {@link GroveThing} is
    * not also associated with or organizationally under the lower index keys.
    *
    * @param nativeKeys an array of the native keys. The number of keys specified should match the rank of the grove.
    * @return the associated {@link GroveThing}
    * @throws IllegalStateException when the {@link Grove} was created for {@link GroveThing} implementations that do
    * not provide native keys.
    * @throws GroveThingNotFoundWithNativeKeysException when the {@link Grove} does not contain a {@link GroveThing}
    * associated with the provided <code>nativeKeys</code>.
    */

   GroveThing getByNativeKeysOrElseThrow(Object... nativeKeys);

   /**
    * Get the {@link GroveThing} associated with the unique primary key.
    *
    * @param uniquePrimaryKey the key.
    * @return when an association with the provided <code>uniquePrimayKey</code> exists, an {@link Optional} containing
    * the associated {@link GroveThing}; otherwise, an empty {@link Optional}.
    */

   Optional<GroveThing> getByUniquePrimaryKey(Object uniquePrimaryKey);

   /**
    * Gets the {@link IdentifierType} associated with the {@link Grove}. In a Synchronization Artifact DOM there is a
    * {@link Grove} for each of the Synchronization Artifact "things". Some of the {@link Grove}s allow the storage of
    * more than one type of {@link GroveThing} or of a {@link GroveThing} superclass. This method cannot be used to
    * determine the {@link IdentifierType} of the {@link Identifier} keys in a primary key set.
    *
    * @return the {@link IdentifierType} associated with the {@link Grove}.
    */

   IdentifierType getType();

   /**
    * Returns the rank of the grove's storage by native keys. The rank is the number of keys in a full native key set.
    *
    * @return the rank of the grove's native storage.
    */

   int nativeRank();

   /**
    * Returns the rank of the grove's storage by primary keys. The rank is the number of keys in a full primary key set.
    *
    * @return the rank of the groves primary storage.
    */

   int rank();

   /**
    * Returns the number of {@link GroveThing} objects in the {@link Grove}.
    *
    * @return the number of entries.
    */

   int size();

   /**
    * Returns an unordered {@link Stream} of the {@link GroveThing} implementations stored in the {@link Grove} under
    * the provided primary keys. The provided primary key set can be a <code>null</code> array, empty array, or an array
    * of up to the {@link Grove}'s rank number of keys. When less than the full set (rank number of keys) of primary
    * keys is provided the keys are taken to be the keys of higher order rank. The keys should be ordered in the array
    * from the highest order rank key in the lowest array index to the lowest order rank key in the highest array index.
    * The content of the {@link Stream} is determined by both the keys in the provided primary key set and the number of
    * provided keys. The actual {@link Stream} content is dependent upon the implementation of the {@link Grove},
    * however, the general notion is as follows:
    * <dl>
    * <dt>Key Count 0:</dt>
    * <dd>An unordered {@link Stream} of all the {@link GroveThing} implementations in the {@link Grove} is
    * returned.</dd>
    * <dt>Key Count 1 to Grove Primary Rank - 1:</dt>
    * <dd>An unordered {@link Stream} of all the {@link GroveThing} implementations that are associated with the high
    * order keys in the provided partial primary key set.</dd>
    * <dt>Key Count Equals Grove Primary Rank:</dt>
    * <dd>A stream consisting of just the single {@link GroveThing} implementation associated with the provided primary
    * key set.</dd>
    * </dl>
    *
    * @param primaryKeys an array of the primary keys. The number of keys specified should be less than or equal to the
    * rank of the grove.
    * @return a unordered {@link Stream} of the {@link GroveThing} implementations stored under the provided primary
    * keys.
    */

   Stream<GroveThing> stream(Identifier... primaryKeys);

   /**
    * Returns an unordered {@link Stream} of the unique primary {@link Identifier} for each {@link GroveThing} stored in
    * the {@link Grove} at all levels under the provided primary keys. The provided primary key set can be a
    * <code>null</code> array, empty array, or an array of up to the {@link Grove}'s rank number of keys. When less than
    * the full set (rank number of keys) of primary keys is provided the keys are taken to be the keys of higher order
    * rank. The keys should be ordered in the array from the highest order rank key in the lowest array index to the
    * lowest order rank key in the highest array index. The content of the {@link Stream} is determined by both the keys
    * in the provided primary key set and the number of provided keys. The actual {@link Stream} content is dependent
    * upon the implementation of the {@link Grove}, however, the general notion is as follows:
    * <dl>
    * <dt>Key Count 0:</dt>
    * <dd>An unordered {@link Stream} of all the {@link GroveThing} {@link Identifier}s in the {@link Grove} is
    * returned.</dd>
    * <dt>Key Count 1 to Grove Primary Rank - 1:</dt>
    * <dd>An unordered {@link Stream} of all the {@link GroveThing} {@link Identifier}s for {@link GroveThing}
    * implementations in the {@link Grove} that are associated with the high order keys in the provided partial primary
    * key set.</dd>
    * <dt>Key Count Equals Grove Primary Rank:</dt>
    * <dd>A stream consisting of just the single primary {@link Identifier} for the {@link GroveThing} implementation in
    * the {@link Grove} associated with the provided primary key set.</dd>
    * </dl>
    *
    * @param primaryKeys an array of the primary keys. The number of keys specified should be less than or equal to the
    * @return a unordered {@link Stream} of the primary {@link Identifier} for the {@link GroveThing} implementations
    * stored in the {@link Grove} under the provided primary keys.
    */

   Stream<Identifier> streamIdentifiersDeep(Identifier... primaryKeys);

   /**
    * Returns an unordered {@link Stream} of the unique primary {@link Identifier} for each {@link GroveThing} stored in
    * the {@link Grove} for the level under the provided primary keys. The provided primary key set can be a
    * <code>null</code> array, empty array, or an array of up to the {@link Grove}'s rank number of keys. When less than
    * the full set (rank number of keys) of primary keys is provided the keys are taken to be the keys of higher order
    * rank. The keys should be ordered in the array from the highest order rank key in the lowest array index to the
    * lowest order rank key in the highest array index. The content of the {@link Stream} is determined by both the keys
    * in the provided primary key set and the number of provided keys. The actual {@link Stream} content is dependent
    * upon the implementation of the {@link Grove}, however, the general notion is as follows:
    * <dl>
    * <dt>Key Count 0:</dt>
    * <dd>An unordered {@link Stream} of the {@link GroveThing} {@link Identifier}s at the top level of the
    * {@link Grove} is returned.</dd>
    * <dt>Key Count 1 to Grove Primary Rank - 1:</dt>
    * <dd>An unordered {@link Stream} of the {@link GroveThing} {@link Identifier}s for the {@link GroveThing}
    * implementations in the {@link Grove} that are children of the {@link GroveThing} associated with the high order
    * keys in the provided partial primary key set. The {@link Identifier}s of grand children are not included in the
    * {@link Stream}.</dd>
    * <dt>Key Count Equals Grove Primary Rank:</dt>
    * <dd>A stream consisting of just the single primary {@link Identifier} for the {@link GroveThing} implementation in
    * the {@link Grove} associated with the provided primary key set.</dd>
    * </dl>
    *
    * @param primaryKeys an array of the primary keys. The number of keys specified should be less than or equal to the
    * @return a unordered {@link Stream} of the primary {@link Identifier} for the {@link GroveThing} implementations
    * stored in the {@link Grove} under the provided primary keys.
    */

   Stream<Identifier> streamIdentifiersShallow(Identifier... primaryKeys);

   /**
    * Returns an unordered {@link Stream} of the primary key sets for each {@link GroveThing} stored in the
    * {@link Grove} under the provided primary key set. The primary key sets in the {@link Stream} will be
    * {@link Identifier} arrays with all the primary keys for each of the {@link GroveThing}s in the {@link Stream}. The
    * provided primary key set can be a <code>null</code> array, empty array, or an array of up to the {@link Grove}'s
    * rank number of keys. When less than the full set (rank number of keys) of primary keys is provided the keys are
    * taken to be the keys of higher order rank. The keys should be ordered in the array from the highest order rank key
    * in the lowest array index to the lowest order rank key in the highest array index. The content of the
    * {@link Stream} is determined by both the keys in the provided primary key set and the number of provided keys. The
    * actual {@link Stream} content is dependent upon the implementation of the {@link Grove}, however, the general
    * notion is as follows:
    * <dl>
    * <dt>Key Count 0:</dt>
    * <dd>An unordered {@link Stream} of all the primary key sets for each {@link GroveThing} in the {@link Grove} is
    * returned.</dd>
    * <dt>Key Count 1 to Grove Primary Rank - 1:</dt>
    * <dd>An unordered {@link Stream} of all the primary key sets for each {@link GroveThing} implementation in the
    * {@link Grove} that are associated with the high order keys in the provided partial primary key set.</dd>
    * <dt>Key Count Equals Grove Primary Rank:</dt>
    * <dd>A stream consisting of just the full primary key set of the single {@link GroveThing} implementation in the
    * {@link Grove} associated with the provided primary key set.</dd>
    * </dl>
    *
    * @param primaryKeys an array of the primary keys. The number of keys specified should be less than or equal to the
    * @return a unordered {@link Stream} of the primary {@link Identifier} for the {@link GroveThing} implementations
    * stored in the {@link Grove} under the provided primary keys.
    */

   Stream<Identifier[]> streamKeySets(Identifier... groveThingKeys);

}

/* EOF */
