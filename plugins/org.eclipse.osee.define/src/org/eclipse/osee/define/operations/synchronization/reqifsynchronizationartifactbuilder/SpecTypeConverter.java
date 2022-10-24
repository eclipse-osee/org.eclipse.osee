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

package org.eclipse.osee.define.operations.synchronization.reqifsynchronizationartifactbuilder;

import java.util.Map;
import java.util.Objects;
import org.eclipse.osee.define.operations.synchronization.UnexpectedGroveThingTypeException;
import org.eclipse.osee.define.operations.synchronization.forest.GroveThing;
import org.eclipse.osee.define.operations.synchronization.identifier.IdentifierType;
import org.eclipse.osee.define.operations.synchronization.identifier.IdentifierTypeGroup;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.jdk.core.util.EnumSupplierMap;
import org.eclipse.rmf.reqif10.ReqIF10Factory;
import org.eclipse.rmf.reqif10.SpecType;

/**
 * Class contains the converter method to create the ReqIf {@link SpecType} things from the native OSEE
 * {@link ArtifactTypeToken} things.
 *
 * @author Loren K. Ashley
 */

public class SpecTypeConverter {

   /**
    * Map of ReqIf factories to create the foreign thing associated with each allowed {@link IdentifierType}.
    */

   //@formatter:off
   private static EnumSupplierMap<IdentifierType,SpecType> specTypeSupplierMap =
      EnumSupplierMap.ofEntries
         (
            IdentifierType.class,
            Map.entry( IdentifierType.SPECIFICATION_TYPE, ReqIF10Factory.eINSTANCE::createSpecificationType ),
            Map.entry( IdentifierType.SPEC_OBJECT_TYPE,   ReqIF10Factory.eINSTANCE::createSpecObjectType    ),
            Map.entry( IdentifierType.SPEC_RELATION_TYPE, ReqIF10Factory.eINSTANCE::createSpecRelationType  )
         );
   //@formatter:on

   /**
    * Map of descriptions to used for each allowed {@link IdentifierType}.
    */

   //@formatter:off
   private static Map<IdentifierType,String> descriptionMap =
      Map.ofEntries
         (
            Map.entry( IdentifierType.SPECIFICATION_TYPE, " Specification Type" ),
            Map.entry( IdentifierType.SPEC_OBJECT_TYPE,   " Spec Object Type"   ),
            Map.entry( IdentifierType.SPEC_RELATION_TYPE, " Spec Relation Type" )
         );
   //@formatter:on

   /**
    * Private constructor to prevent instantiation of the class.
    */

   private SpecTypeConverter() {
   }

   /**
    * Converter method for {@link SpecificationTypeGroveThing}, {@link SpecObjecTypeGroveThing}, and
    * {@link SpecRelationTypeGroveThing} things. This method creates the foreign ReqIF {@link SpecType} from the native
    * {@link ArtifactTypeToken}.
    *
    * @param groveThing the {@link GroveThing} thing to be converted.
    */

   static void convert(GroveThing groveThing) {

      //@formatter:off
      assert
            Objects.nonNull( groveThing )
         && groveThing.isInGroup( IdentifierTypeGroup.TYPE )
         : UnexpectedGroveThingTypeException.buildMessage( groveThing, IdentifierTypeGroup.TYPE );
      //@formatter:on

      var nativeArtifactTypeToken = (ArtifactTypeToken) groveThing.getNativeThing();

      var reqifSpecificationType = SpecTypeConverter.specTypeSupplierMap.get(groveThing.getIdentifier().getType());

      //@formatter:off
      var description =
         new StringBuilder( 512 )
                .append( "OSEE " )
                .append( nativeArtifactTypeToken.getName() )
                .append( SpecTypeConverter.descriptionMap.get( groveThing.getIdentifier().getType() ) )
                .toString();
      //@formatter:on

      reqifSpecificationType.setDesc(description);
      reqifSpecificationType.setIdentifier(groveThing.getIdentifier().toString());
      reqifSpecificationType.setLastChange(ReqIFSynchronizationArtifactBuilder.lastChangeEpoch);
      reqifSpecificationType.setLongName(nativeArtifactTypeToken.getName());

      groveThing.setForeignThing(reqifSpecificationType);
   }

}

/* EOF */
