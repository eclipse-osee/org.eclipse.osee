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
import org.eclipse.osee.mim.types.InterfaceMessageToken;

/**
 * @author Luciano T. Vaglienti
 */
@Path("messages")
public interface InterfaceMessageEndpoint {

   @GET()
   @Produces(MediaType.APPLICATION_JSON)
   /**
    * Gets all messages and related submessages
    *
    * @return messages and submessages
    */
   Collection<InterfaceMessageToken> getAllMessages();

   @POST()
   @Produces(MediaType.APPLICATION_JSON)
   /**
    * Adds an interface message w/o relations
    *
    * @param token interface message to add
    * @return result of operation
    */
   XResultData addMessage(InterfaceMessageToken token);

   @PUT()
   @Produces(MediaType.APPLICATION_JSON)
   /**
    * Updates an interface message w/o updating relations
    *
    * @param token interface message to update and params to update
    * @return result of operation
    */
   XResultData updateMessage(InterfaceMessageToken token);

   @PATCH()
   @Produces(MediaType.APPLICATION_JSON)
   /**
    * Performs a partial update on an interface message w/o updating relations
    *
    * @param token interface message to update, requires id
    * @return result of operation
    */
   XResultData patchMessage(InterfaceMessageToken token);

   @GET()
   @Path("{id}")
   @Produces(MediaType.APPLICATION_JSON)
   /**
    * Gets a specific interface message based on id
    *
    * @param messageId message id to look for
    * @return interface message that is found
    */
   InterfaceMessageToken getInterfaceMessage(@PathParam("id") ArtifactId messageId);

   @DELETE()
   @Path("{id}")
   @Produces(MediaType.APPLICATION_JSON)
   /**
    * Removes a specific interface message based on id
    *
    * @param messageId message id to look for removal purposes
    * @return result of operation
    */
   XResultData removeInterfaceMessage(@PathParam("id") ArtifactId messageId);
}
