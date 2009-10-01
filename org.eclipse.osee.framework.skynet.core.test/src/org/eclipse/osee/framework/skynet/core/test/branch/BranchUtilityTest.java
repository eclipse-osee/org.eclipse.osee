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

package org.eclipse.osee.framework.skynet.core.test.branch;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchUtility;
import org.eclipse.osee.framework.skynet.core.test.types.OseeTestDataAccessor;
import org.eclipse.osee.framework.skynet.core.types.AbstractOseeCache;
import org.eclipse.osee.framework.skynet.core.types.BranchCache;
import org.eclipse.osee.framework.skynet.core.types.IOseeTypeFactory;
import org.eclipse.osee.framework.skynet.core.types.OseeTypeFactory;
import org.junit.Test;

/**
 * @author Roberto E. Escobar
 */
public class BranchUtilityTest {

   private final IOseeTypeFactory factory;

   public BranchUtilityTest() {
      factory = new OseeTypeFactory();
   }

   @Test
   public void testBranchToFileName() throws Exception {
      AbstractOseeCache<Branch> cache = new TestCache(factory);
      for (int index = 0; index < 100; index++) {
         String guid = GUID.create();
         Branch branch = createBranch(cache, guid, String.format("Test %s", index + 1), index);

         String actual = BranchUtility.toFileName(branch);
         Assert.assertEquals(encode(guid), actual);
      }
   }

   @Test
   public void testBranchToFileNameInvalidGuid() throws OseeCoreException {
      AbstractOseeCache<Branch> cache = new TestCache(factory);
      Branch branch = createBranch(cache, "!#A", "Invalid Guid", 2);
      try {
         BranchUtility.toFileName(branch);
         Assert.assertFalse("This line should not be executed", true);
      } catch (Exception ex) {
         Assert.assertTrue(ex instanceof OseeStateException);
      }
   }

   @Test
   public void testBranchToFileNameNullBranch() throws OseeCoreException {
      try {
         BranchUtility.toFileName(null);
         Assert.assertFalse("This line should not be executed", true);
      } catch (Exception ex) {
         Assert.assertTrue(ex instanceof OseeArgumentException);
      }
   }

   @Test
   public void testfromFileNameGuidNotFound() throws OseeCoreException {
      AbstractOseeCache<Branch> cache = new TestCache(factory);
      cache.ensurePopulated();
      Assert.assertEquals(0, cache.size());
      try {
         String guid = GUID.create();
         BranchUtility.fromFileName(cache, encode(guid));
      } catch (Exception ex) {
         Assert.assertTrue(ex.getClass().getSimpleName(), ex instanceof OseeArgumentException);
      }
   }

   @Test
   public void testfromFileNameIdNotFound() throws OseeCoreException {
      AbstractOseeCache<Branch> cache = new TestCache(factory);
      cache.ensurePopulated();
      Assert.assertEquals(0, cache.size());
      try {
         BranchUtility.fromFileName(cache, "hello.-2");
      } catch (Exception ex) {
         Assert.assertTrue(ex instanceof OseeArgumentException);
      }
   }

   @Test
   public void testfromFileNameNullArgs() {
      try {
         BranchUtility.fromFileName(null, "hello.-2");
      } catch (Exception ex) {
         Assert.assertTrue(ex instanceof OseeArgumentException);
      }
      AbstractOseeCache<Branch> cache = new TestCache(factory);
      try {
         BranchUtility.fromFileName(cache, null);
      } catch (Exception ex) {
         Assert.assertTrue(ex instanceof OseeArgumentException);
      }
   }

   @Test
   public void testfromFileName() throws OseeCoreException, UnsupportedEncodingException {
      AbstractOseeCache<Branch> cache = new TestCache(factory);
      cache.ensurePopulated();
      Assert.assertEquals(0, cache.size());

      Branch expectedBranch1 = createBranch(cache, "AyH_e52bdW+WUbdQUoQA", "Test 1", 1);
      Branch expectedBranch2 = createBranch(cache, "AyH_e52bdW+WUbdQUoQB", "Test 2", 2);

      cache.cache(expectedBranch1);
      cache.cache(expectedBranch2);

      Assert.assertEquals(2, cache.size());

      checkBranchFromFile(cache, expectedBranch1, encode("AyH_e52bdW+WUbdQUoQA"));
      checkBranchFromFile(cache, expectedBranch2, encode("AyH_e52bdW+WUbdQUoQB"));

      checkBranchFromFile(cache, expectedBranch1, encode("X.1"));
      checkBranchFromFile(cache, expectedBranch2, encode("X.2"));
   }

   private void checkBranchFromFile(AbstractOseeCache<Branch> cache, Branch expected, String fileName) throws OseeCoreException {
      Branch actual = BranchUtility.fromFileName(cache, fileName);
      Assert.assertNotNull(actual);
      Assert.assertEquals(expected, actual);
   }

   private String encode(String guid) throws UnsupportedEncodingException {
      return URLEncoder.encode(guid, "UTF-8");
   }

   private Branch createBranch(AbstractOseeCache<Branch> cache, String guid, String name, int id) throws OseeCoreException {
      Branch branch = factory.createBranch(cache, guid, name, BranchType.WORKING, BranchState.MODIFIED, false);
      Assert.assertNotNull(branch);
      branch.setId(id);
      return branch;
   }

   private final class TestCache extends BranchCache {
      public TestCache(IOseeTypeFactory factory) {
         super(factory, new OseeTestDataAccessor<Branch>());
      }
   }
}
