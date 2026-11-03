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
package org.eclipse.osee.ats.ide.integration.tests.coderev.packet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.ide.integration.tests.coderev.CodeQualityUtil;

/**
 * THROWAWAY migration tool - the single entry point that builds the ENTIRE XWidget -&gt; XXWidget review packet. It
 * runs each area generator in dependency order, then verifies that every expected artifact was produced and looks
 * well-formed, printing a PASS/FAIL summary.
 * <p>
 * Run it with Right-click → Run As → Java Application. It scans {@code C:/Tools/git_ggg} (new) and
 * {@code C:/Tools/git_try} (old) and writes everything into {@code ~/CodeXWidgetReview/} (relative links, so the whole
 * folder is zippable). The {@code git_try} checkout of {@code org.eclipse.osee} must be present or the "old" side of
 * each report is skipped (the generators warn but still produce output).
 * <p>
 * Order of operations:
 * <ol>
 * <li>{@link ReportXWidgetChanges} - XWidget JSON + source copies + Rename/Diff/Testing reports.</li>
 * <li>{@link ReportBlamChanges} - BLAM JSON + source copies + Renamed/Diff/Testing reports.</li>
 * <li>{@link ReportWorkDefChange} - Work Definition JSON + source copies + Rename/Diff/Testing reports.</li>
 * <li>{@link ReportXWidgetOverview} - the {@code XWidget_Overview.html} entry page that links all nine reports (built
 * last, since it points at the others).</li>
 * </ol>
 * Delete this (and the sibling {@code Report*} tools) after the migration is reviewed and merged.
 *
 * @author Donald G. Dunne
 */
public class GenerateFullReviewPacket {

   /** Minimum plausible size (bytes) for a generated HTML report - anything smaller is treated as suspect/empty. */
   private static final long MIN_REPORT_BYTES = 300;

   /** A generator step: a human label and the action that runs it (its {@code main}). */
   private interface Step {
      void run(String[] args) throws Exception;
   }

   public static void main(String[] args) {
      System.out.println("================================================================");
      System.out.println(" Generating FULL XWidget -> XXWidget review packet");
      File outDir = CodeQualityUtil.getOutputDir(CodeQualityUtil.CODE_XWIDGET_REVIEW_DIR, "CodeXWidgetReviewCui");
      System.out.println(" Output: " + outDir.getAbsolutePath());
      System.out.println("================================================================");
      System.out.println();

      // Run every generator in dependency order. One failing generator must not abort the rest - we collect the
      // failure and keep going so the user still gets as much of the packet (and as complete a verification) as
      // possible.
      Map<String, Step> steps = new LinkedHashMap<>();
      steps.put("XWidget change reports (ReportXWidgetChanges)", ReportXWidgetChanges::main);
      steps.put("BLAM change reports (ReportBlamChanges)", ReportBlamChanges::main);
      steps.put("Work Definition change reports (ReportWorkDefChange)", ReportWorkDefChange::main);
      steps.put("Overview entry page (ReportXWidgetOverview)", ReportXWidgetOverview::main);

      List<String> generatorFailures = new ArrayList<>();
      for (Map.Entry<String, Step> entry : steps.entrySet()) {
         System.out.println("---- Running: " + entry.getKey() + " ----");
         try {
            entry.getValue().run(args);
         } catch (Throwable t) {
            String msg = entry.getKey() + " -> " + t.getClass().getSimpleName() + ": " + t.getMessage();
            generatorFailures.add(msg);
            System.err.println("GENERATOR FAILED: " + msg);
         }
         System.out.println();
      }

      verify(outDir, generatorFailures);
   }

