/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;
import org.eclipse.osee.mim.types.InterfaceSubMessageToken;

/**
 * @author Luciano T. Vaglienti
 */
@Path("filter")
@Swagger
public interface InterfaceSubMessageFilterEndpoint {

   @GET()
   @Produces(MediaType.APPLICATION_JSON)
   Collection<InterfaceSubMessageToken> getSubMessages(@QueryParam("pageNum") long pageNum,
      @QueryParam("count") long pageSize, @QueryParam("orderByAttributeType") AttributeTypeToken orderByAttributeType);

   @GET()
   @Path("{filter}")
   @Produces(MediaType.APPLICATION_JSON)
   Collection<InterfaceSubMessageToken> getSubMessages(@PathParam("filter") String filter,
      @QueryParam("pageNum") long pageNum, @QueryParam("count") long pageSize,
      @QueryParam("orderByAttributeType") AttributeTypeToken orderByAttributeType);

   @GET()
   @Path("name")
   @Produces(MediaType.APPLICATION_JSON)
   Collection<InterfaceSubMessageToken> getSubMessagesByName(@QueryParam("name") String name,
      @QueryParam("pageNum") long pageNum, @QueryParam("count") long pageSize,
      @QueryParam("orderByAttributeType") AttributeTypeToken orderByAttributeType);

   @GET
   @Path("name/count")
   @Produces(MediaType.APPLICATION_JSON)
   int getSubMessagesByNameCount(@QueryParam("name") String name);
}
