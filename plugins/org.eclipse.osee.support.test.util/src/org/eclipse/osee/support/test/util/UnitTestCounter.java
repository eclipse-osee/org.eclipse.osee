/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.support.test.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.result.XConsoleLogger;
import org.eclipse.osee.framework.jdk.core.type.CountingMap;
import org.eclipse.osee.framework.jdk.core.type.MutableInteger;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.MatchFilter;

/**
 * Count all JUnit classes and sort/count by author
 *
 * @author Donald G. Dunne
 */
public class UnitTestCounter {

   private static int unitTestCount = 0, testPointCount = 0;
   private static final CountingMap<String> authorToFileCount = new CountingMap<>(10);
   private static final CountingMap<String> authorToTestPointCount = new CountingMap<>(10);

   public static void main(String[] args) {

      try {
         StringBuffer results = new StringBuffer();
         StringBuffer errors = new StringBuffer();
         for (String dirNam : Arrays.asList("C:\\UserData\\git\\org.eclipse.osee\\plugins\\",
            "C:\\UserData\\git\\lba.osee\\plugins\\")) {
            File dir1 = new File(dirNam);
            for (String filename : Lib.readListFromDir(dir1, new MatchFilter(".*\\.test"), true)) {
               if (!Strings.isValid(filename)) {
                  continue;
               }
               System.out.println(String.format("Processing [%s]", filename));
               File file = new File(dir1 + "\\" + filename);
               recurseAndFind(file, results, errors);
            }
         }

         results.append("\n\nTest Unit Total (file with at least 1 test case), " + unitTestCount + "\n");

         List<String> names = new ArrayList<>();
         for (Entry<String, MutableInteger> entry : authorToFileCount.getCounts()) {
            System.out.println(String.format("Author [%s]", entry.getKey()));
            names.add(entry.getKey() + ", " + entry.getValue() + "\n");
         }
         Collections.sort(names);
         for (String name : names) {
            results.append(name);
         }

         results.append("\n\nTest Case Total (@org.junit.Test or @Test), " + testPointCount + "\n");
         names.clear();
         for (Entry<String, MutableInteger> entry : authorToTestPointCount.getCounts()) {
            names.add(entry.getKey() + ", " + entry.getValue() + "\n");
         }
         Collections.sort(names);
         for (String name : names) {
            results.append(name);
         }

         results.append("\n\n");
         results.append(results.toString());

         XConsoleLogger.err("\n\n" + errors);
         String outputFilename = "C:\\UserData\\UnitTestCounter.csv";
         System.out.println("\n\nResults written to " + outputFilename + "\n");
         Lib.writeStringToFile(errors.toString() + "\n\n" + results.toString(), new File(outputFilename));

      } catch (Exception ex) {
         System.out.println(ex.getLocalizedMessage());
      }
   }

   private static void recurseAndFind(File file, StringBuffer results, StringBuffer errors) throws IOException {
      if (file.isDirectory()) {
         for (String filename : Lib.readListFromDir(file, new MatchFilter(".*"), true)) {
            File childFile = new File(file.getAbsolutePath() + "\\" + filename);
            recurseAndFind(childFile, results, errors);
         }
      }
      if (file.getAbsolutePath().endsWith(
         ".java") && !file.getName().contains("UnitTestCounter") && !file.getName().contains("UnitTestUtil")) {
         String text = Lib.fileToString(file);
         if (UnitTestUtil.isUnitTest(text) || file.getAbsolutePath().endsWith("Test.java")) {
            results.append(file.getName() + ", ");
            unitTestCount++;
            Set<String> authors = UnitTestUtil.getAuthors(text);
            if (authors.isEmpty()) {
               errors.append(String.format("File [%s] has no authors\n", file.getName()));
            }
            int fileTestPointCount = UnitTestUtil.getTestMethodCount(text);
            int fileTestPointCountFromSuperclass = UnitTestUtil.getTestMethodCountFromSuperclass(file, text);
            int totalFileTestPointCount = fileTestPointCount + fileTestPointCountFromSuperclass;
            testPointCount += totalFileTestPointCount;

            for (String author : authors) {
               results.append(author + "; ");
               authorToFileCount.put(author);
               authorToTestPointCount.put(author, fileTestPointCount + fileTestPointCountFromSuperclass);
            }
            XConsoleLogger.err(String.format("[%s] tests [ main:%s super:%s ]found in %s", totalFileTestPointCount,
               fileTestPointCount, fileTestPointCountFromSuperclass, file.getName()));
            results.append("\n");
         } else if (!UnitTestUtil.isSuite(file) && !UnitTestUtil.isMock(file) && !UnitTestUtil.isTestUtil(file)) {
            System.out.println("NOT TEST FILE " + file.getName());
         }
      }
   }
}
