/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.test.renderer;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.BranchFactory;
import org.eclipse.osee.framework.core.model.cache.AbstractOseeCache;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.test.mocks.MockOseeDataAccessor;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
import org.junit.Test;

/**
 * @author Roberto E. Escobar
 */
public class RenderingUtilTest {
   @Test
   public void testBranchToFileName() throws Exception {
      AbstractOseeCache<Branch> cache = new TestCache();
      for (int index = 0; index < 100; index++) {
         String guid = GUID.create();
         Branch branch = createBranch(cache, guid, String.format("Test %s", index + 1), index);

         String actual = RenderingUtil.toFileName(branch);
         Assert.assertEquals(encode(guid), actual);
      }
   }

   private String encode(String guid) throws UnsupportedEncodingException {
      return URLEncoder.encode(guid, "UTF-8");
   }

   private Branch createBranch(AbstractOseeCache<Branch> cache, String guid, String name, int id) throws OseeCoreException {
      Branch branch = new BranchFactory().create(guid, name, BranchType.WORKING, BranchState.MODIFIED, false);
      Assert.assertNotNull(branch);
      branch.setId(id);
      return branch;
   }

   private final class TestCache extends BranchCache {
      public TestCache() {
         super(new MockOseeDataAccessor<Branch>());
      }
   }
}