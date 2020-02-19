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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.result.XConsoleLogger;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.MatchFilter;

/**
 * Validate that all Test or org.junit.Test classes exist in Suite or MasterTestSuite class
 *
 * @author Donald G. Dunne
 */
public class UnitTestValidator {

   private static int suiteCount = 0;
   private static List<File> testClassFiles = new ArrayList<>();
   private static List<File> notForSuiteFiles = new ArrayList<>();
   private static final Map<String, Collection<String>> testClassToSuites = new HashMap<>(100);
   private static final List<File> suites = new ArrayList<>();
   private static final String NOT_FOR_SUITE = "NOT_FOR_SUITE";

   public static void main(String[] args) {

      try {
         StringBuffer sb = new StringBuffer();
         for (String dirNam : Arrays.asList("C:\\UserData\\git\\org.eclipse.osee\\plugins\\",
            "C:\\UserData\\git\\lba.osee\\plugins\\")) {
            File dir1 = new File(dirNam);
            for (String endName : Arrays.asList("test", "tests")) {
               for (String filename : Lib.readListFromDir(dir1, new MatchFilter(".*\\." + endName), true)) {
                  if (!Strings.isValid(filename)) {
                     continue;
                  }
                  System.out.println("Processing BUNDLE " + filename);
                  File file = new File(dir1 + "\\" + filename);
                  recurseAndFind(file, sb);
               }
            }
         }

         Collections.sort(testClassFiles);

         // Search for Suite classes and MasterTestSuite classes and search for test names within
         for (File suiteFile : suites) {
            String suiteFileText = Lib.fileToString(suiteFile);
            //            System.out.println("Searching SUITE " + suiteFile.getName());
            for (File testClass : testClassFiles) {
               String testClassName = testClass.getName();
               if (suiteFileText.contains(testClassName.replaceFirst(".java", ".class"))) {
                  Collection<String> collection = testClassToSuites.get(testClassName);
                  if (collection == null) {
                     collection = new ArrayList<>();
                     testClassToSuites.put(testClassName, collection);
                  }
                  collection.add(suiteFile.getName());
               }
            }
         }

         // output results for test classes without suites
         int noMatchCount = 0;
         XConsoleLogger.err("\n\n");
         for (File testClass : testClassFiles) {
            String testClassName = testClass.getName();
            if (!testClassToSuites.containsKey(testClassName) && !notForSuiteFiles.contains(testClass)) {
               XConsoleLogger.err(String.format("No Suite contains test unit [%s] - Authors [%s]", testClassName,
                  UnitTestUtil.getAuthors(testClass)));
               noMatchCount++;
            }
         }

         // output test classes and # suites in

         System.out.println(String.format("\nTest Cases [%d] Suites [%s] No Match [%d]", testClassFiles.size(),
            suiteCount, noMatchCount));

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
      boolean isSuite = UnitTestUtil.isSuite(file);
      if (file.getAbsolutePath().endsWith(".java") && !isSuite) {
         String text = Lib.fileToString(file);
         if (file.getName().startsWith("Abstract") || text.contains(
            "abstract class " + file.getName().replaceFirst(".java", ""))) {
            System.out.println("Found ABSTRACT TEST " + file.getName() + ", Ignorning");
         } else if (text.contains("Parameterized.class")) {
            System.out.println("Found Parameterized TEST " + file.getName() + ", Ignorning");
         } else if (text.contains("@Test") || text.contains("@org.junit.Test")) {
            System.out.println("Found TEST " + file.getName());
            testClassFiles.add(file);
            sb.append("\n");
         }
         if (text.contains(NOT_FOR_SUITE)) {
            notForSuiteFiles.add(file);
         }
      }
      if (file.getAbsolutePath().endsWith(".java") && isSuite) {
         System.out.println("Found SUITE " + file.getName());
         suites.add(file);
         suiteCount++;
      }
   }
}
