/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.testscript;

import java.util.Collection;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;
import org.eclipse.osee.testscript.internal.TeamStatsToken;

/**
 * @author Ryan T. Baldwin
 */
@Path("dashboard")
@Swagger
public interface DashboardEndpoint {

   @GET
   @Path("{branch}/{ciSet}/teamstats")
   @Produces(MediaType.APPLICATION_JSON)
   public Collection<TeamStatsToken> getTeamStats(@PathParam("branch") BranchId branch,
      @PathParam("ciSet") ArtifactId ciSet, @QueryParam("viewId") ArtifactId viewId);

}