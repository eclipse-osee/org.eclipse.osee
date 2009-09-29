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
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class VcpResultsFile {

   private final Map<ResultsValue, String> resultsValues = new HashMap<ResultsValue, String>(20);
   Pattern valuePattern = Pattern.compile("(.*?):(.*?)$");
   private VcpResultsDatFile vcpResultsDatFile;
   private final String vcastDirectory;

   public static enum ResultsValue {
      FILENAME, DIRECTORY, DISPLAY_NAME, RESULT_TYPE, ADDITION_TIME, IS_SELECTED, HAD_COVERAGE_REMOVED
   };

   public VcpResultsFile(String vcastDirectory) {
      this.vcastDirectory = vcastDirectory;
   }

   public String getValue(ResultsValue resultsValue) {
      return resultsValues.get(resultsValue);
   }

   public void addLine(String line) {
      Matcher m = valuePattern.matcher(line);
      if (m.find()) {
         ResultsValue resultsValue = ResultsValue.valueOf(m.group(1));
         if (resultsValue == null) {
            OseeLog.log(Activator.class, Level.SEVERE, String.format("Unhandled VcpResultsFile value [%s]",
                  m.group(1)));
         } else {
            resultsValues.put(resultsValue, m.group(2));
         }
      } else {
         OseeLog.log(Activator.class, Level.SEVERE, String.format("Unhandled VcpResultsFile line [%s]", line));
      }
   }

   public VcpResultsDatFile getVcpResultsDatFile() {
      if (vcpResultsDatFile == null) {
         vcpResultsDatFile = new VcpResultsDatFile(vcastDirectory, this);
      }
      return vcpResultsDatFile;
   }

   @Override
   public String toString() {
      return getValue(ResultsValue.FILENAME);
   }
}
