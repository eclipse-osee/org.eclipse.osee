/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.config;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.rest.util.AbstractConfigResource;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.util.NamedComparator;
import org.eclipse.osee.framework.jdk.core.util.SortOrder;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Donald G. Dunne
 */
@Path("team")
public class TeamResource extends AbstractConfigResource {

   public TeamResource(AtsApi atsApi, OrcsApi orcsApi) {
      super(AtsArtifactTypes.TeamDefinition, atsApi, orcsApi);
   }

   @GET
   @Path("{id}/Version")
   @Produces(MediaType.APPLICATION_JSON)
   public List<String> getVersionNames(@PathParam("id") ArtifactId teamId) {
      List<String> versions = new LinkedList<>();
      IAtsTeamDefinition teamDef = atsApi.getQueryService().getConfigItem(teamId);
      for (IAtsVersion version : atsApi.getVersionService().getVersions(teamDef)) {
         versions.add(version.getName());
      }
      return versions;
   }

   @Path("{id}/ai")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public List<IAtsActionableItem> getActionableAis(@PathParam("id") ArtifactId teamId) {
      IAtsTeamDefinition teamDef = atsApi.getQueryService().getConfigItem(teamId);
      if (teamDef != null) {
         List<IAtsActionableItem> ais = atsApi.getActionableItemService().getActiveActionableItemsAndChildren(teamDef);
         Collections.sort(ais, new NamedComparator(SortOrder.ASCENDING));
         return ais;
      }
      return Collections.emptyList();
   }

}
