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
   private static final CountingMap<String> authorToFileCount = new CountingMap<>(10);
   private static final CountingMap<String> authorToSlocCount = new CountingMap<>(10);
   private static final CountingMap<String> packageToSlocCount = new CountingMap<>(10);
   private static final CountingMap<String> productToSlocCount = new CountingMap<>(10);
   private static List<String> gitRepos = Arrays.asList("C:\\UserData\\git_merge\\org.eclipse.osee\\plugins\\",
      "C:\\UserData\\git_merge\\lba.osee\\plugins\\");

   public static void main(String[] args) {

      try {
         StringBuffer results = new StringBuffer();
         StringBuffer errors = new StringBuffer();
         bundleNames = new HashSet<>();
         for (String dirNam : gitRepos) {
            File dir1 = new File(dirNam);
            for (String bundleMatchStr : Arrays.asList(".*\\.osee\\..*", ".*\\.ats\\..*", ".*\\.xviewer\\..*",
               ".*\\.tte\\..*", ".*\\.lba\\..*", ".*\\.rdt\\..*", ".*\\.coverage\\..*")) {
               for (String filename : Lib.readListFromDir(dir1, new MatchFilter(bundleMatchStr), true)) {
                  if (Strings.isValid(filename)) {
                     bundleNames.add(filename);
                  }
               }
            }
            bundleNames.remove("org.eclipse.osee.support.dev.java");
            bundleNames.remove("org.eclipse.osee.framework.ui.data.model.editor");
            for (String filename : bundleNames) {
               System.out.println(String.format("Processing [%s]", filename));
               File file = new File(dir1 + "\\" + filename);
               recurseAndFind(file, results, errors);
            }
         }

         results.append("\n\nSource files by Author\n");
         results.append("\nAuthor, Count, Percent of Total\n");
         results.append("\nTotal, " + sourceFileCounter + "\n");

         List<String> names = new ArrayList<>();
         for (Entry<String, MutableInteger> entry : authorToFileCount.getCounts()) {
            System.out.println(String.format("Author [%s]", entry.getKey()));
            int count = entry.getValue().getValue();
            double percent = new Double(count).doubleValue() / new Double(sourceFileCounter).doubleValue();
            names.add(entry.getKey() + ", " + entry.getValue() + ", " + doubleToI18nString(percent, false) + "\n");
         }
         Collections.sort(names);
         for (String name : names) {
            results.append(name);
         }

         results.append("\n\nSLOC by Author\n");
         results.append("\nAuthor, Count, Percent of Total\n");
         results.append("\nTotal, " + slocCounter + "\n");
         names.clear();
         for (Entry<String, MutableInteger> entry : authorToSlocCount.getCounts()) {
            int count = entry.getValue().getValue();
            double percent = new Double(count).doubleValue() / new Double(slocCounter).doubleValue();
            names.add(entry.getKey() + ", " + count + ", " + doubleToI18nString(percent, false) + "\n");
         }
         Collections.sort(names);
         for (String name : names) {
            results.append(name);
         }

         results.append("\n\nSLOC by Product/Feature\n");
         results.append("\nProduct, Count, Percent of Total\n");
         names.clear();
         for (Entry<String, MutableInteger> entry : productToSlocCount.getCounts()) {
            String featureName = entry.getKey();
            int count = entry.getValue().getValue();
            double percent = new Double(count).doubleValue() / new Double(slocCounter).doubleValue();
            names.add(featureName + ", " + count + ", " + doubleToI18nString(percent, false) + "\n");
         }
         Collections.sort(names);
         for (String name : names) {
            results.append(name);
         }

         results.append("\n\nSLOC By Package\n");
         names.clear();
         for (Entry<String, MutableInteger> entry : packageToSlocCount.getCounts()) {
            String packageName = entry.getKey();
            String featureName = getFeatureName(packageName);
            MutableInteger slocs = entry.getValue();
            names.add(packageName + ", " + slocs + ", " + featureName + "\n");
         }
         Collections.sort(names);
         for (String name : names) {
            results.append(name);
         }

         results.append("\n\nBundles Searched \n");
         for (String bundle : bundleNames) {
            results.append("bundle: " + bundle + "\n");
         }

         results.append("\n\n");
         //         results.append(results.toString());

         System.err.println("\n\n" + errors);
         String outputFilename = "C:\\UserData\\SourceCounter.csv";
         System.out.println("\n\nResults written to " + outputFilename + "\n");
         Lib.writeStringToFile(results.toString() + "\n\n" + errors.toString(), new File(outputFilename));

      } catch (Exception ex) {
         System.out.println(ex.getLocalizedMessage());
      }
   }

   private static String doubleToI18nString(double d, boolean blankIfZero) {
      if (blankIfZero && d == 0) {
         return "";
      }
      // This enables java to use same string for all 0 cases instead of creating new one
      else if (d == 0) {
         return "0.00";
      } else {
         return String.format("%4.2f", d);
      }
   }

   private static String getFeatureName(String thePackageName) {
      String packageName = thePackageName.toLowerCase();
      if (packageName.contains("trax")) {
         return "TRAX";
      } else if (packageName.contains("cpcr")) {
         return "CPCR";
      } else if (packageName.contains("tpcr")) {
         return "tpcr";
      } else if (packageName.contains("process") || packageName.contains("pacr")) {
         return "pacr";
      } else if (packageName.contains("ats")) {
         if (packageName.contains("review") || packageName.contains("peer")) {
            return "Peer";
         }
         if (packageName.contains("promote")) {
            return "Engineering Build";
         }
         return "ATS";
      } else if (packageName.contains("rdt")) {
         return "RDT";
      } else if (packageName.contains("ote")) {
         return "OTE";
      } else if (packageName.contains("coverage")) {
         return "Coverage";
      } else if (packageName.contains("osee.display") || packageName.contains("orcs")) {
         return "OSEE Web";
      } else if (packageName.contains("skynet.results")) {
         return "Results Editor";
      } else if (packageName.contains("branch.gantt")) {
         return "Branch Visualization";
      } else if (packageName.contains("xviewer")) {
         return "XViewer";
      } else if (packageName.contains("skywalker")) {
         return "Skywalker";
      } else if (packageName.contains("tte")) {
         return "TTE";
      } else if (packageName.contains("widget") || packageName.contains("skynet.widgets") || packageName.contains(
         "swt")) {
         return "XWidgets";
      } else if (packageName.contains("event") || packageName.contains("messaging")) {
         return "Event System";
      } else if (packageName.contains("define")) {
         return "Define";
      } else if (packageName.contains("artifact") || packageName.contains("attribute") || packageName.contains(
         "relation")) {
         return "Persistence";
      } else if (packageName.contains("test")) {
         return "Testing";
      } else if (packageName.contains("framework") || packageName.contains(
         "client.integration") || packageName.contains(
            "cluster") || packageName.contains("executor") || packageName.contains("database")) {
         return "Framework";
      }
      return "Unknown";
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
            System.out.println(
               "Ignoring Counter and Util file cause author search string in [%s]" + file.getAbsolutePath());
         } else {
            sourceFileCounter++;
            Set<String> authors = UnitTestUtil.getAuthors(text);
            if (authors.isEmpty()) {
               if (file.getAbsolutePath().toLowerCase().contains("rdt")) {
                  authors.add("Robert A. Fisher");
               } else if (file.getAbsolutePath().toLowerCase().contains("ote")) {
                  authors.add("OTE Team");
               } else {
                  errors.append(String.format("File [%s] has no authors\n", file.getName()));
                  authors.add("unauthored");
               }
            }
            Matcher m = semiPattern.matcher(text);
            int slocCount = 0;
            while (m.find()) {
               slocCount++;
            }
            slocCounter += slocCount;

            for (String author : authors) {
               authorToFileCount.put(author);
               authorToSlocCount.put(author, slocCount);
            }
            //            results.append(file.getName() + ", " + authorsStr+"\n");

            String packageName = getPackageName(file);
            packageToSlocCount.put(packageName, slocCount);

            String featureName = getFeatureName(packageName);
            productToSlocCount.put(featureName, slocCount);

            System.err.println(String.format("[%s] sloc from author(s) [%s] for file [%s] and package [%s]", slocCount,
               authors, file.getName(), packageName));
         }
      }
   }

   private static String getPackageName(File file) {
      String filename = file.getAbsolutePath();
      filename = filename.replaceFirst("^.*src\\\\", "");
      filename = filename.replaceFirst(file.getName(), "");
      filename = filename.replaceAll("\\\\", ".");
      filename = filename.replace("\\.$", "");
      return filename;
   }
}
