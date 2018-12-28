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
package org.eclipse.osee.client.integration.tests.integration.skynet.core;

import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import org.eclipse.osee.client.integration.tests.integration.skynet.core.utils.TestUtil;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.client.test.framework.TestInfo;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class ArtifactLoaderTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Rule
   public TestInfo testInfo = new TestInfo();

   private static final int TOTAL_THREADS = 7;
   private static final int NUM_ARTIFACTS = 100;
   private static final String ATTRIBUTE_VALUE = "now is the time";
   private static final BranchId branch = CoreBranches.COMMON;

   @After
   public void tearDown() throws Exception {
      SkynetTransaction transaction = TransactionManager.createTransaction(branch, testInfo.getQualifiedTestName());
      List<Artifact> artifacts = ArtifactQuery.getArtifactListFromName(testInfo.getQualifiedTestName(), branch);
      ArtifactPersistenceManager.deleteArtifactCollection(transaction, false, artifacts);
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
   @Test(timeout = 10000)
   public void testThreadSafeLoading() throws Exception {
      // Create some software artifacts
      SkynetTransaction transaction = TransactionManager.createTransaction(COMMON, "ArtifactLoaderTest");
      Collection<Artifact> artifacts = TestUtil.createSimpleArtifacts(CoreArtifactTypes.GlobalPreferences,
         NUM_ARTIFACTS, "ArtifactLoaderTest", COMMON);
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

      Collection<LoadArtifacts> tasks = new ArrayList<>();
      for (int x = 1; x <= TOTAL_THREADS; x++) {
         tasks.add(new LoadArtifacts());
      }

      ExecutorService executor = Executors.newFixedThreadPool(TOTAL_THREADS, new LoadThreadFactory());
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
      String message = String.format("Hit timeout value before threads were completed - completed[%s] cancelled[%s]",
         completed, cancelled);
      Assert.assertEquals(message, TOTAL_THREADS, completed);

      // Load and check artifacts
      artifacts = ArtifactQuery.getArtifactListFromName("ArtifactLoaderTest", COMMON);
      Assert.assertEquals(NUM_ARTIFACTS, artifacts.size());
      for (Artifact artifact : artifacts) {
         Assert.assertEquals(ATTRIBUTE_VALUE, artifact.getSoleAttributeValue(CoreAttributeTypes.DefaultMailServer));
         Assert.assertEquals(1, artifact.getAttributesToStringList(CoreAttributeTypes.DefaultMailServer).size());
      }
   }

   @Test(timeout = 5000)
   public void testThreadSafeLoadingSameArtifact() throws Exception {
      // Create some software artifacts
      SkynetTransaction transaction = TransactionManager.createTransaction(COMMON, "ArtifactLoaderTest");
      Artifact testArt =
         TestUtil.createSimpleArtifact(CoreArtifactTypes.GlobalPreferences, "ArtifactLoaderTest", COMMON);
      testArt.setName("ArtifactLoaderTest");
      testArt.addAttribute(CoreAttributeTypes.DefaultMailServer, ATTRIBUTE_VALUE);
      testArt.persist(transaction);
      transaction.execute();

      final String guid = testArt.getGuid();

      // now, de-cache them
      ArtifactCache.deCache(testArt);

      List<Callable<String>> callables = new LinkedList<>();

      int size = 4;
      //create 4 threads to load the same artifact
      for (int i = 0; i < size; i++) {
         MultiThreadCallable mtc = new MultiThreadCallable(guid);
         callables.add(mtc);
      }

      ExecutorService executor = Executors.newFixedThreadPool(size);
      for (Future<String> future : executor.invokeAll(callables, 81, TimeUnit.SECONDS)) {
         Assert.assertEquals(ATTRIBUTE_VALUE, future.get());
      }

      //double check
      ArtifactCache.deCache(testArt);
      for (Future<String> future : executor.invokeAll(callables, 81, TimeUnit.SECONDS)) {
         Assert.assertEquals(ATTRIBUTE_VALUE, future.get());
      }

   }

   private final class MultiThreadCallable implements Callable<String> {

      private final String guid;

      public MultiThreadCallable(String guid) {
         this.guid = guid;
      }

      @Override
      public String call() throws Exception {
         Artifact art = ArtifactQuery.getArtifactFromId(guid, CoreBranches.COMMON);
         return art.getSoleAttributeValueAsString(CoreAttributeTypes.DefaultMailServer, "");
      }

   }

   private static final class LoadArtifacts implements Callable<List<Artifact>> {

      @Override
      public List<Artifact> call() throws Exception {
         List<Artifact> artifacts = ArtifactQuery.getArtifactListFromName("ArtifactLoaderTest", COMMON);
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