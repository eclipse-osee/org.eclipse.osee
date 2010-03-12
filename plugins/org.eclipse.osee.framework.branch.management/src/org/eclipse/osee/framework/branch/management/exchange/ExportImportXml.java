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
package org.eclipse.osee.framework.branch.management.exchange;

import java.io.IOException;

/**
 * @author Roberto E. Escobar
 */
public class ExportImportXml {
   public static final String XML_ENCODING = "UTF-8";
   public static final String XML_EXTENSION = ".xml";
   public static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"" + XML_ENCODING + "\" ?>\n";

   public static final String RESOURCE_FOLDER_NAME = "resources";
   public static final String METADATA = "metadata";
   public static final String DATA = "data";
   public static final String ENTRY = "entry";
   public static final String BINARY_CONTENT = "binaryContent";
   public static final String STRING_CONTENT = "stringContent";
   public static final String OSEE_COMMENT = "osee_comment";
   public static final String BRANCH_NAME = "branch_name";
   public static final String TABLE = "table";
   public static final String TABLE_NAME = "name";
   public static final String COLUMN = "column";
   public static final String ID = "id";
   public static final String TYPE = "type";
   public static final String PRIORITY = "priority";
   public static final String OPTIONS = "options";
   public static final String SOURCE = "source";
   public static final String DB_SCHEMA = "db.metadata";
   public static final String PART_OF_BRANCH = "part_of_branch_id";
   public static final String RATIONALE = "rationale";
   public static final String EXPORT_ENTRY = "export";
   public static final String EXPORT_VERSION = "exportVersion";
   public static final String DATABASE_ID = "databaseId";
   public static final String EXPORT_DATE = "exportDate";
   public static final String TYPE_GUID = "type_guid";
   public static final String ART_TYPE_ID = "art_type_id";
   public static final String REL_TYPE_ID = "rel_link_type_id";
   public static final String ATTR_TYPE_ID = "attr_type_id";

   private ExportImportXml() {
   }

   public static void closeXmlNode(Appendable appendable, String nodeName) throws IOException {
      appendable.append(String.format("</%s>\n", nodeName));
   }

   public static void openXmlNode(Appendable appendable, String nodeName) throws IOException {
      appendable.append(String.format("<%s>\n", nodeName));
   }

   public static void openXmlNodeNoNewline(Appendable appendable, String nodeName) throws IOException {
      appendable.append(String.format("<%s>", nodeName));
   }

   public static void openPartialXmlNode(Appendable appendable, String nodeName) throws IOException {
      appendable.append(String.format("<%s ", nodeName));
   }

   public static void addXmlAttribute(Appendable appendable, String name, Object value) throws IOException {
      appendable.append(String.format("%s=\"%s\" ", name, value));
   }

   public static void closePartialXmlNode(Appendable appendable) throws IOException {
      appendable.append(" />\n");
   }

   public static void endOpenedPartialXmlNode(Appendable appendable) throws IOException {
      appendable.append(">\n");
   }
}
