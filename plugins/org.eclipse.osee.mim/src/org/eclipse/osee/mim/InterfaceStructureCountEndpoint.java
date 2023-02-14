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
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;

@Path("count")
public interface InterfaceStructureCountEndpoint {

   @GET()
   @Produces(MediaType.APPLICATION_JSON)
   /**
    * Same as /types/ GET() returns a *blank* filtered request
    *
    * @return list of platform Types
    */
   int getStructures(@QueryParam("pageNum") long pageNum, @QueryParam("count") long pageSize, @QueryParam("orderByAttributeType") AttributeTypeToken orderByAttributeType);

   @GET()
   @Path("{filter}")
   @Produces(MediaType.APPLICATION_JSON)
   /**
    * Gets List of filtered Platform Types
    *
    * @return List of platform types
    */
   int getStructures(@PathParam("filter") String filter, @QueryParam("pageNum") long pageNum, @QueryParam("count") long pageSize, @QueryParam("orderByAttributeType") AttributeTypeToken orderByAttributeType);
}
