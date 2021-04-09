/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.ats.api.workflow;

import static org.eclipse.osee.framework.core.data.OseeClient.OSEE_ACCOUNT_ID;
import java.util.Collection;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
@Path("teamwf")
public interface AtsTeamWfEndpointApi {

   @GET
   @Path("{id}/changedata")
   @Produces({MediaType.APPLICATION_JSON})
   public List<ChangeItem> getChangeData(@PathParam("id") String id);

   @GET
   @Path("{aiId}/version")
   @Produces({MediaType.APPLICATION_JSON})
   Collection<IAtsVersion> getVersionsbyTeamDefinition(@PathParam("aiId") String aiId);

   @GET
   @Path("{id}")
   @Produces({MediaType.APPLICATION_JSON})
   IAtsTeamWorkflow getTeamWorkflow(@PathParam("id") String id);

   @PUT
   @Path("{id}/addchangeids/{teamId}")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   XResultData addChangeIds(@PathParam("id") String workItemId, @PathParam("teamId") String teamId, @HeaderParam(OSEE_ACCOUNT_ID) UserId userId, List<String> changeIds);

   @GET
   @Path("{id}/goal")
   @Produces({MediaType.APPLICATION_JSON})
   List<IAtsGoal> getGoals(@PathParam("id") String id);

   @PUT
   @Path("build/{build}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   XResultData relateReleaseToWorkflow(@PathParam("build") String build, @HeaderParam(OSEE_ACCOUNT_ID) UserId userId, List<String> changeIds);

}