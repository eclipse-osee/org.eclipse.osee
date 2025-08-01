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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import javax.ws.rs.core.Response;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTokenWithIcon;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.ArtifactWithRelations;
import org.eclipse.osee.framework.core.data.AttributeTypeJoin;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.util.ArtifactSearchOptions;
import org.eclipse.osee.orcs.rest.model.search.artifact.SearchRequest;
import org.eclipse.osee.orcs.rest.model.search.artifact.SearchResponse;
import org.eclipse.osee.orcs.search.ArtifactTable;

/**
 * @author Ryan D. Brooks
 */
@Path("artifact")
public interface ArtifactEndpoint {
   @GET
   @Path("search")
   @Produces(MediaType.APPLICATION_JSON)
   List<ArtifactReadable> getSearchResults(@QueryParam("search") String search, @QueryParam("viewId") ArtifactId viewId,
      @QueryParam("artifactType") List<ArtifactTypeToken> artifactTypes,
      @QueryParam("attributeType") List<AttributeTypeToken> attributeTypes, @QueryParam("exact") boolean exactMatch,
      @QueryParam("searchById") boolean searchById, @QueryParam("pageNum") long pageNum,
      @QueryParam("count") long pageSize);

   @GET
   @Path("search/token")
   @Produces(MediaType.APPLICATION_JSON)
   List<ArtifactTokenWithIcon> getSearchResultTokens(@QueryParam("search") String search,
      @QueryParam("viewId") ArtifactId viewId, @QueryParam("artifactType") List<ArtifactTypeToken> artifactTypes,
      @QueryParam("attributeType") List<AttributeTypeToken> attributeTypes, @QueryParam("exact") boolean exactMatch,
      @QueryParam("searchById") boolean searchById, @QueryParam("pageNum") long pageNum,
      @QueryParam("count") long pageSize);

   @GET
   @Path("search/count")
   @Produces(MediaType.APPLICATION_JSON)
   int getSearchResultCount(@QueryParam("search") String search, @QueryParam("viewId") ArtifactId viewId,
      @QueryParam("artifactType") List<ArtifactTypeToken> artifactTypes,
      @QueryParam("attributeType") List<AttributeTypeToken> attributeTypes, @QueryParam("exact") boolean exactMatch,
      @QueryParam("searchById") boolean searchById);

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
   @Path("{artifactId}/json")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   ArtifactReadable getArtifactAsJson(@PathParam("artifactId") ArtifactId artifactId,
      @DefaultValue("-1") @QueryParam("view") ArtifactId view);

   @GET
   @Path("{artifactId}/token")
   @Produces(MediaType.APPLICATION_JSON)
   ArtifactToken getArtifactToken(@PathParam("artifactId") ArtifactId artifactId);

   @GET
   @Path("{artifactId}/tokenOrSentinal")
   @Produces(MediaType.APPLICATION_JSON)
   ArtifactToken getArtifactTokenOrSentinel(@PathParam("artifactId") ArtifactId artifactId);

   @Path("{artifactId}/attribute")
   AttributeEndpoint getAttributes(@PathParam("artifactId") ArtifactId artifactId);

   @GET
   @Path("attType/{attributeType}/token")
   @Produces(MediaType.APPLICATION_JSON)
   List<ArtifactToken> getArtifactTokensByAttribute(@PathParam("attributeType") AttributeTypeToken attributeType,
      @QueryParam("value") String value, @DefaultValue("true") @QueryParam("exists") boolean exists,
      @DefaultValue("-1") @QueryParam("artifactType") ArtifactTypeToken artifactType);

   @GET
   @Path("attType/{attributeType}/id")
   @Produces(MediaType.APPLICATION_JSON)
   List<ArtifactId> getArtifactIdsByAttribute(@PathParam("attributeType") AttributeTypeToken attributeType,
      @QueryParam("value") String value, @DefaultValue("true") @QueryParam("exists") boolean exists,
      @DefaultValue("-1") @QueryParam("artifactType") ArtifactTypeToken artifactType);

   @GET
   @Path("map")
   @Produces(MediaType.APPLICATION_JSON)
   List<Map<String, Object>> getArtifactMaps(
      @DefaultValue("-1") @QueryParam("attributeType") AttributeTypeToken attributeType,
      @QueryParam("representation") String representation, @QueryParam("value") String value,
      @DefaultValue("true") @QueryParam("exists") boolean exists,
      @DefaultValue("-1") @QueryParam("artifactType") ArtifactTypeToken artifactType,
      @DefaultValue("-1") @QueryParam("view") ArtifactId view);

   @GET
   @Path("table")
   @Produces(MediaType.APPLICATION_JSON)
   ArtifactTable getArtifactTable(@DefaultValue("-1") @QueryParam("attributeType") AttributeTypeToken attributeType,
      @QueryParam("attributeColumns") List<AttributeTypeToken> attributeColumns, @QueryParam("value") String value,
      @DefaultValue("true") @QueryParam("exists") boolean exists,
      @DefaultValue("-1") @QueryParam("artifactType") ArtifactTypeToken artifactType,
      @DefaultValue("-1") @QueryParam("view") ArtifactId view);

   @GET
   @Path("type/{artifactType}/token")
   @Produces(MediaType.APPLICATION_JSON)
   List<ArtifactToken> getArtifactTokensByType(@PathParam("artifactType") ArtifactTypeToken artifactType);

   @GET
   @Path("exp")
   @Produces(MediaType.APPLICATION_JSON)
   List<ArtifactToken> expGetArtifactTokens(
      @DefaultValue("-1") @QueryParam("artifactType") ArtifactTypeToken artifactType,
      @DefaultValue("-1") @QueryParam("parent") ArtifactId parent,
      @DefaultValue("-1") @QueryParam("view") ArtifactId view);