   /**
    * Verify that every expected artifact exists and looks well-formed, then print a PASS/FAIL summary and open the
    * Overview. "Looks good" is a lightweight check appropriate for throwaway tooling: the file exists, is larger than
    * {@link #MIN_REPORT_BYTES}, and (for HTML) ends with a closing {@code </html>} tag so a truncated/half-written
    * report is caught.
    */
   private static void verify(File outDir, List<String> generatorFailures) {
      System.out.println("================================================================");
      System.out.println(" Verifying generated review packet");
      System.out.println("================================================================");

      // The nine per-area reports + the overview entry page. Names must match the generators' actual outputs and the
      // links in ReportXWidgetOverview.
      List<String> htmlReports = List.of( //
         "XWidget_Overview.html", //
         "XWidgets_XWidgetReview_Rename.html", //
         "XWidgets_XWidgetReview_Diff.html", //
         "XWidgets_XWidgetReview_Testing.html", //
         "XWidgets_BlamReview_Renamed.html", //
         "XWidgets_BlamReview_Diff.html", //
         "XWidgets_BlamReview_Testing.html", //
         "XWidgets_WorkDefReview_Rename.html", //
         "XWidgets_WorkDefReview_Diff.html", //
         "XWidgets_WorkDefReview_Testing.html" //
      );

      // Supporting data the diff reports rely on. These come from the two-workspace scan; if git_try is missing the
      // *_try.json files may be absent, which is a warning (packet still opens) rather than a hard failure.
      List<String> requiredJson = List.of( //
         "xwidget_classes_ggg.json", //
         "blam_classes_ggg.json", //
         "workdef_classes_ggg.json" //
      );
      List<String> optionalJson = List.of( //
         "xwidget_classes_try.json", //
         "blam_classes_try.json", //
         "workdef_classes_try.json" //
      );

      List<String> failures = new ArrayList<>();
      List<String> warnings = new ArrayList<>();

      if (!outDir.isDirectory()) {
         failures.add("Output directory does not exist: " + outDir.getAbsolutePath());
      }

      System.out.println();
      System.out.println("Report                                      Status");
      System.out.println("------------------------------------------- --------------------------------");
      for (String name : htmlReports) {
         System.out.printf("%-43s %s%n", name, checkHtml(new File(outDir, name), failures));
      }

      System.out.println();
      System.out.println("Data file                                   Status");
      System.out.println("------------------------------------------- --------------------------------");
      for (String name : requiredJson) {
         System.out.printf("%-43s %s%n", name, checkData(new File(outDir, name), failures, false));
      }
      for (String name : optionalJson) {
         System.out.printf("%-43s %s%n", name, checkData(new File(outDir, name), warnings, true));
      }

      System.out.println();
      System.out.println("================================================================");
      for (String f : generatorFailures) {
         System.out.println(" GENERATOR ERROR: " + f);
      }
      for (String w : warnings) {
         System.out.println(" WARNING: " + w);
      }
      for (String f : failures) {
         System.out.println(" FAIL: " + f);
      }

      boolean ok = generatorFailures.isEmpty() && failures.isEmpty();
      if (ok && warnings.isEmpty()) {
         System.out.println(" RESULT: PASS - all reports generated and look good.");
      } else if (ok) {
         System.out.println(" RESULT: PASS (with warnings) - all reports generated; see warnings above.");
      } else {
         System.out.println(
            " RESULT: FAIL - see errors above. Common cause: git_try checkout missing, or a " + "generator threw.");
      }
      System.out.println(" Packet: " + outDir.getAbsolutePath());
      System.out.println("================================================================");

      // Open the entry page for a quick visual eyeball (local runs only; ignored when headless).
      File overview = new File(outDir, "XWidget_Overview.html");
      if (overview.isFile()) {
         try {
            java.awt.Desktop.getDesktop().open(overview);
         } catch (Exception ex) {
            // ignore
         }
      }
   }

   /** @return a status string for an HTML report, adding to {@code failures} if it is missing/empty/truncated. */
   private static String checkHtml(File file, List<String> failures) {
      if (!file.isFile()) {
         failures.add("Missing report: " + file.getName());
         return "MISSING";
      }
      long size = file.length();
      if (size < MIN_REPORT_BYTES) {
         failures.add("Report too small (" + size + " bytes), likely empty: " + file.getName());
         return "TOO SMALL (" + size + "B)";
      }
      try {
         String content = Files.readString(file.toPath());
         String tail = content.stripTrailing();
         if (!tail.endsWith("</html>")) {
            failures.add("Report not closed with </html> (truncated?): " + file.getName());
            return "TRUNCATED";
         }
      } catch (IOException ex) {
         failures.add("Could not read report: " + file.getName() + " (" + ex.getMessage() + ")");
         return "UNREADABLE";
      }
      return "OK (" + size + "B)";
   }

   /**
    * @return a status string for a data file. When {@code optional}, a missing file is recorded as a warning (added to
    * the passed list) rather than a hard failure - the "old" side JSON is absent when the git_try checkout is missing.
    */
   private static String checkData(File file, List<String> problems, boolean optional) {
      if (!file.isFile()) {
         problems.add(
            (optional ? "Optional " : "") + "data file missing: " + file.getName() + (optional ? " (git_try checkout not present?)" : ""));
         return optional ? "MISSING (optional)" : "MISSING";
      }
      long size = file.length();
      if (size < 2) {
         problems.add("Data file empty: " + file.getName());
         return "EMPTY";
      }
      return "OK (" + size + "B)";
   }

}
