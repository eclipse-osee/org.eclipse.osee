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
package org.eclipse.osee.coverage.test;

import static org.eclipse.osee.framework.skynet.core.artifact.DeletionFlag.INCLUDE_DELETED;
import java.util.Collection;
import junit.framework.Assert;
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
import org.eclipse.osee.coverage.store.OseeCoveragePackageStore;
import org.eclipse.osee.coverage.store.OseeCoverageStore;
import org.eclipse.osee.coverage.test.import1.CoverageImport1TestBlam;
import org.eclipse.osee.coverage.test.import2.CoverageImport2TestBlam;
import org.eclipse.osee.coverage.test.import3.CoverageImport3TestBlam;
import org.eclipse.osee.coverage.test.import4.CoverageImport4TestBlam;
import org.eclipse.osee.coverage.test.import5.CoverageImport5TestBlam;
import org.eclipse.osee.coverage.test.import6.CoverageImport6TestBlam;
import org.eclipse.osee.coverage.test.import7.CoverageImport7TestBlam;
import org.eclipse.osee.coverage.test.import8.CoverageImport8TestBlam;
import org.eclipse.osee.coverage.test.import9.CoverageImport9TestBlam;
import org.eclipse.osee.coverage.test.util.CoverageTestUtil;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.coverage.util.ISaveable;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class CoveragePackageImportTest {

   public static CoveragePackage coveragePackage = null;
   public static CoverageImport coverageImport = null;
   private static boolean testWithDb = true;
   private static Branch commonBranch = null;

   @BeforeClass
   public static void setUp() throws OseeCoreException {
      CoverageUtil.setIsInTest(true);
      commonBranch = BranchManager.getCommonBranch();
      testCleanup();
   }

   @AfterClass
   public static void cleanUp() throws OseeCoreException {
      CoverageUtil.setIsInTest(false);
      testCleanup();
   }

   public static void testCleanup() throws OseeCoreException {
      if (testWithDb) {
         CoverageUtil.setNavigatorSelectedBranch(commonBranch);
         CoverageTestUtil.cleanupCoverageTests();
      } else {
         System.err.println("Test with Db Disabled...re-inenable");
      }
   }

   @Test
   public void testImport1() throws Exception {
      CoverageImport1TestBlam coverageImport1TestBlam = new CoverageImport1TestBlam();
      coverageImport = coverageImport1TestBlam.run(null);
      Assert.assertNotNull(coverageImport);

      // Check import results
      Assert.assertEquals(60, coverageImport.getCoverageItemsCovered().size());
      Assert.assertEquals(122, coverageImport.getCoverageItems().size());

      System.out.println(CoverageUtil.printTree(coverageImport));

      Assert.assertEquals(49, coverageImport.getCoveragePercent().intValue());
      Assert.assertEquals(0, coverageImport.getCoverageItemsCovered(CoverageOptionManager.Deactivated_Code).size());
      Assert.assertEquals(0, coverageImport.getCoverageItemsCovered(CoverageOptionManager.Exception_Handling).size());
      Assert.assertEquals(60, coverageImport.getCoverageItemsCovered(CoverageOptionManager.Test_Unit).size());
      Assert.assertEquals(62, coverageImport.getCoverageItemsCovered(CoverageOptionManager.Not_Covered).size());

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
      coveragePackage = new CoveragePackage("Test Coverage Package", CoverageOptionManagerDefault.instance());
      MergeManager mergeManager = new MergeManager(coveragePackage, coverageImport);
      Assert.assertEquals(4, mergeManager.getMergeItems().size());
      for (IMergeItem mergeItem : mergeManager.getMergeItems()) {
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
         public Result save() throws OseeCoreException {
            return Result.TrueResult;
         }

         @Override
         public Result save(Collection<ICoverage> coverages) throws OseeCoreException {
            return Result.TrueResult;
         }

         @Override
         public Result saveImportRecord(SkynetTransaction transaction, CoverageImport coverageImport) throws OseeCoreException {
            return Result.TrueResult;
         }

         @Override
         public Branch getBranch() throws OseeCoreException {
            return commonBranch;
         }

      }, mergeManager.getMergeItems());
      Assert.assertEquals(1, resultData.getNumErrors());

      resultData = importManager.importItems(new ISaveable() {

         @Override
         public Result isEditable() {
            return Result.TrueResult;
         }

         @Override
         public Result save() throws OseeCoreException {
            return Result.TrueResult;
         }

         @Override
         public Result save(Collection<ICoverage> coverages) throws OseeCoreException {
            return Result.TrueResult;
         }

         @Override
         public Result saveImportRecord(SkynetTransaction transaction, CoverageImport coverageImport) throws OseeCoreException {
            return Result.TrueResult;
         }

         @Override
         public Branch getBranch() throws OseeCoreException {
            return commonBranch;
         }

      }, mergeManager.getMergeItems());
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
      System.out.println(CoverageUtil.printTree(coveragePackage));

      // CoveragePackage should now have imported results
      Assert.assertEquals(60, coveragePackage.getCoverageItemsCovered().size());
      Assert.assertEquals(122, coveragePackage.getCoverageItems().size());
      Assert.assertEquals(49, coveragePackage.getCoveragePercent().intValue());
      Assert.assertEquals(0, coveragePackage.getCoverageItemsCovered(CoverageOptionManager.Deactivated_Code).size());
      Assert.assertEquals(0, coveragePackage.getCoverageItemsCovered(CoverageOptionManager.Exception_Handling).size());
      Assert.assertEquals(60, coveragePackage.getCoverageItemsCovered(CoverageOptionManager.Test_Unit).size());
      Assert.assertEquals(62, coveragePackage.getCoverageItemsCovered(CoverageOptionManager.Not_Covered).size());

      CoveragePackage loadedCp = null;
      if (testWithDb) {
         // Test Persist of CoveragePackage
         OseeCoverageStore store = OseeCoveragePackageStore.get(coveragePackage, commonBranch);
         SkynetTransaction transaction = new SkynetTransaction(commonBranch, "Coverage Package Save");
         store.save(transaction);
         transaction.execute();

         // Test Load of Coverage Package
         Artifact artifact = ArtifactQuery.getArtifactFromId(coveragePackage.getGuid(), commonBranch);
         CoverageTestUtil.registerAsTestArtifact(artifact);
         OseeSystemArtifacts.getDefaultHierarchyRootArtifact(artifact.getBranch()).addChild(artifact);
         artifact.persist();

         OseeCoveragePackageStore packageStore = new OseeCoveragePackageStore(artifact);
         Assert.assertNotNull(packageStore.getArtifact(false));
         loadedCp = packageStore.getCoveragePackage();
      } else {
         loadedCp = coveragePackage;
      }

      Assert.assertEquals(60, loadedCp.getCoverageItemsCovered().size());
      Assert.assertEquals(122, loadedCp.getCoverageItems().size());
      Assert.assertEquals(49, loadedCp.getCoveragePercent().intValue());
      Assert.assertEquals(0, loadedCp.getCoverageItemsCovered(CoverageOptionManager.Deactivated_Code).size());
      Assert.assertEquals(0, loadedCp.getCoverageItemsCovered(CoverageOptionManager.Exception_Handling).size());
      Assert.assertEquals(60, loadedCp.getCoverageItemsCovered(CoverageOptionManager.Test_Unit).size());
      Assert.assertEquals(62, loadedCp.getCoverageItemsCovered(CoverageOptionManager.Not_Covered).size());

   }

   @Test
   // Re-import with no changes, make sure no merge items exist
   public void testImport1B() throws Exception {
      CoverageImport1TestBlam coverageImport1TestBlam = new CoverageImport1TestBlam();
      coverageImport = coverageImport1TestBlam.run(null);
      Assert.assertNotNull(coverageImport);

      // Check import results
      Assert.assertEquals(60, coverageImport.getCoverageItemsCovered().size());
      Assert.assertEquals(122, coverageImport.getCoverageItems().size());
      Assert.assertEquals(49, coverageImport.getCoveragePercent().intValue());
      Assert.assertEquals(0, coverageImport.getCoverageItemsCovered(CoverageOptionManager.Deactivated_Code).size());
      Assert.assertEquals(0, coverageImport.getCoverageItemsCovered(CoverageOptionManager.Exception_Handling).size());
      Assert.assertEquals(60, coverageImport.getCoverageItemsCovered(CoverageOptionManager.Test_Unit).size());
      Assert.assertEquals(62, coverageImport.getCoverageItemsCovered(CoverageOptionManager.Not_Covered).size());

      // Test MergeManager
      Assert.assertNotNull(coveragePackage);
      MergeManager mergeManager = new MergeManager(coveragePackage, coverageImport);
      Assert.assertEquals(1, mergeManager.getMergeItems().size());
      // Merge item will be the "Nothing to Import" message item
      Assert.assertTrue(mergeManager.getMergeItems().iterator().next() instanceof MessageMergeItem);
   }

   @Test
   // Re-import two new Coverage Unit files 
   // com.screenA.ComScrnButton3 and epu.PowerUnit3
   public void testImport2() throws Exception {
      CoverageImport2TestBlam coverageImport2TestBlam = new CoverageImport2TestBlam();
      coverageImport = coverageImport2TestBlam.run(null);
      Assert.assertNotNull(coverageImport);

      // Test MergeManager
      Assert.assertNotNull(coveragePackage);
      MergeManager mergeManager = new MergeManager(coveragePackage, coverageImport);
      Assert.assertEquals(2, mergeManager.getMergeItems().size());
      for (IMergeItem mergeItem : mergeManager.getMergeItems()) {
         Assert.assertEquals(MergeType.Add, mergeItem.getMergeType());
      }
      MergeImportManager importManager = new MergeImportManager(mergeManager);
      XResultData resultData = importManager.importItems(new ISaveable() {

         @Override
         public Result isEditable() {
            return Result.TrueResult;
         }

         @Override
         public Result save() throws OseeCoreException {
            return Result.TrueResult;
         }

         @Override
         public Result save(Collection<ICoverage> coverages) throws OseeCoreException {
            return Result.TrueResult;
         }

         @Override
         public Result saveImportRecord(SkynetTransaction transaction, CoverageImport coverageImport) throws OseeCoreException {
            return Result.TrueResult;
         }

         @Override
         public Branch getBranch() throws OseeCoreException {
            return commonBranch;
         }

      }, mergeManager.getMergeItems());
      Assert.assertEquals(0, resultData.getNumErrors());

      // CoveragePackage should now have imported results
      Assert.assertEquals(66, coveragePackage.getCoverageItemsCovered().size());
      Assert.assertEquals(133, coveragePackage.getCoverageItems().size());
      Assert.assertEquals(49, coveragePackage.getCoveragePercent().intValue());
      Assert.assertEquals(0, coveragePackage.getCoverageItemsCovered(CoverageOptionManager.Deactivated_Code).size());
      Assert.assertEquals(0, coveragePackage.getCoverageItemsCovered(CoverageOptionManager.Exception_Handling).size());
      Assert.assertEquals(66, coveragePackage.getCoverageItemsCovered(CoverageOptionManager.Test_Unit).size());
      Assert.assertEquals(67, coveragePackage.getCoverageItemsCovered(CoverageOptionManager.Not_Covered).size());

      CoveragePackage loadedCp = null;
      if (testWithDb) {
         // Test Persist of CoveragePackage
         OseeCoverageStore store = OseeCoveragePackageStore.get(coveragePackage, commonBranch);
         SkynetTransaction transaction = new SkynetTransaction(commonBranch, "Coverage Package Save");
         store.save(transaction);
         transaction.execute();

         // Test Load of Coverage Package
         Artifact artifact = ArtifactQuery.getArtifactFromId(coveragePackage.getGuid(), commonBranch);
         CoverageTestUtil.registerAsTestArtifact(artifact);
         artifact.persist();

         OseeCoveragePackageStore packageStore = new OseeCoveragePackageStore(artifact);
         Assert.assertNotNull(packageStore.getArtifact(false));
         loadedCp = packageStore.getCoveragePackage();
      } else {
         loadedCp = coveragePackage;
      }

      Assert.assertEquals(66, loadedCp.getCoverageItemsCovered().size());
      Assert.assertEquals(133, loadedCp.getCoverageItems().size());
      Assert.assertEquals(49, loadedCp.getCoveragePercent().intValue());
      Assert.assertEquals(0, loadedCp.getCoverageItemsCovered(CoverageOptionManager.Deactivated_Code).size());
      Assert.assertEquals(0, loadedCp.getCoverageItemsCovered(CoverageOptionManager.Exception_Handling).size());
      Assert.assertEquals(66, loadedCp.getCoverageItemsCovered(CoverageOptionManager.Test_Unit).size());
      Assert.assertEquals(67, loadedCp.getCoverageItemsCovered(CoverageOptionManager.Not_Covered).size());

   }

   @Test
   // Re-import with new CoverageUnit method initAdded() at end of epu.PowerUnit1.java
   public void testImport3() throws Exception {
      CoverageImport3TestBlam coverageImport3TestBlam = new CoverageImport3TestBlam();
      coverageImport = coverageImport3TestBlam.run(null);
      Assert.assertNotNull(coverageImport);

      if (testWithDb) {
         // Test Load of Coverage Package
         Artifact artifact = ArtifactQuery.getArtifactFromId(coveragePackage.getGuid(), commonBranch);
         CoverageTestUtil.registerAsTestArtifact(artifact);
         artifact.persist();
         OseeCoveragePackageStore packageStore = new OseeCoveragePackageStore(artifact);
         Assert.assertNotNull(packageStore.getArtifact(false));
         coveragePackage = packageStore.getCoveragePackage();
      }

      // Look at file contents for PowerUnit1.java
      CoverageUnit coverageUnit =
            (CoverageUnit) CoverageTestUtil.getFirstCoverageByName(coveragePackage, "PowerUnit1.java");
      Assert.assertNotNull(coverageUnit);
      String preFileContents = coverageUnit.getFileContents();
      Assert.assertFalse("initAdded should not yet exist in file contents", preFileContents.contains("initAdded"));

      // Test MergeManager
      Assert.assertNotNull(coveragePackage);
      MergeManager mergeManager = new MergeManager(coveragePackage, coverageImport);
      Assert.assertEquals(1, mergeManager.getMergeItems().size());
      for (IMergeItem mergeItem : mergeManager.getMergeItems()) {
         Assert.assertEquals(MergeType.Add, mergeItem.getMergeType());
      }
      MergeImportManager importManager = new MergeImportManager(mergeManager);
      XResultData resultData = importManager.importItems(new ISaveable() {

         @Override
         public Result isEditable() {
            return Result.TrueResult;
         }

         @Override
         public Result save() throws OseeCoreException {
            return Result.TrueResult;
         }

         @Override
         public Result save(Collection<ICoverage> coverages) throws OseeCoreException {
            return Result.TrueResult;
         }

         @Override
         public Result saveImportRecord(SkynetTransaction transaction, CoverageImport coverageImport) throws OseeCoreException {
            return Result.TrueResult;
         }

         @Override
         public Branch getBranch() throws OseeCoreException {
            return commonBranch;
         }

      }, mergeManager.getMergeItems());
      Assert.assertEquals(0, resultData.getNumErrors());

      // CoveragePackage should now have imported results
      Assert.assertEquals(67, coveragePackage.getCoverageItemsCovered().size());
      Assert.assertEquals(134, coveragePackage.getCoverageItems().size());
      Assert.assertEquals(50, coveragePackage.getCoveragePercent().intValue());
      Assert.assertEquals(0, coveragePackage.getCoverageItemsCovered(CoverageOptionManager.Deactivated_Code).size());
      Assert.assertEquals(0, coveragePackage.getCoverageItemsCovered(CoverageOptionManager.Exception_Handling).size());
      Assert.assertEquals(67, coveragePackage.getCoverageItemsCovered(CoverageOptionManager.Test_Unit).size());
      Assert.assertEquals(67, coveragePackage.getCoverageItemsCovered(CoverageOptionManager.Not_Covered).size());

      // Confirm that fileContents were updated
      coverageUnit = (CoverageUnit) CoverageTestUtil.getFirstCoverageByName(coveragePackage, "PowerUnit1.java");
      Assert.assertNotNull(coverageUnit);
      String postFileContents = coverageUnit.getFileContents();
      Assert.assertTrue("File Contents should have been updated and thus not equal",
            !postFileContents.equals(preFileContents));
      Assert.assertTrue("initAdded should now exist in file contents", postFileContents.contains("initAdded"));

      CoveragePackage loadedCp = null;
      if (testWithDb) {
         // Test Persist of CoveragePackage
         OseeCoverageStore store = OseeCoveragePackageStore.get(coveragePackage, commonBranch);
         SkynetTransaction transaction = new SkynetTransaction(commonBranch, "Coverage Package Save");
         store.save(transaction);
         transaction.execute();

         // Test Load of Coverage Package
         Artifact artifact = ArtifactQuery.getArtifactFromId(coveragePackage.getGuid(), commonBranch);
         CoverageTestUtil.registerAsTestArtifact(artifact);
         artifact.persist();

         OseeCoveragePackageStore packageStore = new OseeCoveragePackageStore(artifact);
         Assert.assertNotNull(packageStore.getArtifact(false));
         loadedCp = packageStore.getCoveragePackage();
      } else {
         loadedCp = coveragePackage;
      }

      Assert.assertEquals(67, loadedCp.getCoverageItemsCovered().size());
      Assert.assertEquals(134, loadedCp.getCoverageItems().size());
      Assert.assertEquals(50, loadedCp.getCoveragePercent().intValue());
      Assert.assertEquals(0, loadedCp.getCoverageItemsCovered(CoverageOptionManager.Deactivated_Code).size());
      Assert.assertEquals(0, loadedCp.getCoverageItemsCovered(CoverageOptionManager.Exception_Handling).size());
      Assert.assertEquals(67, loadedCp.getCoverageItemsCovered(CoverageOptionManager.Test_Unit).size());
      Assert.assertEquals(67, loadedCp.getCoverageItemsCovered(CoverageOptionManager.Not_Covered).size());

      // Confirm that updated fileContents were loaded
      coverageUnit = (CoverageUnit) CoverageTestUtil.getFirstCoverageByName(coveragePackage, "PowerUnit1.java");
      Assert.assertNotNull(coverageUnit);
      String postLoadFileContents = coverageUnit.getFileContents();
      Assert.assertEquals("File Contents should be same pre and post save", postLoadFileContents, postFileContents);
      Assert.assertTrue("initAdded should exist in loaded file contents", postLoadFileContents.contains("initAdded"));
   }

   @Test
   // Re-import with deselectAdded method added to middle of epu.PowerUnit1
   public void testImport4() throws Exception {
      CoverageImport4TestBlam coverageImport4TestBlam = new CoverageImport4TestBlam();
      coverageImport = coverageImport4TestBlam.run(null);
      Assert.assertNotNull(coverageImport);

      if (testWithDb) {
         // Test Load of Coverage Package
         Artifact artifact = ArtifactQuery.getArtifactFromId(coveragePackage.getGuid(), commonBranch);
         CoverageTestUtil.registerAsTestArtifact(artifact);
         artifact.persist();
         OseeCoveragePackageStore packageStore = new OseeCoveragePackageStore(artifact);
         Assert.assertNotNull(packageStore.getArtifact(false));
         coveragePackage = packageStore.getCoveragePackage();
      }

      // Look at file contents for PowerUnit1.java
      CoverageUnit coverageUnit =
            (CoverageUnit) CoverageTestUtil.getFirstCoverageByName(coveragePackage, "PowerUnit1.java");
      Assert.assertNotNull(coverageUnit);
      String preFileContents = coverageUnit.getFileContents();
      Assert.assertFalse("deselectAdded should not yet exist in file contents",
            preFileContents.contains("deselectAdded"));

      // Test MergeManager
      Assert.assertNotNull(coveragePackage);
      MergeManager mergeManager = new MergeManager(coveragePackage, coverageImport);
      Assert.assertEquals(1, mergeManager.getMergeItems().size());
      for (IMergeItem mergeItem : mergeManager.getMergeItems()) {
         Assert.assertEquals(MergeType.Add_With_Moves, mergeItem.getMergeType());
      }
      MergeImportManager importManager = new MergeImportManager(mergeManager);
      XResultData resultData = importManager.importItems(new ISaveable() {

         @Override
         public Result isEditable() {
            return Result.TrueResult;
         }

         @Override
         public Result save() throws OseeCoreException {
            return Result.TrueResult;
         }

         @Override
         public Result save(Collection<ICoverage> coverages) throws OseeCoreException {
            return Result.TrueResult;
         }

         @Override
         public Result saveImportRecord(SkynetTransaction transaction, CoverageImport coverageImport) throws OseeCoreException {
            return Result.TrueResult;
         }

         @Override
         public Branch getBranch() throws OseeCoreException {
            return commonBranch;
         }

      }, mergeManager.getMergeItems());
      Assert.assertEquals(0, resultData.getNumErrors());

      // CoveragePackage should now have imported results
      Assert.assertEquals(68, coveragePackage.getCoverageItemsCovered().size());
      Assert.assertEquals(135, coveragePackage.getCoverageItems().size());
      Assert.assertEquals(50, coveragePackage.getCoveragePercent().intValue());
      Assert.assertEquals(0, coveragePackage.getCoverageItemsCovered(CoverageOptionManager.Deactivated_Code).size());
      Assert.assertEquals(0, coveragePackage.getCoverageItemsCovered(CoverageOptionManager.Exception_Handling).size());
      Assert.assertEquals(68, coveragePackage.getCoverageItemsCovered(CoverageOptionManager.Test_Unit).size());
      Assert.assertEquals(67, coveragePackage.getCoverageItemsCovered(CoverageOptionManager.Not_Covered).size());

      // Confirm that fileContents were updated
      coverageUnit = (CoverageUnit) CoverageTestUtil.getFirstCoverageByName(coveragePackage, "PowerUnit1.java");
      Assert.assertNotNull(coverageUnit);
      String postFileContents = coverageUnit.getFileContents();
      Assert.assertTrue("File Contents should have been updated and thus not equal",
            !postFileContents.equals(preFileContents));
      Assert.assertTrue("deselectAdded should now exist in file contents", postFileContents.contains("deselectAdded"));

      CoveragePackage loadedCp = null;
      if (testWithDb) {
         // Test Persist of CoveragePackage
         OseeCoverageStore store = OseeCoveragePackageStore.get(coveragePackage, commonBranch);
         SkynetTransaction transaction = new SkynetTransaction(commonBranch, "Coverage Package Save");
         store.save(transaction);
         transaction.execute();

         // Test Load of Coverage Package
         Artifact artifact = ArtifactQuery.getArtifactFromId(coveragePackage.getGuid(), commonBranch);
         CoverageTestUtil.registerAsTestArtifact(artifact);
         artifact.persist();

         OseeCoveragePackageStore packageStore = new OseeCoveragePackageStore(artifact);
         Assert.assertNotNull(packageStore.getArtifact(false));
         loadedCp = packageStore.getCoveragePackage();
      } else {
         loadedCp = coveragePackage;
      }

      Assert.assertEquals(68, loadedCp.getCoverageItemsCovered().size());
      Assert.assertEquals(135, loadedCp.getCoverageItems().size());
      Assert.assertEquals(50, loadedCp.getCoveragePercent().intValue());
      Assert.assertEquals(0, loadedCp.getCoverageItemsCovered(CoverageOptionManager.Deactivated_Code).size());
      Assert.assertEquals(0, loadedCp.getCoverageItemsCovered(CoverageOptionManager.Exception_Handling).size());
      Assert.assertEquals(68, loadedCp.getCoverageItemsCovered(CoverageOptionManager.Test_Unit).size());
      Assert.assertEquals(67, loadedCp.getCoverageItemsCovered(CoverageOptionManager.Not_Covered).size());

      // Confirm that updated fileContents were loaded
      coverageUnit = (CoverageUnit) CoverageTestUtil.getFirstCoverageByName(coveragePackage, "PowerUnit1.java");
      Assert.assertNotNull(coverageUnit);
      String postLoadFileContents = coverageUnit.getFileContents();
      Assert.assertEquals("File Contents should be same pre and post save", postLoadFileContents, postFileContents);
      Assert.assertTrue("deselectAdded should exist in loaded file contents",
            postLoadFileContents.contains("deselectAdded"));
   }

   @Test
   // Re-import; Add NavigationButton.setImage coverageItems 2,3 to end
   public void testImport5() throws Exception {
      CoverageImport5TestBlam coverageImport5TestBlam = new CoverageImport5TestBlam();
      coverageImport = coverageImport5TestBlam.run(null);
      Assert.assertNotNull(coverageImport);

      if (testWithDb) {
         // Test Load of Coverage Package
         Artifact artifact = ArtifactQuery.getArtifactFromId(coveragePackage.getGuid(), commonBranch);
         CoverageTestUtil.registerAsTestArtifact(artifact);
         artifact.persist();
         OseeCoveragePackageStore packageStore = new OseeCoveragePackageStore(artifact);
         Assert.assertNotNull(packageStore.getArtifact(false));
         coveragePackage = packageStore.getCoveragePackage();
      }

      // Look at file contents for NavigationButton.setImage and make sure only one coverageitem exists
      CoverageUnit coverageUnit =
            (CoverageUnit) CoverageTestUtil.getFirstCoverageByName(coveragePackage, "NavigationButton1.java");
      Assert.assertNotNull(coverageUnit);
      CoverageUnit setImageCoverageUnit = null;
      for (CoverageUnit childCoverageUnit : coverageUnit.getCoverageUnits()) {
         if (childCoverageUnit.getName().equals("setImage")) {
            setImageCoverageUnit = childCoverageUnit;
         }
      }
      Assert.assertNotNull(setImageCoverageUnit);
      Assert.assertEquals(1, setImageCoverageUnit.getCoverageItems().size());

      // Test MergeManager
      Assert.assertNotNull(coveragePackage);
      MergeManager mergeManager = new MergeManager(coveragePackage, coverageImport);
      Assert.assertEquals(1, mergeManager.getMergeItems().size());
      Assert.assertTrue(mergeManager.getMergeItems().iterator().next() instanceof MergeItemGroup);
      Assert.assertTrue(((MergeItemGroup) mergeManager.getMergeItems().iterator().next()).getMergeType() == MergeType.CI_Changes);
      Assert.assertEquals(2, ((MergeItemGroup) mergeManager.getMergeItems().iterator().next()).getMergeItems().size());

      MergeImportManager importManager = new MergeImportManager(mergeManager);
      XResultData resultData = importManager.importItems(new ISaveable() {

         @Override
         public Result isEditable() {
            return Result.TrueResult;
         }

         @Override
         public Result save() throws OseeCoreException {
            return Result.TrueResult;
         }

         @Override
         public Result save(Collection<ICoverage> coverages) throws OseeCoreException {
            return Result.TrueResult;
         }

         @Override
         public Result saveImportRecord(SkynetTransaction transaction, CoverageImport coverageImport) throws OseeCoreException {
            return Result.TrueResult;
         }

         @Override
         public Branch getBranch() throws OseeCoreException {
            return commonBranch;
         }

      }, mergeManager.getMergeItems());
      Assert.assertEquals(0, resultData.getNumErrors());

      // CoveragePackage should now have imported results
      Assert.assertEquals(69, coveragePackage.getCoverageItemsCovered().size());
      Assert.assertEquals(137, coveragePackage.getCoverageItems().size());
      Assert.assertEquals(50, coveragePackage.getCoveragePercent().intValue());
      Assert.assertEquals(0, coveragePackage.getCoverageItemsCovered(CoverageOptionManager.Deactivated_Code).size());
      Assert.assertEquals(0, coveragePackage.getCoverageItemsCovered(CoverageOptionManager.Exception_Handling).size());
      Assert.assertEquals(69, coveragePackage.getCoverageItemsCovered(CoverageOptionManager.Test_Unit).size());
      Assert.assertEquals(68, coveragePackage.getCoverageItemsCovered(CoverageOptionManager.Not_Covered).size());

      CoveragePackage loadedCp = null;
      if (testWithDb) {
         // Test Persist of CoveragePackage
         OseeCoverageStore store = OseeCoveragePackageStore.get(coveragePackage, commonBranch);
         SkynetTransaction transaction = new SkynetTransaction(commonBranch, "Coverage Package Save");
         store.save(transaction);
         transaction.execute();

         // Test Load of Coverage Package
         Artifact artifact = ArtifactQuery.getArtifactFromId(coveragePackage.getGuid(), commonBranch);
         CoverageTestUtil.registerAsTestArtifact(artifact);
         artifact.persist();

         OseeCoveragePackageStore packageStore = new OseeCoveragePackageStore(artifact);
         Assert.assertNotNull(packageStore.getArtifact(false));
         loadedCp = packageStore.getCoveragePackage();
      } else {
         loadedCp = coveragePackage;
      }

      Assert.assertEquals(69, loadedCp.getCoverageItemsCovered().size());
      Assert.assertEquals(137, loadedCp.getCoverageItems().size());
      Assert.assertEquals(50, loadedCp.getCoveragePercent().intValue());
      Assert.assertEquals(0, loadedCp.getCoverageItemsCovered(CoverageOptionManager.Deactivated_Code).size());
      Assert.assertEquals(0, loadedCp.getCoverageItemsCovered(CoverageOptionManager.Exception_Handling).size());
      Assert.assertEquals(69, loadedCp.getCoverageItemsCovered(CoverageOptionManager.Test_Unit).size());
      Assert.assertEquals(68, loadedCp.getCoverageItemsCovered(CoverageOptionManager.Not_Covered).size());
   }

   @Test
   // Re-import; Delete PowerUnit2.clear
   public void testImport6() throws Exception {
      CoverageImport6TestBlam coverageImport6TestBlam = new CoverageImport6TestBlam();
      coverageImport = coverageImport6TestBlam.run(null);
      Assert.assertNotNull(coverageImport);

      if (testWithDb) {
         // Test Load of Coverage Package
         Artifact artifact = ArtifactQuery.getArtifactFromId(coveragePackage.getGuid(), commonBranch);
         CoverageTestUtil.registerAsTestArtifact(artifact);
         artifact.persist();
         OseeCoveragePackageStore packageStore = new OseeCoveragePackageStore(artifact);
         Assert.assertNotNull(packageStore.getArtifact(false));
         coveragePackage = packageStore.getCoveragePackage();
      }

      // Get and store off coverage unit to delete so can confirm deletion occurred
      CoverageUnit powerUnit2CoverageUnit =
            (CoverageUnit) CoverageTestUtil.getFirstCoverageByName(coveragePackage, "PowerUnit2.java");
      Assert.assertNotNull(powerUnit2CoverageUnit);
      CoverageUnit clearCoverageUnitForDeletion = null;
      for (CoverageUnit childCoverageUnit : powerUnit2CoverageUnit.getCoverageUnits()) {
         if (childCoverageUnit.getName().equals("clear")) {
            clearCoverageUnitForDeletion = childCoverageUnit;
         }
      }
      Assert.assertNotNull("clear CoverageUnit should exist", clearCoverageUnitForDeletion);

      // Test MergeManager
      Assert.assertNotNull(coveragePackage);
      MergeManager mergeManager = new MergeManager(coveragePackage, coverageImport);
      Assert.assertEquals(1, mergeManager.getMergeItems().size());
      Assert.assertTrue(mergeManager.getMergeItems().iterator().next() instanceof MergeItemGroup);
      int numDelete = 0, numMoveDueToDelete = 0;
      for (IMergeItem mergeItem : ((MergeItemGroup) mergeManager.getMergeItems().iterator().next()).getMergeItems()) {
         if (mergeItem.getMergeType() == MergeType.Delete) {
            numDelete++;
         } else if (mergeItem.getMergeType() == MergeType.Moved_Due_To_Delete) {
            numMoveDueToDelete++;
         } else {
            throw new OseeStateException("Unexpected merge type for Delete" + mergeItem.getMergeType());
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
         public Result save() throws OseeCoreException {
            return Result.TrueResult;
         }

         @Override
         public Result save(Collection<ICoverage> coverages) throws OseeCoreException {
            return Result.TrueResult;
         }

         @Override
         public Result saveImportRecord(SkynetTransaction transaction, CoverageImport coverageImport) throws OseeCoreException {
            return Result.TrueResult;
         }

         @Override
         public Branch getBranch() throws OseeCoreException {
            return commonBranch;
         }

      }, mergeManager.getMergeItems());
      Assert.assertEquals(0, resultData.getNumErrors());

      // Make sure clear is not there anymore
      powerUnit2CoverageUnit =
            (CoverageUnit) CoverageTestUtil.getFirstCoverageByName(coveragePackage, "PowerUnit2.java");
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
      Assert.assertEquals(0, coveragePackage.getCoverageItemsCovered(CoverageOptionManager.Deactivated_Code).size());
      Assert.assertEquals(0, coveragePackage.getCoverageItemsCovered(CoverageOptionManager.Exception_Handling).size());
      Assert.assertEquals(68, coveragePackage.getCoverageItemsCovered(CoverageOptionManager.Test_Unit).size());
      Assert.assertEquals(67, coveragePackage.getCoverageItemsCovered(CoverageOptionManager.Not_Covered).size());

      CoveragePackage loadedCp = null;
      if (testWithDb) {
         // Test Persist of CoveragePackage
         OseeCoverageStore store = OseeCoveragePackageStore.get(coveragePackage, commonBranch);
         SkynetTransaction transaction = new SkynetTransaction(commonBranch, "Coverage Package Save");
         store.save(transaction);
         transaction.execute();

         // Test Load of Coverage Package
         Artifact artifact = ArtifactQuery.getArtifactFromId(coveragePackage.getGuid(), commonBranch);
         CoverageTestUtil.registerAsTestArtifact(artifact);
         artifact.persist();

         OseeCoveragePackageStore packageStore = new OseeCoveragePackageStore(artifact);
         Assert.assertNotNull(packageStore.getArtifact(false));
         loadedCp = packageStore.getCoveragePackage();
      } else {
         loadedCp = coveragePackage;
      }

      Assert.assertEquals(68, loadedCp.getCoverageItemsCovered().size());
      Assert.assertEquals(135, loadedCp.getCoverageItems().size());
      Assert.assertEquals(50, loadedCp.getCoveragePercent().intValue());
      Assert.assertEquals(0, loadedCp.getCoverageItemsCovered(CoverageOptionManager.Deactivated_Code).size());
      Assert.assertEquals(0, loadedCp.getCoverageItemsCovered(CoverageOptionManager.Exception_Handling).size());
      Assert.assertEquals(68, loadedCp.getCoverageItemsCovered(CoverageOptionManager.Test_Unit).size());
      Assert.assertEquals(67, loadedCp.getCoverageItemsCovered(CoverageOptionManager.Not_Covered).size());

      // Make sure clear is not there anymore
      powerUnit2CoverageUnit = (CoverageUnit) CoverageTestUtil.getFirstCoverageByName(loadedCp, "PowerUnit2.java");
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
         ArtifactQuery.getArtifactFromId(clearCoverageUnitForDeletion.getGuid(), commonBranch);
         Assert.fail("clear CoverageUnit should no longer exist");
      } catch (ArtifactDoesNotExist ex) {
         // do nothing, this exception should have been thrown
      }
      Artifact clearArt =
            ArtifactQuery.getArtifactFromId(clearCoverageUnitForDeletion.getGuid(), commonBranch, INCLUDE_DELETED);
      Assert.assertNotNull("clear CoverageUnit should exist if search for deleted == true", clearArt);

   }

   @Test
   // Re-import; Delete PowerUnit2.clear
   public void testImport7() throws Exception {
      CoverageImport7TestBlam coverageImport7TestBlam = new CoverageImport7TestBlam();
      coverageImport = coverageImport7TestBlam.run(null);
      Assert.assertNotNull(coverageImport);

      if (testWithDb) {
         // Test Load of Coverage Package
         Artifact artifact = ArtifactQuery.getArtifactFromId(coveragePackage.getGuid(), commonBranch);
         CoverageTestUtil.registerAsTestArtifact(artifact);
         artifact.persist();
         OseeCoveragePackageStore packageStore = new OseeCoveragePackageStore(artifact);
         Assert.assertNotNull(packageStore.getArtifact(false));
         coveragePackage = packageStore.getCoveragePackage();
      }

      // Get and store off coverage unit to delete so can confirm deletion occurred
      CoverageUnit auxPowerUnit1CoverageUnit =
            (CoverageUnit) CoverageTestUtil.getFirstCoverageByName(coveragePackage, "AuxPowerUnit1.java");
      Assert.assertNotNull(auxPowerUnit1CoverageUnit);
      CoverageUnit clearCoverageUnit = null;
      for (CoverageUnit childCoverageUnit : auxPowerUnit1CoverageUnit.getCoverageUnits()) {
         if (childCoverageUnit.getName().equals("clear")) {
            clearCoverageUnit = childCoverageUnit;
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
      Assert.assertNotNull(clearCoverageItemNum3);

      String preFileContents = auxPowerUnit1CoverageUnit.getFileContents();
      Assert.assertFalse("\"clear it now\" should not yet exist in file contents",
            preFileContents.contains("clear it now"));

      // Test MergeManager
      Assert.assertNotNull(coveragePackage);
      MergeManager mergeManager = new MergeManager(coveragePackage, coverageImport);
      Assert.assertEquals(1, mergeManager.getMergeItems().size());
      Assert.assertTrue(mergeManager.getMergeItems().iterator().next() instanceof MergeItemGroup);
      int numRename = 0;
      for (IMergeItem mergeItem : ((MergeItemGroup) mergeManager.getMergeItems().iterator().next()).getMergeItems()) {
         if (mergeItem.getMergeType() == MergeType.CI_Renamed) {
            numRename++;
         } else {
            throw new OseeStateException(String.format("Unexpected merge type [%s] for Delete_And_Reorder group",
                  mergeItem.getMergeType()));
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
         public Result save() throws OseeCoreException {
            return Result.TrueResult;
         }

         @Override
         public Result save(Collection<ICoverage> coverages) throws OseeCoreException {
            return Result.TrueResult;
         }

         @Override
         public Result saveImportRecord(SkynetTransaction transaction, CoverageImport coverageImport) throws OseeCoreException {
            return Result.TrueResult;
         }

         @Override
         public Branch getBranch() throws OseeCoreException {
            return commonBranch;
         }

      }, mergeManager.getMergeItems());
      Assert.assertEquals(0, resultData.getNumErrors());

      // CoveragePackage should now have imported results
      Assert.assertEquals(69, coveragePackage.getCoverageItemsCovered().size());
      Assert.assertEquals(135, coveragePackage.getCoverageItems().size());
      Assert.assertEquals(51, coveragePackage.getCoveragePercent().intValue());
      Assert.assertEquals(0, coveragePackage.getCoverageItemsCovered(CoverageOptionManager.Deactivated_Code).size());
      Assert.assertEquals(0, coveragePackage.getCoverageItemsCovered(CoverageOptionManager.Exception_Handling).size());
      Assert.assertEquals(69, coveragePackage.getCoverageItemsCovered(CoverageOptionManager.Test_Unit).size());
      Assert.assertEquals(66, coveragePackage.getCoverageItemsCovered(CoverageOptionManager.Not_Covered).size());

      CoveragePackage loadedCp = null;
      if (testWithDb) {
         // Test Persist of CoveragePackage
         OseeCoverageStore store = OseeCoveragePackageStore.get(coveragePackage, commonBranch);
         SkynetTransaction transaction = new SkynetTransaction(commonBranch, "Coverage Package Save");
         store.save(transaction);
         transaction.execute();

         // Test Load of Coverage Package
         Artifact artifact = ArtifactQuery.getArtifactFromId(coveragePackage.getGuid(), commonBranch);
         CoverageTestUtil.registerAsTestArtifact(artifact);
         artifact.persist();

         OseeCoveragePackageStore packageStore = new OseeCoveragePackageStore(artifact);
         Assert.assertNotNull(packageStore.getArtifact(false));
         loadedCp = packageStore.getCoveragePackage();
      } else {
         loadedCp = coveragePackage;
      }

      // Confirm that fileContents were updated
      auxPowerUnit1CoverageUnit =
            (CoverageUnit) CoverageTestUtil.getFirstCoverageByName(coveragePackage, "AuxPowerUnit1.java");
      Assert.assertNotNull(auxPowerUnit1CoverageUnit);
      String postFileContents = auxPowerUnit1CoverageUnit.getFileContents();
      Assert.assertTrue("File Contents should have been updated and thus not equal",
            !postFileContents.equals(preFileContents));
      Assert.assertTrue("\"clear it now\" should now exist in file contents", postFileContents.contains("clear it now"));

      Assert.assertEquals(69, loadedCp.getCoverageItemsCovered().size());
      Assert.assertEquals(135, loadedCp.getCoverageItems().size());
      Assert.assertEquals(51, loadedCp.getCoveragePercent().intValue());
      Assert.assertEquals(0, loadedCp.getCoverageItemsCovered(CoverageOptionManager.Deactivated_Code).size());
      Assert.assertEquals(0, loadedCp.getCoverageItemsCovered(CoverageOptionManager.Exception_Handling).size());
      Assert.assertEquals(69, loadedCp.getCoverageItemsCovered(CoverageOptionManager.Test_Unit).size());
      Assert.assertEquals(66, loadedCp.getCoverageItemsCovered(CoverageOptionManager.Not_Covered).size());

      // Ensure that the items were updated
      Assert.assertTrue(clearCoverageItemNum2.getName().contains("clear it now"));
      Assert.assertTrue(clearCoverageItemNum3.getName().contains("clear it"));

      // Ensure that file contents were updated
      auxPowerUnit1CoverageUnit =
            (CoverageUnit) CoverageTestUtil.getFirstCoverageByName(coveragePackage, "AuxPowerUnit1.java");
      Assert.assertNotNull(auxPowerUnit1CoverageUnit);
      String postLoadFileContents = auxPowerUnit1CoverageUnit.getFileContents();
      Assert.assertEquals("File Contents should be same pre and post save", postLoadFileContents, postFileContents);
      Assert.assertTrue("deselectAdded should exist in loaded file contents",
            postLoadFileContents.contains("clear it now"));

   }

   @Test
   // Re-import; Deletes NavigationButton2.getText.line2
   public void testImport8() throws Exception {
      CoverageImport8TestBlam coverageImport8TestBlam = new CoverageImport8TestBlam();
      coverageImport = coverageImport8TestBlam.run(null);
      Assert.assertNotNull(coverageImport);

      if (testWithDb) {
         // Test Load of Coverage Package
         Artifact artifact = ArtifactQuery.getArtifactFromId(coveragePackage.getGuid(), commonBranch);
         CoverageTestUtil.registerAsTestArtifact(artifact);
         artifact.persist();
         OseeCoveragePackageStore packageStore = new OseeCoveragePackageStore(artifact);
         Assert.assertNotNull(packageStore.getArtifact(false));
         coveragePackage = packageStore.getCoveragePackage();
      }

      // Get and store off coverage unit to delete so can confirm deletion occurred
      CoverageUnit navigateButton2 =
            (CoverageUnit) CoverageTestUtil.getFirstCoverageByName(coveragePackage, "NavigationButton2.java");
      Assert.assertNotNull(navigateButton2);
      CoverageUnit getTextCoverageUnit = null;
      for (CoverageUnit childCoverageUnit : navigateButton2.getCoverageUnits()) {
         if (childCoverageUnit.getName().equals("getText")) {
            getTextCoverageUnit = childCoverageUnit;
         }
      }

      Assert.assertNotNull(getTextCoverageUnit);
      boolean foundIt = false;
      for (CoverageItem childCoverageUnit : getTextCoverageUnit.getCoverageItems()) {
         if (childCoverageUnit.getName().contains("Navigate Here")) {
            foundIt = true;
         }
      }
      Assert.assertTrue("Should have found coverage item with \"Navigate Here\" string.", foundIt);

      String preFileContents = navigateButton2.getFileContents();
      Assert.assertTrue("\"Navigate Here\" should exist in file contents", preFileContents.contains("Navigate Here"));

      // Test MergeManager
      Assert.assertNotNull(coveragePackage);
      MergeManager mergeManager = new MergeManager(coveragePackage, coverageImport);
      Assert.assertEquals(1, mergeManager.getMergeItems().size());
      Assert.assertTrue(mergeManager.getMergeItems().iterator().next() instanceof MergeItemGroup);
      int numRename = 0, numDeleted = 0;
      for (IMergeItem mergeItem : ((MergeItemGroup) mergeManager.getMergeItems().iterator().next()).getMergeItems()) {
         if (mergeItem.getMergeType() == MergeType.CI_Renamed) {
            numRename++;
         } else if (mergeItem.getMergeType() == MergeType.CI_Delete) {
            numDeleted++;
         } else {
            throw new OseeStateException(String.format("Unexpected merge type [%s] for CI_Changes group",
                  mergeItem.getMergeType()));
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
         public Result save() throws OseeCoreException {
            return Result.TrueResult;
         }

         @Override
         public Result save(Collection<ICoverage> coverages) throws OseeCoreException {
            return Result.TrueResult;
         }

         @Override
         public Result saveImportRecord(SkynetTransaction transaction, CoverageImport coverageImport) throws OseeCoreException {
            return Result.TrueResult;
         }

         @Override
         public Branch getBranch() throws OseeCoreException {
            return commonBranch;
         }

      }, mergeManager.getMergeItems());
      Assert.assertEquals(0, resultData.getNumErrors());

      // CoveragePackage should now have imported results
      Assert.assertEquals(68, coveragePackage.getCoverageItemsCovered().size());
      Assert.assertEquals(134, coveragePackage.getCoverageItems().size());
      Assert.assertEquals(50, coveragePackage.getCoveragePercent().intValue());
      Assert.assertEquals(0, coveragePackage.getCoverageItemsCovered(CoverageOptionManager.Deactivated_Code).size());
      Assert.assertEquals(0, coveragePackage.getCoverageItemsCovered(CoverageOptionManager.Exception_Handling).size());
      Assert.assertEquals(68, coveragePackage.getCoverageItemsCovered(CoverageOptionManager.Test_Unit).size());
      Assert.assertEquals(66, coveragePackage.getCoverageItemsCovered(CoverageOptionManager.Not_Covered).size());

      CoveragePackage loadedCp = null;
      if (testWithDb) {
         // Test Persist of CoveragePackage
         OseeCoverageStore store = OseeCoveragePackageStore.get(coveragePackage, commonBranch);
         SkynetTransaction transaction = new SkynetTransaction(commonBranch, "Coverage Package Save");
         store.save(transaction);
         transaction.execute();

         // Test Load of Coverage Package
         Artifact artifact = ArtifactQuery.getArtifactFromId(coveragePackage.getGuid(), commonBranch);
         CoverageTestUtil.registerAsTestArtifact(artifact);
         artifact.persist();

         OseeCoveragePackageStore packageStore = new OseeCoveragePackageStore(artifact);
         Assert.assertNotNull(packageStore.getArtifact(false));
         loadedCp = packageStore.getCoveragePackage();
      } else {
         loadedCp = coveragePackage;
      }

      // Confirm that fileContents were updated
      navigateButton2 =
            (CoverageUnit) CoverageTestUtil.getFirstCoverageByName(coveragePackage, "NavigationButton2.java");
      Assert.assertNotNull(navigateButton2);
      String postFileContents = navigateButton2.getFileContents();
      Assert.assertTrue("File Contents should been updated and thus not equal",
            !postFileContents.equals(preFileContents));
      Assert.assertFalse("\"Navigate Here\" should NOT exist in file contents",
            postFileContents.contains("Navigate Here"));

      Assert.assertEquals(68, loadedCp.getCoverageItemsCovered().size());
      Assert.assertEquals(134, loadedCp.getCoverageItems().size());
      Assert.assertEquals(50, loadedCp.getCoveragePercent().intValue());
      Assert.assertEquals(0, loadedCp.getCoverageItemsCovered(CoverageOptionManager.Deactivated_Code).size());
      Assert.assertEquals(0, loadedCp.getCoverageItemsCovered(CoverageOptionManager.Exception_Handling).size());
      Assert.assertEquals(68, loadedCp.getCoverageItemsCovered(CoverageOptionManager.Test_Unit).size());
      Assert.assertEquals(66, loadedCp.getCoverageItemsCovered(CoverageOptionManager.Not_Covered).size());

      // Ensure that file contents were updated
      navigateButton2 =
            (CoverageUnit) CoverageTestUtil.getFirstCoverageByName(coveragePackage, "NavigationButton2.java");
      Assert.assertNotNull(navigateButton2);
      String postLoadFileContents = navigateButton2.getFileContents();
      Assert.assertEquals("File Contents should be same pre and post save", postLoadFileContents, postFileContents);
      Assert.assertFalse("\"Navigate Here\" should NOT exist in loaded file contents",
            postLoadFileContents.contains("Navigate Here"));

   }

   @Test
   // Re-import; Method update for NavigationButton2.getImage.line2 and line5
   public void testImport9() throws Exception {
      CoverageImport9TestBlam coverageImport9TestBlam = new CoverageImport9TestBlam();
      coverageImport = coverageImport9TestBlam.run(null);
      Assert.assertNotNull(coverageImport);

      if (testWithDb) {
         // Test Load of Coverage Package
         Artifact artifact = ArtifactQuery.getArtifactFromId(coveragePackage.getGuid(), commonBranch);
         CoverageTestUtil.registerAsTestArtifact(artifact);
         artifact.persist();
         OseeCoveragePackageStore packageStore = new OseeCoveragePackageStore(artifact);
         Assert.assertNotNull(packageStore.getArtifact(false));
         coveragePackage = packageStore.getCoveragePackage();
      }

      // Test MergeManager
      Assert.assertNotNull(coveragePackage);
      MergeManager mergeManager = new MergeManager(coveragePackage, coverageImport);
      Assert.assertEquals(1, mergeManager.getMergeItems().size());
      Assert.assertTrue(mergeManager.getMergeItems().iterator().next() instanceof MergeItemGroup);
      int numUpdateMethod = 0;
      for (IMergeItem mergeItem : ((MergeItemGroup) mergeManager.getMergeItems().iterator().next()).getMergeItems()) {
         if (mergeItem.getMergeType() == MergeType.CI_Method_Update) {
            numUpdateMethod++;
         } else {
            throw new OseeStateException(String.format("Unexpected merge type [%s] for CI_Changes group",
                  mergeItem.getMergeType()));
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
         public Result save() throws OseeCoreException {
            return Result.TrueResult;
         }

         @Override
         public Result save(Collection<ICoverage> coverages) throws OseeCoreException {
            return Result.TrueResult;
         }

         @Override
         public Result saveImportRecord(SkynetTransaction transaction, CoverageImport coverageImport) throws OseeCoreException {
            return Result.TrueResult;
         }

         @Override
         public Branch getBranch() throws OseeCoreException {
            return commonBranch;
         }

      }, mergeManager.getMergeItems());
      Assert.assertEquals(0, resultData.getNumErrors());

      // CoveragePackage should now have imported results
      Assert.assertEquals(70, coveragePackage.getCoverageItemsCovered().size());
      Assert.assertEquals(134, coveragePackage.getCoverageItems().size());
      Assert.assertEquals(52, coveragePackage.getCoveragePercent().intValue());
      Assert.assertEquals(0, coveragePackage.getCoverageItemsCovered(CoverageOptionManager.Deactivated_Code).size());
      Assert.assertEquals(0, coveragePackage.getCoverageItemsCovered(CoverageOptionManager.Exception_Handling).size());
      Assert.assertEquals(70, coveragePackage.getCoverageItemsCovered(CoverageOptionManager.Test_Unit).size());
      Assert.assertEquals(64, coveragePackage.getCoverageItemsCovered(CoverageOptionManager.Not_Covered).size());

      CoveragePackage loadedCp = null;
      if (testWithDb) {
         // Test Persist of CoveragePackage
         OseeCoverageStore store = OseeCoveragePackageStore.get(coveragePackage, commonBranch);
         SkynetTransaction transaction = new SkynetTransaction(commonBranch, "Coverage Package Save");
         store.save(transaction);
         transaction.execute();

         // Test Load of Coverage Package
         Artifact artifact = ArtifactQuery.getArtifactFromId(coveragePackage.getGuid(), commonBranch);
         CoverageTestUtil.registerAsTestArtifact(artifact);
         artifact.persist();

         OseeCoveragePackageStore packageStore = new OseeCoveragePackageStore(artifact);
         Assert.assertNotNull(packageStore.getArtifact(false));
         loadedCp = packageStore.getCoveragePackage();
      } else {
         loadedCp = coveragePackage;
      }

      Assert.assertEquals(70, loadedCp.getCoverageItemsCovered().size());
      Assert.assertEquals(134, loadedCp.getCoverageItems().size());
      Assert.assertEquals(52, loadedCp.getCoveragePercent().intValue());
      Assert.assertEquals(0, loadedCp.getCoverageItemsCovered(CoverageOptionManager.Deactivated_Code).size());
      Assert.assertEquals(0, loadedCp.getCoverageItemsCovered(CoverageOptionManager.Exception_Handling).size());
      Assert.assertEquals(70, loadedCp.getCoverageItemsCovered(CoverageOptionManager.Test_Unit).size());
      Assert.assertEquals(64, loadedCp.getCoverageItemsCovered(CoverageOptionManager.Not_Covered).size());

   }
}
