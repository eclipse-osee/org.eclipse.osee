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
package org.eclipse.osee.define.jobs;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.define.traceability.operations.ImportTraceUnitsOperation;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.util.Compare;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.support.test.util.DemoSawBuilds;
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

   private static final String TEST_ONE_FILE = "ImportTraceUnitsTest1.txt";
   private static final String TEST_TWO_FILE = "ImportTraceUnitsTest2.txt";
   private static final IOseeBranch TEST_BRANCH =
      TokenFactory.createBranch("BIkSWxVrZClFHss6FTAA", "Trace Unit Branch");
   private static final int RUNS = 3;

   private Branch importToBranch;

   @Rule
   public final TemporaryFolder tempFolder = new TemporaryFolder();

   private URI getMockFile(String fileName, String text) throws Exception {
      File testFile = tempFolder.newFile(fileName);
      Lib.writeStringToFile(text, testFile);
      return testFile.toURI();
   }

   @Before
   public void setup() throws OseeCoreException {
      if (BranchManager.branchExists(TEST_BRANCH)) {
         BranchManager.purgeBranch(TEST_BRANCH);
      }
      importToBranch = BranchManager.createWorkingBranch(DemoSawBuilds.SAW_Bld_2, TEST_BRANCH);
   }

   @After
   public void tearDown() throws OseeCoreException {
      BranchManager.purgeBranch(importToBranch);
   }

   private void runOperation(URI file) {
      boolean isRecursive = false;
      boolean isPersistChanges = true;
      String[] traceUnitHandlerIds = {"org.eclipse.osee.ats.config.demo.DemoTraceUnitHandler"};
      boolean fileWithMultiPaths = false;

      IOperation op =
         new ImportTraceUnitsOperation("Import Trace Units", importToBranch, file, isRecursive, isPersistChanges,
            fileWithMultiPaths, traceUnitHandlerIds);
      Operations.executeWork(op);
   }

   @Test
   public void testImportTraceUnitsJob() throws Exception {
      URI mockURI1 = getMockFile(TEST_ONE_FILE, "some text");
      URI mockURI2 = getMockFile(TEST_TWO_FILE, "some text");

      ArrayList<Integer> gammas = new ArrayList<Integer>(RUNS);

      for (int i = 0; i < RUNS; i++) {
         runOperation(mockURI1);

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
         List<String> expected =
            Arrays.asList("Robot Object", "Haptic Constraints", "Robot Interfaces", "Individual robot events",
               "Collaborative Robot");
         Assert.assertFalse(Compare.isDifferent(expected, actual));
      }
      // make sure multiple artifacts were not created
      Assert.assertEquals(1, gammas.size());

      // create a 2nd artifact
      runOperation(mockURI2);
      Artifact artifact =
         ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.CodeUnit, TEST_TWO_FILE, importToBranch);
      Assert.assertNotNull(artifact);
      // make sure a new artifact was created
      Assert.assertFalse(gammas.contains(artifact.getGammaId()));

   }
}
