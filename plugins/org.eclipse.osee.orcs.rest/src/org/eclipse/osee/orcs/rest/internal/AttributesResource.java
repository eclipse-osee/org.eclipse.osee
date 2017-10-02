/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.internal;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * @author Roberto E. Escobar
 */
@Path("attribute")
public class AttributesResource {

   // Allows insertion of contextual objects into the class,
   // e.g. ServletContext, Request, Response, UriInfo
   @Context
   private final UriInfo uriInfo;
   @Context
   private final Request request;

   private final BranchId branchId;
   private final Long artifactUuid;

   public AttributesResource(UriInfo uriInfo, Request request, BranchId branchId, Long artifactUuid) {
      this.uriInfo = uriInfo;
      this.request = request;
      this.branchId = branchId;
      this.artifactUuid = artifactUuid;
   }

   @Path("{attributeId}")
   public AttributeResource getAttribute(@PathParam("attributeId") AttributeId attributeId) {
      return new AttributeResource(uriInfo, request, branchId, artifactUuid, attributeId);
   }

   @Path("{attributeId}/version/{transactionId}/text")
   public AttributeResource getAttributeWithGammaAsText(@PathParam("attributeId") AttributeId attributeId, @PathParam("transactionId") TransactionId transactionId) {
      AttributeResource toReturn =
         new AttributeResource(uriInfo, request, branchId, artifactUuid, attributeId, transactionId);
      toReturn.setTextOut(true);
      return toReturn;
   }

   @Path("{attributeId}/version/{transactionId}")
   public AttributeResource getAttributeWithGamma(@PathParam("attributeId") AttributeId attributeId, @PathParam("transactionId") TransactionId transactionId) {
      return new AttributeResource(uriInfo, request, branchId, artifactUuid, attributeId, transactionId);
   }

   @Path("type")
   @Produces(MediaType.TEXT_HTML)
   public AttributeTypeResource getAttributeTypes() {
      return new AttributeTypeResource(uriInfo, request, branchId, artifactUuid);
   }

   @Path("type/{attributeTypeId}")
   @Produces(MediaType.TEXT_HTML)
   public AttributeTypeResource getAttributeTypeValues(@PathParam("attributeTypeId") Long attributeTypeId) {
      return new AttributeTypeResource(uriInfo, request, branchId, artifactUuid, attributeTypeId);
   }

   @Path("type/{attributeTypeId}/version/{transactionId}")
   @Produces(MediaType.TEXT_HTML)
   public AttributeTypeResource getAttributeTypeValuesForTransaction(@PathParam("attributeTypeId") Long attributeTypeId, @PathParam("transactionId") TransactionId transactionId) {
      return new AttributeTypeResource(uriInfo, request, branchId, artifactUuid, attributeTypeId, transactionId);
   }

   @GET
   @Produces(MediaType.TEXT_HTML)
   public String getAllAttributes()  {
      QueryFactory factory = OrcsApplication.getOrcsApi().getQueryFactory();
      ArtifactReadable artifact = factory.fromBranch(branchId).andUuid(artifactUuid).getResults().getExactlyOne();

      HtmlWriter writer = new HtmlWriter(uriInfo);
      return writer.toHtml(artifact.getAttributes());
   }
}
