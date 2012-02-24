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
package org.eclipse.osee.orcs.core.internal.branch.provider;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author John Misinco
 */
public final class PurgeBranchTest {

   @Ignore
   @Test
   public void testPurgeBranch() throws Exception {
      //
      //      runTest(false, false, false, true);
   }
   //
   //   @Test
   //   public void testBranchOperationException__nullFactory() throws Exception {
   //      runTest(true, false, false, false);
   //   }
   //
   //   @Test
   //   public void testBranchOperationException__nullProvider() throws Exception {
   //      runTest(false, true, false, false);
   //   }
   //
   //   @Test
   //   public void testBranchOperationException__nullLogger() throws Exception {
   //      runTest(false, false, true, false);
   //   }
   //
   //   @Test
   //   public void testBranchOperationException__allNull() throws Exception {
   //      runTest(true, true, true, false);
   //   }
   //
   //   private void runTest(boolean nullFactory, boolean nullProvider, boolean nullLogger, boolean expectedResult) throws Exception {
   //      MockBranchProvider mbp = new MockBranchProvider();
   //
   //      if (nullProvider) {
   //         mbp = null;
   //      }
   //
   //      Collection<Branch> branches = mbp.getBranches();
   //      Branch rootBranch = MockBranchProvider.getRootBranch(branches);
   //      BranchCache branchCache = createBranchCache();
   //      branchCache.cache(rootBranch);
   //
   //      IOseeBranch toPurge = TokenFactory.createBranch(rootBranch.getGuid(), rootBranch.getName());
   //      MockBranchDataStore branchStore = new MockBranchDataStore();
   //
   //      Callable<List<Branch>> callable =
   //         new PurgeBranchCallable(new MockLog(), new MockSessionContext(), branchStore, branchCache, toPurge, false);
   //      List<Branch> purged = callable.call();
   //
   //      Assert.assertEquals(10, branchStore.getPurgeCount());
   //      Assert.assertTrue(verifyCallOrder(purged));
   //   }
   //
   //   private static boolean verifyCallOrder(List<Branch> callOrder) throws OseeCoreException {
   //      boolean result = true;
   //      for (Branch cur : callOrder) {
   //         int idxCur = callOrder.indexOf(cur);
   //         Branch parent = cur.getParentBranch();
   //         if (parent != null) {
   //            int idxParent = callOrder.indexOf(parent);
   //            if (idxCur > idxParent) {
   //               result = false;
   //               break;
   //            }
   //         }
   //      }
   //      return result;
   //   }
   //
   //   private static BranchCache createBranchCache() {
   //      return new BranchCache(new IOseeDataAccessor<String, Branch>() {
   //
   //         @Override
   //         public void load(IOseeCache<String, Branch> cache) {
   //            //
   //         }
   //
   //         @Override
   //         public void store(Collection<Branch> types) {
   //            //
   //         }
   //      });
   //   }
   //
   //   private final class MockBranchDataStore implements BranchDataStore {
   //
   //      private int purgeCount = 0;
   //
   //      @Override
   //      public Callable<Branch> createBranch(String sessionId, CreateBranchData newBranchData) {
   //         return null;
   //      }
   //
   //      @Override
   //      public Callable<Branch> purgeBranch(String sessionId, final Branch branch) {
   //         purgeCount++;
   //         return new Callable<Branch>() {
   //
   //            @Override
   //            public Branch call() throws Exception {
   //               return branch;
   //            }
   //
   //         };
   //      }
   //
   //      @Override
   //      public Callable<TransactionRecord> commitBranch(String sessionId, Branch source, Branch destination) {
   //         return null;
   //      }
   //
   //      @Override
   //      public Callable<?> compareBranch(String sessionId, TransactionRecord sourceTx, TransactionRecord destinationTx) {
   //         return null;
   //      }
   //
   //      public int getPurgeCount() {
   //         return purgeCount;
   //      }
   //
   //   }

}
