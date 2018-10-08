/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.internal.writer;

/**
 * @author Donald G. Dunne
 */
public class OrcsWriterUtil {

   public static final String PERSIST_COMMENT_SETTING = "PERSIST COMMENT";
   public static final String AS_USER_ID_SETTING = "AS USER ID";
   public static final String BRANCH_TOKEN_SETTING = "BRANCH TOKEN";
   public static final String INSTRUCTIONS_AND_SETTINGS_SHEET_NAME = "Instructions and Settings";
   public static final String CREATE_SHEET_NAME = "CREATE";
   public static final String DELETE_SHEET_NAME = "DELETE";
   public static final String UPDATE_SHEET_NAME = "UPDATE";

   private OrcsWriterUtil() {
      // Utility Class
   }

   public static String getData(String sheetName, int rowCount, int colCount, String data) {
      if (data != null) {
         data += ", ";
      }
      data += getRowColumnStr(rowCount, colCount, sheetName);
      return data;
   }

   public static String getData(String sheetName, int rowCount, int colCount) {

      return getRowColumnStr(rowCount, colCount, sheetName);

   }

   public static String getRowColumnStr(int rowCount, int colCount, String sheetName) {
      return " sheet=" + sheetName + " row=" + rowCount + ", column=" + (colCount + 1);
   }

}
