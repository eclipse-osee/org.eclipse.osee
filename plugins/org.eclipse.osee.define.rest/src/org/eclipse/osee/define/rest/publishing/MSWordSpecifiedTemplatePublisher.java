/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.define.rest.publishing;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.define.api.PublishingOptions;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.util.PageOrientation;
import org.eclipse.osee.framework.core.util.WordMLProducer;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Branden W. Phillips
 */
public class MSWordSpecifiedTemplatePublisher extends MSWordTemplatePublisher {

   /**
    * This subclass of the MSWordTemplatePublisher is the publishing process for publishing artifacts similarly to the
    * old client "Publish With Specified Template" BLAM. This publishes via the hierarchy under the given artifact,
    * using the given template. This REST Operation is currently not in use anywhere.
    */
   public MSWordSpecifiedTemplatePublisher(PublishingOptions publishingOptions, Log logger, OrcsApi orcsApi) {
      super(publishingOptions, logger, orcsApi);
   }

   @Override
   protected void processContent(ArtifactReadable headArtifact, WordMLProducer wordMl) {
      if (!includeEmptyHeaders) {
         List<ArtifactReadable> artifacts = new LinkedList<>();
         artifacts.add(headArtifact);
         populateEmptyHeaders(artifacts);
      }

      getDataRightsOverride();
      setDataRightResponse(headArtifact);

      processArtifact(headArtifact, wordMl);
      if (publishingOptions.updateParagraphNumbers) {
         updateParagraphNumbers();
      }

      addErrorLogToWordMl(wordMl);
   }

   @Override
   protected boolean checkIncluded(ArtifactReadable artifact) {
      return super.checkIncluded(artifact) && !emptyFolders.contains(artifact);
   }

   @Override
   protected String getArtifactFooter(ArtifactReadable artifact) {
      String orientationStr = null;
      if (artifact.isAttributeTypeValid(CoreAttributeTypes.PageOrientation)) {
         orientationStr = artifact.getSoleAttributeValue(CoreAttributeTypes.PageOrientation, "Portrait");
      }
      PageOrientation orientation = PageOrientation.fromString(orientationStr);
      String footer = response.getContent(artifact, orientation);
      return footer;
   }
}
