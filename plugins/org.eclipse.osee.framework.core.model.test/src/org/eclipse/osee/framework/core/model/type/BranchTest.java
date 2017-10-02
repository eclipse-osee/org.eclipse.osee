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
package org.eclipse.osee.framework.core.model.type;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Roberto E. Escobar
 */
public class BranchTest {

   @Ignore
   @Test
   public void test() {

   }

   //   @Test
   //   public void testSystemRootBranch()  {
   //      Branch systemRootBranch = cache.getSystemRootBranch();
   //      Assert.assertNotNull(systemRootBranch);
   //   }
   //
   //   @Test
   //   public void testBranchHierarchy()  {
   //      OseeTypesUtil.checkHierarchy(cache, "AAA", "BBB", "CCC");
   //      OseeTypesUtil.checkHierarchy(cache, "BBB", "DDD", "EEE");
   //      OseeTypesUtil.checkHierarchy(cache, "CCC", "FFF", "GGG", "HHH");
   //   }
   //
   //   @Test
   //   public void testMergeBranches()  {
   //      OseeTypesUtil.checkMergeBranch(cache, null, "HHH", "AAA");
   //      OseeTypesUtil.checkMergeBranch(cache, "III", "DDD", "BBB");
   //      OseeTypesUtil.checkMergeBranch(cache, "JJJ", "GGG", "CCC");
   //      OseeTypesUtil.checkMergeBranch(cache, "KKK", "HHH", "CCC");
   //   }
   //
   //   @Test
   //   public void testBranchAliases()  {
   //      OseeTypesUtil.checkAliases(cache, "AAA", "root", "system", "main");
   //      OseeTypesUtil.checkAliases(cache, "BBB", "base 1", "build 1", "common");
   //      OseeTypesUtil.checkAliases(cache, "CCC", "base 2", "build 2");
   //
   //      OseeTypesUtil.checkAliases(cache, "DDD");
   //      OseeTypesUtil.checkAliases(cache, "EEE");
   //      OseeTypesUtil.checkAliases(cache, "FFF");
   //      OseeTypesUtil.checkAliases(cache, "GGG");
   //      OseeTypesUtil.checkAliases(cache, "HHH");
   //      OseeTypesUtil.checkAliases(cache, "III");
   //   }
   //
   //   @Test
   //   public void testSameAliasForMultipleBranches()  {
   //      OseeTypesUtil.checkAliases(cache, "JJJ", "a merge branch");
   //      OseeTypesUtil.checkAliases(cache, "KKK", "a merge branch");
   //
   //      List<Branch> aliasedbranch = new ArrayList<>(cache.getByAlias("a merge branch"));
   //      Assert.assertEquals(2, aliasedbranch.size());
   //
   //      Collections.sort(aliasedbranch);
   //      Assert.assertEquals(cache.getByGuid("JJJ"), aliasedbranch.get(0));
   //      Assert.assertEquals(cache.getByGuid("KKK"), aliasedbranch.get(1));
   //   }
   //
   //   @Test
   //   public void testSetAliasForBranch()  {
   //      Branch branch = cache.getByGuid("JJJ");
   //      Assert.assertNotNull(branch);
   //
   //      branch.setAliases("One", "Two", "Three");
   //      OseeTypesUtil.checkAliases(cache, "JJJ", "one", "two", "three");
   //
   //      branch.setAliases("One", "Three");
   //      OseeTypesUtil.checkAliases(cache, "JJJ", "one", "three");
   //
   //      branch.setAliases();
   //      OseeTypesUtil.checkAliases(cache, "JJJ");
   //
   //      branch.setAliases("a merge branch");
   //      OseeTypesUtil.checkAliases(cache, "JJJ", "a merge branch");
   //   }
   //
   //   @Test
   //   public void testBaseTransaction()  {
   //      Branch branch = cache.getByGuid("BBB");
   //      Assert.assertNotNull(branch);
   //
   //      Assert.assertNull(branch.getBaseTransaction());
   //
   //      TransactionRecord expectedTx =
   //            new TransactionRecord(1, branch, "Transaction 1", new Date(), 1, 2, TransactionDetailsType.Baselined);
   //      cache.cacheBaseTransaction(branch, expectedTx);
   //
   //      TransactionRecord actualTx = branch.getBaseTransaction();
   //      Assert.assertEquals(expectedTx, actualTx);
   //   }
   //
   //   @Test
   //   public void testInvalidBaseTransactionCaching()  {
   //      Branch branch = cache.getByGuid("BBB");
   //      Assert.assertNotNull(branch);
   //      TransactionRecord expectedTx =
   //            new TransactionRecord(1, branch, "Transaction 1", new Date(), 1, 2, TransactionDetailsType.NonBaselined);
   //
   //      try {
   //         cache.cacheBaseTransaction(null, expectedTx);
   //         Assert.assertTrue("This line should not be executed", true);
   //      } catch (Exception ex) {
   //         Assert.assertTrue(ex instanceof OseeArgumentException);
   //      }
   //
   //      try {
   //         cache.cacheBaseTransaction(branch, null);
   //         Assert.assertTrue("This line should not be executed", true);
   //      } catch (Exception ex) {
   //         Assert.assertTrue(ex instanceof OseeArgumentException);
   //      }
   //
   //      try {
   //         cache.cacheBaseTransaction(branch, expectedTx);
   //         Assert.assertTrue("This line should not be executed", true);
   //      } catch (Exception ex) {
   //         Assert.assertTrue(ex instanceof OseeArgumentException);
   //      }
   //   }
   //
   //   @Test
   //   public void testSourceTransaction()  {
   //      Branch branch = cache.getByGuid("BBB");
   //      Assert.assertNotNull(branch);
   //
   //      Assert.assertNull(branch.getSourceTransaction());
   //
   //      TransactionRecord expectedTx =
   //            new TransactionRecord(1, null, "Transaction 1", new Date(), 1, 2, TransactionDetailsType.NonBaselined);
   //      cache.cacheSourceTransaction(branch, expectedTx);
   //
   //      TransactionRecord actualTx = branch.getSourceTransaction();
   //      Assert.assertEquals(expectedTx, actualTx);
   //   }
   //
   //   @Test
   //   public void testInvalidSourceTransaction()  {
   //      Branch branch = cache.getByGuid("BBB");
   //      Assert.assertNotNull(branch);
   //      TransactionRecord expectedTx =
   //            new TransactionRecord(1, branch, "Transaction 1", new Date(), 1, 2, TransactionDetailsType.NonBaselined);
   //      cache.cacheSourceTransaction(branch, expectedTx);
   //
   //      try {
   //         cache.cacheSourceTransaction(null, expectedTx);
   //         Assert.assertTrue("This line should not be executed", true);
   //      } catch (Exception ex) {
   //         Assert.assertTrue(ex instanceof OseeArgumentException);
   //      }
   //
   //      try {
   //         cache.cacheSourceTransaction(branch, null);
   //         Assert.assertTrue("This line should not be executed", true);
   //      } catch (Exception ex) {
   //         Assert.assertTrue(ex instanceof OseeArgumentException);
   //      }
   //   }
   //
   //   @Test
   //   public void testAssociatedArtifact()  {
   //      Assert.assertEquals(defaultAssociatedArtifact, cache.getDefaultAssociatedArtifact());
   //
   //      Branch branch = cache.getByGuid("BBB");
   //      Assert.assertNotNull(branch);
   //
   //      Assert.assertEquals(defaultAssociatedArtifact, branch.getAssociatedArtifact());
   //
   //      String guid = GUID.create();
   //      IBasicArtifact<?> expectedArtifact = new TestArtifact(100, guid, "Test Artifact");
   //      branch.setAssociatedArtifact(expectedArtifact);
   //
   //      Assert.assertEquals(branch.getAssociatedArtifact(), expectedArtifact);
   //
   //      branch.setAssociatedArtifact(null);
   //      Assert.assertEquals(defaultAssociatedArtifact, branch.getAssociatedArtifact());
   //   }
   //
   //   @Override
   //   public void testDirty()  {
   //      // TODO test Rename
   //
   //      //      AttributeType attributeType = OseeTypesUtil.createAttributeType(attrCache, factory, "GUID", "AttributeDirtyTest");
   //      //      Assert.assertTrue(attributeType.isDirty());
   //      //      attributeType.clearDirty();
   //      //
   //      //      String initialValue = attributeType.getName();
   //      //      attributeType.setName("My Name Has Changes");
   //      //      Assert.assertTrue(attributeType.isDirty());
   //      //
   //      //      // Remains Dirty
   //      //      attributeType.setName(initialValue);
   //      //      Assert.assertTrue(attributeType.isDirty());
   //      //
   //      //      //      attributeType.setFields(name, baseAttributeTypeId, attributeProviderNameId, baseAttributeClass,
   //      //      //            providerAttributeClass, fileTypeExtension, defaultValue, oseeEnumType, minOccurrences, maxOccurrences,
   //      //      //            description, taggerId);
   //
   //   }
   //
   //   private final static class TestArtifact implements IBasicArtifact<Object> {
   //
   //      private final int artId;
   //      private final String guid;
   //      private final String name;
   //
   //      public TestArtifact(int uniqueId, String guid, String name) {
   //         this.artId = uniqueId;
   //         this.guid = guid;
   //         this.name = name;
   //      }
   //
   //      @Override
   //      public int getArtId() {
   //         return artId;
   //      }
   //
   //      @Override
   //      public ArtifactType getArtifactType() {
   //         return null;
   //      }
   //
   //      @Override
   //      public IBasicArtifact<Object> getFullArtifact()  {
   //         return null;
   //      }
   //
   //      @Override
   //      public String getGuid() {
   //         return guid;
   //      }
   //
   //      @Override
   //      public String getName() {
   //         return name;
   //      }
   //
   //      @Override
   //      public Branch getBranch() {
   //         return null;
   //      }
   //   }
   //
   //   private final static class BranchDataAccessor extends MockOseeDataAccessor<Branch> {
   //
   //      private final List<Branch> data;
   //
   //      public BranchDataAccessor(List<Branch> data) {
   //         super();
   //         this.data = data;
   //      }
   //
   //      @Override
   //      public void load(AbstractOseeCache<Branch> cache)  {
   //         super.load(cache);
   //         data.add(OseeTypesUtil.createBranch(cache, factory, "AAA", "Root", BranchType.SYSTEM_ROOT,
   //               BranchState.CREATED, false));
   //
   //         data.add(OseeTypesUtil.createBranch(cache, factory, "BBB", "B-Branch", BranchType.BASELINE,
   //               BranchState.CREATED, false));
   //         data.add(OseeTypesUtil.createBranch(cache, factory, "CCC", "C-Branch", BranchType.BASELINE,
   //               BranchState.MODIFIED, false));
   //
   //         data.add(OseeTypesUtil.createBranch(cache, factory, "DDD", "D-Branch", BranchType.WORKING,
   //               BranchState.MODIFIED, false));
   //         data.add(OseeTypesUtil.createBranch(cache, factory, "EEE", "E-Branch", BranchType.WORKING,
   //               BranchState.MODIFIED, false));
   //
   //         data.add(OseeTypesUtil.createBranch(cache, factory, "FFF", "F-Branch", BranchType.WORKING,
   //               BranchState.MODIFIED, false));
   //         data.add(OseeTypesUtil.createBranch(cache, factory, "GGG", "G-Branch", BranchType.WORKING,
   //               BranchState.MODIFIED, true));
   //         data.add(OseeTypesUtil.createBranch(cache, factory, "HHH", "H-Branch", BranchType.WORKING,
   //               BranchState.MODIFIED, true));
   //
   //         // Merge Branches
   //         data.add(OseeTypesUtil.createBranch(cache, factory, "III", "Merge-A", BranchType.MERGE, BranchState.CREATED,
   //               false));
   //         data.add(OseeTypesUtil.createBranch(cache, factory, "JJJ", "Merge-B", BranchType.MERGE, BranchState.CREATED,
   //               false));
   //         data.add(OseeTypesUtil.createBranch(cache, factory, "KKK", "Merge-C", BranchType.MERGE, BranchState.CREATED,
   //               false));
   //
   //         int typeId = 500;
   //         for (Branch type : data) {
   //            type.setId(typeId++);
   //            cache.cache(type);
   //         }
   //         BranchCache branchCache = (BranchCache) cache;
   //         loadBranchHierarchy(branchCache);
   //         loadMergeBranches(branchCache);
   //         loadBranchAliases(branchCache);
   //      }
   //
   //      private void loadBranchHierarchy(BranchCache cache)  {
   //         OseeTypesUtil.createBranchHierarchy(cache, "AAA", "BBB", "CCC");
   //         OseeTypesUtil.createBranchHierarchy(cache, "BBB", "DDD", "EEE");
   //         OseeTypesUtil.createBranchHierarchy(cache, "CCC", "FFF", "GGG", "HHH");
   //      }
   //
   //      private void loadMergeBranches(BranchCache branchCache)  {
   //         OseeTypesUtil.createMergeBranch(cache, "III", "DDD", "BBB");
   //         OseeTypesUtil.createMergeBranch(cache, "JJJ", "GGG", "CCC");
   //         OseeTypesUtil.createMergeBranch(cache, "KKK", "HHH", "CCC");
   //      }
   //
   //      private void loadBranchAliases(BranchCache branchCache)  {
   //         OseeTypesUtil.createAlias(cache, "AAA", "Root", "System", "Main");
   //         OseeTypesUtil.createAlias(cache, "BBB", "Base 1", "Build 1", "common");
   //         OseeTypesUtil.createAlias(cache, "CCC", "Base 2", "Build 2");
   //
   //         OseeTypesUtil.createAlias(cache, "JJJ", "a merge branch");
   //         OseeTypesUtil.createAlias(cache, "KKK", "a merge branch");
   //      }
   //   }
}
