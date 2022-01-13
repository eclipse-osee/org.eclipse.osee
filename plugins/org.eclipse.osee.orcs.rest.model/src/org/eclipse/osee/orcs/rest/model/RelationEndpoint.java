/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.orcs.rest.model;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.RelationTypeToken;

/**
 * @author Hugo Trejo, Torin Grenda, David Miller
 */
@Path("relation")
public interface RelationEndpoint {

   /**
    * Find the next level children artifacts of the given artifact
    *
    * @param artifact the parent artifact for which children artifacts are desired
    * @return list of children artifact tokens
    */
   @GET
   @Path("getRelatedHierarchy/{artifact}")
   @Produces({MediaType.APPLICATION_JSON})
   List<ArtifactToken> getRelatedHierarchy(@PathParam("artifact") ArtifactId artifact);

   @POST
   @Path("createRelationByType/sideA/{sideA}/sideB/{sideB}/relationTypeToken/{relationType}")
   @Consumes({MediaType.TEXT_PLAIN})
   Response createRelationByType(@PathParam("sideA") ArtifactId sideA, @PathParam("sideB") ArtifactId sideB, @PathParam("relationType") RelationTypeToken relationType);

}