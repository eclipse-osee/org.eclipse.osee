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

import junit.framework.Assert;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.message.SearchOptions;
import org.eclipse.osee.framework.core.message.SearchRequest;
import org.eclipse.osee.framework.core.message.test.mocks.DataAsserts;
import org.junit.Test;

/**
 * Test Case for {@link SearchRequest}
 * 
 * @author Roberto E. Escobar
 */
public class SearchRequestTest {

   @Test
   public void testDataDefaultOptions() {
      SearchRequest request = new SearchRequest(CoreBranches.COMMON, "Search");
      Assert.assertEquals(CoreBranches.COMMON, request.getBranch());
      Assert.assertEquals("Search", request.getRawSearch());
      DataAsserts.assertEquals(new SearchOptions(), request.getOptions());
   }

   @Test
   public void testDataNullConstructedOptions() {
      SearchRequest request = new SearchRequest(CoreBranches.COMMON, "Search", null);
      Assert.assertEquals(CoreBranches.COMMON, request.getBranch());
      Assert.assertEquals("Search", request.getRawSearch());
      DataAsserts.assertEquals(new SearchOptions(), request.getOptions());
   }
}
