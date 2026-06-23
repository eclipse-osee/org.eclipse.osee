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
package org.eclipse.osee.ats.ide.integration.tests.publishing.markdown;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.Base64;
import java.util.HashMap;
import org.eclipse.osee.framework.core.publishing.markdown.MarkdownConverter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests for {@link MarkdownConverter#embedImages(String, HashMap)}.
 * Verifies that images are embedded as data URIs with correct width/height attributes,
 * and that images inside table cells are constrained to a smaller max width than standalone images.
 *
 * @author Jaden W. Puckett
 */
public class EmbedImagesTest {

   /** Minimal valid 2x2 white PNG for testing. */
   private static final byte[] TEST_PNG_2x2 = new byte[] {
      (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, // PNG signature
      0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52, // IHDR chunk length + type
      0x00, 0x00, 0x00, 0x02, 0x00, 0x00, 0x00, 0x02, // width=2, height=2
      0x08, 0x02, 0x00, 0x00, 0x00, (byte) 0xFD, (byte) 0xD4, (byte) 0x9A, 0x73, // bit depth, color type, CRC
      0x00, 0x00, 0x00, 0x12, 0x49, 0x44, 0x41, 0x54, // IDAT chunk
      0x08, (byte) 0xD7, 0x63, (byte) 0xF8, (byte) 0x0F, 0x00, 0x00, 0x01, 0x01, 0x00, 0x00,
      0x18, (byte) 0xDD, (byte) 0x8D, (byte) 0xB4, 0x00, 0x00, 0x00, 0x00, // filler
      0x49, 0x45, 0x4E, 0x44, (byte) 0xAE, 0x42, 0x60, (byte) 0x82 // IEND
   };

   /** A 400x300 PNG header (only the first 24 bytes matter for dimension parsing). */
   private static final byte[] TEST_PNG_400x300;

   static {
      // Build a minimal PNG header with width=400 (0x190), height=300 (0x12C)
      TEST_PNG_400x300 = new byte[] {
         (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, // PNG signature
         0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52, // IHDR chunk length + type
         0x00, 0x00, 0x01, (byte) 0x90,                   // width = 400
         0x00, 0x00, 0x01, 0x2C,                           // height = 300
         0x08, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, // bit depth, color, etc.
         0x00, 0x00, 0x00, 0x00, 0x49, 0x45, 0x4E, 0x44, // IEND
         (byte) 0xAE, 0x42, 0x60, (byte) 0x82
      };
   }

   private static MarkdownConverter converter;

   @BeforeClass
   public static void setup() {
      converter = new MarkdownConverter();
   }

   @Test
   public void testStandaloneImageGetsDataUri() {
      String html = "<html><body><p><img src=\"resources/test_1.png\" alt=\"test\" /></p></body></html>";
      HashMap<String, String> imageMap = new HashMap<>();
      imageMap.put("resources/test_1.png", Base64.getEncoder().encodeToString(TEST_PNG_2x2));

      String result = converter.embedImages(html, imageMap);

      Document doc = Jsoup.parse(result);
      Element img = doc.selectFirst("img");
      assertTrue("Image src should be a data URI", img.attr("src").startsWith("data:image/png;base64,"));
   }

   @Test
   public void testStandaloneImageHasWidthHeight() {
      String html = "<html><body><p><img src=\"resources/big_400x300.png\" alt=\"big\" /></p></body></html>";
      HashMap<String, String> imageMap = new HashMap<>();
      imageMap.put("resources/big_400x300.png", Base64.getEncoder().encodeToString(TEST_PNG_400x300));

      String result = converter.embedImages(html, imageMap);

      Document doc = Jsoup.parse(result);
      Element img = doc.selectFirst("img");
      assertTrue("Image should have width attribute", img.hasAttr("width"));
      assertTrue("Image should have height attribute", img.hasAttr("height"));

      int width = Integer.parseInt(img.attr("width"));
      int height = Integer.parseInt(img.attr("height"));
      assertTrue("Standalone image width should be <= 468", width <= 468);
      assertTrue("Aspect ratio should be preserved", height > 0);
   }

   @Test
   public void testImageInsideTableCellHasSmallerMaxWidth() {
      String html = "<html><body><table><tr>" +
         "<td><img src=\"resources/big_400x300.png\" alt=\"big\" /></td>" +
         "</tr></table></body></html>";
      HashMap<String, String> imageMap = new HashMap<>();
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
      HashMap<String, String> imageMap = new HashMap<>();
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
      HashMap<String, String> imageMap = new HashMap<>();
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
      HashMap<String, String> imageMap = new HashMap<>();
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
      HashMap<String, String> imageMap = new HashMap<>();
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
      HashMap<String, String> imageMap = new HashMap<>();
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
