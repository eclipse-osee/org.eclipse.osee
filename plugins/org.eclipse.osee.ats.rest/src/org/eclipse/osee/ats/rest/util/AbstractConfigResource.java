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
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.jaxrs.mvc.IdentityView;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractConfigResource {

   protected final AtsApi atsApi;
   private final IArtifactType artifactType;
   protected IAtsServer atsServer;

   public AbstractConfigResource(IArtifactType artifactType, AtsApi atsApi) {
      this.artifactType = artifactType;
      this.atsApi = atsApi;
      this.atsServer = (IAtsServer) atsApi;
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
   @Path("{uuid}")
   @IdentityView
   @Produces(MediaType.APPLICATION_JSON)
   public IAtsConfigObject getObjectJson(@PathParam("uuid") int uuid) throws Exception {
      return getObject(uuid);
   }

   @GET
   @Path("{uuid}/details")
   @Produces(MediaType.APPLICATION_JSON)
   public IAtsConfigObject getObjectDetails(@PathParam("uuid") int uuid) throws Exception {
      return getObject(uuid);
   }

   private IAtsConfigObject getObject(int uuid) {
      ArtifactReadable configArt = (ArtifactReadable) atsApi.getArtifact(new Long(uuid));
      return atsApi.getConfigItemFactory().getConfigObject(configArt);
   }

   private List<IAtsConfigObject> getObjects() {
      List<IAtsConfigObject> configs = new ArrayList<>();
      for (ArtifactId art : atsApi.getArtifacts(artifactType)) {
         configs.add(atsApi.getConfigItemFactory().getConfigObject(art));
      }
      return configs;
   }

}
