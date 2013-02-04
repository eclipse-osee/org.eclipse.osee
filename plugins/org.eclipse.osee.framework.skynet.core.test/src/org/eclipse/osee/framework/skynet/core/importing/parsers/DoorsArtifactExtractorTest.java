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
package org.eclipse.osee.framework.skynet.core.importing.parsers;

import static org.junit.Assert.assertEquals;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.osee.framework.core.operation.NullOperationLogger;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;
import org.eclipse.osee.framework.skynet.core.importing.operations.RoughArtifactCollector;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Marc A. Potter
 */
public class DoorsArtifactExtractorTest {

   private static final String SUPPORT_SAMPLE_DOORS_EXPORT_HTM = "support/sample_DOORS_export.htm";
   private static final String IMAGE_CONTENT = "Image Content";
   private static final String PRIME_ITEM_DIAGRAM = "Prime item diagram.";
   private static final String[] ARTIFACT_NAMES = {
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

   @Rule
   public TemporaryFolder folder = new TemporaryFolder();

   private DoorsArtifactExtractor extractor;
   private RoughArtifactCollector collector;
   private File doorHtmlExport;

   @Before
   public void setUp() throws IOException {
      extractor = new DoorsArtifactExtractor();
      collector = new RoughArtifactCollector(null);

      InputStream inputStream = null;
      try {
         doorHtmlExport = folder.newFile("sample_DOORS_export.htm");
         inputStream = getClass().getResourceAsStream(SUPPORT_SAMPLE_DOORS_EXPORT_HTM);
         Lib.inputStreamToFile(inputStream, doorHtmlExport);
      } finally {
         Lib.close(inputStream);
      }
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

            assertEquals("Wrong image stored in slot 0", "This_is_a_JPEG_image.jpg", getName(uri1));
            assertEquals("Wrong image stored in slot 1", "This_is_a_PNG_image.png", getName(uri2));
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
}
