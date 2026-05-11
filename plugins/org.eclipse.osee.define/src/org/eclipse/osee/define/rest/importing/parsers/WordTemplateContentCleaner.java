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

import java.util.regex.Pattern;
import org.eclipse.osee.define.rest.importing.parsers.ArtifactImportExportUtils.ArtifactRecord;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;

/**
 * DISCLAIMER: This cleaner works for >98% cases. Each update to word template content attribute should still be
 * manually reviewed by branch change report.
 *
 * @author Jaden W. Puckett
 */
public class WordTemplateContentCleaner {
   private final StringBuffer errorLog = new StringBuffer();
   private final BranchId branchId;

   /**
    * Trims trailing paragraphs that are "unwanted" at the very end of the WordML: 1) A paragraph whose only meaningful
    * content is <w:pPr><w:sectPr>...</w:sectPr></w:pPr> 2) A paragraph whose only meaningful content is one or more
    * page breaks: <w:r> <w:br w:type="page"/> or <w:br w:type="page"></w:br> </w:r> repeated any number of times, with
    * optional <w:pPr> (e.g., <w:pStyle .../>).
    */
   private static final Pattern TRAILING_UNWANTED_PATTERN = Pattern.compile("(?s)(?:"
      // (1) trailing sectPr-only paragraph
      + "<w:p\\b[^>]*>\\s*" + "<w:pPr\\b[^>]*>\\s*<w:sectPr\\b[^>]*>.*?</w:sectPr>\\s*</w:pPr>\\s*" + "</w:p>" + "|"
      // (2) trailing page-break-only paragraph (supports 1+ runs, each containing only a page break)
      + "<w:p\\b[^>]*>\\s*" + "(?:<w:pPr\\b[^>]*>.*?</w:pPr>\\s*)?" // optional pPr (can include pStyle, etc.)
      + "(?:" + "<w:r\\b[^>]*>\\s*" + "(?:" + "<w:br\\b[^>]*w:type\\s*=\\s*\"page\"[^>]*/\\s*>" // <w:br .../>
      + "|" + "<w:br\\b[^>]*w:type\\s*=\\s*\"page\"[^>]*>\\s*</w:br>" // <w:br ...></w:br>
      + ")\\s*" + "</w:r>\\s*" + ")+" // one or more page-break runs
      + "</w:p>" + ")\\s*$");

   public WordTemplateContentCleaner(BranchId branchId) {
      this.branchId = branchId;
   }

   public String trimUnwantedSectionsFromEnd(String wordTemplateContent,
      ArtifactImportExportUtils.ArtifactRecord record) {

      if (wordTemplateContent == null || wordTemplateContent.isEmpty()) {
         logError("Input content is null or empty", record.getArtifactId());
         return wordTemplateContent;
      }

      String original = wordTemplateContent;
      String working = wordTemplateContent;

      // Keep trimming until nothing unwanted remains at the end
      while (TRAILING_UNWANTED_PATTERN.matcher(working).find()) {
         working = TRAILING_UNWANTED_PATTERN.matcher(working).replaceFirst("");
      }

      logCleaningDetails(original, working, record);
      return working;
   }

   private synchronized void logCleaningDetails(String original, String cleaned,
      ArtifactImportExportUtils.ArtifactRecord record) {
      errorLog.append("\n---------------------------\n");
      errorLog.append("\nArtifactRecord: ").append(record.getName()).append(" ").append(record.getArtifactId()).append(
         "\n");
      errorLog.append("\nBefore cleaning:\n\n").append(original);
      errorLog.append("\nAfter cleaning:\n\n").append(cleaned);
      errorLog.append("\n---------------------------\n");
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