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
package org.eclipse.osee.define.rest.importing.parsers;

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;

/**
 * Thread safe
 *
 * @author Jaden W. Puckett
 */
public class WordTemplateContentCleaner {
   private final StringBuffer errorLog = new StringBuffer();
   private final BranchId branchId;

   public WordTemplateContentCleaner(BranchId branchId) {
      this.branchId = branchId;
   }

   public String removeEndingEmptySectionBreaksAndEmptyPageBreaksFromWordTemplateContent(String wordTemplateContent,
      ArtifactImportExportUtils.ArtifactRecord record) {
      if (wordTemplateContent == null || wordTemplateContent.isEmpty()) {
         return wordTemplateContent;
      }

      // Regular expression to match <w:p> blocks with <w:sectPr> (section breaks) at the very end of the string
      String trailingSectionBreakPattern =
         "<w:p[^>]*>\\s*<w:pPr>\\s*<w:sectPr[^>]*>.*?</w:sectPr>\\s*</w:pPr>\\s*</w:p>\\s*\\z";

      // Regular expression to match <w:p> blocks that are empty or contain only a page break at the very end of the string
      String trailingEmptyOrPageBreakPattern =
         "<w:p[^>]*>\\s*(<w:pPr>.*?</w:pPr>)?\\s*(<w:r>\\s*<w:br[^>]*w:type=\"page\"[^>]*>\\s*</w:br>\\s*</w:r>)?\\s*</w:p>\\s*\\z";

      // Remove trailing section breaks and empty/page-break paragraphs
      while (true) {
         String updatedContent = wordTemplateContent.replaceAll(trailingSectionBreakPattern, "") // Remove trailing section breaks
            .replaceAll(trailingEmptyOrPageBreakPattern, ""); // Remove trailing empty/page-break paragraphs

         if (updatedContent.equals(wordTemplateContent)) {
            break; // Stop if no further changes are made
         }

         wordTemplateContent = updatedContent;
      }

      errorLog.append("\n---------------------------\n");
      errorLog.append("\nArtifactRecord: " + record.getName() + " " + record.getArtifactId() + "\n");
      errorLog.append("\nBefore cleaning:\n\n" + record.getWordTemplateContent());
      errorLog.append("\nAfter cleaning:\n\n" + wordTemplateContent);
      errorLog.append("\n---------------------------\n");

      return wordTemplateContent;
   }

   public synchronized void logError(String message, ArtifactId artifactId) {
      errorLog.append("\n<!----------------------------------------\n").append("Error for artifact: ").append(
         artifactId).append(" on branch: ").append(branchId).append("\n").append(message).append(
            "\n---------------------------------------->\n");
   }

   public synchronized String getErrorLog() {
      return errorLog.toString();
   }
}
