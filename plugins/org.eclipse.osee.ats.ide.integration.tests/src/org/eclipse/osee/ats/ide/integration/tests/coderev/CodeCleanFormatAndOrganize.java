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

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Runs Eclipse JDT format on every Java file added or modified in the last git commit. Iterates over all git repos
 * found under the resolved workspace root (any sibling directory containing a .git folder).
 * <p>
 * CodeClean operation. Run as Java Application (Debug As > Java Application) or via {@link CodeCleanAll}. Mutates
 * files, so it is never run as part of the integration test suite.
 *
 * @author Donald G. Dunne
 */
public class CodeCleanFormatAndOrganize extends AbstractCodeClean {

   public CodeCleanFormatAndOrganize() {
      // Report only by default; set true manually when ready to format files.
      changeFiles = false;
   }

   /** Path to eclipsec executable; Define ECLIPSEC_EXE in Env variables or uncomment and set */
   //   private static final String ECLIPSEC_EXE = "C:/Tools/OSEE Nightly/eclipsec.exe";
   private static final String ECLIPSE_PATH = System.getenv("ECLIPSEC_EXE");

   /** Path to Eclipse JDT formatter prefs (OSEE Java Standard) */
   private static final String FORMATTER_PREFS =
      "C:/Tools/workspace_ggg/.metadata/.plugins/org.eclipse.core.runtime/.settings/org.eclipse.jdt.core.prefs";

   /** Files per Eclipse invocation */
   private static final int BATCH_SIZE = 20;

   /** Timeout in seconds per batch */
   private static final int TIMEOUT_SECONDS = 120;

   public static void main(String[] args) {
      new CodeCleanFormatAndOrganize().run();
   }

   @Override
   protected String getCleanName() {
      return "CodeCleanFormatAndOrganize";
   }

   /**
    * Log an informational line into {@link #rd} so it appears in the HTML report (and the {@link CodeCleanAll}
    * aggregation).
    */
   private void log(String msg) {
      // rd.log() already appends a single newline; passing msg (not msg + "\n") avoids the double blank line
      // (double-spaced report) that an extra newline would render as.
      rd.log(msg);
   }

   @Override
   protected void performClean() {
      File wsDir = getWorkspaceRoot();
      if (wsDir == null) {
         // getWorkspaceRoot already logged the error into rd (the HTML report).
         return;
      }
      log("Workspace: " + wsDir.getAbsolutePath());
      log("");

      if (!validateConfig()) {
         return;
      }

      List<File> repos = findGitRepos(wsDir);
      if (repos.isEmpty()) {
         log("No git repos found in " + wsDir.getAbsolutePath());
         return;
      }

      log("Found " + repos.size() + " git repo(s):");
      for (File repo : repos) {
         log("  " + repo.getName());
      }
      log("");

      // Collect changed files from all repos
      List<String> allFiles = new ArrayList<>();
      for (File repo : repos) {
         log("--- " + repo.getName() + " ---");
         List<String> files = getChangedJavaFiles(repo);
         if (!files.isEmpty()) {
            log("  " + files.size() + " changed Java file(s)");
            allFiles.addAll(files);
         } else {
            log("  No changed Java files");
         }
      }

      if (allFiles.isEmpty()) {
         log("\nNo Java files to format across all repos.");
         return;
      }

      log("\nTotal files to format: " + allFiles.size());
      log("");

      if (!changeFiles) {
         log("=== REPORT ONLY (changeFiles=false) ===");
         for (String f : allFiles) {
            log("  " + f);
            changedFiles.add(f);
         }
         return;
      }

      // Format in batches
      int formatted = 0;
      int totalBatches = (allFiles.size() + BATCH_SIZE - 1) / BATCH_SIZE;

      for (int i = 0; i < allFiles.size(); i += BATCH_SIZE) {
         int batchNum = (i / BATCH_SIZE) + 1;
         List<String> batch = allFiles.subList(i, Math.min(i + BATCH_SIZE, allFiles.size()));
         log(String.format("[Batch %d/%d] Formatting %d files...", batchNum, totalBatches, batch.size()));

         boolean success = runEclipseFormat(batch);
         if (success) {
            formatted += batch.size();
            changedFiles.addAll(batch);
            log(String.format("[Batch %d/%d] SUCCESS", batchNum, totalBatches));
         } else {
            rd.errorf("[Batch %d/%d] FAILED\n", batchNum, totalBatches);
         }
      }

      log("\n=== Complete ===");
      log("Formatted: " + formatted);
      log("Errors: " + rd.getNumErrors());
   }

