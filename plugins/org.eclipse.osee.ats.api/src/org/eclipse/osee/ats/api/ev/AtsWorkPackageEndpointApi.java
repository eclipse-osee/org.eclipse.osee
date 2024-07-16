/*********************************************************************
 * Copyright (c) 2015 Boeing
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
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.util.ColorTeams;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald D. Dunne
 */
@Path("workpackage")
@Swagger
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
   @Produces({MediaType.APPLICATION_JSON})
   XResultData setWorkPackage(@PathParam("workPackageId") long workPackageId, JaxWorkPackageData workPackageData);

   @DELETE
   @Path("{workPackageId}/workitem")
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   XResultData deleteWorkPackageItems(@PathParam("workPackageId") long workPackageId,
      JaxWorkPackageData workPackageData);

}
