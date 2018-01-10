/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.model;

import java.util.List;
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
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
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

   @Path("{artifactId}/attribute")
   AttributeEndpoint getAttributes(@PathParam("artifactId") ArtifactId artifactId);

   @GET
   @Path("type/{artifactType}/atrType/{attributeType}")
   @Produces(MediaType.APPLICATION_JSON)
   List<ArtifactToken> getArtifactTokensByAttribute(@PathParam("artifactType") ArtifactTypeId artifactType, @PathParam("attributeType") AttributeTypeId attributeType, @QueryParam("value") String value, @DefaultValue("true") @QueryParam("exists") boolean exists);

   @GET
   @Path("type/{artifactType}")
   @Produces(MediaType.APPLICATION_JSON)
   List<ArtifactToken> getArtifactTokensByType(@PathParam("artifactType") ArtifactTypeId artifactType);

   /**
    * error if an artifact with the same name and type already exist
    */
   @POST
   @Path("type/{artifactType}/parent/{parent}")
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   List<ArtifactToken> createArtifacts(@PathParam("branch") BranchId branch, @PathParam("artifactType") ArtifactTypeId artifactType, @DefaultValue("-1") @PathParam("parent") ArtifactId parent, List<String> names);

   @DELETE
   @Path("{artifact}")
   TransactionId deleteArtifact(@PathParam("branch") BranchId branch, @PathParam("artifact") ArtifactId artifact);

   @PUT
   @Path("{artifact}/attribute/type/{attributeType}")
   TransactionId setSoleAttributeValue(@PathParam("branch") BranchId branch, @PathParam("artifact") ArtifactId artifact, @PathParam("attributeType") AttributeTypeId attributeType, String value);
}