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
package org.eclipse.osee.framework.skynet.core.test.cases;

import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
import static org.junit.Assert.assertFalse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.test.util.FrameworkTestUtil;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * @author Donald G. Dunne
 */
public class ArtifactLoaderTest {

   private static SevereLoggingMonitor monitorLog;
   private static int NUM_ARTIFACTS = 100;
   private static String ATTRIBUTE_VALUE = "now is the time";

   @BeforeClass
   public static void testInitialize() throws Exception {
      assertFalse(TestUtil.isProductionDb());
      testCleanup();
      monitorLog = TestUtil.severeLoggingStart();
   }

   @AfterClass
   public static void tearDown() throws Exception {
      TestUtil.severeLoggingEnd(monitorLog);
      testCleanup();
   }

   private static void testCleanup() throws Exception {
      SkynetTransaction transaction = new SkynetTransaction(BranchManager.getCommonBranch(), "ArtifactLoaderTest");
      List<Artifact> artifacts =
         ArtifactQuery.getArtifactListFromName("ArtifactLoaderTest", BranchManager.getCommonBranch(), EXCLUDE_DELETED);
      ArtifactPersistenceManager.deleteArtifactList(transaction, false, artifacts);
      transaction.execute();
   }

   /**
    * This test was created because artifact loading is loading multiple attribute instances for the same attribute in
    * the database. This will create 100 artifacts with an attribute that should only be a singleton, decache the
    * artifacts so they are reloaded and then reload them in 8 different threads. Due to the intermittent nature, it may
    * need to be run multiple times.<br>
    * <br>
    * Another interesting side-effect, is this test also gives a repeatable case for duplicate relation loading. These
    * are caught by the SevereLoggingMonitor.
    */
   @org.junit.Test
   public void testThreadSafeLoading() throws Exception {
      // Create some software artifacts
      SkynetTransaction transaction = new SkynetTransaction(BranchManager.getCommonBranch(), "ArtifactLoaderTest");
      Collection<Artifact> artifacts =
         FrameworkTestUtil.createSimpleArtifacts(CoreArtifactTypes.GlobalPreferences, NUM_ARTIFACTS,
            "ArtifactLoaderTest", BranchManager.getCommonBranch());
      for (Artifact artifact : artifacts) {
         artifact.setName("ArtifactLoaderTest");
         artifact.addAttribute(CoreAttributeTypes.DefaultMailServer, ATTRIBUTE_VALUE);
         artifact.persist(transaction);
      }
      transaction.execute();

      // now, de-cache them
      for (Artifact artifact : artifacts) {
         ArtifactCache.deCache(artifact);
      }

      final int TOTAL_THREADS = 7;
      Collection<LoadArtifacts> tasks = new ArrayList<LoadArtifacts>();
      for (int x = 1; x <= TOTAL_THREADS; x++) {
         tasks.add(new LoadArtifacts());
      }

      ExecutorService executor = Executors.newFixedThreadPool(7, new LoadThreadFactory());
      List<Future<List<Artifact>>> futures = executor.invokeAll(tasks, 81, TimeUnit.SECONDS);
      int completed = 0;
      int cancelled = 0;
      for (Future<?> future : futures) {
         if (future.isCancelled()) {
            cancelled++;
         }
         if (future.isDone()) {
            completed++;
         }
      }
      executor.shutdownNow();
      String message =
         String.format("Hit timeout value before threads were completed - completed[%s] cancelled[%s]", completed,
            cancelled);
      Assert.assertEquals(message, 7, completed);

      // Load and check artifacts
      artifacts =
         ArtifactQuery.getArtifactListFromName("ArtifactLoaderTest", BranchManager.getCommonBranch(), EXCLUDE_DELETED);
      Assert.assertEquals(NUM_ARTIFACTS, artifacts.size());
      for (Artifact artifact : artifacts) {
         Assert.assertEquals(ATTRIBUTE_VALUE, artifact.getSoleAttributeValue(CoreAttributeTypes.DefaultMailServer));
         Assert.assertEquals(1, artifact.getAttributesToStringList(CoreAttributeTypes.DefaultMailServer).size());
      }
   }

   private static final class LoadArtifacts implements Callable<List<Artifact>> {

      @Override
      public List<Artifact> call() throws Exception {
         List<Artifact> artifacts =
            ArtifactQuery.getArtifactListFromName("ArtifactLoaderTest", BranchManager.getCommonBranch(),
               EXCLUDE_DELETED);
         if (artifacts.size() != NUM_ARTIFACTS) {
            throw new OseeStateException("Should have loaded %d not %d", NUM_ARTIFACTS, artifacts.size());
         }
         return artifacts;
      }
   };

   private static final class LoadThreadFactory implements ThreadFactory {
      private static int threadCount = 0;

      @Override
      public Thread newThread(Runnable target) {
         return new Thread(target, "Loading Thread: " + ++threadCount);
      }
   }
}
