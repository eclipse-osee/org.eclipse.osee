/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.client.integration.tests.integration.skynet.core;

import static org.junit.Assert.assertEquals;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.operation.NullOperationLogger;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;
import org.eclipse.osee.framework.skynet.core.importing.operations.RoughArtifactCollector;
import org.eclipse.osee.framework.skynet.core.importing.parsers.DoorsArtifactExtractor;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Marc A. Potter
 */
public class DoorsArtifactExtractorTest {

   private static final String THIS_IS_A_PNG_IMAGE_PNG = "This_is_a_PNG_image.png";
   private static final String THIS_IS_A_JPEG_IMAGE_JPG = "This_is_a_JPEG_image.jpg";
   private static final String IMAGE_CONTENT = "Image Content";
   private static final String PRIME_ITEM_DIAGRAM = "Prime item diagram.";
   private static final String COMPANY_DOCUMENTS = "Company documents.";
   private static final String[] ARTIFACT_NAMES = {
      "Door_Requirements",
      "SCOPE",
      "APPLICABLE DOCUMENTS",
      "Non-Government documents.",
      "Company documents.",
      "REQUIREMENTS",
      "Prime item definition.",
      PRIME_ITEM_DIAGRAM};
   private static final String[] ATTRIBUTE_TYPE_LIST = {
      "Name",
      "Legacy Id",
      "HTML Content",
      IMAGE_CONTENT,
      "Paragraph Number"};

   private static final String DOCUMENT_APPLICABILITY = "Document 1";

   @ClassRule
   public static TemporaryFolder folder = new TemporaryFolder();

   private DoorsArtifactExtractor extractor = null;
   private RoughArtifactCollector collector;
   private static File doorHtmlExport;

   @BeforeClass
   public static void setUpResources() throws IOException {
      doorHtmlExport = folder.newFile("sample_DOORS_export.htm");
      copyResource("sample_DOORS_export.htm", doorHtmlExport);
      copyResource(THIS_IS_A_JPEG_IMAGE_JPG, folder.newFile(THIS_IS_A_JPEG_IMAGE_JPG));
      copyResource(THIS_IS_A_PNG_IMAGE_PNG, folder.newFile(THIS_IS_A_PNG_IMAGE_PNG));
   }

   @Before
   public void setUp() {
      extractor = new DoorsArtifactExtractor();
      collector = new RoughArtifactCollector(null);

   }

   @Test
   public void testHtmlSourceExtractor() throws Exception {
      extractor.extractFromSource(NullOperationLogger.getSingleton(), doorHtmlExport.toURI(), collector);

      List<RoughArtifact> theOutput = collector.getRoughArtifacts();

      assertEquals("Wrong number of artifacts detected", ARTIFACT_NAMES.length, theOutput.size());

      for (int index = 0; index < ARTIFACT_NAMES.length; index++) {
         String expectedName = ARTIFACT_NAMES[index];

         RoughArtifact artifact = theOutput.get(index);
         String actualName = artifact.getName();
         assertEquals("Artifact Name is incorrect", expectedName, actualName);

         /***********************************************************
          * Prime item diagram. is checked here because it is the most complicated artifact in the example
          */
         if (PRIME_ITEM_DIAGRAM.equals(actualName)) {
            checkPrimeItemDiagram(artifact);
         }
      }
   }

   private void checkPrimeItemDiagram(RoughArtifact artifact) {
      List<String> actualTypes = new ArrayList<String>(artifact.getAttributeTypeNames());
      assertEquals("Wrong number of attribute types detected", ATTRIBUTE_TYPE_LIST.length, actualTypes.size());

      for (int index = 0; index < ATTRIBUTE_TYPE_LIST.length; index++) {
         String expectedTypeName = ATTRIBUTE_TYPE_LIST[index];
         String typeName = actualTypes.get(index);

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

   @Test
   public void testDocumentFilter() throws Exception {
      extractor.doExtraction(NullOperationLogger.getSingleton(), doorHtmlExport.toURI(), collector,
         DOCUMENT_APPLICABILITY);
      List<RoughArtifact> theOutput = collector.getRoughArtifacts();
      assertEquals("Wrong number of artifacts detected", ARTIFACT_NAMES.length, theOutput.size());

      for (int index = 0; index < ARTIFACT_NAMES.length; index++) {
         String expectedName = ARTIFACT_NAMES[index];

         RoughArtifact artifact = theOutput.get(index);
         String actualName = artifact.getName();
         assertEquals("Artifact Name is incorrect", expectedName, actualName);

         /***********************************************************
          * verify only the Document 1 text exists here
          */
         if (COMPANY_DOCUMENTS.equals(actualName)) {
            String theHtml = artifact.getRoughAttribute(CoreAttributeTypes.HTMLContent.getName());
            int theValue = theHtml.indexOf("ABC-DEF");
            assertEquals("Document Applicability filter failed", theHtml.indexOf("ABC-DEF"), -1);
         }
      }
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
      Bundle bundle = FrameworkUtil.getBundle(DoorsArtifactExtractorTest.class);
      String fullPath = String.format("support/doorsArtifactExtractor/%s", resource);
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
}
