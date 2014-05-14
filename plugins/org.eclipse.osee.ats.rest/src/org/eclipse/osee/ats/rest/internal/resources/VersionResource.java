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
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.utility.RestUtil;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Donald G. Dunne
 */
@Path("version")
public final class VersionResource {

   private final OrcsApi orcsApi;

   public VersionResource(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public String get() throws Exception {
      ResultSet<ArtifactReadable> artifacts =
         orcsApi.getQueryFactory(null).fromBranch(AtsUtilCore.getAtsBranch()).andTypeEquals(AtsArtifactTypes.Version).getResults();
      JSONArray jsonArray = RestUtil.getDefaultJSonArray(artifacts);
      return RestUtil.jsonToPretty(jsonArray, true);
   }

   @GET
   @Path("{uuid}")
   @Produces(MediaType.APPLICATION_JSON)
   public String getVersion(@PathParam("uuid") int uuid) throws Exception {
      ArtifactReadable verArt =
         orcsApi.getQueryFactory(null).fromBranch(AtsUtilCore.getAtsBranch()).andLocalId(Integer.valueOf(uuid)).getResults().getExactlyOne();
      JSONObject version = RestUtil.getDefaultJSon(verArt);
      version.put("workflows",
         RestUtil.getDefaultJSonArray(verArt.getRelated(AtsRelationTypes.TeamWorkflowTargetedForVersion_Workflow)));
      return RestUtil.jsonToPretty(version, true);
   }

}
