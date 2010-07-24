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
package org.eclipse.osee.coverage.test.model;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.merge.IMergeItem;
import org.eclipse.osee.coverage.merge.MergeImportManager;
import org.eclipse.osee.coverage.merge.MergeItem;
import org.eclipse.osee.coverage.merge.MergeManager;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoverageOptionManager;
import org.eclipse.osee.coverage.model.CoverageOptionManagerDefault;
import org.eclipse.osee.coverage.model.CoveragePackage;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.coverage.model.SimpleCoverageUnitFileContentsProvider;
import org.eclipse.osee.coverage.model.SimpleTestUnitProvider;
import org.eclipse.osee.coverage.store.CoverageArtifactTypes;
import org.eclipse.osee.coverage.store.DbTestUnitProvider;
import org.eclipse.osee.coverage.store.OseeCoveragePackageStore;
import org.eclipse.osee.coverage.store.OseeCoverageUnitStore;
import org.eclipse.osee.coverage.store.TestUnitStore;
import org.eclipse.osee.coverage.test.SampleJavaFileParser;
import org.eclipse.osee.coverage.test.import1.CoverageImport1TestBlam;
import org.eclipse.osee.coverage.test.util.CoverageTestUtil;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.coverage.util.ISaveable;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class CoverageUnitPersistTest {
   public static CoverageUnit parentCu = null;
   public static CoverageItem ci = null;
   public static String parentGuid = null;
   public static String guid = null;
   public static String PATH = "../../../../../../../src/org/eclipse/osee/coverage/test/import1/";
   public static CoverageImport coverageImport;
   public static CoveragePackage saveCoveragePackage;
   public static CoveragePackage loadCoveragePackage;

   @BeforeClass
   @AfterClass
   public static void testCleanup() throws OseeCoreException {
      CoverageUtil.setNavigatorSelectedBranch(BranchManager.getCommonBranch());
      CoverageTestUtil.cleanupCoverageTests();
   }

   @BeforeClass
   public static void testSetup() throws OseeCoreException {
      Assert.assertEquals(0, CoverageTestUtil.getAllCoverageArtifacts().size());

      coverageImport = new CoverageImport("CU Test");
      coverageImport.setCoverageUnitFileContentsProvider(new SimpleCoverageUnitFileContentsProvider());
      try {
         for (String filename : Arrays.asList(
         //
            "com/screenA/ComScrnAButton1.java", "com/screenA/ComScrnAButton2.java",
            //
            "com/screenB/ScreenBButton1.java", "com/screenB/ScreenBButton2.java", "com/screenB/ScreenBButton3.java"
         //
         )) {
            System.err.println(String.format("Importing [%s]", PATH + filename));
            URL url = CoverageImport1TestBlam.class.getResource(PATH + filename);
            CoverageUnit coverageUnit =
               SampleJavaFileParser.createCodeUnit(url, coverageImport.getCoverageUnitFileContentsProvider());
            String namespace = coverageUnit.getNamespace().replaceFirst("org.eclipse.osee.coverage.test.import1.", "");
            coverageUnit.setNamespace(namespace);
            CoverageUnit parentCoverageUnit = coverageImport.getOrCreateParent(namespace);
            if (parentCoverageUnit != null) {
               parentCoverageUnit.addCoverageUnit(coverageUnit);
            } else {
               coverageImport.addCoverageUnit(coverageUnit);
            }
         }
         coverageImport.setLocation(PATH);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Test
   public void testSaveLoadDelete() throws OseeCoreException {
      // TEST SAVE
      saveCoveragePackage = new CoveragePackage("CU Test", CoverageOptionManagerDefault.instance());
      MergeManager mergeManager = new MergeManager(saveCoveragePackage, coverageImport);
      List<IMergeItem> mergeItems = new ArrayList<IMergeItem>();
      for (IMergeItem mergeItem : mergeManager.getMergeItems()) {
         ((MergeItem) mergeItem).setChecked(true);
      }
      MergeImportManager importer = new MergeImportManager(mergeManager);
      importer.importItems(new ISaveable() {

         @Override
         public Result save() throws OseeCoreException {
            OseeCoveragePackageStore store =
               OseeCoveragePackageStore.get(saveCoveragePackage, BranchManager.getCommonBranch());
            store.save();
            Artifact artifact = store.getArtifact(false);
            CoverageTestUtil.registerAsTestArtifact(artifact, true);
            artifact.persist();
            return Result.TrueResult;
         }

         @Override
         public Result isEditable() {
            return Result.TrueResult;
         }

         @Override
         public Result save(Collection<ICoverage> coverages) throws OseeCoreException {
            return Result.TrueResult;
         }

         @Override
         public Result saveImportRecord(SkynetTransaction transaction, CoverageImport coverageImport) throws OseeCoreException {
            return null;
         }

         @Override
         public Branch getBranch() throws OseeCoreException {
            return BranchManager.getCommonBranch();
         }
      }, mergeItems);

      // TEST LOAD
      Artifact artifact =
         ArtifactQuery.getArtifactFromTypeAndName(CoverageArtifactTypes.CoveragePackage, "CU Test",
            BranchManager.getCommonBranch());
      loadCoveragePackage = OseeCoveragePackageStore.get(artifact);
      Assert.assertEquals(saveCoveragePackage.getName(), loadCoveragePackage.getName());
      Assert.assertEquals(saveCoveragePackage.getNamespace(), loadCoveragePackage.getNamespace());
      Assert.assertEquals(saveCoveragePackage.getCoverageItems().size(), loadCoveragePackage.getCoverageItems().size());
      Assert.assertEquals(saveCoveragePackage.getChildren(false).size(), loadCoveragePackage.getChildren(false).size());
      Assert.assertEquals(saveCoveragePackage.getChildren(true).size(), loadCoveragePackage.getChildren(true).size());
      Assert.assertEquals(saveCoveragePackage.getCoveragePercentStr(), loadCoveragePackage.getCoveragePercentStr());

      // TEST DELETE
      artifact =
         ArtifactQuery.getArtifactFromTypeAndName(CoverageArtifactTypes.CoveragePackage, "CU Test",
            BranchManager.getCommonBranch());
      Assert.assertNotNull(artifact);
      OseeCoveragePackageStore store = new OseeCoveragePackageStore(artifact);
      store.delete(false);
      try {
         artifact =
            ArtifactQuery.getArtifactFromTypeAndName(CoverageArtifactTypes.CoveragePackage, "CU Test",
               BranchManager.getCommonBranch());
         Assert.assertNotNull("CU Test should not have been found", artifact);
      } catch (ArtifactDoesNotExist ex) {
         //do nothing
      }
   }

   /**
    * Test that a coverage item that has a simpletestunitprovider, as imports will, will covert over and use the
    * DbTestUnitProvider when the item is persisted. Then, when re-loaded, will load back properly using
    * DbTestUnitProvider
    */
   @Test
   public void testSimpleToDbTestUnitProvider() throws OseeCoreException {
      TestUnitStore.clearStore();
      String cuName = DbTestUnitProviderTest.class.getSimpleName() + "-" + GUID.create();
      CoverageUnit unit = new CoverageUnit(null, cuName, "location", new SimpleCoverageUnitFileContentsProvider());
      CoverageItem item = new CoverageItem(unit, CoverageOptionManager.Test_Unit, "1");
      item.setTestUnitProvider(new SimpleTestUnitProvider());
      for (int x = 0; x < 10; x++) {
         item.addTestUnitName("Test Unit " + x);
      }
      Assert.assertEquals(10, item.getTestUnits().size());
      OseeCoverageUnitStore store = new OseeCoverageUnitStore(unit, BranchManager.getCommonBranch());
      Result result = store.save();
      Assert.assertTrue(result.isTrue());

      Artifact artifact =
         ArtifactQuery.getArtifactFromTypeAndName(CoverageArtifactTypes.CoverageUnit, cuName,
            BranchManager.getCommonBranch());
      Assert.assertNotNull(artifact);
      OseeCoverageUnitStore dbStore =
         new OseeCoverageUnitStore(null, artifact, CoverageOptionManagerDefault.instance());
      CoverageUnit dbUnit = dbStore.getCoverageUnit();
      Assert.assertEquals(1, dbUnit.getCoverageItems().size());
      CoverageItem dbItem = dbUnit.getCoverageItems().iterator().next();
      Assert.assertTrue(dbItem.getTestUnitProvider() instanceof DbTestUnitProvider);
      Assert.assertEquals(10, dbItem.getTestUnits().size());
      Assert.assertTrue(dbItem.getTestUnits().iterator().next().startsWith("Test Unit "));
      Assert.assertEquals(10, TestUnitStore.getTestUnitCount());
      TestUnitStore.clearStore();
   }
}
