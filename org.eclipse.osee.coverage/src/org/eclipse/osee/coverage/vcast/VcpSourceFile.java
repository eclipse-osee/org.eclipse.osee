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
public class VcpSourceFile {

   private final Map<SourceValue, String> sourceValues = new HashMap<SourceValue, String>(20);
   Pattern valuePattern = Pattern.compile("(.*?):(.*?)$");
   private VcpSourceLineFile vcpSourceLineFile = null;
   private VcpSourceLisFile vcpSourceLisFile = null;
   private final String vcastDirectory;

   public static enum SourceValue {
      SOURCE_FILENAME,
      SOURCE_DIRECTORY,
      DEST_FILENAME,
      DEST_DIRECTORY,
      DISPLAY_NAME,
      UNIT_NUMBER,
      FILE_TYPE,
      IS_BACKUP,
      COVERAGE,
      COVERAGE_IO_TYPE,
      ADDITION_TIME,
      MODIFIED_TIME,
      CHECKSUM,
      UNINSTR_CHECKSUM
   };

   public VcpSourceFile(String vcastDirectory) {
      this.vcastDirectory = vcastDirectory;
   }

   public String getValue(SourceValue sourceValue) {
      return sourceValues.get(sourceValue);
   }

   public void addLine(String line) {
      Matcher m = valuePattern.matcher(line);
      if (m.find()) {
         SourceValue sourceValue = SourceValue.valueOf(m.group(1));
         if (sourceValue == null) {
            OseeLog.log(Activator.class, Level.SEVERE, String.format("Unhandled VcpSourceFile value [%s]",
                  m.group(1)));
         } else {
            sourceValues.put(sourceValue, m.group(2));
         }
      } else {
         OseeLog.log(Activator.class, Level.SEVERE, String.format("Unhandled VcpSourceFile line [%s]", line));
      }
   }

   public VcpSourceLineFile getVcpSourceLineFile() {
      if (vcpSourceLineFile == null) {
         vcpSourceLineFile = new VcpSourceLineFile(vcastDirectory, this);
      }
      return vcpSourceLineFile;
   }

   public VcpSourceLisFile getVcpSourceLisFile() {
      if (vcpSourceLisFile == null) {
         vcpSourceLisFile = new VcpSourceLisFile(vcastDirectory, this);
      }
      return vcpSourceLisFile;
   }

}
