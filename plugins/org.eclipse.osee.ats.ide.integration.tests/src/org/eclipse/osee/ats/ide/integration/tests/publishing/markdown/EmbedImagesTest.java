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
 * Verifies that images are embedded as data URIs with correct width/height attributes,
 * and that images inside table cells are constrained to a smaller max width than standalone images.
 *
 * @author Jaden W. Puckett
 */
public class EmbedImagesTest {

   /** Minimal valid 2x2 PNG generated at test time. */
   private static final byte[] TEST_PNG_2x2;

   /** Valid 400x300 PNG generated at test time. */
   private static final byte[] TEST_PNG_400x300;

   static {
      try {
         TEST_PNG_2x2 = createValidPng(2, 2);
         TEST_PNG_400x300 = createValidPng(400, 300);
      } catch (IOException e) {
         throw new ExceptionInInitializerError(e);
      }
   }

   /**
    * Creates a valid PNG byte array with the given dimensions using ImageIO.
    */
   private static byte[] createValidPng(int width, int height) throws IOException {
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

   @Test
   public void testStandaloneImageGetsDataUri() {
      String html = "<html><body><p><img src=\"resources/test_1.png\" alt=\"test\" /></p></body></html>";
      Map<String, String> imageMap = new HashMap<>();
      imageMap.put("resources/test_1.png", Base64.getEncoder().encodeToString(TEST_PNG_2x2));

      String result = converter.embedImages(html, imageMap);

      Document doc = Jsoup.parse(result);
      Element img = doc.selectFirst("img");
      assertTrue("Image src should be a data URI", img.attr("src").startsWith("data:image/png;base64,"));
   }

   @Test
   public void testStandaloneImageHasWidthHeight() {
      String html = "<html><body><p><img src=\"resources/big_400x300.png\" alt=\"big\" /></p></body></html>";
      Map<String, String> imageMap = new HashMap<>();
      imageMap.put("resources/big_400x300.png", Base64.getEncoder().encodeToString(TEST_PNG_400x300));

      String result = converter.embedImages(html, imageMap);

      Document doc = Jsoup.parse(result);
      Element img = doc.selectFirst("img");
      assertTrue("Image should have width attribute", img.hasAttr("width"));
      assertTrue("Image should have height attribute", img.hasAttr("height"));

      int width = Integer.parseInt(img.attr("width"));
      int height = Integer.parseInt(img.attr("height"));
      // 400x300 is under the 468 standalone max, so dimensions should be unchanged
      assertEquals("Standalone image width should remain at original 400", 400, width);
      assertEquals("Standalone image height should remain at original 300", 300, height);
   }

   @Test
   public void testImageInsideTableCellHasSmallerMaxWidth() {
      String html = "<html><body><table><tr>" +
         "<td><img src=\"resources/big_400x300.png\" alt=\"big\" /></td>" +
         "</tr></table></body></html>";
      Map<String, String> imageMap = new HashMap<>();
      imageMap.put("resources/big_400x300.png", Base64.getEncoder().encodeToString(TEST_PNG_400x300));

      String result = converter.embedImages(html, imageMap);

      Document doc = Jsoup.parse(result);
      Element img = doc.selectFirst("td img");
      assertTrue("Table image should have width attribute", img.hasAttr("width"));

      int width = Integer.parseInt(img.attr("width"));
      assertTrue("Table image width should be <= 150", width <= 150);
   }

   @Test
   public void testSameImageDifferentContextsDifferentSizes() {
      String html = "<html><body>" +
         "<p><img src=\"resources/big_400x300.png\" alt=\"standalone\" /></p>" +
         "<table><tr><td><img src=\"resources/big_400x300.png\" alt=\"in-table\" /></td></tr></table>" +
         "</body></html>";
      Map<String, String> imageMap = new HashMap<>();
      imageMap.put("resources/big_400x300.png", Base64.getEncoder().encodeToString(TEST_PNG_400x300));

      String result = converter.embedImages(html, imageMap);

      Document doc = Jsoup.parse(result);
      Elements imgs = doc.select("img");
      assertEquals("Should have 2 images", 2, imgs.size());

      // First image is standalone (in <p>)
      Element standaloneImg = imgs.get(0);
      int standaloneWidth = Integer.parseInt(standaloneImg.attr("width"));

      // Second image is in table cell
      Element tableImg = imgs.get(1);
      int tableWidth = Integer.parseInt(tableImg.attr("width"));

      assertTrue("Standalone width should be larger than table width", standaloneWidth > tableWidth);
      assertTrue("Table image width should be <= 150", tableWidth <= 150);
   }

   @Test
   public void testSmallImageNotUpscaled() {
      // 2x2 PNG - should stay at 2x2, not upscaled
      String html = "<html><body><p><img src=\"resources/tiny.png\" alt=\"tiny\" /></p></body></html>";
      Map<String, String> imageMap = new HashMap<>();
      imageMap.put("resources/tiny.png", Base64.getEncoder().encodeToString(TEST_PNG_2x2));

      String result = converter.embedImages(html, imageMap);

      Document doc = Jsoup.parse(result);
      Element img = doc.selectFirst("img");

      int width = Integer.parseInt(img.attr("width"));
      int height = Integer.parseInt(img.attr("height"));
      assertEquals("Small image should not be upscaled (width)", 2, width);
      assertEquals("Small image should not be upscaled (height)", 2, height);
   }

   @Test
   public void testUnmatchedImageNotModified() {
      String html = "<html><body><p><img src=\"resources/unknown.png\" alt=\"x\" /></p></body></html>";
      Map<String, String> imageMap = new HashMap<>();
      // imageMap does NOT contain "resources/unknown.png"

      String result = converter.embedImages(html, imageMap);

      Document doc = Jsoup.parse(result);
      Element img = doc.selectFirst("img");
      assertEquals("Unmatched image src should remain unchanged", "resources/unknown.png", img.attr("src"));
      assertFalse("Unmatched image should not have width attribute", img.hasAttr("width"));
   }

   @Test
   public void testImageInsideThCell() {
      String html = "<html><body><table><tr>" +
         "<th><img src=\"resources/big_400x300.png\" alt=\"header-img\" /></th>" +
         "</tr></table></body></html>";
      Map<String, String> imageMap = new HashMap<>();
      imageMap.put("resources/big_400x300.png", Base64.getEncoder().encodeToString(TEST_PNG_400x300));

      String result = converter.embedImages(html, imageMap);

      Document doc = Jsoup.parse(result);
      Element img = doc.selectFirst("th img");
      int width = Integer.parseInt(img.attr("width"));
      assertTrue("Image in <th> should be constrained like <td>", width <= 150);
   }

   @Test
   public void testMultipleImagesInTable() {
      String html = "<html><body><table><tr>" +
         "<td>Cat</td>" +
         "<td><img src=\"resources/img1.png\" alt=\"img1\" /></td>" +
         "<td><img src=\"resources/img2.png\" alt=\"img2\" /></td>" +
         "<td>Dog</td>" +
         "</tr></table></body></html>";
      Map<String, String> imageMap = new HashMap<>();
      imageMap.put("resources/img1.png", Base64.getEncoder().encodeToString(TEST_PNG_400x300));
      imageMap.put("resources/img2.png", Base64.getEncoder().encodeToString(TEST_PNG_400x300));

      String result = converter.embedImages(html, imageMap);

      Document doc = Jsoup.parse(result);
      Elements imgs = doc.select("td img");
      assertEquals("Should find 2 images in table cells", 2, imgs.size());

      for (Element img : imgs) {
         assertTrue("Each table image should have data URI src", img.attr("src").startsWith("data:"));
         int width = Integer.parseInt(img.attr("width"));
         assertTrue("Each table image should be <= 150px wide", width <= 150);
      }
   }
}
