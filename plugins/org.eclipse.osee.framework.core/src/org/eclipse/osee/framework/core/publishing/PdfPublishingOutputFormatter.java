/*********************************************************************
 * Copyright (c) 2025 Boeing
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
package org.eclipse.osee.framework.core.publishing;

import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;

public class PdfPublishingOutputFormatter extends AbstractPublishingOutputFormatter {

   public PdfPublishingOutputFormatter() {
      super(PublishingOutputFormatMode.PAGED, CoreArtifactTokens.HtmlDataRightsFooters);
   }

   @Override
   public String formatDataRightsOpen(String classification, String content) {
      if (!cssKeyExists(classification)) {
         addCss(classification, getDataRightsCss(classification, content));
      }

      return String.format("<div class=\"page-%s\">", getPageClassName(classification));
   }

   @Override
   public String formatDataRightsClose(String content) {
      return "</div>";
   }

   @Override
   public String getDataRightsCss(String classification, String content) {
      String pageName = getPageClassName(classification);

      // Escape content for CSS: Replace newlines with \A and escape double quotes
      String escapedContent = content.replace("\\", "\\\\") // escape backslashes
         .replace("\"", "\\\"") // escape double quotes
         .replace("\n", "\\A ") // convert line breaks to CSS line breaks
         .replace("\r", ""); // remove carriage returns

      //@formatter:off
      // Build the CSS string
      return String.format(
          "@page %s {\n" +
          "  size: letter;\n" +
          "  margin: 1in;\n\n" +
          "  @bottom-center {\n" +
          "    content: \"%s\";\n" +
          "    white-space: pre-wrap;\n" +
          "    text-align: center;\n" +
          "    font-size: 8pt;\n" +
          "  }\n" +
          "}\n" +
          ".page-%s {\n" +
          "  page: %s;\n" +
          "}", pageName, escapedContent, pageName, pageName
      );
      //@formatter:on
   }

   /*
    * Normalize classification to be used as a CSS-friendly identifier
    */
   private String getPageClassName(String classification) {
      return classification.toLowerCase().replaceAll("[^a-z0-9]+", "-");
   }
}
