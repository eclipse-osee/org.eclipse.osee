/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;
import org.eclipse.osee.mim.types.CrossReference;

@Path("crossReference")
@Swagger
public interface CrossReferenceEndpoint {

   @GET()
   @Produces(MediaType.APPLICATION_JSON)
   Collection<CrossReference> getAll(@QueryParam("connectionId") @DefaultValue("-1") ArtifactId connectionId,
      @QueryParam("filter") @DefaultValue("") String filter, @QueryParam("pageNum") long pageNum,
      @QueryParam("count") long pageSize, @QueryParam("orderByAttributeType") AttributeTypeToken orderByAttributeType,
      @QueryParam("viewId") ArtifactId viewId);

   @GET()
   @Path("count")
   @Produces(MediaType.APPLICATION_JSON)
   /**
    * Gets count of cross references returned
    */
   int getCount(@QueryParam("connectionId") @DefaultValue("-1") ArtifactId connectionId,
      @QueryParam("filter") @DefaultValue("") String filter, @QueryParam("viewId") ArtifactId viewId);

   @GET()
   @Path("{id}")
   @Produces(MediaType.APPLICATION_JSON)
   CrossReference get(@PathParam("id") ArtifactId crossReferenceId);
}
