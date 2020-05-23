/*********************************************************************
 * Copyright (c) 2011 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.support.test.util;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class UnitTestUtil {

   public static boolean isUnitTest(File file) throws IOException {
      String text = Lib.fileToString(file);
      return isUnitTest(text);
   }

   public static boolean isUnitTest(String fileContents) {
      return fileContents.contains("@Test") || fileContents.contains("@org.junit.Test");
   }

   public static boolean isSuite(File file) {
      return file.getAbsolutePath().endsWith(".java") && file.getName().contains("Suite");
   }

   public static boolean isTestUtil(File file) {
      return file.getAbsolutePath().endsWith(".java") && file.getName().contains("Util");
   }

   public static boolean isMock(File file) {
      return file.getAbsolutePath().endsWith(".java") && file.getName().contains("Mock");
   }

   public static Set<String> getAuthors(File file) throws IOException {
      String text = Lib.fileToString(file);
      return getAuthors(text);

   }

   public static Set<String> getAuthors(String fileContents) {
      Set<String> authors = new HashSet<>();
      for (String line : fileContents.split("\n")) {
         if (line.contains("* @author")) {
            String author = line;
            //            System.out.println("  " + author);
            author = author.replaceAll(":", "");
            author = author.replaceAll(System.getProperty("line.separator"), "");
            author = author.replaceFirst("^.*@author *", "");
            author = author.replaceFirst(" *$", "");
            author = author.replaceAll("\\s+$", "");
            if (Strings.isValid(author)) {
               authors.add(author);
            }
         }
      }
      return authors;
   }

   private final static Pattern testMethodPattern = Pattern.compile("(@Test|@org.junit.Test)");

   public static int getTestMethodCount(String fileContents) {
      Matcher m = testMethodPattern.matcher(fileContents);
      int fileTestPointCount = 0;
      while (m.find()) {
         fileTestPointCount++;
      }
      return fileTestPointCount;
   }

   private final static Pattern extendsPattern = Pattern.compile("class (.*) extends (.*) ");

   public static int getTestMethodCountFromSuperclass(File file, String text) throws IOException {
      Matcher m = extendsPattern.matcher(text);
      if (m.find()) {
         String superClassName = m.group(2);
         //         System.out.println("Found SUPERCLASS " + superClassName);
         if (Strings.isValid(superClassName)) {
            String fullPath = file.getAbsolutePath();
            File superFile = new File(fullPath.replaceFirst(file.getName(), superClassName + ".java"));
            if (superFile.exists()) {
               return getTestMethodCount(Lib.fileToString(superFile));
            }
         }
      }
      return 0;
   }

}
