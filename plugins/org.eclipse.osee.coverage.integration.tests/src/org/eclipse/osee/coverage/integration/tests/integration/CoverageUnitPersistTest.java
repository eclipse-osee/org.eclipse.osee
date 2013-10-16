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

import static org.eclipse.osee.coverage.demo.CoverageChoice.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeHousekeepingRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.coverage.integration.tests.integration.util.CoverageTestUtil;
import org.eclipse.osee.coverage.merge.IMergeItem;
import org.eclipse.osee.coverage.merge.MergeImportManager;
import org.eclipse.osee.coverage.merge.MergeManager;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoverageOptionManager;
import org.eclipse.osee.coverage.model.CoverageOptionManagerDefault;
import org.eclipse.osee.coverage.model.CoveragePackage;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.coverage.model.SimpleWorkProductTaskProvider;
import org.eclipse.osee.coverage.store.CoverageArtifactTypes;
import org.eclipse.osee.coverage.store.OseeCoveragePackageStore;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.coverage.util.ISaveable;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
@Ignore("fix me")
public class CoverageUnitPersistTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_COVERAGE_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Rule
   public OseeHousekeepingRule hk = new OseeHousekeepingRule();

   public static CoverageUnit parentCu = null;
   public static CoverageItem ci = null;
   public static String parentGuid = null;
   public static String guid = null;
   public static String PATH = "../../../../../../src/org/eclipse/osee/coverage/import01/";
   public static CoverageImport coverageImport;
   public static CoveragePackage saveCoveragePackage;
   public static CoveragePackage loadCoveragePackage;

   @BeforeClass
   @AfterClass
   public static void testCleanup() throws OseeCoreException {
      CoverageUtil.setNavigatorSelectedBranch(CoverageTestUtil.getTestBranch());
      CoverageTestUtil.cleanupCoverageTests();
   }

   //   @BeforeClass
   //   public static void testSetup() throws OseeCoreException {
   //      Assert.assertEquals(0, CoverageTestUtil.getAllCoverageArtifacts().size());
   //
   //      coverageImport = new CoverageImport("CU Test");
   //      coverageImport.setCoverageUnitFileContentsProvider(new SimpleCoverageUnitFileContentsProvider());
   //      for (String filename : Arrays.asList(
   //      //
   //         "com/screenA/ComScrnAButton1.java", "com/screenA/ComScrnAButton2.java",
   //         //
   //         "com/screenB/ScreenBButton1.java", "com/screenB/ScreenBButton2.java", "com/screenB/ScreenBButton3.java"
   //      //
   //      )) {
   //         // System.err.println(String.format("Importing [%s]", PATH + filename));
   //         URL url = CoverageImport1TestBlam.class.getResource(PATH + filename);
   //         CoverageUnit coverageUnit =
   //            SampleJavaFileParser.createCodeUnit(url, coverageImport.getCoverageUnitFileContentsProvider());
   //         String namespace = coverageUnit.getNamespace().replaceFirst("org.eclipse.osee.coverage.import01.", "");
   //         coverageUnit.setNamespace(namespace);
   //         CoverageUnit parentCoverageUnit = coverageImport.getOrCreateParent(namespace);
   //         if (parentCoverageUnit != null) {
   //            parentCoverageUnit.addCoverageUnit(coverageUnit);
   //         } else {
   //            coverageImport.addCoverageUnit(coverageUnit);
   //         }
   //      }
   //      coverageImport.setLocation(PATH);
   //   }

   @Test
   public void testSaveLoadDelete() throws OseeCoreException {
      // TEST SAVE
      saveCoveragePackage =
         new CoveragePackage("CU Test", CoverageOptionManagerDefault.instance(), new SimpleWorkProductTaskProvider());
      MergeManager mergeManager = new MergeManager(saveCoveragePackage, coverageImport);
      List<IMergeItem> mergeItems = new ArrayList<IMergeItem>();
      for (IMergeItem mergeItem : mergeManager.getMergeItems(null)) {
         mergeItem.setChecked(true);
      }
      MergeImportManager importer = new MergeImportManager(mergeManager);
      importer.importItems(new ISaveable() {

         @Override
         public Result save(String saveName, CoverageOptionManager coverageOptionManager) throws OseeCoreException {
            OseeCoveragePackageStore store =
               OseeCoveragePackageStore.get(saveCoveragePackage, CoverageTestUtil.getTestBranch());
            store.save(saveCoveragePackage.getName(), saveCoveragePackage.getCoverageOptionManager());
            Artifact artifact = store.getArtifact(false);
            CoverageTestUtil.registerAsTestArtifact(artifact, true);
            artifact.persist(getClass().getSimpleName());
            return Result.TrueResult;
         }

         @Override
         public Result isEditable() {
            return Result.TrueResult;
         }

         @Override
         public Result save(Collection<ICoverage> coverages, String saveName) {
            return Result.TrueResult;
         }

         @Override
         public Result saveImportRecord(SkynetTransaction transaction, CoverageImport coverageImport) {
            return null;
         }

         @Override
         public IOseeBranch getBranch() {
            return CoverageTestUtil.getTestBranch();
         }
      }, mergeItems);

      // TEST LOAD
      Artifact artifact =
         ArtifactQuery.getArtifactFromTypeAndName(CoverageArtifactTypes.CoveragePackage, "CU Test",
            CoverageTestUtil.getTestBranch());
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
            CoverageTestUtil.getTestBranch());
      Assert.assertNotNull(artifact);
      OseeCoveragePackageStore store = new OseeCoveragePackageStore(artifact);
      store.delete(false, loadCoveragePackage.getName());
      try {
         artifact =
            ArtifactQuery.getArtifactFromTypeAndName(CoverageArtifactTypes.CoveragePackage, "CU Test",
               CoverageTestUtil.getTestBranch());
         Assert.assertNotNull("CU Test should not have been found", artifact);
      } catch (ArtifactDoesNotExist ex) {
         //do nothing
      }
   }

}
