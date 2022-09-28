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
import org.eclipse.osee.mim.types.ElementPosition;
import org.eclipse.osee.mim.types.InterfaceStructureElementToken;
import org.eclipse.osee.mim.types.InterfaceStructureElementTokenWithPath;

/**
 * @author Luciano T. Vaglienti
 */
@Path("elements")
public interface InterfaceElementSearchEndpoint {

   @GET()
   @Path("{a:filter|types/filter}")
   @Produces(MediaType.APPLICATION_JSON)
   /**
    * Same as /types/ GET() returns a *blank* filtered request
    *
    * @return list of elements
    */
   Collection<InterfaceStructureElementToken> getElements(@QueryParam("pageNum") long pageNum, @QueryParam("count") long pageSize);

   @GET()
   @Path("filter/{filter}")
   @Produces(MediaType.APPLICATION_JSON)
   /**
    * Gets List of filtered Platform Types
    *
    * @return List of elements
    */
   Collection<InterfaceStructureElementToken> getElements(@PathParam("filter") String filter, @QueryParam("pageNum") long pageNum, @QueryParam("count") long pageSize);

   /**
    * Gets list of elements utilizing platform type
    *
    * @param platformTypeId platform type to look for
    * @return list of elements
    */
   @GET()
   @Path("getType/{typeId}")
   @Produces(MediaType.APPLICATION_JSON)
   Collection<InterfaceStructureElementToken> getElementsOfType(@PathParam("typeId") ArtifactId platformTypeId);

   /**
    * Finds the structure, submessages, and message an element belongs to, for navigation purposes todo.
    *
    * @param elementId element to find the structure for
    * @return relationship structure that contains the element
    */
   @GET()
   @Path("{id}/find")
   @Produces(MediaType.APPLICATION_JSON)
   ElementPosition findElement(@PathParam("id") ArtifactId elementId);

   @GET
   @Path("types/filter/")
   @Produces(MediaType.APPLICATION_JSON)
   Collection<InterfaceStructureElementTokenWithPath> getElementsByType();

   @GET()
   @Path("types/filter/{filter}")
   @Produces(MediaType.APPLICATION_JSON)
   Collection<InterfaceStructureElementTokenWithPath> getElementsByType(@PathParam("filter") String filter);
}
