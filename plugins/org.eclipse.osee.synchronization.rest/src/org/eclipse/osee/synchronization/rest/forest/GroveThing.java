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

package org.eclipse.osee.synchronization.rest.forest;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;
import org.eclipse.osee.synchronization.rest.IdentifierType;
import org.eclipse.osee.synchronization.rest.IdentifierType.Identifier;
import org.eclipse.osee.synchronization.rest.IdentifierTypeGroup;
import org.eclipse.osee.synchronization.rest.LinkType;
import org.eclipse.osee.synchronization.rest.SynchronizationArtifactBuilder;
import org.eclipse.osee.synchronization.rest.UnexpectedGroveThingTypeException;
import org.eclipse.osee.synchronization.util.ToMessage;

/**
 * Implementations of this interface are a container used to hold native OSEE things and the corresponding foreign thing
 * derived from it by an {@link SynchronizationArtifactBuilder} implementation.
 * <p>
 * Implementations of {@link GroveThing} have a rank and native rank which should match the rank of the {@link Grove}
 * the {@link GroveThing} objects are stored in. The rank determines the number of keys returned by the methods
 * {@link #getPrimaryKeys()} and {@link #getNativeKeys()}.
 * <p>
 * {@link GroveThing} implementations maybe linked to other types of {@link GroveThing} implementations for the
 * Synchronization Artifact. The {@link GroveThing} implementation defines which {@link IdentifierType} of
 * {@link GroveThing} implementations maybe linked. For a particular {@link IdentifierType}, {@link GroveThing}
 * implementations may be linked in one of the following ways:
 * <dl>
 * <dt>Scalar</dt>
 * <dd>A link to single {@link GroveThing} of the link's {@link IdentifierType} is allowed.</dd>
 * <dt>Vector</dt>
 * <dd>A link to a vector of {@link GroveThing} implementations of the link's {@link IdentifierType} is allowed. Each
 * {@link GroveThing} linked is sequentially appended to the end of the vector.</dd>
 * <dt>Map</dt>
 * <dd>A link to a map of {@link GroveThing} implementations of the link's {@link IdentifierType} is allowed. Each
 * {@link GroveThing} linked is added to the map using the identifier of the {@link GroveThing} as the map key. When a
 * duplicate is added the prior {@link GroveThing} is replaced.</dd>
 * </dl>
 *
 * @author Loren K. Ashley
 */

public interface GroveThing extends ToMessage {

   /**
    * Gets the foreign hierarchy thing.
    *
    * @return the foreign hierarchy thing.
    */

   Object getForeignHierarchy();

   /**
    * Get the foreign thing saved in this container.
    *
    * @return the foreign thing.
    */

   Object getForeignThing();

   /**
    * Gets the unique {@link Identifier} for the {@link GroveThing}.
    */

   Identifier getIdentifier();

   /**
    * Gets a linked scalar {@link GroveThing} of the specified {@link LinkType}.
    *
    * @param linkType the {@link LinkType} of the linked {@link GroveThing} to get.
    * @return when a scalar {@link GroveThing} of the {@link IdentiferType} specified by {@link LinkType} has been
    * linked, an {@link Optional} with the linked {@link GroveThing}; otherwise, an empty {@link GroveThing}.
    */

   Optional<GroveThing> getLinkScalar(LinkType linkType);

   /**
    * Gets a linked vector of {@link GroveThing} implementations of the specified {@link LinkType}.
    *
    * @param linkType the {@link LinkType} of the linked {@link GroveThing} vector to get.
    * @return when a vector of {@link GroveThing} implementations of the {@link IdentifierType} specified by the
    * {@link LinkType} has been linked, an {@link Optional} with the linked vector of {@link GroveThing}
    * implementations; otherwise, an empty {@link Optional}.
    */

   Optional<Collection<GroveThing>> getLinkVector(LinkType linkType);

   /**
    * Gets an element from a linked vector of {@link GroveThing} implementations of the specified {@link LinkType}.
    *
    * @param linkType the {@link LinkType} of the linked {@link GroveThing} vector to get.
    * @param index the index of the element to get from the linked vector.
    * @return when a vector of {@link GroveThing} implementations of the {@link IdentifierType} specified by
    * {@link LinkType} has been linked that has an element at the specified index, an {@link Optional} with the linked
    * vector element; otherwise, an empty {@link Optional}.
    */

   Optional<GroveThing> getLinkVectorElement(LinkType linkType, int index);

   /**
    * Gets the selected parent.
    *
    * @param selector When greater than or equal to zero, specifies the rank of the desired parent. When less than or
    * equal to -1, specifies the number of parent levels above the {@link GroveThing} of the parent to get.
    * @return an {@link Optional} with the selected {@link GroveThing} parent; otherwise, an empty {@link Optional}.
    */

   Optional<GroveThing> getParent(int selector);

   /**
    * Get the {@link Identifier} objects that identify this {@link GroveThing} for the grove's organizational structure.
    *
    * @return an array of {@link Identifier}s.
    */

   Optional<Object[]> getPrimaryKeys();

   /**
    * Gets a key set derived from the native OSEE thing or things stored in this container. The key set returned by the
    * implementation must uniquely identify the {@link GroveThing}.
    *
    * @return when native keys are available, an {@link Optional} containing an array of the native keys; otherwise, an
    * empty {@link Optional}.
    */

   Optional<Object[]> getNativeKeys();

   /**
    * Get the native OSEE thing of lowest rank saved in this container.
    *
    * @return the native OSEE thing.
    */

   Object getNativeThing();

