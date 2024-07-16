/*********************************************************************
 * Copyright (c) 2017 Boeing
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
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeJoin;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.util.ArtifactSearchOptions;
import org.eclipse.osee.orcs.rest.model.search.artifact.SearchRequest;
import org.eclipse.osee.orcs.rest.model.search.artifact.SearchResponse;

/**
 * @author Ryan D. Brooks
 */
@Path("artifact")
public interface ArtifactEndpoint {
   @POST
   @Path("search/v1")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   SearchResponse getSearchWithMatrixParams(SearchRequest params);

   @GET
   @Produces(MediaType.TEXT_HTML)
   String getRootChildrenAsHtml();

   @GET
   @Path("{artifactId}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.TEXT_HTML)
   String getArtifactAsHtml(@PathParam("artifactId") ArtifactId artifactId);

   @GET
   @Path("{artifactId}/token")
   @Produces(MediaType.APPLICATION_JSON)
   ArtifactToken getArtifactToken(@PathParam("artifactId") ArtifactId artifactId);

   @Path("{artifactId}/attribute")
   AttributeEndpoint getAttributes(@PathParam("artifactId") ArtifactId artifactId);

   @GET
   @Path("attType/{attributeType}/token")
   @Produces(MediaType.APPLICATION_JSON)
   List<ArtifactToken> getArtifactTokensByAttribute(@PathParam("attributeType") AttributeTypeToken attributeType, @QueryParam("value") String value, @DefaultValue("true") @QueryParam("exists") boolean exists, @DefaultValue("-1") @QueryParam("artifactType") ArtifactTypeToken artifactType);

   @GET
   @Path("attType/{attributeType}/id")
   @Produces(MediaType.APPLICATION_JSON)
   List<ArtifactId> getArtifactIdsByAttribute(@PathParam("attributeType") AttributeTypeToken attributeType, @QueryParam("value") String value, @DefaultValue("true") @QueryParam("exists") boolean exists, @DefaultValue("-1") @QueryParam("artifactType") ArtifactTypeToken artifactType);

   @GET
   @Path("map")
   @Produces(MediaType.APPLICATION_JSON)
   List<Map<String, Object>> getArtifactMaps(@DefaultValue("-1") @QueryParam("attributeType") AttributeTypeToken attributeType, @QueryParam("representation") String representation, @QueryParam("value") String value, @DefaultValue("true") @QueryParam("exists") boolean exists, @DefaultValue("-1") @QueryParam("artifactType") ArtifactTypeToken artifactType, @DefaultValue("-1") @QueryParam("view") ArtifactId view);

   @GET
   @Path("type/{artifactType}/token")
   @Produces(MediaType.APPLICATION_JSON)
   List<ArtifactToken> getArtifactTokensByType(@PathParam("artifactType") ArtifactTypeToken artifactType);

   @GET
   @Path("exp")
   @Produces(MediaType.APPLICATION_JSON)
   List<ArtifactToken> expGetArtifactTokens(@DefaultValue("-1") @QueryParam("artifactType") ArtifactTypeToken artifactType, @DefaultValue("-1") @QueryParam("parent") ArtifactId parent, @DefaultValue("-1") @QueryParam("view") ArtifactId view);

   @GET
   @Path("changed_artifacts/{attributeTypeJoin}/{commentPattern}")
   @Produces(MediaType.APPLICATION_JSON)
   List<ArtifactToken> getChangedArtifactTokens(@DefaultValue("-1") @QueryParam("view") ArtifactId view, @PathParam("attributeTypeJoin") AttributeTypeJoin typeJoin, @PathParam("commentPattern") String commentPattern);

   @GET
   @Path("applicability/{id}")
   @Produces(MediaType.APPLICATION_JSON)
   List<ArtifactToken> getArtifactTokensByApplicability(@PathParam("id") ApplicabilityId appId);

   @GET
   @Path("type/{artifactType}/id")
   @Produces(MediaType.APPLICATION_JSON)
   List<ArtifactId> getArtifactIdsByType(@PathParam("artifactType") ArtifactTypeToken artifactType);

   /**
    * error if an artifact with the same name and type already exist
    */
   @POST
   @Path("type/{artifactType}/parent/{parent}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   List<ArtifactToken> createArtifacts(@PathParam("branch") BranchId branch, @PathParam("artifactType") ArtifactTypeToken artifactType, @DefaultValue("-1") @PathParam("parent") ArtifactId parent, List<String> names);

   @POST
   @Path("type/{artifactType}/parent/{parent}/create")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.APPLICATION_JSON)
   ArtifactToken createArtifact(@PathParam("branch") BranchId branch, @PathParam("artifactType") ArtifactTypeToken artifactType, @DefaultValue("-1") @PathParam("parent") ArtifactId parent, String name);

   @POST
   @Path("old-type/{oldType}/new-type/{newType}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   List<ArtifactToken> changeArtifactType(@PathParam("branch") BranchId branch, @PathParam("oldType") ArtifactTypeToken oldType, @PathParam("newType") ArtifactTypeToken newType, List<String> names);

   @DELETE
   @Path("{artifact}")
   @Produces(MediaType.APPLICATION_JSON)
   TransactionToken deleteArtifact(@PathParam("branch") BranchId branch, @PathParam("artifact") ArtifactId artifact);

   @PUT
   @Path("{artifact}/attribute/type/{attributeType}")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.APPLICATION_JSON)
   TransactionToken setSoleAttributeValue(@PathParam("branch") BranchId branch, @PathParam("artifact") ArtifactId artifact, @PathParam("attributeType") AttributeTypeToken attributeType, String value);

   @POST
   @Path("search")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   List<ArtifactId> findArtifactIds(ArtifactSearchOptions searchOptions);

   @POST
   @Path("search/token")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   List<ArtifactToken> findArtifactTokens(ArtifactSearchOptions searchOptions);

}