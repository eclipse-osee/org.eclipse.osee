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
package org.eclipse.osee.framework.jdk.core.util.io.excel.pojo;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.io.excel.pojo.model.EFile;

/**
 * Example of reading an Excel xml file and returning a generic java EFile Pojo and then writing back to Excel file.
 * This has a main and can be run stand-alone. Should read in excel file turn to pojo, then write to excel and open
 * excel file.
 *
 * @author Donald G. Dunne
 */
public class ExcelToPojoOperationExample {

   public static void main(String[] args) {

      EFile eFile = getExcelToPojo();

      PojoToExcelOperation op2 = new PojoToExcelOperation(eFile, "ExampleExcelWorkbookOut.xml");
      File outFile = op2.run();

      Desktop desktop = Desktop.getDesktop();
      if (outFile.exists()) {
         try {
            desktop.open(outFile);
         } catch (IOException ex) {
            System.err.println(Lib.exceptionToString(ex));
         }
      }

   }

   public static EFile getExcelToPojo() {
      URL url = ExcelToPojoOperationExample.class.getResource("ExampleExcelWorkbook.xml");
      ExcelToPojoOperation op = new ExcelToPojoOperation(url.getFile());
      EFile eFile = op.run();

      return eFile;
   }

}
