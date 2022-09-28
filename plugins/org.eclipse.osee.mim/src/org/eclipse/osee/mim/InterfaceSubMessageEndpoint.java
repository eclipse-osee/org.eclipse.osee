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
   Collection<InterfaceSubMessageToken> getAllSubMessages(@QueryParam("pageNum") long pageNum, @QueryParam("count") long pageSize);

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

}
