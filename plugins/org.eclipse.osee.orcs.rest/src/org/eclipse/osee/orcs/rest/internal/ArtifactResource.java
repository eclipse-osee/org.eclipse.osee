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
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactResource {

   @Context
   UriInfo uriInfo;
   @Context
   Request request;

   String branchUuid;
   String artifactUuid;

   public ArtifactResource(UriInfo uriInfo, Request request, String branchUuid, String artifactUuid) {
      this.uriInfo = uriInfo;
      this.request = request;
      this.branchUuid = branchUuid;
      this.artifactUuid = artifactUuid;
   }

   @Path("version")
   public VersionsResource getArtifactVersions() {
      return new VersionsResource(uriInfo, request, branchUuid, artifactUuid);
   }

   @GET
   @Produces(MediaType.TEXT_PLAIN)
   public String getAsText() {
      return String.format("BranchUuid [%s] ArtifactUuid [%s]", branchUuid, artifactUuid);
   }
}
