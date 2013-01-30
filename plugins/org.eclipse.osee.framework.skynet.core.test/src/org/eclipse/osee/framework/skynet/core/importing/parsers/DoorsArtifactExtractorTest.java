/*
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial implementation
 */
package org.eclipse.osee.framework.skynet.core.importing.parsers;

import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;
import org.eclipse.osee.framework.skynet.core.importing.operations.RoughArtifactCollector;
import org.junit.Assert;
import org.junit.Test;

public class DoorsArtifactExtractorTest {

   @Test
   public void test() {
      DoorsArtifactExtractor ext = new DoorsArtifactExtractor();
      RoughArtifactCollector collector = new RoughArtifactCollector(null);
      try {
         String relPath = "org.eclipse.osee.framework.skynet.core.test/support/sample_DOORS_export.htm";
         ClassLoader cl = this.getClass().getClassLoader();
         URL baseURL = cl.getResource("");
         String fullPath = baseURL.getPath() + "../../" + relPath;
         URI source = new URI(fullPath);
         ext.extractFromSource(null, source, collector);
      } catch (Exception e) {
         e.printStackTrace();
      }
      List<RoughArtifact> theOutput = collector.getRoughArtifacts();
      int size = theOutput.size();
      String[] Names =
         {
            "SCOPE",
            "APPLICABLE DOCUMENTS",
            "Non-Government documents.",
            "Company documents.",
            "REQUIREMENTS",
            "Prime item definition.",
            "Prime item diagram."};
      int i = 0;
      Set<String> types;
      for (RoughArtifact artifact : theOutput) {
         String name = artifact.getName();
         boolean equal = name.equals(Names[i]);
         Assert.assertTrue("name of artfact incorrect is " + name + " should be " + Names[i], equal);
         types = artifact.getAttributeTypeNames();
         /***********************************************************
          * Prime item diagram. is checked here because it is the most complicated artifact in the example
          */
         if (name.equals("Prime item diagram.")) {
            int j = 0;
            String[] typeList = {"Name", "Legacy Id", "HTML Content", "Image Content", "Paragraph Number"};
            for (String str : types) {
               equal = str.equals(typeList[j]);
               Assert.assertTrue("type of artfact incorrect is " + str + " should be " + typeList[j], equal);
               if (str.equals("Image Content")) {
                  Collection<URI> theURIs = artifact.getURIAttributes();
                  Assert.assertTrue("wrong number of images", theURIs.size() == 2);
                  Iterator<URI> iter = theURIs.iterator();
                  URI image = iter.next();
                  Assert.assertFalse("Wrong image stored in slot 0",
                     image.getPath().indexOf("This_is_a_JPEG_image.jpg") == -1);
                  image = iter.next();
                  Assert.assertFalse("Wrong image stored in slot 1",
                     image.getPath().indexOf("This_is_a_PNG_image.png") == -1);
               }
               j++;
            }
         }
         i++;
      }
      Assert.assertTrue("Wrong number of artifacts detected", size == 7);

   }
}
