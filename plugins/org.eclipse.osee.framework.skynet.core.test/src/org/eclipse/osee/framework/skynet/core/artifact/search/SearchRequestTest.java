/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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
         Assert.assertEquals(expected.getDeletionFlag(), actual.getDeletionFlag());
         Assert.assertEquals(expected.isMatchWordOrder(), actual.isMatchWordOrder());

         Assert.assertTrue(Collections.isEqual(expected.getAttributeTypeFilter(), actual.getAttributeTypeFilter()));
      }
   }
}
