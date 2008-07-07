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
package org.eclipse.osee.framework.search.engine.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import junit.framework.TestCase;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.search.engine.ISearchTagger;
import org.eclipse.osee.framework.search.engine.Options;
import org.eclipse.osee.framework.search.engine.data.IAttributeLocator;
import org.eclipse.osee.framework.search.engine.data.SearchTag;
import org.eclipse.osee.framework.search.engine.utility.SearchTagDataStore;

/**
 * @author Roberto E. Escobar
 */
public class TestSearchEngine extends TestCase {

   public void testGettingSearchEngine() {
      assertNotNull(Activator.getInstance().getSearchEngine());
   }

   public void testGettingSearchTagger() {
      assertNotNull(Activator.getInstance().getSearchTagger());
   }

   private List<SearchTag> getTestSearchTagDataStoreData() {
      List<SearchTag> tags = new ArrayList<SearchTag>();

      SearchTag tag = new SearchTag(1, 2345);
      tag.addTag(6);
      tag.addTag(7);
      tags.add(tag);

      tag = new SearchTag(8, 91011);
      tag.addTag(12);
      tag.addTag(13);

      tags.add(tag);
      return tags;
   }

   public void testSearchTagDataStore() {
      List<SearchTag> testData = getTestSearchTagDataStoreData();
      try {
         int totalTags = 0;
         for (SearchTag searchTag : testData) {
            totalTags += searchTag.size();
         }

         int updated = SearchTagDataStore.storeTags(testData);
         assertEquals(totalTags, updated);

         for (SearchTag tag : testData) {
            for (Long codedTag : tag.getTags()) {
               Set<IAttributeLocator> locators = SearchTagDataStore.fetchTagEntries(new Options(), codedTag);
               assertEquals(locators.size(), 1);
               IAttributeLocator locator = locators.iterator().next();
               assertEquals(locator.getAttrId(), tag.getAttrId());
               assertEquals(locator.getGamma_id(), tag.getGamma_id());
            }
         }

         List<IAttributeLocator> locators = Collections.castAll(testData);
         updated = SearchTagDataStore.deleteTags(locators);
         assertEquals(totalTags, updated);

      } catch (Exception ex) {
         assertTrue(ex.getLocalizedMessage(), false);
      }
   }

   public void test() {
      // create dummy attribute

      // tag it
      ISearchTagger tagger = Activator.getInstance().getSearchTagger();
      tagger.submitForTagging(3, 4);

      // check tagged

      // delete dummy attribute

   }
}