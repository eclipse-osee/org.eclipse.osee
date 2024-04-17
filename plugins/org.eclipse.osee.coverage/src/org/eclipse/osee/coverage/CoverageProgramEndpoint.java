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

package org.eclipse.osee.coverage;

import java.util.Collection;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.coverage.internal.CoverageProgramToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;

/**
 * @author Stephen J. Molaro
 */
@Path("set")
@Swagger
public interface CoverageProgramEndpoint {
   @GET()
   @Produces(MediaType.APPLICATION_JSON)
   Collection<CoverageProgramToken> getAllScriptSets(@QueryParam("filter") String filter,
      @QueryParam("viewId") ArtifactId viewId, @QueryParam("pageNum") long pageNum, @QueryParam("count") long pageSize,
      @QueryParam("orderByAttributeType") AttributeTypeToken orderByAttributeType,
      @QueryParam("activeOnly") boolean activeOnly);

   @GET()
   @Path("{id}")
   @Produces(MediaType.APPLICATION_JSON)
   CoverageProgramToken getCoverageProgram(@PathParam("id") ArtifactId coverageProgramId);

   @GET()
   @Path("count")
   @Produces(MediaType.APPLICATION_JSON)
   int getCount(@QueryParam("filter") String filter, @QueryParam("viewId") ArtifactId viewId);

}