   private List<File> findGitRepos(File wsDir) {
      List<File> repos = new ArrayList<>();
      File[] children = wsDir.listFiles();
      if (children == null) {
         return repos;
      }
      for (File child : children) {
         if (child.isDirectory() && new File(child, ".git").exists()) {
            repos.add(child);
         }
      }
      return repos;
   }

   private List<String> getChangedJavaFiles(File repoDir) {
      List<String> files = new ArrayList<>();
      try {
         // Use diff-tree which works for first commit (no parent needed)
         // --no-commit-id -r gives flat list of files in the commit
         ProcessBuilder pb = new ProcessBuilder("git", "diff-tree", "--no-commit-id", "--name-only",
            "--diff-filter=AMR", "-r", "HEAD", "--", "*.java");
         pb.directory(repoDir);
         pb.redirectErrorStream(true);
         Process process = pb.start();

         try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
               line = line.trim();
               if (line.endsWith(".java")) {
                  Path fullPath = Paths.get(repoDir.getAbsolutePath(), line);
                  if (Files.exists(fullPath)) {
                     files.add(fullPath.toString());
                  } else {
                     log("  SKIP (not on disk): " + line);
                  }
               }
            }
         }
         boolean finished = process.waitFor(30, TimeUnit.SECONDS);
         if (!finished) {
            process.destroyForcibly();
            rd.errorf("  ERROR: git timed out in %s\n", repoDir.getName());
         } else if (process.exitValue() != 0) {
            rd.errorf("  ERROR: git exited %d in %s\n", process.exitValue(), repoDir.getName());
         }
      } catch (Exception ex) {
         rd.errorf("  ERROR: git failed in %s: %s\n", repoDir.getName(), ex.getMessage());
      }
      return files;
   }

   private boolean validateConfig() {
      boolean valid = true;
      if (ECLIPSE_PATH == null || ECLIPSE_PATH.isBlank()) {
         log("ERROR: ECLIPSEC_EXE environment variable is not set.");
         log("");
         log("  Set the Windows environment variable ECLIPSEC_EXE to point to your eclipsec.exe.");
         log("  Example: setx ECLIPSEC_EXE \"C:\\Tools\\OSEE Nightly\\eclipsec.exe\"");
         log("");
         log("  Or uncomment and edit the ECLIPSEC_EXE constant in this script.");
         log("  NOTE: After setting, you must restart your IDE/terminal for it to take effect.");
         valid = false;
      } else if (!new File(ECLIPSE_PATH).exists()) {
         log("ERROR: Eclipse not found at ECLIPSEC_EXE path: " + ECLIPSE_PATH);
         valid = false;
      } else {
         log("Eclipse: " + ECLIPSE_PATH + " [OK]");
      }
      if (!new File(FORMATTER_PREFS).exists()) {
         log("ERROR: Prefs not found: " + FORMATTER_PREFS);
         valid = false;
      } else {
         log("Prefs: " + FORMATTER_PREFS + " [OK]");
      }
      log("");
      if (!valid) {
         // A warning (not error): a missing local Eclipse/formatter config should skip formatting, not fail the run.
         rd.warningf(
            "Skipped: Eclipse formatter not configured (set ECLIPSEC_EXE and formatter prefs). See report above for details.\n");
      }
      return valid;
   }

   private boolean runEclipseFormat(List<String> files) {
      try {
         List<String> command = new ArrayList<>();
         command.add(ECLIPSE_PATH);
         command.add("-nosplash");
         command.add("-data");
         command.add(System.getProperty("java.io.tmpdir") + "/eclipse_formatter_ws");
         command.add("-application");
         command.add("org.eclipse.jdt.core.JavaCodeFormatter");
         command.add("-verbose");
         command.add("-config");
         command.add(FORMATTER_PREFS);
         command.addAll(files);

         ProcessBuilder pb = new ProcessBuilder(command);
         pb.redirectErrorStream(true);
         Process process = pb.start();

         Thread outputThread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
               String line;
               while ((line = reader.readLine()) != null) {
                  log("    [eclipse] " + line);
               }
            } catch (Exception ex) {
               /* ignore */ }
         });
         outputThread.setDaemon(true);
         outputThread.start();

         boolean finished = process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS);
         if (!finished) {
            rd.errorf("  TIMEOUT - killing\n");
            process.destroyForcibly();
            return false;
         }
         return process.exitValue() == 0;
      } catch (Exception ex) {
         rd.errorf("  ERROR: %s\n", ex.getMessage());
         return false;
      }
   }

}
