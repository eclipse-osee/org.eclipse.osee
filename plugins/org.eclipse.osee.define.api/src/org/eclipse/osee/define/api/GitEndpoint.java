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

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;

/**
 * @author Ryan D. Brooks
 */
@Path("git")
@Swagger
public interface GitEndpoint {

   @POST
   @Path("{branch}/repo")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.APPLICATION_JSON)
   ArtifactId trackGitBranch(@PathParam("branch") BranchId branch, @QueryParam("git-branch") String gitBranchName,
      @QueryParam("clone") boolean clone, String gitRepoUrl);

   @POST
   @Path("{branch}/repo/{repositoryName}")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.APPLICATION_JSON)
   ArtifactId updateGitTrackingBranch(@PathParam("branch") BranchId branch,
      @PathParam("repositoryName") String repositoryName, @QueryParam("fetch") boolean fetch,
      @QueryParam("shallowImport") boolean shallowImport, String gitBranchName);

   @GET
   @Path("{branch}/repo/{repositoryName}/changeId/tags")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.APPLICATION_JSON)
   List<String> getChangeIdBetweenTags(@PathParam("branch") BranchId branch,
      @PathParam("repositoryName") String repositoryName, @QueryParam("startTag") String startTag,
      @QueryParam("endTag") String endTag);

   @GET
   @Path("{branch}/repo/{repositoryName}/branches")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.APPLICATION_JSON)
   List<String> getRemoteBranches(@PathParam("branch") BranchId branch,
      @PathParam("repositoryName") String repositoryName);

}