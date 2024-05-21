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

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactWithRelationsAttribute;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Roberto E. Escobar
 */
@Path("types")
@Swagger
public interface TypesEndpoint {

   @GET
   @Produces({MediaType.APPLICATION_JSON})
   XResultData getTypes();

   @GET
   @Path("artifact")
   @Produces(MediaType.APPLICATION_JSON)
   Collection<NamedIdBase> getArtifactTypes(@QueryParam("filter") String filter);

   @GET
   @Path("artifact/{artifactId}/attributes")
   @Produces(MediaType.APPLICATION_JSON)
   Collection<ArtifactWithRelationsAttribute> getArtifactTypeAttributes(@PathParam("artifactId") ArtifactId artifactId);

   @GET
   @Path("attribute")
   @Produces(MediaType.APPLICATION_JSON)
   Collection<NamedIdBase> getAttributeTypes(@QueryParam("artifactType") List<ArtifactTypeToken> artifactTypes);

   @GET
   @Path("attribute/{attributeId}/enums")
   @Produces(MediaType.APPLICATION_JSON)
   Set<String> getAttributeEnums(@PathParam("attributeId") AttributeId attributeId);

   @GET
   @Path("health")
   @Produces({MediaType.APPLICATION_JSON})
   XResultData getHealthReport();

   @GET
   @Path("serverEnumTypesAndValues")
   @Produces({MediaType.APPLICATION_JSON})
   List<LinkedHashMap<String, Object>> getServerEnumTypesAndValues();
}