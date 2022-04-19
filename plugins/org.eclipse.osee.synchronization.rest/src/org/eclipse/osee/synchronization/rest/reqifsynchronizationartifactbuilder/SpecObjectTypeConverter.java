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
import org.eclipse.osee.synchronization.rest.forest.SpecObjectTypeGroveThing;
import org.eclipse.osee.synchronization.rest.forest.morphology.GroveThing;
import org.eclipse.rmf.reqif10.ReqIF10Factory;
import org.eclipse.rmf.reqif10.SpecObjectType;

/**
 * Class contains the converter method to create the ReqIf {@link SpecObjectType} things from the native OSEE
 * {@link ArtifactTypeToken} things.
 *
 * @author Loren K. Ashley
 */

public class SpecObjectTypeConverter {

   /**
    * Private constructor to prevent instantiation of the class.
    */

   private SpecObjectTypeConverter() {
   }

   /**
    * Converter method for {@link SpecObjectTypeGroveThing} things. This method creates the foreign ReqIF
    * {@link SpecObjecType} from the native {@link ArtifactTypeToken}.
    *
    * @param groveThing the {@link SpecObjectTypeGroveThing} thing to be converted.
    */

   static void convert(GroveThing groveThing) {

      assert Objects.nonNull(groveThing) && (groveThing instanceof SpecObjectTypeGroveThing);

      var specObjectType = (SpecObjectTypeGroveThing) groveThing;
      var nativeArtifactTypeToken = (ArtifactTypeToken) groveThing.getNativeThing();

      var reqifSpecObjectType = ReqIF10Factory.eINSTANCE.createSpecObjectType();

      //@formatter:off
      var description = new StringBuilder( 512 )
                           .append( "OSEE " ).append( nativeArtifactTypeToken.getName() ).append( " Spec Object Type")
                           .toString();
      //@formatter:on

      reqifSpecObjectType.setDesc(description);
      reqifSpecObjectType.setIdentifier(specObjectType.getIdentifier().toString());
      reqifSpecObjectType.setLastChange(ReqIFSynchronizationArtifactBuilder.lastChangeEpoch);
      reqifSpecObjectType.setLongName(nativeArtifactTypeToken.getName());

      specObjectType.setForeignThing(reqifSpecObjectType);
   }

}

/* EOF */
