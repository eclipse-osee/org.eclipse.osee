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
import org.eclipse.osee.mim.types.InterfaceSubMessageToken;

/**
 * @author Luciano T. Vaglienti
 */
@Path("submessages")
public interface InterfaceSubMessageEndpoint {

   @GET()
   @Produces(MediaType.APPLICATION_JSON)
   /**
    * Gets submessages for a given message
    *
    * @return all submessages for a given message
    */
   Collection<InterfaceSubMessageToken> getAllSubMessages();

   @POST()
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   /**
    * Creates a new sub message and relates it back to the message
    *
    * @param token sub message to create
    * @return results of operation
    */
   XResultData createNewSubMessage(InterfaceSubMessageToken token);

   @PUT()
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   /**
    * Updates a sub message
    *
    * @param token sub message to update
    * @return results of operation
    */
   XResultData updateSubMessage(InterfaceSubMessageToken token);

   @PATCH()
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   /**
    * Partial update of a sub message
    *
    * @param token sub message contents to update(id required)
    * @return results of operation
    */
   XResultData patchSubMessage(InterfaceSubMessageToken token);

   @GET()
   @Path("{id}")
   @Produces(MediaType.APPLICATION_JSON)
   /**
    * Gets a specific sub message of a given message
    *
    * @param subMessageId id of sub message to fetch
    * @return sub message that is fetched
    */
   InterfaceSubMessageToken getSubMessage(@PathParam("id") ArtifactId subMessageId);

   @PATCH()
   @Path("{id}")
   @Produces(MediaType.APPLICATION_JSON)
   /**
    * Adds a sub message relation to a message
    *
    * @param subMessageId sub message to relate
    * @return results of operation
    */
   XResultData relateSubMessage(@PathParam("id") ArtifactId subMessageId);

   @DELETE()
   @Path("{id}")
   @Produces(MediaType.APPLICATION_JSON)
   /**
    * Removes a sub message from a message
    *
    * @param subMessageId sub message to un-relate
    * @return results of operation
    */
   XResultData removeSubMessage(@PathParam("id") ArtifactId subMessageId);
}
