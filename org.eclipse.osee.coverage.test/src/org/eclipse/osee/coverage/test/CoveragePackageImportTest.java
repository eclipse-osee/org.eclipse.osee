/*
 * Created on Oct 27, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.test;

import junit.framework.Assert;
import org.eclipse.osee.coverage.merge.MergeItem;
import org.eclipse.osee.coverage.merge.MergeManager;
import org.eclipse.osee.coverage.merge.MergeType;
import org.eclipse.osee.coverage.merge.MessageMergeItem;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.coverage.model.CoverageMethodEnum;
import org.eclipse.osee.coverage.model.CoveragePackage;
import org.eclipse.osee.coverage.store.OseeCoveragePackageStore;
import org.eclipse.osee.coverage.store.OseeCoverageStore;
import org.eclipse.osee.coverage.test.import1.CoverageImport1TestBlam;
import org.eclipse.osee.coverage.test.import2.CoverageImport2TestBlam;
import org.eclipse.osee.coverage.test.util.CoverageTestUtil;
import org.eclipse.osee.coverage.util.CoveragePackageImportManager;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.coverage.util.ISaveable;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
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

   @BeforeClass
   @AfterClass
   public static void testCleanup() throws OseeCoreException {
      CoverageTestUtil.cleanupCoverageTests();
   }

   @Test
   public void testImport1() throws Exception {
      CoverageImport1TestBlam coverageImport1TestBlam = new CoverageImport1TestBlam();
      coverageImport = coverageImport1TestBlam.run();
      Assert.assertNotNull(coverageImport);

      // Check import results
      Assert.assertEquals(121, coverageImport.getCoverageItems().size());
      Assert.assertEquals(49, coverageImport.getCoveragePercent());
      Assert.assertEquals(60, coverageImport.getCoverageItemsCovered().size());
      Assert.assertEquals(0, coverageImport.getCoverageItemsCovered(CoverageMethodEnum.Deactivated_Code).size());
      Assert.assertEquals(0, coverageImport.getCoverageItemsCovered(CoverageMethodEnum.Exception_Handling).size());
      Assert.assertEquals(61, coverageImport.getCoverageItemsCovered(CoverageMethodEnum.Not_Covered).size());
      Assert.assertEquals(60, coverageImport.getCoverageItemsCovered(CoverageMethodEnum.Test_Unit).size());

      // Test MergeManager
      coveragePackage = new CoveragePackage("Test Import");
      MergeManager mergeManager = new MergeManager(coveragePackage, coverageImport);
      Assert.assertEquals(4, mergeManager.getMergeItems().size());
      for (MergeItem mergeItem : mergeManager.getMergeItems()) {
         Assert.assertEquals(MergeType.Add, mergeItem.getMergeType());
      }

      // Test import where not-editable
      CoveragePackageImportManager importManager = new CoveragePackageImportManager(mergeManager);
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

      // CoveragePackage should now have imported results
      Assert.assertEquals(121, coveragePackage.getCoverageItems().size());
      Assert.assertEquals(49, coveragePackage.getCoveragePercent());
      Assert.assertEquals(60, coveragePackage.getCoverageItemsCovered().size());
      Assert.assertEquals(0, coveragePackage.getCoverageItemsCovered(CoverageMethodEnum.Deactivated_Code).size());
      Assert.assertEquals(0, coveragePackage.getCoverageItemsCovered(CoverageMethodEnum.Exception_Handling).size());
      Assert.assertEquals(61, coveragePackage.getCoverageItemsCovered(CoverageMethodEnum.Not_Covered).size());
      Assert.assertEquals(60, coveragePackage.getCoverageItemsCovered(CoverageMethodEnum.Test_Unit).size());

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
      CoveragePackage loadedCp = packageStore.getCoveragePackage();

      Assert.assertEquals(121, loadedCp.getCoverageItems().size());
      Assert.assertEquals(49, loadedCp.getCoveragePercent());
      Assert.assertEquals(60, loadedCp.getCoverageItemsCovered().size());
      Assert.assertEquals(0, loadedCp.getCoverageItemsCovered(CoverageMethodEnum.Deactivated_Code).size());
      Assert.assertEquals(0, loadedCp.getCoverageItemsCovered(CoverageMethodEnum.Exception_Handling).size());
      Assert.assertEquals(61, loadedCp.getCoverageItemsCovered(CoverageMethodEnum.Not_Covered).size());
      Assert.assertEquals(60, loadedCp.getCoverageItemsCovered(CoverageMethodEnum.Test_Unit).size());

   }

   @Test
   // Re-import with no changes, make sure no merge items exist
   public void testImport1B() throws Exception {
      CoverageImport1TestBlam coverageImport1TestBlam = new CoverageImport1TestBlam();
      coverageImport = coverageImport1TestBlam.run();
      Assert.assertNotNull(coverageImport);

      // Check import results
      Assert.assertEquals(121, coverageImport.getCoverageItems().size());
      Assert.assertEquals(49, coverageImport.getCoveragePercent());
      Assert.assertEquals(60, coverageImport.getCoverageItemsCovered().size());
      Assert.assertEquals(0, coverageImport.getCoverageItemsCovered(CoverageMethodEnum.Deactivated_Code).size());
      Assert.assertEquals(0, coverageImport.getCoverageItemsCovered(CoverageMethodEnum.Exception_Handling).size());
      Assert.assertEquals(61, coverageImport.getCoverageItemsCovered(CoverageMethodEnum.Not_Covered).size());
      Assert.assertEquals(60, coverageImport.getCoverageItemsCovered(CoverageMethodEnum.Test_Unit).size());

      // Test MergeManager
      MergeManager mergeManager = new MergeManager(coveragePackage, coverageImport);
      Assert.assertEquals(1, mergeManager.getMergeItems().size());
      // Merge item will be the "Nothing to Import" message item
      Assert.assertTrue(mergeManager.getMergeItems().iterator().next() instanceof MessageMergeItem);
   }

   @Test
   // Re-import two new Coverage Units
   public void testImport2() throws Exception {
      CoverageImport2TestBlam coverageImport2TestBlam = new CoverageImport2TestBlam();
      coverageImport = coverageImport2TestBlam.run();
      Assert.assertNotNull(coverageImport);

      // Test MergeManager
      coveragePackage = new CoveragePackage("Test Import");
      MergeManager mergeManager = new MergeManager(coveragePackage, coverageImport);
      Assert.assertEquals(4, mergeManager.getMergeItems().size());
      for (MergeItem mergeItem : mergeManager.getMergeItems()) {
         Assert.assertEquals(MergeType.Add, mergeItem.getMergeType());
      }
      CoveragePackageImportManager importManager = new CoveragePackageImportManager(mergeManager);
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
      Assert.assertEquals(132, coveragePackage.getCoverageItems().size());
      Assert.assertEquals(50, coveragePackage.getCoveragePercent());
      Assert.assertEquals(66, coveragePackage.getCoverageItemsCovered().size());
      Assert.assertEquals(0, coveragePackage.getCoverageItemsCovered(CoverageMethodEnum.Deactivated_Code).size());
      Assert.assertEquals(0, coveragePackage.getCoverageItemsCovered(CoverageMethodEnum.Exception_Handling).size());
      Assert.assertEquals(66, coveragePackage.getCoverageItemsCovered(CoverageMethodEnum.Not_Covered).size());
      Assert.assertEquals(66, coveragePackage.getCoverageItemsCovered(CoverageMethodEnum.Test_Unit).size());

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
      CoveragePackage loadedCp = packageStore.getCoveragePackage();

      Assert.assertEquals(132, loadedCp.getCoverageItems().size());
      Assert.assertEquals(50, loadedCp.getCoveragePercent());
      Assert.assertEquals(66, loadedCp.getCoverageItemsCovered().size());
      Assert.assertEquals(0, loadedCp.getCoverageItemsCovered(CoverageMethodEnum.Deactivated_Code).size());
      Assert.assertEquals(0, loadedCp.getCoverageItemsCovered(CoverageMethodEnum.Exception_Handling).size());
      Assert.assertEquals(66, loadedCp.getCoverageItemsCovered(CoverageMethodEnum.Not_Covered).size());
      Assert.assertEquals(66, loadedCp.getCoverageItemsCovered(CoverageMethodEnum.Test_Unit).size());

   }
}
