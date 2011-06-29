/*******************************************************************************
 * Copyright (c) 2011 Boeing.
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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;

public class UnitTestUtil {

   public static boolean isUnitTest(File file) throws IOException {
      String text = Lib.fileToString(file);
      return isUnitTest(text);
   }

   public static boolean isUnitTest(String fileContents) {
      return (fileContents.contains("@Test") || fileContents.contains("@org.junit.Test"));
   }

   public static boolean isSuite(File file) {
      return (file.getAbsolutePath().endsWith(".java") && file.getName().contains("Suite"));
   }

   public static List<String> getAuthors(File file) throws IOException {
      String text = Lib.fileToString(file);
      return getAuthors(text);

   }

   public static List<String> getAuthors(String fileContents) {
      List<String> authors = new ArrayList<String>();
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
}
