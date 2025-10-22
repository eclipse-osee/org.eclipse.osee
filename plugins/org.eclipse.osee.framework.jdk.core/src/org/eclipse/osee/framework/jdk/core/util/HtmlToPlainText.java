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

package org.eclipse.osee.framework.jdk.core.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeVisitor;

public class HtmlToPlainText {
   /**
    * Convert HTML to plain text preserving meaningful newlines. Relies on jsoup (https://jsoup.org/) for parsing and
    * entity decoding. Behavior: - Removes script and style content. - Collapses multiple whitespace in text nodes to
    * single spaces except for newlines inserted when encountering block-level elements. - Inserts newlines for
    * block-level elements (p, div, br, li, h1..h6, tr, etc.).
    *
    * @param html the HTML string (may be null)
    * @return plain text with newlines
    */
   public static String htmlToPlainTextWithNewlines(String html) {
      if (html == null || html.isEmpty()) {
         return "";
      }

      Document doc = Jsoup.parse(html);
      // Remove elements that shouldn't contribute visible text
      doc.select("script, style, noscript, iframe, head, meta, link").remove();

      final StringBuilder sb = new StringBuilder();

      // A simple set of tags that should produce a newline before/after or both.
      // This is conservative — adjust as needed.
      final java.util.Set<String> blockTags = new java.util.HashSet<>();
      String[] blocks = {
         "p",
         "div",
         "section",
         "article",
         "header",
         "footer",
         "aside",
         "nav",
         "figure",
         "figcaption",
         "h1",
         "h2",
         "h3",
         "h4",
         "h5",
         "h6",
         "li",
         "ul",
         "ol",
         "table",
         "thead",
         "tbody",
         "tfoot",
         "tr",
         "td",
         "th",
         "br",
         "hr",
         "address"};
      for (String t : blocks) {
         blockTags.add(t);
      }

      // Walk the DOM and build text, inserting newlines for block boundaries.
      doc.body().traverse(new NodeVisitor() {
         @Override
         public void head(Node node, int depth) {
            if (node instanceof Element) {
               Element el = (Element) node;
               String tag = el.tagName().toLowerCase();
               if ("br".equals(tag)) {
                  sb.append("\n");
               } else if (blockTags.contains(tag)) {
                  // Add newline if not at start and previous char isn't newline
                  if (sb.length() > 0 && sb.charAt(sb.length() - 1) != '\n') {
                     sb.append("\n");
                  }
               }
            } else if (node instanceof TextNode) {
               String text = ((TextNode) node).text();
               if (!text.trim().isEmpty()) {
                  // Collapse internal whitespace to single spaces (but preserve intentional newlines we add)
                  String collapsed = text.replaceAll("\\s+", " ");
                  // Avoid adding leading space if previous char is whitespace or newline
                  if (sb.length() > 0) {
                     char last = sb.charAt(sb.length() - 1);
                     if (last == ' ' || last == '\n') {
                        collapsed = collapsed.replaceAll("^\\s+", "");
                     }
                  }
                  sb.append(collapsed);
               }
            }
         }

         @Override
         public void tail(Node node, int depth) {
            if (node instanceof Element) {
               Element el = (Element) node;
               String tag = el.tagName().toLowerCase();
               if (blockTags.contains(tag)) {
                  // Ensure a trailing newline after block elements
                  if (sb.length() > 0 && sb.charAt(sb.length() - 1) != '\n') {
                     sb.append("\n");
                  }
               }
            }
         }
      });

      // Post-process: collapse multiple consecutive blank lines to at most two, trim edges
      String result = sb.toString().replaceAll("[ \\t]+(?=\\n)", "") // remove trailing spaces before newlines
         .replaceAll("\\n{3,}", "\n\n") // collapse 3+ newlines to 2
         .trim();

      return result;
   }

   // Simple test
   public static void main(String[] args) {
      String html =
         "<html><body><h1>Title</h1><p>This is a <b>paragraph</b> with <a href='#'>link</a>.</p>" + "<div>Another block<br>line two<br/>line three</div><ul><li>One</li><li>Two</li></ul>" + "<script>var a = 1;</script></body></html>";

      System.out.println(htmlToPlainTextWithNewlines(html));
   }

}