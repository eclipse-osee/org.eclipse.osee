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
public enum DoorsDataType {
   HEADING("Heading", true),
   HEADER("Header", true),
   INFORMATION("Information", false),
   REQUIREMENT("Requirement", false),
   DESIGN_REQ("Design Req", true),
   FUNCTIONAL_REQ("Functional Req", true),
   PERFORMANCE_REQ("Performance Req", true),
   PHYSICAL_REQ("Physical Req", true),
   SAFETY_REQ("Safety Req", true),
   ENVIRONMENT_REQ("Environment Req", true),
   INTERFACE_REQ("Interface Req", true),
   TABLE("Table", false),
   FIGURE("Figure", false),
   LIST("List", false),
   NOT_DEFINED("Not Defined", false),
   OTHER("", false);

   private final String _dataType;
   private final Boolean isSingle; // marks the type as single, that is, not to be combined during processing
   private final static Map<String, DoorsDataType> rawStringToDataType = new HashMap<>();

   static {
      for (DoorsDataType enumStatus : DoorsDataType.values()) {
         DoorsDataType.rawStringToDataType.put(enumStatus._dataType, enumStatus);
      }
   }

   DoorsDataType(String dataType, Boolean single) {
      _dataType = dataType;
      isSingle = single;
   }

   public static synchronized DoorsDataType fromString(String value) {
      DoorsDataType returnVal = rawStringToDataType.get(value);
      return returnVal != null ? returnVal : OTHER;
   }

   public Boolean isSingle() {
      return isSingle;
   }
}