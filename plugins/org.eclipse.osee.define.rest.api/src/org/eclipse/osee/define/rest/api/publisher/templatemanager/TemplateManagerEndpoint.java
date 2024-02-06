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
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.apache.cxf.jaxrs.model.wadl.Description;
import org.eclipse.osee.framework.core.publishing.PublishingTemplate;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;

/**
 * This interface defines the REST API end points for obtaining publishing templates.
 *
 * @author Loren K. Ashley
 */

//@formatter:off
@Swagger
@Path("templatemanager")
public interface TemplateManagerEndpoint {
//@formatter:on

   /**
    * Empties any caches of Publishing Templates that are held by the Template Manager.
    *
    * @implSpec This entry point requires the user has Publishing Group permissions.
    */

   //@formatter:off
   @DELETE
   @Description("Empties the caches of Publishing Templates that are held by the Template Manager.")
   void deleteCache();
   //@formatter:on

   /**
    * Gets the publishing template that is the "best match" for the provided {@link PublishingTemplateRequest}
    * parameters.
    *
    * @param publishingTemplateRequest the publishing template selection parameters.
    * @return the publishing template.
    * @apiNote The preferred method for requesting a Publishing Template is by match criteria. When a Publishing
    * Template request is made by identifier, the request should be for a "well known" artifact with a code defined
    * ArtifactToken.
    */

   //@formatter:off
   @POST
   @Description("Gets the publishing template that is the \"best match\" for the publishing template request parameters.")
   @Path("getPublishingTemplate")
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   PublishingTemplate
      getPublishingTemplate
         (
            PublishingTemplateRequest publishingTemplateRequest
         );
   //@formatter:on

   /**
    * Gets a status report for the publishing template that is the "best match" for the provided
    * {@link PublishingTemplateRequest} parameters.
    *
    * @param publishingTemplateRequest the publishing template selection parameters.
    * @return a status report for the requested publishing template.
    * @apiNote The preferred method for requesting a Publishing Template is by match criteria. When a Publishing
    * Template request is made by identifier, the request should be for a "well known" artifact with a code defined
    * ArtifactToken.
    */

   //@formatter:off
   @POST
   @Description("Gets a status report for the publishing template that is the \"best match\" for the publishing template request parameters.")
   @Path("getPublishingTemplateStatus")
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   String
      getPublishingTemplateStatus
         (
            PublishingTemplateRequest publishingTemplateRequest
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

   //@formatter:off
   @GET
   @Description
      (
           "Gets a list of all the keys for the Publishing Templates cached by the Publishing Template Manager. This method is "
         + "provided for building a client GUI list of publishing templates for selection. Each element of the list contains "
         + "the publishing template identifier, match criteria, name, and safe name. The list is sorted by the safe name."
      )
   @Path("getPublishingTemplateKeyGroups")
   @Produces({MediaType.APPLICATION_JSON})
   PublishingTemplateKeyGroups
      getPublishingTemplateKeyGroups();
   //@formatter:on

}

/* EOF */
