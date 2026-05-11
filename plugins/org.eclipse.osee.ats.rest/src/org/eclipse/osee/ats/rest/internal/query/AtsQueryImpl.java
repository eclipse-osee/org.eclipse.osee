/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.ats.rest.internal.query;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.query.IAtsQuery;
import org.eclipse.osee.ats.core.query.AbstractAtsQueryImpl;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchViewToken;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Donald G. Dunne
 */
public class AtsQueryImpl extends AbstractAtsQueryImpl {

   private final OrcsApi orcsApi;

   public AtsQueryImpl(AtsApi atsApi, OrcsApi orcsApi) {
      super(atsApi);
      this.orcsApi = orcsApi;
   }

   @Override
   public Collection<? extends ArtifactToken> runQueryLegacy() {
      return query.getResults().getList();
   }

   @Override
   public Collection<? extends ArtifactToken> runQueryAsArts() {
      List<ArtifactReadable> asArtifacts = query.asArtifacts();
      return asArtifacts;
   }

   @Override
   public void createQueryBuilder(ArtifactId configId, BranchId applicBranch) {
      if (query == null) {
         query = orcsApi.getQueryFactory().fromBranch(atsApi.getAtsBranch(), configId, applicBranch);
      }
   }

   @Override
   public Collection<? extends ArtifactToken> runServerQueryAsArts() {
      throw new UnsupportedOperationException();
   }

   @Override
   public void createQueryBuilder() {
      if (query == null) {
         query = orcsApi.getQueryFactory().fromBranch(atsApi.getAtsBranch());
      }
   }

   @Override
   public IAtsQuery andConfiguration(BranchViewToken configTok) {
      if (query != null) {
         throw new OseeStateException("query should not be set");
      }
      query = orcsApi.getQueryFactory().fromBranch(atsApi.getAtsBranch());
      return this;
   }

   @Override
   public void queryAnd(AttributeTypeToken attrType, Collection<String> values) {
      query.and(attrType, values);
   }

   @Override
   public IAtsQuery getAtsQuery() {
      return new AtsQueryImpl(atsApi, orcsApi);
   }

   @Override
   public Collection<? extends ArtifactId> runQueryIds() {
      return query.asArtifactIds();
   }

   @SuppressWarnings("unchecked")
   @Override
   protected <T> Collection<T> collectResults(Set<T> allResults, Set<ArtifactTypeToken> allArtTypes,
      boolean newSearch) {
      Set<T> workItems = new HashSet<>();
      if (isOnlyIds()) {
         validateReleasedOption();
         onlyIds.addAll(queryGetIds());
      }
      // filter on original artifact types
      else {
         for (ArtifactToken artifact : newSearch ? runQueryAsArts() : runQueryLegacy()) {
            if (isArtifactTypeMatch(artifact, allArtTypes)) {
               IAtsWorkItem workItem = atsApi.getWorkItemService().getWorkItem(artifact);
               if (workItem != null) {
                  workItems.add((T) workItem);
               }
            }
         }
         addtoResultsWithNullCheck(allResults, handleReleasedOption(workItems));
      }
      return workItems;
   }

   @Override
   public void andBuildImpact() {
      createQueryBuilder();
      query.follow(AtsRelationTypes.ProblemReportToBid_Bid);
   }

}
