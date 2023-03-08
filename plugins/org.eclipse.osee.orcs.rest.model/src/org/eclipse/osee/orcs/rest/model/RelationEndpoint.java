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
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
    * @param view the view to apply during the query for the related hierarchy, default is SENTINEL
    * @return list of children artifact tokens
    */
   @GET
   @Path("getRelatedHierarchy/{artifact}")
   @Produces({MediaType.APPLICATION_JSON})
   List<ArtifactToken> getRelatedHierarchy(@PathParam("artifact") ArtifactId artifact, @DefaultValue("-1") @QueryParam("view") ArtifactId view);

   /**
    * Find the recursive related artifacts of the given artifact
    *
    * @param artifact the parent artifact for which children artifacts are desired
    * @param relationType the relation type to follow - currently only side A to side B
    * @param view the view to apply during the query for the related hierarchy, default is SENTINEL
    * @return list of related artifact tokens
    */
   @GET
   @Path("getRelatedRecursive/{artifact}/relationTypeToken/{relationType}")
   @Produces({MediaType.APPLICATION_JSON})
   List<ArtifactToken> getRelatedRecursive(@PathParam("artifact") ArtifactId artifact, @PathParam("relationType") RelationTypeToken relationType, @DefaultValue("-1") @QueryParam("view") ArtifactId view);

   /**
    * Create a relation between the given artifacts of the given relation type
    *
    * @param sideA the sideA artifact for the relation
    * @param sideB the sideB artifact for the relation
    * @param relationType the relation type to create between the two artifacts
    * @return list of related artifact tokens
    */
   @POST
   @Path("createRelationByType/sideA/{sideA}/sideB/{sideB}/relationTypeToken/{relationType}")
   @Consumes({MediaType.TEXT_PLAIN})
   Response createRelationByType(@PathParam("sideA") ArtifactId sideA, @PathParam("sideB") ArtifactId sideB, @PathParam("relationType") RelationTypeToken relationType);

   /**
    * Convert specific relation on an artifact to its newer version
    *
    * @param artId: the Artifact whose relations are being converted
    * @param oldRelationType: relation type which uses osee_relation_link_id
    * @param newRelationType: relation type which uses osee_relation
    * @return list of updated relations
    */
   @POST
   @Path("convert/artifact/{artifactA}/{oldRelationType}/{newRelationType}")
   @Consumes({MediaType.TEXT_PLAIN})
   @Produces({MediaType.APPLICATION_JSON})
   List<RelationTypeToken> convertRelations(@PathParam("artifactA") ArtifactId artifactA, @PathParam("oldRelationType") RelationTypeToken oldRelationType, @PathParam("newRelationType") RelationTypeToken newRelationType);

}