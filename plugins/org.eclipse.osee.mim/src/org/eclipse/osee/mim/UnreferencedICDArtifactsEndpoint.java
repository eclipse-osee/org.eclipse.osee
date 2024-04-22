/*********************************************************************
 * Copyright (c) 2024 Boeing
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
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;
import org.eclipse.osee.mim.types.InterfaceMessageToken;
import org.eclipse.osee.mim.types.InterfaceStructureElementToken;
import org.eclipse.osee.mim.types.InterfaceStructureToken;
import org.eclipse.osee.mim.types.InterfaceSubMessageToken;
import org.eclipse.osee.mim.types.PlatformTypeToken;

@Path("unreferenced")
@Swagger
public interface UnreferencedICDArtifactsEndpoint {

   @GET()
   @Path("types")
   @Produces(MediaType.APPLICATION_JSON)
   Collection<PlatformTypeToken> getPlatformTypes(@QueryParam("filter") String filter,
      @QueryParam("pageNum") long pageNum, @QueryParam("count") long pageSize);

   @GET()
   @Path("types/count")
   @Produces(MediaType.APPLICATION_JSON)
   int getPlatformTypesCount(@QueryParam("filter") String filter);

   @GET()
   @Path("elements")
   @Produces(MediaType.APPLICATION_JSON)
   Collection<InterfaceStructureElementToken> getElements(@QueryParam("filter") String filter,
      @QueryParam("pageNum") long pageNum, @QueryParam("count") long pageSize);

   @GET()
   @Path("elements/count")
   @Produces(MediaType.APPLICATION_JSON)
   int getElementsCount(@QueryParam("filter") String filter);

   @GET()
   @Path("structures")
   @Produces(MediaType.APPLICATION_JSON)
   Collection<InterfaceStructureToken> getStructures(@QueryParam("filter") String filter,
      @QueryParam("pageNum") long pageNum, @QueryParam("count") long pageSize);

   @GET()
   @Path("structures/count")
   @Produces(MediaType.APPLICATION_JSON)
   int getStructuresCount(@QueryParam("filter") String filter);

   @GET()
   @Path("submessages")
   @Produces(MediaType.APPLICATION_JSON)
   Collection<InterfaceSubMessageToken> getSubmessages(@QueryParam("filter") String filter,
      @QueryParam("pageNum") long pageNum, @QueryParam("count") long pageSize);

   @GET()
   @Path("submessages/count")
   @Produces(MediaType.APPLICATION_JSON)
   int getSubmessagesCount(@QueryParam("filter") String filter);

   @GET()
   @Path("messages")
   @Produces(MediaType.APPLICATION_JSON)
   Collection<InterfaceMessageToken> getMessages(@QueryParam("filter") String filter,
      @QueryParam("pageNum") long pageNum, @QueryParam("count") long pageSize);

   @GET()
   @Path("messages/count")
   @Produces(MediaType.APPLICATION_JSON)
   int getMessagesCount(@QueryParam("filter") String filter);

}
