/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.model;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
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
   @Path("{attributeId}")
   Response getAttribute(@PathParam("attributeId") AttributeId attributeId);

   @GET
   @Consumes({MediaType.APPLICATION_JSON})
   @Path("{attributeId}/version/{transactionId}/text")
   Response getAttributeWithGammaAsText(@PathParam("attributeId") AttributeId attributeId, @PathParam("transactionId") TransactionId transaction);

   @GET
   @Consumes({MediaType.APPLICATION_JSON})
   @Path("{attributeId}/version/{transactionId}")
   Response getAttributeWithGamma(@PathParam("attributeId") AttributeId attributeId, @PathParam("transactionId") TransactionId transaction);

   @GET
   @Path("type")
   @Produces(MediaType.TEXT_HTML)
   Response getAttributeTypes();

   @GET
   @Path("type/{attributeTypeId}")
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces(MediaType.TEXT_HTML)
   Response getAttributeTypeValues(@PathParam("attributeTypeId") AttributeTypeId attributeType);

   @GET
   @Path("type/{attributeTypeId}/version/{transactionId}")
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces(MediaType.TEXT_HTML)
   Response getAttributeTypeValuesForTransaction(@PathParam("attributeTypeId") AttributeTypeId attributeType, @PathParam("transactionId") TransactionId transaction);
}