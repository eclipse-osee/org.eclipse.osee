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
package org.eclipse.osee.framework.ui.swt;

import java.io.BufferedReader;
import java.util.logging.Level;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.eclipse.osee.framework.logging.OseeLog;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.xml.sax.InputSource;

/**
 * @author Jaden W. Puckett
 */
public class MsoApplicationExtractor {

   public static String findMsoApplicationValue(BufferedReader xmlContent) throws Exception {
      if (xmlContent == null) {
         throw new IllegalArgumentException("BufferedReader is null");
      }
      return extractMsoApplicationValue(xmlContent);
   }

   private static String extractMsoApplicationValue(BufferedReader xmlContent) throws Exception {
      try {
         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         factory.setNamespaceAware(true);
         DocumentBuilder builder = factory.newDocumentBuilder();
         InputSource inputSource = new InputSource(xmlContent);
         Document document = builder.parse(inputSource);
         // Check all nodes for a processing instruction
         Node rootNode = document.getFirstChild();
         while (rootNode != null) {
            if (rootNode.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE) {
               ProcessingInstruction pi = (ProcessingInstruction) rootNode;
               if ("mso-application".equals(pi.getTarget())) {
                  // Return the data (e.g., `progid="Word.Document"`)
                  return pi.getData();
               }
            }
            rootNode = rootNode.getNextSibling();
         }
      } catch (Exception e) {
         OseeLog.log(MsoApplicationExtractor.class, Level.SEVERE, e);
      }
      return ""; // Return "" if no processing instruction is found
   }
}