/*
 * Created on Sep 22, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoverageMethodEnum;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Donald G. Dunne
 */
public class SampleJavaFileParser {

   private static final Pattern methodPatter =
         Pattern.compile("\\s+(public|private)\\s(\\w+)\\s(\\w+)\\(.*\\)\\s+\\{\\s*");
   private static final Pattern executeLine = Pattern.compile("\\s+//\\s+(\\w+),\\s+(\\w+),\\s+(\\w+)");

   public static CoverageUnit createCodeUnit(URL url) throws OseeCoreException {
      try {
         if (url == null) {
            throw new IllegalArgumentException("Valid filename must be specified");
         }
         InputStream inputStream = url.openStream();
         if (inputStream == null) {
            throw new IllegalArgumentException(String.format("File doesn't exist [%s]", url));
         }
         CoverageUnit fileCoverageUnit = new CoverageUnit(null, url.getFile(), "");
         String fileStr = Lib.inputStreamToString(inputStream);
         CoverageUnit coverageUnit = null;
         int lineNum = 0;
         for (String line : fileStr.split("\r\n")) {
            lineNum++;
            Matcher m = methodPatter.matcher(line);
            if (m.find()) {
               String name = m.group(3);
               coverageUnit = new CoverageUnit(fileCoverageUnit, name, "Line " + lineNum);
               fileCoverageUnit.addCoverageUnit(coverageUnit);
            }
            m = executeLine.matcher(line);
            if (m.find()) {
               String methodNum = m.group(1);
               String executeNumStr = m.group(2);
               int executeNum = new Integer(executeNumStr).intValue();
               boolean covered = m.group(3).equals("y");
               CoverageItem coverageItem =
                     new CoverageItem(coverageUnit, covered ? CoverageMethodEnum.Test_Unit : CoverageMethodEnum.None,
                           executeNum);
               coverageItem.setLineNum(lineNum);
               coverageItem.setMethodNum(new Integer(methodNum).intValue());
               coverageUnit.addCoverageItem(coverageItem);
            }
         }
         return fileCoverageUnit;
      } catch (IOException ex) {
         throw new OseeWrappedException(ex);
      }
   }
}
