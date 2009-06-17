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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.util.TableWriterAdaptor;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.ui.define.OteUiDefinePlugin;

/**
 * @author Roberto E. Escobar
 */
public class ReportWriter implements IReportWriter {
   private TableWriterAdaptor tableWriterAdapter;
   private ByteArrayOutputStream outputStream;

   public ReportWriter(String writerId) {
      try {
         this.outputStream = new ByteArrayOutputStream();
         this.tableWriterAdapter = new TableWriterAdaptor(writerId, outputStream);
      } catch (Exception ex) {
         OseeLog.log(OteUiDefinePlugin.class, Level.SEVERE, ex);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.ui.define.reports.output.IReportWriter#getReport()
    */
   public String getReport() throws IOException {
      return outputStream.toString(TableWriterAdaptor.ENCODING);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.ui.define.reports.output.IReportWriter#length()
    */
   public int length() throws Exception {
      if (!tableWriterAdapter.isCompleted()) {
         try {
            tableWriterAdapter.openDocument();
            tableWriterAdapter.writeDocument();
         } finally {
            tableWriterAdapter.close();
         }
      }
      return outputStream.toByteArray().length;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.ui.define.reports.output.IReportWriter#writeHeader(java.lang.String[])
    */
   public void writeHeader(String[] headers) {
      try {
         tableWriterAdapter.writeHeader(headers);
      } catch (Exception ex) {
         OseeLog.log(OteUiDefinePlugin.class, Level.SEVERE, ex);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.ui.define.reports.output.IReportWriter#writeRow(java.lang.String[])
    */
   public void writeRow(String... cellData) {
      tableWriterAdapter.writeRow(cellData);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.ui.define.reports.output.IReportWriter#writeTitle(java.lang.String)
    */
   public void writeTitle(String title) {
      tableWriterAdapter.writeTitle(title);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.ui.define.reports.output.IReportWriter#writeToOutput(java.io.OutputStream)
    */
   public void writeToOutput(OutputStream outputStream) throws IOException {
      outputStream.write(this.outputStream.toByteArray());
   }

}
