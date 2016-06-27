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
import org.eclipse.osee.ats.api.config.IAtsCache;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.search.QueryBuilderArtifact;

/**
 * @author Donald G. Dunne
 */
public class LoadAtsConfigCacheCallable {

   private final IAtsCache cache;

   public LoadAtsConfigCacheCallable(IAtsCache cache) {
      this.cache = cache;
   }

   public void run() throws Exception {

      List<IArtifactType> typesToLoad = getTypesToLoad();
      QueryBuilderArtifact query = ArtifactQuery.createQueryBuilder(AtsUtilCore.getAtsBranch());
      query.andTypeEquals(typesToLoad).and(AtsAttributeTypes.Active, "true");
      List<Artifact> artifactListFromType = query.getResults().getList();

      for (Artifact artifact : artifactListFromType) {
         cache.cacheArtifact(artifact);
      }
   }

   private List<IArtifactType> getTypesToLoad() {
      return Arrays.asList(AtsArtifactTypes.TeamDefinition, AtsArtifactTypes.ActionableItem, AtsArtifactTypes.Version);
   }

}