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
package org.eclipse.osee.ats.ide.integration.tests.coderev;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * Reads all *.xml files in OSGI-INF directories and validates:
 * <ul>
 * <li>Implementation class exists in the correct bundle</li>
 * <li>Bind/unbind methods follow naming conventions</li>
 * <li>Bind/unbind methods exist in the implementation class and aren't static</li>
 * </ul>
 *
 * @author Donald G. Dunne
 */
public class CodeReviewOsgiXml extends AbstractCodeReview {

   private final Collection<String> ignoreShouldBeBind = Arrays.asList("setLog");

   @Override
   protected String getReviewName() {
      return "CodeReviewOsgiXml";
   }

   @Override
   protected List<String> getChecksPerformed() {
      return Arrays.asList( //
         "Implementation class exists in the correct bundle",
         "Bind/unbind methods follow naming conventions (set/add/remove + interface name)",
         "Bind/unbind methods exist in the implementation class");
   }

   @Override
   protected String getFilePattern() {
      return ".*\\.xml";
   }

   @Override
   protected List<String> getIgnoreDirs() {
      // Path substrings are matched against separator-normalized ('/') absolute paths, so they work on
      // Windows and Linux builds alike.
      return Arrays.asList(".git", ".postgresql.", ".help.", ".support.", ".integration.tests.", "/target/", "/bin/",
         "/src/");
   }

   /**
    * OSGI-INF component XML is not matched by a simple file-name pattern, so this review does its own discovery:
    * resolve the workspace root (anchored on {@code plugins}), then within each sibling repo descend into that repo's
    * {@code plugins} directory collecting {@code OSGI-INF/*.xml}.
    */
   @Override
   protected List<String> findFiles() {
      File workspaceRoot = getWorkspaceRoot();
      if (workspaceRoot == null || !workspaceRoot.isDirectory()) {
         return new ArrayList<>();
      }
      basePath = workspaceRoot.getAbsolutePath();
      rd.logf("Workspace root: %s\n", basePath);

      List<String> results = new ArrayList<>();
      File[] repos = workspaceRoot.listFiles();
      if (repos == null) {
         return results;
      }
      for (File repo : repos) {
         if (!repo.isDirectory()) {
            continue;
         }
         File pluginsDir = new File(repo, "plugins");
         if (pluginsDir.isDirectory()) {
            findOsgiFiles(pluginsDir, results);
         }
      }
      return results;
   }

   private List<String> findOsgiFiles(File dir, List<String> results) {
      File[] fileList = dir.listFiles();
      if (fileList == null) {
         return results;
      }
      for (File file : fileList) {
         if (file.isDirectory()) {
            // Normalize to '/' so the ignore-dir substrings match on Windows and Linux alike.
            String path = file.getAbsolutePath().replace('\\', '/');
            boolean skip = false;
            for (String ignore : getIgnoreDirs()) {
               if (path.contains(ignore)) {
                  skip = true;
                  break;
               }
            }
            if (!skip) {
               findOsgiFiles(file, results);
            }
         } else {
            if (file.getAbsolutePath().contains("OSGI-INF") && file.getName().endsWith(".xml")) {
               results.add(file.getAbsolutePath());
            }
         }
      }
      return results;
   }

   @Override
   protected void performReview(List<String> files) {
      int x = 0;
      for (String xmlFilename : files) {
         x++;
         validateFile(xmlFilename);
         if (breakAfterFile != null && x == breakAfterFile) {
            break;
         }
      }
   }

