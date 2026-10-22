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

import java.awt.Desktop;
import java.io.File;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * Master CodeClean that runs all individual CodeClean operations in sequence and reports combined results. Mirrors
 * {@link CodeReviewAll}, but for the clean/organize operations.
 * <p>
 * <b>Unlike {@link CodeReviewAll}, this is standalone-only and is deliberately NOT wired into any JUnit suite.</b>
 * Every operation here MUTATES source files (formatting, member reordering, reverting non-functional changes), so it
 * must never run as part of the integration test suite. Run it manually via {@link #main(String[])} (Run As &gt; Java
 * Application) when preparing a change for review. Eclipse > Select CodeCleanAll > Right-click > Run As or Debug As >
 * Java Application.
 * <p>
 * Operations run in pipeline order:
 * <ol>
 * <li>{@link CodeCleanFormatAndOrganize} - Eclipse JDT format of files changed in the last commit (requires the
 * {@code ECLIPSEC_EXE} environment variable; it reports and skips gracefully if not configured).</li>
 * <li>{@link CodeCleanOrganizeBlams} - reorders {@code *Blam.java} members into the standard BLAM order.</li>
 * <li>{@link CodeCleanOrganizeXWidgets} - reorders {@code XX*Widget.java} members into the standard order.</li>
 * <li>{@link CodeCleanRevertNonFunctional} - reverts files in the last commit whose only changes are non-functional
 * (whitespace/imports/blank lines), so cosmetic churn from the steps above is dropped.</li>
 * </ol>
 *
 * @author Donald G. Dunne
 */
public class CodeCleanAll {

   private final XResultData rd = new XResultData();

   public void run() {
      rd.logf("=== Running All Code Cleans ===\n\n");
      rd.logf("NOTE: CodeClean operations modify source files. Review changes with git before committing.\n\n");
      rd.addResultsTag();

      // Pipeline order: format first, then structural reorganizers, then revert non-functional churn last.
      // All four extend AbstractCodeClean, so each is run + merged uniformly.
      runClean(new CodeCleanFormatAndOrganize());
      runClean(new CodeCleanOrganizeBlams());
      runClean(new CodeCleanOrganizeXWidgets());
      runClean(new CodeCleanRevertNonFunctional());

      rd.logf("\n\n=== Summary ===\n");
      rd.logf("Errors: %d\n", rd.getNumErrors());
      rd.logf("Warnings: %d\n", rd.getNumWarnings());

      reportResults();
   }

   /**
    * Run one CodeClean operation and merge its results into the combined report. Each op also writes its own
    * {@code ~/<name>.html}. One failing op never aborts the whole run.
    */
   private void runClean(AbstractCodeClean op) {
      String name = op.getCleanName();
      rd.logf("\n=== %s ===\n", name);
      try {
         op.run();
         rd.merge(op.getResults());
         if (op.getResults().isErrors()) {
            rd.logf("  %d error(s) - see section above and ~/%s.html\n", op.getResults().getNumErrors(), name);
         } else {
            rd.logf("  Done - see ~/%s.html\n", name);
         }
      } catch (RuntimeException ex) {
         rd.errorf("  Error running %s: %s\n", name, Lib.exceptionToString(ex));
      }
   }

   private void reportResults() {
      File outFile =
         CodeQualityUtil.getReportFile(CodeQualityUtil.CODE_CLEAN_DIR, "CodeClean", "CodeCleanAll");
      try {
         Lib.writeStringToFile(rd.getHtml(), outFile);
         Desktop.getDesktop().open(outFile);
      } catch (Exception ex) {
         // Silently fail if browser can't open
      }
   }

   public XResultData getResults() {
      return rd;
   }

   public static void main(String[] args) {
      new CodeCleanAll().run();
   }

}
