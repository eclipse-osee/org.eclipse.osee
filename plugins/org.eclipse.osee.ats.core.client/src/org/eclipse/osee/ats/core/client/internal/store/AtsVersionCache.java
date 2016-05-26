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

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.concurrent.TimeUnit;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * This uses the config cache to cache the relation between the team workflow and version.<br/>
 * NOTE: Each teamWf can only have one version
 *
 * @author Donald G. Dunne
 */
public class AtsVersionCache {

   private final IAtsServices services;
   public final CacheLoader<Long, IAtsVersion> teamWfUuidToVersionCacheLoader = new CacheLoader<Long, IAtsVersion>() {
      @Override
      public IAtsVersion load(Long uuid) {
         return services.getConfigItemFactory().getVersion(services.getArtifact(uuid));
      }
   };
   private final LoadingCache<Long, IAtsVersion> teamWfUuidToVersionCache = CacheBuilder.newBuilder() //
      .expireAfterWrite(15, TimeUnit.MINUTES) //
      .build(teamWfUuidToVersionCacheLoader);

   public AtsVersionCache(IAtsServices services) {
      this.services = services;
   }

   public IAtsVersion getVersion(IAtsTeamWorkflow teamWf) {
      return teamWfUuidToVersionCache.getIfPresent(teamWf.getUuid());
   }

   public boolean hasVersion(IAtsTeamWorkflow teamWf) throws OseeCoreException {
      return getVersion(teamWf) != null;
   }

   public IAtsVersion cache(IAtsTeamWorkflow teamWf, IAtsVersion version) throws OseeCoreException {
      if (version != null) {
      teamWfUuidToVersionCache.put(teamWf.getUuid(), version);
      }
      return version;
   }

   public void deCache(IAtsTeamWorkflow teamWf) throws OseeCoreException {
      teamWfUuidToVersionCache.invalidate(teamWf.getUuid());
   }

   public void invalidateCache() {
      teamWfUuidToVersionCache.invalidateAll();
   }

}