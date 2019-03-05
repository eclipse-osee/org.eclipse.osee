/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.UserId;

/**
 * @author Ryan D. Brooks
 */
@Path("git")
public interface GitEndpoint {

   @POST
   @Path("{branch}/repo")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.APPLICATION_JSON)
   ArtifactId trackGitBranch(@PathParam("branch") BranchId branch, @HeaderParam("osee.account.id") UserId account, @QueryParam("git-branch") String gitBranchName, @QueryParam("clone") boolean clone, String gitRepoUrl);

   @POST
   @Path("{branch}/repo/{repository-name}")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.APPLICATION_JSON)
   ArtifactId updateGitTrackingBranch(@PathParam("branch") BranchId branch, @PathParam("repository-name") String repositoryName, @HeaderParam("osee.account.id") UserId account, @QueryParam("fetch") boolean fetch, String gitBranchName);

}