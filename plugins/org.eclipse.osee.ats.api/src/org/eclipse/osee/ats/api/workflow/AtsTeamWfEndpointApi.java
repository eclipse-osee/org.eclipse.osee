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
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.framework.core.model.change.ChangeItem;

/**
 * @author Donald G. Dunne
 */
@Path("teamwf")
public interface AtsTeamWfEndpointApi {

   @GET
   @Path("{id}/changedata")
   @Produces({MediaType.APPLICATION_JSON})
   public List<ChangeItem> getChangeData(@PathParam("id") String id);

   @GET
   @Path("{aiId}/version")
   @Produces({MediaType.APPLICATION_JSON})
   Collection<IAtsVersion> getVersionsbyTeamDefinition(@PathParam("aiId") String aiId);

}