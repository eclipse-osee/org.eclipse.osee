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
package org.eclipse.osee.framework.search.engine.test.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.core.DbTransaction;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.database.core.JoinUtility.TransactionJoinQuery;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.search.engine.data.IAttributeLocator;
import org.eclipse.osee.framework.search.engine.data.SearchTag;
import org.eclipse.osee.framework.search.engine.utility.SearchTagDataStore;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test Case for {@link SearchTagDataStore}
 * 
 * @author Roberto E. Escobar
 */
public class SearchTagDataStoreTest {

   private List<SearchTag> getTestSearchTagDataStoreData() {
      List<SearchTag> tags = new ArrayList<SearchTag>();

      SearchTag tag = new SearchTag(2345);
      tag.addTag(-99999999999L);
      tag.addTag(-99999999998L);
      tags.add(tag);

      tag = new SearchTag(91011);
      tag.addTag(-99999999997L);
      tag.addTag(-99999999996L);
      tags.add(tag);

      tag = new SearchTag(15161718);
      tag.addTag(-99999999995L);
      tag.addTag(-99999999994L);
      tags.add(tag);

      return tags;
   }

   @Ignore
   // Decouple from database
   @Test
   public void testSearchTagDataStore() throws OseeCoreException {
      new DbTransaction() {
         @Override
         protected void handleTxWork(OseeConnection connection) throws OseeCoreException {
            List<SearchTag> testData = getTestSearchTagDataStoreData();
            int totalTags = 0;
            for (SearchTag searchTag : testData) {
               totalTags += searchTag.cacheSize();
            }
            int updated = SearchTagDataStore.storeTags(connection, testData);
            Assert.assertEquals(totalTags, updated);

            for (SearchTag tag : testData) {
               for (Long codedTag : tag.getTags()) {
                  Set<IAttributeLocator> locators = SearchTagDataStore.fetchTagEntries(connection, codedTag);
                  Assert.assertEquals(locators.size(), 1);
                  IAttributeLocator locator = locators.iterator().next();
                  Assert.assertEquals(locator.getGammaId(), tag.getGammaId());
               }
            }

            List<IAttributeLocator> locators = Collections.castAll(testData);
            updated = SearchTagDataStore.deleteTags(connection, locators);
            Assert.assertEquals(totalTags, updated);
         }
      }.execute();
   }

   @Ignore
   // Decouple from database
   @Test
   public void testSearchTagDataStoreDeleteByQuery() throws Exception {
      new DbTransaction() {
         @Override
         protected void handleTxWork(OseeConnection connection) throws OseeCoreException {
            List<SearchTag> testData = getTestSearchTagDataStoreData();
            TransactionJoinQuery joinQuery = null;
            int totalTags = 0;
            for (SearchTag searchTag : testData) {
               totalTags += searchTag.cacheSize();
            }

            int updated = SearchTagDataStore.storeTags(connection, testData);
            Assert.assertEquals(totalTags, updated);

            for (SearchTag tag : testData) {
               for (Long codedTag : tag.getTags()) {
                  Set<IAttributeLocator> locators = SearchTagDataStore.fetchTagEntries(connection, codedTag);
                  Assert.assertEquals(locators.size(), 1);
                  IAttributeLocator locator = locators.iterator().next();
                  Assert.assertEquals(locator.getGammaId(), tag.getGammaId());
               }
            }
            try {
               joinQuery = JoinUtility.createTransactionJoinQuery();
               for (SearchTag tag : testData) {
                  joinQuery.add(tag.getGammaId(), -1);
               }
               joinQuery.store(connection);
               updated = SearchTagDataStore.deleteTags(connection, joinQuery.getQueryId());
               Assert.assertEquals(totalTags, updated);
            } finally {
               if (joinQuery != null) {
                  joinQuery.delete(connection);
               }
            }
         }
      }.execute();
   }
}
