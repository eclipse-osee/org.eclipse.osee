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
package org.eclipse.osee.framework.core.test.data;

import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.data.CacheUpdateRequest;
import org.eclipse.osee.framework.core.enums.OseeCacheEnum;
import org.junit.Test;

/**
 * Test Case for {@link CacheUpdateRequest}
 * 
 * @author Roberto E. Escobar
 */
public class CacheUpdateRequestTest {

   @Test
   public void testNoGuids() {
      CacheUpdateRequest request = new CacheUpdateRequest(OseeCacheEnum.ARTIFACT_TYPE_CACHE);
      Assert.assertEquals(OseeCacheEnum.ARTIFACT_TYPE_CACHE, request.getCacheId());
      Assert.assertTrue(request.getItemsIds().isEmpty());
   }

   @Test
   public void testWithGuids() {
      List<Integer> guids = Arrays.asList(45, 55);
      CacheUpdateRequest request = new CacheUpdateRequest(OseeCacheEnum.ATTRIBUTE_TYPE_CACHE, guids);
      Assert.assertEquals(OseeCacheEnum.ATTRIBUTE_TYPE_CACHE, request.getCacheId());
      Assert.assertEquals(guids, request.getItemsIds());
   }
}
