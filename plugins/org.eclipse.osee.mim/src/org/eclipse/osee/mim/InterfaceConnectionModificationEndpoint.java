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

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.mim.types.InterfaceConnection;

/**
 * @author Luciano T. Vaglienti This endpoint handles creation/deletion of connections with their related nodes.
 */
@Path("connections")
public interface InterfaceConnectionModificationEndpoint {

   @POST()
   @Path("{type}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   /**
    * Creates a new Connection
    *
    * @param ConnectionToCreate Connection to insert into db
    * @param type type of connection primary/secondary
    * @return results of operation
    */
   XResultData createNewConnection(InterfaceConnection ConnectionToCreate, @PathParam("type") String type);

   @PATCH()
   @Path("{id}/{type}")
   @Produces(MediaType.APPLICATION_JSON)
   /**
    * Relates a connection to another node
    * 
    * @param connectionId connection to relate
    * @param type type of relation i.e. primary/secondary
    * @return results of operation
    */
   XResultData relateConnection(@PathParam("id") ArtifactId connectionId, @PathParam("type") String type);

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
