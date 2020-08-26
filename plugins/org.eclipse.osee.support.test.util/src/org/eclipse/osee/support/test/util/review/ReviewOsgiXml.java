/*********************************************************************
 * Copyright (c) 2020 Boeing
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
package org.eclipse.osee.support.test.util.review;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * This will read all *.xml files in OSGI-INF directorys in workspace and check that<br/>
 * <ul>
 * <li>Interface class exists</li>
 * <li>If bind/unbind method it follows naming convention</li>
 * <li>If bind/unbind method it exists in class and isn't static</li>
 * </ul>
 * Classes with // for ReviewOsgiXml public void methodName(...<br/>
 * This allows the author to override the check/error. This is currently used cause parser does not handle abstract
 * classes where those methods ARE defined. This will be resolved in future versions of this review check. <br/>
 *
 * @author Donald G. Dunne
 */
public class ReviewOsgiXml {

   public static String IGNORE_LINE = "ReviewOsgiXml.IgnoreLine";

   // Set to debug
   Integer breakAfterFile = null;
   XResultData rd = new XResultData();
   Collection<String> ignoreShouldBeBind = Arrays.asList("setLog");

   public void run() {
      rd.logf("Reviewing OSGI Files...\n\n");
      rd.logf("Search for Error: and Warning: and resolve\n");
      rd.addResultsTag();
      Collection<String> files = findFiles();

      if (files.isEmpty()) {
         rd.logf("No Files found");
         return;
      }
      int x = 0;

      for (String xmlfilename : files) {
         x = x + 1;
         rd.logf("\n=================================================================\n");
         rd.logf("\nOSGI [%s]\n", xmlfilename);
         validateFile(xmlfilename);

         if (breakAfterFile != null && x == breakAfterFile) {
            rd.logf("\n\nStopping early at count itemCount\n");
            break;
         }

      }
      rd.logf("\n");

      String path = System.getProperty("user.home") + File.separator + "ReviewOsgiXml.html";
      File outFile = new File(path);
      String html = rd.getHtml();
      try {
         Lib.writeStringToFile(html, outFile);
         Desktop.getDesktop().open(outFile);
      } catch (Exception ex) {
         // do nothing
      }
   }

   private Collection<String> findFiles() {
      File thisClass = new File(ReviewOsgiXml.class.getProtectionDomain().getCodeSource().getLocation().getPath());
      String pathStr = thisClass.getAbsolutePath();
      String basePath = pathStr;
      basePath = basePath.replaceFirst("org.eclipse.osee.*$", "");
      //      basePath = basePath.replaceFirst("org.eclipse.osee.*$", "org.eclipse.osee");

      return getOsgiFiles(new File(basePath), new ArrayList<>());
   }

