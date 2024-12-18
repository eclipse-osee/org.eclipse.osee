/*********************************************************************
 * Copyright (c) 2024 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.ats.ide.integration.tests.ui.skynet;

import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.framework.core.data.MicrosoftOfficeApplicationEnum;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.PurgeArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;
import org.eclipse.osee.framework.skynet.core.importing.operations.RoughArtifactCollector;
import org.eclipse.osee.framework.skynet.core.importing.parsers.NativeDocumentExtractor;
import org.eclipse.osee.framework.skynet.core.importing.resolvers.IArtifactImportResolver;
import org.eclipse.osee.framework.ui.skynet.Import.ArtifactImportOperationFactory;
import org.eclipse.osee.framework.ui.skynet.Import.ArtifactResolverFactory;
import org.eclipse.osee.framework.ui.skynet.Import.ArtifactResolverFactory.ArtifactCreationStrategy;
import org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer;
import org.eclipse.osee.framework.ui.skynet.render.NativeRenderer;
import org.eclipse.swt.program.Program;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Tests 2 cases for artifacts with XML native content attributes: <br/>
 * 1 - artifact has a Microsoft Office (MSO) application attribute specified. <br/>
 * 2 - artifact has no MSO application attribute specified.
 *
 * @author Jaden W. Puckett
 */
public class ArtifactXmlNativeContentTest {

   private static final String ARTIFACT_NAME_MSO_APP_SPECIFIED = "ArtifactXmlNativeContentTest_WordXmlMsoAppSpecified";
   private static final String ARTIFACT_NAME_NO_MSO_APP_SPECIFIED =
      "ArtifactXmlNativeContentTest_WordXmlNoMsoAppSpecified";
   private static final String WORD_XML_IMPORT_FILE_NAME = "ArtifactXmlNativeContentTestWord.xml";
   private static Artifact rootArtifact;

   /**
    * Some tests should only be ran in Windows. Linux environments don't have an associated program for files with xml
    * extensions out of the box.
    */
   private static boolean isWindows = Lib.isWindows();

   @Rule
   public NotProductionDataStoreRule notProduction = new NotProductionDataStoreRule();

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @ClassRule
   public static TemporaryFolder resource = new TemporaryFolder();

   @BeforeClass
   public static void setUp() throws Exception {
      // Initialize root artifact and add it to the hierarchy
      rootArtifact = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, SAW_Bld_1,
         "XML Native Content Artifact Operations Test");
      OseeSystemArtifacts.getDefaultHierarchyRootArtifact(SAW_Bld_1).addChild(rootArtifact);

      // Load XML content file from test resources
      URL xmlContentFileUrl = ArtifactXmlNativeContentTest.class.getResource("support/" + WORD_XML_IMPORT_FILE_NAME);
      Assert.assertNotNull("Test resource file not found", xmlContentFileUrl);

      String artifactFileName = ARTIFACT_NAME_MSO_APP_SPECIFIED + ".xml";
      File xmlContentFile = resource.newFile(artifactFileName);
      copyResourceToFile(xmlContentFileUrl, xmlContentFile);

      Assert.assertTrue("XML content file not created", xmlContentFile.exists());

      // Case 1: Artifact with no MSO application attribute specified
      createArtifactWithNoMsoApplicationSpecified(rootArtifact, xmlContentFileUrl);

