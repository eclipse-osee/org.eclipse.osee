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

package org.eclipse.osee.define.rest.api.publisher.templatemanager;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.publishing.PublishingTemplate;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;

/**
 * This interface defines the REST API end points for obtaining publishing templates.
 *
 * @author Loren K. Ashley
 */

@Path("templatemanager")
@Swagger
public interface TemplateManagerEndpoint {

   /**
    * Empties any caches of Publishing Templates that maybe held by the Template Manager.
    *
    * @implSpec This entry point requires the user has Publishing Group permissions.
    */

   @DELETE
   void deleteCache();

   /**
    * Gets the publishing template that is the "best match" for the provided {@link PublishingTemplateRequest}
    * parameters.
    *
    * @param publishingTemplateRequest the publishing template selection parameters.
    * @return the publishing template.
    */

   @POST
   @Path("getPublishingTemplate")
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   //@formatter:off
   PublishingTemplate
      getPublishingTemplate
         (
            PublishingTemplateRequest publishingTemplateRequest
         );
   //@formatter:on

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

   @POST
   @Path("getPublishingTemplate/{primaryKey}")
   @Consumes({MediaType.TEXT_PLAIN})
   @Produces({MediaType.APPLICATION_JSON})
   //@formatter:off
   PublishingTemplate
      getPublishingTemplate
         (
            @PathParam(value = "primaryKey") String primaryKey,
                                             String secondaryKey
         );
   //@formatter:on

   /**
    * Gets a list of all the keys for the Publishing Templates cached by the Publishing Template Manager. This method is
    * provided for building a client GUI list of publishing templates for selection by the user. The
    * {@link PublishingTemplateKeyGroup} objects within the {@link PublishingTemplateKeyGroups} object will be sorted by
    * the value of the safe name key. The selection list will be ambiguous for publishing templates with the same safe
    * name.
    *
    * @return a list of the keys used for caching the Publishing Templates.
    */

   @GET
   @Path("getPublishingTemplateKeyGroups")
   @Produces({MediaType.APPLICATION_JSON})
   //@formatter:off
   PublishingTemplateKeyGroups
      getPublishingTemplateKeyGroups();
   //@formatter:on

}

/* EOF */
