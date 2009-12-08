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
package org.eclipse.osee.framework.branch.management.exchange.handler;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.branch.management.exchange.ExportImportXml;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.xml.AbstractSaxHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Roberto E. Escobar
 */
public abstract class BaseExportImportSaxHandler extends AbstractSaxHandler {
   protected final static String STRING_CONTENT = "stringContent";
   protected final static String BINARY_CONTENT_LOCATION = "binaryContentLocation";

   private final Map<String, String> dataMap;

   protected BaseExportImportSaxHandler() {
      super();
      this.dataMap = new HashMap<String, String>();
   }

   @Override
   public void startElementFound(String uri, String localName, String name, Attributes attributes) throws SAXException {
      if (localName.equalsIgnoreCase(ExportImportXml.DATA)) {
         handleData(attributes);
      } else if (localName.equalsIgnoreCase(ExportImportXml.ENTRY)) {
         handleEntry(attributes);
      } else if (localName.equalsIgnoreCase(ExportImportXml.BINARY_CONTENT)) {
         handleBinaryContent(attributes);
      }
   }

   @Override
   public void endElementFound(String uri, String localName, String name) throws SAXException {
      try {
         if (localName.equalsIgnoreCase(ExportImportXml.STRING_CONTENT)) {
            finishStringContent(ExportImportXml.STRING_CONTENT);
         } else if (localName.equalsIgnoreCase(ExportImportXml.OSEE_COMMENT)) {
            finishStringContent(ExportImportXml.OSEE_COMMENT);
         } else if (localName.equalsIgnoreCase(ExportImportXml.BRANCH_NAME)) {
            finishStringContent(ExportImportXml.BRANCH_NAME);
         } else if (localName.equalsIgnoreCase(ExportImportXml.RATIONALE)) {
            finishStringContent(ExportImportXml.RATIONALE);
         } else if (localName.equalsIgnoreCase(ExportImportXml.ENTRY)) {
            finishEntry();
         } else if (localName.equalsIgnoreCase(ExportImportXml.DATA)) {
            finishData();
         }
      } catch (Exception ex) {
         throw new IllegalStateException(ex);
      }
   }

   private void handleData(Attributes attributes) {
      // Do Nothing
   }

   protected void finishData() {
   }

   private void handleEntry(Attributes attributes) {
      this.dataMap.clear();
      int attributeCount = attributes.getLength();
      for (int index = 0; index < attributeCount; index++) {
         String columnName = attributes.getLocalName(index);
         String value = attributes.getValue(index);
         if (Strings.isValid(value) && !value.equals("null")) {
            this.dataMap.put(columnName, value);
         }
      }
   }

   private void handleBinaryContent(Attributes attributes) {
      this.dataMap.put(BINARY_CONTENT_LOCATION, attributes.getValue("location"));
   }

   private void finishEntry() throws Exception {
      if (this.dataMap.isEmpty() != true) {
         processData(this.dataMap);
      }
      this.dataMap.clear();
   }

   private void finishStringContent(String name) {
      this.dataMap.put(name, getContents());
   }

   protected abstract void processData(Map<String, String> dataMap) throws Exception;
}
