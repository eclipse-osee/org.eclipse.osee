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

package org.eclipse.osee.ats.ide.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.query.AtsSearchDataResults;
import org.eclipse.osee.ats.api.query.IAtsQuery;
import org.eclipse.osee.ats.api.query.ISearchCriteriaProvider;
import org.eclipse.osee.ats.core.query.AtsSearchDataSearch;
import org.eclipse.osee.ats.core.query.AtsSearchDataVersionSearch;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.world.search.WorldUISearchItem;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;

/**
 * @author Donald G. Dunne
 */
public class WorldSearchDataItem extends WorldUISearchItem implements ISearchCriteriaProvider {

   AtsSearchData data;
   AtsApi atsApi;

   public WorldSearchDataItem(AtsSearchData data) {
      super(data.getSearchName());
      this.data = data.copy();
      this.atsApi = AtsApiService.get();
   }

   public WorldSearchDataItem(String searchName) {
      super(searchName);
      data = new AtsSearchData(searchName);
   }

   @Override
   public String getSelectedName(SearchType searchType) {
      return super.getSelectedName(searchType);
   }

   @Override
   public Collection<Artifact> performSearch(SearchType searchType) {

      // Version search performs better by starting from the version and filtering
      if (data.getVersionId() != null && data.getVersionId() > 0) {
         AtsSearchDataVersionSearch query = new AtsSearchDataVersionSearch(data, AtsApiService.get());
         AtsSearchDataResults results = query.performSearch();
         if (results.getRd().isErrors()) {
            XResultDataUI.report(results.getRd(), getName());
         }
         return Collections.castAll(results.getArtifacts());
      }

      AtsSearchDataSearch query = new AtsSearchDataSearch(data, AtsApiService.get(), this);
      AtsSearchDataResults results = query.performSearch();
      if (results.getRd().isErrors()) {
         XResultDataUI.report(results.getRd(), getName());
      }

      return Collections.castAll(results.getArtifacts());
   }

   @Override
   public Collection<Artifact> performSearchAsArtifacts(SearchType searchType) {
      Set<Artifact> arts = new HashSet<>();

      XResultData results = atsApi.getServerEndpoints().getActionEndpoint().queryIds(data);
      if (results.isErrors()) {
         XResultDataUI.report(results, getName());
      } else {
         List<ArtifactId> artIds = new ArrayList<>();
         results.getIds().stream().forEach(id -> artIds.add(ArtifactId.valueOf(id)));
         arts.addAll(ArtifactQuery.getArtifactListFrom(artIds, atsApi.getAtsBranch()));
      }

      return arts;
   }

   /**
    * Implement to populate query with extended options
    */
   protected void performSearch(IAtsQuery query) {
      // do nothing
   }

   @Override
   public WorldUISearchItem copy() {
      return new WorldSearchDataItem(data);
   }

   public AtsSearchData getData() {
      return data;
   }

   @Override
   public void andCriteria(IAtsQuery query) {
      performSearch(query);
   }

}
