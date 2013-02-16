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
package org.eclipse.osee.ats.core.client.config;

import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.core.client.config.store.ActionableItemArtifactStore;
import org.eclipse.osee.ats.core.client.config.store.TeamDefinitionArtifactStore;
import org.eclipse.osee.ats.core.client.config.store.VersionArtifactStore;
import org.eclipse.osee.ats.core.config.AtsConfigCache;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class AtsConfigCacheLoaderClient {

   private final AtsConfigCache cache;

   public AtsConfigCacheLoaderClient(AtsConfigCache cache) {
      this.cache = cache;
   }

   public void cacheConfigArtifact(Artifact artifact) throws OseeCoreException {
      // cache
      if (artifact.isOfType(AtsArtifactTypes.TeamDefinition)) {
         TeamDefinitionArtifactStore store = new TeamDefinitionArtifactStore(artifact, cache);
         IAtsTeamDefinition teamDef = store.getTeamDefinition();

         for (String staticId : artifact.getAttributesToStringList(CoreAttributeTypes.StaticId)) {
            cache.cacheByTag(staticId, teamDef);
         }
      }
      if (artifact.isOfType(AtsArtifactTypes.ActionableItem)) {
         ActionableItemArtifactStore store = new ActionableItemArtifactStore(artifact, cache);
         IAtsActionableItem ai = store.getActionableItem();

         for (String staticId : artifact.getAttributesToStringList(CoreAttributeTypes.StaticId)) {
            cache.cacheByTag(staticId, ai);
         }
      }
      if (artifact.isOfType(AtsArtifactTypes.Version)) {
         VersionArtifactStore store = new VersionArtifactStore(artifact, cache);
         IAtsVersion version = store.getVersion();

         for (String staticId : artifact.getAttributesToStringList(CoreAttributeTypes.StaticId)) {
            cache.cacheByTag(staticId, version);
         }
      }
   }

}
