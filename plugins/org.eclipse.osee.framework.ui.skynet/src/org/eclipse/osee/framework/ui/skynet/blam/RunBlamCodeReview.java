/*********************************************************************
 * Copyright (c) 2026 Boeing
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
package org.eclipse.osee.framework.ui.skynet.blam;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * This will read all .*Blam files and test for commonality rules<br/>
 * <ul>
 *
 * @author Donald G. Dunne
 */
public class RunBlamCodeReview {

   public static String IGNORE_LINE = "XWidgetReview.IgnoreLine";
   private static final boolean changeFiles = true;
   private static final List<String> ignoreFiles = Arrays.asList("XNavigateItemBlam");

   Integer breakAfterFile = null;
   XResultData rd = new XResultData();

   public void run() {
      rd.logf("Reviewing Blam Files...\n\n");
      rd.logf("Search for Error: and Warning: and resolve\n");
      rd.addResultsTag();
      Collection<String> files = findBlamFiles();

      if (files.isEmpty()) {
         rd.logf("No Files found");
         return;
      }
      int x = 0;

      for (String widgetFile : files) {
         x = x + 1;
         rd.logf("\n=================================================================\n");
         rd.logf("\nWidget [%s]\n", widgetFile);
         validateFile(widgetFile);

         if (breakAfterFile != null && x == breakAfterFile) {
            rd.logf("\n\nStopping early at count itemCount\n");
            break;
         }
      }
      rd.logf("\n\n");

      String path = System.getProperty("user.home") + File.separator + getClass().getSimpleName() + ".html";
      File outFile = new File(path);
      String html = rd.getHtml();
      try {
         Lib.writeStringToFile(html, outFile);
         Desktop.getDesktop().open(outFile);
      } catch (Exception ex) {
         // do nothing
      }
   }

   public void validateFile(String fullFilename) {
      String widgetFileContents;
      try {
         File file = new File(fullFilename);
         String justFilename = file.getName().replaceFirst("\\.java", "");
         if (ignoreFiles.contains(justFilename)) {
            rd.logf("Ignoring [%s]\n", justFilename);
            return;
         }
         widgetFileContents = Lib.fileToString(file);
         widgetFileContents = widgetFileContents.replaceAll("[\n\r]+", "\r\n");
         String[] lines = widgetFileContents.split("\n");
         if (lines.length < 5) {
            rd.errorf("File [%s] did not split correctly", justFilename);
            return;
         }

         String defaultConstructorLine = null;
         String osgiComponentLine = null;

         boolean isAbstractClass = false;
         boolean blamTested = false;
         for (String line : lines) {
            //            rd.logf("line   %s\n", line);
            if (line.contains("public abstract ")) {
               isAbstractClass = true;
            }
            if (line.contains("@Component(service = AbstractBlam")) {
               osgiComponentLine = line;
            }
            if (line.contains(justFilename + "()")) {
               defaultConstructorLine = line;
            }
            if (!blamTested && line.contains("BLAMTESTED")) {
               blamTested = true;
            }
         }

         if (isAbstractClass) {

            // All abstracts, except XWiget, must start with XAbstract
            if (!justFilename.startsWith("Abstract")) {
               rd.errorf("Abstract class should start with Abstract for [%s]\n", justFilename);
               return; // Fix abstract error before continuing tests
            }

         }
         if (!isAbstractClass) {

            if (justFilename.contains("Abstract")) {
               rd.errorf("Remove \"Abstract\" from name or add \"public abstract\" [%s]\n", justFilename);
               return; // Fix abstract error before continuing tests
            }

            // Must have OSGI Component line
            if (Strings.isValid(osgiComponentLine)) {
               rd.logf("Success: OSGI Componet Line Found [%s]\n", osgiComponentLine);
            } else {
               rd.errorf("Missing OSGI Componet[%s] - Attemping to add, Needs review.\n", justFilename);
               addOsgiComponent(fullFilename, justFilename, widgetFileContents, rd);
            }

            // Must have default constructor if not abstract
            if (Strings.isValid(defaultConstructorLine)) {
               rd.logf("Success: Default Constructor Found [%s]\n", defaultConstructorLine);
            }

            if (!blamTested) {
               rd.errorf("BLAM not tested.  Test and add BLAMTESTED to comment [%s]\n", justFilename);
            }
         }

      } catch (IOException ex) {
         rd.errorf("===> Error: Exception loading java file [%s] exception %s\n", fullFilename,
            Lib.exceptionToString(ex));
         return;
      }
   }

   private void addOsgiComponent(String fullFilename, String justFilename, String widgetFileContents, XResultData rd) {
      //eg: @Component(service = AbstractBlam.class, immediate = true)
      String search = "public class " + justFilename;
      String replace = "@Component(service = AbstractBlam.class, immediate = true)\n" + search;
      searchReplaceFile(fullFilename, justFilename, search, replace, rd);
   }

   private void searchReplaceFile(String fullFilename, String justFilename, String searchStr, String replaceStr,
      XResultData rd) {
      try {
         rd.logf("SrchReplace: [%s] Srch[%s] Rplc[%s]\n", justFilename, searchStr, replaceStr);
         File file = new File(fullFilename);
         String fileContents = Lib.fileToString(file);
         fileContents = fileContents.replaceFirst(searchStr, replaceStr);
         if (changeFiles) {
            Lib.writeStringToFile(fileContents, new File(fullFilename));
         }
      } catch (IOException ex) {
         System.err.println(Lib.exceptionToString(ex));
      }
   }

   private Collection<String> findBlamFiles() {
      File thisClass = new File(RunBlamCodeReview.class.getProtectionDomain().getCodeSource().getLocation().getPath());
      String pathStr = thisClass.getAbsolutePath();
      String basePath = pathStr;
      basePath = basePath.replaceFirst("org.eclipse.osee.*$", "");
      return getBlamFiles(new File(basePath), new ArrayList<>());
   }

   public static List<String> getBlamFiles(File dir, List<String> filenames) {
      //      System.err.println("dir: " + dir.getAbsolutePath());
      // Do not ever need to check these dirs; Improves search performance
      List<String> ignoreDirs = Arrays.asList("admin", ".git", //
         ".postgresql.", ".help.", ".support.", "web", "nebula", //
         ".integration.tests.", "\\target\\", "\\bin" //
      );
      try {
         File[] fileList = dir.listFiles();
         for (File file : fileList) {
            //            System.err.println(file.getAbsolutePath());
            if (file.isDirectory()) {
               boolean skipDir = false;
               for (String dirName : ignoreDirs) {
                  if (file.getAbsolutePath().contains(dirName)) {
                     skipDir = true;
                     break;
                  }
               }
               if (skipDir) {
                  continue;
               }
               getBlamFiles(file, filenames);
            } else {
               if (file.getName().matches(".*Blam.java")) {
                  System.err.println("File: " + file.getName());
                  filenames.add(file.getAbsolutePath());
               }
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
      return filenames;
   }

   public static void main(String[] args) {
      RunBlamCodeReview op = new RunBlamCodeReview();
      op.run();
   }

}
