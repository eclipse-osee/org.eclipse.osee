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

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.ArtifactId;
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
}