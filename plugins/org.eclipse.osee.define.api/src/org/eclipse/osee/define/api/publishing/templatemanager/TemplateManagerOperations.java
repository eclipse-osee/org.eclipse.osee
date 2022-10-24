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

package org.eclipse.osee.define.api.publishing.templatemanager;

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

   void deleteCache();

   /**
    * Gets the publishing template that is the "best match" for the provided {@link PublishingTemplateRequest}
    * parameters.
    *
    * @param publishingTemplateRequest the publishing template selection parameters.
    * @return the publishing template.
    */

   PublishingTemplate getPublishingTemplate(PublishingTemplateRequest publishingTemplateRequest);

   /**
    * Gets a publishing template by a primary and secondary key pair. In the case where more than one publishing
    * template has the same key pair, the first publishing template found is returned. The following key pair types are
    * supported:
    * <dl>
    * <dt>Primary Key: &quot;NAME&quot;</dt>
    * <dd>Secondary Key: the publishing template name.</dd>
    * <dt>Primary Key: &quot;SAFE_NAME&quot;</dt>
    * <dd>Secondary Key: the publishing template safe name.</dd>
    * <dt>Primary Key: &quot;IDENTIFIER&quot;</dt>
    * <dd>Secondary Key: the publishing template identifier.</dd>
    * </dl>
    *
    * @param primaryKey the primary search key.
    * @param secondaryKey the secondary search key.
    * @return the first found {@link PublishingTemplate}.
    */

   PublishingTemplate getPublishingTemplate(String primaryKey, String secondaryKey);

   /**
    * Gets a list of all publishing template safe names from all publishing template providers. This method is provided
    * for building client GUI list of publishing templates for selection by the user. The selection list will be
    * ambiguous for publishing templates with the same safe name.
    *
    * @return a POJO containing a list of the publishing template safe names.
    */

   PublishingTemplateSafeNames getPublishingTemplateSafeNames();

}

/* EOF */
