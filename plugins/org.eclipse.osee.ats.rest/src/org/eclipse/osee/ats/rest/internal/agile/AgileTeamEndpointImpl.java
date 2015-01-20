/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.agile;

import java.util.LinkedList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.osee.ats.api.agile.AgileTeamEndpointApi;
import org.eclipse.osee.ats.api.agile.AgileUtil;
import org.eclipse.osee.ats.api.agile.IAgileBacklog;
import org.eclipse.osee.ats.api.agile.IAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.agile.NewAgileBacklog;
import org.eclipse.osee.ats.api.agile.NewAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.NewAgileSprint;
import org.eclipse.osee.ats.api.agile.NewAgileTeam;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.impl.IAtsServer;
import org.eclipse.osee.ats.impl.config.AbstractConfigResource;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jaxrs.mvc.IdentityView;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * Donald G. Dunne
 */
@Path("agile/team")
public class AgileTeamEndpointImpl extends AbstractConfigResource implements AgileTeamEndpointApi {

   public AgileTeamEndpointImpl(IAtsServer atsServer) {
      super(AtsArtifactTypes.AgileTeam, atsServer);
   }

   /********************************
    ** Agile Team
    ***********************************/
   @Override
   @POST
   @IdentityView
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public NewAgileTeam createTeam(NewAgileTeam team) throws Exception {
      // validate title
      if (!Strings.isValid(team.getName())) {
         throw new OseeArgumentException("name is not valid");
      }

      String guid = team.getGuid();
      if (guid == null) {
         guid = GUID.create();
      }

      IAgileTeam updatedTeam = atsServer.getAgileService().createUpdateAgileTeam(team);
      NewAgileTeam created = new NewAgileTeam();
      created.setGuid(team.getGuid());
      created.setName(team.getName());
      created.setUuid(updatedTeam.getId());
      created.getAtsTeamUuids().addAll(team.getAtsTeamUuids());
      return created;
   }

   @Override
   @Path("{teamUuid}")
   @DELETE
   @IdentityView
   public Response deleteTeam(@PathParam("teamUuid") long teamUuid) throws Exception {
      atsServer.getAgileService().deleteAgileTeam(teamUuid);
      return Response.ok().build();

   }

   /********************************
    ** Agile Team Feature
    ***********************************/

   @Override
   @Path("{teamUuid}/feature")
   @GET
   @IdentityView
   @Produces(MediaType.APPLICATION_JSON)
   public List<IAgileFeatureGroup> getFeatureGroups(@PathParam("teamUuid") long teamUuid) throws Exception {
      List<IAgileFeatureGroup> groups = new LinkedList<IAgileFeatureGroup>();
      ArtifactReadable agileTeamArt = atsServer.getArtifactByUuid(teamUuid);
      for (ArtifactReadable child : agileTeamArt.getChildren()) {
         if (child.getName().equals(AgileUtil.FEATURE_GROUP_FOLDER_NAME)) {
            for (ArtifactReadable subChild : child.getChildren()) {
               if (subChild.isOfType(AtsArtifactTypes.AgileFeatureGroup)) {
                  groups.add(atsServer.getConfigItemFactory().getAgileFeatureGroup(subChild));
               }
            }
         }
      }
      return groups;
   }

   @Override
   @Path("feature")
   @POST
   @IdentityView
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public NewAgileFeatureGroup createFeatureGroup(NewAgileFeatureGroup newFeatureGroup) throws Exception {
      // validate title
      if (!Strings.isValid(newFeatureGroup.getName())) {
         throw new OseeArgumentException("name is not valid");
      }
      if (newFeatureGroup.getTeamUuid() <= 0) {
         throw new OseeArgumentException("teamUuid is not valid");
      }

      String guid = newFeatureGroup.getGuid();
      if (guid == null) {
         guid = GUID.create();
      }

      IAgileFeatureGroup team =
         atsServer.getAgileService().createAgileFeatureGroup(newFeatureGroup.getTeamUuid(), newFeatureGroup.getName(),
            guid);
      NewAgileFeatureGroup created = new NewAgileFeatureGroup();
      created.setGuid(team.getGuid());
      created.setName(team.getName());
      created.setUuid(team.getId());
      created.setTeamUuid(team.getTeamUuid());
      return created;
   }

   @Override
   @Path("feature/{featureUuid}")
   @DELETE
   @IdentityView
   public Response deleteFeatureGroup(@PathParam("featureUuid") long featureUuid) throws Exception {
      atsServer.getAgileService().deleteAgileFeatureGroup(featureUuid);
      return Response.ok().build();

   }

   /********************************
    ** Agile Sprint
    ***********************************/
   @Override
   @Path("sprint")
   @POST
   @IdentityView
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public NewAgileSprint createSprint(NewAgileSprint newSprint) throws Exception {
      // validate title
      if (!Strings.isValid(newSprint.getName())) {
         throw new OseeArgumentException("name is not valid");
      }
      if (newSprint.getTeamUuid() <= 0) {
         throw new OseeArgumentException("teamUuid is not valid");
      }

      String guid = newSprint.getGuid();
      if (guid == null) {
         guid = GUID.create();
      }

      IAgileSprint sprint =
         atsServer.getAgileService().createAgileSprint(newSprint.getTeamUuid(), newSprint.getName(), guid);
      NewAgileSprint created = new NewAgileSprint();
      created.setGuid(sprint.getGuid());
      created.setName(sprint.getName());
      created.setUuid(sprint.getId());
      created.setTeamUuid(sprint.getTeamUuid());
      return created;
   }

   /********************************
    ** Agile Backlog
    ***********************************/
   @Override
   @Path("backlog")
   @POST
   @IdentityView
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public NewAgileBacklog createBacklog(NewAgileBacklog newBacklog) throws Exception {
      // validate title
      if (!Strings.isValid(newBacklog.getName())) {
         throw new OseeArgumentException("name is not valid");
      }
      if (newBacklog.getTeamUuid() <= 0) {
         throw new OseeArgumentException("teamUuid is not valid");
      }

      String guid = newBacklog.getGuid();
      if (guid == null) {
         guid = GUID.create();
      }

      IAgileBacklog backlog =
         atsServer.getAgileService().createAgileBacklog(newBacklog.getTeamUuid(), newBacklog.getName(), guid);
      NewAgileBacklog created = new NewAgileBacklog();
      created.setGuid(backlog.getGuid());
      created.setName(backlog.getName());
      created.setUuid(backlog.getId());
      created.setTeamUuid(backlog.getTeamUuid());
      return created;
   }

}
