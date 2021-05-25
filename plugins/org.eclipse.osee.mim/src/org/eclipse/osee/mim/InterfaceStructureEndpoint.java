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
import org.eclipse.osee.mim.types.InterfaceStructureToken;

/**
 * @author Luciano T. Vaglienti
 */
@Path("structures")
public interface InterfaceStructureEndpoint {

   @GET()
   @Produces(MediaType.APPLICATION_JSON)
   /**
    * Gets structure for a given sub message
    *
    * @return all structures for a given sub message
    */
   Collection<InterfaceStructureToken> getAllStructures();

   @POST()
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   /**
    * Creates a new structure and relates it to sub message
    *
    * @param token structure to create
    * @return results of operation
    */
   XResultData createNewStructure(InterfaceStructureToken token);

   @PUT()
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   /**
    * Updates a structure
    *
    * @param token structure to update
    * @return results of operation
    */
   XResultData updateStructure(InterfaceStructureToken token);

   @PATCH()
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   /**
    * Partial update of a structure
    *
    * @param token structure contents to update(id required)
    * @return results of operation
    */
   XResultData patchStructure(InterfaceStructureToken token);

   @GET()
   @Path("{id}")
   @Produces(MediaType.APPLICATION_JSON)
   /**
    * Gets a specific structure of a given sub message
    *
    * @param structureId id of structure to fetch
    * @return structure that is fetched
    */
   InterfaceStructureToken getStructure(@PathParam("id") ArtifactId structureId);

   @PATCH()
   @Path("{id}")
   @Produces(MediaType.APPLICATION_JSON)
   /**
    * Adds a structure relation to a sub message
    *
    * @param structureId structure to relate
    * @return results of operation
    */
   XResultData relateStructure(@PathParam("id") ArtifactId structureId);

   @DELETE()
   @Path("{id}")
   @Produces(MediaType.APPLICATION_JSON)
   /**
    * Removes a structure from a sub message
    *
    * @param structureId structure to un-relate
    * @return results of operation
    */
   XResultData removeStructure(@PathParam("id") ArtifactId structureId);

}
