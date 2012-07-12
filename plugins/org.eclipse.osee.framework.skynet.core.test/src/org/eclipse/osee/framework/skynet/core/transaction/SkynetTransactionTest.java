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

import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.rule.OseeHousekeepingRule;
import org.eclipse.osee.support.test.util.DemoSawBuilds;
import org.junit.After;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

/**
 * @author Karol M. Wilk
 */
public final class SkynetTransactionTest {

   @Rule
   public OseeHousekeepingRule rule = new OseeHousekeepingRule();

   @Rule
   public TestName testName = new TestName();

   @After
   public void tearDown() {
      System.gc();
   }

   @Test(expected = OseeStateException.class)
   public void test_overalappingTransactions() throws OseeCoreException {

      SkynetTransaction trans1 = TransactionManager.createTransaction(DemoSawBuilds.SAW_Bld_1, createComment(1));

      SkynetTransaction trans2 = TransactionManager.createTransaction(DemoSawBuilds.SAW_Bld_1, createComment(2));

      Artifact artifact = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, DemoSawBuilds.SAW_Bld_1);

      try {
         trans1.addArtifact(artifact);
         trans2.addArtifact(artifact);
      } finally {
         artifact.purgeFromBranch();
      }
   }

   @Test
   public void test_overalappingTransactionsButWithDifferentArtifacts() throws OseeCoreException {

      SkynetTransaction trans1 = TransactionManager.createTransaction(DemoSawBuilds.SAW_Bld_1, createComment(1));
      SkynetTransaction trans2 = TransactionManager.createTransaction(DemoSawBuilds.SAW_Bld_1, createComment(2));

      Artifact artifact1 = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, DemoSawBuilds.SAW_Bld_1);
      Artifact artifact2 = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, DemoSawBuilds.SAW_Bld_1);

      boolean wasAdded = false;
      try {
         trans1.addArtifact(artifact1);
         trans2.addArtifact(artifact2);
         wasAdded = true;
      } finally {
         artifact1.purgeFromBranch();
         artifact2.purgeFromBranch();
      }
      Assert.assertTrue(wasAdded);
   }

   @Test
   public void test_overalappingTransactionsDifferentBranches() throws Exception {
      Object lock = new Object();

      WorkerThread thread1 = new WorkerThread(lock, DemoSawBuilds.SAW_Bld_1, WorkerType.PRIMARY);
      WorkerThread thread2 = new WorkerThread(lock, DemoSawBuilds.SAW_Bld_2, WorkerType.SECONDARY);

      Artifact artifact1 = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, DemoSawBuilds.SAW_Bld_1);
      Artifact artifact2 = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, DemoSawBuilds.SAW_Bld_2);

      thread1.addItem(artifact1);
      thread2.addItem(artifact2);

      executeThreads(thread1, thread2);

      checkNull(thread1.getException());
      checkNull(thread2.getException());

      artifact1.purgeFromBranch();
      artifact2.purgeFromBranch();
   }

   @Test
   public void test_multiThreadedOveralappingTransactions() throws Exception {
      Object lock = new Object();

      WorkerThread thread1 = new WorkerThread(lock, DemoSawBuilds.SAW_Bld_1, WorkerType.PRIMARY);
      WorkerThread thread2 = new WorkerThread(lock, DemoSawBuilds.SAW_Bld_1, WorkerType.SECONDARY);

      Artifact artifact1 = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, DemoSawBuilds.SAW_Bld_1);
      Artifact artifact2 = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, DemoSawBuilds.SAW_Bld_1);

      thread1.addItem(artifact1);
      thread2.addItem(artifact2);

      executeThreads(thread1, thread2);

      checkNull(thread1.getException());
      checkNull(thread2.getException());

      artifact1.purgeFromBranch();
      artifact2.purgeFromBranch();
   }

   @Test
   public void test_multiThreadedCoModificationOveralappingTransactions() throws Exception {
      Object lock = new Object();

      WorkerThread thread1 = new WorkerThread(lock, DemoSawBuilds.SAW_Bld_1, WorkerType.PRIMARY);
      WorkerThread thread2 = new WorkerThread(lock, DemoSawBuilds.SAW_Bld_1, WorkerType.SECONDARY);

      Artifact artifact1 = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, DemoSawBuilds.SAW_Bld_1);

      thread1.addItem(artifact1);
      thread2.addItem(artifact1);

      executeThreads(thread1, thread2);

      checkNull(thread1.getException());
      checkNotNull(thread2.getException());

      artifact1.purgeFromBranch();
   }

   private static void checkNotNull(Throwable ex) {
      Assert.assertNotNull(ex);
      Assert.assertEquals(Lib.exceptionToString(ex), OseeStateException.class, ex.getClass());
   }

   private static void checkNull(Throwable ex) {
      Assert.assertEquals(Lib.exceptionToString(ex), (Throwable) null, ex);
   }

   private void executeThreads(Thread... threads) throws Exception {
      boolean first = true;

      for (Thread thread : threads) {
         thread.start();
         if (first) {
            first = false;
            synchronized (this) {
               wait(500);
            }
         }
      }
      for (Thread thread : threads) {
         thread.join();
      }
   }

   private String createComment(int index) {
      return String.format("%s.%s %d", getClass().getSimpleName(), testName.getMethodName(), index);
   }

   private static enum WorkerType {
      PRIMARY,
      SECONDARY;
   }

   private class WorkerThread extends Thread {
      private final IOseeBranch branch;
      private final Object lock;
      private final Set<Artifact> data = new HashSet<Artifact>();
      private Throwable ex;
      private final WorkerType workerType;

      public WorkerThread(Object lock, IOseeBranch branch, WorkerType workerType) {
         this.lock = lock;
         this.branch = branch;
         this.workerType = workerType;
      }

      public void addItem(Artifact artifact) {
         data.add(artifact);
      }

      @Override
      public void run() {
         try {
            String comment = createComment(WorkerType.PRIMARY == workerType ? 1 : 2);
            SkynetTransaction transaction = TransactionManager.createTransaction(branch, comment);

            try {
               for (Artifact artifact : data) {
                  artifact.persist(transaction);
               }
               if (WorkerType.PRIMARY == workerType) {
                  synchronized (lock) {
                     try {
                        lock.wait();
                     } catch (InterruptedException ex) {
                        //
                     }
                  }
               }
            } finally {
               if (WorkerType.SECONDARY == workerType) {
                  synchronized (lock) {
                     lock.notifyAll();
                  }
               }
            }

            transaction.execute();
         } catch (Throwable ex) {
            this.ex = ex;
         }
      }

      public Throwable getException() {
         return ex;
      }
   }
}
