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

package org.eclipse.osee.define.rest.synchronization.reqifsynchronizationartifactbuilder;

import java.util.Map;
import java.util.Objects;
import org.eclipse.osee.define.rest.synchronization.IdentifierType;
import org.eclipse.osee.define.rest.synchronization.IdentifierTypeGroup;
import org.eclipse.osee.define.rest.synchronization.UnexpectedGroveThingTypeException;
import org.eclipse.osee.define.rest.synchronization.forest.GroveThing;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.jdk.core.util.EnumSupplierMap;
import org.eclipse.rmf.reqif10.ReqIF10Factory;
import org.eclipse.rmf.reqif10.SpecElementWithAttributes;

/**
 * Class contains the converter method to create the ReqIf {@link SpecElementWithAttributes} things from the native OSEE
 * {@link ArtifactReadable} things.
 *
 * @author Loren K. Ashley
 */

public class SpecElementWithAttributesConverter {

   /**
    * Map of ReqIf factories to create the foreign thing associated with each allowed {@link IdentifierType}.
    */

   //@formatter:off
   private static EnumSupplierMap<IdentifierType,SpecElementWithAttributes> specElementWithAttributesSupplierMap =
      EnumSupplierMap.ofEntries
         (
            IdentifierType.class,
            Map.entry( IdentifierType.SPECIFICATION,       ReqIF10Factory.eINSTANCE::createSpecification ),
            Map.entry( IdentifierType.SPECTER_SPEC_OBJECT, ReqIF10Factory.eINSTANCE::createSpecObject    ),
            Map.entry( IdentifierType.SPEC_OBJECT,         ReqIF10Factory.eINSTANCE::createSpecObject    ),
            Map.entry( IdentifierType.SPEC_RELATION,       ReqIF10Factory.eINSTANCE::createSpecRelation  )
         );
   //@formatter:on

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
    * Private constructor to prevent instantiation of the class.
    */

   private SpecElementWithAttributesConverter() {
   }

   /**
    * Converter method for {@link SpecificationGroveThing}, {@link SpecObjecGroveThing}, and
    * {@link SpecRelationGroveThing} things. This method creates the foreign ReqIF {@link SpecElementWithAttribute} from
    * the native {@link ArtifactTypeToken}.
    *
    * @param groveThing the {@link GroveThing} thing to be converted.
    */

   static void convert(GroveThing groveThing) {

      //@formatter:off
      assert
            Objects.nonNull( groveThing )
         && groveThing.isInGroup( IdentifierTypeGroup.OBJECT )
         : UnexpectedGroveThingTypeException.buildMessage( groveThing, IdentifierTypeGroup.OBJECT );
      //@formatter:on

      var nativeArtifactReadable = (ArtifactReadable) groveThing.getNativeThing();

      var reqifSpecElementWithAttributes = SpecElementWithAttributesConverter.specElementWithAttributesSupplierMap.get(
         groveThing.getIdentifier().getType());

      //@formatter:off
      var description =
         new StringBuilder( 512 )
                .append( "OSEE " )
                .append( nativeArtifactReadable.getName() )
                .append( SpecElementWithAttributesConverter.descriptionMap.get( groveThing.getIdentifier().getType() ) )
                .toString();
      //@formatter:on

      reqifSpecElementWithAttributes.setDesc(description);
      reqifSpecElementWithAttributes.setIdentifier(groveThing.getIdentifier().toString());
      reqifSpecElementWithAttributes.setLastChange(ReqIFSynchronizationArtifactBuilder.lastChangeEpoch);
      reqifSpecElementWithAttributes.setLongName(nativeArtifactReadable.getName());

      groveThing.setForeignThing(reqifSpecElementWithAttributes);

   }
}

/* EOF */
