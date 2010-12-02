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
package org.eclipse.osee.framework.ui.skynet.test.importer;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.PurgeArtifacts;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifactKind;
import org.eclipse.osee.framework.skynet.core.importing.operations.RoughArtifactCollector;
import org.eclipse.osee.framework.skynet.core.importing.parsers.ExcelArtifactExtractor;
import org.eclipse.osee.framework.skynet.core.importing.resolvers.IArtifactImportResolver;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.Import.ArtifactImportOperationFactory;
import org.eclipse.osee.framework.ui.skynet.Import.MatchingStrategy;
import org.eclipse.osee.support.test.util.DemoSawBuilds;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @link ArtifactImportWizard
 * @author Karol M. Wilk
 */
public final class ArtifactImportWizardTest {

   private static SevereLoggingMonitor monitorLog = null;
   private static Artifact myRootArtifact = null;

   @Test
   public void simpleImportNobodyGetsDeleted() throws Exception {
      int numberOfFirstLevelDescendants = myRootArtifact.getDescendants().size();

      buildAndRunCoreTest("artifactExcelImportInput_Base.xml");

      Assert.assertTrue("Unexpected number of descendants.",
         numberOfFirstLevelDescendants == myRootArtifact.getDescendants().size());
   }

   @Test
   public void removeSimpleChild() throws Exception {
      //D will be deleted...
      int numberOfDescendants = myRootArtifact.getDescendants().size();

      buildAndRunCoreTest("artifactExcelImportInput_SimpleChild.xml");

      List<Artifact> afterArtifacts = myRootArtifact.getDescendants();
      Assert.assertTrue("Unexpected number of artifacts.", numberOfDescendants - 1 == afterArtifacts.size());

      //look for D
      Assert.assertFalse(afterArtifacts.contains("D"));

   }

   @Test
   public void attributeCopyTest() throws Exception {
      // add imported paragraph number to sample artifacts, result should have copied that imported paragraph over...

      // copy imported paragraph over... because they will be matched on guid...
      Map<String, String> answerParagraphNumbers = new HashMap<String, String>();
      answerParagraphNumbers.put("B", "3");
      answerParagraphNumbers.put("D", "2");

      int numberOfDescendants = myRootArtifact.getDescendants().size();

      buildAndRunCoreTest("artifactExcelImportInput_attributeCopyTest.xml");

      List<Artifact> afterArtifacts = myRootArtifact.getDescendants();
      Assert.assertTrue("Unexpected number of artifacts.", numberOfDescendants == afterArtifacts.size());

      //check if artifacts have correct attributes copied over
      for (Artifact artifact : afterArtifacts) {
         String artifactName = artifact.getName();
         List<?> attributes = artifact.getAttributes(CoreAttributeTypes.ParagraphNumber);
         for (Object attribute : attributes) {
            String paragraphNumberAnswer = answerParagraphNumbers.get(artifactName);
            if (paragraphNumberAnswer != null) {
               Assert.assertTrue(String.format("Expected attribute: %s, on Artifact %s, wasn't copied. ",
                  CoreAttributeTypes.ParagraphNumber, artifact), paragraphNumberAnswer.equals(attribute.toString()));
            }
         }
      }
   }

   private void buildAndRunCoreTest(String nameOfExcelImportFile) throws Exception {
      URL url = ArtifactImportWizardTest.class.getResource(nameOfExcelImportFile);
      Assert.assertNotNull(url);
      File inputExcelFile = new File(url.toURI());
      Assert.assertTrue(inputExcelFile.exists());

      IArtifactImportResolver resolver =
         MatchingStrategy.GUID.getResolver(CoreArtifactTypes.SystemRequirement, null, true, true);

      RoughArtifactCollector collector = new RoughArtifactCollector(new RoughArtifact(RoughArtifactKind.PRIMARY));
      collector.reset();

      IOperation operation =
         ArtifactImportOperationFactory.createArtifactAndRoughToRealOperation(inputExcelFile, myRootArtifact,
            new ExcelArtifactExtractor(), resolver, collector, Arrays.asList(CoreArtifactTypes.SystemRequirement),
            true, true, false);
      Operations.executeWork(operation);

      Assert.assertFalse(collector.getRoughArtifacts().size() == 0);
   }