   public static List<String> getOsgiFiles(File dir, List<String> filenames) {
      // Do not ever need to check these dirs; Improves search performance
      List<String> ignoreDirs = Arrays.asList(".git", //
         ".postgresql.", ".help.", ".support.", //
         ".integration.tests.", "\\target\\", "\\bin", "\\src" //
      );
      try {
         File[] fileList = dir.listFiles();
         for (File file : fileList) {
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
               getOsgiFiles(file, filenames);
            } else {
               if (file.getAbsolutePath().contains("OSGI-INF")) {
                  filenames.add(file.getAbsolutePath());
               }
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
      return filenames;
   }

   public void validateFile(String xmlfilename) {
      String xmlFileContents;
      try {
         xmlFileContents = Lib.fileToString(new File(xmlfilename));
      } catch (IOException ex) {
         rd.errorf("===> Error: Exception loading xml file [%s] exception %s", xmlfilename, Lib.exceptionToString(ex));
         return;
      }
      String[] lines = xmlFileContents.split("\n");

      String className = "Unknown";
      String javaFileContents = "";

      // Get class first
      for (String line : lines) {
         if (line.contains("implementation class")) {
            rd.logf("xml   %s\n", line);
            String fileqname = line;
            fileqname = fileqname.replaceFirst("^.*class *= *\"", "");
            fileqname = fileqname.replaceAll("\".*$", "");
            rd.logf("==> File Qual Name [%s]\n", fileqname);

            className = fileqname;
            className = className.replaceFirst("^.*\\.", "");

            String existsFileName = validateFileExists(xmlfilename, fileqname);
            if (Strings.isInValid(existsFileName)) {
               continue;
            }
            // If here, filename is existing file
            try {
               javaFileContents = Lib.fileToString(new File(existsFileName));
            } catch (IOException ex) {
               rd.errorf("===> Error: Exception loading java file [%s] exception %s", existsFileName,
                  Lib.exceptionToString(ex));
               continue;
            }
         }
      }
      for (String line : lines) {
         rd.logf("xml   %s\n", line);
         if (line.contains("reference")) {
            rd.logf("\n== Reference =======\n");
            String iface = line;
            iface = iface.replaceFirst("^.*interface=\"", "");
            iface = iface.replaceAll("\".*", "");
            rd.logf("==> Interface [%s]\n", iface);
            boolean addRemove = line.contains("cardinality=\"0..n\"") || line.contains("cardinality=\"1..n\"");

            String bind = "", unbind = "";
            boolean validateBind = false;
            if (line.contains("bind=")) {
               bind = line;
               bind = bind.replaceFirst("^.* bind=\"", "");
               bind = bind.replaceFirst("\".*", "");
               rd.logf("==> Bind [%s]\n", bind);
               validateBind = true;
            }
            if (line.contains("unbind=")) {
               unbind = line;
               unbind = unbind.replaceFirst("^.*unbind=\"", "");
               unbind = unbind.replaceFirst("\".*", "");
               rd.logf("==> UnBind [%s]\n", unbind);
               validateBind = true;
            }
            if (validateBind) {
               validateBind(javaFileContents, bind, unbind, iface, className, addRemove);
            }
            rd.logf("== End Reference ===\n");
            rd.logf("\n");
         }
      }
   }

   public void validateBind(String filecontents, String bind, String unbind, String iface, String className, boolean addRemove) {

      String ifaceClass = iface;
      ifaceClass = ifaceClass.replaceAll("^.*\\.", "");
      rd.logf("==> IFace Class [%s]\n", ifaceClass);

      if (Strings.isValid(bind)) {
         // Test that bind method is using standard method name (warning)
         if (ifaceClass.startsWith("I")) {
            rd.warningf("==> Warning - Remove I from interface [%s]\n", ifaceClass);
         }
         ifaceClass = ifaceClass.replaceFirst("^I", "");
         String expectedBind = "";
         if (addRemove) {
            expectedBind = "add" + ifaceClass;
         } else {
            expectedBind = "set" + ifaceClass;
         }
         rd.logf("==> XML Expected Bind [%s] Actual [%s] (based on interface)\n", expectedBind, bind);
         if (!expectedBind.equals(bind) && !ignoreShouldBeBind.contains(expectedBind)) {
            rd.warningf("==> Warning - Bind method should be [%s] not [%s]\n", expectedBind, bind);
         }
         // Test that bind method used matches method in class
         Collection<String> mLines = getMethodLines(filecontents, bind);
         if (mLines.isEmpty()) {
            rd.errorf("==> Error - No bind method [%s] found in class [%s]\n", bind, className);
         } else if (mLines.size() > 1) {
            rd.errorf("==> Error - %s bind methods [%s] found in class [%s]; expect 1\n", mLines.size(), bind,
               className);
            for (String line : mLines) {
               rd.logf("java [%s]\n", line);
            }
         } else {
            rd.logf("==> SUCCESS - Bind methods match\n");
         }
      }

      if (Strings.isValid(unbind)) {
         // Test that unbind method is using standard method name (warning)
         String expectedUnBind = "";
         if (addRemove) {
            expectedUnBind = "remove" + ifaceClass;
         }
         rd.logf("==> XML Expected UnBind [%s] Actual [%s] (based on interface)\n", expectedUnBind, unbind);
         if (!expectedUnBind.equals(unbind)) {
            rd.warningf("==> Warning - UnBind method should be [%s] not [%s]\n", expectedUnBind, unbind);
         }
         // Test that bind method used matches method in class
         Collection<String> mLines = getMethodLines(filecontents, unbind);
         if (mLines.isEmpty()) {
            rd.errorf("==> Error - No unbind method [%s] found in class [%s]\n", unbind, className);
         } else if (mLines.size() > 1) {
            rd.errorf("==> Error - %s bind methods [%s] found in class [%s]; expect 1\n", mLines.size(), unbind,
               className);
            for (String line : mLines) {
               rd.logf("java [%s]\n", line);
            }
         } else {
            rd.logf("==> SUCCESS - UnBind methods match\n");
         }
      }

   }

   public Collection<String> getMethodLines(String filecontents, String method) {
      String[] lines = filecontents.split("\n");
      ArrayList<String> results = new ArrayList<>();
      for (String line : lines) {
         if (line.contains("void " + method + "(") && !line.contains(IGNORE_LINE)) {
            results.add(line);
         }
      }
      return results;
   }

   /**
    * @return filename if file exists, else null
    */
   public String validateFileExists(String xmlfilename, String fileqname) {

      // validate filename
      String bundle = xmlfilename;
      bundle = bundle.replaceFirst("OSGI-INF.*", "src");
      bundle = bundle + "/";
      bundle = Matcher.quoteReplacement(bundle);
      rd.logf("==> Bundle [%s]\n", bundle);

      String bundleNamespace = xmlfilename;
      bundleNamespace = bundleNamespace.replaceFirst("^.*plugins\\\\", "");
      bundleNamespace = bundleNamespace.replaceFirst("\\\\.*$", "");
      rd.logf("==> Bundle Namespace [%s]\n", bundleNamespace);

      if (!fileqname.contains(bundleNamespace)) {
         rd.errorf("==> Error: file [%s] in wrong bundle [%s]; should be same as .xml file bundle [%s].\n", fileqname,
            bundle, bundleNamespace);
         return null;
      }

      String filename = fileqname;
      filename = filename.replaceAll("\\.", "/");
      filename = filename.replaceFirst("^", bundle);
      filename = filename.replaceFirst("$", ".java");
      rd.logf("==> FileName [%s]\n", filename);

      if ((new File(filename)).exists()) {
         return filename;
      } else {
         rd.errorf("==> Error: file not found [%s]\n", filename);
      }
      return null;
   }

   public static void main(String[] args) {
      ReviewOsgiXml op = new ReviewOsgiXml();
      op.run();
   }

}
