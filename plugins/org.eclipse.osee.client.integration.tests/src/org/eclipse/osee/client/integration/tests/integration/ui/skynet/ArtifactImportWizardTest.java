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

import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.util.Lib;
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
import org.eclipse.osee.framework.ui.skynet.Import.ArtifactResolverFactory;
import org.eclipse.osee.framework.ui.skynet.Import.ArtifactResolverFactory.ArtifactCreationStrategy;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @see ArtifactImportWizard
 * @author Karol M. Wilk
 */
public final class ArtifactImportWizardTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Rule
   public TemporaryFolder resource = new TemporaryFolder();

   private Artifact myRootArtifact;

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
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, SAW_Bld_1, "ArtifactImportWizardTest_Root");

      OseeSystemArtifacts.getDefaultHierarchyRootArtifact(SAW_Bld_1).addChild(myRootArtifact);

      Artifact artifactA = ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirement, SAW_Bld_1, "A");
      myRootArtifact.addChild(artifactA);

      artifactA.addChild(ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirement, SAW_Bld_1, "C"));

      artifactA.addChild(ArtifactTypeManager.addArtifact(CoreArtifactTypes.Requirement, SAW_Bld_1, "D"));

      myRootArtifact.addChild(ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirement, SAW_Bld_1, "B"));

      myRootArtifact.persist("ArtifactImportWizardTest");
   }

   @After
   public void tearDown() throws Exception {
      Operations.executeWorkAndCheckStatus(new PurgeArtifacts(Collections.singletonList(myRootArtifact), true));
   }

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
      Map<String, String> answerParagraphNumbers = new HashMap<>();
      answerParagraphNumbers.put("B", "3");
      answerParagraphNumbers.put("D", "2");

      int numberOfDescendants = myRootArtifact.getDescendants().size();

      buildAndRunCoreTest("artifactExcelImportInput_attributeCopyTest.xml");

      List<Artifact> afterArtifacts = myRootArtifact.getDescendants();
      Assert.assertTrue("Unexpected number of artifacts.", numberOfDescendants == afterArtifacts.size());

      //check if artifacts have correct attributes copied over
      for (Artifact artifact : afterArtifacts) {
         List<String> attributes = artifact.getAttributesToStringList(CoreAttributeTypes.ParagraphNumber);
         for (String attribute : attributes) {
            String paragraphNumberAnswer = answerParagraphNumbers.get(artifact.getName());
            if (paragraphNumberAnswer != null) {
               Assert.assertTrue(String.format("Expected attribute: %s, on Artifact %s, was not copied. ",
                  CoreAttributeTypes.ParagraphNumber, artifact), paragraphNumberAnswer.equals(attribute.toString()));
            }
         }
      }
   }

   private void buildAndRunCoreTest(String nameOfExcelImportFile) throws Exception {
      URL url = ArtifactImportWizardTest.class.getResource("support/" + nameOfExcelImportFile);
      Assert.assertNotNull(url);

      String importFileName = String.format("artifact.import.wizard.test_%s", nameOfExcelImportFile);
      File inputExcelFile = resource.newFile(importFileName);

      InputStream inputStream = null;
      OutputStream outputStream = null;
      try {
         inputStream = new BufferedInputStream(url.openStream());
         outputStream = new FileOutputStream(inputExcelFile);
         Lib.inputStreamToOutputStream(inputStream, outputStream);
      } finally {
         Lib.close(inputStream);
         Lib.close(outputStream);
      }

      Assert.assertTrue(inputExcelFile.exists());
      try {
         IArtifactImportResolver resolver =
            ArtifactResolverFactory.createResolver(ArtifactCreationStrategy.CREATE_ON_NEW_ART_GUID,
               CoreArtifactTypes.SystemRequirementMsWord, null, true, true);

         RoughArtifactCollector collector = new RoughArtifactCollector(new RoughArtifact(RoughArtifactKind.PRIMARY));
         collector.reset();

         IOperation operation = ArtifactImportOperationFactory.createOperation(inputExcelFile, myRootArtifact, null,
            new ExcelArtifactExtractor(), resolver, collector, Arrays.asList(CoreArtifactTypes.SystemRequirementMsWord),
            true, true, false);
         Operations.executeWorkAndCheckStatus(operation);

         Assert.assertFalse(collector.getRoughArtifacts().isEmpty());
      } finally {
         inputExcelFile.delete();
      }
   }
}
