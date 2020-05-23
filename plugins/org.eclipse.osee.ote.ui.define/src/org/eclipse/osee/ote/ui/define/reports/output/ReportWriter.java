/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ote.ui.define.reports.output;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.util.TableWriterAdaptor;
import org.eclipse.osee.ote.ui.define.internal.Activator;

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
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   @Override
   public String getReport() throws IOException {
      return outputStream.toString(TableWriterAdaptor.ENCODING);
   }

   @Override
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

   @Override
   public void writeHeader(String[] headers) {
      try {
         tableWriterAdapter.writeHeader(headers);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   @Override
   public void writeRow(String... cellData) {
      tableWriterAdapter.writeRow(cellData);
   }

   @Override
   public void writeTitle(String title) {
      tableWriterAdapter.writeTitle(title);
   }

   @Override
   public void writeToOutput(OutputStream outputStream) throws IOException {
      outputStream.write(this.outputStream.toByteArray());
   }

}
