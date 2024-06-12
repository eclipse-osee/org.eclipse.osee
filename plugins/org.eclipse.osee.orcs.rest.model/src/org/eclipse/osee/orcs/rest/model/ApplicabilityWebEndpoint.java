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
package org.eclipse.osee.orcs.rest.model;

import java.util.Collection;
import java.util.List;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.applicability.FeatureDefinition;
import org.eclipse.osee.framework.core.applicability.FeatureSelectionWithConstraints;
import org.eclipse.osee.framework.core.applicability.ProductLineConfig;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.ConfigurationGroupDefinition;
import org.eclipse.osee.framework.core.data.CreateViewDefinition;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * Note: this should only be used by the plconfig webpage...if you need to share these more optimized queries, put them
 * in orcApplicabilityOps
 */
@Path("applicability")
public interface ApplicabilityWebEndpoint {

   @GET()
   @Produces(MediaType.APPLICATION_JSON)
   ProductLineConfig getTable(@QueryParam("filter") @DefaultValue("") String filter,
      @QueryParam("viewId") ArtifactId viewId, @QueryParam("pageNum") long pageNum, @QueryParam("count") long pageSize);

   @GET()
   @Path("count")
   @Produces(MediaType.APPLICATION_JSON)
   long getTableCount(@QueryParam("filter") @DefaultValue("") String filter, @QueryParam("viewId") ArtifactId viewId);

   @GET()
   @Path("feature/{featureId}/{configId}")
   @Produces(MediaType.APPLICATION_JSON)
   List<FeatureSelectionWithConstraints> getFeatures(@PathParam("featureId") ArtifactId featureId,
      @PathParam("configId") ArtifactId configId, @QueryParam("filter") @DefaultValue("") String filter,
      @QueryParam("pageNum") long pageNum, @QueryParam("count") long pageSize);

   @GET()
   @Path("feature/{featureId}/{configId}/count")
   @Produces(MediaType.APPLICATION_JSON)
   long getFeaturesCount(@PathParam("featureId") ArtifactId featureId, @PathParam("configId") ArtifactId configId,
      @QueryParam("filter") @DefaultValue("") String filter);

   @GET()
   @Path("views/{view}/groups")
   @Produces(MediaType.APPLICATION_JSON)
   List<ConfigurationGroupDefinition> getConfigurations(@PathParam("view") ArtifactId viewId);

   @PUT()
   @Path("applic/{feature}/{view}")
   @Produces(MediaType.APPLICATION_JSON)
   XResultData setApplicability(@PathParam("view") ArtifactId viewId, @PathParam("feature") ArtifactId featureId,
      String[] applicability);

   @GET()
   @Path("views")
   @Produces(MediaType.APPLICATION_JSON)
   Collection<CreateViewDefinition> getViews(@QueryParam("id") Collection<ArtifactId> ids,
      @QueryParam("orderByAttributeType") AttributeTypeToken orderByAttributeType);

   @GET()
   @Path("features")
   @Produces(MediaType.APPLICATION_JSON)
   Collection<FeatureDefinition> getFeatureDefinitions(
      @QueryParam("orderByAttributeType") AttributeTypeToken orderByAttributeType);
}
