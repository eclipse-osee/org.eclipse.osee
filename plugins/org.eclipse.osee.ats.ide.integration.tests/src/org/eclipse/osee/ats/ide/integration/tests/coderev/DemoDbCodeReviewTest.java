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

import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit test that runs all code reviews. Behavior depends on context:
 * <ul>
 * <li>Run individually: always opens HTML report in browser</li>
 * <li>Run as integration suite: fails test and opens report only if errors, passes silently otherwise</li>
 * </ul>
 * On failure the collected error lines are included in the JUnit failure message so the cause is visible in a
 * headless/CI run (where there is no browser and the HTML report is not surfaced).
 *
 * @author Donald G. Dunne
 */
public class DemoDbCodeReviewTest {

   @Test
   public void testCodeReviewOsgiXml() {
      runAndAssert(new CodeReviewOsgiXml());
   }

   @Test
   public void testCodeReviewXWidgets() {
      runAndAssert(new CodeReviewXWidgets());
   }

   @Test
   public void testCodeReviewBlams() {
      runAndAssert(new CodeReviewBlams());
   }

   @Test
   public void testCodeReviewGeneral() {
      runAndAssert(new CodeReviewGeneral());
   }

   private static void runAndAssert(AbstractCodeReview review) {
      boolean passed = review.runAndAssert();
      Assert.assertTrue(buildFailureMessage(review), passed);
   }

   /** Build a failure message that embeds the actual error lines so the cause is visible without the HTML report. */
   private static String buildFailureMessage(AbstractCodeReview review) {
      StringBuilder sb = new StringBuilder();
      sb.append(review.getClass().getSimpleName()).append(" found errors (HTML report also written):\n");
      List<String> errors = review.getResults().getErrorStrings();
      if (errors.isEmpty()) {
         sb.append(review.getResults().toString());
      } else {
         for (String error : errors) {
            sb.append("  ").append(error).append("\n");
         }
      }
      return sb.toString();
   }

}
