/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.integration.tests.integration;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Collection;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.vcast.VCastClient;
import org.eclipse.osee.vcast.VCastDataStore;
import org.eclipse.osee.vcast.model.VCastBranchCoverage;
import org.eclipse.osee.vcast.model.VCastBranchData;
import org.eclipse.osee.vcast.model.VCastCoverageType;
import org.eclipse.osee.vcast.model.VCastFunction;
import org.eclipse.osee.vcast.model.VCastInstrumentedFile;
import org.eclipse.osee.vcast.model.VCastMcdcCoverage;
import org.eclipse.osee.vcast.model.VCastMcdcCoverageCondition;
import org.eclipse.osee.vcast.model.VCastMcdcCoveragePair;
import org.eclipse.osee.vcast.model.VCastMcdcCoveragePairRow;
import org.eclipse.osee.vcast.model.VCastMcdcData;
import org.eclipse.osee.vcast.model.VCastMcdcDataCondition;
import org.eclipse.osee.vcast.model.VCastProject;
import org.eclipse.osee.vcast.model.VCastProjectFile;
import org.eclipse.osee.vcast.model.VCastResult;
import org.eclipse.osee.vcast.model.VCastSetting;
import org.eclipse.osee.vcast.model.VCastSourceFile;
import org.eclipse.osee.vcast.model.VCastStatementCoverage;
import org.eclipse.osee.vcast.model.VCastStatementData;
import org.eclipse.osee.vcast.model.VCastVersion;
import org.eclipse.osee.vcast.model.VCastWritable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Shawn F. Cook
 */
public class VCastDataStoreTest {
   private static String sqliteUtilTestDbFileName = "vCastSqliteUtilityTest.db";
   private static VCastDataStore dataStore = null;

   @org.junit.Rule
   public TemporaryFolder tempFolder = new TemporaryFolder();

   @Before
   public void setUp() throws Exception {
      File outfile = tempFolder.newFile(sqliteUtilTestDbFileName);

      copyResource("vCastSqliteUtilityTest.db", outfile);

      dataStore = VCastClient.newDataStore(outfile.getAbsolutePath());
   }

   private static void copyResource(String resource, File output) throws IOException {
      Bundle bundle = FrameworkUtil.getBundle(VCastDataStoreTest.class);
      String fullPath = String.format("support/vcastData/%s", resource);
      URL input = bundle.getResource(fullPath);

      OutputStream outputStream = null;
      InputStream inputStream = null;
      try {
         outputStream = new BufferedOutputStream(new FileOutputStream(output));
         inputStream = new BufferedInputStream(input.openStream());
         Lib.inputStreamToOutputStream(inputStream, outputStream);
      } finally {
         Lib.close(inputStream);
         Lib.close(outputStream);
      }
   }

   @Test
   public void testGetAllBranchCoverages() throws Exception {
      Collection<VCastBranchCoverage> data = dataStore.getAllBranchCoverages();
      int i = 1;
      for (VCastBranchCoverage dataItem : data) {
         Assert.assertEquals(i, dataItem.getId());
         Assert.assertEquals(i, dataItem.getFunctionId());
         Assert.assertEquals(i, dataItem.getLine());
         Assert.assertEquals(i, dataItem.getNumConditions());
         Assert.assertEquals(i, dataItem.getTrueCount());
         Assert.assertEquals(i, dataItem.getFalseCount());
         Assert.assertEquals(i, dataItem.getMaxTrueCount());
         Assert.assertEquals(i, dataItem.getMaxFalseCount());
         i++;
      }
   }

   @Test
   public void testGetAllBranchData() throws Exception {
      Collection<VCastBranchData> data = dataStore.getAllBranchData();
      int i = 1;
      for (VCastBranchData dataItem : data) {
         Assert.assertEquals(i, dataItem.getId());
         Assert.assertEquals(i, dataItem.getBranchId());
         Assert.assertEquals(i, dataItem.getResultId());
         Assert.assertEquals(i, dataItem.getResultLine());
         Assert.assertTrue(dataItem.getTaken().booleanValue());
         i++;
      }
   }

   @Test
   public void testGetAllFunctions() throws Exception {
      Collection<VCastFunction> data = dataStore.getAllFunctions();
      int i = 1;
      for (VCastFunction dataItem : data) {
         Assert.assertEquals(i, dataItem.getId());
         Assert.assertEquals(i, dataItem.getInstrumentedFileId());
         Assert.assertEquals(i, dataItem.getFindex());
         Assert.assertTrue(dataItem.getName().equalsIgnoreCase("name" + i));
         Assert.assertTrue(dataItem.getCanonicalName().equalsIgnoreCase("canonical_name" + i));
         Assert.assertEquals(i, dataItem.getTotalLines());
         Assert.assertEquals(i, dataItem.getComplexity());
         Assert.assertEquals(i, dataItem.getNumPairsOrPaths());
         i++;
      }
   }

