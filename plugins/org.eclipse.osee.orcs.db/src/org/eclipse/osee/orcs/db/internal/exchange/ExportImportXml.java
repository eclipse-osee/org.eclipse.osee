/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.orcs.db.internal.exchange;

import java.io.IOException;

/**
 * @author Roberto E. Escobar
 */
public class ExportImportXml {

   private ExportImportXml() {
   }

   public static void closeXmlNode(Appendable appendable, String nodeName) throws IOException {
      appendable.append("</");
      appendable.append(nodeName);
      appendable.append(">\n");
   }

   public static void openXmlNode(Appendable appendable, String nodeName) throws IOException {
      appendable.append("<");
      appendable.append(nodeName);
      appendable.append(">\n");
   }

   public static void openXmlNodeNoNewline(Appendable appendable, String nodeName) throws IOException {
      appendable.append("<");
      appendable.append(nodeName);
      appendable.append(">");
   }

   public static void openPartialXmlNode(Appendable appendable, String nodeName) throws IOException {
      appendable.append("<");
      appendable.append(nodeName);
      appendable.append(" ");
   }

   public static void addXmlAttribute(Appendable appendable, String name, Object value) throws IOException {
      appendable.append(name);
      appendable.append("=\"");
      appendable.append(String.valueOf(value));
      appendable.append("\" ");
   }

   public static void closePartialXmlNode(Appendable appendable) throws IOException {
      appendable.append("/>\n");
   }

   public static void endOpenedPartialXmlNode(Appendable appendable) throws IOException {
      appendable.append(">\n");
   }
}
