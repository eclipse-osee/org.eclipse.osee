/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.ats.rest.internal.config;

import java.util.Collections;
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
import org.eclipse.osee.ats.rest.util.AbstractConfigResource;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.Named;
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
      IAtsTeamDefinition teamDef = atsApi.getConfigService().getConfigurations().getIdToTeamDef().get(teamId.getId());
      if (teamDef == null) {
         teamDef = atsApi.getQueryService().getConfigItem(teamId);
      }
      return Named.getNames(atsApi.getVersionService().getVersions(teamDef));
   }

   @Path("{id}/ai")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public List<IAtsActionableItem> getActionableAis(@PathParam("id") ArtifactId teamId) {
      IAtsTeamDefinition teamDef = atsApi.getConfigService().getConfigurations().getIdToTeamDef().get(teamId.getId());
      if (teamDef == null) {
         teamDef = atsApi.getQueryService().getConfigItem(teamId);
      }
      if (teamDef != null) {
         List<IAtsActionableItem> ais = atsApi.getActionableItemService().getActiveActionableItemsAndChildren(teamDef);
         Collections.sort(ais, new NamedComparator(SortOrder.ASCENDING));
         return ais;
      }
      return Collections.emptyList();
   }

}