   @Test
   public void testGetAllInstrumentedFiles() throws Exception {
      Collection<VCastInstrumentedFile> data = dataStore.getAllInstrumentedFiles();
      int i = 1;
      for (VCastInstrumentedFile dataItem : data) {
         Assert.assertEquals(i, dataItem.getId());
         Assert.assertEquals(i, dataItem.getSourceFileId());
         Assert.assertEquals(i, dataItem.getProjectId());
         Assert.assertEquals(i, dataItem.getUnitIndex());
         Assert.assertEquals(VCastCoverageType.STATEMENT, dataItem.getCoverageType());
         Assert.assertTrue(dataItem.getLISFile().equalsIgnoreCase("LIS_file" + i));
         Assert.assertEquals(i, dataItem.getChecksum());
         i++;
      }
   }

   @Test
   public void testGetAllMcdcCoverages() throws Exception {
      Collection<VCastMcdcCoverage> data = dataStore.getAllMcdcCoverages();
      int i = 1;
      for (VCastMcdcCoverage dataItem : data) {
         Assert.assertEquals(i, dataItem.getId());
         Assert.assertEquals(i, dataItem.getFunctionId());
         Assert.assertEquals(i, dataItem.getLine());
         Assert.assertEquals(i, dataItem.getSourceLine());
         Assert.assertEquals(i, dataItem.getNumConditions());
         Assert.assertTrue(dataItem.getActualExpr().equalsIgnoreCase("actual_expr" + i));
         Assert.assertTrue(dataItem.getSimplifiedExpr().equalsIgnoreCase("simplified_expr" + i));
         i++;
      }
   }

   @Test
   public void testGetAllMcdcCoverageConditions() throws Exception {
      Collection<VCastMcdcCoverageCondition> data = dataStore.getAllMcdcCoverageConditions();
      int i = 1;
      for (VCastMcdcCoverageCondition dataItem : data) {
         Assert.assertEquals(i, dataItem.getId());
         Assert.assertEquals(i, dataItem.getMcdcId());
         Assert.assertEquals(i, dataItem.getCondIndex());
         Assert.assertEquals(i, dataItem.getTrueCount());
         Assert.assertEquals(i, dataItem.getFalseCount());
         Assert.assertEquals(i, dataItem.getMaxTrueCount());
         Assert.assertEquals(i, dataItem.getMaxFalseCount());
         Assert.assertTrue(dataItem.getCondVariable().equalsIgnoreCase("cond_variable" + i));
         Assert.assertTrue(dataItem.getCondExpr().equalsIgnoreCase("cond_expr" + i));
         i++;
      }
   }

   @Test
   public void testGetAllMcdcCoveragePairRows() throws Exception {
      Collection<VCastMcdcCoveragePairRow> data = dataStore.getAllMcdcCoveragePairRows();
      int i = 1;
      for (VCastMcdcCoveragePairRow dataItem : data) {
         Assert.assertEquals(i, dataItem.getId());
         Assert.assertEquals(i, dataItem.getMcdcId());
         Assert.assertEquals(i, dataItem.getRowValue());
         Assert.assertEquals(i, dataItem.getRowResult());
         Assert.assertEquals(i, dataItem.getHitCount());
         Assert.assertEquals(i, dataItem.getMaxHitCount());
         i++;
      }
   }

   @Test
   public void testGetAllMcdcCoveragePairs() throws Exception {
      Collection<VCastMcdcCoveragePair> data = dataStore.getAllMcdcCoveragePairs();
      int i = 1;
      for (VCastMcdcCoveragePair dataItem : data) {
         Assert.assertEquals(i, dataItem.getId());
         Assert.assertEquals(i, dataItem.getMcdcCondId());
         Assert.assertEquals(i, dataItem.getPairRow1());
         Assert.assertEquals(i, dataItem.getPairRow2());
         i++;
      }
   }

   @Test
   public void testGetAllMcdcData() throws Exception {
      Collection<VCastMcdcData> data = dataStore.getAllMcdcData();
      int i = 1;
      for (VCastMcdcData dataItem : data) {
         Assert.assertEquals(i, dataItem.getId());
         Assert.assertEquals(i, dataItem.getMcdcId());
         Assert.assertEquals(i, dataItem.getResultId());
         Assert.assertEquals(i, dataItem.getPairValue());
         Assert.assertEquals(i, dataItem.getUsedValue());
         i++;
      }
   }

