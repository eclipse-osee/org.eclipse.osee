/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.util;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import java.util.concurrent.TimeUnit;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.config.AtsConfigurations;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionAdmin;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Donald G. Dunne
 */
public abstract class AtsCoreServiceImpl implements IAtsServices {

   protected IAtsWorkDefinitionAdmin workDefAdmin;

   @Override
   public String getAtsId(ArtifactId artifact) {
      return getAtsId(getAttributeResolver(), artifact);
   }

   @Override
   public String getAtsId(IAtsObject atsObject) {
      return getAtsId(getAttributeResolver(), atsObject.getStoreObject());
   }

   protected static String getAtsId(IAttributeResolver attrResolver, IAtsObject atsObject) {
      return getAtsId(attrResolver, atsObject.getStoreObject());
   }

   protected static String getAtsId(IAttributeResolver attrResolver, ArtifactId artifact) {
      Conditions.checkNotNull(artifact, "artifact");
      String toReturn = attrResolver.getSoleAttributeValue(artifact, AtsAttributeTypes.AtsId, null);
      if (toReturn == null) {
         toReturn = artifact.getGuid();
      }
      return toReturn;
   }

   public IAtsWorkDefinitionAdmin getWorkDefAdmin() {
      return workDefAdmin;
   }

   @Override
   public AtsConfigurations getConfigurations() {
      return configurationsCache.get();
   }

   private final Supplier<AtsConfigurations> configurationsCache =
      Suppliers.memoizeWithExpiration(getConfigurationsSupplier(), 5, TimeUnit.MINUTES);

   private Supplier<AtsConfigurations> getConfigurationsSupplier() {
      return new Supplier<AtsConfigurations>() {
         @Override
         public AtsConfigurations get() {
            return loadConfigurations();
         }
      };
   }

   /**
    * Provided loader for configurations. Should be called infrequently only by AtsClientImpl or AtsServerImpl
    */
   abstract protected AtsConfigurations loadConfigurations();

   @Override
   public <T> T getConfigItem(IArtifactToken artifactToken) {
      return getConfigItem(artifactToken.getUuid());
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> T getConfigItem(String guid) {
      T atsObject = getCache().getAtsObjectByGuid(guid);
      if (atsObject == null) {
         ArtifactId artifact = getArtifactByGuid(guid);
         if (artifact != null && artifact instanceof IAtsConfigObject) {
            atsObject = (T) getConfigItemFactory().getConfigObject(artifact);
         }
      }
      return atsObject;
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> T getConfigItem(Long uuid) {
      T atsObject = getCache().getAtsObject(uuid);
      if (atsObject == null) {
         ArtifactId artifact = getArtifact(uuid);
         if (artifact != null && artifact instanceof IAtsConfigObject) {
            atsObject = (T) getConfigItemFactory().getConfigObject(artifact);
         }
      }
      return atsObject;
   }

}
