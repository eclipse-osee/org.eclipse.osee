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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author Donald G. Dunne
 */
@Path("agile")
public interface AgileEndpointApi {

   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public String get();

   @Path("team")
   @GET
   public AgileTeamEndpointApi team() throws Exception;

}
