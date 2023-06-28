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

import java.util.Collection;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.query.AtsSearchDataResults;
import org.eclipse.osee.ats.api.query.IAtsQuery;
import org.eclipse.osee.ats.api.query.ISearchCriteriaProvider;
import org.eclipse.osee.ats.core.query.AtsSearchDataSearch;
import org.eclipse.osee.ats.core.query.AtsSearchDataVersionSearch;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.world.search.WorldUISearchItem;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;

/**
 * @author Donald G. Dunne
 */
public class WorldSearchItem extends WorldUISearchItem implements ISearchCriteriaProvider {

   AtsSearchData data;

   public WorldSearchItem(AtsSearchData data) {
      super(data.getSearchName());
      this.data = data.copy();
   }

   public WorldSearchItem(String searchName) {
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
      // Else do query with search criteria
      AtsSearchDataSearch query = new AtsSearchDataSearch(data, AtsApiService.get(), this);
      AtsSearchDataResults results = query.performSearch();
      if (results.getRd().isErrors()) {
         XResultDataUI.report(results.getRd(), getName());
      }
      return Collections.castAll(results.getArtifacts());
   }

   /**
    * Implement to populate query with extended options
    */
   protected void performSearch(IAtsQuery query) {
      // do nothing
   }

   @Override
   public WorldUISearchItem copy() {
      return new WorldSearchItem(data);
   }

   public AtsSearchData getData() {
      return data;
   }

   @Override
   public void andCriteria(IAtsQuery query) {
      performSearch(query);
   }

}
