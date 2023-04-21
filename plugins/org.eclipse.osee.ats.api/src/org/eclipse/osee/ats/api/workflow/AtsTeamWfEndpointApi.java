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

import java.util.Collection;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.model.dto.DiffReportEndpointDto;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.jaxrs.mvc.IdentityView;

/**
 * @author Donald G. Dunne
 */
@Path("teamwf")
@Swagger
public interface AtsTeamWfEndpointApi {

   @GET
   @Path("{id}/changedata")
   @Produces({MediaType.APPLICATION_JSON})
   public List<ChangeItem> getChangeData(@PathParam("id") String id);

   @GET
   @IdentityView
   @Path("{aiId}/version")
   @Produces({MediaType.APPLICATION_JSON})
   Collection<IAtsVersion> getVersionsbyTeamDefinition(@PathParam("aiId") String aiId, @QueryParam("sort") String sort);

   @GET
   @Path("{id}")
   @Produces({MediaType.APPLICATION_JSON})
   IAtsTeamWorkflow getTeamWorkflow(@PathParam("id") String id);

   /**
    * @param id can be ai,teamdef,workflow id
    */
   @GET
   @IdentityView
   @Path("{id}/changeTypes")
   @Produces({MediaType.APPLICATION_JSON})
   Collection<ChangeTypes> getChangeTypes(@PathParam("id") String id, @QueryParam("sort") String sort);

   @PUT
   @Path("{id}/addchangeids/{teamId}")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   XResultData addChangeIds(@PathParam("id") String workItemId, @PathParam("teamId") String teamId,
      List<String> changeIds);

   @GET
   @Path("{id}/goal")
   @Produces({MediaType.APPLICATION_JSON})
   List<IAtsGoal> getGoals(@PathParam("id") String id);

   @GET
   @Path("release/{release}")
   @Produces(MediaType.APPLICATION_JSON)
   Collection<ArtifactToken> getWfByRelease(@PathParam("release") String releaseName);

   @PUT
   @Path("build/{build}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   XResultData relateReleaseToWorkflow(@PathParam("build") String build, List<String> changeIds);

   @GET
   @Path("diff")
   @Produces(MediaType.APPLICATION_JSON)
   public DiffReportEndpointDto getDiffReportEndpoint();

}