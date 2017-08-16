/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.utility;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.sql.OseeSql;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoader;

/**
 * @author Megumi Telles
 */
public class ViewIdUtility {

   public static Set<ArtifactId> findExcludedArtifactsByView(ArtifactId viewId, BranchId branch) {
      Set<ArtifactId> excludedArtifactIdMap = new HashSet<>();
      if (!viewId.equals(ArtifactId.SENTINEL)) {
         Object[] objs = {branch, viewId, branch};
         List<ArtifactId> excludedArtifacts =
            ArtifactLoader.selectArtifactIds(OseeSql.LOAD_EXCLUDED_ARTIFACT_IDS.getSql(), objs, 300);
         for (ArtifactId artId : excludedArtifacts) {
            excludedArtifactIdMap.add(artId);
         }
      }
      return excludedArtifactIdMap;
   }

   public static void removeExcludedArtifacts(Iterator<Artifact> iterator, Set<ArtifactId> excludedArtifactIdMap) {
      while (iterator.hasNext()) {
         Artifact artifact = iterator.next();
         if (excludedArtifactIdMap.contains(ArtifactId.valueOf(artifact.getId()))) {
            iterator.remove();
         }
      }
   }

}
