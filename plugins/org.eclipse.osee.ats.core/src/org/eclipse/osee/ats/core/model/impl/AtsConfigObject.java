/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.model.impl;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.logger.Log;

/**
 * @author Donald G Dunne
 */
public abstract class AtsConfigObject extends org.eclipse.osee.ats.core.model.impl.AtsObject implements IAtsConfigObject {
   protected ArtifactToken artifact;
   protected final Log logger;
   protected final AtsApi atsApi;

   public AtsConfigObject(Log logger, AtsApi atsApi, ArtifactToken artifact, ArtifactTypeToken artifactType) {
      super(artifact.getName(), artifact.getId());
      this.logger = logger;
      this.atsApi = atsApi;
      this.artifact = artifact;
      setStoreObject(artifact);
   }

   public Log getLogger() {
      return logger;
   }

   @Override
   public AtsApi getAtsApi() {
      return atsApi;
   }

   public Boolean isActionable() {
      return getAttributeValue(AtsAttributeTypes.Actionable, false);
   }

   @SuppressWarnings("unchecked")
   protected <T> T getAttributeValue(AttributeTypeToken attributeType, Object defaultValue) {
      T value = null;
      try {
         value = (T) atsApi.getAttributeResolver().getSoleAttributeValue(artifact, attributeType, defaultValue);
      } catch (OseeCoreException ex) {
         logger.error(ex, "Error getting attribute value for - attributeType[%s]", attributeType);
      }
      return value;
   }

   @Override
   public boolean isActive() {
      if (atsApi.getStoreService().isDeleted(artifact)) {
         return false;
      }
      return getAttributeValue(AtsAttributeTypes.Active, false);
   }

   public Collection<String> getStaticIds() {
      Collection<String> results = Collections.emptyList();
      try {
         results = atsApi.getAttributeResolver().getAttributeValues(artifact, CoreAttributeTypes.StaticId);
      } catch (OseeCoreException ex) {
         logger.error(ex, "Error getting static Ids");
      }
      return results;
   }

   @Override
   public Long getId() {
      return artifact.getId();
   }

   @Override
   public String getDescription() {
      return atsApi.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.Description, "");
   }

   public ArtifactToken loadStoreObject() {
      ArtifactToken art = atsApi.getQueryService().getArtifact(artifact.getId());
      if (art.isValid()) {
         setStoreObject(art);
      }
      return art;
   }

   @Override
   public ArtifactToken getStoreObject() {
      if (artifact != null) {
         return artifact;
      }

      if (super.getStoreObject() == null) {
         return loadStoreObject();
      }
      return super.getStoreObject();
   }
}
