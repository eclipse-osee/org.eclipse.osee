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
package org.eclipse.osee.ats.core.client.internal.config;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.core.client.internal.IAtsArtifactStore;
import org.eclipse.osee.ats.core.client.util.AtsUtilCore;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Donald G. Dunne
 */
public class LoadAtsConfigCacheCallable implements Callable<AtsArtifactConfigCache> {

   private final IAtsArtifactStore artifactStore;

   public LoadAtsConfigCacheCallable(IAtsArtifactStore artifactStore) {
      this.artifactStore = artifactStore;
   }

   @Override
   public AtsArtifactConfigCache call() throws Exception {
      AtsArtifactConfigCache cache = new AtsArtifactConfigCache();

      List<IArtifactType> typesToLoad = getTypesToLoad();
      List<Artifact> artifactListFromType =
         ArtifactQuery.getArtifactListFromType(typesToLoad, AtsUtilCore.getAtsBranchToken(),
            DeletionFlag.EXCLUDE_DELETED);

      for (Artifact artifact : artifactListFromType) {
         loadAtsConfigCacheArtifacts(artifactStore, cache, artifact);
      }
      return cache;
   }

   private List<IArtifactType> getTypesToLoad() {
      return Arrays.asList(AtsArtifactTypes.TeamDefinition, AtsArtifactTypes.ActionableItem, AtsArtifactTypes.Version);
   }

   private void loadAtsConfigCacheArtifacts(IAtsArtifactStore artifactStore, AtsArtifactConfigCache cache, Artifact artifact) throws OseeCoreException {
      if (artifact.isOfType(AtsArtifactTypes.TeamDefinition)) {
         IAtsTeamDefinition teamDef = artifactStore.load(cache, artifact);

         for (String staticId : artifact.getAttributesToStringList(CoreAttributeTypes.StaticId)) {
            cache.cacheByTag(staticId, teamDef);
         }
      }
      if (artifact.isOfType(AtsArtifactTypes.ActionableItem)) {
         IAtsActionableItem ai = artifactStore.load(cache, artifact);

         for (String staticId : artifact.getAttributesToStringList(CoreAttributeTypes.StaticId)) {
            cache.cacheByTag(staticId, ai);
         }
      }
      if (artifact.isOfType(AtsArtifactTypes.Version)) {
         IAtsVersion version = artifactStore.load(cache, artifact);

         for (String staticId : artifact.getAttributesToStringList(CoreAttributeTypes.StaticId)) {
            cache.cacheByTag(staticId, version);
         }
      }
   }
}