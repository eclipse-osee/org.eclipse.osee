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

package org.eclipse.osee.synchronization.rest.reqifsynchronizationartifactbuilder;

import java.util.Objects;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.synchronization.rest.forest.SpecTypeGroveThing;
import org.eclipse.osee.synchronization.rest.forest.morphology.GroveThing;
import org.eclipse.rmf.reqif10.ReqIF10Factory;
import org.eclipse.rmf.reqif10.SpecType;
import org.eclipse.rmf.reqif10.SpecificationType;

/**
 * Class contains the converter method to create the ReqIf {@link SpecificationType} things from the native OSEE
 * {@link ArtifactTypeToken} things.
 *
 * @author Loren K. Ashley
 */

public class SpecTypeConverter {

   /**
    * Private constructor to prevent instantiation of the class.
    */

   private SpecTypeConverter() {
   }

   /**
    * Converter method for {@link SpecTypeGroveThing} things. This method creates the foreign ReqIF {@link SpecType}
    * from the native {@link ArtifactTypeToken}.
    *
    * @param groveThing the {@link SpecTypeGroveThing} thing to be converted.
    */

   static void convert(GroveThing groveThing) {
      assert Objects.nonNull(groveThing) && (groveThing instanceof SpecTypeGroveThing);

      var specType = (SpecTypeGroveThing) groveThing;
      var nativeArtifactTypeToken = (ArtifactTypeToken) groveThing.getNativeThing();

      var reqifSpecificationType = ReqIF10Factory.eINSTANCE.createSpecificationType();

      //@formatter:off
      var description = new StringBuilder( 512 )
                           .append( "OSEE " ).append( nativeArtifactTypeToken.getName() ).append( " SpecificationGroveThing Type")
                           .toString();
      //@formatter:on

      reqifSpecificationType.setDesc(description);
      reqifSpecificationType.setIdentifier(specType.getIdentifier().toString());
      reqifSpecificationType.setLastChange(ReqIFSynchronizationArtifactBuilder.lastChangeEpoch);
      reqifSpecificationType.setLongName(nativeArtifactTypeToken.getName());

      specType.setForeignThing(reqifSpecificationType);
   }

}

/* EOF */
