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
 * Master code review that runs all individual code reviews and reports combined results. Can be run standalone via
 * main() or as a JUnit test via {@link #runAndAssert()}.
 *
 * @author Donald G. Dunne
 */
public class CodeReviewAll {

   private final XResultData rd = new XResultData();

   public void run() {
      rd.logf("=== Running All Code Reviews ===\n\n");
      rd.addResultsTag();

      runReview(new CodeReviewOsgiXml());
      runReview(new CodeReviewXWidgets());
      runReview(new CodeReviewBlams());
      runReview(new CodeReviewGeneral());

      rd.logf("\n\n=== Summary ===\n");
      rd.logf("Errors: %d\n", rd.getNumErrors());
      rd.logf("Warnings: %d\n", rd.getNumWarnings());

      reportResults();
   }

   /**
    * Run all reviews and return true if no errors. Opens combined report if not in integration test. Individual reviews
    * with errors open their own reports.
    */
   public boolean runAndAssert() {
      runReview(new CodeReviewOsgiXml());
      runReview(new CodeReviewXWidgets());
      runReview(new CodeReviewBlams());
      runReview(new CodeReviewGeneral());

      if (rd.isErrors()) {
         reportResults();
      }
      return !rd.isErrors();
   }

   private void runReview(AbstractCodeReview review) {
      rd.logf("\n=== %s ===\n", review.getReviewName());
      review.runAndAssert();
      XResultData reviewRd = review.getResults();
      rd.merge(reviewRd);
      if (reviewRd.isErrors()) {
         rd.logf("  FAILED - %d errors found\n", reviewRd.getNumErrors());
      } else {
         rd.logf("  PASSED\n");
      }
   }

   private void reportResults() {
      File outFile =
         CodeQualityUtil.getReportFile(CodeQualityUtil.CODE_REVIEW_DIR, "CodeReview", "CodeReviewAll");
      String html = rd.getHtml();
      try {
         Lib.writeStringToFile(html, outFile);
         Desktop.getDesktop().open(outFile);
      } catch (Exception ex) {
         // Silently fail if browser can't open
      }
   }

   public XResultData getResults() {
      return rd;
   }

   public static void main(String[] args) {
      new CodeReviewAll().run();
   }

}
