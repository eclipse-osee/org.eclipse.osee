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
package org.eclipse.osee.framework.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * @author Andrew M. Finkbeiner
 */
public class SimpleOseeFormatter extends Formatter {
   Date dat = new Date();
   private final StringBuilder sb = new StringBuilder();

   // Line separator string. This is the value of the line.separator
   // property at the moment that the SimpleFormatter was created.
   private final String lineSeparator =
         java.security.AccessController.doPrivileged(new sun.security.action.GetPropertyAction("line.separator"));

   @Override
   public String format(LogRecord record) {
      sb.setLength(0);
      // Minimize memory allocations here.
      dat.setTime(record.getMillis());
      sb.append(dat.toString());
      sb.append("      ");
      sb.append(record.getLoggerName());
      sb.append(lineSeparator);

      Throwable th = new Throwable();
      StackTraceElement[] elements = th.getStackTrace();
      boolean captureNextItem = false;
      for (StackTraceElement el : elements) {
         if (el.getClassName().contains("OseeLog")) {
            captureNextItem = true;
         } else if (captureNextItem) {
            sb.append(String.format("%s   %s   %s (%d)", record.getLevel().getLocalizedName(), el.getClassName(),
                  el.getMethodName(), el.getLineNumber()));
            sb.append(lineSeparator);
            break;
         }
      }

      String message = formatMessage(record);
      if (!captureNextItem) {
         sb.append(record.getLevel().getLocalizedName());
         sb.append("      ");

      }
      sb.append(message);
      sb.append(lineSeparator);
      if (record.getThrown() != null) {
         StringWriter sw = new StringWriter();
         PrintWriter pw = new PrintWriter(sw);
         record.getThrown().printStackTrace(pw);
         pw.close();
         sb.append(sw.toString());
      }
      return sb.toString();
   }

}
