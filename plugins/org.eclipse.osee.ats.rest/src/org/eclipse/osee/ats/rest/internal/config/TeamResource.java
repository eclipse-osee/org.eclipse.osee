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

import java.util.LinkedList;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.rest.util.AbstractConfigResource;

/**
 * Donald G. Dunne
 */
@Path("team")
public class TeamResource extends AbstractConfigResource {

   public TeamResource(IAtsServices services) {
      super(AtsArtifactTypes.TeamDefinition, services);
   }

   @GET
   @Path("{id}/Version")
   @Produces(MediaType.APPLICATION_JSON)
   public List<String> getVersionNames(@PathParam("id") Long id) throws Exception {
      List<String> versions = new LinkedList<>();
      IAtsTeamDefinition teamDef = services.getConfigItem(id);
      for (IAtsVersion version : services.getVersionService().getVersions(teamDef)) {
         versions.add(version.getName());
      }
      return versions;
   }

}
