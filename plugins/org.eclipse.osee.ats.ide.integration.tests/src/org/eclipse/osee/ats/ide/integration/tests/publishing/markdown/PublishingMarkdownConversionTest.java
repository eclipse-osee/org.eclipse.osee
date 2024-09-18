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
package org.eclipse.osee.ats.ide.integration.tests.publishing.markdown;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.ats.ide.util.ServiceUtil;
import org.eclipse.osee.define.rest.api.publisher.publishing.PublishingEndpoint;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests for Markdown conversion operations.
 *
 * @author Jaden W. Puckett
 */
public class PublishingMarkdownConversionTest {

   private static PublishingEndpoint publishingEndpoint;

   @BeforeClass
   public static void testSetup() {
      PublishingMarkdownConversionTest.publishingEndpoint = ServiceUtil.getOseeClient().getPublishingEndpoint();
   }

   public static String trimHtml(String html) {
      Pattern pattern = Pattern.compile("<body>(.*?)</body>", Pattern.DOTALL);
      Matcher matcher = pattern.matcher(html);
      if (matcher.find()) {
         String bodyContent = matcher.group(1);
         return bodyContent.trim();
      } else {
         return "";
      }
   }

   @Test
   public void testHeaderConversion() {
      String markdown = "# Header 1";
      String expectedHtml = "<h1 id=\"header-1\">Header 1</h1>";
      try {
         String html = PublishingMarkdownConversionTest.publishingEndpoint.convertMarkdownToHtml(markdown).readEntity(
            String.class);
         String trimmedHtml = trimHtml(html);
         assertEquals(expectedHtml, trimmedHtml);
      } catch (Exception e) {
         fail("An exception occurred: " + e.getMessage());
      }
   }

   @Test
   public void testBoldTextConversion() {
      String markdown = "**Bold Text**";
      String expectedHtml = "<p><strong>Bold Text</strong></p>";
      try {
         String html = PublishingMarkdownConversionTest.publishingEndpoint.convertMarkdownToHtml(markdown).readEntity(
            String.class);
         String trimmedHtml = trimHtml(html);
         assertEquals(expectedHtml, trimmedHtml);
      } catch (Exception e) {
         fail("An exception occurred: " + e.getMessage());
      }
   }

   @Test
   public void testItalicTextConversion() {
      String markdown = "*Italic Text*";
      String expectedHtml = "<p><em>Italic Text</em></p>";
      try {
         String html = PublishingMarkdownConversionTest.publishingEndpoint.convertMarkdownToHtml(markdown).readEntity(
            String.class);
         String trimmedHtml = trimHtml(html);
         assertEquals(expectedHtml, trimmedHtml);
      } catch (Exception e) {
         fail("An exception occurred: " + e.getMessage());
      }
   }

   @Test
   public void testLinkConversion() {
      String markdown = "[Link](http://example.com)";
      String expectedHtml = "<p><a href=\"http://example.com\">Link</a></p>";
      try {
         String html = PublishingMarkdownConversionTest.publishingEndpoint.convertMarkdownToHtml(markdown).readEntity(
            String.class);
         String trimmedHtml = trimHtml(html);
         assertEquals(expectedHtml, trimmedHtml);
      } catch (Exception e) {
         fail("An exception occurred: " + e.getMessage());
      }
   }

   @Test
   public void testTableConversion() {
      String markdown = "| Header 1 | Header 2 | Header 3 |\r\n| :-: | :-- | --: |\r\n| Cell 1 | Cell 2 | Cell 3 |";
      String expectedHtml =
         "<table>\n<thead>\n<tr><th align=\"center\">Header 1</th><th align=\"left\">Header 2</th><th align=\"right\">Header 3</th></tr>\n</thead>\n<tbody>\n<tr><td align=\"center\">Cell 1</td><td align=\"left\">Cell 2</td><td align=\"right\">Cell 3</td></tr>\n</tbody>\n</table>";
      try {
         String html = PublishingMarkdownConversionTest.publishingEndpoint.convertMarkdownToHtml(markdown).readEntity(
            String.class);
         String trimmedHtml = trimHtml(html);
         assertEquals(expectedHtml, trimmedHtml);
      } catch (Exception e) {
         fail("An exception occurred: " + e.getMessage());
      }
   }

   @Test
   public void testTaskListConversion() {
      String markdown = "- [x] Task 1\r\n- [ ] Task 2";
      String expectedHtml =
         "<ul>\n<li class=\"task-list-item\"><input type=\"checkbox\" class=\"task-list-item-checkbox\" checked=\"checked\" disabled=\"disabled\" readonly=\"readonly\" />&nbsp;Task 1</li>\n<li class=\"task-list-item\"><input type=\"checkbox\" class=\"task-list-item-checkbox\" disabled=\"disabled\" readonly=\"readonly\" />&nbsp;Task 2</li>\n</ul>";
      try {
         String html = PublishingMarkdownConversionTest.publishingEndpoint.convertMarkdownToHtml(markdown).readEntity(
            String.class);
         String trimmedHtml = trimHtml(html);
         assertEquals(expectedHtml, trimmedHtml);
      } catch (Exception e) {
         fail("An exception occurred: " + e.getMessage());
      }
   }

   @Test
   public void testListConversion() {
      String markdown = "- Item 1\n- Item 2";
      String expectedHtml = "<ul>\n<li>Item 1</li>\n<li>Item 2</li>\n</ul>";
      try {
         String html = PublishingMarkdownConversionTest.publishingEndpoint.convertMarkdownToHtml(markdown).readEntity(
            String.class);
         String trimmedHtml = trimHtml(html);
         assertEquals(expectedHtml, trimmedHtml);
      } catch (Exception e) {
         fail("An exception occurred: " + e.getMessage());
      }
   }

   @Test
   public void testHorizontalDividerConversion() {
      String markdown = "---";
      String expectedHtml = "<hr />";
      try {
         String html = PublishingMarkdownConversionTest.publishingEndpoint.convertMarkdownToHtml(markdown).readEntity(
            String.class);
         String trimmedHtml = trimHtml(html);
         assertEquals(expectedHtml, trimmedHtml);
      } catch (Exception e) {
         fail("An exception occurred: " + e.getMessage());
      }
   }
}
