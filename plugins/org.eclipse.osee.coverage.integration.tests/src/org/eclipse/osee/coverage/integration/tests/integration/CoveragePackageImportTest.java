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
import static org.eclipse.osee.framework.core.enums.DeletionFlag.INCLUDE_DELETED;
import java.util.Collection;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeHousekeepingRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.coverage.ICoverageImporter;
import org.eclipse.osee.coverage.demo.CoverageExampleFactory;
import org.eclipse.osee.coverage.demo.CoverageExamples;
import org.eclipse.osee.coverage.event.CoverageEventType;
import org.eclipse.osee.coverage.event.CoveragePackageEvent;
import org.eclipse.osee.coverage.integration.tests.integration.util.CoverageTestUtil;
import org.eclipse.osee.coverage.merge.IMergeItem;
import org.eclipse.osee.coverage.merge.MergeImportManager;
import org.eclipse.osee.coverage.merge.MergeItemGroup;
import org.eclipse.osee.coverage.merge.MergeManager;
import org.eclipse.osee.coverage.merge.MergeType;
import org.eclipse.osee.coverage.merge.MessageMergeItem;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoverageOptionManager;
import org.eclipse.osee.coverage.model.CoverageOptionManagerDefault;
import org.eclipse.osee.coverage.model.CoveragePackage;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.coverage.model.SimpleWorkProductTaskProvider;
import org.eclipse.osee.coverage.store.OseeCoveragePackageStore;
import org.eclipse.osee.coverage.store.OseeCoverageStore;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.coverage.util.ISaveable;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

/**
 * @author Donald G. Dunne
 */
