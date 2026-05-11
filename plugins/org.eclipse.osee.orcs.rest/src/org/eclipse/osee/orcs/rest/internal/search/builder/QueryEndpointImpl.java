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

package org.eclipse.osee.orcs.rest.internal.search.builder;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.rest.model.search.builder.QueryEndpoint;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryData;

/**
 * @author Donald G. Dunne
 */
public class QueryEndpointImpl implements QueryEndpoint {

   private final OrcsApi orcsApi;

   public QueryEndpointImpl(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   @Override
   public List<ArtifactReadable> query(QueryBuilder queryBuilder) {
      QueryBuilder localQBuild = createLocalQueryBuilder(queryBuilder);
      List<ArtifactReadable> asArtifacts = localQBuild.asArtifacts();
      return asArtifacts;
   }

   @Override
   public List<ArtifactId> queryIds(QueryBuilder queryBuilder) {
      QueryBuilder localQBuild = createLocalQueryBuilder(queryBuilder);
      List<ArtifactId> asArtifacts = localQBuild.asArtifactIds();
      return asArtifacts;
   }

   @Override
   public List<ArtifactId> queryIdsLegacy(QueryBuilder queryBuilder) {
      QueryBuilder localQBuild = createLocalQueryBuilder(queryBuilder);
      List<ArtifactId> results = new ArrayList<>();
      for (ArtifactReadable art : localQBuild.getResults().getList()) {
         results.add(art.getArtifactId());
      }
      return results;
   }

   private QueryBuilder createLocalQueryBuilder(QueryBuilder queryBuilder) {
      QueryData fromQData = (QueryData) queryBuilder;
      BranchToken branch = fromQData.getBranch();
      QueryBuilder toQBuild = orcsApi.getQueryFactory().fromBranch(branch);
      QueryData toQData = (QueryData) toQBuild;
      toQData.setCriteriaSets(fromQData.getCriteriaSets());
      return toQBuild;
   }

}
