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
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.mim.types.InterfaceNode;

/**
 * @author Luciano T. Vaglienti
 */
@Path("nodes")
public interface InterfaceNodeEndpoint {

   @GET()
   @Produces(MediaType.APPLICATION_JSON)
   /**
    * Gets all nodes
    *
    * @return all nodes
    */
   Collection<InterfaceNode> getAllNodes();

   @POST()
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   /**
    * Creates a new node
    *
    * @param nodeToCreate node to insert into db
    * @return results of operation
    */
   XResultData createNewNode(InterfaceNode nodeToCreate);

   @PUT()
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   /**
    * Replaces a node with a node, requires matching id
    *
    * @param nodeToUpdate node to replace with
    * @return results of operation
    */
   XResultData updateNode(InterfaceNode nodeToUpdate);

   @PATCH()
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   /**
    * Updates the contents of a node, requires matching id
    *
    * @param nodeToUpdate node contents to update
    * @return results of operation
    */
   XResultData patchNode(InterfaceNode nodeToPatch);

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

   @DELETE()
   @Path("{id}")
   @Produces(MediaType.APPLICATION_JSON)
   /**
    * Deletes an interface node
    * 
    * @param nodeId id of node to remove
    * @return result of operation
    */
   XResultData deleteNode(@PathParam("id") ArtifactId nodeId);
}
