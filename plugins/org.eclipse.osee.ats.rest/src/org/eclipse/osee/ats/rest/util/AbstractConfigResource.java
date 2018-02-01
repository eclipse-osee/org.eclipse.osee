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
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.jaxrs.mvc.IdentityView;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractConfigResource {

   protected final AtsApi atsApi;
   private final IArtifactType artifactType;

   public AbstractConfigResource(IArtifactType artifactType, AtsApi atsApi) {
      this.artifactType = artifactType;
      this.atsApi = atsApi;
   }

   @GET
   @IdentityView
   @Produces(MediaType.APPLICATION_JSON)
   public List<IAtsConfigObject> get() throws Exception {
      return getObjects();
   }

   @GET
   @Path("details")
   @Produces(MediaType.APPLICATION_JSON)
   public List<IAtsConfigObject> getObjectsJson() throws Exception {
      return getObjects();
   }

   @GET
   @Path("{id}")
   @IdentityView
   @Produces(MediaType.APPLICATION_JSON)
   public IAtsConfigObject getObjectJson(@PathParam("id") int id) throws Exception {
      return getObject(id);
   }

   @GET
   @Path("{id}/details")
   @Produces(MediaType.APPLICATION_JSON)
   public IAtsConfigObject getObjectDetails(@PathParam("id") int id) throws Exception {
      return getObject(id);
   }

   private IAtsConfigObject getObject(int id) {
      ArtifactToken configArt = atsApi.getQueryService().getArtifact(new Long(id));
      return atsApi.getConfigItemFactory().getConfigObject(configArt);
   }

   private List<IAtsConfigObject> getObjects() {
      List<IAtsConfigObject> configs = new ArrayList<>();
      for (ArtifactId art : atsApi.getQueryService().getArtifacts(artifactType)) {
         configs.add(atsApi.getConfigItemFactory().getConfigObject(art));
      }
      return configs;
   }

}
