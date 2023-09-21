/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.orcs.rest.model;

import java.util.List;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeDataTransfer;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.TransactionId;

/**
 * @author Roberto E. Escobar
 */
@Path("{artifactId}/attribute")
public interface AttributeEndpoint {

   @GET
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces(MediaType.TEXT_HTML)
   String getAttributesAsHtml(@PathParam("artifactId") ArtifactId artifactId);

   @GET
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces(MediaType.APPLICATION_JSON)
   @Path("all")
   List<AttributeDataTransfer> getAttributes(@PathParam("artifactId") ArtifactId artifactId);

   @GET
   @Consumes({MediaType.APPLICATION_JSON})
   @Path("{attributeId}")
   Response getAttribute(@PathParam("attributeId") AttributeId attributeId);

   @GET
   @Consumes({MediaType.APPLICATION_JSON})
   @Path("{attributeId}/version/{transactionId}/text")
   Response getAttributeWithGammaAsText(@PathParam("attributeId") AttributeId attributeId,
      @PathParam("transactionId") TransactionId transaction);

   @GET
   @Consumes({MediaType.APPLICATION_JSON})
   @Path("{attributeId}/version/{transactionId}")
   Response getAttributeWithGamma(@PathParam("attributeId") AttributeId attributeId,
      @PathParam("transactionId") TransactionId transaction);

   @GET
   @Path("type")
   @Produces(MediaType.TEXT_HTML)
   Response getAttributeTypes();

   @GET
   @Path("type/{attributeType}")
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces(MediaType.TEXT_HTML)
   Response getAttributeTypeValues(@PathParam("attributeType") AttributeTypeToken attributeType);

   @GET
   @Path("type/{attributeType}/version/{transactionId}")
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces(MediaType.TEXT_HTML)
   Response getAttributeTypeValuesForTransaction(@PathParam("attributeType") AttributeTypeToken attributeType,
      @PathParam("transactionId") TransactionId transaction);

   @GET
   @Path("{attributeId}/enums")
   @Produces(MediaType.APPLICATION_JSON)
   Set<String> getAttributeEnums(@PathParam("attributeId") AttributeId attributeId);
}