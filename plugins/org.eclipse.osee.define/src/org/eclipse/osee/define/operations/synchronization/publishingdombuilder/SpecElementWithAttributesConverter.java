/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.define.operations.synchronization.publishingdombuilder;

import java.util.Map;
import java.util.Objects;
import org.eclipse.osee.define.operations.synchronization.UnexpectedGroveThingTypeException;
import org.eclipse.osee.define.operations.synchronization.forest.GroveThing;
import org.eclipse.osee.define.operations.synchronization.identifier.Identifier;
import org.eclipse.osee.define.operations.synchronization.identifier.IdentifierType;
import org.eclipse.osee.define.operations.synchronization.identifier.IdentifierTypeGroup;
import org.eclipse.osee.define.operations.synchronization.publishingdom.DocumentMap;
import org.eclipse.osee.define.operations.synchronization.publishingdom.DocumentObject;
import org.eclipse.osee.define.operations.synchronization.publishingdom.Node;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.jdk.core.util.EnumFunctionMap;

/**
 * Class contains the converter method to create the Publishing DOM {@link Document} and {@link DocumentObject} things
 * from the Native OSEE {@link ArtifactReadable} things.
 *
 * @author Loren K. Ashley
 */

public class SpecElementWithAttributesConverter {

   /**
    * Map of descriptions to used for each allowed {@link IdentifierType}.
    */

   //@formatter:off
   private static Map<IdentifierType,String> descriptionMap =
      Map.ofEntries
         (
            Map.entry( IdentifierType.SPECIFICATION,       " Specification"       ),
            Map.entry( IdentifierType.SPECTER_SPEC_OBJECT, " Specter Spec Object" ),
            Map.entry( IdentifierType.SPEC_OBJECT,         " Spec Object"         ),
            Map.entry( IdentifierType.SPEC_RELATION,       " Spec Relation"       )
         );
   //@formatter:on

   /**
    * Save the Publishing DOM {@link DocumentMap} which the factory object for creating {@link Document} and
    * {@link DocumentObject} things.
    */

   private final DocumentMap documentMap;

   /**
    * Map of {@link Function} implementations by {@link IdentifierType} used to create the Publishing DOM
    * {@link Document} and {@link DocumentObject} things.
    */

   private final EnumFunctionMap<IdentifierType, Object[], Node> nodeFactoryMap;

   /**
    * Creates a new {@link SpecElementWithAttributesConverter} and saves the Publishing DOM.
    *
    * @param documentMap the Publishing DOM.
    */

   public SpecElementWithAttributesConverter(DocumentMap documentMap) {

      this.documentMap = documentMap;
      this.nodeFactoryMap = new EnumFunctionMap<>(IdentifierType.class);

      //@formatter:off
      this.nodeFactoryMap.put( IdentifierType.SPECIFICATION,       ( args ) -> this.documentMap.createDocument( (Identifier) args[0], (String) args[1], (String) args[2] )       );
      this.nodeFactoryMap.put( IdentifierType.SPECTER_SPEC_OBJECT, ( args ) -> this.documentMap.createDocumentObject( (Identifier) args[0], (String) args[1], (String) args[2] ) );
      this.nodeFactoryMap.put( IdentifierType.SPEC_OBJECT,         ( args ) -> this.documentMap.createDocumentObject( (Identifier) args[0], (String) args[1], (String) args[2] ) );
      this.nodeFactoryMap.put( IdentifierType.SPEC_RELATION,       ( args ) -> this.documentMap.createDocumentObject( (Identifier) args[0], (String) args[1], (String) args[2] ) );
      //@formatter:on

   }

   /**
    * Converter method for {@link SpecificationGroveThing}, {@link SpecterSpecObjectGroveThing},
    * {@link SpecObjecGroveThing}, and {@link SpecRelationGroveThing} things. This method creates the foreign Publishing
    * DOM {@link Document} and {@link DOcumentObject} things from the Native OSEE {@link ArtifactReadable}.
    *
    * @param groveThing the {@link GroveThing} thing to be converted.
    */

   void convert(GroveThing groveThing) {

      //@formatter:off
      assert
            Objects.nonNull( groveThing )
         && groveThing.isInGroup( IdentifierTypeGroup.OBJECT )
         : UnexpectedGroveThingTypeException.buildMessage( groveThing, IdentifierTypeGroup.OBJECT );

      var nativeArtifactReadable = (ArtifactReadable) groveThing.getNativeThing();

      var name       = nativeArtifactReadable.getName();
      var type       = SpecElementWithAttributesConverter.descriptionMap.get( groveThing.getIdentifier().getType() );
      var identifier = groveThing.getIdentifier();
      var node       = this.nodeFactoryMap.apply( groveThing.getIdentifier().getType(), new Object[] { identifier, name, type } );
      //@formatter:on

      groveThing.setForeignThing(node);

   }
}

/* EOF */
