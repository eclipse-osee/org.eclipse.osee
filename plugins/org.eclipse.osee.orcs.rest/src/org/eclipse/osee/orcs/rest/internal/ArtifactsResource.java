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
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.rest.internal.search.artifact.ArtifactSearch_V1;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactsResource {

   // Allows to insert contextual objects into the class,
   // e.g. ServletContext, Request, Response, UriInfo
   @Context
   private final UriInfo uriInfo;
   @Context
   private final Request request;

   private final BranchId branchId;

   private final OrcsApi orcsApi;

   public ArtifactsResource(UriInfo uriInfo, Request request, BranchId branchId, OrcsApi orcsApi) {
      this.uriInfo = uriInfo;
      this.request = request;
      this.branchId = branchId;
      this.orcsApi = orcsApi;
   }

   @Path("search/v1")
   public ArtifactSearch_V1 getSearch() {
      return new ArtifactSearch_V1(uriInfo, request, orcsApi);
   }

   @Path("{uuid}")
   public ArtifactResource getArtifact(@PathParam("uuid") Long artifactUuid) {
      return new ArtifactResource(uriInfo, request, branchId, artifactUuid);
   }

   @GET
   @Produces(MediaType.TEXT_HTML)
   public String getAsHtml() {
      ArtifactReadable rootArtifact =
         orcsApi.getQueryFactory().fromBranch(branchId).andIsHeirarchicalRootArtifact().getResults().getExactlyOne();

      ResultSet<ArtifactReadable> arts = rootArtifact.getChildren();
      HtmlWriter writer = new HtmlWriter(uriInfo);
      return writer.toHtml(arts);
   }
}
