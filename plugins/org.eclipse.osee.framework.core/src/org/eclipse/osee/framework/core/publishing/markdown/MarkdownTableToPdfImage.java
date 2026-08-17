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
package org.eclipse.osee.framework.core.publishing.markdown;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.pdf.converter.PdfConverterExtension;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.eclipse.osee.framework.core.util.OseeInf;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * Renders markdown table content (or any complex markdown construct) to a PNG image. Uses the existing FlexMark +
 * OpenHTMLToPDF pipeline to render markdown → HTML → PDF, then rasterizes the PDF page to a BufferedImage via PDFBox.
 * <p>
 * This is used by the Generic Report Excel output to embed visual representations of markdown tables into Excel cells,
 * since Excel cannot natively render HTML or markdown content.
 *
 * @author David W. Miller
 */
public class MarkdownTableToPdfImage {

   private static final int RENDER_DPI = 150;
   private static final String CSS_RESOURCE = "markdownToExcelStyles";

   private final MutableDataSet options;
   private final Parser parser;
   private final HtmlRenderer htmlRenderer;

   public MarkdownTableToPdfImage() {
      this.options = MarkdownHtmlUtil.getMarkdownParserOptions();
      this.parser = Parser.builder(options).build();
      this.htmlRenderer = HtmlRenderer.builder(options).build();
   }

   /**
    * Renders a markdown fragment (typically containing a table) to a PNG image.
    *
    * @param markdownFragment the markdown text to render (e.g. a GFM table)
    * @return PNG image bytes, or an empty array if the rendered PDF has no pages
    * @throws OseeCoreException if rendering fails
    */
   public byte[] renderMarkdownToImage(String markdownFragment) {
      try {
         String html = markdownToHtml(markdownFragment);
         byte[] pdfBytes = htmlToPdf(html);
         return pdfToImage(pdfBytes);
      } catch (Exception ex) {
         throw new OseeCoreException("Failed to render markdown to image", ex);
      }
   }

   /**
    * Renders an HTML fragment to a PNG image. Useful when the caller has already converted markdown to HTML and wants to
    * render a specific block (like a table) as an image.
    *
    * @param htmlFragment the HTML content to render
    * @return PNG image bytes, or an empty array if the rendered PDF has no pages
    * @throws OseeCoreException if rendering fails
    */
   public byte[] renderHtmlToImage(String htmlFragment) {
      try {
         String fullHtml = wrapInHtmlDocument(htmlFragment);
         byte[] pdfBytes = htmlToPdf(fullHtml);
         return pdfToImage(pdfBytes);
      } catch (Exception ex) {
         throw new OseeCoreException("Failed to render HTML to image", ex);
      }
   }

   private String markdownToHtml(String markdown) {
      Node document = parser.parse(markdown);
      String bodyHtml = htmlRenderer.render(document);
      return wrapInHtmlDocument(bodyHtml);
   }

   private String wrapInHtmlDocument(String bodyHtml) {
      String css = getCssStyles();
      StringBuilder sb = new StringBuilder();
      sb.append("<!DOCTYPE html><html><head><style>");
      sb.append(css);
      sb.append("</style></head><body>");
      sb.append(bodyHtml);
      sb.append("</body></html>");
      return sb.toString();
   }

   private byte[] htmlToPdf(String html) {
      try (ByteArrayOutputStream pdfOut = new ByteArrayOutputStream()) {
         PdfConverterExtension.exportToPdf(pdfOut, html, "", options);
         return pdfOut.toByteArray();
      } catch (IOException ex) {
         throw new OseeCoreException("Failed to convert HTML to PDF for image rendering", ex);
      }
   }

   private byte[] pdfToImage(byte[] pdfBytes) throws IOException {
      try (PDDocument document = PDDocument.load(new ByteArrayInputStream(pdfBytes))) {
         if (document.getNumberOfPages() == 0) {
            return new byte[0];
         }
         PDFRenderer pdfRenderer = new PDFRenderer(document);
         BufferedImage image = pdfRenderer.renderImageWithDPI(0, RENDER_DPI);

         // Trim whitespace from the bottom of the image
         image = trimBottomWhitespace(image);

         try (ByteArrayOutputStream pngOut = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", pngOut);
            return pngOut.toByteArray();
         }
      }
   }

   /**
    * Trims empty (white) rows from the bottom of the image to avoid excessive whitespace in Excel cells.
    */
   private BufferedImage trimBottomWhitespace(BufferedImage image) {
      int width = image.getWidth();
      int height = image.getHeight();
      int lastContentRow = height - 1;

      for (int y = height - 1; y >= 0; y--) {
         boolean hasContent = false;
         for (int x = 0; x < width; x += 4) { // sample every 4th pixel for performance
            int rgb = image.getRGB(x, y) & 0x00FFFFFF;
            if (rgb != 0x00FFFFFF) { // not white
               hasContent = true;
               break;
            }
         }
         if (hasContent) {
            lastContentRow = y;
            break;
         }
      }

      int trimmedHeight = Math.min(lastContentRow + 10, height); // 10px padding
      if (trimmedHeight < height) {
         BufferedImage trimmed = new BufferedImage(width, trimmedHeight, BufferedImage.TYPE_INT_RGB);
         Graphics2D g = trimmed.createGraphics();
         g.drawImage(image, 0, 0, width, trimmedHeight, 0, 0, width, trimmedHeight, null);
         g.dispose();
         return trimmed;
      }
      return image;
   }

   private String getCssStyles() {
      return OseeInf.getResourceContents("markdown/" + CSS_RESOURCE + ".css", MarkdownTableToPdfImage.class);
   }
}
