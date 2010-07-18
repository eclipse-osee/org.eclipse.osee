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

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import junit.framework.Assert;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.merge.MergeImportManager;
import org.eclipse.osee.coverage.merge.MergeManager;
import org.eclipse.osee.coverage.merge.MergeType;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoverageOptionManager;
import org.eclipse.osee.coverage.model.CoverageOptionManagerDefault;
import org.eclipse.osee.coverage.model.CoveragePackage;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.coverage.store.OseeCoveragePackageStore;
import org.eclipse.osee.coverage.store.OseeCoverageStore;
import org.eclipse.osee.coverage.test.util.CoverageTestUtil;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.coverage.util.ISaveable;
import org.eclipse.osee.coverage.vcast.IVectorCastCoverageImportProvider;
import org.eclipse.osee.coverage.vcast.VectorCastAdaCoverageImporter;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.util.AFile;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.plugin.core.util.OseeData;
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
public class VectorCastImportTest {

   public static CoveragePackage coveragePackage = null;
   public static CoverageImport coverageImport = null;
   private static boolean testWithDb = true;

   @BeforeClass
   public static void setUp() throws OseeCoreException, IOException {
      CoverageUtil.setIsInTest(true);
      testCleanup();
      createVCastFileset();
   }

   @AfterClass
   public static void cleanUp() throws OseeCoreException {
      CoverageUtil.setIsInTest(false);
      testCleanup();
   }

   public static void testCleanup() throws OseeCoreException {
      if (testWithDb) {
         CoverageUtil.setNavigatorSelectedBranch(BranchManager.getCommonBranch());
         CoverageTestUtil.cleanupCoverageTests();
      } else
         System.err.println("Test with Db Disabled...re-inenable");
   }

   @Test
   public void testImport() throws Exception {
      // Store file as CoverageUnit
      File file = OseeData.getFolder("vcast.wrk").getLocation().toFile();
      final String coverageInputDir = file.getAbsolutePath();
      Assert.assertTrue(file.exists());

      VectorCastAdaCoverageImporter vectorCastImporter =
            new VectorCastAdaCoverageImporter(new IVectorCastCoverageImportProvider() {

               @Override
               public boolean isResolveExceptionHandling() {
                  return false;
               }

               @Override
               public String getVCastDirectory() {
                  return coverageInputDir;
               }

               @Override
               public String getFileNamespace(String filename) {
                  return "test";
               }
            });
      coverageImport = vectorCastImporter.run(null);
      Assert.assertNotNull(coverageImport);

      // Check import results
      Assert.assertEquals(12, coverageImport.getCoverageItems().size());
      Assert.assertEquals(58, coverageImport.getCoveragePercent().intValue());
      Assert.assertEquals(7, coverageImport.getCoverageItemsCovered().size());
      Assert.assertEquals(0, coverageImport.getCoverageItemsCovered(CoverageOptionManager.Deactivated_Code).size());
      Assert.assertEquals(0, coverageImport.getCoverageItemsCovered(CoverageOptionManager.Exception_Handling).size());
      Assert.assertEquals(7, coverageImport.getCoverageItemsCovered(CoverageOptionManager.Test_Unit).size());
      Assert.assertEquals(5, coverageImport.getCoverageItemsCovered(CoverageOptionManager.Not_Covered).size());

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
      Assert.assertEquals(1, mergeManager.getMergeItems().size());
      Assert.assertEquals(MergeType.Add, mergeManager.getMergeItems().iterator().next().getMergeType());

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
            return BranchManager.getCommonBranch();
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
      Assert.assertEquals(12, coveragePackage.getCoverageItems().size());
      Assert.assertEquals(58, coveragePackage.getCoveragePercent().intValue());
      Assert.assertEquals(7, coveragePackage.getCoverageItemsCovered().size());
      Assert.assertEquals(0, coveragePackage.getCoverageItemsCovered(CoverageOptionManager.Deactivated_Code).size());
      Assert.assertEquals(0, coveragePackage.getCoverageItemsCovered(CoverageOptionManager.Exception_Handling).size());
      Assert.assertEquals(7, coveragePackage.getCoverageItemsCovered(CoverageOptionManager.Test_Unit).size());
      Assert.assertEquals(5, coveragePackage.getCoverageItemsCovered(CoverageOptionManager.Not_Covered).size());

      CoveragePackage loadedCp = null;
      if (testWithDb) {
         // Test Persist of CoveragePackage
         OseeCoverageStore store = OseeCoveragePackageStore.get(coveragePackage, BranchManager.getCommonBranch());
         SkynetTransaction transaction =
               new SkynetTransaction(BranchManager.getCommonBranch(), "Coverage Package Save");
         store.save(transaction);
         transaction.execute();

         // Test Load of Coverage Package
         Artifact artifact =
               ArtifactQuery.getArtifactFromId(coveragePackage.getGuid(), BranchManager.getCommonBranch());
         CoverageTestUtil.registerAsTestArtifact(artifact);
         artifact.persist();

         OseeCoveragePackageStore packageStore = new OseeCoveragePackageStore(artifact);
         Assert.assertNotNull(packageStore.getArtifact(false));
         loadedCp = packageStore.getCoveragePackage();
      } else {
         loadedCp = coveragePackage;
      }

      Assert.assertEquals(12, loadedCp.getCoverageItems().size());
      Assert.assertEquals(58, loadedCp.getCoveragePercent().intValue());
      Assert.assertEquals(7, loadedCp.getCoverageItemsCovered().size());
      Assert.assertEquals(0, loadedCp.getCoverageItemsCovered(CoverageOptionManager.Deactivated_Code).size());
      Assert.assertEquals(0, loadedCp.getCoverageItemsCovered(CoverageOptionManager.Exception_Handling).size());
      Assert.assertEquals(7, loadedCp.getCoverageItemsCovered(CoverageOptionManager.Test_Unit).size());
      Assert.assertEquals(5, loadedCp.getCoverageItemsCovered(CoverageOptionManager.Not_Covered).size());

   }

