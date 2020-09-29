/*********************************************************************
 * Copyright (c) 2021 Boeing
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
package org.eclipse.osee.ats.core.access;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.access.IAtsAccessContextProvider;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.core.internal.AtsApiService;
import org.eclipse.osee.ats.core.task.TaskAutoGenArtifactChecks;
import org.eclipse.osee.framework.core.access.ArtifactCheck;
import org.eclipse.osee.framework.core.access.AccessControlUtil;
import org.eclipse.osee.framework.core.access.IOseeAccessProvider;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * Provides for Framework access control questions. Loops through contributed AtsAccessContextProviders to determine
 * which is applicable. If none, default ATS provider is used.
 *
 * @author Donald G. Dunne
 */
public class AtsOseeAccessProvider implements IOseeAccessProvider {

   private final Collection<IAtsAccessContextProvider> providers = new ArrayList<>();
   private AtsAccessContextProvider defaultAtsAccessProvider;
   public static Collection<ArtifactCheck> artChecks = null;

   public AtsOseeAccessProvider() {
      // for jax-rs
      AccessControlUtil.errorf(getClass().getSimpleName());
      getDefaultAtsAccessProvider();
   }

   public void addAtsAccessProvider(IAtsAccessContextProvider provider) {
      AccessControlUtil.errorf(getClass().getSimpleName() + " - " + provider.getClass().getSimpleName());
      providers.add(provider);
   }

   @Override
   public boolean isApplicable(ArtifactToken user, Object object) {
      AtsUser atsUser = AtsApiService.get().getUserService().getUserById(user);
      for (IAtsAccessContextProvider provider : providers) {
         if (provider.isApplicable(atsUser, object)) {
            return true;
         }
      }
      return getDefaultAtsAccessProvider().isApplicable(atsUser, object);
   }

   @Override
   public XResultData hasArtifactContextWriteAccess(ArtifactToken user, Collection<? extends ArtifactToken> artifacts, XResultData rd) {
      AtsUser atsUser = AtsApiService.get().getUserService().getUserById(user);
      for (ArtifactToken artifact : artifacts) {
         boolean found = false;
         for (IAtsAccessContextProvider provider : providers) {
            if (provider.isApplicable(atsUser, artifact)) {
               provider.hasArtifactContextWriteAccess(atsUser, artifacts, rd);
               found = true;
               break;
            }
         }
         if (!found) {
            getDefaultAtsAccessProvider().hasArtifactContextWriteAccess(atsUser, artifacts, rd);
         }
      }
      return rd;
   }

   @Override
   public XResultData hasAttributeTypeContextWriteAccess(ArtifactToken user, Collection<? extends ArtifactToken> artifacts, AttributeTypeToken attributeType, XResultData rd) {
      AtsUser atsUser = AtsApiService.get().getUserService().getUserById(user);
      for (ArtifactToken artifact : artifacts) {
         boolean found = false;
         for (IAtsAccessContextProvider provider : providers) {
            if (provider.isApplicable(atsUser, artifact)) {
               provider.hasAttributeTypeContextWriteAccess(atsUser, artifacts, attributeType, rd);
               found = true;
               break;
            }
         }
         if (!found) {
            getDefaultAtsAccessProvider().hasAttributeTypeContextWriteAccess(atsUser, artifacts, attributeType, rd);
         }
      }
      return rd;
   }

   @Override
   public XResultData hasRelationContextWriteAccess(ArtifactToken user, ArtifactToken artifact, RelationTypeToken relationType, XResultData rd) {
      AtsUser atsUser = AtsApiService.get().getUserService().getUserById(user);
      boolean found = false;
      for (IAtsAccessContextProvider provider : providers) {
         if (provider.isApplicable(atsUser, artifact)) {
            provider.hasRelationContextWriteAccess(atsUser, artifact, relationType, rd);
            found = true;
            break;
         }
      }
      if (!found) {
         getDefaultAtsAccessProvider().hasRelationContextWriteAccess(atsUser, artifact, relationType, rd);
      }
      return rd;
   }

   public IAtsAccessContextProvider getDefaultAtsAccessProvider() {
      if (defaultAtsAccessProvider == null) {
         defaultAtsAccessProvider = new AtsAccessContextProvider();
      }
      AtsApi atsApi = AtsApiService.get();
      defaultAtsAccessProvider.setAtsApi(atsApi);
      return defaultAtsAccessProvider;
   }

   @Override
   public Collection<ArtifactCheck> getArtifactChecks() {
      if (artChecks == null) {
         artChecks = Arrays.asList(new AtsArtifactChecks(), new TaskAutoGenArtifactChecks());
      }
      return artChecks;
   }

}