   private void validateFile(String xmlFilename) {
      String xmlFileContents = readFile(xmlFilename);
      if (Strings.isInvalid(xmlFileContents)) {
         return;
      }
      FileResult fr = startFile(xmlFilename);
      String[] lines = xmlFileContents.split("\n");
      String className = "Unknown";
      String javaFileContents = "";

      for (String line : lines) {
         if (line.contains("implementation class")) {
            String fileqname = line.replaceFirst("^.*class *= *\"", "").replaceAll("\".*$", "");
            fileqname = fileqname.replaceAll("[<>/\\s]", "").trim();
            if (Strings.isInvalid(fileqname)) {
               continue;
            }
            className = fileqname.replaceFirst("^.*\\.", "");
            String existsFileName = validateFileExists(fr, xmlFilename, fileqname);
            if (Strings.isInValid(existsFileName)) {
               continue;
            }
            javaFileContents = readFile(existsFileName);
            fr.logf("Class: %s\n", className);
         }
      }

      for (String line : lines) {
         if (line.contains("reference")) {
            String iface = line.replaceFirst("^.*interface=\"", "").replaceAll("\".*", "");
            boolean addRemove = line.contains("cardinality=\"0..n\"") || line.contains("cardinality=\"1..n\"");
            String bind = "";
            String unbind = "";
            if (line.contains("bind=")) {
               bind = line.replaceFirst("^.* bind=\"", "").replaceFirst("\".*", "").strip();
            }
            if (line.contains("unbind=")) {
               unbind = line.replaceFirst("^.*unbind=\"", "").replaceFirst("\".*", "").strip();
            }
            if (Strings.isValid(bind) || Strings.isValid(unbind)) {
               validateBind(fr, javaFileContents, bind, unbind, iface, className, addRemove);
            }
         }
      }
   }

   private void validateBind(FileResult fr, String fileContents, String bind, String unbind, String iface,
      String className, boolean addRemove) {
      String ifaceClass = iface.replaceAll("^.*\\.", "").strip();
      if (Strings.isValid(bind)) {
         if (ifaceClass.startsWith("I")) {
            fr.warningf("Bind interface starts with I [%s] - consider removing\n", ifaceClass);
         }
         ifaceClass = ifaceClass.replaceFirst("^I", "");
         String expectedBind = addRemove ? "add" + ifaceClass : "set" + ifaceClass;
         if (!expectedBind.equals(bind) && !ignoreShouldBeBind.contains(expectedBind)) {
            fr.warningf("Bind method should be [%s] not [%s]\n", expectedBind, bind);
         }
         Collection<String> mLines = getMethodLines(fileContents, bind);
         if (mLines.isEmpty()) {
            fr.errorf("No bind method [%s] found in class [%s]\n", bind, className);
         }
      }
      if (Strings.isValid(unbind)) {
         String expectedUnBind = addRemove ? "remove" + ifaceClass : "";
         if (Strings.isValid(expectedUnBind) && !expectedUnBind.equals(unbind)) {
            fr.warningf("UnBind method should be [%s] not [%s]\n", expectedUnBind, unbind);
         }
         Collection<String> mLines = getMethodLines(fileContents, unbind);
         if (mLines.isEmpty()) {
            fr.errorf("No unbind method [%s] found in class [%s]\n", unbind, className);
         }
      }
   }

   private Collection<String> getMethodLines(String fileContents, String method) {
      String[] lines = fileContents.split("\n");
      String bindMethod = "void " + method + "(";
      ArrayList<String> results = new ArrayList<>();
      for (String line : lines) {
         if (line.contains(bindMethod) && !isIgnored(line)) {
            results.add(line);
         }
      }
      return results;
   }

   private String validateFileExists(FileResult fr, String xmlFilename, String fileqname) {
      // Normalize separators to '/' so parsing works on both Windows (\) and Linux (/) build paths.
      String normalized = xmlFilename.replace('\\', '/');
      // Bundle source root: everything up to OSGI-INF, with 'src' appended (e.g. .../<bundle>/src/).
      String bundle = normalized.replaceFirst("OSGI-INF.*", "src") + "/";
      // Bundle namespace = the directory name directly under 'plugins' (the bundle symbolic name).
      String bundleNamespace = normalized.replaceFirst("^.*/plugins/", "").replaceFirst("/.*$", "");
      if (!fileqname.contains(bundleNamespace)) {
         fr.errorf("File [%s] in wrong bundle; should match .xml bundle [%s].\n", fileqname, bundleNamespace);
         return null;
      }
      String filename = bundle + fileqname.replace('.', '/') + ".java";
      try {
         filename = new File(filename).toPath().normalize().toString();
      } catch (Exception ex) {
         fr.errorf("Invalid path for [%s]: %s\n", fileqname, ex.getMessage());
         return null;
      }
      if (new File(filename).exists()) {
         return filename;
      }
      fr.errorf("File not found [%s]\n", filename);
      return null;
   }

   public static void main(String[] args) {
      new CodeReviewOsgiXml().run();
   }

}
