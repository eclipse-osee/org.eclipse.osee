/*********************************************************************
 * Copyright (c) 2021 Boeing
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
package org.eclipse.osee.mim;

import java.util.Collection;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;
import org.eclipse.osee.mim.types.InterfaceStructureToken;

/**
 * @author Luciano T. Vaglienti
 */
@Path("structures")
@Swagger
public interface InterfaceStructureSearchEndpoint {

   @GET()
   @Produces(MediaType.APPLICATION_JSON)
   Collection<InterfaceStructureToken> getStructures(@QueryParam("filter") String filter,
      @QueryParam("pageNum") long pageNum, @QueryParam("count") long pageSize,
      @QueryParam("orderByAttributeType") AttributeTypeToken orderByAttributeType);
  
   @GET
   @Path("name")
   @Produces(MediaType.APPLICATION_JSON)
   Collection<InterfaceStructureToken> getStructuresByName(@QueryParam("name") String name,
      @QueryParam("pageNum") long pageNum, @QueryParam("count") long pageSize);

   @GET
   @Path("name/count")
   @Produces(MediaType.APPLICATION_JSON)
   int getStructuresByNameCount(@QueryParam("name") String name);
  
}
