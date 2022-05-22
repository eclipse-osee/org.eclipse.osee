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
import org.eclipse.osee.synchronization.rest.IdentifierType;
import org.eclipse.osee.synchronization.rest.UnexpectedGroveThingTypeException;
import org.eclipse.osee.synchronization.rest.forest.GroveThing;
import org.eclipse.osee.synchronization.rest.forest.denizens.NativeHeader;
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
    * Converter method for {@link NativeHeader}s. This method creates the foreign ReqIF {@link ReqIFHeader} from the
    * native {@link NativeHeader} thing.
    *
    * @param groveThing the {@link NativeHeader} to be converted.
    */

   static void convert(GroveThing groveThing) {

      //@formatter:off
      assert
            Objects.nonNull(groveThing)
         && groveThing.isType(IdentifierType.HEADER)
         : UnexpectedGroveThingTypeException.buildMessage( groveThing, IdentifierType.HEADER );
      //@formatter:on

      var nativeHeader = (NativeHeader) groveThing.getNativeThing();
      var reqifHeader = ReqIF10Factory.eINSTANCE.createReqIFHeader();

      reqifHeader.setComment(nativeHeader.getComment());
      reqifHeader.setIdentifier(nativeHeader.getId().toString());
      reqifHeader.setRepositoryId(nativeHeader.getRepositoryId());
      reqifHeader.setReqIFVersion("1.0.1");
      reqifHeader.setSourceToolId(nativeHeader.getSourceToolId());
      reqifHeader.setReqIFToolId(
         ReqIFSynchronizationArtifactBuilder.class.getName() + ":" + ReqIFSynchronizationArtifactBuilder.version);
      reqifHeader.setCreationTime(nativeHeader.getTime());
      reqifHeader.setTitle(nativeHeader.getTitle());

      groveThing.setForeignThing(reqifHeader);
   }

}

/* EOF */
