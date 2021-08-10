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
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.ArtifactId;
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

}
