/*
 * Created on Sep 22, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.test;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.coverage.CoverageItem;
import org.eclipse.osee.coverage.CoverageMethodEnum;
import org.eclipse.osee.coverage.CoverageUnit;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AFile;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class SampleJavaFileParser {

   private static final Pattern methodPatter = Pattern.compile("   (public|private) (.*?) (.*?)\\(\\) { *");
   private static final Pattern executeLine = Pattern.compile("   // (.), (.), (.)");

   public static CoverageUnit createCodeUnit(String filename) throws OseeCoreException {
      if (!Strings.isValid(filename)) {
         throw new IllegalArgumentException("Valid filename must be specified");
      }
      File file = new File(filename);
      if (!file.exists()) {
         throw new IllegalArgumentException(String.format("File doesn't exist [%s]", filename));
      }
      String fileStr = AFile.readFile(file);
      StringBuffer method = new StringBuffer();
      CoverageUnit coverageUnit = null;
      int lineNum = 0;
      for (String line : fileStr.split("\n")) {
         lineNum++;
         Matcher m = methodPatter.matcher(line);
         if (m.find()) {
            String name = m.group(3);
            coverageUnit = new CoverageUnit(name, "Line " + lineNum);
         }
         m = executeLine.matcher(line);
         if (m.find()) {
            String methodNum = m.group(1);
            String executeNumStr = m.group(2);
            int executeNum = new Integer(executeNumStr).intValue();
            boolean covered = m.group(3).equals("y");
            CoverageItem coverageItem =
                  new CoverageItem(covered ? CoverageMethodEnum.Test_Unit : CoverageMethodEnum.None, executeNum);
            coverageItem.setLineNum(lineNum);
            coverageUnit.addCoverageItem(coverageItem);
         }

      }
      return coverageUnit;
   }
}
