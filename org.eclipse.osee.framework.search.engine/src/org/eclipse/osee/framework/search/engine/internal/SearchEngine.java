/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.search.engine.internal;

import java.util.List;
import org.eclipse.osee.framework.db.connection.core.JoinUtility;
import org.eclipse.osee.framework.db.connection.core.JoinUtility.ArtifactJoinQuery;
import org.eclipse.osee.framework.search.engine.Activator;
import org.eclipse.osee.framework.search.engine.IAttributeTaggerProviderManager;
import org.eclipse.osee.framework.search.engine.ISearchEngine;
import org.eclipse.osee.framework.search.engine.Options;
import org.eclipse.osee.framework.search.engine.attribute.AttributeData;
import org.eclipse.osee.framework.search.engine.data.AttributeSearch;

/**
 * @author Roberto E. Escobar
 */
public class SearchEngine implements ISearchEngine {

   private SearchStatistics statistics;

   public SearchEngine() {
      this.statistics = new SearchStatistics();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.search.engine.ISearchEngine#search(java.lang.String, org.eclipse.osee.framework.search.engine.Options)
    */
   @Override
   public String search(String searchString, int branchId, Options options) throws Exception {
      long startTime = System.currentTimeMillis();
      AttributeSearch attributeSearch = new AttributeSearch(searchString, branchId, options);
      List<AttributeData> attributes = attributeSearch.getMatchingAttributes();

      ArtifactJoinQuery joinQuery = JoinUtility.createArtifactJoinQuery();
      IAttributeTaggerProviderManager manager = Activator.getInstance().getTaggerManager();
      for (AttributeData attributeData : attributes) {
         if (manager.find(attributeData, searchString)) {
            joinQuery.add(attributeData.getArtId(), attributeData.getBranchId());
         }
      }
      joinQuery.store();
      statistics.addEntry(searchString, branchId, options, joinQuery.size(), System.currentTimeMillis() - startTime);
      return String.format("%d,%d", joinQuery.getQueryId(), joinQuery.size());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.search.engine.ISearchEngine#clearStatistics()
    */
   @Override
   public void clearStatistics() {
      this.statistics.clear();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.search.engine.ISearchEngine#getStatistics()
    */
   @Override
   public SearchStatistics getStatistics() {
      try {
         return this.statistics.clone();
      } catch (CloneNotSupportedException ex) {
         return SearchStatistics.EMPTY_STATS;
      }
   }
}
