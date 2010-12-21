/*
 * Created on Dec 21, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.support.test.util;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.type.CountingMap;
import org.eclipse.osee.framework.jdk.core.type.MutableInteger;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.MatchFilter;

public class UnitTestCounter {

   private static int unitTestCount = 0, testPointCount = 0;
   private static final CountingMap<String> authorToFileCount = new CountingMap<String>(10);
   private static final CountingMap<String> authorToTestPointCount = new CountingMap<String>(10);

   public static void main(String[] args) {

      try {
         StringBuffer sb = new StringBuffer();
         for (String dirNam : Arrays.asList("C:\\UserData\\git\\org.eclipse.osee\\plugins\\",
            "C:\\UserData\\git\\lba.osee\\plugins\\")) {
            File dir1 = new File(dirNam);
            for (String filename : Lib.readListFromDir(dir1, new MatchFilter(".*\\.test"), true)) {
               if (!Strings.isValid(filename)) {
                  continue;
               }
               System.out.println(String.format("Processing [%s]", filename));
               File file = new File(dir1 + "\\" + filename);
               recurseAndFind(file, sb);
            }
         }

         StringBuffer results = new StringBuffer();
         results.append("Test Unit Total, " + unitTestCount + "\n");
         for (Entry<String, MutableInteger> entry : authorToFileCount.getCounts()) {
            results.append(entry.getKey() + ", " + entry.getValue() + "\n");
         }
         results.append("\n\n");
         results.append("Test Point Total, " + testPointCount + "\n");
         for (Entry<String, MutableInteger> entry : authorToTestPointCount.getCounts()) {
            results.append(entry.getKey() + ", " + entry.getValue() + "\n");
         }
         results.append("\n\n");
         results.append(sb.toString());

         String outputFilename = "C:\\UserData\\UnitTestCounter.csv";
         System.out.println("\n\nResults written to " + outputFilename + "\n");
         Lib.writeStringToFile(results.toString(), new File(outputFilename));
      } catch (Exception ex) {
         System.out.println(ex.getLocalizedMessage());
      }
   }

   private static void recurseAndFind(File file, StringBuffer sb) throws IOException {
      if (file.isDirectory()) {
         for (String filename : Lib.readListFromDir(file, new MatchFilter(".*"), true)) {
            File childFile = new File(file.getAbsolutePath() + "\\" + filename);
            recurseAndFind(childFile, sb);
         }
      }
      if (file.getAbsolutePath().endsWith(".java") && !file.getName().contains("UnitTestCounter")) {
         String text = Lib.fileToString(file);
         if (text.contains("@Test") || text.contains("@org.junit.Test")) {
            System.err.println("Found java test file " + file.getName());
            sb.append(file.getName() + ", ");
            unitTestCount++;
            for (String line : text.split("\n")) {
               if (line.contains("* @author")) {
                  String author = line;
                  System.out.println("  " + author);
                  author = author.replaceAll(":", "");
                  author = author.replaceAll(System.getProperty("line.separator"), "");
                  author = author.replaceFirst("^.*@author *", "");
                  author = author.replaceFirst(" *$", "");
                  authorToFileCount.put(author);
                  sb.append(author);

                  Matcher m = Pattern.compile("(@Test|@org.junit.Test)").matcher(text);
                  int fileTestPointCount = 0;
                  while (m.find()) {
                     fileTestPointCount++;
                  }
                  testPointCount += fileTestPointCount;
                  authorToTestPointCount.put(author, fileTestPointCount);
               }
            }
            sb.append("\n");
         } else {
            System.err.println("NOT TEST FILE " + file.getName());
         }
      }
   }
}
