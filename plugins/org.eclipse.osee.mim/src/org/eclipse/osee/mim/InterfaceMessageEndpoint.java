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
   Collection<InterfaceMessageToken> getAllMessages(@QueryParam("filter") String filter, @QueryParam("viewId") ArtifactId viewId, @QueryParam("pageNum") long pageNum, @QueryParam("count") long pageSize, @QueryParam("orderByAttributeType") AttributeTypeToken orderByAttributeType);

   @GET()
   @Path("{id}")
   @Produces(MediaType.APPLICATION_JSON)
   /**
    * Gets a specific interface message based on id
    *
    * @param messageId message id to look for
    * @return interface message that is found
    */
   InterfaceMessageToken getInterfaceMessage(@PathParam("id") ArtifactId messageId, @QueryParam("viewId") ArtifactId viewId);

}
