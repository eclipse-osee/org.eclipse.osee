/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.framework.skynet.core.utility;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.skynet.core.OseeApiService;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryData;

/**
 * @author Donald G. Dunne
 */
public class OrcsQueryService {

   /**
    * Creates a server QueryBuilder that will be passed to server to run and resulting artifacts returned as json and
    * injected into the client expected Artifact class. This is the future and all IDE client calls should be migrated
    * to use this mechanism. All the above queries should migrate to this also.
    */
   public static QueryBuilder fromBranch(BranchToken branch) {
      return new QueryData(null, null, null, null, branch);
   }

   public static List<Artifact> loadArtifacts(List<? extends ArtifactId> artIds, BranchId branch) {
      return ArtifactQuery.getArtifactListFrom(artIds, branch);
   }

   public static List<Artifact> query(QueryBuilder query) {
      BranchToken branch = ((QueryData) query).getBranch();
      List<? extends ArtifactId> artToks = queryIds(query);
      List<Artifact> arts = loadArtifacts(artToks, branch);
      return arts;
   }

   public static List<? extends ArtifactId> queryIds(QueryBuilder query) {
      List<ArtifactId> artIds = OseeApiService.serverEnpoints().getQueryEndpoint().queryIds(query);
      return artIds;
   }

   public static Collection<? extends ArtifactToken> queryLegacy(QueryBuilder query) {
      BranchToken branch = ((QueryData) query).getBranch();
      List<? extends ArtifactId> artToks = OseeApiService.serverEnpoints().getQueryEndpoint().queryIdsLegacy(query);
      List<Artifact> arts = loadArtifacts(artToks, branch);
      return arts;
   }

}
