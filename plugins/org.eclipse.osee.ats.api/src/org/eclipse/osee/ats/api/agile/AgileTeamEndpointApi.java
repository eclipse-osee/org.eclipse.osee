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
package org.eclipse.osee.ats.api.agile;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.osee.jaxrs.mvc.IdentityView;

/**
 * @author Donald G. Dunne
 */
@Path("agile/team")
public interface AgileTeamEndpointApi {

   @POST
   @IdentityView
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public NewAgileTeam createTeam(NewAgileTeam newTeam) throws Exception;

   @Path("feature")
   @POST
   @IdentityView
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public NewAgileFeatureGroup createFeatureGroup(NewAgileFeatureGroup newFeatureGroup) throws Exception;

   @Path("{teamUuid}/feature")
   @GET
   @IdentityView
   @Produces(MediaType.APPLICATION_JSON)
   public List<IAgileFeatureGroup> getFeatureGroups(long teamUuid) throws Exception;

   @Path("feature/{featureUuid}")
   @DELETE
   @IdentityView
   public Response deleteFeatureGroup(long featureUuid) throws Exception;

   @Path("{teamUuid}")
   @DELETE
   @IdentityView
   Response deleteTeam(long teamUuid) throws Exception;

}