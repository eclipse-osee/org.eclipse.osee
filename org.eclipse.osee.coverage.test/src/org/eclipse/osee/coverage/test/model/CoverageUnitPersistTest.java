/*
 * Created on Oct 28, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.test.model;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.merge.MergeImportManager;
import org.eclipse.osee.coverage.merge.IMergeItem;
import org.eclipse.osee.coverage.merge.MergeItem;
import org.eclipse.osee.coverage.merge.MergeManager;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoveragePackage;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.store.OseeCoveragePackageStore;
import org.eclipse.osee.coverage.test.SampleJavaFileParser;
import org.eclipse.osee.coverage.test.import1.CoverageImport1TestBlam;
import org.eclipse.osee.coverage.test.util.CoverageTestUtil;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.coverage.util.ISaveable;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
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
      CoverageUtil.setBranch(BranchManager.getCommonBranch());
      CoverageTestUtil.cleanupCoverageTests();
   }

   @BeforeClass
   public static void testSetup() throws OseeCoreException {
      Assert.assertEquals(0, CoverageTestUtil.getAllCoverageArtifacts().size());

      coverageImport = new CoverageImport("CU Test");
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
            CoverageUnit coverageUnit = SampleJavaFileParser.createCodeUnit(url);
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
   public void testASave() {
      try {
         saveCoveragePackage = new CoveragePackage("CU Test");
         MergeManager mergeManager = new MergeManager(saveCoveragePackage, coverageImport);
         List<IMergeItem> mergeItems = new ArrayList<IMergeItem>();
         for (IMergeItem mergeItem : mergeManager.getMergeItems()) {
            ((MergeItem) mergeItem).setChecked(true);
         }
         MergeImportManager importer = new MergeImportManager(mergeManager);
         importer.importItems(new ISaveable() {

            @Override
            public Result save() throws OseeCoreException {
               OseeCoveragePackageStore store = new OseeCoveragePackageStore(saveCoveragePackage);
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
         }, mergeItems);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Test
   public void testBLoad() throws OseeCoreException {
      Artifact artifact =
            ArtifactQuery.getArtifactFromTypeAndName(OseeCoveragePackageStore.ARTIFACT_NAME, "CU Test",
                  CoverageUtil.getBranch());
      loadCoveragePackage = OseeCoveragePackageStore.get(artifact);
      Assert.assertEquals(saveCoveragePackage.getName(), loadCoveragePackage.getName());
      Assert.assertEquals(saveCoveragePackage.getNamespace(), loadCoveragePackage.getNamespace());
      Assert.assertEquals(saveCoveragePackage.getCoverageItems().size(), loadCoveragePackage.getCoverageItems().size());
      Assert.assertEquals(saveCoveragePackage.getChildren(false).size(), loadCoveragePackage.getChildren(false).size());
      Assert.assertEquals(saveCoveragePackage.getChildren(true).size(), loadCoveragePackage.getChildren(true).size());
      Assert.assertEquals(saveCoveragePackage.getCoveragePercentStr(), loadCoveragePackage.getCoveragePercentStr());
   }

   @Test
   public void testCDelete() throws OseeCoreException {
      Artifact artifact =
            ArtifactQuery.getArtifactFromTypeAndName(OseeCoveragePackageStore.ARTIFACT_NAME, "CU Test",
                  CoverageUtil.getBranch());
      Assert.assertNotNull(artifact);
      OseeCoveragePackageStore store = new OseeCoveragePackageStore(artifact);
      store.delete(false);
      try {
         artifact =
               ArtifactQuery.getArtifactFromTypeAndName(OseeCoveragePackageStore.ARTIFACT_NAME, "CU Test",
                     CoverageUtil.getBranch());
         Assert.assertNotNull("CU Test should not have been found", artifact);
      } catch (ArtifactDoesNotExist ex) {
         //do nothing
      }
   }
}
