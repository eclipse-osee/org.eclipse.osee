/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.client.integration.tests.integration.define;

import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IPath;
import org.eclipse.osee.client.demo.DemoTraceability;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.client.test.framework.TestInfo;
import org.eclipse.osee.define.traceability.operations.ImportTraceUnitsOperation;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Compare;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author John R. Misinco
 */
public final class ImportTraceUnitsOperationTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Rule
   public TestInfo testInfo = new TestInfo();

   @Rule
   public TemporaryFolder tempFolder = new TemporaryFolder();
   private static final String topLevelFolderName = "topLevelFolder";

   private static final String TEST_ONE_FOLDER_NAME = "abc.ss";
   private static final String TEST_TWO_FOLDER_NAME = "def.ss";
   private static final String TEST_THREE_FOLDER_NAME = "ryan.ss";
   private static final String TEST_FOUR_FOLDER_NAME = "jason.ss";
   private static final String TEST_FIVE_FOLDER_NAME = "megumi.ss";

   private static final String TEST_ONE_FILE = "ImportTraceUnitsTest1.txt";
   private static final String TEST_TWO_FILE = "ImportTraceUnitsTest2.txt";
   private static final String TEST_THREE_FILE = "ImportTraceUnitsTest3.txt";
   private static final String TEST_FOUR_FILE = "ImportTraceUnitsTest4.txt";
   private static final String TEST_FIVE_FILE = "ImportTraceUnitsTest5.txt";

   private static final int RUNS = 3;

   private final String[] fileNames = {TEST_ONE_FILE, TEST_TWO_FILE, TEST_THREE_FILE, TEST_FOUR_FILE, TEST_FIVE_FILE};
   private final String[] folderNames = {
      TEST_ONE_FOLDER_NAME,
      TEST_TWO_FOLDER_NAME,
      TEST_THREE_FOLDER_NAME,
      TEST_FOUR_FOLDER_NAME,
      TEST_FIVE_FOLDER_NAME};

   private static final List<String> expectedReqs = Arrays.asList("Robot Object", "Haptic Constraints",
      "Robot Interfaces", "Individual robot events", "Collaborative Robot");

   private IOseeBranch branch;
   private BranchId importToBranch;
   private File testFile;

   @Before
   public void setup() throws Exception {
      branch = IOseeBranch.create(testInfo.getQualifiedTestName());
      importToBranch = BranchManager.createWorkingBranch(SAW_Bld_1, branch);

      setupDirectoryStructure();
   }

   @After
   public void tearDown() throws OseeCoreException {
      if (importToBranch != null) {
         BranchManager.purgeBranch(importToBranch);
      }
   }

   @Test
   public void testImportTraceUnitsJob() throws Exception {

      ArrayList<Integer> gammas = new ArrayList<>(RUNS);

      for (int i = 0; i < RUNS; i++) {
         runOperation(Arrays.asList(testFile.toURI()));

         for (int j = 0; j < fileNames.length; j++) {
            Artifact artifact =
               ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.CodeUnit, fileNames[j], importToBranch);
            Assert.assertNotNull(artifact);

            Integer gamma = artifact.getGammaId();
            if (!gammas.contains(gamma)) {
               gammas.add(gamma);
            }

            Assert.assertEquals("Code Units", artifact.getParent().getName());

            List<Artifact> reqArtifacts = artifact.getRelatedArtifacts(CoreRelationTypes.CodeRequirement_Requirement);
            Assert.assertEquals(5, reqArtifacts.size());

            Collection<String> actual = Artifacts.getNames(reqArtifacts);

            Assert.assertFalse(Compare.isDifferent(expectedReqs, actual));
         }
      }
   }

   private void setupDirectoryStructure() throws Exception {
      File topLevelFolder = new File(tempFolder.getRoot().getAbsolutePath() + IPath.SEPARATOR + topLevelFolderName);
      if (!topLevelFolder.exists()) {
         tempFolder.newFolder(topLevelFolderName);
      }
      for (String folderName : folderNames) {
         File tempFile = new File(
            tempFolder.getRoot().getAbsolutePath() + IPath.SEPARATOR + topLevelFolderName + IPath.SEPARATOR + folderName);
         if (!tempFile.exists()) {
            tempFolder.newFolder(topLevelFolderName, folderName);
         }
      }
      for (int i = 0; i < fileNames.length; i++) {
         File codeUnitFile = new File(
            tempFolder.getRoot().getAbsolutePath() + IPath.SEPARATOR + topLevelFolderName + IPath.SEPARATOR + folderNames[i] + IPath.SEPARATOR + fileNames[i]);
         if (!codeUnitFile.exists()) {
            codeUnitFile.createNewFile();
            Lib.writeStringToFile("Dummy String", codeUnitFile);
         }
      }

      testFile = new File(tempFolder.getRoot().getAbsolutePath() + IPath.SEPARATOR + "testFile.txt");
      if (!testFile.exists()) {
         testFile.createNewFile();
      }

      String pathNames = "";
      for (String folderName : folderNames) {
         Lib.writeStringToFile(
            pathNames + tempFolder.getRoot().getAbsolutePath() + IPath.SEPARATOR + topLevelFolderName + IPath.SEPARATOR + folderName + "\n",
            testFile);
         pathNames = Lib.fileToString(testFile);
      }
   }

   private void runOperation(Iterable<URI> files) throws OseeCoreException {
      boolean isRecursive = false;
      boolean isPersistChanges = true;
      boolean fileWithMultiPaths = true;
      boolean addGuidToSourceFile = false;
      boolean includeImpd = false;

      IOperation op =
         new ImportTraceUnitsOperation("Import Trace Units", importToBranch, files, isRecursive, isPersistChanges,
            fileWithMultiPaths, addGuidToSourceFile, includeImpd, DemoTraceability.DEMO_TRACE_UNIT_HANDLER_ID);
      Operations.executeWorkAndCheckStatus(op);
   }
}
