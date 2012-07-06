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
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.type.CountingMap;
import org.eclipse.osee.framework.jdk.core.type.MutableInteger;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.MatchFilter;

/**
 * Count all OSEE source lines of code
 * 
 * @author Donald G. Dunne
 */
public class SourceCounter {

   private static int sourceFileCounter = 0, slocCounter = 0;
   private static final CountingMap<String> authorToFileCount = new CountingMap<String>(10);
   private static final CountingMap<String> authorToSlocCount = new CountingMap<String>(10);

   public static void main(String[] args) {

      try {
         StringBuffer results = new StringBuffer();
         StringBuffer errors = new StringBuffer();
         bundleNames = new HashSet<String>();
         for (String dirNam : Arrays.asList("C:\\UserData\\git\\org.eclipse.osee\\plugins\\",
            "C:\\UserData\\git\\lba.osee\\plugins\\")) {
            File dir1 = new File(dirNam);
            for (String bundleMatchStr : Arrays.asList(".*\\.osee\\..*", ".*\\.ats\\..*")) {
               //            for (String bundleMatchStr : Arrays.asList(".*\\.ats\\..*")) {
               for (String filename : Lib.readListFromDir(dir1, new MatchFilter(bundleMatchStr), true)) {
                  if (Strings.isValid(filename)) {
                     bundleNames.add(filename);
                  }
               }
            }
            bundleNames.remove("org.eclipse.osee.support.dev.java");
            for (String filename : bundleNames) {
               System.out.println(String.format("Processing [%s]", filename));
               File file = new File(dir1 + "\\" + filename);
               recurseAndFind(file, results, errors);
            }
         }

         results.append("\n\nBundles Searched \n");
         for (String bundle : bundleNames) {
            results.append("bundle: " + bundle + "\n");
         }

         results.append("\n\nSource Code Files, " + sourceFileCounter + "\n");

         List<String> names = new ArrayList<String>();
         for (Entry<String, MutableInteger> entry : authorToFileCount.getCounts()) {
            System.out.println(String.format("Author [%s]", entry.getKey()));
            names.add(entry.getKey() + ", " + entry.getValue() + "\n");
         }
         Collections.sort(names);
         for (String name : names) {
            results.append(name);
         }

         results.append("\n\nSLOC Total, " + slocCounter + "\n");
         names.clear();
         for (Entry<String, MutableInteger> entry : authorToSlocCount.getCounts()) {
            names.add(entry.getKey() + ", " + entry.getValue() + "\n");
         }
         Collections.sort(names);
         for (String name : names) {
            results.append(name);
         }

         results.append("\n\n");
         results.append(results.toString());

         System.err.println("\n\n" + errors);
         String outputFilename = "C:\\UserData\\SourceCounter.csv";
         System.out.println("\n\nResults written to " + outputFilename + "\n");
         Lib.writeStringToFile(errors.toString() + "\n\n" + results.toString(), new File(outputFilename));

      } catch (Exception ex) {
         System.out.println(ex.getLocalizedMessage());
      }
   }

   private static Pattern semiPattern = Pattern.compile("\\;");
   private static Set<String> bundleNames;

   private static void recurseAndFind(File file, StringBuffer results, StringBuffer errors) throws IOException {
      if (file.isDirectory()) {
         for (String filename : Lib.readListFromDir(file, new MatchFilter(".*"), true)) {
            File childFile = new File(file.getAbsolutePath() + "\\" + filename);
            recurseAndFind(childFile, results, errors);
         }
      }
      if (file.getAbsolutePath().endsWith(".java")) {
         String text = Lib.fileToString(file);
         if (file.getAbsolutePath().contains("src-gen")) {
            System.out.println("Ignoring SRC-GEN file [%s]" + file.getAbsolutePath());
         } else if (file.getName().contains("UnitTestCounter") || file.getName().contains("UnitTestUtil")) {
            System.out.println("Ignoring Counter and Util file cause author search string in [%s]" + file.getAbsolutePath());
         } else {
            results.append(file.getName() + ", ");
            sourceFileCounter++;
            Set<String> authors = UnitTestUtil.getAuthors(text);
            if (authors.isEmpty()) {
               errors.append(String.format("File [%s] has no authors\n", file.getName()));
               authors.add("unauthored");
            }
            Matcher m = semiPattern.matcher(text);
            int slocCount = 0;
            while (m.find()) {
               slocCount++;
            }
            slocCounter += slocCount;

            for (String author : authors) {
               results.append(author + "; ");
               authorToFileCount.put(author);
               authorToSlocCount.put(author, slocCount);
            }
            System.err.println(String.format("[%s] sloc from author(s) [%s] for file [%s]", slocCount, authors,
               file.getName()));
            results.append("\n");
         }
      }
   }

}
