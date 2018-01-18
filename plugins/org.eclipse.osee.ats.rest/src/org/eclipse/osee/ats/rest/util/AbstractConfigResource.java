/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.util;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractConfigResource {

   protected final AtsApi atsApi;
   private final ArtifactTypeId artifactType;
   private final QueryBuilder query;

   public AbstractConfigResource(ArtifactTypeId artifactType, AtsApi atsApi, OrcsApi orcsApi) {
      this.artifactType = artifactType;
      this.atsApi = atsApi;
      query = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON);
   }

   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public List<ArtifactToken> get() {
      return query.andIsOfType(artifactType).loadArtifactTokens();
   }

   @GET
   @Path("details")
   @Produces(MediaType.APPLICATION_JSON)
   public List<IAtsConfigObject> getObjectsJson() {
      List<IAtsConfigObject> configs = new ArrayList<>();
      for (ArtifactId art : query.andTypeEquals(artifactType).getResults()) {
         configs.add(atsApi.getConfigItemFactory().getConfigObject(art));
      }
      return configs;
   }

   @GET
   @Path("{id}")
   @Produces(MediaType.APPLICATION_JSON)
   public ArtifactToken getObjectJson(@PathParam("id") ArtifactId artifactId) {
      return query.andId(artifactId).loadArtifactToken();
   }

   @GET
   @Path("{id}/details")
   @Produces(MediaType.APPLICATION_JSON)
   public IAtsConfigObject getObjectDetails(@PathParam("id") ArtifactId artifactId) {
      return atsApi.getConfigItemFactory().getConfigObject(artifactId);
   }
}