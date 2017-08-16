/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.workflow;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.model.change.CompareResults;

/**
 * @author Donald G. Dunne
 */
@Path("teamwf")
public interface AtsTeamWfEndpointApi {

   @GET
   @Path("{id}/changedata")
   @Produces({MediaType.APPLICATION_JSON})
   public CompareResults getChangeData(@PathParam("id") String id);

}