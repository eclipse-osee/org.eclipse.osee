/*********************************************************************
 * Copyright (c) 2024 Boeing
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
import org.eclipse.osee.accessor.types.ArtifactAccessorResultWithoutGammas;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;

/**
 * @author Luciano T. Vaglienti
 */
@Path("structureCategories")
@Swagger
public interface InterfaceStructureCategoryEndpoint {
   @GET()
   @Produces(MediaType.APPLICATION_JSON)
   /**
    * @return all rates matching criteria
    */
   Collection<ArtifactAccessorResultWithoutGammas> getAllStructureCategories(@QueryParam("filter") String filter,
      @QueryParam("viewId") ArtifactId viewId, @QueryParam("pageNum") long pageNum, @QueryParam("count") long pageSize,
      @QueryParam("orderByAttributeType") AttributeTypeToken orderByAttributeType);

   @GET()
   @Path("count")
   @Produces(MediaType.APPLICATION_JSON)
   /**
    * @return all rates matching criteria
    */
   int getCount(@QueryParam("filter") String filter, @QueryParam("viewId") ArtifactId viewId);

   @GET()
   @Path("{id}")
   @Produces(MediaType.APPLICATION_JSON)
   /**
    * Gets a specific unit.
    */
   ArtifactAccessorResultWithoutGammas getStructureCategory(@PathParam("id") ArtifactId rateId);

}