   @Before
   public void setUp() throws Exception {
      //@formatter:off
      /*
       setup artifact tree of this form:
         ArtifactImportWizardTest_Root
                                     |
                                     `--A
                                        |\.__ C
                                        | |
                                        | `._ D
                                        B
       */
      //@formatter:on

      SkynetTransaction transaction = new SkynetTransaction(DemoSawBuilds.SAW_Bld_1, "ArtifactImportWizardTest");

      myRootArtifact =
         ArtifactTypeManager.getFactory(CoreArtifactTypes.Folder).makeNewArtifact(DemoSawBuilds.SAW_Bld_1,
            CoreArtifactTypes.Folder, "ArtifactImportWizardTest_Root", "ArtifatImpWizaTestGUID", "12345", null);

      OseeSystemArtifacts.getDefaultHierarchyRootArtifact(DemoSawBuilds.SAW_Bld_1).addChild(myRootArtifact);

      Artifact artifactA =
         ArtifactTypeManager.getFactory(CoreArtifactTypes.SoftwareRequirement).makeNewArtifact(DemoSawBuilds.SAW_Bld_1,
            CoreArtifactTypes.SoftwareRequirement, "A", "AAAAAAAAAAAAAAAAAAAAAA", "A2345", null);
      myRootArtifact.addChild(artifactA);

      artifactA.addChild(ArtifactTypeManager.getFactory(CoreArtifactTypes.SoftwareRequirement).makeNewArtifact(
         DemoSawBuilds.SAW_Bld_1, CoreArtifactTypes.SoftwareRequirement, "C", "CCCCCCCCCCCCCCCCCCCCCC", "C2345", null));

      artifactA.addChild(ArtifactTypeManager.getFactory(CoreArtifactTypes.Requirement).makeNewArtifact(
         DemoSawBuilds.SAW_Bld_1, CoreArtifactTypes.Requirement, "D", "DDDDDDDDDDDDDDDDDDDDDD", "D2345", null));

      myRootArtifact.addChild(ArtifactTypeManager.getFactory(CoreArtifactTypes.SoftwareRequirement).makeNewArtifact(
         DemoSawBuilds.SAW_Bld_1, CoreArtifactTypes.SoftwareRequirement, "B", "BBBBBBBBBBBBBBBBBBBBBB", "B2345", null));

      myRootArtifact.persist(transaction);
      transaction.execute();
   }

   @After
   public void tearDown() throws Exception {
      new PurgeArtifacts(myRootArtifact.getDescendants()).execute();
      new PurgeArtifacts(Collections.singletonList(myRootArtifact)).execute();
   }

   @BeforeClass
   public static void setUpOnce() throws Exception {
      monitorLog = TestUtil.severeLoggingStart();
   }

   @AfterClass
   public static void tearDownOnce() throws Exception {
      TestUtil.severeLoggingEnd(monitorLog);
   }

   //   private void displayArtifactTree(Artifact artifact) throws OseeCoreException {
   //      displayArtifactTree(artifact, 0);
   //   }
   //
   //   private void displayArtifactTree(Artifact artifact, int depth) throws OseeCoreException {
   //      for (int indentCount = 0; indentCount < depth; indentCount++) {
   //         System.out.print(" ");
   //      }
   //      System.out.println(artifact.getName());
   //
   //      int longestArtifactNameLength = artifact.getName().length();
   //      if (longestArtifactNameLength > depth) {
   //         depth = longestArtifactNameLength;
   //      }
   //
   //      List<Artifact> children = artifact.getChildren();
   //      if (!children.isEmpty()) {
   //         for (Artifact child : children) {
   //            displayArtifactTree(child, depth + 1);
   //         }
   //      }
   //   }
}
