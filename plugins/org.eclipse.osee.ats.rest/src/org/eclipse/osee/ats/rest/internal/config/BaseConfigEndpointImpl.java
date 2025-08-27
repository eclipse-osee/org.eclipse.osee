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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.config.JaxAtsObject;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public abstract class BaseConfigEndpointImpl<T extends JaxAtsObject> {

   protected final AtsApi atsApi;
   protected final ArtifactTypeToken artifactType;
   protected final ArtifactToken typeFolder;

   public BaseConfigEndpointImpl(ArtifactTypeToken artifactType, ArtifactToken typeFolder, AtsApi atsApi) {
      this.artifactType = artifactType;
      this.typeFolder = typeFolder;
      this.atsApi = atsApi;
   }

   public T createConfig(T jaxAtsObject) {
      if (Strings.isInValid(jaxAtsObject.getName())) {
         throw new OseeStateException("Invalid name [%s]", jaxAtsObject.getName());
      }
      Long newArtId = jaxAtsObject.getId();
      if (newArtId == null || newArtId <= 0) {
         newArtId = Lib.generateArtifactIdAsInt();
      }
      IAtsChangeSet changes = atsApi.createChangeSet("Create " + artifactType.getName());

      ArtifactReadable artifact = null;
      if (jaxAtsObject.getId() != 0) {
         artifact = (ArtifactReadable) atsApi.getQueryService().getArtifact(newArtId);
      }
      if (artifact == null) {
         artifact = (ArtifactReadable) changes.createArtifact(artifactType, jaxAtsObject.getName(), newArtId);
      }
      changes.setName(artifact, jaxAtsObject.getName());
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
         if (!typeFolderArtifact.getChildren().contains(artifact)) {
            changes.relate(typeFolderArtifact, CoreRelationTypes.DefaultHierarchical_Child, artifact);
         }
      }
      if (Strings.isValid(jaxAtsObject.getDescription())) {
         changes.setSoleAttributeValue(artifact, AtsAttributeTypes.Description, jaxAtsObject.getDescription());
      } else {
         changes.deleteAttributes(artifact, AtsAttributeTypes.Description);
      }
      changes.setSoleAttributeValue(artifact, AtsAttributeTypes.Active, jaxAtsObject.isActive());
      createConfigExt(jaxAtsObject, artifact, changes);
      changes.executeIfNeeded();
      return getConfig(newArtId);
   }

   protected void createConfigExt(T jaxAtsObject, ArtifactId newArtifact, IAtsChangeSet changes) {
      // provided for subclass implementation
   }

   public void deleteConfig(long id) {
      ArtifactReadable artifact = (ArtifactReadable) atsApi.getQueryService().getArtifact(id);
      if (artifact == null) {
         throw new OseeStateException("Artifact with id %d not found", id);
      }
      IAtsChangeSet changes = atsApi.createChangeSet("Create " + artifactType.getName());
      changes.deleteArtifact(artifact);
      changes.execute();
   }

   public abstract T getConfig(ArtifactId artifact);

   public T getConfig(long id) {
      ArtifactToken configArt = atsApi.getQueryService().getArtifact(id);
      getConfig(configArt);
      return getConfig(configArt);
   }

   public T get(long id) {
      return getConfig(id);
   }

   public List<T> getConfigs() {
      List<T> configs = new ArrayList<>();
      for (ArtifactToken art : atsApi.getQueryService().getArtifacts(artifactType)) {
         T config = getConfig(art);
         getConfigExt(art, config);
         configs.add(config);
      }
      return configs;
   }

   protected void getConfigExt(ArtifactToken art, T config) {
      // provided for subclass implementation
   }

}
