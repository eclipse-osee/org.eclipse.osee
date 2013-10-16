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
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeHousekeepingRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.coverage.event.CoverageEventType;
import org.eclipse.osee.coverage.event.CoveragePackageEvent;
import org.eclipse.osee.coverage.integration.tests.integration.util.CoverageTestUtil;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.coverage.model.CoverageOptionManager;
import org.eclipse.osee.coverage.model.CoverageOptionManagerDefault;
import org.eclipse.osee.coverage.model.CoveragePackage;
import org.eclipse.osee.coverage.model.SimpleWorkProductTaskProvider;
import org.eclipse.osee.coverage.store.OseeCoveragePackageStore;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.coverage.vcast.CoverageImportData;
import org.eclipse.osee.coverage.vcast.CoverageImportFactory;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
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
public class VCastAdaCoverage_V5_3_ImportOperationTest {

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

   @BeforeClass
   public static void setUp() throws OseeCoreException {
      CoverageUtil.setIsInTest(true);
      testCleanup();
   }

   @AfterClass
   public static void cleanUp() throws OseeCoreException {
      try {
         testCleanup();
      } finally {
         CoverageUtil.setIsInTest(false);
      }
   }

   private static void testCleanup() throws OseeCoreException {
      CoverageUtil.setNavigatorSelectedBranch(CoverageTestUtil.getTestBranch());
      CoverageTestUtil.cleanupCoverageTests();
   }

   private void createVCastFileset() throws IOException {
      Bundle bundle = FrameworkUtil.getBundle(this.getClass());

      //@formatter:off
      File rootFolder = tempFolder.getRoot();
      copyResource(getURL(bundle, "vcast.vcp"), rootFolder, "vcast.vcp");
      copyResource(getURL(bundle, "CCAST_.CFG"), rootFolder, "CCAST_.CFG");

      File vcastFolder = tempFolder.newFolder("vcast");
      copyResource(getURL(bundle, "vcast_aggregate_coverage_report.html"), vcastFolder, "vcast_aggregate_coverage_report.html");
      copyResource(getURL(bundle, "vcast/build_info.xml"), vcastFolder, "build_info.xml");
      copyResource(getURL(bundle, "vcast/test_main.2.LIS"), vcastFolder, "test_main.2.LIS");
      copyResource(getURL(bundle, "vcast/test_main.2.xml"), vcastFolder, "test_main.2.xml");
      copyResource(getURL(bundle, "vcast/test_scheduler.2.LIS"), vcastFolder, "test_scheduler.2.LIS");
      copyResource(getURL(bundle, "vcast/test_scheduler.2.xml"), vcastFolder, "test_scheduler.2.xml");

      File resultsFolder = tempFolder.newFolder("vcast/results");
      copyResource(getURL(bundle, "vcast/results/test_unit_1.dat"), resultsFolder, "test_unit_1.dat");
      copyResource(getURL(bundle, "vcast/results/test_unit_2.dat"), resultsFolder, "test_unit_2.dat");
      copyResource(getURL(bundle, "vcast/results/test_unit_3.dat"), resultsFolder, "test_unit_3.dat");
      //@formatter:on
   }

   private URL getURL(Bundle bundle, String resource) {
      String fullPath = String.format("support/vcastData/%s", resource);
      return bundle.getEntry(fullPath);
   }

   private static void copyResource(URL source, File folder, String destinationName) throws IOException {
      OutputStream outputStream = null;
      InputStream inputStream = null;
      try {
         inputStream = new BufferedInputStream(source.openStream());

         File destination = new File(folder, destinationName);
         outputStream = new BufferedOutputStream(new FileOutputStream(destination));
         Lib.inputStreamToOutputStream(inputStream, outputStream);
      } finally {
         Lib.close(inputStream);
         Lib.close(outputStream);
      }
   }

   @Test
   public void testAdaVCast53ImportOp() throws Exception {
      createVCastFileset();

      String coverageInputDir = tempFolder.getRoot().getAbsolutePath();
      CoverageImportData params = new CoverageImportData(coverageInputDir, false) {

         @Override
         public String getFileNamespace(String filename) {
            return "test";
         }
      };
      CoverageImport coverageImport = new CoverageImport("VectorCast Import");

      IOperation operation = CoverageImportFactory.createAdaVCast53ImportOp(params, coverageImport);
      Operations.executeWorkAndCheckStatus(operation);

      Assert.assertFalse(coverageImport.getLog().isErrors());

      CoverageOptionManager optionManager = CoverageOptionManagerDefault.instance();
      CoveragePackage coveragePackage =
         new CoveragePackage("Test Coverage Package", optionManager, new SimpleWorkProductTaskProvider());

      OseeCoveragePackageStore store = OseeCoveragePackageStore.get(coveragePackage, CoverageTestUtil.getTestBranch());
      SkynetTransaction transaction =
         TransactionManager.createTransaction(CoverageTestUtil.getTestBranch(),
            String.format("%s: %s.%s", getClass().getSimpleName(), "Coverage Package Save ", testName.getMethodName()));
      CoveragePackageEvent coverageEvent = new CoveragePackageEvent(coveragePackage, CoverageEventType.Modified);
      store.save(transaction, coverageEvent, coveragePackage.getCoverageOptionManager());
      store.getArtifact(false).persist(transaction);

      // Test Load of Coverage Package
      Artifact artifact = ArtifactQuery.getArtifactFromId(coveragePackage.getGuid(), CoverageTestUtil.getTestBranch());
      CoverageTestUtil.registerAsTestArtifact(artifact);
      artifact.persist(transaction);

      OseeCoveragePackageStore packageStore = new OseeCoveragePackageStore(artifact);
      Assert.assertNotNull(packageStore.getArtifact(false));
      packageStore.getArtifact(false).persist(transaction);
      transaction.execute();

      store = OseeCoveragePackageStore.get(coveragePackage, CoverageTestUtil.getTestBranch());
      String txComment =
         String.format("Save Import Record: %s.%s", getClass().getSimpleName(), testName.getMethodName());
      transaction = TransactionManager.createTransaction(CoverageTestUtil.getTestBranch(), txComment);
      Result result = store.saveImportRecord(transaction, coverageImport);
      Assert.assertTrue(result.isTrue());
      transaction.execute();

      Artifact packageArt =
         ArtifactQuery.getArtifactFromId(coveragePackage.getGuid(), CoverageTestUtil.getTestBranch());
      Artifact foundRecordArt = null;
      for (Artifact art : packageArt.getChildren()) {
         if (art.getName().equals(OseeCoveragePackageStore.IMPORT_RECORD_NAME)) {
            foundRecordArt = art;
            CoverageTestUtil.registerAsTestArtifact(foundRecordArt);
            foundRecordArt.persist(getClass().getSimpleName());
         }
      }
      Assert.assertNotNull(foundRecordArt);
      Assert.assertEquals("General Document", foundRecordArt.getArtifactTypeName());
   }

}
