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
import java.util.List;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;

/**
 * Base class for code review checks (read-only validators). Extends {@link AbstractQual} for the shared machinery
 * (result, file discovery anchored on {@code plugins}, read/normalize, path shortening, HTML reporting) and adds the
 * review-specific behavior: per-file {@link FileResult} collection and output sorted errors-first, then warnings, then
 * clean files, plus the JUnit {@link #runAndAssert()} entry point.
 *
 * @author Donald G. Dunne
 */
public abstract class AbstractCodeReview extends AbstractQual {

   public static final String IGNORE_LINE = "CodeReview.IgnoreLine";

   protected final List<FileResult> fileResults = new ArrayList<>();

   /**
    * Holds the review results for a single file.
    */
   protected static class FileResult {
      private final String filePath;
      private final List<String> errors = new ArrayList<>();
      private final List<String> warnings = new ArrayList<>();
      private final List<String> info = new ArrayList<>();

      public FileResult(String filePath) {
         this.filePath = filePath;
      }

      public void error(String msg) {
         errors.add(msg);
      }

      public void errorf(String format, Object... args) {
         errors.add(String.format(format, args));
      }

      public void warning(String msg) {
         warnings.add(msg);
      }

      public void warningf(String format, Object... args) {
         warnings.add(String.format(format, args));
      }

      public void info(String msg) {
         info.add(msg);
      }

      public void logf(String format, Object... args) {
         info.add(String.format(format, args));
      }

      public boolean hasErrors() {
         return !errors.isEmpty();
      }

      public boolean hasWarnings() {
         return !warnings.isEmpty();
      }

      public boolean isClean() {
         return errors.isEmpty() && warnings.isEmpty();
      }

      public String getFilePath() {
         return filePath;
      }

      public List<String> getErrors() {
         return errors;
      }

      public List<String> getWarnings() {
         return warnings;
      }

      public List<String> getInfo() {
         return info;
      }
   }

   /**
    * Run the code review. Subclasses implement {@link #performReview(List)} to define checks.
    */
   public void run() {
      rd.logf("Running %s...\n\n", getReviewName());
      rd.logf("Search for Error: and Warning: and resolve\n\n");
      outputChecksHeader();
      rd.addResultsTag();

      List<String> files = findFiles();
      if (files.isEmpty()) {
         rd.logf("No files found matching criteria.\n");
         reportResults();
         return;
      }
      rd.logf("Found %d files to review.\n\n", files.size());

      performReview(files);
      outputSortedResults();
      reportResults();
   }

   /**
    * Run the review and return true if no errors found. Suitable for JUnit test assertion.
    */
   public boolean runAndAssert() {
      rd.logf("Running %s...\n\n", getReviewName());
      outputChecksHeader();
      rd.addResultsTag();

      List<String> files = findFiles();
      if (files.isEmpty()) {
         rd.logf("No files found matching criteria.\n");
         return true;
      }
      rd.logf("Found %d files to review.\n\n", files.size());

      performReview(files);
      outputSortedResults();

      if (rd.isErrors()) {
         reportResults();
         return false;
      }
      if (!isInIntegrationTest()) {
         reportResults();
      }
      return true;
   }

   /**
    * Output collected file results sorted: errors first, then warnings, then clean/info-only files.
    */
   private void outputSortedResults() {
      List<FileResult> errorFiles = new ArrayList<>();
      List<FileResult> warningFiles = new ArrayList<>();
      List<FileResult> cleanFiles = new ArrayList<>();

      for (FileResult fr : fileResults) {
         if (fr.hasErrors()) {
            errorFiles.add(fr);
         } else if (fr.hasWarnings()) {
            warningFiles.add(fr);
         } else {
            cleanFiles.add(fr);
         }
      }

      // Errors section
      if (!errorFiles.isEmpty()) {
         rd.logf("\n========== ERRORS (%d files) ==========\n", errorFiles.size());
         for (FileResult fr : errorFiles) {
            outputFileResult(fr);
         }
      }

      // Warnings section
      if (!warningFiles.isEmpty()) {
         rd.logf("\n========== WARNINGS (%d files) ==========\n", warningFiles.size());
         for (FileResult fr : warningFiles) {
            outputFileResult(fr);
         }
      }

      // Clean files section
      if (!cleanFiles.isEmpty()) {
         rd.logf("\n========== CLEAN (%d files) ==========\n", cleanFiles.size());
         for (FileResult fr : cleanFiles) {
            rd.logf("  %s\n", shortPath(fr.getFilePath()));
         }
      }

      // Summary
      rd.logf("\n========== SUMMARY ==========\n");
      rd.logf("Total files: %d | Errors: %d | Warnings: %d | Clean: %d\n", fileResults.size(), errorFiles.size(),
         warningFiles.size(), cleanFiles.size());
   }

