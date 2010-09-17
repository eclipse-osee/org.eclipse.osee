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
package org.eclipse.osee.framework.core.message.test.data;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.message.SearchResponse;
import org.eclipse.osee.framework.core.message.SearchResponse.ArtifactMatchMetaData;
import org.eclipse.osee.framework.core.message.SearchResponse.AttributeMatchMetaData;
import org.eclipse.osee.framework.jdk.core.type.MatchLocation;
import org.eclipse.osee.framework.jdk.core.util.Compare;
import org.junit.Test;

/**
 * Test Case for {@link SearchResponse}
 * 
 * @author Roberto E. Escobar
 */
public class SearchResponseTest {

   @Test
   public void testSetGetErrorMessage() {
      SearchResponse searchResponse = new SearchResponse();
      Assert.assertEquals("", searchResponse.getErrorMessage());

      searchResponse.setErrorMessage(null);
      Assert.assertEquals("", searchResponse.getErrorMessage());

      searchResponse.setErrorMessage("An error");
      Assert.assertEquals("An error", searchResponse.getErrorMessage());
   }

   @Test
   public void testSearchTags() {
      SearchResponse searchResponse = new SearchResponse();
      Assert.assertEquals(0, searchResponse.getSearchTags().size());

      Map<String, Long> tags = searchResponse.getSearchTags();
      tags.put("Hello", 45L);
      tags.put("dude", 54L);
      tags.put("Hello", 63L);

      Assert.assertEquals(2, searchResponse.getSearchTags().size());
      Set<String> words = tags.keySet();
      Assert.assertEquals(2, words.size());
      Iterator<String> iterator = words.iterator();
      Assert.assertEquals("Hello", iterator.next());
      Assert.assertEquals("dude", iterator.next());

      Collection<Long> codes = tags.values();
      Assert.assertEquals(2, codes.size());
      Iterator<Long> codeIterator = codes.iterator();
      Assert.assertEquals(63L, (long) codeIterator.next());
      Assert.assertEquals(54L, (long) codeIterator.next());
   }

   @Test
   public void testAddMatch() {
      MatchLocation loc1 = new MatchLocation(3, 6);
      MatchLocation loc2 = new MatchLocation(11, 20);

      SearchResponse searchResponse = new SearchResponse();
      searchResponse.add(1, 2, 3);
      searchResponse.add(1, 2, 4);
      searchResponse.add(1, 2, 5);
      searchResponse.add(1, 2, 5);

      searchResponse.add(2, 1, 6);
      searchResponse.add(2, 1, 8);
      searchResponse.add(2, 3, 8, Arrays.asList(loc1, loc2));
      searchResponse.add(2, 3, 8, 4, 5);
      searchResponse.add(2, 3, 8, 4, 5);

      Assert.assertEquals(8, searchResponse.matches());

      Assert.assertFalse(Compare.isDifferent(Arrays.asList(1, 2), searchResponse.getBranchIds()));

      // Check Branch 1
      Collection<Integer> data1 = searchResponse.getArtifactIds(1);
      Assert.assertEquals(1, data1.size());
      Assert.assertEquals(2, (int) data1.iterator().next());

      Collection<ArtifactMatchMetaData> arts1 = searchResponse.getArtifacts(1);
      Assert.assertEquals(1, arts1.size());

      ArtifactMatchMetaData match1 = searchResponse.getArtifactMatch(1, 2);
      Assert.assertNotNull(match1);

      Assert.assertEquals(arts1.iterator().next(), match1);
      checkArtMatch(match1, 1, 2, 3L, 4L, 5L);

      // Check Branch 2
      Collection<Integer> data2 = searchResponse.getArtifactIds(2);
      Assert.assertEquals(2, data2.size());

      Collection<ArtifactMatchMetaData> arts = searchResponse.getArtifacts(2);
      Assert.assertEquals(2, arts.size());

      ArtifactMatchMetaData match2 = searchResponse.getArtifactMatch(2, 1);
      Assert.assertNotNull(match2);

      ArtifactMatchMetaData match3 = searchResponse.getArtifactMatch(2, 3);
      Assert.assertNotNull(match3);

      Assert.assertFalse(Compare.isDifferent(Arrays.asList(match2, match3), arts));

      checkArtMatch(match2, 2, 1, 6L, 8L);

      Assert.assertEquals(3, match3.getArtId());
      Assert.assertEquals(2, match3.getBranchId());
      Assert.assertEquals(3, match3.matches());

      AttributeMatchMetaData attrMatch = match3.getAttributeMatch(8L);
      checkAttrMatch(attrMatch, 3, 8L, loc1, loc2, new MatchLocation(4, 5));

      Assert.assertFalse(Compare.isDifferent(Arrays.asList(match1, match2, match3), searchResponse.getAll()));
   }

   private static void checkArtMatch(ArtifactMatchMetaData data, int expectedBranchId, int expectedArtId, long... attrs) {
      Assert.assertEquals(expectedArtId, data.getArtId());
      Assert.assertEquals(expectedBranchId, data.getBranchId());
      Assert.assertEquals(attrs.length, data.matches());
      Assert.assertEquals(attrs.length, data.size());
      for (long attr : attrs) {
         AttributeMatchMetaData attrData = data.getAttributeMatch(attr);
         Assert.assertNotNull("Can't get attrData id: " + attr, attrData);
         checkAttrMatch(attrData, expectedArtId, attr);
      }
   }

   private static void checkAttrMatch(AttributeMatchMetaData data, int expectedArtId, long expectedGammaId, MatchLocation... expectedLocs) {
      Assert.assertEquals(expectedArtId, data.getArtId());
      Assert.assertEquals(expectedGammaId, data.getGammaId());
      Collection<MatchLocation> actualLocs = data.getLocations();
      Assert.assertEquals(expectedLocs.length, actualLocs.size());
      Assert.assertFalse(Compare.isDifferent(Arrays.asList(expectedLocs), actualLocs));
   }
}
