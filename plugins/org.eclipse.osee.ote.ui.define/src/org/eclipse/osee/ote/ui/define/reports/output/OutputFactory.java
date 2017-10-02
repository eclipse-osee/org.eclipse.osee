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

import java.util.Date;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;

/**
 * @author Roberto E. Escobar
 */
public class OutputFactory {
   private static final String SEPARATOR = ".";

   private OutputFactory() {
   }

   public static IReportWriter getReportWriter(OutputFormat format)  {
      IReportWriter toReturn = null;
      switch (format) {
         case EXCEL:
            toReturn = new ExcelReportWriter();
            break;
         case PDF:
         case HTML:
         case RTF:
            toReturn = new ReportWriter(format.name());
            break;
         default:
            throw new OseeArgumentException("Unsupported format [%s]", format);
      }
      return toReturn;
   }

   public static String getOutputFilename(OutputFormat format, String reportId)  {
      String extension = "";
      switch (format) {
         case HTML:
            extension = "html";
            break;
         case EXCEL:
            extension = "xml";
            break;
         case PDF:
            extension = "pdf";
            break;
         case RTF:
            extension = "rtf";
            break;
         default:
            throw new OseeArgumentException("Unsupported format [%s]", format);
      }
      StringBuilder builder = new StringBuilder(reportId);
      builder.append(SEPARATOR);
      builder.append(Long.toString(new Date().getTime()));
      builder.append(SEPARATOR);
      builder.append(extension);
      return builder.toString();
   }

   public static String getContentType(OutputFormat format)  {
      String toReturn = "";
      switch (format) {
         case HTML:
            toReturn = "text/html";
            break;
         case EXCEL:
            toReturn = "application/excel";
            break;
         case PDF:
            toReturn = "application/pdf";
            break;
         case RTF:
            toReturn = "application/rtf";
            break;
         default:
            throw new OseeArgumentException("Unsupported format [%s]", format);
      }
      return toReturn;
   }
}
