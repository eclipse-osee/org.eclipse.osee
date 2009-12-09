/*
 * Created on Oct 27, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.test;

import junit.framework.Assert;
import org.eclipse.osee.coverage.merge.IMergeItem;
import org.eclipse.osee.coverage.merge.MergeImportManager;
import org.eclipse.osee.coverage.merge.MergeManager;
import org.eclipse.osee.coverage.merge.MergeType;
import org.eclipse.osee.coverage.merge.MessageMergeItem;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoverageMethodEnum;
import org.eclipse.osee.coverage.model.CoveragePackage;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.store.OseeCoveragePackageStore;
import org.eclipse.osee.coverage.store.OseeCoverageStore;
import org.eclipse.osee.coverage.test.import1.CoverageImport1TestBlam;
import org.eclipse.osee.coverage.test.import2.CoverageImport2TestBlam;
import org.eclipse.osee.coverage.test.import3.CoverageImport3TestBlam;
import org.eclipse.osee.coverage.test.import4.CoverageImport4TestBlam;
import org.eclipse.osee.coverage.test.util.CoverageTestUtil;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.coverage.util.ISaveable;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
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

   @BeforeClass
   @AfterClass
   public static void testCleanup() throws OseeCoreException {
      if (testWithDb) {
         CoverageUtil.setBranch(BranchManager.getCommonBranch());
         CoverageTestUtil.cleanupCoverageTests();
      } else
         System.err.println("Test with Db Disabled...re-inenable");
   }

   @Test
   public void testImport1() throws Exception {
      CoverageImport1TestBlam coverageImport1TestBlam = new CoverageImport1TestBlam();
      coverageImport = coverageImport1TestBlam.run();
      Assert.assertNotNull(coverageImport);

      // Check import results
      Assert.assertEquals(60, coverageImport.getCoverageItemsCovered().size());
      Assert.assertEquals(121, coverageImport.getCoverageItems().size());

      System.out.println(CoverageUtil.printTree(coverageImport));

      Assert.assertEquals(49, coverageImport.getCoveragePercent());
      Assert.assertEquals(0, coverageImport.getCoverageItemsCovered(CoverageMethodEnum.Deactivated_Code).size());
      Assert.assertEquals(0, coverageImport.getCoverageItemsCovered(CoverageMethodEnum.Exception_Handling).size());
      Assert.assertEquals(60, coverageImport.getCoverageItemsCovered(CoverageMethodEnum.Test_Unit).size());
      Assert.assertEquals(61, coverageImport.getCoverageItemsCovered(CoverageMethodEnum.Not_Covered).size());

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
      coveragePackage = new CoveragePackage("Test Coverage Package");
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
      Assert.assertEquals(121, coveragePackage.getCoverageItems().size());
      Assert.assertEquals(49, coveragePackage.getCoveragePercent());
      Assert.assertEquals(0, coveragePackage.getCoverageItemsCovered(CoverageMethodEnum.Deactivated_Code).size());
      Assert.assertEquals(0, coveragePackage.getCoverageItemsCovered(CoverageMethodEnum.Exception_Handling).size());
      Assert.assertEquals(60, coveragePackage.getCoverageItemsCovered(CoverageMethodEnum.Test_Unit).size());
      Assert.assertEquals(61, coveragePackage.getCoverageItemsCovered(CoverageMethodEnum.Not_Covered).size());

      CoveragePackage loadedCp = null;
      if (testWithDb) {
         // Test Persist of CoveragePackage
         OseeCoverageStore store = OseeCoverageStore.get(coveragePackage);
         SkynetTransaction transaction = new SkynetTransaction(CoverageUtil.getBranch(), "Coverage Package Save");
         store.save(transaction);
         transaction.execute();

         // Test Load of Coverage Package
         Artifact artifact = ArtifactQuery.getArtifactFromId(coveragePackage.getGuid(), CoverageUtil.getBranch());
         CoverageTestUtil.registerAsTestArtifact(artifact);
         artifact.persist();

         OseeCoveragePackageStore packageStore = new OseeCoveragePackageStore(artifact);
         Assert.assertNotNull(packageStore.getArtifact(false));
         loadedCp = packageStore.getCoveragePackage();
      } else {
         loadedCp = coveragePackage;
      }

      Assert.assertEquals(60, loadedCp.getCoverageItemsCovered().size());
      Assert.assertEquals(121, loadedCp.getCoverageItems().size());
      Assert.assertEquals(49, loadedCp.getCoveragePercent());
      Assert.assertEquals(0, loadedCp.getCoverageItemsCovered(CoverageMethodEnum.Deactivated_Code).size());
      Assert.assertEquals(0, loadedCp.getCoverageItemsCovered(CoverageMethodEnum.Exception_Handling).size());
      Assert.assertEquals(60, loadedCp.getCoverageItemsCovered(CoverageMethodEnum.Test_Unit).size());
      Assert.assertEquals(61, loadedCp.getCoverageItemsCovered(CoverageMethodEnum.Not_Covered).size());

   }

   @Test
   // Re-import with no changes, make sure no merge items exist
   public void testImport1B() throws Exception {
      CoverageImport1TestBlam coverageImport1TestBlam = new CoverageImport1TestBlam();
      coverageImport = coverageImport1TestBlam.run();
      Assert.assertNotNull(coverageImport);

      // Check import results
      Assert.assertEquals(60, coverageImport.getCoverageItemsCovered().size());
      Assert.assertEquals(121, coverageImport.getCoverageItems().size());
      Assert.assertEquals(49, coverageImport.getCoveragePercent());
      Assert.assertEquals(0, coverageImport.getCoverageItemsCovered(CoverageMethodEnum.Deactivated_Code).size());
      Assert.assertEquals(0, coverageImport.getCoverageItemsCovered(CoverageMethodEnum.Exception_Handling).size());
      Assert.assertEquals(60, coverageImport.getCoverageItemsCovered(CoverageMethodEnum.Test_Unit).size());
      Assert.assertEquals(61, coverageImport.getCoverageItemsCovered(CoverageMethodEnum.Not_Covered).size());

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
      coverageImport = coverageImport2TestBlam.run();
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

      }, mergeManager.getMergeItems());
      Assert.assertEquals(0, resultData.getNumErrors());

      // CoveragePackage should now have imported results
      Assert.assertEquals(66, coveragePackage.getCoverageItemsCovered().size());
      Assert.assertEquals(132, coveragePackage.getCoverageItems().size());
      Assert.assertEquals(50, coveragePackage.getCoveragePercent());
      Assert.assertEquals(0, coveragePackage.getCoverageItemsCovered(CoverageMethodEnum.Deactivated_Code).size());
      Assert.assertEquals(0, coveragePackage.getCoverageItemsCovered(CoverageMethodEnum.Exception_Handling).size());
      Assert.assertEquals(66, coveragePackage.getCoverageItemsCovered(CoverageMethodEnum.Test_Unit).size());
      Assert.assertEquals(66, coveragePackage.getCoverageItemsCovered(CoverageMethodEnum.Not_Covered).size());

      CoveragePackage loadedCp = null;
      if (testWithDb) {
         // Test Persist of CoveragePackage
         OseeCoverageStore store = OseeCoverageStore.get(coveragePackage);
         SkynetTransaction transaction = new SkynetTransaction(CoverageUtil.getBranch(), "Coverage Package Save");
         store.save(transaction);
         transaction.execute();

         // Test Load of Coverage Package
         Artifact artifact = ArtifactQuery.getArtifactFromId(coveragePackage.getGuid(), CoverageUtil.getBranch());
         CoverageTestUtil.registerAsTestArtifact(artifact);
         artifact.persist();

         OseeCoveragePackageStore packageStore = new OseeCoveragePackageStore(artifact);
         Assert.assertNotNull(packageStore.getArtifact(false));
         loadedCp = packageStore.getCoveragePackage();
      } else {
         loadedCp = coveragePackage;
      }

      Assert.assertEquals(66, loadedCp.getCoverageItemsCovered().size());
      Assert.assertEquals(132, loadedCp.getCoverageItems().size());
      Assert.assertEquals(50, loadedCp.getCoveragePercent());
      Assert.assertEquals(0, loadedCp.getCoverageItemsCovered(CoverageMethodEnum.Deactivated_Code).size());
      Assert.assertEquals(0, loadedCp.getCoverageItemsCovered(CoverageMethodEnum.Exception_Handling).size());
      Assert.assertEquals(66, loadedCp.getCoverageItemsCovered(CoverageMethodEnum.Test_Unit).size());
      Assert.assertEquals(66, loadedCp.getCoverageItemsCovered(CoverageMethodEnum.Not_Covered).size());

   }

   @Test
   // Re-import with new CoverageUnit method initAdded() at end of epu.PowerUnit1.java
   public void testImport3() throws Exception {
      CoverageImport3TestBlam coverageImport3TestBlam = new CoverageImport3TestBlam();
      coverageImport = coverageImport3TestBlam.run();
      Assert.assertNotNull(coverageImport);

      if (testWithDb) {
         // Test Load of Coverage Package
         Artifact artifact = ArtifactQuery.getArtifactFromId(coveragePackage.getGuid(), CoverageUtil.getBranch());
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

      }, mergeManager.getMergeItems());
      Assert.assertEquals(0, resultData.getNumErrors());

      // CoveragePackage should now have imported results
      Assert.assertEquals(67, coveragePackage.getCoverageItemsCovered().size());
      Assert.assertEquals(133, coveragePackage.getCoverageItems().size());
      Assert.assertEquals(50, coveragePackage.getCoveragePercent());
      Assert.assertEquals(0, coveragePackage.getCoverageItemsCovered(CoverageMethodEnum.Deactivated_Code).size());
      Assert.assertEquals(0, coveragePackage.getCoverageItemsCovered(CoverageMethodEnum.Exception_Handling).size());
      Assert.assertEquals(67, coveragePackage.getCoverageItemsCovered(CoverageMethodEnum.Test_Unit).size());
      Assert.assertEquals(66, coveragePackage.getCoverageItemsCovered(CoverageMethodEnum.Not_Covered).size());

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
         OseeCoverageStore store = OseeCoverageStore.get(coveragePackage);
         SkynetTransaction transaction = new SkynetTransaction(CoverageUtil.getBranch(), "Coverage Package Save");
         store.save(transaction);
         transaction.execute();

         // Test Load of Coverage Package
         Artifact artifact = ArtifactQuery.getArtifactFromId(coveragePackage.getGuid(), CoverageUtil.getBranch());
         CoverageTestUtil.registerAsTestArtifact(artifact);
         artifact.persist();

         OseeCoveragePackageStore packageStore = new OseeCoveragePackageStore(artifact);
         Assert.assertNotNull(packageStore.getArtifact(false));
         loadedCp = packageStore.getCoveragePackage();
      } else {
         loadedCp = coveragePackage;
      }

      Assert.assertEquals(67, loadedCp.getCoverageItemsCovered().size());
      Assert.assertEquals(133, loadedCp.getCoverageItems().size());
      Assert.assertEquals(50, loadedCp.getCoveragePercent());
      Assert.assertEquals(0, loadedCp.getCoverageItemsCovered(CoverageMethodEnum.Deactivated_Code).size());
      Assert.assertEquals(0, loadedCp.getCoverageItemsCovered(CoverageMethodEnum.Exception_Handling).size());
      Assert.assertEquals(67, loadedCp.getCoverageItemsCovered(CoverageMethodEnum.Test_Unit).size());
      Assert.assertEquals(66, loadedCp.getCoverageItemsCovered(CoverageMethodEnum.Not_Covered).size());

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
      coverageImport = coverageImport4TestBlam.run();
      Assert.assertNotNull(coverageImport);

      if (testWithDb) {
         // Test Load of Coverage Package
         Artifact artifact = ArtifactQuery.getArtifactFromId(coveragePackage.getGuid(), CoverageUtil.getBranch());
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

      }, mergeManager.getMergeItems());
      Assert.assertEquals(0, resultData.getNumErrors());

      // CoveragePackage should now have imported results
      Assert.assertEquals(68, coveragePackage.getCoverageItemsCovered().size());
      Assert.assertEquals(134, coveragePackage.getCoverageItems().size());
      Assert.assertEquals(50, coveragePackage.getCoveragePercent());
      Assert.assertEquals(0, coveragePackage.getCoverageItemsCovered(CoverageMethodEnum.Deactivated_Code).size());
      Assert.assertEquals(0, coveragePackage.getCoverageItemsCovered(CoverageMethodEnum.Exception_Handling).size());
      Assert.assertEquals(68, coveragePackage.getCoverageItemsCovered(CoverageMethodEnum.Test_Unit).size());
      Assert.assertEquals(66, coveragePackage.getCoverageItemsCovered(CoverageMethodEnum.Not_Covered).size());

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
         OseeCoverageStore store = OseeCoverageStore.get(coveragePackage);
         SkynetTransaction transaction = new SkynetTransaction(CoverageUtil.getBranch(), "Coverage Package Save");
         store.save(transaction);
         transaction.execute();

         // Test Load of Coverage Package
         Artifact artifact = ArtifactQuery.getArtifactFromId(coveragePackage.getGuid(), CoverageUtil.getBranch());
         CoverageTestUtil.registerAsTestArtifact(artifact);
         artifact.persist();

         OseeCoveragePackageStore packageStore = new OseeCoveragePackageStore(artifact);
         Assert.assertNotNull(packageStore.getArtifact(false));
         loadedCp = packageStore.getCoveragePackage();
      } else {
         loadedCp = coveragePackage;
      }

      Assert.assertEquals(68, loadedCp.getCoverageItemsCovered().size());
      Assert.assertEquals(134, loadedCp.getCoverageItems().size());
      Assert.assertEquals(50, loadedCp.getCoveragePercent());
      Assert.assertEquals(0, loadedCp.getCoverageItemsCovered(CoverageMethodEnum.Deactivated_Code).size());
      Assert.assertEquals(0, loadedCp.getCoverageItemsCovered(CoverageMethodEnum.Exception_Handling).size());
      Assert.assertEquals(68, loadedCp.getCoverageItemsCovered(CoverageMethodEnum.Test_Unit).size());
      Assert.assertEquals(66, loadedCp.getCoverageItemsCovered(CoverageMethodEnum.Not_Covered).size());

      // Confirm that updated fileContents were loaded
      coverageUnit = (CoverageUnit) CoverageTestUtil.getFirstCoverageByName(coveragePackage, "PowerUnit1.java");
      Assert.assertNotNull(coverageUnit);
      String postLoadFileContents = coverageUnit.getFileContents();
      Assert.assertEquals("File Contents should be same pre and post save", postLoadFileContents, postFileContents);
      Assert.assertTrue("deselectAdded should exist in loaded file contents",
            postLoadFileContents.contains("deselectAdded"));
   }

}