      // Case 2: Import artifact with MSO application attribute specified
      importWordXmlFileAsArtifact(rootArtifact, xmlContentFile, xmlContentFileUrl);
   }

   @AfterClass
   public static void tearDown() throws Exception {
      // Clean up resources or artifacts if necessary
      Operations.executeWorkAndCheckStatus(new PurgeArtifacts(Collections.singletonList(rootArtifact), true));
   }

   private static void copyResourceToFile(URL sourceUrl, File destination) throws Exception {
      try (InputStream inputStream = new BufferedInputStream(sourceUrl.openStream());
         OutputStream outputStream = new FileOutputStream(destination)) {
         Lib.inputStreamToOutputStream(inputStream, outputStream);
      }
   }

   private static void createArtifactWithNoMsoApplicationSpecified(Artifact rootArtifact, URL xmlContentFileUrl)
      throws Exception {
      try (InputStream xmlContent = new BufferedInputStream(xmlContentFileUrl.openStream())) {
         Assert.assertNotNull("InputStream should not be null", xmlContent);

         Artifact artifactNoMso = ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralDocument, SAW_Bld_1,
            ARTIFACT_NAME_NO_MSO_APP_SPECIFIED);
         artifactNoMso.addAttribute(CoreAttributeTypes.Extension, "xml");
         artifactNoMso.addAttribute(CoreAttributeTypes.NativeContent, xmlContent);
         rootArtifact.addChild(artifactNoMso);
         rootArtifact.persist("PublishingXMLNativeContentTest setUp()");
      }
   }

   /**
    * Import an XML file as an artifact and validate attributes are set correctly.
    */
   private static void importWordXmlFileAsArtifact(Artifact rootArtifact, File xmlContentFile, URL xmlContentFileUrl)
      throws Exception {
      IArtifactImportResolver resolver = ArtifactResolverFactory.createResolver(
         ArtifactCreationStrategy.CREATE_ON_NEW_ART_GUID, CoreArtifactTypes.GeneralDocument, null, true, false);
      RoughArtifactCollector collector = new RoughArtifactCollector(new RoughArtifact());
      collector.reset();

      IOperation operation = ArtifactImportOperationFactory.createOperation(xmlContentFile, rootArtifact, null,
         new NativeDocumentExtractor(), resolver, collector, Arrays.asList(CoreArtifactTypes.GeneralDocument), true,
         false, false);
      Operations.executeWorkAndCheckStatus(operation);

      Assert.assertFalse("No artifacts collected", collector.getRoughArtifacts().isEmpty());

      // Validate imported artifact
      Artifact importedArtifact = queryArtifactByName(ARTIFACT_NAME_MSO_APP_SPECIFIED);
      Assert.assertEquals("MSO application attribute mismatch",
         MicrosoftOfficeApplicationEnum.WORD_DOCUMENT.getApplicationName(),
         importedArtifact.getSoleAttributeValue(CoreAttributeTypes.MicrosoftOfficeApplication));
      Assert.assertEquals("Extension attribute mismatch", "xml",
         importedArtifact.getSoleAttributeValue(CoreAttributeTypes.Extension));

      // Validate native content
      try (InputStream expectedContent = new BufferedInputStream(xmlContentFileUrl.openStream());
         InputStream actualContent = importedArtifact.getSoleAttributeValue(CoreAttributeTypes.NativeContent)) {
         Assert.assertEquals("Native content mismatch", Lib.inputStreamToString(expectedContent),
            Lib.inputStreamToString(actualContent));
      }
   }

   private static Artifact queryArtifactByName(String name) {
      List<Artifact> artifacts = ArtifactQuery.getArtifactListFromName(name, SAW_Bld_1);
      Assert.assertEquals("Artifact not found or multiple artifacts exist", 1, artifacts.size());
      return artifacts.get(0);
   }

   /**
    * Test renderer behavior when no MSO application attribute is specified.
    */
   @Test
   public void getAssociatedProgramForArtifactWithNoMsoApplicationSpecified() {
      Assume.assumeTrue("Test can only run on Windows", isWindows);
      Artifact artifact = queryArtifactByName(ARTIFACT_NAME_NO_MSO_APP_SPECIFIED);
      validateRendererOutput(artifact, "Microsoft Word Document");
   }

   /**
    * Test renderer behavior when MSO application attribute is specified.
    */
   @Test
   public void getAssociatedProgramForArtifactWithMsoApplicationSpecified() {
      Assume.assumeTrue("Test can only run on Windows", isWindows);
      Artifact artifact = queryArtifactByName(ARTIFACT_NAME_MSO_APP_SPECIFIED);
      validateRendererOutput(artifact, "Microsoft Word Document");
   }

   /**
    * Validate that the renderer finds the correct associated program.
    */
   private void validateRendererOutput(Artifact artifact, String expectedProgramName) {
      FileSystemRenderer renderer = new NativeRenderer();
      Program program = renderer.getAssociatedProgram(artifact);
      Assert.assertTrue("Renderer did not return expected program", program.getName().contains(expectedProgramName));
   }
}
