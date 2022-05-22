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

package org.eclipse.osee.synchronization.rest;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.util.DoubleHashMap;
import org.eclipse.osee.framework.jdk.core.util.DoubleMap;
import org.eclipse.osee.synchronization.rest.forest.GroveThing;

/**
 * Map used to collect unique native OSEE {@link ArtifactTypeToken}s and associated them with Specification Type, Spec
 * Object Type, and Spec Relation Type {@link GroveThing}s.
 *
 * @author Loren K. Ashley
 */

class CommonObjectTypeContainerMap {

   /**
    * Map used to collect unique native OSEE {@link ArtifactTypeToken}s. The {@link ArtifactTypeToken}s are stored as
    * native things in the {@link GroveThing} map values.
    */

   private final DoubleMap<Long, IdentifierType, GroveThing> map;

   /**
    * Creates a new empty {@link CommonObjectTypeContainerMap}.
    */

   CommonObjectTypeContainerMap() {
      this.map = new DoubleHashMap<>(256, 0.75f, IdentifierType.size(), 1.0f);
   }

   /**
    * Finds the {@link GroveThing} of the type indicated by the {@link #identifierType} that is associated with the OSEE
    * artifact type specified by {@link #artifactTypeToken}.
    *
    * @param identifierType the {@link IdentifierType} indicating whether to get a {@link SpecificationTypeGroveThing},
    * {@link SpecObjectTypeGroveThing}, or {@link SpecRelationTypeGroveThing} thing.
    * @param artifactTypeToken the OSEE artifact type.
    * @return if found, an {@link Optional} with the associated {@link GroveThing}; otherwise, an empty
    * {@link Optional}.
    */

   Optional<GroveThing> get(ArtifactTypeToken artifactTypeToken, IdentifierType identifierType) {

      //@formatter:off
      assert
           Objects.nonNull( artifactTypeToken )
         : "CommonObjectTypeContainerMap::get, artifactTypeToken is null.";

      assert
           Objects.nonNull( identifierType )
         : "CommonObjectTypeContainerMap::get, identifierType is null.";

      assert
           identifierType.isInGroup( IdentifierTypeGroup.TYPE )
         : UnexpectedIdentifierTypeException.buildMessage( identifierType, IdentifierTypeGroup.TYPE );
      //@formatter:on

      return this.map.get(artifactTypeToken.getId(), identifierType);
   }

   /**
    * Adds an association for a Specification, Spec Object, or Spec Relation {@link GroveThing} with the OSEE artifact
    * type to the map. If a map association already exists, the method just returns.
    *
    * @param artifactTypeToken the OSEE artifact type.
    * @param commonObjectTypeGroveThing the {@link SpecificationTypeGroveThing}, {@link SpecObjectTypeGroveThing}, or
    * {@link SpecRelationTypeGroveThing} to be associated with the OSEE artifact type.
    */

   void put(GroveThing groveThing) {

      //@formatter:off
      assert
           Objects.nonNull( groveThing )
         : "CommonObjectTypeContainerMap::put, groveThing is null.";

      assert
           groveThing.getType().isInGroup( IdentifierTypeGroup.TYPE )
         : UnexpectedGroveThingTypeException.buildMessage( groveThing, IdentifierTypeGroup.TYPE );

      assert
           Objects.nonNull( groveThing.getNativeThing() )
         : "CommonObjectTypeContainerMap::put, groveThing has a null native thing.";

      assert
           ( groveThing.getNativeThing() instanceof ArtifactTypeToken )
         : "CommonObjectTypeContainerMap::put, the groveThing's native thing is not an ArtifactTypeToken.";

      assert
           !this.map.containsKey( ((Id) groveThing.getNativeThing()).getId(), groveThing.getType() )
         : "CommonObjectTypeContainerMap::put, map already has an entry for the artifact type token and grove thing type.";
      //@formatter:on

      this.map.put(((Id) groveThing.getNativeThing()).getId(), groveThing.getType(), groveThing);

      //@formatter:off
      assert
           ( this.map.get( ((Id) groveThing.getNativeThing()).getId() ).orElseThrow().values().stream().map( GroveThing::getNativeThing ).distinct().count() == 1 )
         : "CommonObjectTypeContainerMap::put, not all GroveThings under the primay key have the same native thing ArtifactTypeToken";
      //@formatter:on
   }

   /**
    * Returns an unordered {@link Stream} of the {@link GroveThing} objects in the map.
    *
    * @return an unordered {@link Stream} of the {@link GroveThing} objects in the map.
    */

   Stream<GroveThing> stream() {
      return this.map.values().stream();
   }
}

/* EOF */
