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
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;
import org.eclipse.osee.mim.types.InterfaceNode;

/**
 * @author Luciano T. Vaglienti
 */
@Path("nodes")
@Swagger
public interface InterfaceNodeEndpoint {

   @GET()
   @Produces(MediaType.APPLICATION_JSON)
   /**
    * Gets all nodes
    *
    * @return all nodes
    */
   Collection<InterfaceNode> getAllNodes(@QueryParam("pageNum") long pageNum, @QueryParam("count") long pageSize,
      @QueryParam("orderByAttributeType") AttributeTypeToken orderByAttributeType);

   @GET()
   @Path("{id}")
   @Produces(MediaType.APPLICATION_JSON)
   /**
    * Fetches a specific node
    *
    * @param nodeId id of node to fetch
    * @return node
    */
   InterfaceNode getNode(@PathParam("id") ArtifactId nodeId);

   @GET()
   @Path("connection/{id}")
   @Produces(MediaType.APPLICATION_JSON)
   /**
    * Gets all nodes related to the specified connection
    *
    * @param connectionId
    * @return nodes related to connection
    */
   Collection<InterfaceNode> getNodes(@PathParam("id") ArtifactId connectionId);

   @GET
   @Path("name")
   Collection<InterfaceNode> getNodesByName(@QueryParam("name") String name,
      @QueryParam("connectionId") ArtifactId connectionId, @QueryParam("pageNum") long pageNum,
      @QueryParam("count") long pageSize);

   @GET
   @Path("name/count")
   int getNodesByNameCount(@QueryParam("name") String name);

}
