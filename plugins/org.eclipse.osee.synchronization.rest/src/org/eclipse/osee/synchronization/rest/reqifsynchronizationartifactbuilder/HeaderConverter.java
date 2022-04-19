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
import org.eclipse.osee.synchronization.rest.forest.HeaderGroveThing;
import org.eclipse.osee.synchronization.rest.forest.morphology.GroveThing;
import org.eclipse.rmf.reqif10.ReqIF10Factory;
import org.eclipse.rmf.reqif10.ReqIFHeader;

/**
 * Class contains the converter method to create the {@link ReqIFHeader} thing.
 *
 * @author Loren K. Ashley
 */

public class HeaderConverter {

   /**
    * Private constructor to prevent instantiation of the class.
    */

   private HeaderConverter() {
   }

   /**
    * Converter method for {@link HeaderGroveThing}s. This method creates the foreign ReqIF {@link ReqIFHeader} from the
    * native {@link HeaderGroveThing} thing.
    *
    * @param groveThing the {@link HeaderGroveThing} to be converted.
    */

   static void convert(GroveThing groveThing) {

      assert Objects.nonNull(groveThing) && (groveThing instanceof HeaderGroveThing);

      var header = (HeaderGroveThing) groveThing;
      var reqifHeader = ReqIF10Factory.eINSTANCE.createReqIFHeader();

      reqifHeader.setComment(header.getComment());
      reqifHeader.setIdentifier(header.getIdentifier().toString());
      reqifHeader.setRepositoryId(header.getRepositoryId());
      reqifHeader.setReqIFVersion("1.0.1");
      reqifHeader.setSourceToolId(header.getSourceToolId());
      reqifHeader.setReqIFToolId(
         ReqIFSynchronizationArtifactBuilder.class.getName() + ":" + ReqIFSynchronizationArtifactBuilder.version);
      reqifHeader.setCreationTime(header.getTime());
      reqifHeader.setTitle(header.getTitle());

      header.setForeignThing(reqifHeader);
   }

}

/* EOF */
