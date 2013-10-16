/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.access.provider.internal;

import org.eclipse.osee.framework.core.dsl.ui.integration.operations.AbstractOseeDslProvider;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author John R. Misinco
 */
public class FrameworkDslProvider extends AbstractOseeDslProvider {

   public FrameworkDslProvider(String locationUri) {
      super(locationUri);
   }

   protected Artifact getStorageArtifact() {
      try {
         return ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.AccessControlModel,
            DefaultFrameworkAccessConstants.STORAGE_ARTIFACT_NAME, BranchManager.getCommonBranch());
      } catch (OseeCoreException ex) {
         return null;
      }
   }

   @Override
   protected String getModelFromStorage() throws OseeCoreException {
      Artifact storageArtifact = getStorageArtifact();
      if (storageArtifact != null) {
         return storageArtifact.getSoleAttributeValue(CoreAttributeTypes.GeneralStringData);
      } else {
         return Strings.EMPTY_STRING;
      }
   }

   @Override
   protected void saveModelToStorage(String model) throws OseeCoreException {
      Artifact artifact = getStorageArtifact();
      if (artifact != null) {
         artifact.setSoleAttributeFromString(CoreAttributeTypes.GeneralStringData, model);
         artifact.persist(getClass().getSimpleName());
      }
   }

}
