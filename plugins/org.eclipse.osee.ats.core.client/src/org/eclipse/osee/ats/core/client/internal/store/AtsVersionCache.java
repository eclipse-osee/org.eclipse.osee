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
package org.eclipse.osee.ats.core.client.internal.store;

import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.client.internal.config.AtsArtifactConfigCache;
import org.eclipse.osee.ats.core.util.CacheProvider;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * This uses the config cache to cache the relation between the team workflow and version.<br/>
 * NOTE: Each teamWf can only have one version
 * 
 * @author Donald G. Dunne
 */
public class AtsVersionCache {

   CacheProvider<AtsArtifactConfigCache> configCacheProvider;

   public AtsVersionCache(CacheProvider<AtsArtifactConfigCache> configCacheProvider) {
      this.configCacheProvider = configCacheProvider;
   }

   public IAtsVersion getVersion(IAtsTeamWorkflow teamWf) throws OseeCoreException {
      return configCacheProvider.get().getSoleByUuid(teamWf.getUuid(), IAtsVersion.class);
   }

   public boolean hasVersion(IAtsTeamWorkflow teamWf) throws OseeCoreException {
      IAtsVersion version = getVersion(teamWf);
      return version != null;
   }

   public IAtsVersion cache(IAtsTeamWorkflow teamWf, IAtsVersion version) throws OseeCoreException {
      configCacheProvider.get().cacheById(teamWf.getUuid(), version);
      return version;
   }

   public void deCache(IAtsTeamWorkflow teamWf) throws OseeCoreException {
      configCacheProvider.get().invalidateByUuid(teamWf.getUuid());
   }

   public void invalidateCache() {
      configCacheProvider.invalidate();
   }

}