   /**
    * Gets the {@link IdentifierType} of the type of Synchronization Artifact thing represented by this
    * {@link GroveThing} implementation.
    *
    * @return the {@link IdentifierType} of the type of thing represented by this {@link GroveThing}.
    */

   IdentifierType getType();

   /**
    * Predicate to determine if the {@link GroveThing} can provide keys for the native things stored in it.
    *
    * @return <code>true</code> when the method {@link #getNativeKeys} may return a non-empty {@link Optional} with
    * native keys; otherwise, the method {@link #getNativeKeys} will return an empty {@link Optional}.
    */

   boolean hasNativeKeys();

   /**
    * Predicate to determine if the {@link IdentifierType} of this {@link GroveThing} instance's {@link Identifier} is a
    * part of the specified {@link IdentifierTypeGroup}.
    *
    * @param identifierTypeGroup the identifier type group to check for membership in.
    * @return <code>true</code> when the {@link IdentifierType} of this {@link GroveThing} instance's {@link Identifier}
    * is a member of the specified {@link IdentifierTypeGroup}; otherwise, <code>false</code>.
    */

   public boolean isInGroup(IdentifierTypeGroup identifierTypeGroup);

   /**
    * Predicate to determine if the {@link GroveThing} represents a thing of the specified {@link LinkType}. Members of
    * the enumerations {@link IdentifierType} and {@link IdentifierTypeGroup} implement the {@link LinkType} interface.
    *
    * @param linkType the {@link LinkType} to compare to.
    * @return <code>true</code> when the {@link GroveThing} is of the specified @link LinkType}; otherwise,
    * <code>false</code>.
    */

   boolean isType(LinkType linkType);

   /**
    * Predicate to determine if the {@link GroveThing} will provide native keys.
    *
    * @return <code>true</code> when the method {@link #getNativeKeys} may return a non-empty {@link Optional} with
    * native keys; otherwise, the method {@link #getNativeKeys} will always return an empty {@link Optional}.
    */

   boolean mayProvideNativeKeys();

   /**
    * The native rank is the number of native keys that will be returned by the method {@link #getNativeKeys}.
    *
    * @return the native rank.
    */

   int nativeRank();

   /**
    * The rank is the number of keys that will be returned by the method {@link #getPrimaryKeys}.
    *
    * @return the rank.
    */

   int rank();

   /**
    * Saves a reference to a foreign hierarchy thing. Used for building the foreign DOM.
    *
    * @param foreignHierarchy the foreign hierarchy thing.
    */

   void setForeignHierarchy(Object foreignHierarchy);

   /**
    * Sets the foreign thing saved in this container. Foreign things are created by the converter methods provided by a
    * {@link SynchronizationArtifactBuilder} implementation.
    *
    * @param foreignThing the foreign thing to be saved in the container.
    */

   void setForeignThing(Object foreignThing);

   /**
    * Creates a scalar link of the {@link LinkType} to the specified {@link GroveThing}.
    *
    * @param linkType the {@link LinkType} of the type of thing to be linked.
    * @param linkedGroveThing the {@link GroveThing} to be linked.
    * @throws NullPointerException when <code>linkType</code> or <code>linkedGroveThing</code> is <code>null</code>.
    * @throws UnexpectedGroveThingTypeException when the {@link GroveThing} to be linked is not of the type specified by
    * <code>linkType</code>.
    */

   void setLinkScalar(LinkType linkType, GroveThing linkedGroveThing);

   /**
    * Appends a {@link GroveThing} to a vector of linked {@link GroveThing} implementations for the specified
    * {@link LinkType}.
    *
    * @param linkType the {@link LinkType} of the type of thing to be linked.
    * @param linkedGroveThing the {@link GroveThing} to be appended to the vector of linked things.
    * @throws NullPointerException when <code>linkType</code> or <code>linkedGroveThing</code> is <code>null</code>.
    * @throws UnexpectedGroveThingTypeException when the {@link GroveThing} to be linked is not of the type specified by
    * <code>linkType</code>.
    */

   void setLinkVectorElement(LinkType linkType, GroveThing linkedGroveThing);

   /**
    * Sets the native OSEE thing or things saved in this container. The highest rank native thing is at the low array
    * index and the lowest rank native thing is at the highest array index.
    *
    * @param nativeThing the native OSEE thing to be saved.
    * @return the {@link GroveThing}.
    */

   GroveThing setNativeThings(Object... nativeThings);

   /**
    * Gets a {@link Stream} of the linked {@link GroveThing} implementations of the specified {@link IdentifierType}.
    * The returned {@link Stream} is constructed as follows for the rank of the link:
    * <dl>
    * <dt>Scalar</dt>
    * <dd>When there is a linked {@link GrovThing}, a one element {@link Stream} with the linked {@link GroveThing};
    * otherwise, an empty {@link Stream}.</dd>
    * <dt>Vector</dt>
    * <dd>When there is a linked vector of {@link GroveThing} implementations, an ordered {@link Stream} of
    * {@link GroveThing} implementations in the vector; otherwise, an empty {@link Stream}.</dd>
    * <dt>Map</dt>
    * <dd>When there is a linked map of {@link GroveThing} implementations, an unordered {@link Stream} of
    * {@link GroveThing} implementations in the map; otherwise, an empty {@link Stream}.</dd>
    * </dl>
    *
    * @param linkType the {@link IdentifierType} of the {@link GroveThing} implementations to get.
    * @return a {@link Stream} of the {@link GroveThing} implementations of the specified {@link IdentifierType} that
    * are linked.
    */

   Stream<GroveThing> streamLinks(LinkType linkType);
}

/* EOF */
