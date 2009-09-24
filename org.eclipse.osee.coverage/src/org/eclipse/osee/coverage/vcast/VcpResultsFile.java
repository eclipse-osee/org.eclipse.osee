/*
 * Created on Sep 23, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.vcast;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.coverage.internal.CoveragePlugin;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class VcpResultsFile {

   private final Map<Value, String> values = new HashMap<Value, String>(20);
   Pattern valuePattern = Pattern.compile("(.*?):(.*?)");

   public static enum Value {
      FILENAME, DIRECTORY, DISPLAY_NAME, RESULT_TYPE, ADDITION_TIME, IS_SELECTED, HAD_COVERAGE_REMOVED
   };

   public VcpResultsFile() {
   }

   public String getValue(Value value) {
      return values.get(value);
   }

   public void addLine(String line) {
      Matcher m = valuePattern.matcher(line);
      if (m.find()) {
         Value value = Value.valueOf(m.group(1));
         if (value == null) {
            OseeLog.log(CoveragePlugin.class, Level.SEVERE, String.format("Unhandled VcpResultsFile value [%s]",
                  m.group(1)));
         } else {
            values.put(value, m.group(2));
         }
      } else {
         OseeLog.log(CoveragePlugin.class, Level.SEVERE, String.format("Unhandled VcpResultsFile line [%s]", line));
      }
   }
}
