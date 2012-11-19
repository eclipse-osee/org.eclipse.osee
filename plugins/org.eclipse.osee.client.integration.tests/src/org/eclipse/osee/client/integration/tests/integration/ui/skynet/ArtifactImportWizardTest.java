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
package org.eclipse.osee.client.integration.tests.integration.ui.skynet;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.plugin.core.util.OseeData;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.PurgeArtifacts;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifactKind;
import org.eclipse.osee.framework.skynet.core.importing.operations.RoughArtifactCollector;
import org.eclipse.osee.framework.skynet.core.importing.parsers.ExcelArtifactExtractor;
import org.eclipse.osee.framework.skynet.core.importing.resolvers.IArtifactImportResolver;
import org.eclipse.osee.framework.ui.skynet.Import.ArtifactImportOperationFactory;
import org.eclipse.osee.framework.ui.skynet.Import.ArtifactImportWizard;
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
 * @see ArtifactImportWizard
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
      // add paragraph number to sample artifacts, result should have copied that paragraph over...

      // copy paragraph over... because they will be matched on guid...
      Map<String, String> answerParagraphNumbers = new HashMap<String, String>();
      answerParagraphNumbers.put("B", "3");
      answerParagraphNumbers.put("D", "2");

      int numberOfDescendants = myRootArtifact.getDescendants().size();

      buildAndRunCoreTest("artifactExcelImportInput_attributeCopyTest.xml");

      List<Artifact> afterArtifacts = myRootArtifact.getDescendants();
      Assert.assertTrue("Unexpected number of artifacts.", numberOfDescendants == afterArtifacts.size());

      //check if artifacts have correct attributes copied over
      for (Artifact artifact : afterArtifacts) {
         List<?> attributes = artifact.getAttributes(CoreAttributeTypes.ParagraphNumber);
         for (Object attribute : attributes) {
            String paragraphNumberAnswer = answerParagraphNumbers.get(artifact.getName());
            if (paragraphNumberAnswer != null) {
               Assert.assertTrue(String.format("Expected attribute: %s, on Artifact %s, was not copied. ",
                  CoreAttributeTypes.ParagraphNumber, artifact), paragraphNumberAnswer.equals(attribute.toString()));
            }
         }
      }
   }

   private void buildAndRunCoreTest(String nameOfExcelImportFile) throws Exception {
      File inputExcelFile = OseeData.getFile("artifact.import.wizard.test." + nameOfExcelImportFile);
      URL url = ArtifactImportWizardTest.class.getResource("support/" + nameOfExcelImportFile);
      url = FileLocator.resolve(url);

      Assert.assertNotNull(url);

      Lib.copyFile(new File(url.toURI()), inputExcelFile);

      Assert.assertTrue(inputExcelFile.exists());
      try {
         IArtifactImportResolver resolver =
            MatchingStrategy.GUID.getResolver(CoreArtifactTypes.SystemRequirement, null, true, true);

         RoughArtifactCollector collector = new RoughArtifactCollector(new RoughArtifact(RoughArtifactKind.PRIMARY));
         collector.reset();

         IOperation operation =
            ArtifactImportOperationFactory.createOperation(inputExcelFile, myRootArtifact, null,
               new ExcelArtifactExtractor(), resolver, collector, Arrays.asList(CoreArtifactTypes.SystemRequirement),
               true, true, false);
         Operations.executeWorkAndCheckStatus(operation);

         Assert.assertFalse(collector.getRoughArtifacts().isEmpty());
      } finally {
         inputExcelFile.delete();
      }
   }

   /**
    * setup artifact tree of this form:
    * 
    * <pre>
    * myRootArtifact
    *              |
    *              `--A
    *              |   \._ C
    *              |   |
    *              |   `._ D
    *              |
    *              `--B
    * </pre>
    * 
    * Where myRootArtifact real name is "ArtifactImportWizardTest_Root"
    */
   @Before
   public void setUp() throws Exception {
      myRootArtifact =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, DemoSawBuilds.SAW_Bld_1,
            "ArtifactImportWizardTest_Root", "ArtifatImpWizaTestGUID", "12345");

      OseeSystemArtifacts.getDefaultHierarchyRootArtifact(DemoSawBuilds.SAW_Bld_1).addChild(myRootArtifact);

      Artifact artifactA =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirement, DemoSawBuilds.SAW_Bld_1, "A",
            "AAAAAAAAAAAAAAAAAAAAAA", "A2345");
      myRootArtifact.addChild(artifactA);

      artifactA.addChild(ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirement,
         DemoSawBuilds.SAW_Bld_1, "C", "CCCCCCCCCCCCCCCCCCCCCC", "C2345"));

      artifactA.addChild(ArtifactTypeManager.addArtifact(CoreArtifactTypes.Requirement, DemoSawBuilds.SAW_Bld_1, "D",
         "DDDDDDDDDDDDDDDDDDDDDD", "D2345"));

      myRootArtifact.addChild(ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirement,
         DemoSawBuilds.SAW_Bld_1, "B", "BBBBBBBBBBBBBBBBBBBBBB", "B2345"));

      myRootArtifact.persist("ArtifactImportWizardTest");
   }

   @After
   public void tearDown() throws Exception {
      Operations.executeWorkAndCheckStatus(new PurgeArtifacts(Collections.singletonList(myRootArtifact), true));
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
