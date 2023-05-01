/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.define.operations.publishing.templatemanager;

import org.eclipse.osee.define.api.publishing.templatemanager.PublishingTemplateRequest;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * This provider manages publishing templates that are saved as {@link CoreArtifactTypes#RendererTemplateWholeWord} OSEE
 * Artifacts on the Common branch.
 *
 * @author Ryan D. Brooks
 * @author Loren K. Ashley
 */

@IsPublishingTemplateProvider(key = "AT-")
public class ArtifactPublishingTemplateProvider extends AbstractPublishingTemplateProvider {

   /**
    * Creates a new instance of an {@link ArtifactPublishingTemplateProvider}.
    *
    * @param logger a handle to the {@link Log} service.
    * @param orcsApi a handle to the {@link OrcsApi} service.
    */

   public ArtifactPublishingTemplateProvider(Log logger, OrcsApi orcsApi) {
      super(logger);

      this.publishingTemplateCache = ArtifactPublishingTemplateCache.create(logger, orcsApi);
   }

   /**
    * Determines the applicability of this Publishing Template Provider to the provided
    * {@link PublishingTemplateRequest}. The predefined applicability ratings are defined by the interface
    * {@link PublishingTemplateProvider}.
    *
    * @return an integer applicability rating.
    */

   @Override
   public int getApplicabilityRating(PublishingTemplateRequest publishingTemplateRequest) {
      //@formatter:off
      return
         !publishingTemplateRequest.isByOptions()
            ? publishingTemplateRequest.getTemplateId().startsWith("AT-")
                 ? PublishingTemplateProvider.IDENTIFIER_PREFIX_MATCH
                 : PublishingTemplateProvider.NO_MATCH
            : PublishingTemplateProvider.DEFAULT_MATCH;
      //@formatter:on
   }

}

/* EOF */