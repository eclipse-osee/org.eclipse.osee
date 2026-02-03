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

package org.eclipse.osee.ats.ide.search.internal.query;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.query.IAtsQuery;
import org.eclipse.osee.ats.core.query.AbstractAtsQueryImpl;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchViewToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.search.QueryBuilderArtifact;
import org.eclipse.osee.framework.skynet.core.utility.OrcsQueryService;

/**
 * @author Donald G. Dunne
 */
public class AtsQueryImpl extends AbstractAtsQueryImpl {

   public AtsQueryImpl(AtsApi atsApi) {
      super(atsApi);
   }

   @Override
   public Collection<? extends ArtifactToken> runQueryLegacy() {
      return OrcsQueryService.queryLegacy(query);
   }

   @Override
   public Collection<? extends ArtifactId> runQueryIds() {
      return OrcsQueryService.queryIds(query);
   }

   @Override
   public Collection<? extends ArtifactToken> runServerQueryAsArts() {
      return OrcsQueryService.query(query);
   }

   @Override
   public Collection<? extends ArtifactToken> runQueryAsArts() {
      throw new UnsupportedOperationException();
   }

   @Override
   public void createQueryBuilder(ArtifactId configId, BranchId applicBranch) {
      throw new UnsupportedOperationException("Not supported on client");
   }

   @Override
   public void createQueryBuilder() {
      if (query == null) {
         query = atsApi.getQueryService().fromAtsBranch();
      }
   }

   @Override
   @SuppressWarnings("unchecked")
   protected <T> Collection<T> collectResults(Set<T> allResults, Set<ArtifactTypeToken> allArtTypes,
      boolean newSearch) {
      Set<T> workItems = new HashSet<>();
      if (isOnlyIds()) {
         validateReleasedOption();
         onlyIds.addAll(runQueryIds());
      }
      // filter on original artifact types
      else {
         for (ArtifactToken artifact : newSearch ? runServerQueryAsArts() : runQueryLegacy()) {
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
   public IAtsQuery getAtsQuery() {
      return new AtsQueryImpl(atsApi);
   }

   @Override
   public IAtsQuery andConfiguration(BranchViewToken configTok) {
      throw new UnsupportedOperationException("Not supported on client");
   }

   @Override
   public void andBuildImpact(String buildImpact) {
      throw new UnsupportedOperationException("Not supported on client");
   }

}
