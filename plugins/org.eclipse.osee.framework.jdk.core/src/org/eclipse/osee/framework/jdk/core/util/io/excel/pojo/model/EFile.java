/*
 * Created on Jul 10, 2023
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.jdk.core.util.io.excel.pojo.model;

import java.io.File;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/*********************************************************************
 * Copyright (c) 2023 Boeing
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

/**
 * Represents an Excel file that contains an Excel Workbook
 *
 * @author Donald G. Dunne
 */
public class EFile {

   private String filename;
   private final EWorkbook workbook;
   private final XResultData results = new XResultData();

   public EFile(String filename) {
      this.filename = filename;
      File file = new File(filename);
      String name = file.getName();
      workbook = new EWorkbook(name, this);
   }

   public String getFilename() {
      return filename;
   }

   public void setFilename(String filename) {
      this.filename = filename;
   }

   public EWorkbook getWorkbook() {
      return workbook;
   }

   public XResultData getResults() {
      return results;
   }

   @Override
   public String toString() {
      return "EFile [filename=" + filename + ",\n workbook=" + workbook + ",\n results=" + results + "]";
   }

}
