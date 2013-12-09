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
package org.eclipse.osee.framework.skynet.core.artifact.search;

import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link SearchRequest}
 * 
 * @author Roberto E. Escobar
 */
public class SearchRequestTest {

   @Test
   public void testDataDefaultOptions() {
      SearchRequest request = new SearchRequest(CoreBranches.COMMON, "Search", new SearchOptions());
      Assert.assertEquals(CoreBranches.COMMON, request.getBranch());
      Assert.assertEquals("Search", request.getRawSearch());
      assertEquals(new SearchOptions(), request.getOptions());
   }

   @Test
   public void testDataNullConstructedOptions() {
      SearchRequest request = new SearchRequest(CoreBranches.COMMON, "Search", new SearchOptions());
      Assert.assertEquals(CoreBranches.COMMON, request.getBranch());
      Assert.assertEquals("Search", request.getRawSearch());
      assertEquals(new SearchOptions(), request.getOptions());
   }

   private static void assertEquals(SearchOptions expected, SearchOptions actual) {
      if (expected == null) {
         Assert.assertNull(actual);
      } else {
         Assert.assertEquals(expected.isSearchAll(), actual.isSearchAll());
         Assert.assertEquals(expected.isCaseSensitive(), actual.isCaseSensitive());
         Assert.assertEquals(expected.isFindAllLocationsEnabled(), actual.isFindAllLocationsEnabled());
         Assert.assertEquals(expected.getDeletionFlag(), actual.getDeletionFlag());
         Assert.assertEquals(expected.isMatchWordOrder(), actual.isMatchWordOrder());

         Assert.assertTrue(Collections.isEqual(expected.getAttributeTypeFilter(), actual.getAttributeTypeFilter()));
      }
   }
}