   @Test
   public void testGetAllMcdcDataConditions() throws Exception {
      Collection<VCastMcdcDataCondition> data = dataStore.getAllMcdcDataConditions();
      int i = 1;
      for (VCastMcdcDataCondition dataItem : data) {
         Assert.assertEquals(i, dataItem.getId());
         Assert.assertEquals(i, dataItem.getMcdcDataId());
         Assert.assertEquals(i, dataItem.getCondIndex());
         Assert.assertTrue(dataItem.getCondValue());
         i++;
      }
   }

   @Test
   public void testGetAllProjectFiles() throws Exception {
      Collection<VCastProjectFile> data = dataStore.getAllProjectFiles();
      int i = 1;
      for (VCastProjectFile dataItem : data) {
         Assert.assertEquals(i, dataItem.getProjectId());
         Assert.assertEquals(i, dataItem.getSourceFileId());
         Assert.assertEquals(i, dataItem.getInstrumentedFileId());
         Assert.assertEquals(i, dataItem.getTimestamp());
         Assert.assertTrue(dataItem.getBuildMd5Sum().equalsIgnoreCase("build_md5sum" + i));
         i++;
      }
   }

   @Test
   public void testGetAllProjects() throws Exception {
      Collection<VCastProject> data = dataStore.getAllProjects();
      int i = 1;
      for (VCastProject dataItem : data) {
         Assert.assertEquals(i, dataItem.getId());
         Assert.assertTrue(dataItem.getName().equalsIgnoreCase("name" + i));
         Assert.assertTrue(dataItem.getPath().equalsIgnoreCase("path" + i));
         i++;
      }
   }

   @Test
   public void testGetAllResults() throws Exception {
      Collection<VCastResult> data = dataStore.getAllResults();
      int i = 1;
      for (VCastResult dataItem : data) {
         Assert.assertEquals(i, dataItem.getId());
         Assert.assertTrue(dataItem.getName().equalsIgnoreCase("name" + i));
         Assert.assertEquals(i, dataItem.getProjectId());
         Assert.assertTrue(dataItem.getPath().equalsIgnoreCase("path" + i));
         Assert.assertTrue(dataItem.getFullname().equalsIgnoreCase("fullname" + i));
         Assert.assertTrue(dataItem.isEnabled());
         Assert.assertTrue(dataItem.isImported());
         i++;
      }
   }

   @Test
   public void testGetAllSettings() throws Exception {
      Collection<VCastSetting> data = dataStore.getAllSettings();
      int i = 1;
      for (VCastSetting dataItem : data) {
         Assert.assertTrue(dataItem.getSetting().equalsIgnoreCase("setting" + i));
         Assert.assertTrue(dataItem.getValue().equalsIgnoreCase("value" + i));
         i++;
      }
   }

   @Test
   public void testGetAllSourceFiles() throws Exception {
      Collection<VCastSourceFile> data = dataStore.getAllSourceFiles();
      int i = 1;
      for (VCastSourceFile dataItem : data) {
         Assert.assertEquals(i, dataItem.getId());
         Assert.assertTrue(dataItem.getPath().equalsIgnoreCase("path" + i));
         Assert.assertTrue(dataItem.getDisplayName().equalsIgnoreCase("display_name" + i));
         Assert.assertEquals(i, dataItem.getChecksum());
         Assert.assertTrue(dataItem.getDisplayPath().equalsIgnoreCase("display_path" + i));
         i++;
      }
   }

   @Test
   public void testGetAllStatementCoverages() throws Exception {
      Collection<VCastStatementCoverage> data = dataStore.getAllStatementCoverages();
      int i = 1;
      for (VCastStatementCoverage dataItem : data) {
         Assert.assertEquals(i, dataItem.getId());
         Assert.assertEquals(i, dataItem.getFunctionId());
         Assert.assertEquals(i, dataItem.getLine());
         Assert.assertEquals(i, dataItem.getHitCount());
         Assert.assertEquals(i, dataItem.getMaxHitCount());
         i++;
      }
   }

   @Test
   public void testGetAllStatementData() throws Exception {
      Collection<VCastStatementData> data = dataStore.getAllStatementData();
      int i = 1;
      for (VCastStatementData dataItem : data) {
         Assert.assertEquals(i, dataItem.getId());
         Assert.assertEquals(i, dataItem.getStatementId());
         Assert.assertEquals(i, dataItem.getResultId());
         Assert.assertEquals(i, dataItem.getResultLine());
         Assert.assertTrue(dataItem.getHit());
         i++;
      }
   }

   @Test
   public void testGetVersion() throws Exception {
      VCastVersion version = dataStore.getVersion();
      Assert.assertEquals(1, version.getVersion());
      Assert.assertTrue(version.getDateCreated().equalsIgnoreCase("date_created1"));
   }

   @Test
   public void testGetWritable() throws Exception {
      VCastWritable writable = dataStore.getWritable();
      Assert.assertEquals(1, writable.getIsWritable());
   }
}
