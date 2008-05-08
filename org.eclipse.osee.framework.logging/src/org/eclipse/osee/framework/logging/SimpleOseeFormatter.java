
package org.eclipse.osee.framework.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class SimpleOseeFormatter extends Formatter {
   Date dat = new Date();
   private StringBuilder sb = new StringBuilder();

   // Line separator string. This is the value of the line.separator
   // property at the moment that the SimpleFormatter was created.
   @SuppressWarnings("unchecked")
   private String lineSeparator = (String) java.security.AccessController.doPrivileged(new sun.security.action.GetPropertyAction("line.separator"));

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
         }
         else if (captureNextItem) {
            sb.append(String.format("%s   %s   %s (%d)", record.getLevel().getLocalizedName(), el.getClassName(), el.getMethodName(), el.getLineNumber()));
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
         try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            record.getThrown().printStackTrace(pw);
            pw.close();
            sb.append(sw.toString());
         }
         catch (Exception ex) {
         }
      }
      return sb.toString();
   }

}
