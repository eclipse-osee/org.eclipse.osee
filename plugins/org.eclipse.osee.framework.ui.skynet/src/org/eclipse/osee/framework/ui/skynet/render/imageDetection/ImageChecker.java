/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.framework.ui.skynet.render.imageDetection;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import jakarta.xml.bind.DatatypeConverter;
import javax.xml.xpath.XPath;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.jdk.core.util.xml.SimpleNamespaceContext;
import org.eclipse.osee.framework.jdk.core.util.xml.Xml;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ImageChecker {

   private final File basePath;

   public ImageChecker(File basePath) {
      this.basePath = basePath;
   }

   /**
    * @param args
    */
   public static void main(String[] args) throws Exception {
      File basePath = new File("C:\\Documents and Settings\\b1565043\\Desktop\\extracted");

      Lib.deleteDir(basePath);

      InputStream inputStream = null;
      try {
         inputStream = new BufferedInputStream(new FileInputStream(
            "C:\\Documents and Settings\\b1565043\\Desktop\\_MPD_VAM_CONTROL__1177719__20110414_093657-41.xml"));
         ImageChecker checker = new ImageChecker(basePath);
         checker.extractImages(inputStream);
      } finally {
         Lib.close(inputStream);
      }
   }

   private void extractImages(InputStream inputStream) throws Exception {
      Document document = Jaxp.readXmlDocumentNamespaceAware(inputStream);
      Element rootElement = document.getDocumentElement();

      XPath xPath = Jaxp.createXPath();
      SimpleNamespaceContext context = new SimpleNamespaceContext();
      Xml.addNamespacesForWordMarkupLanguage(xPath, context);
      Collection<Node> nodes = Jaxp.selectNodesViaXPath(xPath, rootElement, "//w:binData");

      for (Node node : nodes) {
         Element element = (Element) node;
         String fileName = element.getAttribute("w:name");
         String binData = Jaxp.getElementCharacterData(element);
         process(fileName, binData);
      }
   }

   private void process(String fileName, String binData) throws Exception {
      String extension = Lib.getExtension(fileName);
      if (extension.equalsIgnoreCase("wmz")) {
         String name = fileName.replace("wordml://", "");
         name = Lib.removeExtension(name);

         OutputStream outputStream = null;
         InputStream inputStream = null;
         try {
            basePath.mkdirs();
            byte[] data = DatatypeConverter.parseBase64Binary(binData);
            inputStream = new ByteArrayInputStream(data);
            outputStream = new FileOutputStream(new File(basePath, name + ".gzip"));
            Lib.inputStreamToOutputStream(inputStream, outputStream);
            convert(inputStream, outputStream);
         } finally {
            Lib.close(outputStream);
            Lib.close(inputStream);
         }
      } else {
         System.out.println(extension);
      }
   }

   private void convert(InputStream inputStream, OutputStream outputStream) throws Exception {
      EMZHtmlImageHandler emzHtmlImageHandler = new EMZHtmlImageHandler();
      if (emzHtmlImageHandler.isValid(inputStream)) {
         emzHtmlImageHandler.convert(inputStream, outputStream);
      } else {
         System.out.println("Not valid");
         Lib.inputStreamToOutputStream(inputStream, outputStream);
      }
   }
}