   private void outputFileResult(FileResult fr) {
      rd.logf("\n----- %s -----\n", shortPath(fr.getFilePath()));
      for (String err : fr.getErrors()) {
         rd.errorf("  %s", err);
      }
      for (String warn : fr.getWarnings()) {
         rd.warningf("  %s", warn);
      }
      for (String info : fr.getInfo()) {
         rd.logf("  %s", info);
      }
   }

   /**
    * Create a new FileResult for the given file path and add it to the collection. Subclasses call this at the start of
    * processing each file, then use the returned object to record errors/warnings/info.
    */
   protected FileResult startFile(String filePath) {
      FileResult fr = new FileResult(filePath);
      fileResults.add(fr);
      return fr;
   }

   /**
    * Subclasses implement to perform their specific review checks on the discovered files.
    */
   protected abstract void performReview(List<String> files);

   /** @return human-readable name for this review */
   protected abstract String getReviewName();

   /** @return list of check descriptions to display at top of report */
   protected abstract List<String> getChecksPerformed();

   private void outputChecksHeader() {
      List<String> checks = getChecksPerformed();
      if (checks != null && !checks.isEmpty()) {
         rd.logf("Checks Performed:\n");
         for (String check : checks) {
            rd.logf("  - %s\n", check);
         }
         rd.logf("\n");
      }
   }

   /**
    * The report name is the review name (used for the {@code <name>.html} file and the report title).
    */
   @Override
   protected String getName() {
      return getReviewName();
   }

   @Override
   protected String getOutputDirName() {
      return CodeQualityUtil.CODE_REVIEW_DIR;
   }

   @Override
   protected String getToolLabel() {
      return "CodeReview";
   }

   /**
    * Write results to HTML file and open in browser. Reviews write {@link #rd} directly (no pre-report assembly);
    * delegates to {@code AbstractQual.writeAndOpenReport}.
    */
   protected void reportResults() {
      writeAndOpenReport(rd);
   }

   /**
    * Check if a line should be ignored (contains either the new or legacy ignore marker).
    */
   protected boolean isIgnored(String line) {
      return line.contains(IGNORE_LINE) || line.contains("XWidgetReview.IgnoreLine") || line.contains(
         "ReviewOsgiXml.IgnoreLine");
   }

   /**
    * Find the OSGI-INF XML file for a given Java source file. Returns null if not found.
    */
   protected String findOsgiXmlForClass(String javaFilePath) {
      // Navigate from src/.../ClassName.java up to the bundle root, then look in OSGI-INF
      File javaFile = new File(javaFilePath);
      String className = javaFile.getName().replace(".java", "");
      // Find the bundle root (directory containing src/)
      File dir = javaFile.getParentFile();
      while (dir != null && !new File(dir, "OSGI-INF").isDirectory()) {
         if (dir.getName().equals("src")) {
            dir = dir.getParentFile();
            break;
         }
         dir = dir.getParentFile();
      }
      if (dir == null) {
         return null;
      }
      File osgiInfDir = new File(dir, "OSGI-INF");
      if (!osgiInfDir.isDirectory()) {
         return null;
      }
      // Search for XML file containing the class name
      File[] xmlFiles = osgiInfDir.listFiles((d, name) -> name.endsWith(".xml"));
      if (xmlFiles == null) {
         return null;
      }
      for (File xmlFile : xmlFiles) {
         if (xmlFile.getName().contains(className)) {
            return xmlFile.getAbsolutePath();
         }
      }
      // Also check XML content for the class name
      for (File xmlFile : xmlFiles) {
         try {
            String content = Lib.fileToString(xmlFile);
            if (content.contains(className)) {
               return xmlFile.getAbsolutePath();
            }
         } catch (Exception ex) {
            // skip
         }
      }
      return null;
   }

   /**
    * Check if a file contains valid XML (well-formed).
    */
   protected boolean isValidXml(String filePath) {
      try {
         String content = Lib.fileToString(new File(filePath));
         // Basic well-formedness: has xml declaration or starts with <, and has matching close tags
         return content.trim().startsWith("<") && content.contains("</");
      } catch (Exception ex) {
         return false;
      }
   }

   /**
    * @return true if running inside the integration test suite
    */
   protected boolean isInIntegrationTest() {
      return OseeProperties.isInTest();
   }

}
