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

import com.vladsch.flexmark.ext.autolink.AutolinkExtension;
import com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.ext.toc.TocExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.core.util.OseeInf;

/**
 * @author Jaden W. Puckett
 */

public class MarkdownConverter {
   private final String markdownContent;
   private MutableDataSet options;

   public MarkdownConverter(String markdownContent) {
      this.markdownContent = markdownContent;
      this.setDefaultOptions();
   }

   public String getMarkdownContent() {
      return markdownContent;
   }

   public String toHtml() {
      Parser parser = Parser.builder(this.options).build();
      HtmlRenderer renderer = HtmlRenderer.builder(this.options).build();
      Node document = parser.parse(this.markdownContent);
      return "<html><head>" + getCssStyles() + "</head><body>\n" + renderer.render(document) + "</body></html>";
   }

   private void setDefaultOptions() {
      options = new MutableDataSet();
      options.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create(), TaskListExtension.create(),
         TocExtension.create(), AutolinkExtension.create()));
   }

   protected void setOptions(MutableDataSet options) {
      this.options = options;
   }

   private String getCssStyles() {
      String temp = OseeInf.getResourceContents("markdownToHtmlStyles.css", getClass());
      Pattern pattern = Pattern.compile("^\\s*/\\*{2}.*?\\*/\\s*", Pattern.DOTALL);
      Matcher matcher = pattern.matcher(temp);
      return "<style>\n" + matcher.replaceFirst("") + "\n</style>";
   }
}
