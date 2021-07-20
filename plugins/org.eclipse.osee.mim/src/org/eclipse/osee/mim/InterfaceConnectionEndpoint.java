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
import org.eclipse.osee.mim.types.InterfaceConnection;

/**
 * @author Luciano T. Vaglienti
 */
@Path("connections")
public interface InterfaceConnectionEndpoint {

   @GET()
   @Produces(MediaType.APPLICATION_JSON)
   /**
    * Gets all connections
    *
    * @return all connections
    */
   Collection<InterfaceConnection> getAllConnections();

   @POST()
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   /**
    * Creates a new Connection
    *
    * @param ConnectionToCreate Connection to insert into db
    * @return results of operation
    */
   XResultData createNewConnection(InterfaceConnection ConnectionToCreate);

   @PUT()
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   /**
    * Replaces a Connection with a Connection, requires matching id
    *
    * @param ConnectionToUpdate Connection to replace with
    * @return results of operation
    */
   XResultData updateConnection(InterfaceConnection ConnectionToUpdate);

   @PATCH()
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   /**
    * Updates the contents of a Connection, requires matching id
    *
    * @param ConnectionToUpdate Connection contents to update
    * @return results of operation
    */
   XResultData patchConnection(InterfaceConnection ConnectionToPatch);

   @GET()
   @Path("{id}")
   @Produces(MediaType.APPLICATION_JSON)
   /**
    * Fetches a specific Connection
    *
    * @param ConnectionId id of Connection to fetch
    * @return Connection
    */
   InterfaceConnection getConnection(@PathParam("id") ArtifactId ConnectionId);

   @DELETE()
   @Path("{id}")
   @Produces(MediaType.APPLICATION_JSON)
   /**
    * Deletes an interface Connection
    *
    * @param ConnectionId id of Connection to remove
    * @return result of operation
    */
   XResultData deleteConnection(@PathParam("id") ArtifactId ConnectionId);
}
