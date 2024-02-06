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

package org.eclipse.osee.define.operations.api.publisher.templatemanager;

import org.eclipse.osee.define.rest.api.publisher.templatemanager.PublishingTemplateKeyGroups;
import org.eclipse.osee.define.rest.api.publisher.templatemanager.PublishingTemplateRequest;
import org.eclipse.osee.framework.core.publishing.PublishingTemplate;

/**
 * Interface for the Define Operations Template Manager Service.
 *
 * @author Loren K. Ashley
 */

public interface TemplateManagerOperations {

   /**
    * Deletes any Publishing Template caches maintained by {@link PublishingTemplateProvider} implementations that are
    * managed by this Template Manager.
    */

   public void deleteCache();

   /**
    * Gets the publishing template that is the "best match" for the provided {@link PublishingTemplateRequest}
    * parameters.
    *
    * @param publishingTemplateRequest the publishing template selection parameters.
    * @return the publishing template.
    */

   public PublishingTemplate getPublishingTemplate(PublishingTemplateRequest publishingTemplateRequest);

   /**
    * Gets a status report for the publishing template that is the "best match" for the provided
    * {@link PublishingTemplateRequest} parameters.
    *
    * @param publishingTemplateRequest the publishing template selection parameters.
    * @return the publishing template.
    */

   public String getPublishingTemplateStatus(PublishingTemplateRequest publishingTemplateRequest);

   /**
    * Gets a list of all publishing template safe names from all publishing template providers. This method is provided
    * for building client GUI list of publishing templates for selection by the user. The selection list will be
    * ambiguous for publishing templates with the same safe name.
    *
    * @return a POJO containing a list of the publishing template safe names.
    */

   public PublishingTemplateKeyGroups getPublishingTemplateKeyGroups();

}

/* EOF */