   @GET
   @Path("changed_artifacts/{attributeTypeJoin}/{commentPattern}")
   @Produces(MediaType.APPLICATION_JSON)
   List<ArtifactToken> getChangedArtifactTokens(@DefaultValue("-1") @QueryParam("view") ArtifactId view,
      @PathParam("attributeTypeJoin") AttributeTypeJoin typeJoin, @PathParam("commentPattern") String commentPattern);

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
   List<ArtifactToken> createArtifacts(@PathParam("branch") BranchId branch,
      @PathParam("artifactType") ArtifactTypeToken artifactType,
      @DefaultValue("-1") @PathParam("parent") ArtifactId parent, List<String> names);

   @POST
   @Path("type/{artifactType}/parent/{parent}/create")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.APPLICATION_JSON)
   ArtifactToken createArtifact(@PathParam("branch") BranchId branch,
      @PathParam("artifactType") ArtifactTypeToken artifactType,
      @DefaultValue("-1") @PathParam("parent") ArtifactId parent, String name);

   @POST
   @Path("old-type/{oldType}/new-type/{newType}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   List<ArtifactToken> changeArtifactType(@PathParam("branch") BranchId branch,
      @PathParam("oldType") ArtifactTypeToken oldType, @PathParam("newType") ArtifactTypeToken newType,
      List<String> names);

   @DELETE
   @Path("{artifact}")
   @Produces(MediaType.APPLICATION_JSON)
   TransactionToken deleteArtifact(@PathParam("branch") BranchId branch, @PathParam("artifact") ArtifactId artifact);

   @DELETE
   @Path("{artifact}/purge")
   @Produces(MediaType.APPLICATION_JSON)
   Response purgeArtifact(@PathParam("artifact") ArtifactId artifact);

   /**
    * Deletes attributes of a specified attribute type from all artifacts on branch of a specified artifact type. The
    * attributes to be deleted are determined based on the artifact type defined in the code. The endpoint allows
    * specifying the artifact and/or artifact type to delete attribute data from.
    *
    * @param branch The branch ID from which to delete attributes.
    * @param artifact The artifact ID to specify the artifact type from which to delete attributes. If not provided,
    * defaults to -1.
    * @param artifactType The artifact type token/id to specify the type of artifact from which to delete attributes. If
    * not provided, defaults to -1.
    * @param attributeType The attribute type token specifying the type of attribute to be deleted. If not provided,
    * defaults to -1.
    * @return A {@link TransactionToken} indicating the transaction's success or failure.
    */
   @DELETE
   @Path("attributesOfType")
   @Produces(MediaType.APPLICATION_JSON)
   TransactionToken deleteAttributesOfType(@PathParam("branch") BranchId branch,
      @QueryParam("artifact") @DefaultValue("-1") ArtifactId artifact,
      @QueryParam("artifactType") @DefaultValue("-1") ArtifactTypeToken artifactType,
      @QueryParam("attributeType") @DefaultValue("-1") AttributeTypeToken attributeType);

   @PUT
   @Path("{artifact}/attribute/type/{attributeType}")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.APPLICATION_JSON)
   TransactionToken setSoleAttributeValue(@PathParam("branch") BranchId branch,
      @PathParam("artifact") ArtifactId artifact, @PathParam("attributeType") AttributeTypeToken attributeType,
      String value);

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

   @GET
   @Path("{artifact}/related/maps")
   @Produces(MediaType.APPLICATION_JSON)
   @SwaggerCommonOrcsAnnotations
   @Operation(summary = "Gets a hierarchy tree of Artifacts")
   @Tag(name = "hierarchy")
   List<ArtifactReadable> getRelatedArtifactsTree(@PathParam("branch") BranchId branch,
      @PathParam("artifact") ArtifactId artifact);

   @GET
   @Path("{artifact}/related/direct")
   @Produces(MediaType.APPLICATION_JSON)
   @SwaggerCommonOrcsAnnotations
   @Operation(summary = "Gets only the first layer of related Artifacts")
   @Tag(name = "hierarchy")
   ArtifactWithRelations getRelatedDirect(@PathParam("branch") BranchId branch,
      @PathParam("artifact") ArtifactId artifact, @QueryParam("viewId") ArtifactId viewId,
      @QueryParam("includeRelations") boolean includeRelations);

   @GET
   @Path("createTxBuilder/{artifactTypeId}")
   @Produces(MediaType.APPLICATION_JSON)
   TxBuilderInput getTxBuilderInput(@PathParam("artifactTypeId") ArtifactTypeToken artifactTypeId);

   @GET
   @Path("{artifactId}/getPathToArtifact")
   @Produces(MediaType.APPLICATION_JSON)
   List<List<ArtifactId>> getPathToArtifact(@PathParam("branch") BranchId branch,
      @PathParam("artifactId") ArtifactId artifactId, @QueryParam("viewId") ArtifactId viewId);

   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Path("{artifactId}/convertWordTemplateContentToMarkdownContent")
   String convertWordTemplateContentToMarkdownContent(@PathParam("branch") @DefaultValue("-1") BranchId branchId,
      @PathParam("artifactId") @DefaultValue("-1") ArtifactId artifactId,
      @QueryParam("includeErrorLog") @DefaultValue("false") Boolean includeErrorLog,
      @QueryParam("flushMarkdownContentAttributeAndImageArtifacts") @DefaultValue("false") Boolean flushMarkdownContentAttributeAndImageArtifacts);
}
