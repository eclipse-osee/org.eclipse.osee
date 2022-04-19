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
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.synchronization.rest.forest.SpecObjectGroveThing;
import org.eclipse.osee.synchronization.rest.forest.SpecificationGroveThing;
import org.eclipse.osee.synchronization.rest.forest.morphology.GroveThing;
import org.eclipse.rmf.reqif10.ReqIF10Factory;
import org.eclipse.rmf.reqif10.SpecObject;

/**
 * Class contains the converter method to create the ReqIf {@link SpecObject} things from the native OSEE
 * {@link ArtifactReadable} things.
 *
 * @author Loren K. Ashley
 */

public class SpecObjectConverter {

   /**
    * Private constructor to prevent instantiation of the class.
    */

   private SpecObjectConverter() {
   }

   /**
    * Converter method for {@link SpecObjectGroveThing}s. This method creates the foreign ReqIF {@link SpecObject} from
    * the native {@link ArtifactReadable}.
    *
    * @param groveThing the {@link SpecObjectGroveThing} to be converted.
    */

   static void convert(GroveThing groveThing) {

      if (groveThing instanceof SpecificationGroveThing) {
         return;
      }

      assert Objects.nonNull(groveThing) && (groveThing instanceof SpecObjectGroveThing);

      var specObject = (SpecObjectGroveThing) groveThing;
      var nativeArtifactReadable = (ArtifactReadable) groveThing.getNativeThing();

      var reqifSpecObject = ReqIF10Factory.eINSTANCE.createSpecObject();

      reqifSpecObject.setDesc(nativeArtifactReadable.getClass().getName());
      reqifSpecObject.setIdentifier(groveThing.getIdentifier().toString());
      reqifSpecObject.setLastChange(ReqIFSynchronizationArtifactBuilder.lastChangeEpoch);
      reqifSpecObject.setLongName(nativeArtifactReadable.getName());

      specObject.setForeignThing(reqifSpecObject);
   }

}

/* EOF */