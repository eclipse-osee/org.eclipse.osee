/*********************************************************************
 * Copyright (c) 2023 Boeing
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
/**
@author gg949e
*/
package org.eclipse.osee.mim;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("count")
public interface InterfaceStructureCountEndpoint {

   @GET()
   @Produces(MediaType.APPLICATION_JSON)
   /**
    * Gets count of filtered structures
    *
    * @return List of platform types
    */
   int getStructures(@QueryParam("filter") String filter);

   @GET
   @Path("name")
   @Produces(MediaType.APPLICATION_JSON)
   int getStructuresByNameCount(@QueryParam("name") String name);

}
