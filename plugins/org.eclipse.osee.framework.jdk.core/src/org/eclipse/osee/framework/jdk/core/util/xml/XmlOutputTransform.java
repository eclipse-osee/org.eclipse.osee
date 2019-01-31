/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.jdk.core.util.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * @author Roberto E. Escobar
 */
public final class XmlOutputTransform {

   // Prevent Instantiation
   private XmlOutputTransform() {
   }

   protected static void xmlToHtml(InputStream inputXML, InputStream inputXslt, Result result) throws Exception {
      Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(inputXslt));
      xmlToHtml(inputXML, transformer, result);
   }

   private static void xmlToHtml(InputStream inputXML, Transformer transformer, Result result) throws Exception {
      try {
         transformer.transform(new StreamSource(inputXML), result);
      } catch (Exception ex) {
         throw new Exception("Error during Transform. ", ex);
      }
   }

   private static void xmlToHtml(InputStream inputXML, InputStream inputXslt, Writer result) throws Exception {
      boolean isNetworked = false;

      if (true != isNetworked) {
         // Perform Transform Directly
         xmlToHtml(inputXML, inputXslt, new StreamResult(result));
      } else {
         // Perform Transform as a Separate process by launching an XmlTransformServer.
         XmlTransformAsProcess.getHtmlFromXml(inputXML, inputXslt, result);
      }
   }

   public static String xmlToHtmlString(String sourceFile, File transformToApply) {
      return xmlToHtmlString(new File(sourceFile), transformToApply);
   }

   public static String xmlToHtmlString(File sourceFile, String transformToApply) {
      return xmlToHtmlString(sourceFile, new File(transformToApply));
   }

   public static String xmlToHtmlString(String sourceFile, String transformToApply) {
      return xmlToHtmlString(new File(sourceFile), new File(transformToApply));
   }

   public static File xmlToHtmlFile(File sourceFile, File transformToApply) throws Exception {
      File file = new File(sourceFile.getAbsolutePath().replace(".tmo", ".html"));
      xmlToHtml(new FileInputStream(sourceFile), new FileInputStream(transformToApply), new FileWriter(file));
      return file;
   }

   public static String xmlToHtmlString(File sourceFile, File transformToApply) {
      StringWriter sWriter = new StringWriter();
      try {
         xmlToHtml(new FileInputStream(sourceFile), new FileInputStream(transformToApply), sWriter);
      } catch (Exception e) {
         e.printStackTrace();
      }
      return sWriter.toString();
   }

   public static String xmlToHtmlString(InputStream sourceXML, Transformer transformer) throws Exception {
      StringWriter sWriter = new StringWriter();
      xmlToHtml(sourceXML, transformer, new StreamResult(sWriter));
      return sWriter.toString();
   }
}
