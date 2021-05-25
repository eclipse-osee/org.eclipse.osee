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
import org.eclipse.osee.mim.types.InterfaceStructureElementToken;

/**
 * @author Luciano T. Vaglienti
 */
@Path("elements")
public interface InterfaceElementEndpoint {
   @GET()
   @Produces(MediaType.APPLICATION_JSON)
   /**
    * Gets element for a given structure
    *
    * @return all elements for a given structure
    */
   Collection<InterfaceStructureElementToken> getAllElements();

   @POST()
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   /**
    * Creates a new element and relates it to structure
    *
    * @param token element to create
    * @return results of operation
    */
   XResultData createNewElement(InterfaceStructureElementToken token);

   @PUT()
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   /**
    * Updates a element
    *
    * @param token element to update
    * @return results of operation
    */
   XResultData updateElement(InterfaceStructureElementToken token);

   @PATCH()
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   /**
    * Partial update of a element
    *
    * @param token element contents to update(id required)
    * @return results of operation
    */
   XResultData patchElement(InterfaceStructureElementToken token);

   @GET()
   @Path("{id}")
   @Produces(MediaType.APPLICATION_JSON)
   /**
    * Gets a specific element of a given structure
    *
    * @param elementId id of element to fetch
    * @return element that is fetched
    */
   InterfaceStructureElementToken getElement(@PathParam("id") ArtifactId elementId);

   @PATCH()
   @Path("{id}")
   @Produces(MediaType.APPLICATION_JSON)
   /**
    * Adds a element relation to a structure
    *
    * @param elementId element to relate
    * @return results of operation
    */
   XResultData relateElement(@PathParam("id") ArtifactId elementId);

   @DELETE()
   @Path("{id}")
   @Produces(MediaType.APPLICATION_JSON)
   /**
    * Removes a element from a structure
    *
    * @param elementId element to un-relate
    * @return results of operation
    */
   XResultData removeElement(@PathParam("id") ArtifactId elementId);

   @PATCH()
   @Path("{id}/setType/{typeId}")
   @Produces(MediaType.APPLICATION_JSON)
   XResultData relatePlatformType(@PathParam("id") ArtifactId elementId, @PathParam("typeId") ArtifactId platformTypeId);
}
