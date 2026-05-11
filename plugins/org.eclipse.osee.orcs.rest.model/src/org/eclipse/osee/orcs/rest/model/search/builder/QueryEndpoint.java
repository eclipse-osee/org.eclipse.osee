/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.orcs.rest.model.search.builder;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * @author Donald G. Dunne
 */
@Path("query")
public interface QueryEndpoint {

   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes({MediaType.APPLICATION_JSON})
   List<ArtifactReadable> query(QueryBuilder queryBuilder);

   @POST
   @Path("ids")
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes({MediaType.APPLICATION_JSON})
   /**
    * @return asArtifacts()
    */
   List<ArtifactId> queryIds(QueryBuilder queryBuilder);

   @POST
   @Path("idslegacy")
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes({MediaType.APPLICATION_JSON})
   /**
    * @return getResults().getList()
    */
   List<ArtifactId> queryIdsLegacy(QueryBuilder queryBuilder);

}
