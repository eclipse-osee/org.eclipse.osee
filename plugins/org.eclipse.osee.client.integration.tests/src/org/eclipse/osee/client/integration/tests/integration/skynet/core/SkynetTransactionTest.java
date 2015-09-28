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
package org.eclipse.osee.client.integration.tests.integration.skynet.core;

import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.client.demo.DemoBranches;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeHousekeepingRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.client.test.framework.TestInfo;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Karol M. Wilk
 */
public final class SkynetTransactionTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Rule
   public OseeHousekeepingRule rule = new OseeHousekeepingRule();

   @Rule
   public TestInfo method = new TestInfo();

   private static final IOseeBranch BRANCH = DemoBranches.SAW_Bld_1;
   private static final IOseeBranch BRANCH_2 = DemoBranches.SAW_Bld_2;

   @Test(expected = OseeStateException.class)
   public void test_overalappingTransactions() throws OseeCoreException {

      SkynetTransaction trans1 = TransactionManager.createTransaction(BRANCH, createComment(1));

      SkynetTransaction trans2 = TransactionManager.createTransaction(BRANCH, createComment(2));

      Artifact artifact = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, BRANCH);

      try {
         trans1.addArtifact(artifact);
         trans2.addArtifact(artifact);
      } finally {
         artifact.purgeFromBranch();
      }
   }

   @Test
   public void test_overalappingTransactionsButWithDifferentArtifacts() throws OseeCoreException {

      SkynetTransaction trans1 = TransactionManager.createTransaction(BRANCH, createComment(1));
      SkynetTransaction trans2 = TransactionManager.createTransaction(BRANCH, createComment(2));

      Artifact artifact1 = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, BRANCH);
      Artifact artifact2 = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, BRANCH);

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

      WorkerThread thread1 = new WorkerThread(lock, BRANCH, WorkerType.PRIMARY);
      WorkerThread thread2 = new WorkerThread(lock, BRANCH_2, WorkerType.SECONDARY);

      Artifact artifact1 = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, BRANCH);
      Artifact artifact2 = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, BRANCH_2);

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

      WorkerThread thread1 = new WorkerThread(lock, BRANCH, WorkerType.PRIMARY);
      WorkerThread thread2 = new WorkerThread(lock, BRANCH, WorkerType.SECONDARY);

      Artifact artifact1 = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, BRANCH);
      Artifact artifact2 = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, BRANCH);

      thread1.addItem(artifact1);
      thread2.addItem(artifact2);

      executeThreads(thread1, thread2);

      checkNull(thread1.getException());
      checkNull(thread2.getException());

      artifact1.purgeFromBranch();
      artifact2.purgeFromBranch();
   }

   @Test(expected = OseeStateException.class)
   public void testAttributeMultiplicity() {
      Artifact swReq = ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirement, BRANCH);
      swReq.addAttribute(CoreAttributeTypes.ParagraphNumber, "1.1");
      swReq.addAttribute(CoreAttributeTypes.ParagraphNumber, "2.2");
      try {
         swReq.persist("testAttributeMultiplicity");
      } finally {
         swReq.purgeFromBranch();
      }
   }

   @Test(expected = OseeArgumentException.class)
   public void testRelationMultiplicity() {
      Artifact parent1 = ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirement, BRANCH, "parent1");
      Artifact parent2 = ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirement, BRANCH, "parent2");
      Artifact child = ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirement, BRANCH, "child");
      try {
         parent1.addRelation(CoreRelationTypes.Default_Hierarchical__Child, child);
         parent2.addRelation(CoreRelationTypes.Default_Hierarchical__Child, child);
         child.persist("testRelationMultiplicity");
      } finally {
         child.purgeFromBranch();
         parent1.purgeFromBranch();
         parent2.purgeFromBranch();
      }
   }

   @Test
   public void test_multiThreadedCoModificationOveralappingTransactions() throws Exception {
      Object lock = new Object();

      WorkerThread thread1 = new WorkerThread(lock, BRANCH, WorkerType.PRIMARY);
      WorkerThread thread2 = new WorkerThread(lock, BRANCH, WorkerType.SECONDARY);

      Artifact artifact1 = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, BRANCH);

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
      return String.format("%s_%d", method.getQualifiedTestName(), index);
   }

   private static enum WorkerType {
      PRIMARY,
      SECONDARY;
   }

   private class WorkerThread extends Thread {
      private final IOseeBranch branch;
      private final Object lock;
      private final Set<Artifact> data = new HashSet<>();
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
