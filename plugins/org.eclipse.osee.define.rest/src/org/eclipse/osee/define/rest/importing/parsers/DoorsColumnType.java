/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.rest.importing.parsers;

import java.util.HashMap;
import java.util.Map;

/**
 * @author David W. Miller
 */
public enum DoorsColumnType {
   ID("ID"),
   REQUIREMENTS("Requirements"),
   OBJECT_NUMBER("Object Number"),
   IS_REQ("Req?"),
   PARENT_ID("Parent ID"),
   EFFECTIVITY("Effectivity"),
   PARAGRAPH_HEADING("Paragraph Heading"),
   DOCUMENT_APPLICABILITY("Document Applicability"),
   VERIFICATION_CRITERIA("Verification Criteria"),
   CHANGE_STATUS("Change Status"),
   OBJECT_HEADING("Proposed Object Heading"),
   OBJECT_TEXT("Proposed Object Text"),
   CHANGE_RATIONALE("Change Rationale"),
   LINKS("Links"),
   GUID("OSEE GUID"),
   SUBSYSTEM("Subsystem"),
   DATA_TYPE("Data Type"),
   OTHER("");

   private final String columnType;
   private final static Map<String, DoorsColumnType> rawStringToDataType = new HashMap<>();

   static {
      for (DoorsColumnType enumStatus : DoorsColumnType.values()) {
         DoorsColumnType.rawStringToDataType.put(enumStatus.columnType, enumStatus);
      }
   }

   DoorsColumnType(String columnName) {
      columnType = columnName;
   }

   public static DoorsColumnType fromString(String value) {
      // special case since Verification Criteria column heading varies by program
      if (value.contains("Verification Criteria")) {
         value = "Verification Criteria";
      }
      DoorsColumnType returnVal = rawStringToDataType.get(value);
      return returnVal != null ? returnVal : OTHER;
   }
}