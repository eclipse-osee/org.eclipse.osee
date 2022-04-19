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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.eclipse.osee.framework.jdk.core.util.EnumFunctionMap;
import org.eclipse.osee.synchronization.rest.IdentifierType;
import org.eclipse.osee.synchronization.rest.forest.morphology.Grove;
import org.eclipse.osee.synchronization.rest.forest.morphology.GroveThing;

/**
 * Class encapsulates the data structures for the Synchronization Artifact and provides a factory method for creating
 * Synchronization Artifact {@link GroveThing}s.
 *
 * @author Loren K. Ashley
 */

public class Forest {

   /**
    * A map of factory functions to create the {@link GroveThing} objects associated with each {@link IdentifierType}.
    */

   //@formatter:off
   private static final EnumFunctionMap<IdentifierType, GroveThing, GroveThing> groveThingFactoryMap =
      EnumFunctionMap.ofEntries
         (
           IdentifierType.class,
           Map.entry( IdentifierType.ATTRIBUTE_DEFINITION, AttributeDefinitionGroveThing::new ),
           Map.entry( IdentifierType.ATTRIBUTE_VALUE,      AttributeValueGroveThing::new      ),
           Map.entry( IdentifierType.DATA_TYPE_DEFINITION, DataTypeDefinitionGroveThing::new  ),
           Map.entry( IdentifierType.ENUM_VALUE,           EnumValueGroveThing::new           ),
           Map.entry( IdentifierType.HEADER,               HeaderGroveThing::new              ),
           Map.entry( IdentifierType.SPEC_OBJECT,          SpecObjectGroveThing::new          ),
           Map.entry( IdentifierType.SPEC_OBJECT_TYPE,     SpecObjectTypeGroveThing::new      ),
           Map.entry( IdentifierType.SPECIFICATION,        SpecificationGroveThing::new       ),
           Map.entry( IdentifierType.SPECIFICATION_TYPE,   SpecTypeGroveThing::new            )
         );
   //@formatter:on

   /**
    * A {@link Map} of the Synchronization Artifact {@link Grove} objects by {@link IdentifierType}.
    */

   private final Map<IdentifierType, Grove> groveMap;

   /**
    * Constructor creates the data structures for a new Synchronization Artifact.
    */

   public Forest() {
      var groveMap = new LinkedHashMap<IdentifierType, Grove>() {

         /**
          * Serialization identifier
          */

         private static final long serialVersionUID = 1L;

         /**
          * Extracts the grove type from the grove and uses that as the map key to store the grove by.
          *
          * @param grove the {@link Grove} to be saved in the map.
          */

         public void put(Grove grove) {
            this.put(grove.getType(), grove);
         }
      };

      this.groveMap = groveMap;
      groveMap.put(new HeaderGrove());
      groveMap.put(new EnumValueGrove());
      groveMap.put(new DataTypeDefinitionGrove());
      groveMap.put(new AttributeDefinitionGrove());
      groveMap.put(new SpecTypeGrove());
      groveMap.put(new SpecObjectTypeGrove());
      groveMap.put(new SpecificationGrove());
      groveMap.put(new SpecObjectGrove());
      groveMap.put(new AttributeValueGrove());
   }

   /**
    * Creates a new {@link GroveThing} implementation with a unique {@link Identifier} of the class associated with the
    * {@link IdentifierType}.
    *
    * @param identifierType the type of object to create.
    * @param the hierarchical parent of the {@link GroveThing} to be created.
    * @return a new {@link GroveThing}.
    */

   @SuppressWarnings("unchecked")
   public <T extends GroveThing> T createGroveThing(IdentifierType identifierType, GroveThing parent) {
      return (T) Forest.groveThingFactoryMap.apply(identifierType, parent);
   }

   /**
    * Creates a new {@link GroveThing} implementation with a unique {@link Identifier} of the class associated with the
    * {@link IdentifierType}.
    *
    * @param identifierType the type of object to create.
    * @return a new {@link GroveThing}.
    */

   @SuppressWarnings("unchecked")
   public <T extends GroveThing> T createGroveThing(IdentifierType identifierType) {
      return (T) Forest.groveThingFactoryMap.apply(identifierType, null);
   }

   /**
    * Gets the {@link Grove} for the Synchronization Artifact {@link GroveThing}s specified by
    * <code>identifierType</code>.
    *
    * @param identifierType specifies the {@link Grove} to get.
    * @return the {@link Grove} for the specified {@link GroveThings}.
    */

   @SuppressWarnings("unchecked")
   public <T extends Grove> T getGrove(IdentifierType identifierType) {
      return (T) this.groveMap.get(identifierType);
   }

   /**
    * Returns an ordered stream of the {@link Grove} implementations in the {@link Forest}.
    *
    * @return a {@link Stream} of the {@link Grove}s in the {@link Forest}.
    */

   public Stream<Grove> stream() {
      return this.groveMap.values().stream();
   }
}

/* EOF */
