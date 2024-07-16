/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.ats.rest.internal.config;

import java.net.URI;
import java.util.List;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.config.BaseConfigEndpointApi;
import org.eclipse.osee.ats.api.config.JaxAtsObject;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public abstract class BaseConfigEndpointImpl<T extends JaxAtsObject> implements BaseConfigEndpointApi<T> {

   protected final AtsApi atsApi;
   protected final ArtifactTypeToken artifactType;
   protected final ArtifactToken typeFolder;

   public BaseConfigEndpointImpl(ArtifactTypeToken artifactType, ArtifactToken typeFolder, AtsApi atsApi) {
      this.artifactType = artifactType;
      this.typeFolder = typeFolder;
      this.atsApi = atsApi;
   }

   @Override
   @GET
   public List<T> get() throws Exception {
      return getObjects();
   }

   @Override
   @GET
   @Path("{id}")
   public T get(@PathParam("id") long id) throws Exception {
      return getObject(id);
   }

   @Override
   @POST
   public Response create(T jaxAtsObject) throws Exception {
      if (jaxAtsObject.getId() <= 0L) {
         throw new OseeStateException("Invalid id %d");
      } else if (!Strings.isValid(jaxAtsObject.getName())) {
         throw new OseeStateException("Invalid name [%d]");
      }
      ArtifactReadable artifact = (ArtifactReadable) atsApi.getQueryService().getArtifact(jaxAtsObject.getId());
      if (artifact != null) {
         throw new OseeStateException("Artifact with id %d already exists", jaxAtsObject.getId());
      }
      IAtsChangeSet changes =
         atsApi.getStoreService().createAtsChangeSet("Create " + artifactType.getName(), AtsCoreUsers.SYSTEM_USER);
      ArtifactId newArtifact = changes.createArtifact(artifactType, jaxAtsObject.getName(), jaxAtsObject.getId());
      if (typeFolder != null) {
         ArtifactReadable typeFolderArtifact = (ArtifactReadable) atsApi.getQueryService().getArtifact(typeFolder);
         if (typeFolderArtifact == null) {
            typeFolderArtifact = (ArtifactReadable) changes.createArtifact(AtsArtifactToken.CountryFolder);
         }
         if (typeFolderArtifact.getParent() == null) {
            ArtifactReadable headingFolder =
               (ArtifactReadable) atsApi.getQueryService().getArtifact(AtsArtifactToken.AtsTopFolder);
            changes.relate(headingFolder, CoreRelationTypes.DefaultHierarchical_Child, typeFolderArtifact);
         }
         changes.relate(typeFolderArtifact, CoreRelationTypes.DefaultHierarchical_Child, newArtifact);
      }
      if (Strings.isValid(jaxAtsObject.getDescription())) {
         changes.setSoleAttributeValue(newArtifact, AtsAttributeTypes.Description, jaxAtsObject.getDescription());
      } else {
         changes.deleteAttributes(newArtifact, AtsAttributeTypes.Description);
      }
      changes.setSoleAttributeValue(newArtifact, AtsAttributeTypes.Active, jaxAtsObject.isActive());
      create(jaxAtsObject, newArtifact, changes);
      changes.execute();
      return Response.created(new URI("/" + jaxAtsObject.getIdString())).build();
   }

   /**
    * Implement by subclass to perform other checks and sets during artifact creation
    */
   protected void create(T jaxAtsObject, ArtifactId newArtifact, IAtsChangeSet changes) {
      // provided for subclass implementation
   }

   @Override
   @DELETE
   public Response delete(@PathParam("id") long id) throws Exception {
      ArtifactReadable artifact = (ArtifactReadable) atsApi.getQueryService().getArtifact(id);
      if (artifact == null) {
         throw new OseeStateException("Artifact with id %d not found", id);
      }
      IAtsChangeSet changes =
         atsApi.getStoreService().createAtsChangeSet("Create " + artifactType.getName(), AtsCoreUsers.SYSTEM_USER);
      changes.deleteArtifact(artifact);
      changes.execute();
      return Response.ok().build();
   }

   public abstract T getConfigObject(ArtifactId artifact);

   protected T getObject(long id) {
      ArtifactToken configArt = atsApi.getQueryService().getArtifact(id);
      return getConfigObject(configArt);
   }

   public abstract List<T> getObjects();

}
