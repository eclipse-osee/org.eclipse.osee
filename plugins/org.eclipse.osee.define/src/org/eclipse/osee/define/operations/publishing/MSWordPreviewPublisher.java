/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.osee.define.operations.publishing;

import java.io.Writer;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.define.api.publishing.PublishingOptions;
import org.eclipse.osee.define.api.publishing.templatemanager.PublishingTemplate;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.publishing.WordMLProducer;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Branden W. Phillips
 */
public class MSWordPreviewPublisher extends MSWordTemplatePublisher {

   /**
    * This subclass of the MSWordTemplatePublisher is the publishing process for publishing artifacts with similar
    * features to the client-side publishing preview.
    */
   public MSWordPreviewPublisher(PublishingOptions publishingOptions, PublishingTemplate publishingTemplate, Writer writer, OrcsApi orcsApi, AtsApi atsApi) {
      super(publishingOptions, publishingTemplate, writer, orcsApi, atsApi);
   }

   /**
    * {@inheritDoc}
    * <p>
    * When the flags {@link this#includeEmptyHeaders} and {@link this#recurseChildren} are set empty heads will be
    * populated for each artifact.
    * <p>
    * Data rights will be setup for each artifact.
    * <p>
    * Then the super class will be used to process the artifacts.
    */

   @Override
   protected void processContent(List<ArtifactReadable> artifacts, WordMLProducer wordMl) {

      var includeEmptyHeaders = this.templatePublishingData.getOutliningOptions().isIncludeEmptyHeaders();
      var recurseChildren = this.templatePublishingData.getOutliningOptions().isRecurseChildren();

      if (!includeEmptyHeaders && recurseChildren) {
         this.populateEmptyHeaders(artifacts);
      }

      this.setUpDataRights(artifacts);

      super.processContent(artifacts, wordMl);
   }

   /**
    * {@inheritDoc}
    * <p>
    * Performs the super class inclusion check and also excludes HeadingMsWord artifacts that do not have children
    * artifacts.
    */

   @Override
   protected boolean checkIncluded(ArtifactReadable artifactReadable) {

      /*
       * The super.checkIncluded adds ArtifactReadables of HeadingMsWord without children to the emptyFolders set. The
       * super check must come first.
       */

      return super.checkIncluded(artifactReadable) && !emptyFolders.contains(artifactReadable);

   }

}

/* EOF */