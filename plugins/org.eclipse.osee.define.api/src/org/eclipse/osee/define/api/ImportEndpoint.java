/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.define.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author David W. Miller
 */
@Path("import")
public interface ImportEndpoint {

   @POST
   @Path("{branch}/word")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.APPLICATION_JSON)
   XResultData importWord(@PathParam("branch") BranchId branch, @DefaultValue("") @QueryParam("wordDoc") String wordDoc, @QueryParam("parentArtifact") ArtifactId parent, @QueryParam("tier") Integer tier);

   @POST
   @Path("{branch}/verify")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.APPLICATION_JSON)
   XResultData verifyWordImport(@PathParam("branch") BranchId branch, @DefaultValue("") @QueryParam("wordDoc") String wordDoc, @QueryParam("parentArtifact") ArtifactId parent, @QueryParam("tier") Integer tier);

   @POST
   @Path("{branch}/rectify")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.APPLICATION_JSON)
   XResultData rectifyWordImport(@PathParam("branch") BranchId branch, @DefaultValue("") @QueryParam("wordDoc") String wordDoc, @QueryParam("parentArtifact") ArtifactId parent, @QueryParam("tier") Integer tier, @QueryParam("doorsIds") String doorsIds);

   @POST
   @Path("{branch}/all")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.APPLICATION_JSON)
   XResultData importSetup(@PathParam("branch") BranchId branch, @QueryParam("baseDir") String baseDir, @QueryParam("startBranch") Integer startBranch, @QueryParam("handleRelations") boolean handleRelations, @QueryParam("singleBranch") boolean singleBranch);

   @POST
   @Path("postProcess/{startBranch}/{singleBranch}")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.APPLICATION_JSON)
   XResultData postProcess(@PathParam("startBranch") Integer startBranch, @PathParam("singleBranch") boolean singleBranch);

   @POST
   @Path("postProcessBranch/{branch}")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.APPLICATION_JSON)
   XResultData postProcessBranch(@PathParam("branch") BranchId branch, @QueryParam("figure") ArtifactId figure, @QueryParam("caption") ArtifactId caption);

   @POST
   @Path("postProcessBranchLinks/{branch}/parent/{parent}")
   @Produces(MediaType.APPLICATION_JSON)
   XResultData postProcessBranchLinks(@PathParam("branch") BranchId branch, @PathParam("parent") ArtifactId parent);
}