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

import java.io.Writer;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.define.api.PublishingOptions;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.util.PageOrientation;
import org.eclipse.osee.framework.core.util.WordMLWriter;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Branden W. Phillips
 */
public class MSWordPreviewPublisher extends MSWordTemplatePublisher {

   /**
    * This subclass of the MSWordTemplatePublisher is the publishing process for publishing artifacts with similar
    * features to the client-side publishing preview.
    */
   public MSWordPreviewPublisher(PublishingOptions publishingOptions, Writer writer, OrcsApi orcsApi, AtsApi atsApi) {
      super(publishingOptions, writer, orcsApi, atsApi);
   }

   @Override
   protected void processContent(ArtifactReadable headArtifact, WordMLWriter wordMl) {
      if (!includeEmptyHeaders) {
         List<ArtifactReadable> artifacts = new LinkedList<>();
         artifacts.add(headArtifact);
         populateEmptyHeaders(artifacts);
      }

      setUpDataRights(headArtifact);

      processArtifact(headArtifact, wordMl);
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