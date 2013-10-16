/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.integration.tests.integration;

import static org.eclipse.osee.coverage.demo.CoverageChoice.OSEE_COVERAGE_DEMO;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Collection;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeHousekeepingRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.coverage.event.CoverageEventType;
import org.eclipse.osee.coverage.event.CoveragePackageEvent;
import org.eclipse.osee.coverage.integration.tests.integration.util.CoverageTestUtil;
import org.eclipse.osee.coverage.merge.IMergeItem;
import org.eclipse.osee.coverage.merge.MergeImportManager;
import org.eclipse.osee.coverage.merge.MergeManager;
import org.eclipse.osee.coverage.merge.MergeType;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoverageOptionManager;
import org.eclipse.osee.coverage.model.CoverageOptionManagerDefault;
import org.eclipse.osee.coverage.model.CoveragePackage;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.coverage.model.SimpleWorkProductTaskProvider;
import org.eclipse.osee.coverage.store.OseeCoveragePackageStore;
import org.eclipse.osee.coverage.store.OseeCoverageStore;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.coverage.util.ISaveable;
import org.eclipse.osee.coverage.vcast.CoverageImportFactory;
import org.eclipse.osee.coverage.vcast.VCast60Params;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Donald G. Dunne
 */
public class VCastAdaCoverage_V6_0_ImportOperationTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_COVERAGE_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Rule
   public TemporaryFolder tempFolder = new TemporaryFolder();

   @Rule
   public TestName testName = new TestName();
   @Rule
   public OseeHousekeepingRule houseKeepingRule = new OseeHousekeepingRule();

   public static CoveragePackage coveragePackage = null;
   public static CoverageImport coverageImport = null;
   private static boolean testWithDb = true;

   @BeforeClass
   public static void setUp() throws OseeCoreException {
      CoverageUtil.setIsInTest(true);
      testCleanup();
   }

   @AfterClass
   public static void cleanUp() throws OseeCoreException {
      CoverageUtil.setIsInTest(false);
      testCleanup();
   }

   public static void testCleanup() throws OseeCoreException {
      if (testWithDb) {
         CoverageUtil.setNavigatorSelectedBranch(CoverageTestUtil.getTestBranch());
         CoverageTestUtil.cleanupCoverageTests();
      } else {
         System.err.println("Test with Db Disabled...re-inenable");
      }
   }

   private URL getURL(Bundle bundle, String resource) {
      String fullPath = String.format("support/vcastData/%s", resource);
      return bundle.getEntry(fullPath);
   }

   private static void copyResource(URL source, File destination) throws IOException {
      OutputStream outputStream = null;
      InputStream inputStream = null;
      try {
         inputStream = new BufferedInputStream(source.openStream());

         outputStream = new BufferedOutputStream(new FileOutputStream(destination));
         Lib.inputStreamToOutputStream(inputStream, outputStream);
      } finally {
         Lib.close(inputStream);
         Lib.close(outputStream);
      }
   }

   @Test
   public void testAdaVCast60ImportOp() throws Exception {
      File dbFile = tempFolder.newFile("vCastImportTest.db");
      File resultsFolder = tempFolder.newFolder("results");

      Bundle bundle = FrameworkUtil.getBundle(this.getClass());
      copyResource(getURL(bundle, "vcast/vCastImportTest.db"), dbFile);
      copyResource(getURL(bundle, "vcast/test_main.2.LIS"), tempFolder.newFile("test_main.2.LIS"));
      copyResource(getURL(bundle, "vcast/test_scheduler.2.LIS"), tempFolder.newFile("test_scheduler.2.LIS"));
      copyResource(getURL(bundle, "vcast/results/test_unit_1.dat"), new File(resultsFolder, "test_unit_1.dat"));
      copyResource(getURL(bundle, "vcast/results/test_unit_2.dat"), new File(resultsFolder, "test_unit_2.dat"));
      copyResource(getURL(bundle, "vcast/results/test_unit_3.dat"), new File(resultsFolder, "test_unit_3.dat"));

      VCast60Params params =
         new VCast60Params(tempFolder.getRoot().getAbsolutePath() + "/", "test", false, dbFile.getName());
      coverageImport = new CoverageImport("VectorCast Import");
      IOperation operation = CoverageImportFactory.createAdaVCast60ImportOp(params, coverageImport);
      Operations.executeWorkAndCheckStatus(operation);

      Assert.assertNotNull(coverageImport);
      Assert.assertFalse(coverageImport.getLog().isErrors());

      // Check import results
      Assert.assertEquals(12, coverageImport.getCoverageItems().size());
      Assert.assertEquals(58, coverageImport.getCoveragePercent().intValue());
      Assert.assertEquals(7, coverageImport.getCoverageItemsCovered().size());
      Assert.assertEquals(0, coverageImport.getCoverageItemsCount(CoverageOptionManager.Deactivated_Code));
      Assert.assertEquals(0, coverageImport.getCoverageItemsCount(CoverageOptionManager.Exception_Handling));
      Assert.assertEquals(7, coverageImport.getCoverageItemsCount(CoverageOptionManager.Test_Unit));
      Assert.assertEquals(5, coverageImport.getCoverageItemsCount(CoverageOptionManager.Not_Covered));

      // Ensure all coverageItems have valid name
      for (CoverageItem coverageItem : coverageImport.getCoverageItems()) {
         Assert.assertTrue(Strings.isValid(coverageItem.getName()));
      }

      // Ensure all coverageItems have valid namespace
      for (CoverageItem coverageItem : coverageImport.getCoverageItems()) {
         Assert.assertTrue(Strings.isValid(coverageItem.getNamespace()));
      }

      // Ensure all coverageItems have valid orderNumber
      for (CoverageItem coverageItem : coverageImport.getCoverageItems()) {
         Assert.assertTrue(Strings.isValid(coverageItem.getOrderNumber()));
      }

      // Test MergeManager
      coveragePackage =
         new CoveragePackage("Test Coverage Package", CoverageOptionManagerDefault.instance(),
            new SimpleWorkProductTaskProvider());
      MergeManager mergeManager = new MergeManager(coveragePackage, coverageImport);

      // Add printout to catch intermittent failure
      if (mergeManager.getMergeItems(null).size() > 1) {
         int x = 0;
         System.err.println("Unexpected multiple merge items...");
         for (IMergeItem mergeItem : mergeManager.getMergeItems(null)) {
            System.err.println("MergeItem: " + x + " - " + mergeItem);
            x++;
         }
      }
      Assert.assertEquals(1, mergeManager.getMergeItems(null).size());
      Assert.assertEquals(MergeType.Add, mergeManager.getMergeItems(null).iterator().next().getMergeType());

      MergeImportManager importManager = new MergeImportManager(mergeManager);
      XResultData resultData = importManager.importItems(new ISaveable() {

         @Override
         public Result isEditable() {
            return Result.TrueResult;
         }

         @Override
         public Result save(String saveName, CoverageOptionManager coverageOptionManager) {
            return Result.TrueResult;
         }

         @Override
         public Result save(Collection<ICoverage> coverages, String saveName) {
            return Result.TrueResult;
         }

         @Override
         public Result saveImportRecord(SkynetTransaction transaction, CoverageImport coverageImport) {
            return Result.TrueResult;
         }

         @Override
         public IOseeBranch getBranch() {
            return CoverageTestUtil.getTestBranch();
         }

      }, mergeManager.getMergeItems(null));
      Assert.assertEquals(0, resultData.getNumErrors());

      // Ensure all coverageItems have valid name
      for (CoverageItem coverageItem : coveragePackage.getCoverageItems()) {
         Assert.assertTrue(Strings.isValid(coverageItem.getName()));
      }

      // Ensure all coverageItems have valid namespace
      for (CoverageItem coverageItem : coveragePackage.getCoverageItems()) {
         Assert.assertTrue(Strings.isValid(coverageItem.getNamespace()));
      }

      // Ensure all coverageItems have valid orderNumber
      for (CoverageItem coverageItem : coveragePackage.getCoverageItems()) {
         Assert.assertTrue(Strings.isValid(coverageItem.getOrderNumber()));
      }

      CoverageUtil.printCoverageItemDiffs(coveragePackage, coverageImport);
      // System.out.println(CoverageUtil.printTree(coveragePackage));

      // CoveragePackage should now have imported results
      Assert.assertEquals(12, coveragePackage.getCoverageItems().size());
      Assert.assertEquals(58, coveragePackage.getCoveragePercent().intValue());
      Assert.assertEquals(7, coveragePackage.getCoverageItemsCovered().size());
      Assert.assertEquals(0, coveragePackage.getCoverageItemsCount(CoverageOptionManager.Deactivated_Code));
      Assert.assertEquals(0, coveragePackage.getCoverageItemsCount(CoverageOptionManager.Exception_Handling));
      Assert.assertEquals(7, coveragePackage.getCoverageItemsCount(CoverageOptionManager.Test_Unit));
      Assert.assertEquals(5, coveragePackage.getCoverageItemsCount(CoverageOptionManager.Not_Covered));

      CoveragePackage loadedCp = null;
      if (testWithDb) {
         // Test Persist of CoveragePackage
         OseeCoverageStore store = OseeCoveragePackageStore.get(coveragePackage, CoverageTestUtil.getTestBranch());
         String txComment =
            String.format("Coverage Package Save: %s.%s", getClass().getSimpleName(), testName.getMethodName());
         SkynetTransaction transaction =
            TransactionManager.createTransaction(CoverageTestUtil.getTestBranch(), txComment);

         CoveragePackageEvent coverageEvent = new CoveragePackageEvent(coveragePackage, CoverageEventType.Modified);
         store.save(transaction, coverageEvent, coveragePackage.getCoverageOptionManager());
         store.getArtifact(false).persist(transaction);

         // Test Load of Coverage Package
         Artifact artifact =
            ArtifactQuery.getArtifactFromId(coveragePackage.getGuid(), CoverageTestUtil.getTestBranch());
         CoverageTestUtil.registerAsTestArtifact(artifact);
         artifact.persist(transaction);

         OseeCoveragePackageStore packageStore = new OseeCoveragePackageStore(artifact);
         Assert.assertNotNull(packageStore.getArtifact(false));
         loadedCp = packageStore.getCoveragePackage();
         packageStore.getArtifact(false).persist(transaction);

         transaction.execute();
      } else {
         loadedCp = coveragePackage;
      }

      Assert.assertEquals(12, loadedCp.getCoverageItems().size());
      Assert.assertEquals(58, loadedCp.getCoveragePercent().intValue());
      Assert.assertEquals(7, loadedCp.getCoverageItemsCovered().size());
      Assert.assertEquals(0, loadedCp.getCoverageItemsCount(CoverageOptionManager.Deactivated_Code));
      Assert.assertEquals(0, loadedCp.getCoverageItemsCount(CoverageOptionManager.Exception_Handling));
      Assert.assertEquals(7, loadedCp.getCoverageItemsCount(CoverageOptionManager.Test_Unit));
      Assert.assertEquals(5, loadedCp.getCoverageItemsCount(CoverageOptionManager.Not_Covered));
   }
}
