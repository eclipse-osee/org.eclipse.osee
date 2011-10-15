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
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;

/**
 * @author Roberto E. Escobar
 */
public class BranchResource {

   @Context
   UriInfo uriInfo;
   @Context
   Request request;

   String branchUuid;

   public BranchResource(UriInfo uriInfo, Request request, String branchUuid) {
      this.uriInfo = uriInfo;
      this.request = request;
      this.branchUuid = branchUuid;
   }

   @Path("artifact")
   public ArtifactsResource getArtifacts() {
      return new ArtifactsResource(uriInfo, request, branchUuid);
   }

   @GET
   @Produces(MediaType.TEXT_PLAIN)
   public String getAsText() throws OseeCoreException {
      IOseeBranch token = TokenFactory.createBranch(branchUuid, "");
      Branch branch = OrcsApplication.getOseeApi().getBranchCache().get(token);

      StringBuilder builder = new StringBuilder(String.format("BranchUuid [%s]\n", branchUuid));
      builder.append(branch.toStringWithId());
      builder.append("\n");
      return builder.toString();
   }
}
