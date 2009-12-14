/*
 * Created on Sep 23, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.vcast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.vcast.VcpResultsFile.ResultsValue;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.AFile;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * Reads results.dat file that contains <file num> <procedure num> <execution line num>
 * 
 * @author Donald G. Dunne
 */
public class VcpResultsDatFile {

   private final CompositeKeyHashMap<String, String, HashSet<String>> resultsValues =
         new CompositeKeyHashMap<String, String, HashSet<String>>(1000, true);
   Pattern valuePattern = Pattern.compile("\\s+([0-9]+)\\s+([0-9]+)\\s+([0-9]+)");

   public VcpResultsDatFile(String vcastDirectory, VcpResultsFile vcpResultsFile) {
      String resultFilename = vcastDirectory + "/vcast/results/" + vcpResultsFile.getValue(ResultsValue.FILENAME);
      File resultsFile = new File(resultFilename);
      if (!resultsFile.exists()) {
         throw new IllegalArgumentException(String.format("VectorCast resultsFile file doesn't exist [%s]",
               resultFilename));
      }
      for (String resultsLine : AFile.readFile(resultsFile).split("\n")) {
         if (Strings.isValid(resultsLine)) {
            addLine(resultsLine);
         }
      }
   }

   public void addLine(String line) {
      Matcher m = valuePattern.matcher(line);
      if (m.find()) {
         HashSet<String> values = resultsValues.get(m.group(1), m.group(2));
         if (values == null) {
            values = new HashSet<String>();
            resultsValues.put(m.group(1), m.group(2), values);
         }
         values.add(m.group(3));
      } else {
         OseeLog.log(Activator.class, Level.SEVERE, String.format("Unhandled VcpResultsDatFile line [%s]", line));
      }
   }

   public Collection<String> getFileNumbers() {
      List<String> fileNumbers = new ArrayList<String>();
      for (Pair<String, String> pair : resultsValues.keySet()) {
         if (!fileNumbers.contains(pair.getFirst())) {
            fileNumbers.add(pair.getFirst());
         }
      }
      return fileNumbers;
   }

   public Collection<Pair<String, HashSet<String>>> getMethodExecutionPairs(String fileNumber) {
      List<Pair<String, HashSet<String>>> methodExecutionPairs = new ArrayList<Pair<String, HashSet<String>>>();
      for (Pair<String, String> pair : resultsValues.keySet()) {
         if (fileNumber.equals(pair.getFirst())) {
            methodExecutionPairs.add(new Pair<String, HashSet<String>>(pair.getSecond(), resultsValues.get(fileNumber,
                  pair.getSecond())));
         }
      }
      return methodExecutionPairs;
   }
}
