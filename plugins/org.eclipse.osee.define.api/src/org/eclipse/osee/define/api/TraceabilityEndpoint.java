/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.api;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.data.UserId;

/**
 * @author Ryan D. Brooks
 */
@Path("trace")
public interface TraceabilityEndpoint {

   /**
    * @param branch -- the id of the branch the artifacts are loaded from
    * @param selectedTypes -- a list of the Low level Artifact types that will be used for the report
    * @return -- An Excel sheet (in XML format) containing the two reports
    */
   @GET
   @Path("highlowtrace")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_XML)
   Response getLowHighReqReport(@QueryParam("branch") BranchId branch, @QueryParam("selected_types") String selectedTypes);

   @GET
   @Path("srs-impd/{branch}")
   @Produces(MediaType.APPLICATION_JSON)
   TraceData getSrsToImpd(@PathParam("branch") BranchId branch, @DefaultValue("-1") @QueryParam("excludeType") ArtifactTypeId excludeType);

   @GET
   @Path("ui")
   @Produces(MediaType.TEXT_HTML)
   String getSinglePageApp();

   @GET
   @Path("pidsVerification")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_XML)
   Response getPidsVerificationReport(@QueryParam("branch") BranchId branch, @QueryParam("rootArtifact") ArtifactId rootArtifact);

   @POST
   @Path("cert/{branch}/repo/{repository-name}/files")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   ArtifactId baselineFiles(@PathParam("branch") BranchId branch, @PathParam("repository-name") String repositoryName, @HeaderParam("osee.account.id") UserId account, CertBaselineData baselineData);

   @GET
   @Path("cert/{branch}/{artifact}")
   @Produces(MediaType.APPLICATION_JSON)
   CertBaselineData getBaselineData(@PathParam("branch") BranchId branch, @PathParam("artifact") ArtifactId certBaselineData);

   @GET
   @Path("cert/{branch}/repo/{repository-name}")
   @Produces(MediaType.APPLICATION_JSON)
   List<CertBaselineData> getBaselineData(@PathParam("branch") BranchId branch, @PathParam("repository-name") String repositoryName);

   @POST
   @Path("cert/{destination-branch}/repo/{repository-name}/{source-branch}")
   @Produces(MediaType.APPLICATION_JSON)
   TransactionToken copyCertBaselineData(@HeaderParam("osee.account.id") UserId account, @PathParam("destination-branch") BranchId destinationBranch, @PathParam("repository-name") String repositoryName, @PathParam("source-branch") BranchId sourceBranch);

   @GET
   @Path("cert/{branch}/repo/{repository-name}/files")
   @Produces(MediaType.APPLICATION_JSON)
   List<CertFileData> getCertFileData(@PathParam("branch") BranchId branch, @PathParam("repository-name") String repositoryName);
}