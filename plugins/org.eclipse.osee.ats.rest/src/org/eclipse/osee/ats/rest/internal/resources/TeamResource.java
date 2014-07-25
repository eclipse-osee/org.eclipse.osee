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
package org.eclipse.osee.ats.rest.internal.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.impl.IAtsServer;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.utility.RestUtil;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Donald G. Dunne
 */
@Path("team")
public final class TeamResource {

   private final IAtsServer atsServer;

   public TeamResource(IAtsServer atsServer) {
      this.atsServer = atsServer;
   }

   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public String get() throws Exception {
      ResultSet<ArtifactReadable> artifacts =
         atsServer.getQuery().andTypeEquals(AtsArtifactTypes.TeamDefinition).getResults();
      JSONArray jsonArray = RestUtil.getDefaultJSonArray(artifacts);
      return RestUtil.jsonToPretty(jsonArray, true);
   }

   @GET
   @Path("{uuid}")
   @Produces(MediaType.APPLICATION_JSON)
   public String getTeam(@PathParam("uuid") int uuid) throws Exception {
      ArtifactReadable teamArt = atsServer.getQuery().andLocalId(Integer.valueOf(uuid)).getResults().getExactlyOne();
      JSONObject team = RestUtil.getDefaultJSon(teamArt);
      team.put("versions",
         RestUtil.getDefaultJSonArray(teamArt.getRelated(AtsRelationTypes.TeamDefinitionToVersion_Version)));
      return RestUtil.jsonToPretty(team, true);
   }

}
