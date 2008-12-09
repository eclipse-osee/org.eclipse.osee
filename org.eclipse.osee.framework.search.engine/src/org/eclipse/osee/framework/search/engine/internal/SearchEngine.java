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

import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.JoinUtility;
import org.eclipse.osee.framework.core.data.JoinUtility.ArtifactJoinQuery;
import org.eclipse.osee.framework.logging.OseeLog;
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
   public String search(String searchString, int branchId, Options options, String... attributeTypes) throws Exception {
      long startTime = System.currentTimeMillis();
      ArtifactJoinQuery joinQuery = JoinUtility.createArtifactJoinQuery();
      IAttributeTaggerProviderManager manager = Activator.getTaggerManager();
      AttributeSearch attributeSearch = new AttributeSearch(searchString, branchId, options, attributeTypes);
      Collection<AttributeData> tagMatches = attributeSearch.getMatchingAttributes();
      long timeAfterPass1 = System.currentTimeMillis() - startTime;
      long secondPass = System.currentTimeMillis();

      boolean bypassSecondPass = !options.getBoolean("match word order");
      if (bypassSecondPass) {
         for (AttributeData attributeData : tagMatches) {
            joinQuery.add(attributeData.getArtId(), attributeData.getBranchId());
         }
      } else {
         for (AttributeData attributeData : tagMatches) {
            try {
               if (manager.find(attributeData, searchString)) {
                  joinQuery.add(attributeData.getArtId(), attributeData.getBranchId());
               }
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, String.format("Error processing: [%s]", attributeData));
            }
         }
      }
      secondPass = System.currentTimeMillis() - secondPass;
      joinQuery.store();

      String firstPassMsg =
            String.format("Pass 1: [%d items in %d ms]);", bypassSecondPass ? joinQuery.size() : tagMatches.size(),
                  timeAfterPass1);
      String secondPassMsg = String.format(" Pass 2: [%d items in %d ms]", joinQuery.size(), secondPass);

      System.out.println(String.format("Search for [%s] - %s%s", searchString, firstPassMsg,
            bypassSecondPass ? "" : secondPassMsg));
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
