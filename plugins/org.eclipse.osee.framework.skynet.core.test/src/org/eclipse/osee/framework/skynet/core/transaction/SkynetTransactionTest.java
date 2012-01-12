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
package org.eclipse.osee.framework.skynet.core.transaction;

import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.support.test.util.DemoSawBuilds;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

/**
 * @author Karol M Wilk
 */
public final class SkynetTransactionTest {

   public static final String MSG = "%s.%s %d";

   @Rule
   public TestName testName = new TestName();

   @Test(expected = OseeCoreException.class)
   public void test_overalappingTransactions() throws OseeCoreException {

      TransactionManager.createTransaction(DemoSawBuilds.SAW_Bld_1, String.format(MSG, SkynetTransactionTest.class.getSimpleName(),
         testName.getMethodName(), 1));

      TransactionManager.createTransaction(DemoSawBuilds.SAW_Bld_1, String.format(MSG, SkynetTransactionTest.class.getSimpleName(),
         testName.getMethodName(), 2));
   }

   @Test
   public void test_overalappingTransactionsDifferentBranches() throws OseeCoreException {

      SkynetTransaction trans1 =
         TransactionManager.createTransaction(DemoSawBuilds.SAW_Bld_2, String.format(MSG, SkynetTransactionTest.class.getSimpleName(),
            testName.getMethodName(), 1));

      SkynetTransaction trans2 =
         TransactionManager.createTransaction(DemoSawBuilds.SAW_Bld_1, String.format(MSG, SkynetTransactionTest.class.getSimpleName(),
            testName.getMethodName(), 2));

      trans1.execute();
      trans2.execute();
   }

   @Test
   public void test_multiThreadedOveralappingTransactions() throws Exception {
      TransactionManager.createTransaction(DemoSawBuilds.SAW_Bld_1, String.format(MSG, SkynetTransactionTest.class.getSimpleName(),
         testName.getMethodName(), 1));

      ThreadedWorker worker = new ThreadedWorker(2, DemoSawBuilds.SAW_Bld_1);
      worker.execute();

      worker.join();

      worker.finish();
      Assert.assertTrue(worker.caughtOseeCoreException);
   }

   @Test
   public void test_multiThreadedDifferentBranches() throws Exception {
      TransactionManager.createTransaction(DemoSawBuilds.SAW_Bld_1, String.format(MSG, SkynetTransactionTest.class.getSimpleName(),
         testName.getMethodName(), 1));
      ThreadedWorker worker = new ThreadedWorker(2, DemoSawBuilds.SAW_Bld_2);
      worker.execute();
      worker.join();
      worker.finish();
      Assert.assertFalse(worker.caughtOseeCoreException);
   }

   private class ThreadedWorker implements Runnable {

      private static final String TRANS_COMMENT = "%s Id: %d";
      private final Thread myThread;
      private SkynetTransaction governingTransaction;
      private final IOseeBranch branch;
      public boolean caughtOseeCoreException;
      private final int manualId;

      public ThreadedWorker(int id, IOseeBranch branchToken) {
         this.manualId = id;
         this.branch = branchToken;
         this.caughtOseeCoreException = false;
         this.myThread = new Thread(this, "Threaded Worker #" + id);
      }

      @Override
      public void run() {
         try {
            governingTransaction =
               TransactionManager.createTransaction(branch, String.format(TRANS_COMMENT, SkynetTransactionTest.class.getSimpleName(),
                  manualId));
            //hold onto this transaction
         } catch (OseeCoreException ex) {
            caughtOseeCoreException = true;
         }
      }

      public void join() throws InterruptedException {
         myThread.join();
      }

      public void execute() {
         myThread.start();
      }

      public void finish() throws OseeCoreException {
         finish(false);
      }

      public void finish(boolean executeTransaction) throws OseeCoreException {
         if (executeTransaction && governingTransaction != null) {
            governingTransaction.execute();
         }
      }
   }
}