public class CoveragePackageImportTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_COVERAGE_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Rule
   public OseeHousekeepingRule hk = new OseeHousekeepingRule();

   @Rule
   public TestName testName = new TestName();

   public static CoveragePackage coveragePackage = null;
   public static CoverageImport coverageImport = null;
   private static boolean testWithDb = true;
   private static boolean runOnce = false;

   @Before
   public void setUp() throws OseeCoreException {
      if (!runOnce) {
         runOnce = true;
         CoverageUtil.setIsInTest(true);
         testCleanup();
      }
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

   private String getComment(int index) {
      return String.format("%s.%s %d", CoveragePackageImportTest.class.getSimpleName(), testName.getMethodName(), index);
   }

   @Test
   public void testImport1() throws Exception {
      ICoverageImporter importer = CoverageExampleFactory.createExample(CoverageExamples.COVERAGE_IMPORT_01);
      coverageImport = importer.run(null);
      Assert.assertNotNull(coverageImport);

      // Check import results
      Assert.assertEquals(60, coverageImport.getCoverageItemsCovered().size());
      Assert.assertEquals(122, coverageImport.getCoverageItems().size());

      // System.out.println(CoverageUtil.printTree(coverageImport));

      Assert.assertEquals(49, coverageImport.getCoveragePercent().intValue());
      Assert.assertEquals(0, coverageImport.getCoverageItemsCount(CoverageOptionManager.Deactivated_Code));
      Assert.assertEquals(0, coverageImport.getCoverageItemsCount(CoverageOptionManager.Exception_Handling));
      Assert.assertEquals(60, coverageImport.getCoverageItemsCount(CoverageOptionManager.Test_Unit));
      Assert.assertEquals(62, coverageImport.getCoverageItemsCount(CoverageOptionManager.Not_Covered));

      for (CoverageItem coverageItem : coverageImport.getCoverageItems()) {
         Assert.assertTrue(Strings.isValid(coverageItem.getName()));
         Assert.assertTrue(Strings.isValid(coverageItem.getNamespace()));
         Assert.assertTrue(Strings.isValid(coverageItem.getOrderNumber()));
      }

      // Test MergeManager
      coveragePackage =
         new CoveragePackage("Test Coverage Package", CoverageOptionManagerDefault.instance(),
            new SimpleWorkProductTaskProvider());
      MergeManager mergeManager = new MergeManager(coveragePackage, coverageImport);
      Assert.assertEquals(4, mergeManager.getMergeItems(null).size());
      for (IMergeItem mergeItem : mergeManager.getMergeItems(null)) {
         Assert.assertEquals(MergeType.Add, mergeItem.getMergeType());
      }

      // Test import where not-editable
      MergeImportManager importManager = new MergeImportManager(mergeManager);
      XResultData resultData = importManager.importItems(new ISaveable() {

         @Override
         public Result isEditable() {
            return Result.FalseResult;
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
      Assert.assertEquals(1, resultData.getNumErrors());

      resultData = importManager.importItems(new ISaveable() {

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
      Assert.assertEquals(60, coveragePackage.getCoverageItemsCovered().size());
      Assert.assertEquals(122, coveragePackage.getCoverageItems().size());
      Assert.assertEquals(49, coveragePackage.getCoveragePercent().intValue());
      Assert.assertEquals(0, coveragePackage.getCoverageItemsCount(CoverageOptionManager.Deactivated_Code));
      Assert.assertEquals(0, coveragePackage.getCoverageItemsCount(CoverageOptionManager.Exception_Handling));
      Assert.assertEquals(60, coveragePackage.getCoverageItemsCount(CoverageOptionManager.Test_Unit));
      Assert.assertEquals(62, coveragePackage.getCoverageItemsCount(CoverageOptionManager.Not_Covered));

      CoveragePackage loadedCp = null;
      if (testWithDb) {
         // Test Persist of CoveragePackage
         OseeCoverageStore store = OseeCoveragePackageStore.get(coveragePackage, CoverageTestUtil.getTestBranch());
         SkynetTransaction transaction =
            TransactionManager.createTransaction(CoverageTestUtil.getTestBranch(),
               "Coverage Package Save " + testName.getMethodName());

         store.save(transaction, getTestCoveragePackageEvent(), coveragePackage.getCoverageOptionManager());
         Artifact artifactX = store.getArtifact(false);
         artifactX.persist(transaction);

         // Test Load of Coverage Package
         Artifact artifact =
            ArtifactQuery.getArtifactFromId(coveragePackage.getGuid(), CoverageTestUtil.getTestBranch());
         CoverageTestUtil.registerAsTestArtifact(artifact);
         OseeSystemArtifacts.getDefaultHierarchyRootArtifact(artifact.getBranch()).addChild(artifact);
         artifact.persist(transaction);

         OseeCoveragePackageStore packageStore = new OseeCoveragePackageStore(artifact);
         Assert.assertNotNull(packageStore.getArtifact(false));
         loadedCp = packageStore.getCoveragePackage();

         transaction.execute();
      } else {
         loadedCp = coveragePackage;
      }

      Assert.assertEquals(60, loadedCp.getCoverageItemsCovered().size());
      Assert.assertEquals(122, loadedCp.getCoverageItems().size());
      Assert.assertEquals(49, loadedCp.getCoveragePercent().intValue());
      Assert.assertEquals(0, loadedCp.getCoverageItemsCount(CoverageOptionManager.Deactivated_Code));
      Assert.assertEquals(0, loadedCp.getCoverageItemsCount(CoverageOptionManager.Exception_Handling));
      Assert.assertEquals(60, loadedCp.getCoverageItemsCount(CoverageOptionManager.Test_Unit));
      Assert.assertEquals(62, loadedCp.getCoverageItemsCount(CoverageOptionManager.Not_Covered));

   }

   @Test
   @Ignore("Old import merge capabilities replaced.  These should be removed when that code is removed.")
   // Re-import with no changes, make sure no merge items exist
   public void testImport1B() throws Exception {
      ICoverageImporter importer = CoverageExampleFactory.createExample(CoverageExamples.COVERAGE_IMPORT_01);
      coverageImport = importer.run(null);
      Assert.assertNotNull(coverageImport);

      // Check import results
      Assert.assertEquals(60, coverageImport.getCoverageItemsCovered().size());
      Assert.assertEquals(122, coverageImport.getCoverageItems().size());
      Assert.assertEquals(49, coverageImport.getCoveragePercent().intValue());
      Assert.assertEquals(0, coverageImport.getCoverageItemsCount(CoverageOptionManager.Deactivated_Code));
      Assert.assertEquals(0, coverageImport.getCoverageItemsCount(CoverageOptionManager.Exception_Handling));
      Assert.assertEquals(60, coverageImport.getCoverageItemsCount(CoverageOptionManager.Test_Unit));
      Assert.assertEquals(62, coverageImport.getCoverageItemsCount(CoverageOptionManager.Not_Covered));

      // Test MergeManager
      Assert.assertNotNull(coveragePackage);
      MergeManager mergeManager = new MergeManager(coveragePackage, coverageImport);
      Assert.assertEquals(1, mergeManager.getMergeItems(null).size());
      // Merge item will be the "Nothing to Import" message item
      Assert.assertTrue(mergeManager.getMergeItems(null).iterator().next() instanceof MessageMergeItem);
   }

   @Test
   @Ignore("Old import merge capabilities replaced.  These should be removed when that code is removed.")
   // Re-import two new Coverage Unit files
   // com.screenA.ComScrnButton3 and epu.PowerUnit3
   public void testImport2() throws Exception {
      ICoverageImporter importer = CoverageExampleFactory.createExample(CoverageExamples.COVERAGE_IMPORT_02);
      coverageImport = importer.run(null);
      Assert.assertNotNull(coverageImport);

      // Test MergeManager
      Assert.assertNotNull(coveragePackage);
      MergeManager mergeManager = new MergeManager(coveragePackage, coverageImport);
      Assert.assertEquals(2, mergeManager.getMergeItems(null).size());
      for (IMergeItem mergeItem : mergeManager.getMergeItems(null)) {
         Assert.assertEquals(MergeType.Add, mergeItem.getMergeType());
      }
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

      // CoveragePackage should now have imported results
      Assert.assertEquals(66, coveragePackage.getCoverageItemsCovered().size());
      Assert.assertEquals(133, coveragePackage.getCoverageItems().size());
      Assert.assertEquals(49, coveragePackage.getCoveragePercent().intValue());
      Assert.assertEquals(0, coveragePackage.getCoverageItemsCount(CoverageOptionManager.Deactivated_Code));
      Assert.assertEquals(0, coveragePackage.getCoverageItemsCount(CoverageOptionManager.Exception_Handling));
      Assert.assertEquals(66, coveragePackage.getCoverageItemsCount(CoverageOptionManager.Test_Unit));
      Assert.assertEquals(67, coveragePackage.getCoverageItemsCount(CoverageOptionManager.Not_Covered));

      CoveragePackage loadedCp = null;
      if (testWithDb) {
         // Test Persist of CoveragePackage
         OseeCoverageStore store = OseeCoveragePackageStore.get(coveragePackage, CoverageTestUtil.getTestBranch());
         SkynetTransaction transaction =
            TransactionManager.createTransaction(CoverageTestUtil.getTestBranch(),
               "Coverage Package Save " + testName.getMethodName());
         store.save(transaction, getTestCoveragePackageEvent(), coveragePackage.getCoverageOptionManager());
         store.getArtifact(false).persist(transaction);

         // Test Load of Coverage Package
         Artifact artifact =
            ArtifactQuery.getArtifactFromId(coveragePackage.getGuid(), CoverageTestUtil.getTestBranch());
         CoverageTestUtil.registerAsTestArtifact(artifact);
         artifact.persist(transaction);

         OseeCoveragePackageStore packageStore = new OseeCoveragePackageStore(artifact);
         Assert.assertNotNull(packageStore.getArtifact(false));
         loadedCp = packageStore.getCoveragePackage();

         transaction.execute();
      } else {
         loadedCp = coveragePackage;
      }

      Assert.assertEquals(66, loadedCp.getCoverageItemsCovered().size());
      Assert.assertEquals(133, loadedCp.getCoverageItems().size());
      Assert.assertEquals(49, loadedCp.getCoveragePercent().intValue());
      Assert.assertEquals(0, loadedCp.getCoverageItemsCount(CoverageOptionManager.Deactivated_Code));
      Assert.assertEquals(0, loadedCp.getCoverageItemsCount(CoverageOptionManager.Exception_Handling));
      Assert.assertEquals(66, loadedCp.getCoverageItemsCount(CoverageOptionManager.Test_Unit));
      Assert.assertEquals(67, loadedCp.getCoverageItemsCount(CoverageOptionManager.Not_Covered));

   }

   @Test
   @Ignore("Old import merge capabilities replaced.  These should be removed when that code is removed.")
   // Add PowerUnit1.initAdded to end; Change getColumnCount.line1 from TestUnit2 to TestUnit3
   public void testImport3() throws Exception {
      ICoverageImporter importer = CoverageExampleFactory.createExample(CoverageExamples.COVERAGE_IMPORT_03);
      coverageImport = importer.run(null);
      Assert.assertNotNull(coverageImport);

      SkynetTransaction testLoadCovPackageTransaction =
         TransactionManager.createTransaction(CoverageTestUtil.getTestBranch(),
            String.format("%s.%s", CoveragePackageImportTest.class.getSimpleName(), testName.getMethodName()));
      if (testWithDb) {
         // Test Load of Coverage Package
         Artifact artifact =
            ArtifactQuery.getArtifactFromId(coveragePackage.getGuid(), CoverageTestUtil.getTestBranch());
         CoverageTestUtil.registerAsTestArtifact(artifact);
         artifact.persist(testLoadCovPackageTransaction);
         OseeCoveragePackageStore packageStore = new OseeCoveragePackageStore(artifact);
         Assert.assertNotNull(packageStore.getArtifact(false));
         coveragePackage = packageStore.getCoveragePackage();
      }
      testLoadCovPackageTransaction.execute();

      // Look at file contents for PowerUnit1.java
      CoverageUnit coverageUnit =
         (CoverageUnit) CoverageTestUtil.getFirstCoverageByNameEquals(coveragePackage, "PowerUnit1.java");
      Assert.assertNotNull(coverageUnit);
      String preFileContents = coverageUnit.getFileContents();
      Assert.assertFalse("initAdded should not yet exist in file contents", preFileContents.contains("initAdded"));

      // Check getColumnCount test unit is covered by TestUnit2
      CoverageUnit columnCountUnit =
         CoverageTestUtil.getFirstCoverageUnitByNameContains(coverageUnit, "getColumnCount");
      Assert.assertNotNull(columnCountUnit);

      // Confirm that getColumnCount line1 is covered by TestUnit2
      CoverageItem coverageItem =
         CoverageTestUtil.getFirstCoverageItemByNameContains(columnCountUnit, "getColumnCount");
      Assert.assertNotNull(coverageItem);
      Assert.assertEquals(1, coverageItem.getTestUnits().size());
      Assert.assertEquals("TestUnit2", coverageItem.getTestUnits().iterator().next());

      // Test MergeManager
      Assert.assertNotNull(coveragePackage);
      MergeManager mergeManager = new MergeManager(coveragePackage, coverageImport);
      Assert.assertEquals(2, mergeManager.getMergeItems(null).size());
      int numAdd = 0, numTestUnitUpdate = 0;
      for (IMergeItem mergeItem : mergeManager.getMergeItems(null)) {
         if (MergeType.Add == mergeItem.getMergeType()) {
            numAdd++;
         } else if (MergeType.CI_Changes == mergeItem.getMergeType()) {
            if (((MergeItemGroup) mergeItem).getMergeItems().iterator().next().getMergeType() == MergeType.CI_Test_Units_Update) {
               numTestUnitUpdate++;
            }
         }
      }
      Assert.assertEquals(1, numAdd);
      Assert.assertEquals(1, numTestUnitUpdate);
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

      // CoveragePackage should now have imported results
      Assert.assertEquals(67, coveragePackage.getCoverageItemsCovered().size());
      Assert.assertEquals(134, coveragePackage.getCoverageItems().size());
      Assert.assertEquals(50, coveragePackage.getCoveragePercent().intValue());
      Assert.assertEquals(0, coveragePackage.getCoverageItemsCount(CoverageOptionManager.Deactivated_Code));
      Assert.assertEquals(0, coveragePackage.getCoverageItemsCount(CoverageOptionManager.Exception_Handling));
      Assert.assertEquals(67, coveragePackage.getCoverageItemsCount(CoverageOptionManager.Test_Unit));
      Assert.assertEquals(67, coveragePackage.getCoverageItemsCount(CoverageOptionManager.Not_Covered));

      // Confirm that fileContents were updated
      coverageUnit = (CoverageUnit) CoverageTestUtil.getFirstCoverageByNameEquals(coveragePackage, "PowerUnit1.java");
      Assert.assertNotNull(coverageUnit);
      String postFileContents = coverageUnit.getFileContents();
      Assert.assertTrue("File Contents should have been updated and thus not equal",
         !postFileContents.equals(preFileContents));
      Assert.assertTrue("initAdded should now exist in file contents", postFileContents.contains("initAdded"));

      CoveragePackage loadedCp = null;
      if (testWithDb) {
         // Test Persist of CoveragePackage
         OseeCoverageStore store = OseeCoveragePackageStore.get(coveragePackage, CoverageTestUtil.getTestBranch());
         SkynetTransaction transaction =
            TransactionManager.createTransaction(CoverageTestUtil.getTestBranch(),
               "Coverage Package Save " + testName.getMethodName());
         store.save(transaction, getTestCoveragePackageEvent(), coveragePackage.getCoverageOptionManager());
         store.getArtifact(false).persist(transaction);

         // Test Load of Coverage Package
         Artifact artifact =
            ArtifactQuery.getArtifactFromId(coveragePackage.getGuid(), CoverageTestUtil.getTestBranch());
         CoverageTestUtil.registerAsTestArtifact(artifact);
         artifact.persist(transaction);

         OseeCoveragePackageStore packageStore = new OseeCoveragePackageStore(artifact);
         Assert.assertNotNull(packageStore.getArtifact(false));
         loadedCp = packageStore.getCoveragePackage();

         transaction.execute();
      } else {
         loadedCp = coveragePackage;
      }

      Assert.assertEquals(67, loadedCp.getCoverageItemsCovered().size());
      Assert.assertEquals(134, loadedCp.getCoverageItems().size());
      Assert.assertEquals(50, loadedCp.getCoveragePercent().intValue());
      Assert.assertEquals(0, loadedCp.getCoverageItemsCount(CoverageOptionManager.Deactivated_Code));
      Assert.assertEquals(0, loadedCp.getCoverageItemsCount(CoverageOptionManager.Exception_Handling));
      Assert.assertEquals(67, loadedCp.getCoverageItemsCount(CoverageOptionManager.Test_Unit));
      Assert.assertEquals(67, loadedCp.getCoverageItemsCount(CoverageOptionManager.Not_Covered));

      // Confirm that updated fileContents were loaded
      coverageUnit = (CoverageUnit) CoverageTestUtil.getFirstCoverageByNameEquals(loadedCp, "PowerUnit1.java");
      Assert.assertNotNull(coverageUnit);
      String postLoadFileContents = coverageUnit.getFileContents();
      Assert.assertEquals("File Contents should be same pre and post save", postLoadFileContents, postFileContents);
      Assert.assertTrue("initAdded should exist in loaded file contents", postLoadFileContents.contains("initAdded"));

      // Confirm that initAdded has single coverageItem and single coverage test unit
      CoverageUnit initAddedUnit = CoverageTestUtil.getFirstCoverageUnitByNameContains(coverageUnit, "initAdded");
      Assert.assertNotNull(initAddedUnit);
      Assert.assertEquals(1, initAddedUnit.getCoverageItems().size());
      CoverageItem item = initAddedUnit.getCoverageItems().iterator().next();
      Assert.assertEquals(CoverageOptionManager.Test_Unit, item.getCoverageMethod());
      Assert.assertEquals(1, item.getTestUnits().size());
      Assert.assertEquals("TestUnit4", item.getTestUnits().iterator().next());

      // Confirm that getColumnCount line1 is NOW covered by TestUnit3
      columnCountUnit = CoverageTestUtil.getFirstCoverageUnitByNameContains(coverageUnit, "getColumnCount");
      Assert.assertNotNull(columnCountUnit);
      coverageItem = CoverageTestUtil.getFirstCoverageItemByNameContains(columnCountUnit, "getColumnCount");
      Assert.assertNotNull(coverageItem);
      Assert.assertEquals(1, coverageItem.getTestUnits().size());
      Assert.assertEquals("TestUnit3", coverageItem.getTestUnits().iterator().next());
   }

   @Test
   @Ignore("Old import merge capabilities replaced.  These should be removed when that code is removed.")
   // Re-import with deselectAdded method added to middle of epu.PowerUnit1
   public void testImport4() throws Exception {
      ICoverageImporter importer = CoverageExampleFactory.createExample(CoverageExamples.COVERAGE_IMPORT_04);
      coverageImport = importer.run(null);
      Assert.assertNotNull(coverageImport);

      SkynetTransaction testLoadTransaction =
         TransactionManager.createTransaction(CoverageTestUtil.getTestBranch(),
            String.format("%s.%s", CoveragePackageImportTest.class.getSimpleName(), testName.getMethodName()));
      if (testWithDb) {
         // Test Load of Coverage Package
         Artifact artifact =
            ArtifactQuery.getArtifactFromId(coveragePackage.getGuid(), CoverageTestUtil.getTestBranch());
         CoverageTestUtil.registerAsTestArtifact(artifact);
         artifact.persist(testLoadTransaction);
         OseeCoveragePackageStore packageStore = new OseeCoveragePackageStore(artifact);
         Assert.assertNotNull(packageStore.getArtifact(false));
         coveragePackage = packageStore.getCoveragePackage();
      }
      testLoadTransaction.execute();

      // Look at file contents for PowerUnit1.java
      CoverageUnit coverageUnit =
         (CoverageUnit) CoverageTestUtil.getFirstCoverageByNameEquals(coveragePackage, "PowerUnit1.java");
      Assert.assertNotNull(coverageUnit);
      String preFileContents = coverageUnit.getFileContents();
      Assert.assertFalse("deselectAdded should not yet exist in file contents",
         preFileContents.contains("deselectAdded"));

      // Test MergeManager
      Assert.assertNotNull(coveragePackage);
      MergeManager mergeManager = new MergeManager(coveragePackage, coverageImport);
      Assert.assertEquals(1, mergeManager.getMergeItems(null).size());
      for (IMergeItem mergeItem : mergeManager.getMergeItems(null)) {
         Assert.assertEquals(MergeType.Add_With_Moves, mergeItem.getMergeType());
      }
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

      // CoveragePackage should now have imported results
      Assert.assertEquals(68, coveragePackage.getCoverageItemsCovered().size());
      Assert.assertEquals(135, coveragePackage.getCoverageItems().size());
      Assert.assertEquals(50, coveragePackage.getCoveragePercent().intValue());
      Assert.assertEquals(0, coveragePackage.getCoverageItemsCount(CoverageOptionManager.Deactivated_Code));
      Assert.assertEquals(0, coveragePackage.getCoverageItemsCount(CoverageOptionManager.Exception_Handling));
      Assert.assertEquals(68, coveragePackage.getCoverageItemsCount(CoverageOptionManager.Test_Unit));
      Assert.assertEquals(67, coveragePackage.getCoverageItemsCount(CoverageOptionManager.Not_Covered));

      // Confirm that fileContents were updated
      coverageUnit = (CoverageUnit) CoverageTestUtil.getFirstCoverageByNameEquals(coveragePackage, "PowerUnit1.java");
      Assert.assertNotNull(coverageUnit);
      String postFileContents = coverageUnit.getFileContents();
      Assert.assertTrue("File Contents should have been updated and thus not equal",
         !postFileContents.equals(preFileContents));
      Assert.assertTrue("deselectAdded should now exist in file contents", postFileContents.contains("deselectAdded"));

      CoveragePackage loadedCp = null;
      if (testWithDb) {
         // Test Persist of CoveragePackage
         OseeCoverageStore store = OseeCoveragePackageStore.get(coveragePackage, CoverageTestUtil.getTestBranch());
         SkynetTransaction transaction =
            TransactionManager.createTransaction(CoverageTestUtil.getTestBranch(),
               "Coverage Package Save " + testName.getMethodName());
         store.save(transaction, getTestCoveragePackageEvent(), coveragePackage.getCoverageOptionManager());
         store.getArtifact(false).persist(transaction);

         // Test Load of Coverage Package
         Artifact artifact =
            ArtifactQuery.getArtifactFromId(coveragePackage.getGuid(), CoverageTestUtil.getTestBranch());
         CoverageTestUtil.registerAsTestArtifact(artifact);
         artifact.persist(transaction);

         OseeCoveragePackageStore packageStore = new OseeCoveragePackageStore(artifact);
         Assert.assertNotNull(packageStore.getArtifact(false));
         loadedCp = packageStore.getCoveragePackage();

         transaction.execute();
      } else {
         loadedCp = coveragePackage;
      }

      Assert.assertEquals(68, loadedCp.getCoverageItemsCovered().size());
      Assert.assertEquals(135, loadedCp.getCoverageItems().size());
      Assert.assertEquals(50, loadedCp.getCoveragePercent().intValue());
      Assert.assertEquals(0, loadedCp.getCoverageItemsCount(CoverageOptionManager.Deactivated_Code));
      Assert.assertEquals(0, loadedCp.getCoverageItemsCount(CoverageOptionManager.Exception_Handling));
      Assert.assertEquals(68, loadedCp.getCoverageItemsCount(CoverageOptionManager.Test_Unit));
      Assert.assertEquals(67, loadedCp.getCoverageItemsCount(CoverageOptionManager.Not_Covered));

      // Confirm that updated fileContents were loaded
      coverageUnit = (CoverageUnit) CoverageTestUtil.getFirstCoverageByNameEquals(coveragePackage, "PowerUnit1.java");
      Assert.assertNotNull(coverageUnit);
      String postLoadFileContents = coverageUnit.getFileContents();
      Assert.assertEquals("File Contents should be same pre and post save", postLoadFileContents, postFileContents);
      Assert.assertTrue("deselectAdded should exist in loaded file contents",
         postLoadFileContents.contains("deselectAdded"));
   }

   @Test
   @Ignore("Old import merge capabilities replaced.  These should be removed when that code is removed.")
   // Re-import; Add NavigationButton.setImage coverageItems 2,3 to end
   public void testImport5() throws Exception {
      ICoverageImporter importer = CoverageExampleFactory.createExample(CoverageExamples.COVERAGE_IMPORT_05);
      coverageImport = importer.run(null);
      Assert.assertNotNull(coverageImport);

      if (testWithDb) {
         // Test Load of Coverage Package
         Artifact artifact =
            ArtifactQuery.getArtifactFromId(coveragePackage.getGuid(), CoverageTestUtil.getTestBranch());
         CoverageTestUtil.registerAsTestArtifact(artifact);
         artifact.persist(getComment(1));
         OseeCoveragePackageStore packageStore = new OseeCoveragePackageStore(artifact);
         Assert.assertNotNull(packageStore.getArtifact(false));
         coveragePackage = packageStore.getCoveragePackage();
      }

      // Look at file contents for NavigationButton.setImage and make sure only one coverage item exists
      CoverageUnit coverageUnit =
         (CoverageUnit) CoverageTestUtil.getFirstCoverageByNameEquals(coveragePackage, "NavigationButton1.java");
      Assert.assertNotNull(coverageUnit);
      CoverageUnit setImageCoverageUnit = null;
      for (CoverageUnit childCoverageUnit : coverageUnit.getCoverageUnits()) {
         if (childCoverageUnit.getName().equals("setImage")) {
            setImageCoverageUnit = childCoverageUnit;
            break;
         }
      }
      Assert.assertNotNull(setImageCoverageUnit);
      Assert.assertEquals(1, setImageCoverageUnit.getCoverageItems().size());

      // Test MergeManager
      Assert.assertNotNull(coveragePackage);
      MergeManager mergeManager = new MergeManager(coveragePackage, coverageImport);
      Assert.assertEquals(1, mergeManager.getMergeItems(null).size());
      Assert.assertTrue(mergeManager.getMergeItems(null).iterator().next() instanceof MergeItemGroup);
      Assert.assertTrue(((MergeItemGroup) mergeManager.getMergeItems(null).iterator().next()).getMergeType() == MergeType.CI_Changes);
      Assert.assertEquals(2,
         ((MergeItemGroup) mergeManager.getMergeItems(null).iterator().next()).getMergeItems().size());

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

      // CoveragePackage should now have imported results
      Assert.assertEquals(69, coveragePackage.getCoverageItemsCovered().size());
      Assert.assertEquals(137, coveragePackage.getCoverageItems().size());
      Assert.assertEquals(50, coveragePackage.getCoveragePercent().intValue());
      Assert.assertEquals(0, coveragePackage.getCoverageItemsCount(CoverageOptionManager.Deactivated_Code));
      Assert.assertEquals(0, coveragePackage.getCoverageItemsCount(CoverageOptionManager.Exception_Handling));
      Assert.assertEquals(69, coveragePackage.getCoverageItemsCount(CoverageOptionManager.Test_Unit));
      Assert.assertEquals(68, coveragePackage.getCoverageItemsCount(CoverageOptionManager.Not_Covered));

      CoveragePackage loadedCp = null;
      if (testWithDb) {
         // Test Persist of CoveragePackage
         OseeCoverageStore store = OseeCoveragePackageStore.get(coveragePackage, CoverageTestUtil.getTestBranch());
         SkynetTransaction transaction =
            TransactionManager.createTransaction(CoverageTestUtil.getTestBranch(),
               "Coverage Package Save " + testName.getMethodName());
         store.save(transaction, getTestCoveragePackageEvent(), coveragePackage.getCoverageOptionManager());
         store.getArtifact(false).persist(transaction);

         // Test Load of Coverage Package
         Artifact artifact =
            ArtifactQuery.getArtifactFromId(coveragePackage.getGuid(), CoverageTestUtil.getTestBranch());
         CoverageTestUtil.registerAsTestArtifact(artifact);
         artifact.persist(transaction);

         OseeCoveragePackageStore packageStore = new OseeCoveragePackageStore(artifact);
         Assert.assertNotNull(packageStore.getArtifact(false));
         loadedCp = packageStore.getCoveragePackage();

         transaction.execute();
      } else {
         loadedCp = coveragePackage;
      }

      Assert.assertEquals(69, loadedCp.getCoverageItemsCovered().size());
      Assert.assertEquals(137, loadedCp.getCoverageItems().size());
      Assert.assertEquals(50, loadedCp.getCoveragePercent().intValue());
      Assert.assertEquals(0, loadedCp.getCoverageItemsCount(CoverageOptionManager.Deactivated_Code));
      Assert.assertEquals(0, loadedCp.getCoverageItemsCount(CoverageOptionManager.Exception_Handling));
      Assert.assertEquals(69, loadedCp.getCoverageItemsCount(CoverageOptionManager.Test_Unit));
      Assert.assertEquals(68, loadedCp.getCoverageItemsCount(CoverageOptionManager.Not_Covered));
   }

   @Test
   @Ignore("Old import merge capabilities replaced.  These should be removed when that code is removed.")
   // Re-import; Delete PowerUnit2.clear
   public void testImport6() throws Exception {
      ICoverageImporter importer = CoverageExampleFactory.createExample(CoverageExamples.COVERAGE_IMPORT_06);
      coverageImport = importer.run(null);
      Assert.assertNotNull(coverageImport);

      if (testWithDb) {
         // Test Load of Coverage Package
         Artifact artifact =
            ArtifactQuery.getArtifactFromId(coveragePackage.getGuid(), CoverageTestUtil.getTestBranch());
         CoverageTestUtil.registerAsTestArtifact(artifact);
         artifact.persist(getComment(1));
         OseeCoveragePackageStore packageStore = new OseeCoveragePackageStore(artifact);
         Assert.assertNotNull(packageStore.getArtifact(false));
         coveragePackage = packageStore.getCoveragePackage();
      }

      // Get and store off coverage unit to delete so can confirm deletion occurred
      CoverageUnit powerUnit2CoverageUnit =
         (CoverageUnit) CoverageTestUtil.getFirstCoverageByNameEquals(coveragePackage, "PowerUnit2.java");
      Assert.assertNotNull(powerUnit2CoverageUnit);
      CoverageUnit clearCoverageUnitForDeletion = null;
      for (CoverageUnit childCoverageUnit : powerUnit2CoverageUnit.getCoverageUnits()) {
         if (childCoverageUnit.getName().equals("clear")) {
            clearCoverageUnitForDeletion = childCoverageUnit;
            break;
         }
      }
      Assert.assertNotNull("clear CoverageUnit should exist", clearCoverageUnitForDeletion);

      // Test MergeManager
      Assert.assertNotNull(coveragePackage);
      MergeManager mergeManager = new MergeManager(coveragePackage, coverageImport);
      Assert.assertEquals(1, mergeManager.getMergeItems(null).size());
      Assert.assertTrue(mergeManager.getMergeItems(null).iterator().next() instanceof MergeItemGroup);
      int numDelete = 0, numMoveDueToDelete = 0;
      for (IMergeItem mergeItem : ((MergeItemGroup) mergeManager.getMergeItems(null).iterator().next()).getMergeItems()) {
         if (mergeItem.getMergeType() == MergeType.Delete) {
            numDelete++;
         } else if (mergeItem.getMergeType() == MergeType.Moved_Due_To_Delete) {
            numMoveDueToDelete++;
         } else {
            throw new OseeStateException("Unexpected merge type for Delete [%s]", mergeItem.getMergeType());
         }
      }
      Assert.assertEquals(1, numDelete);
      Assert.assertEquals(3, numMoveDueToDelete);

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

      // Make sure clear is not there anymore
      powerUnit2CoverageUnit =
         (CoverageUnit) CoverageTestUtil.getFirstCoverageByNameEquals(coveragePackage, "PowerUnit2.java");
      Assert.assertNotNull(powerUnit2CoverageUnit);
      boolean found = false;
      for (CoverageUnit childCoverageUnit : powerUnit2CoverageUnit.getCoverageUnits()) {
         if (childCoverageUnit.getName().equals("clear")) {
            found = true;
            break;
         }
      }
      Assert.assertFalse("clear CoverageUnit should have been deleted", found);

      // CoveragePackage should now have imported results
      Assert.assertEquals(68, coveragePackage.getCoverageItemsCovered().size());
      Assert.assertEquals(135, coveragePackage.getCoverageItems().size());
      Assert.assertEquals(50, coveragePackage.getCoveragePercent().intValue());
      Assert.assertEquals(0, coveragePackage.getCoverageItemsCount(CoverageOptionManager.Deactivated_Code));
      Assert.assertEquals(0, coveragePackage.getCoverageItemsCount(CoverageOptionManager.Exception_Handling));
      Assert.assertEquals(68, coveragePackage.getCoverageItemsCount(CoverageOptionManager.Test_Unit));
      Assert.assertEquals(67, coveragePackage.getCoverageItemsCount(CoverageOptionManager.Not_Covered));

      CoveragePackage loadedCp = null;
      if (testWithDb) {
         // Test Persist of CoveragePackage
         OseeCoverageStore store = OseeCoveragePackageStore.get(coveragePackage, CoverageTestUtil.getTestBranch());
         SkynetTransaction transaction =
            TransactionManager.createTransaction(CoverageTestUtil.getTestBranch(),
               "Coverage Package Save " + testName.getMethodName());
         store.save(transaction, getTestCoveragePackageEvent(), coveragePackage.getCoverageOptionManager());
         store.getArtifact(false).persist(transaction);

         // Test Load of Coverage Package
         Artifact artifact =
            ArtifactQuery.getArtifactFromId(coveragePackage.getGuid(), CoverageTestUtil.getTestBranch());
         CoverageTestUtil.registerAsTestArtifact(artifact);
         artifact.persist(transaction);

         OseeCoveragePackageStore packageStore = new OseeCoveragePackageStore(artifact);
         Assert.assertNotNull(packageStore.getArtifact(false));
         loadedCp = packageStore.getCoveragePackage();

         transaction.execute();
      } else {
         loadedCp = coveragePackage;
      }

      Assert.assertEquals(68, loadedCp.getCoverageItemsCovered().size());
      Assert.assertEquals(135, loadedCp.getCoverageItems().size());
      Assert.assertEquals(50, loadedCp.getCoveragePercent().intValue());
      Assert.assertEquals(0, loadedCp.getCoverageItemsCount(CoverageOptionManager.Deactivated_Code));
      Assert.assertEquals(0, loadedCp.getCoverageItemsCount(CoverageOptionManager.Exception_Handling));
      Assert.assertEquals(68, loadedCp.getCoverageItemsCount(CoverageOptionManager.Test_Unit));
      Assert.assertEquals(67, loadedCp.getCoverageItemsCount(CoverageOptionManager.Not_Covered));

      // Make sure clear is not there anymore
      powerUnit2CoverageUnit =
         (CoverageUnit) CoverageTestUtil.getFirstCoverageByNameEquals(loadedCp, "PowerUnit2.java");
      Assert.assertNotNull(powerUnit2CoverageUnit);
      found = false;
      for (CoverageUnit childCoverageUnit : powerUnit2CoverageUnit.getCoverageUnits()) {
         if (childCoverageUnit.getName().equals("clear")) {
            found = true;
            break;
         }
      }
      Assert.assertFalse("\"clear\" CoverageUnit should have been deleted", found);

      // Ensure that the artifact was deleted and not just relation deletion
      try {
         ArtifactQuery.getArtifactFromId(clearCoverageUnitForDeletion.getGuid(), CoverageTestUtil.getTestBranch());
         Assert.fail("clear CoverageUnit should no longer exist");
      } catch (ArtifactDoesNotExist ex) {
         // do nothing, this exception should have been thrown
      }
      Artifact clearArt =
         ArtifactQuery.getArtifactFromId(clearCoverageUnitForDeletion.getGuid(), CoverageTestUtil.getTestBranch(),
            INCLUDE_DELETED);
      Assert.assertNotNull("clear CoverageUnit should exist if search for deleted == true", clearArt);

   }

   @Test
   @Ignore("Old import merge capabilities replaced.  These should be removed when that code is removed.")
   // Re-import; Change items AuxPowerUnit1.clear line 2 and 3
   public void testImport7() throws Exception {
      ICoverageImporter importer = CoverageExampleFactory.createExample(CoverageExamples.COVERAGE_IMPORT_07);
      coverageImport = importer.run(null);
      Assert.assertNotNull(coverageImport);

      if (testWithDb) {
         // Test Load of Coverage Package
         Artifact artifact =
            ArtifactQuery.getArtifactFromId(coveragePackage.getGuid(), CoverageTestUtil.getTestBranch());
         CoverageTestUtil.registerAsTestArtifact(artifact);
         artifact.persist(getComment(1));
         OseeCoveragePackageStore packageStore = new OseeCoveragePackageStore(artifact);
         Assert.assertNotNull(packageStore.getArtifact(false));
         coveragePackage = packageStore.getCoveragePackage();
      }

      // Get and store off coverage unit to delete so can confirm deletion occurred
      CoverageUnit auxPowerUnit1CoverageUnit =
         (CoverageUnit) CoverageTestUtil.getFirstCoverageByNameEquals(coveragePackage, "AuxPowerUnit1.java");
      Assert.assertNotNull(auxPowerUnit1CoverageUnit);
      CoverageUnit clearCoverageUnit = null;
      for (CoverageUnit childCoverageUnit : auxPowerUnit1CoverageUnit.getCoverageUnits()) {
         if (childCoverageUnit.getName().equals("clear")) {
            clearCoverageUnit = childCoverageUnit;
            break;
         }
      }

      Assert.assertNotNull(clearCoverageUnit);
      CoverageItem clearCoverageItemNum2 = null, clearCoverageItemNum3 = null;
      for (CoverageItem childCoverageUnit : clearCoverageUnit.getCoverageItems()) {
         if (childCoverageUnit.getName().contains("clear it")) {
            clearCoverageItemNum2 = childCoverageUnit;
         }
         if (childCoverageUnit.getName().contains("clear")) {
            clearCoverageItemNum3 = childCoverageUnit;
         }
      }
      Assert.assertNotNull(clearCoverageItemNum2);
      Assert.assertEquals(CoverageOptionManager.Not_Covered, clearCoverageItemNum2.getCoverageMethod());
      Assert.assertTrue("No test units should be stored", clearCoverageItemNum2.getTestUnits().isEmpty());
      Assert.assertNotNull(clearCoverageItemNum3);
      Assert.assertEquals(CoverageOptionManager.Not_Covered, clearCoverageItemNum3.getCoverageMethod());
      Assert.assertTrue("No test units should be stored", clearCoverageItemNum3.getTestUnits().isEmpty());

      String preFileContents = auxPowerUnit1CoverageUnit.getFileContents();
      Assert.assertFalse("\"clear it now\" should not yet exist in file contents",
         preFileContents.contains("clear it now"));

      // Test MergeManager
      Assert.assertNotNull(coveragePackage);
      MergeManager mergeManager = new MergeManager(coveragePackage, coverageImport);
      Assert.assertEquals(1, mergeManager.getMergeItems(null).size());
      Assert.assertTrue(mergeManager.getMergeItems(null).iterator().next() instanceof MergeItemGroup);
      int numRename = 0;
      for (IMergeItem mergeItem : ((MergeItemGroup) mergeManager.getMergeItems(null).iterator().next()).getMergeItems()) {
         if (mergeItem.getMergeType() == MergeType.CI_Renamed) {
            numRename++;
         } else {
            throw new OseeStateException("Unexpected merge type [%s] for Delete_And_Reorder group",
               mergeItem.getMergeType());
         }
      }
      Assert.assertEquals(2, numRename);

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

      // CoveragePackage should now have imported results
      Assert.assertEquals(69, coveragePackage.getCoverageItemsCovered().size());
      Assert.assertEquals(135, coveragePackage.getCoverageItems().size());
      Assert.assertEquals(51, coveragePackage.getCoveragePercent().intValue());
      Assert.assertEquals(0, coveragePackage.getCoverageItemsCount(CoverageOptionManager.Deactivated_Code));
      Assert.assertEquals(0, coveragePackage.getCoverageItemsCount(CoverageOptionManager.Exception_Handling));
      Assert.assertEquals(69, coveragePackage.getCoverageItemsCount(CoverageOptionManager.Test_Unit));
      Assert.assertEquals(66, coveragePackage.getCoverageItemsCount(CoverageOptionManager.Not_Covered));

      CoveragePackage loadedCp = null;
      if (testWithDb) {
         // Test Persist of CoveragePackage
         OseeCoverageStore store = OseeCoveragePackageStore.get(coveragePackage, CoverageTestUtil.getTestBranch());
         SkynetTransaction transaction =
            TransactionManager.createTransaction(CoverageTestUtil.getTestBranch(),
               "Coverage Package Save " + testName.getMethodName());
         store.save(transaction, getTestCoveragePackageEvent(), coveragePackage.getCoverageOptionManager());
         store.getArtifact(false).persist(transaction);

         // Test Load of Coverage Package
         Artifact artifact =
            ArtifactQuery.getArtifactFromId(coveragePackage.getGuid(), CoverageTestUtil.getTestBranch());
         CoverageTestUtil.registerAsTestArtifact(artifact);
         artifact.persist(transaction);

         OseeCoveragePackageStore packageStore = new OseeCoveragePackageStore(artifact);
         Assert.assertNotNull(packageStore.getArtifact(false));
         loadedCp = packageStore.getCoveragePackage();

         transaction.execute();
      } else {
         loadedCp = coveragePackage;
      }

      // Confirm that fileContents were updated
      auxPowerUnit1CoverageUnit =
         (CoverageUnit) CoverageTestUtil.getFirstCoverageByNameEquals(loadedCp, "AuxPowerUnit1.java");
      Assert.assertNotNull(auxPowerUnit1CoverageUnit);
      String postFileContents = auxPowerUnit1CoverageUnit.getFileContents();
      Assert.assertTrue("File Contents should have been updated and thus not equal",
         !postFileContents.equals(preFileContents));
      Assert.assertTrue("\"clear it now\" should now exist in file contents", postFileContents.contains("clear it now"));

      Assert.assertEquals(69, loadedCp.getCoverageItemsCovered().size());
      Assert.assertEquals(135, loadedCp.getCoverageItems().size());
      Assert.assertEquals(51, loadedCp.getCoveragePercent().intValue());
      Assert.assertEquals(0, loadedCp.getCoverageItemsCount(CoverageOptionManager.Deactivated_Code));
      Assert.assertEquals(0, loadedCp.getCoverageItemsCount(CoverageOptionManager.Exception_Handling));
      Assert.assertEquals(69, loadedCp.getCoverageItemsCount(CoverageOptionManager.Test_Unit));
      Assert.assertEquals(66, loadedCp.getCoverageItemsCount(CoverageOptionManager.Not_Covered));

      // Re-acquired coverage unit from loaded coverage package
      clearCoverageUnit = null;
      for (CoverageUnit childCoverageUnit : auxPowerUnit1CoverageUnit.getCoverageUnits()) {
         if (childCoverageUnit.getName().equals("clear")) {
            clearCoverageUnit = childCoverageUnit;
            break;
         }
      }

      // re-acquire coverage items from loaded coverage package
      Assert.assertNotNull(clearCoverageUnit);
      clearCoverageItemNum2 = null;
      clearCoverageItemNum3 = null;
      for (CoverageItem childCoverageUnit : clearCoverageUnit.getCoverageItems()) {
         if (childCoverageUnit.getName().contains("\"clear it now\"")) {
            clearCoverageItemNum2 = childCoverageUnit;
         }
         if (childCoverageUnit.getName().contains("\"clear it\"")) {
            clearCoverageItemNum3 = childCoverageUnit;
         }
      }

      // Ensure that the items were updated
      Assert.assertTrue(clearCoverageItemNum2.getName().contains("clear it now"));
      Assert.assertEquals(CoverageOptionManager.Test_Unit, clearCoverageItemNum2.getCoverageMethod());
      Assert.assertEquals(1, clearCoverageItemNum2.getTestUnits().size());
      Assert.assertEquals("TestUnit1", clearCoverageItemNum2.getTestUnits().iterator().next());

      Assert.assertTrue(clearCoverageItemNum3.getName().contains("clear it"));
      Assert.assertEquals(CoverageOptionManager.Not_Covered, clearCoverageItemNum3.getCoverageMethod());
      Assert.assertTrue("No test units should be stored", clearCoverageItemNum3.getTestUnits().isEmpty());

      // Ensure that file contents were updated
      auxPowerUnit1CoverageUnit =
         (CoverageUnit) CoverageTestUtil.getFirstCoverageByNameEquals(coveragePackage, "AuxPowerUnit1.java");
      Assert.assertNotNull(auxPowerUnit1CoverageUnit);
      String postLoadFileContents = auxPowerUnit1CoverageUnit.getFileContents();
      Assert.assertEquals("File Contents should be same pre and post save", postLoadFileContents, postFileContents);
      Assert.assertTrue("deselectAdded should exist in loaded file contents",
         postLoadFileContents.contains("clear it now"));

   }

   @Test
   @Ignore("Old import merge capabilities replaced.  These should be removed when that code is removed.")
   // Re-import; Deletes NavigationButton2.getText.line2
   public void testImport8() throws Exception {
      ICoverageImporter importer = CoverageExampleFactory.createExample(CoverageExamples.COVERAGE_IMPORT_08);
      coverageImport = importer.run(null);
      Assert.assertNotNull(coverageImport);

      if (testWithDb) {
         // Test Load of Coverage Package
         Artifact artifact =
            ArtifactQuery.getArtifactFromId(coveragePackage.getGuid(), CoverageTestUtil.getTestBranch());
         CoverageTestUtil.registerAsTestArtifact(artifact);
         artifact.persist(getComment(1));
         OseeCoveragePackageStore packageStore = new OseeCoveragePackageStore(artifact);
         Assert.assertNotNull(packageStore.getArtifact(false));
         coveragePackage = packageStore.getCoveragePackage();
      }

      // Get and store off coverage unit to delete so can confirm deletion occurred
      CoverageUnit navigateButton2 =
         (CoverageUnit) CoverageTestUtil.getFirstCoverageByNameEquals(coveragePackage, "NavigationButton2.java");
      Assert.assertNotNull(navigateButton2);
      CoverageUnit getTextCoverageUnit = null;
      for (CoverageUnit childCoverageUnit : navigateButton2.getCoverageUnits()) {
         if (childCoverageUnit.getName().equals("getText")) {
            getTextCoverageUnit = childCoverageUnit;
            break;
         }
      }

      Assert.assertNotNull(getTextCoverageUnit);
      boolean foundIt = false;
      for (CoverageItem childCoverageUnit : getTextCoverageUnit.getCoverageItems()) {
         if (childCoverageUnit.getName().contains("Navigate Here")) {
            foundIt = true;
            break;
         }
      }
      Assert.assertTrue("Should have found coverage item with \"Navigate Here\" string.", foundIt);

      String preFileContents = navigateButton2.getFileContents();
      Assert.assertTrue("\"Navigate Here\" should exist in file contents", preFileContents.contains("Navigate Here"));

      // Test MergeManager
      Assert.assertNotNull(coveragePackage);
      MergeManager mergeManager = new MergeManager(coveragePackage, coverageImport);
      Assert.assertEquals(1, mergeManager.getMergeItems(null).size());
      Assert.assertTrue(mergeManager.getMergeItems(null).iterator().next() instanceof MergeItemGroup);
      int numRename = 0, numDeleted = 0;
      for (IMergeItem mergeItem : ((MergeItemGroup) mergeManager.getMergeItems(null).iterator().next()).getMergeItems()) {
         if (mergeItem.getMergeType() == MergeType.CI_Renamed) {
            numRename++;
         } else if (mergeItem.getMergeType() == MergeType.CI_Delete) {
            numDeleted++;
         } else {
            throw new OseeStateException("Unexpected merge type [%s] for CI_Changes group", mergeItem.getMergeType());
         }
      }
      Assert.assertEquals(2, numRename);
      Assert.assertEquals(1, numDeleted);

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

      // CoveragePackage should now have imported results
      Assert.assertEquals(68, coveragePackage.getCoverageItemsCovered().size());
      Assert.assertEquals(134, coveragePackage.getCoverageItems().size());
      Assert.assertEquals(50, coveragePackage.getCoveragePercent().intValue());
      Assert.assertEquals(0, coveragePackage.getCoverageItemsCount(CoverageOptionManager.Deactivated_Code));
      Assert.assertEquals(0, coveragePackage.getCoverageItemsCount(CoverageOptionManager.Exception_Handling));
      Assert.assertEquals(68, coveragePackage.getCoverageItemsCount(CoverageOptionManager.Test_Unit));
      Assert.assertEquals(66, coveragePackage.getCoverageItemsCount(CoverageOptionManager.Not_Covered));

      CoveragePackage loadedCp = null;
      if (testWithDb) {
         // Test Persist of CoveragePackage
         OseeCoverageStore store = OseeCoveragePackageStore.get(coveragePackage, CoverageTestUtil.getTestBranch());
         SkynetTransaction transaction =
            TransactionManager.createTransaction(CoverageTestUtil.getTestBranch(),
               "Coverage Package Save " + testName.getMethodName());
         store.save(transaction, getTestCoveragePackageEvent(), coveragePackage.getCoverageOptionManager());
         store.getArtifact(false).persist(transaction);

         // Test Load of Coverage Package
         Artifact artifact =
            ArtifactQuery.getArtifactFromId(coveragePackage.getGuid(), CoverageTestUtil.getTestBranch());
         CoverageTestUtil.registerAsTestArtifact(artifact);
         artifact.persist(transaction);

         OseeCoveragePackageStore packageStore = new OseeCoveragePackageStore(artifact);
         Assert.assertNotNull(packageStore.getArtifact(false));
         loadedCp = packageStore.getCoveragePackage();
         transaction.execute();
      } else {
         loadedCp = coveragePackage;
      }

      // Confirm that fileContents were updated
      navigateButton2 =
         (CoverageUnit) CoverageTestUtil.getFirstCoverageByNameEquals(coveragePackage, "NavigationButton2.java");
      Assert.assertNotNull(navigateButton2);
      String postFileContents = navigateButton2.getFileContents();
      Assert.assertTrue("File Contents should been updated and thus not equal",
         !postFileContents.equals(preFileContents));
      Assert.assertFalse("\"Navigate Here\" should NOT exist in file contents",
         postFileContents.contains("Navigate Here"));

      Assert.assertEquals(68, loadedCp.getCoverageItemsCovered().size());
      Assert.assertEquals(134, loadedCp.getCoverageItems().size());
      Assert.assertEquals(50, loadedCp.getCoveragePercent().intValue());
      Assert.assertEquals(0, loadedCp.getCoverageItemsCount(CoverageOptionManager.Deactivated_Code));
      Assert.assertEquals(0, loadedCp.getCoverageItemsCount(CoverageOptionManager.Exception_Handling));
      Assert.assertEquals(68, loadedCp.getCoverageItemsCount(CoverageOptionManager.Test_Unit));
      Assert.assertEquals(66, loadedCp.getCoverageItemsCount(CoverageOptionManager.Not_Covered));

      // Ensure that file contents were updated
      navigateButton2 =
         (CoverageUnit) CoverageTestUtil.getFirstCoverageByNameEquals(coveragePackage, "NavigationButton2.java");
      Assert.assertNotNull(navigateButton2);
      String postLoadFileContents = navigateButton2.getFileContents();
      Assert.assertEquals("File Contents should be same pre and post save", postLoadFileContents, postFileContents);
      Assert.assertFalse("\"Navigate Here\" should NOT exist in loaded file contents",
         postLoadFileContents.contains("Navigate Here"));

   }

   @Test
   @Ignore("Old import merge capabilities replaced.  These should be removed when that code is removed.")
   // Re-import; Method update for NavigationButton2.getImage.line2 and line5
   public void testImport9() throws Exception {
      ICoverageImporter importer = CoverageExampleFactory.createExample(CoverageExamples.COVERAGE_IMPORT_09);
      coverageImport = importer.run(null);
      Assert.assertNotNull(coverageImport);

      if (testWithDb) {
         // Test Load of Coverage Package
         Artifact artifact =
            ArtifactQuery.getArtifactFromId(coveragePackage.getGuid(), CoverageTestUtil.getTestBranch());
         CoverageTestUtil.registerAsTestArtifact(artifact);
         artifact.persist(getComment(1));
         OseeCoveragePackageStore packageStore = new OseeCoveragePackageStore(artifact);
         Assert.assertNotNull(packageStore.getArtifact(false));
         coveragePackage = packageStore.getCoveragePackage();
      }

      CoverageUnit navigationButton2Unit =
         (CoverageUnit) CoverageTestUtil.getFirstCoverageByNameEquals(coveragePackage, "NavigationButton2.java");
      Assert.assertNotNull(navigationButton2Unit);
      CoverageUnit getImageItem =
         CoverageTestUtil.getFirstCoverageUnitByNameContains(navigationButton2Unit, "getImage");
      Assert.assertNotNull(getImageItem);
      // Confirm line2 has no test units associated
      CoverageItem line2 = CoverageTestUtil.getFirstCoverageItemByNameContains(getImageItem, "return this.image");
      Assert.assertNotNull(line2);
      Assert.assertEquals(CoverageOptionManager.Not_Covered, line2.getCoverageMethod());
      Assert.assertEquals(0, line2.getTestUnits().size());

      // Confirm line4 has no test units associated
      CoverageItem line4 = CoverageTestUtil.getFirstCoverageItemByNameContains(getImageItem, "return null;");
      Assert.assertNotNull(line4);
      Assert.assertEquals(CoverageOptionManager.Not_Covered, line4.getCoverageMethod());
      Assert.assertEquals(0, line4.getTestUnits().size());

      // Test MergeManager
      Assert.assertNotNull(coveragePackage);
      MergeManager mergeManager = new MergeManager(coveragePackage, coverageImport);
      Assert.assertEquals(1, mergeManager.getMergeItems(null).size());
      Assert.assertTrue(mergeManager.getMergeItems(null).iterator().next() instanceof MergeItemGroup);
      int numUpdateMethod = 0;
      for (IMergeItem mergeItem : ((MergeItemGroup) mergeManager.getMergeItems(null).iterator().next()).getMergeItems()) {
         if (mergeItem.getMergeType() == MergeType.CI_Method_Update) {
            numUpdateMethod++;
         } else {
            throw new OseeStateException("Unexpected merge type [%s] for CI_Changes group", mergeItem.getMergeType());
         }
      }
      Assert.assertEquals(2, numUpdateMethod);

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

      // CoveragePackage should now have imported results
      Assert.assertEquals(70, coveragePackage.getCoverageItemsCovered().size());
      Assert.assertEquals(134, coveragePackage.getCoverageItems().size());
      Assert.assertEquals(52, coveragePackage.getCoveragePercent().intValue());
      Assert.assertEquals(0, coveragePackage.getCoverageItemsCount(CoverageOptionManager.Deactivated_Code));
      Assert.assertEquals(0, coveragePackage.getCoverageItemsCount(CoverageOptionManager.Exception_Handling));
      Assert.assertEquals(70, coveragePackage.getCoverageItemsCount(CoverageOptionManager.Test_Unit));
      Assert.assertEquals(64, coveragePackage.getCoverageItemsCount(CoverageOptionManager.Not_Covered));

      CoveragePackage loadedCp = null;
      if (testWithDb) {
         // Test Persist of CoveragePackage
         OseeCoverageStore store = OseeCoveragePackageStore.get(coveragePackage, CoverageTestUtil.getTestBranch());
         SkynetTransaction transaction =
            TransactionManager.createTransaction(CoverageTestUtil.getTestBranch(),
               "Coverage Package Save " + testName.getMethodName());
         store.save(transaction, getTestCoveragePackageEvent(), coveragePackage.getCoverageOptionManager());
         store.getArtifact(false).persist(transaction);

         // Test Load of Coverage Package
         Artifact artifact =
            ArtifactQuery.getArtifactFromId(coveragePackage.getGuid(), CoverageTestUtil.getTestBranch());
         CoverageTestUtil.registerAsTestArtifact(artifact);
         artifact.persist(transaction);

         OseeCoveragePackageStore packageStore = new OseeCoveragePackageStore(artifact);
         Assert.assertNotNull(packageStore.getArtifact(false));
         loadedCp = packageStore.getCoveragePackage();

         transaction.execute();
      } else {
         loadedCp = coveragePackage;
      }

      Assert.assertEquals(70, loadedCp.getCoverageItemsCovered().size());
      Assert.assertEquals(134, loadedCp.getCoverageItems().size());
      Assert.assertEquals(52, loadedCp.getCoveragePercent().intValue());
      Assert.assertEquals(0, loadedCp.getCoverageItemsCount(CoverageOptionManager.Deactivated_Code));
      Assert.assertEquals(0, loadedCp.getCoverageItemsCount(CoverageOptionManager.Exception_Handling));
      Assert.assertEquals(70, loadedCp.getCoverageItemsCount(CoverageOptionManager.Test_Unit));
      Assert.assertEquals(64, loadedCp.getCoverageItemsCount(CoverageOptionManager.Not_Covered));

      // re-acquire coverage unit from loaded coverage package
      navigationButton2Unit =
         (CoverageUnit) CoverageTestUtil.getFirstCoverageByNameEquals(loadedCp, "NavigationButton2.java");
      Assert.assertNotNull(navigationButton2Unit);
      getImageItem = CoverageTestUtil.getFirstCoverageUnitByNameContains(navigationButton2Unit, "getImage");
      Assert.assertNotNull(getImageItem);

      // Confirm line2 is now TestUnit2
      line2 = CoverageTestUtil.getFirstCoverageItemByNameContains(getImageItem, "return this.image");
      Assert.assertNotNull(line2);
      Assert.assertEquals(CoverageOptionManager.Test_Unit, line2.getCoverageMethod());
      Assert.assertEquals(1, line2.getTestUnits().size());
      Assert.assertEquals("TestUnit2", line2.getTestUnits().iterator().next());

      // Confirm line4 is now TestUnit2
      line4 = CoverageTestUtil.getFirstCoverageItemByNameContains(getImageItem, "return null;");
      Assert.assertNotNull(line4);
      Assert.assertEquals(CoverageOptionManager.Test_Unit, line4.getCoverageMethod());
      Assert.assertEquals(1, line4.getTestUnits().size());
      Assert.assertEquals("TestUnit2", line2.getTestUnits().iterator().next());

   }

   @Test
   @Ignore("Old import merge capabilities replaced.  These should be removed when that code is removed.")
   // Re-import; Test Improvement to resolving coverage method differences
   public void testImport10() throws Exception {
      ICoverageImporter importer = CoverageExampleFactory.createExample(CoverageExamples.COVERAGE_IMPORT_10);
      coverageImport = importer.run(null);
      Assert.assertNotNull(coverageImport);

      if (testWithDb) {
         // Test Load of Coverage Package
         Artifact artifact =
            ArtifactQuery.getArtifactFromId(coveragePackage.getGuid(), CoverageTestUtil.getTestBranch());
         CoverageTestUtil.registerAsTestArtifact(artifact);
         artifact.persist(getComment(1));
         OseeCoveragePackageStore packageStore = new OseeCoveragePackageStore(artifact);
         Assert.assertNotNull(packageStore.getArtifact(false));
         coveragePackage = packageStore.getCoveragePackage();
      }

      // Change NavigateButton1.java methods to insert user disposition item as Coverage_Method for 2 items
      Result result = setupCoveragePackageForImport10(coveragePackage);
      Assert.assertTrue(result.getText(), result.isTrue());

      // reload coverage package from store
      if (testWithDb) {
         SkynetTransaction transaction =
            TransactionManager.createTransaction(CoverageTestUtil.getTestBranch(),
               String.format("%s.%s", CoveragePackageImportTest.class.getSimpleName(), testName.getMethodName()));
         // Test Load of Coverage Package
         Artifact artifact =
            ArtifactQuery.getArtifactFromId(coveragePackage.getGuid(), CoverageTestUtil.getTestBranch());
         CoverageTestUtil.registerAsTestArtifact(artifact);
         artifact.persist(transaction);
         OseeCoveragePackageStore packageStore = new OseeCoveragePackageStore(artifact);
         Assert.assertNotNull(packageStore.getArtifact(false));
         coveragePackage = packageStore.getCoveragePackage();
         transaction.execute();
      }

      // Confirm that programatic changes were recorded
      // Two items were changed to Deactivated, one was not covered, other test covered, no net gain of 1 item covered
      Assert.assertEquals(71, coveragePackage.getCoverageItemsCovered().size());
      Assert.assertEquals(134, coveragePackage.getCoverageItems().size());
      Assert.assertEquals(52, coveragePackage.getCoveragePercent().intValue());
      Assert.assertEquals(2, coveragePackage.getCoverageItemsCount(CoverageOptionManager.Deactivated_Code));
      Assert.assertEquals(0, coveragePackage.getCoverageItemsCount(CoverageOptionManager.Exception_Handling));
      Assert.assertEquals(69, coveragePackage.getCoverageItemsCount(CoverageOptionManager.Test_Unit));
      Assert.assertEquals(63, coveragePackage.getCoverageItemsCount(CoverageOptionManager.Not_Covered));

      CoverageItem item = getNavigateButton2getImageLine3CoverageItem(coveragePackage);
      Assert.assertEquals("This is the rationale", item.getRationale());

      // Test MergeManager
      Assert.assertNotNull(coveragePackage);
      MergeManager mergeManager = new MergeManager(coveragePackage, coverageImport);
      Assert.assertEquals(1, mergeManager.getMergeItems(null).size());
      Assert.assertTrue(mergeManager.getMergeItems(null).iterator().next() instanceof MergeItemGroup);
      int numUpdateMethod = 0;
      for (IMergeItem mergeItem : ((MergeItemGroup) mergeManager.getMergeItems(null).iterator().next()).getMergeItems()) {
         if (mergeItem.getMergeType() == MergeType.CI_Method_Update) {
            numUpdateMethod++;
         } else {
            throw new OseeStateException("Unexpected merge type [%s] for CI_Changes group", mergeItem.getMergeType());
         }
      }
      Assert.assertEquals(2, numUpdateMethod);

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

      // CoveragePackage should now have imported results
      Assert.assertEquals(70, coveragePackage.getCoverageItemsCovered().size());
      Assert.assertEquals(134, coveragePackage.getCoverageItems().size());
      Assert.assertEquals(52, coveragePackage.getCoveragePercent().intValue());
      Assert.assertEquals(1, coveragePackage.getCoverageItemsCount(CoverageOptionManager.Deactivated_Code));
      Assert.assertEquals(0, coveragePackage.getCoverageItemsCount(CoverageOptionManager.Exception_Handling));
      Assert.assertEquals(69, coveragePackage.getCoverageItemsCount(CoverageOptionManager.Test_Unit));
      Assert.assertEquals(64, coveragePackage.getCoverageItemsCount(CoverageOptionManager.Not_Covered));

      // Rationale should have been cleared
      CoverageItem item1 = getNavigateButton2getImageLine3CoverageItem(coveragePackage);
      Assert.assertEquals("", item1.getRationale());

      CoveragePackage loadedCp = null;
      if (testWithDb) {
         // Test Persist of CoveragePackage
         OseeCoverageStore store = OseeCoveragePackageStore.get(coveragePackage, CoverageTestUtil.getTestBranch());
         SkynetTransaction transaction =
            TransactionManager.createTransaction(CoverageTestUtil.getTestBranch(),
               "Coverage Package Save " + testName.getMethodName());
         store.save(transaction, getTestCoveragePackageEvent(), coveragePackage.getCoverageOptionManager());
         store.getArtifact(false).persist(transaction);

         // Test Load of Coverage Package
         Artifact artifact =
            ArtifactQuery.getArtifactFromId(coveragePackage.getGuid(), CoverageTestUtil.getTestBranch());
         CoverageTestUtil.registerAsTestArtifact(artifact);
         artifact.persist(transaction);

         OseeCoveragePackageStore packageStore = new OseeCoveragePackageStore(artifact);
         Assert.assertNotNull(packageStore.getArtifact(false));
         loadedCp = packageStore.getCoveragePackage();

         transaction.execute();
      } else {
         loadedCp = coveragePackage;
      }

      Assert.assertEquals(70, loadedCp.getCoverageItemsCovered().size());
      Assert.assertEquals(134, loadedCp.getCoverageItems().size());
      Assert.assertEquals(52, loadedCp.getCoveragePercent().intValue());
      Assert.assertEquals(1, loadedCp.getCoverageItemsCount(CoverageOptionManager.Deactivated_Code));
      Assert.assertEquals(0, loadedCp.getCoverageItemsCount(CoverageOptionManager.Exception_Handling));
      Assert.assertEquals(69, loadedCp.getCoverageItemsCount(CoverageOptionManager.Test_Unit));
      Assert.assertEquals(64, loadedCp.getCoverageItemsCount(CoverageOptionManager.Not_Covered));

      // Rationale should have been cleared
      CoverageItem item2 = getNavigateButton2getImageLine3CoverageItem(loadedCp);
      Assert.assertEquals("", item2.getRationale());
   }

   public static CoverageItem getNavigateButton2getTextLine3CoverageItem(CoveragePackage coveragePackage) {
      ICoverage coverage = CoverageTestUtil.getFirstCoverageByNameEquals(coveragePackage, "NavigationButton2.java");
      CoverageUnit coverageUnit = (CoverageUnit) coverage;
      for (CoverageUnit childCU : coverageUnit.getCoverageUnits()) {
         if (childCU.getName().equals("getText")) {
            for (CoverageItem item : childCU.getCoverageItems()) {
               if (item.getOrderNumber().equals("3")) {
                  return item;
               }
            }
         }
      }
      return null;
   }

   public static CoverageItem getNavigateButton2getImageLine3CoverageItem(CoveragePackage coveragePackage) {
      ICoverage coverage = CoverageTestUtil.getFirstCoverageByNameEquals(coveragePackage, "NavigationButton2.java");
      CoverageUnit coverageUnit = (CoverageUnit) coverage;
      for (CoverageUnit childCU : coverageUnit.getCoverageUnits()) {
         if (childCU.getName().equals("getImage")) {
            for (CoverageItem item : childCU.getCoverageItems()) {
               if (item.getOrderNumber().equals("3")) {
                  return item;
               }
            }
         }
      }
      return null;
   }

   /**
    * Setup test by changing two lines in NavigateButton2.java to be user dispositioned and getText line 3 rationale
    */
   public static Result setupCoveragePackageForImport10(CoveragePackage coveragePackage) {
      String errStr = null;

      CoverageItem item = getNavigateButton2getTextLine3CoverageItem(coveragePackage);
      if (item == null) {
         errStr = "NavigationButton.java/getText/line 3 not found\n";
      } else {
         item.setCoverageMethod(CoverageOptionManager.Deactivated_Code);
      }

      item = getNavigateButton2getImageLine3CoverageItem(coveragePackage);
      if (item == null) {
         errStr += "NavigationButton.java/getImage/line 3 not found";
      } else {
         item.setCoverageMethod(CoverageOptionManager.Deactivated_Code);
         item.setRationale("This is the rationale");
      }

      if (Strings.isValid(errStr)) {
         return new Result(errStr);
      }

      if (testWithDb) {
         OseeCoveragePackageStore packageStore =
            new OseeCoveragePackageStore(coveragePackage, CoverageTestUtil.getTestBranch());
         packageStore.save(coveragePackage.getName(), coveragePackage.getCoverageOptionManager());
      }

      return Result.TrueResult;
   }

   private CoveragePackageEvent getTestCoveragePackageEvent() {
      return new CoveragePackageEvent(coveragePackage, CoverageEventType.Modified);
   }
}
