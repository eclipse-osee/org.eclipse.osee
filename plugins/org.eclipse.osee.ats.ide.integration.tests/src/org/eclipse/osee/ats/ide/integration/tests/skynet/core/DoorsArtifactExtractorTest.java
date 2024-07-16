/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.skynet.core;

import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import static org.junit.Assert.assertEquals;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.client.demo.DemoOseeTypes;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;
import org.eclipse.osee.framework.skynet.core.importing.RoughAttributeSet;
import org.eclipse.osee.framework.skynet.core.importing.operations.RoughArtifactCollector;
import org.eclipse.osee.framework.skynet.core.importing.parsers.DoorsArtifactExtractor;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.rules.TemporaryFolder;

/**
 * @author Marc A. Potter
 */
public class DoorsArtifactExtractorTest {

   private static final String THIS_IS_A_PNG_IMAGE_PNG = "This_is_a_PNG_image.png";
   private static final String THIS_IS_A_JPEG_IMAGE_JPG = "This_is_a_JPEG_image.jpg";
   private static final String IMAGE_CONTENT = "Image Content";
   private static final String PRIME_ITEM_DIAGRAM = "Prime item diagram.";
   private static final String COMPANY_DOCUMENTS = "Company documents.";
   private static final String VOICE_STATUS = "Voice status.";
   private static final String[] ARTIFACT_NAMES = {
      "SCOPE",
      "APPLICABLE DOCUMENTS",
      "Government documents.",
      "Specifications.",
      "Military.",
      "Standards.",
      "REQUIREMENTS",
      "Prime item definition.",
      PRIME_ITEM_DIAGRAM,
      VOICE_STATUS};
   private static final String[] ATTRIBUTE_TYPE_LIST = {
      "Name",
      "Paragraph Number",
      "HTML Content",
      "Legacy Id",
      "Subsystem",
      "Qualification Method",
      "Verification Event",
      "Verification Level",
      IMAGE_CONTENT};

   private static final ArtifactTypeToken[] ARTIFACT_TYPES = {
      CoreArtifactTypes.HtmlArtifact,
      CoreArtifactTypes.HtmlArtifact,
      CoreArtifactTypes.HeadingHtml,
      CoreArtifactTypes.HeadingHtml,
      CoreArtifactTypes.HtmlArtifact,
      CoreArtifactTypes.HeadingHtml,
      CoreArtifactTypes.HeadingHtml,
      CoreArtifactTypes.HtmlArtifact,
      CoreArtifactTypes.HtmlArtifact,
      CoreArtifactTypes.HtmlArtifact};

   private static final String DOCUMENT_APPLICABILITY = "Document 1";

   private Artifact theArtifact;

   private final List<String> content = Arrays.asList("<img src=\"Image Content_0>\"");
   private final Collection<InputStream> imageList = new LinkedList<>();

   @ClassRule
   public static TemporaryFolder folder = new TemporaryFolder();

   private DoorsArtifactExtractor extractor = null;
   private RoughArtifactCollector collector;
   private static File doorHtmlExport;
   private static File expectedList;

   @BeforeClass
   public static void setUpResources() throws IOException {
      doorHtmlExport = folder.newFile("sample_DOORS_export.htm");
      copyResource("sample_DOORS_export.htm", doorHtmlExport);
      copyResource(THIS_IS_A_JPEG_IMAGE_JPG, folder.newFile(THIS_IS_A_JPEG_IMAGE_JPG));
      copyResource(THIS_IS_A_PNG_IMAGE_PNG, folder.newFile(THIS_IS_A_PNG_IMAGE_PNG));
      expectedList = folder.newFile("Expected_list.htm");
      copyResource("Expected_list.htm", expectedList);
   }

   @Before
   public void setUp() throws UnsupportedEncodingException {
      extractor = new DoorsArtifactExtractor();
      collector = new RoughArtifactCollector(null);
      theArtifact = ArtifactTypeManager.addArtifact(DemoOseeTypes.DemoArtifactWithSelectivePartition, SAW_Bld_1);
      String image = new String("String to represent binary image data");
      imageList.add(Lib.stringToInputStream(image));
      theArtifact.setAttributeFromValues(CoreAttributeTypes.ImageContent, imageList);
      theArtifact.setAttributeValues(CoreAttributeTypes.HtmlContent, content);
   }

   @After
   public void tearDown() throws Exception {
      if (theArtifact != null) {
         theArtifact.deleteAndPersist(getClass().getSimpleName());
      }
   }

