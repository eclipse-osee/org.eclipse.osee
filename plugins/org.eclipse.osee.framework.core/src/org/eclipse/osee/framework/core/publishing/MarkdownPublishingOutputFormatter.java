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

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.publishing.markdown.MarkdownConverter;
import org.eclipse.osee.framework.core.publishing.markdown.MarkdownHtmlUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MarkdownPublishingOutputFormatter extends SinglePagePublishingOutputFormatter {

   public MarkdownPublishingOutputFormatter() {
      super(PublishingOutputFormatMode.MARKDOWN, CoreArtifactTokens.HtmlDataRightsFooters);
   }

   @Override
   public String formatToc(String content) {

      MarkdownConverter mdConverter = new MarkdownConverter();

      Document htmlDoc = Jsoup.parse(mdConverter.convertToHtmlStringWithStyle(content));

      Elements tocElements = htmlDoc.select(".toc");

      if (!tocElements.isEmpty()) {
         // Replace TOCs with rendered HTML to preserve flexmark styling.
         Pattern tocPattern = Pattern.compile(MarkdownHtmlUtil.TOC_PATTERN_STRING);
         Matcher tocMatcher = tocPattern.matcher(content);

         for (Element tocElement : tocElements) {
            // Replace TOCs in order.
            if (tocMatcher.find()) {
               content = content.replaceFirst(Pattern.quote(tocMatcher.group()), tocElement.outerHtml());
            }
         }
      }

      return content;
   }

   @Override
   public Optional<String> getDefaultCaptionStyle() {
      return Optional.of(" style=\"text-align: center;\"");
   }
}
