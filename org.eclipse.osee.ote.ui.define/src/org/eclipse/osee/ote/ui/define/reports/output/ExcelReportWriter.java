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
package org.eclipse.osee.ote.ui.define.reports.output;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ISheetWriter;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.ui.define.OteUiDefinePlugin;

/**
 * @author Roberto E. Escobar
 */
public class ExcelReportWriter implements IReportWriter {
   private ISheetWriter sheetWriter;
   private StringWriter stringWriter;
   private String result;
   private String title;

   public ExcelReportWriter() {
      this.title = Long.toString(new Date().getTime());
      this.stringWriter = new StringWriter();
      try {
         this.sheetWriter = new ExcelXmlWriter(stringWriter);
      } catch (IOException ex) {
         OseeLog.log(OteUiDefinePlugin.class, Level.SEVERE, ex);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.ui.define.reports.output.IReportWriter#getReport()
    */
   public String getReport() throws IOException {
      if (result == null) {
         generate();
      }
      return result;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.ui.define.reports.output.IReportWriter#length()
    */
   public int length() throws IOException {
      if (result == null) {
         generate();
      }
      return result.length();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.ui.define.reports.output.IReportWriter#writeHeader(java.lang.String[])
    */
   public void writeHeader(String[] headers) {
      try {
         this.sheetWriter.startSheet(title, headers.length);
         this.sheetWriter.writeRow(headers);
      } catch (IOException ex) {
         OseeLog.log(OteUiDefinePlugin.class, Level.SEVERE, ex);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.ui.define.reports.output.IReportWriter#writeRow(java.lang.String[])
    */
   public void writeRow(String... cellData) {
      try {
         this.sheetWriter.writeRow(cellData);
      } catch (IOException ex) {
         OseeLog.log(OteUiDefinePlugin.class, Level.SEVERE, ex);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.ui.define.reports.output.IReportWriter#writeTitle(java.lang.String)
    */
   public void writeTitle(String title) {
      this.title = title;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.ui.define.reports.output.IReportWriter#writeToOutput(java.io.OutputStream)
    */
   public void writeToOutput(OutputStream outputStream) throws IOException {
      if (result == null) {
         generate();
      }
      outputStream.write(result.getBytes("UTF-8"));
   }

   private void generate() throws IOException {
      this.sheetWriter.endSheet();
      this.sheetWriter.endWorkbook();
      this.result = stringWriter.toString();
   }
}