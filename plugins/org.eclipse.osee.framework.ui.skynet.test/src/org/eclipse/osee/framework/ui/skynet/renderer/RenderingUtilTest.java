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
package org.eclipse.osee.framework.ui.skynet.renderer;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.BranchFactory;
import org.eclipse.osee.framework.core.model.cache.AbstractOseeCache;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Roberto E. Escobar
 */
public class RenderingUtilTest {

   private static AbstractOseeCache<String, Branch> cache;
   private static Branch branch;

   @BeforeClass
   public static void setUpOnce() throws OseeCoreException {
      cache = new BranchCache(new MockOseeDataAccessor<String, Branch>());
      branch = createBranch(cache, GUID.create(), "Test 1", 1);
   }

   @Test
   public void testBranchToFileName() throws Exception {
      String actual = RenderingUtil.toFileName(branch);
      Assert.assertEquals(encode(branch.getShortName()), actual);
   }

   @Test
   public void test_branchToFileName_notAllowedCharsInName() throws OseeCoreException {
      branch.setName("0123455789012345578901234557890123.5");
      String branchShortName = RenderingUtil.toFileName(branch);
      Assert.assertEquals("Not safe character found at end of branch name.", "0123455789012345578901234557890123_",
         branchShortName);
   }

   private String encode(String guid) throws UnsupportedEncodingException {
      return URLEncoder.encode(guid, "UTF-8");
   }

   private static Branch createBranch(AbstractOseeCache<String, Branch> cache, String guid, String name, int id) throws OseeCoreException {
      Branch branch = new BranchFactory().create(guid, name, BranchType.WORKING, BranchState.MODIFIED, false);
      Assert.assertNotNull(branch);
      branch.setId(id);
      return branch;
   }
}