   /*
    * @Test public void testHtmlSourceExtractor() throws Exception {
    * extractor.extractFromSource(NullOperationLogger.getSingleton(), doorHtmlExport.toURI(), collector);
    * List<RoughArtifact> theOutput = collector.getRoughArtifacts(); assertEquals("Wrong number of artifacts detected",
    * ARTIFACT_NAMES.length, theOutput.size()); for (int index = 0; index < ARTIFACT_NAMES.length; index++) { String
    * expectedName = ARTIFACT_NAMES[index]; RoughArtifact artifact = theOutput.get(index); String actualName =
    * artifact.getName(); assertEquals("Artifact Name is incorrect", expectedName, actualName); ArtifactTypeToken
    * expectedType = ARTIFACT_TYPES[index]; ArtifactTypeToken actualType = artifact.getArtifactType();
    * assertEquals("Artifact Type is incorrect", expectedType, actualType); if (PRIME_ITEM_DIAGRAM.equals(actualName)) {
    * checkPrimeItemDiagram(artifact); assertTrue(extractor.artifactCreated(theArtifact, artifact)); } else if
    * (VOICE_STATUS.equals(actualName)) { checkList(artifact); } } }
    */
   private void checkPrimeItemDiagram(RoughArtifact artifact) {
      Set<String> attributeTypeNames = artifact.getAttributeTypeNames();
      assertEquals("Wrong number of attribute types detected", ATTRIBUTE_TYPE_LIST.length, attributeTypeNames.size());

      Iterator<String> iterator = attributeTypeNames.iterator();
      for (String expectedTypeName : ATTRIBUTE_TYPE_LIST) {
         String typeName = iterator.next();

         assertEquals("Incorrect attribute type", expectedTypeName, typeName);

         if (IMAGE_CONTENT.equals(typeName)) {

            Collection<URI> theURIs = artifact.getURIAttributes();
            assertEquals("wrong number of images", 2, theURIs.size());

            Iterator<URI> iter = theURIs.iterator();

            URI uri1 = iter.next();
            URI uri2 = iter.next();

            assertEquals("Wrong image stored in slot 0", THIS_IS_A_JPEG_IMAGE_JPG, getName(uri1));
            assertEquals("Wrong image stored in slot 1", THIS_IS_A_PNG_IMAGE_PNG, getName(uri2));
         }
      }
   }

   /*
    * @Test public void testDocumentFilter() throws Exception {
    * extractor.doExtraction(NullOperationLogger.getSingleton(), doorHtmlExport.toURI(), collector,
    * DOCUMENT_APPLICABILITY); List<RoughArtifact> theOutput = collector.getRoughArtifacts();
    * assertEquals("Wrong number of artifacts detected", ARTIFACT_NAMES.length, theOutput.size()); for (int index = 0;
    * index < ARTIFACT_NAMES.length; index++) { String expectedName = ARTIFACT_NAMES[index]; RoughArtifact artifact =
    * theOutput.get(index); String actualName = artifact.getName(); assertEquals("Artifact Name is incorrect",
    * expectedName, actualName); if (COMPANY_DOCUMENTS.equals(actualName)) { String theHtml =
    * artifact.getRoughAttribute(CoreAttributeTypes.HtmlContent.getName());
    * assertEquals("Document Applicability filter failed", theHtml.indexOf("XYZ-ABC"), -1); } } }
    */
   @Test
   public void test(){
   
   }

   private String getName(URI uri) {
      String value = uri.getPath();
      int index = value.lastIndexOf("/");
      if (index > 0 && index + 1 < value.length()) {
         value = value.substring(index + 1, value.length());
      }
      return value;
   }

   private static void copyResource(String resource, File output) throws IOException {
      OutputStream outputStream = null;
      try (InputStream inputStream =
         OsgiUtil.getResourceAsStream(DoorsArtifactExtractorTest.class, "support/doorsArtifactExtractor/" + resource)) {
         outputStream = new BufferedOutputStream(new FileOutputStream(output));
         Lib.inputStreamToOutputStream(inputStream, outputStream);
      } finally {
         Lib.close(outputStream);
      }
   }

   private void checkList(RoughArtifact artifact) throws Exception {
      RoughAttributeSet attributes = artifact.getAttributes();
      String input = attributes.getSoleAttributeValue("HTML Content");
      String expected = Lib.fileToString(expectedList);
      assertEquals("Document Applicability filter failed", input, expected);

   }
}