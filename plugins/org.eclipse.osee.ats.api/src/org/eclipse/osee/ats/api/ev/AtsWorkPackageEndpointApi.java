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
package org.eclipse.osee.ats.api.ev;

import java.util.Collection;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.util.ColorTeams;

/**
 * @author Donald D. Dunne
 */
@Path("workpackage")
public interface AtsWorkPackageEndpointApi {

   @GET
   @Path("colorteam")
   @Produces({MediaType.APPLICATION_JSON})
   ColorTeams getColorTeams();

   @GET
   @Path("{workPackageId}/workitem")
   @Produces({MediaType.APPLICATION_JSON})
   Collection<IAtsWorkItem> getWorkItems(@PathParam("workPackageId") long workPackageId);

   @PUT
   @Path("{workPackageId}")
   @Consumes({MediaType.APPLICATION_JSON})
   abstract Response setWorkPackage(@PathParam("workPackageId") long workPackageId, JaxWorkPackageData workPackageData);

   @DELETE
   @Path("{workPackageId}/workitem")
   @Consumes({MediaType.APPLICATION_JSON})
   Response deleteWorkPackageItems(@PathParam("workPackageId") long workPackageId, JaxWorkPackageData workPackageData);

}