   @Test
   public void testImportRecord() throws Exception {
      File file = OseeData.getFolder("vcast.wrk").getLocation().toFile();
      final String coverageInputDir = file.getAbsolutePath();
      Assert.assertTrue(file.exists());

      VectorCastAdaCoverageImporter vectorCastImporter =
            new VectorCastAdaCoverageImporter(new IVectorCastCoverageImportProvider() {

               @Override
               public boolean isResolveExceptionHandling() {
                  return false;
               }

               @Override
               public String getVCastDirectory() {
                  return coverageInputDir;
               }

               @Override
               public String getFileNamespace(String filename) {
                  return "test";
               }
            });
      coverageImport = vectorCastImporter.run(null);

      OseeCoveragePackageStore store = OseeCoveragePackageStore.get(coveragePackage, BranchManager.getCommonBranch());
      SkynetTransaction transaction = new SkynetTransaction(BranchManager.getCommonBranch(), "Save Import Record");
      Result result = store.saveImportRecord(transaction, coverageImport);
      Assert.assertTrue(result.isTrue());
      transaction.execute();

      Artifact packageArt = ArtifactQuery.getArtifactFromId(coveragePackage.getGuid(), BranchManager.getCommonBranch());
      Artifact foundRecordArt = null;
      for (Artifact artifact : packageArt.getChildren()) {
         if (artifact.getName().equals(OseeCoveragePackageStore.IMPORT_RECORD_NAME)) {
            foundRecordArt = artifact;
            CoverageTestUtil.registerAsTestArtifact(foundRecordArt);
            foundRecordArt.persist();
         }
      }
      Assert.assertNotNull(foundRecordArt);
      Assert.assertEquals("General Document", foundRecordArt.getArtifactTypeName());

   }

   /**
    * Create dir structure for importer to read
    */
   private static void createVCastFileset() throws OseeCoreException, IOException {
      OseeData.getFolder("vcast.wrk").getLocation().toFile();
      Lib.writeStringToFile(AFile.readFile(Activator.getInstance().getPluginFile("support/vcastData/vcast.vcp")),
            OseeData.getFile("vcast.wrk/vcast.vcp"));
      Lib.writeStringToFile(AFile.readFile(Activator.getInstance().getPluginFile("support/vcastData/CCAST_.CFG")),
            OseeData.getFile("vcast.wrk/CCAST_.CFG"));
      Lib.writeStringToFile(AFile.readFile(Activator.getInstance().getPluginFile("support/vcastData/CCAST_.CFG")),
            OseeData.getFile("vcast.wrk/build_info.xml"));
      OseeData.getFolder("vcast.wrk/vcast").getLocation().toFile();
      Lib.writeStringToFile(AFile.readFile(Activator.getInstance().getPluginFile(
            "support/vcastData/vcast/test_main.2.LIS")), OseeData.getFile("vcast.wrk/vcast/test_main.2.LIS"));
      Lib.writeStringToFile(AFile.readFile(Activator.getInstance().getPluginFile(
            "support/vcastData/vcast/test_main.2.xml")), OseeData.getFile("vcast.wrk/vcast/test_main.2.xml"));
      Lib.writeStringToFile(AFile.readFile(Activator.getInstance().getPluginFile(
            "support/vcastData/vcast/test_scheduler.2.LIS")), OseeData.getFile("vcast.wrk/vcast/test_scheduler.2.LIS"));
      Lib.writeStringToFile(AFile.readFile(Activator.getInstance().getPluginFile(
            "support/vcastData/vcast/test_scheduler.2.xml")), OseeData.getFile("vcast.wrk/vcast/test_scheduler.2.xml"));
      OseeData.getFolder("vcast.wrk/vcast/results").getLocation().toFile();
      Lib.writeStringToFile(AFile.readFile(Activator.getInstance().getPluginFile(
            "support/vcastData/vcast/results/test_unit_1")), OseeData.getFile("vcast.wrk/vcast/results/test_unit_1"));
      Lib.writeStringToFile(AFile.readFile(Activator.getInstance().getPluginFile(
            "support/vcastData/vcast/results/test_unit_2")), OseeData.getFile("vcast.wrk/vcast/results/test_unit_2"));
      Lib.writeStringToFile(AFile.readFile(Activator.getInstance().getPluginFile(
            "support/vcastData/vcast/results/test_unit_3")), OseeData.getFile("vcast.wrk/vcast/results/test_unit_3"));
   }

}
