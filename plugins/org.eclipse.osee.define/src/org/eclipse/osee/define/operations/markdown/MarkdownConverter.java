/*********************************************************************
 * Copyright (c) 2024 Boeing
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
package org.eclipse.osee.define.operations.markdown;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.eclipse.osee.framework.core.util.OseeInf;

import com.vladsch.flexmark.ext.autolink.AutolinkExtension;
import com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.ext.toc.TocExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;

/**
 * @author Jaden W. Puckett
 */

public class MarkdownConverter {

   private MutableDataSet options;

   public MarkdownConverter() {
      setDefaultOptions();
   }

   public MarkdownConverter(MutableDataSet options) {
      this.options = options;
   }

   public void setOptions(MutableDataSet options) {
      this.options = options;
   }

   private void setDefaultOptions() {
      options = new MutableDataSet();
      options.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create(), TaskListExtension.create(),
         TocExtension.create(), AutolinkExtension.create()));
   }

   public ByteArrayInputStream convertToHtmlStream(ByteArrayInputStream markdownInputStream) {
      // Convert Markdown ByteArrayInputStream to String
      String markdownContent = new String(markdownInputStream.readAllBytes(), StandardCharsets.UTF_8);

      // Convert Markdown to HTML
      String htmlContent = convertToHtmlString(markdownContent);

      // Convert HTML String to ByteArrayInputStream
      return new ByteArrayInputStream(htmlContent.getBytes(StandardCharsets.UTF_8));
   }

   public String convertToHtmlString(String markdownContent) {
      Parser parser = Parser.builder(this.options).build();
      HtmlRenderer renderer = HtmlRenderer.builder(this.options).build();
      Node document = parser.parse(markdownContent);
      return "<html><head><meta charset=\"UTF-8\">" + getCssStyles() + "</head><body>\n" + renderer.render(document) + "</body></html>";
   }

   private String getCssStyles() {
      return "<style>\n" + OseeInf.getResourceContents("markdownToHtmlStyles.css",
         MarkdownConverter.class) + "\n</style>";
   }

}
