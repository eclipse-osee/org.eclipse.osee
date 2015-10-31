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
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
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

   private final Long branchUuid;
   private final Long artifactUuid;

   public AttributesResource(UriInfo uriInfo, Request request, Long branchUuid, Long artifactUuid) {
      this.uriInfo = uriInfo;
      this.request = request;
      this.branchUuid = branchUuid;
      this.artifactUuid = artifactUuid;
   }

   @Path("{attributeId}")
   public AttributeResource getAttribute(@PathParam("attributeId") int attributeId) {
      return new AttributeResource(uriInfo, request, branchUuid, artifactUuid, attributeId);
   }

   @Path("{attributeId}/version/{transactionId}")
   public AttributeResource getAttributeWithGamma(@PathParam("attributeId") int attributeId, @PathParam("transactionId") int transactionId) {
      return new AttributeResource(uriInfo, request, branchUuid, artifactUuid, attributeId, transactionId);
   }

   @GET
   @Produces(MediaType.TEXT_HTML)
   public String getAllAttributes() throws OseeCoreException {
      QueryFactory factory = OrcsApplication.getOrcsApi().getQueryFactory();
      ArtifactReadable artifact = factory.fromBranch(branchUuid).andUuid(artifactUuid).getResults().getExactlyOne();

      HtmlWriter writer = new HtmlWriter(uriInfo);
      return writer.toHtml(artifact.getAttributes());
   }
}
