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
package org.eclipse.osee.ats.ide.integration.tests.publishing.markdown;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import org.eclipse.osee.framework.core.publishing.markdown.MarkdownConverter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests for {@link MarkdownConverter#embedImages(String, Map)}.
 * Verifies data URI embedding, dimension capping (standalone vs table cell), and size attribute handling.
 */
public class EmbedImagesTest {

   private static final byte[] PNG_2x2;
   private static final byte[] PNG_400x300;
   private static final String SRC_400 = "resources/big_400x300.png";
   private static final String SRC_TINY = "resources/tiny.png";

   static {
      try {
         PNG_2x2 = createPng(2, 2);
         PNG_400x300 = createPng(400, 300);
      } catch (IOException e) {
         throw new ExceptionInInitializerError(e);
      }
   }

   private static byte[] createPng(int width, int height) throws IOException {
      BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ImageIO.write(img, "png", baos);
      return baos.toByteArray();
   }

   private static MarkdownConverter converter;

   @BeforeClass
   public static void setup() {
      converter = new MarkdownConverter();
   }

   // ─── Helpers ──────────────────────────────────────────────────────────────────

   private static String base64(byte[] bytes) {
      return Base64.getEncoder().encodeToString(bytes);
   }

   private static Map<String, String> imageMap(String src, byte[] bytes) {
      Map<String, String> map = new HashMap<>();
      map.put(src, base64(bytes));
      return map;
   }

   /** Embeds an image in a standalone paragraph context and returns the parsed img element. */
   private Element embedStandalone(String src, byte[] bytes, String style) {
      String styleAttr = style != null ? " style=\"" + style + "\"" : "";
      String html = "<html><body><p><img src=\"" + src + "\" alt=\"test\"" + styleAttr + " /></p></body></html>";
      String result = converter.embedImages(html, imageMap(src, bytes));
      return Jsoup.parse(result).selectFirst("img");
   }

   /** Embeds an image inside a table cell and returns the parsed img element. */
   private Element embedInTable(String src, byte[] bytes, String style) {
      String styleAttr = style != null ? " style=\"" + style + "\"" : "";
      String html = "<html><body><table><tr><td><img src=\"" + src + "\" alt=\"test\"" + styleAttr + " /></td></tr></table></body></html>";
      String result = converter.embedImages(html, imageMap(src, bytes));
      return Jsoup.parse(result).selectFirst("td img");
   }

   private int getWidth(Element img) {
      return Integer.parseInt(img.attr("width"));
   }

   private int getHeight(Element img) {
      return Integer.parseInt(img.attr("height"));
   }

   // ─── Data URI Embedding ───────────────────────────────────────────────────────

   @Test
   public void testImageEmbeddedAsDataUri() {
      Element img = embedStandalone(SRC_TINY, PNG_2x2, null);
      assertTrue("Image src should be a data URI", img.attr("src").startsWith("data:image/png;base64,"));
   }

   @Test
   public void testUnmatchedImageNotModified() {
      String html = "<html><body><p><img src=\"resources/unknown.png\" alt=\"x\" /></p></body></html>";
      String result = converter.embedImages(html, new HashMap<>());
      Element img = Jsoup.parse(result).selectFirst("img");
      assertEquals("Unmatched image src should remain unchanged", "resources/unknown.png", img.attr("src"));
      assertFalse("Unmatched image should not have width attribute", img.hasAttr("width"));
   }

   // ─── Dimension Capping (no size attribute) ────────────────────────────────────

   @Test
   public void testStandaloneDimensionsUnchangedWhenUnderMax() {
      Element img = embedStandalone(SRC_400, PNG_400x300, null);
      assertEquals("Width should be native 400 (under 468 max)", 400, getWidth(img));
      assertEquals("Height should be native 300", 300, getHeight(img));
   }

   @Test
   public void testTableCellCapsAt150() {
      Element img = embedInTable(SRC_400, PNG_400x300, null);
      assertTrue("Table image width should be <= 150", getWidth(img) <= 150);
   }

   @Test
   public void testThCellCapsLikeTd() {
      String html = "<html><body><table><tr><th><img src=\"" + SRC_400 + "\" alt=\"test\" /></th></tr></table></body></html>";
      String result = converter.embedImages(html, imageMap(SRC_400, PNG_400x300));
      Element img = Jsoup.parse(result).selectFirst("th img");
      assertTrue("Image in <th> should be constrained like <td>", getWidth(img) <= 150);
   }

   @Test
   public void testSmallImageNotUpscaled() {
      Element img = embedStandalone(SRC_TINY, PNG_2x2, null);
      assertEquals("Small image should stay at native 2px width", 2, getWidth(img));
      assertEquals("Small image should stay at native 2px height", 2, getHeight(img));
   }

   @Test
   public void testSameImageDifferentContexts() {
      String html = "<html><body>" +
         "<p><img src=\"" + SRC_400 + "\" alt=\"standalone\" /></p>" +
         "<table><tr><td><img src=\"" + SRC_400 + "\" alt=\"in-table\" /></td></tr></table>" +
         "</body></html>";
      String result = converter.embedImages(html, imageMap(SRC_400, PNG_400x300));
      Document doc = Jsoup.parse(result);
      Elements imgs = doc.select("img");

      int standaloneWidth = Integer.parseInt(imgs.get(0).attr("width"));
      int tableWidth = Integer.parseInt(imgs.get(1).attr("width"));

      assertTrue("Standalone should be wider than table-constrained", standaloneWidth > tableWidth);
      assertTrue("Table image should be <= 150", tableWidth <= 150);
   }

   @Test
   public void testMultipleImagesInTable() {
      String html = "<html><body><table><tr>" +
         "<td><img src=\"resources/img1.png\" alt=\"img1\" /></td>" +
         "<td><img src=\"resources/img2.png\" alt=\"img2\" /></td>" +
         "</tr></table></body></html>";
      Map<String, String> map = new HashMap<>();
      map.put("resources/img1.png", base64(PNG_400x300));
      map.put("resources/img2.png", base64(PNG_400x300));

      String result = converter.embedImages(html, map);
      Elements imgs = Jsoup.parse(result).select("td img");

      assertEquals("Should find 2 images", 2, imgs.size());
      for (Element img : imgs) {
         assertTrue("Should have data URI", img.attr("src").startsWith("data:"));
         assertTrue("Should be <= 150px", getWidth(img) <= 150);
      }
   }

   // ─── Size Attribute (max-width style) ─────────────────────────────────────────

   @Test
   public void testSizePercentagesStandalone() {
      // 400x300 image, standalone max = 468. Result = min(percent * 468, native 400).
      Object[][] cases = {
         {25, 117},  // 25% of 468 = 117
         {50, 234},  // 50% of 468 = 234
         {75, 351},  // 75% of 468 = 351
         {100, 400}, // 100% of 468 = 468, capped at native 400
      };
      for (Object[] c : cases) {
         int percent = (int) c[0];
         int expected = (int) c[1];
         Element img = embedStandalone(SRC_400, PNG_400x300, "max-width:" + percent + "%;height:auto");
         assertEquals("Standalone at " + percent + "%", expected, getWidth(img));
      }
   }

   @Test
   public void testSizePercentagesInTableCell() {
      // 400x300 image, table max = 150. Result = min(percent * 150, native 400).
      Object[][] cases = {
         {25, 38},   // round(150 * 0.25) = 38
         {50, 75},
         {75, 113},  // round(150 * 0.75) = 113
         {100, 150},
      };
      for (Object[] c : cases) {
         int percent = (int) c[0];
         int expected = (int) c[1];
         Element img = embedInTable(SRC_400, PNG_400x300, "max-width:" + percent + "%;height:auto");
         assertEquals("Table at " + percent + "%", expected, getWidth(img));
      }
   }

   @Test
   public void testSmallImageNotUpscaledWithSize() {
      Element img = embedStandalone(SRC_TINY, PNG_2x2, "max-width:100%;height:auto");
      assertEquals("Small image should not upscale even with 100%", 2, getWidth(img));
   }

   @Test
   public void testSizeStyleRemovedAfterProcessing() {
      Element img = embedStandalone(SRC_400, PNG_400x300, "max-width:50%;height:auto");
      assertFalse("Style attribute should be removed after embedding", img.hasAttr("style"));
   }
}
