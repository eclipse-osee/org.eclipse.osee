/*
 * Created on Sep 23, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.vcast;

import java.io.File;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.vcast.VcpSourceFile.SourceValue;
import org.eclipse.osee.framework.jdk.core.util.AFile;

/**
 * Reads results.dat file that contains <file num> <procedure num> <execution line num>
 * 
 * @author Donald G. Dunne
 */
public class VcpSourceLineFile {

   Pattern pattern = Pattern.compile("NAME:(\\w*)\\s+FILE:\\s*(.+?)\\s+START:\\s*([0-9]+)\\s+END:\\s*([0-9]+)");
   File resultsFile = null;
   private final VcpSourceFile vcpSourceFile;

   public VcpSourceLineFile(String vcastDirectory, VcpSourceFile vcpSourceFile) {
      this.vcpSourceFile = vcpSourceFile;
      String lineFilename = vcastDirectory + "/vcast/LINE." + vcpSourceFile.getValue(SourceValue.UNIT_NUMBER);
      resultsFile = new File(lineFilename);
      if (!resultsFile.exists()) {
         throw new IllegalArgumentException(
               String.format("VectorCast LINE.<num> file doesn't exist [%s]", lineFilename));
      }
   }

   public void createCoverageUnits(CoverageUnit parentCoverageUnit) {
      VcpSourceLisFile vcpSourceLisFile = vcpSourceFile.getVcpSourceLisFile();
      String contents = AFile.readFile(resultsFile);
      Matcher m = pattern.matcher(contents);
      while (m.find()) {
         CoverageUnit coverageUnit =
               new CoverageUnit(parentCoverageUnit, m.group(1), m.group(2) + ":" + m.group(3) + "-" + m.group(4));
         String source = Arrays.toString(vcpSourceLisFile.getSection(m.group(3), m.group(4)));
         coverageUnit.setText(source);
         parentCoverageUnit.addCoverageUnit(coverageUnit);
      }
   }
}
