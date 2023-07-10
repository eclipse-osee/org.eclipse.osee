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

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.io.excel.pojo.model.ECell;
import org.eclipse.osee.framework.jdk.core.util.io.excel.pojo.model.EFile;
import org.eclipse.osee.framework.jdk.core.util.io.excel.pojo.model.EHeaderCell;
import org.eclipse.osee.framework.jdk.core.util.io.excel.pojo.model.ERow;
import org.eclipse.osee.framework.jdk.core.util.io.excel.pojo.model.EWorksheet;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;

/**
 * Take a EFile Pojo and write out to Excel xml file.
 *
 * @author Donald G. Dunne
 */
public class PojoToExcelOperation {

   private final String excelFile;
   private final EFile eFile;

   public PojoToExcelOperation(EFile eFile, String excelFile) {
      this.eFile = eFile;
      this.excelFile = excelFile;
   }

   public File run() {
      File outFile = new File(excelFile);
      try {

         FileOutputStream outputStream = new FileOutputStream(outFile);
         Writer writer = new OutputStreamWriter(outputStream, "UTF-8");
         ExcelXmlWriter sheetWriter = new ExcelXmlWriter(writer);

         for (EWorksheet sheet : eFile.getWorkbook().getSheets()) {

            List<String> headers = new ArrayList<>();
            for (EHeaderCell hCell : sheet.getHeader().getHcells()) {
               headers.add(hCell.getName());
            }
            int columns = headers.size();
            sheetWriter.startSheet(sheet.getName(), columns);

            sheetWriter.writeRow(headers.toArray(new Object[columns]));

            for (ERow row : sheet.getRows()) {
               List<String> rowVals = new ArrayList<>();
               for (ECell cell : row.getCells()) {
                  rowVals.add(cell.getValue());
               }
               sheetWriter.writeRow(rowVals.toArray(new Object[rowVals.size()]));
            }
            sheetWriter.endSheet();
         }
         sheetWriter.endWorkbook();

         writer.flush();
         writer.close();

      } catch (Exception ex) {
         eFile.getResults().errorf("Exception reading input: %s", Lib.exceptionToString(ex));
      }
      return outFile;
   }

}
