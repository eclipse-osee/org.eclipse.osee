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

import static org.eclipse.osee.client.demo.DemoBranches.SAW_Bld_1;
import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.client.demo.DemoTraceability;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.client.test.framework.TestInfo;
import org.eclipse.osee.define.traceability.operations.ImportTraceUnitsOperation;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Compare;
import org.eclipse.osee.framework.jdk.core.util.GUID;
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

   private static final String TEST_ONE_FILE = "ImportTraceUnitsTest1.txt";
   private static final String TEST_TWO_FILE = "ImportTraceUnitsTest2.txt";
   private static final String TEST_THREE_FILE = "ImportTraceUnitsTest3.txt";
   private static final String TEST_FOUR_FILE = "ImportTraceUnitsTest4.txt";
   private static final String TEST_FIVE_FILE = "ImportTraceUnitsTest5.txt";
   private static final int RUNS = 3;

   private static final List<String> expectedReqs = Arrays.asList("Robot Object", "Haptic Constraints",
      "Robot Interfaces", "Individual robot events", "Collaborative Robot");

   private IOseeBranch branch;
   private Branch importToBranch;

   @Before
   public void setup() throws Exception {
      branch = TokenFactory.createBranch( testInfo.getQualifiedTestName());
      importToBranch = BranchManager.createWorkingBranch(SAW_Bld_1, branch);
   }

   @After
   public void tearDown() throws OseeCoreException {
      if (importToBranch != null) {
         BranchManager.purgeBranch(importToBranch);
      }
   }

   @Test
   public void testImportTraceUnitsJob() throws Exception {
      URI mockURI1 = getMockFile(TEST_ONE_FILE, "some text");
      URI mockURI2 = getMockFile(TEST_TWO_FILE, "some text");

      ArrayList<Integer> gammas = new ArrayList<Integer>(RUNS);

      for (int i = 0; i < RUNS; i++) {
         runOperation(Arrays.asList(mockURI1));

         Artifact artifact =
            ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.CodeUnit, TEST_ONE_FILE, importToBranch);
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
      // make sure multiple artifacts were not created
      Assert.assertEquals(1, gammas.size());

      // create a 2nd artifact
      runOperation(Arrays.asList(mockURI2));
      Artifact artifact =
         ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.CodeUnit, TEST_TWO_FILE, importToBranch);
      Assert.assertNotNull(artifact);
      // make sure a new artifact was created
      Assert.assertFalse(gammas.contains(artifact.getGammaId()));
   }

   @Test
   public void testMultipleUris() throws Exception {
      URI mockURI3 = getMockFile(TEST_THREE_FILE, "some text");
      URI mockURI4 = getMockFile(TEST_FOUR_FILE, "some text");
      URI mockURI5 = getMockFile(TEST_FIVE_FILE, "some text");

      Iterable<URI> uris = Arrays.asList(mockURI3, mockURI4, mockURI5);
      runOperation(uris);

      for (String fileName : Arrays.asList(TEST_THREE_FILE, TEST_FOUR_FILE, TEST_FIVE_FILE)) {
         Artifact artifact =
            ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.CodeUnit, fileName, importToBranch);
         Assert.assertNotNull(artifact);
         List<Artifact> reqArtifacts = artifact.getRelatedArtifacts(CoreRelationTypes.CodeRequirement_Requirement);
         Assert.assertEquals(5, reqArtifacts.size());

         Collection<String> actual = Artifacts.getNames(reqArtifacts);

         Assert.assertFalse(Compare.isDifferent(expectedReqs, actual));
      }

   }

   private URI getMockFile(String fileName, String text) throws Exception {
      File testFile = tempFolder.newFile(fileName);
      Lib.writeStringToFile(text, testFile);
      return testFile.toURI();
   }

   private void runOperation(Iterable<URI> files) throws OseeCoreException {
      boolean isRecursive = false;
      boolean isPersistChanges = true;
      boolean fileWithMultiPaths = false;

      IOperation op =
         new ImportTraceUnitsOperation("Import Trace Units", importToBranch, files, isRecursive, isPersistChanges,
            fileWithMultiPaths, DemoTraceability.DEMO_TRACE_UNIT_HANDLER_ID);
      Operations.executeWorkAndCheckStatus(op);
   }
}
