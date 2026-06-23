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
package org.eclipse.osee.framework.core.publishing.markdown;

import com.vladsch.flexmark.formatter.Formatter;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.pdf.converter.PdfConverterExtension;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.util.logging.Level;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.eclipse.osee.framework.core.publishing.PublishingOutputFormatter;
import org.eclipse.osee.framework.core.util.OseeInf;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Entities;

/**
 * @author Jaden W. Puckett
 */

public class MarkdownConverter {

   /**
    * Maximum image width (in pixels) when rendered inside a table cell.
    */
   private static final int MAX_IMAGE_WIDTH_IN_TABLE = 150;

   /**
    * Maximum image width (in pixels) when rendered as a standalone image (~6.5in at 72dpi).
    */
   private static final int MAX_IMAGE_WIDTH_STANDALONE = 468;

   private MutableDataSet options;

   public MarkdownConverter() {
      this.options = MarkdownHtmlUtil.getMarkdownParserOptions();
   }

   public MarkdownConverter(MutableDataSet options) {
      this.options = options;
   }

   public void setOptions(MutableDataSet options) {
      this.options = options;
   }

   public ByteArrayInputStream convertMarkdownZipToHtml(ZipInputStream zipInputStream) {

      ByteArrayOutputStream htmlZipOutputStream = new ByteArrayOutputStream();

      try (ZipOutputStream zipOutputStream = new ZipOutputStream(htmlZipOutputStream)) {
         MarkdownZip mdZip = MarkdownHtmlUtil.processMarkdownZip(zipInputStream);

         Node markdownDocument = mdZip.getMarkdownDocument();

         // Convert the markdown document to HTML and add the HTML document to the zip
         ZipEntry htmlEntry = new ZipEntry("document.html");
         zipOutputStream.putNextEntry(htmlEntry);
         zipOutputStream.write(convertToHtmlBytes(markdownDocument));
         zipOutputStream.closeEntry();

         // Add images to the zip
         HashMap<String, String> imageContentMap = mdZip.getImageContentMap();
         for (String imageName : imageContentMap.keySet()) {
            ZipEntry imageEntry = new ZipEntry(imageName);
            zipOutputStream.putNextEntry(imageEntry);
            // Decode Base64 to get the original image bytes
            zipOutputStream.write(Base64.getDecoder().decode(imageContentMap.get(imageName)));
            zipOutputStream.closeEntry();
         }
      } catch (Exception e) {
         OseeCoreException.wrapAndThrow(e);
      }

      // Return the zip file as a ByteArrayInputStream
      return new ByteArrayInputStream(htmlZipOutputStream.toByteArray());
   }

   public ByteArrayInputStream convertMarkdownZipToPdf(ZipInputStream zipInputStream,
      PublishingOutputFormatter pubOutputFormatter) {
      try {
         // Process the zip input stream to extract the markdown document
         MarkdownZip mdZip = MarkdownHtmlUtil.processMarkdownZip(zipInputStream);
         Node markdownDocument = mdZip.getMarkdownDocument();

         // Convert the markdown document to PDF bytes
         byte[] pdfBytes =
            convertToPdfBytes(markdownDocument, mdZip.getImageContentMap(), pubOutputFormatter.getCollectedCss());

         // Return the PDF as a ByteArrayInputStream
         return new ByteArrayInputStream(pdfBytes);

      } catch (IOException e) {
         // Handle IOException specifically
         throw new OseeCoreException("Error processing markdown zip to PDF", e);
      } catch (Exception e) {
         // Handle any other exceptions
         throw new OseeCoreException("Unexpected error occurred", e);
      }
   }

   public ByteArrayInputStream convertToHtmlStream(ByteArrayInputStream markdownInputStream) {
      // Convert Markdown ByteArrayInputStream to String
      String markdownContent = new String(markdownInputStream.readAllBytes(), StandardCharsets.UTF_8);

      // Convert Markdown to HTML
      String htmlContent = convertToHtmlStringWithStyle(markdownContent);

      // Convert HTML String to ByteArrayInputStream
      return new ByteArrayInputStream(htmlContent.getBytes(StandardCharsets.UTF_8));
   }

   public String convertToHtmlStringWithStyle(String markdownContent) {
      Node document = parseMarkdownAsNode(markdownContent);

      return PdfConverterExtension.embedCss(renderMarkdownDocumentToHtmlString(document),
         getCssStyles("markdownToHtmlStyles"));
   }

   public String convertToHtmlStringWithStyle(Node document) {
      return PdfConverterExtension.embedCss(renderMarkdownDocumentToHtmlString(document),
         getCssStyles("markdownToHtmlStyles"));
   }

   public String renderMarkdownDocumentToHtmlString(Node document) {
      HtmlRenderer renderer = HtmlRenderer.builder(this.options).build();
      return renderer.render(document);
   }

   public String renderMarkdownDocumentToMarkdownString(Node document) {
      Formatter formatter = Formatter.builder(this.options).build();
      return formatter.render(document);
   }

   public byte[] convertToHtmlBytes(Node markdownDocument) {

      String htmlWithCss = convertToHtmlStringWithStyle(markdownDocument);
      return htmlWithCss.getBytes(StandardCharsets.UTF_8);
   }

   public byte[] convertToPdfBytes(Node markdownDocument, HashMap<String, String> imageContentMap, String collectedCss)
      throws IOException {
      HtmlRenderer renderer = HtmlRenderer.builder(this.options).build();

      String cssString = getCssStyles("markdownToPdfStyles") + "\n\n" + collectedCss;

      String htmlWithCss = PdfConverterExtension.embedCss(renderer.render(markdownDocument), cssString);

      htmlWithCss = embedImages(htmlWithCss, imageContentMap);

      // Render PDF to memory
      byte[] initialPdfBytes;
      try (ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream()) {
         PdfConverterExtension.exportToPdf(pdfOutputStream, htmlWithCss, "", options);
         initialPdfBytes = pdfOutputStream.toByteArray();
      }

      // Inspect and trim PDF
      try (PDDocument document = PDDocument.load(new ByteArrayInputStream(initialPdfBytes))) {
         int numPages = document.getNumberOfPages();

         trimPdf(document, numPages);

         try (ByteArrayOutputStream finalOutputStream = new ByteArrayOutputStream()) {
            document.save(finalOutputStream);
            return finalOutputStream.toByteArray();
         }
      }
   }

   private void trimPdf(PDDocument document, int numPages) throws IOException {
      // Check first page
      if (numPages > 0) {
         PDFTextStripper stripper = new PDFTextStripper();
         stripper.setStartPage(1);
         stripper.setEndPage(1);
         String firstPageText = stripper.getText(document).trim();
         if (firstPageText.isEmpty()) {
            document.removePage(0); // Remove first page
            numPages--; // Adjust page count
         }
      }

      // Check last page (after possibly removing first)
      if (numPages > 0) {
         PDFTextStripper stripper = new PDFTextStripper();
         stripper.setStartPage(numPages);
         stripper.setEndPage(numPages);
         String lastPageText = stripper.getText(document).trim();
         if (lastPageText.isEmpty()) {
            document.removePage(numPages - 1); // 0-based index
         }
      }
   }

   /**
    * Embeds images as base64 data URIs with explicit width/height attributes.
    * The CSS table-layout:fixed enables images to render in table cells, but without
    * explicit dimensions they render at full intrinsic size and overflow. This method
    * caps image dimensions to prevent overflow.
    */
   public String embedImages(String html, HashMap<String, String> imageContentMap) {
      org.jsoup.nodes.Document doc = Jsoup.parse(html);
      doc.outputSettings().syntax(org.jsoup.nodes.Document.OutputSettings.Syntax.xml);
      doc.outputSettings().escapeMode(Entities.EscapeMode.xhtml);

      for (org.jsoup.nodes.Element img : doc.select("img[src]")) {
         String src = img.attr("src");
         String base64 = imageContentMap.get(src);
         if (base64 == null) {
            continue;
         }

         // Build data URI
         String dataUri;
         byte[] imageBytes;
         try {
            if (base64.startsWith("data:")) {
               dataUri = base64;
               int commaIdx = base64.indexOf(',');
               imageBytes = (commaIdx >= 0) ? Base64.getDecoder().decode(base64.substring(commaIdx + 1)) : new byte[0];
            } else {
               String mediaType = guessMediaType(src);
               dataUri = "data:" + mediaType + ";base64," + base64;
               imageBytes = Base64.getDecoder().decode(base64);
            }
         } catch (IllegalArgumentException e) {
            // Malformed base64 — skip this image
            OseeLog.log(MarkdownConverter.class, Level.WARNING,
               "Skipping image with malformed base64 data: " + src, e);
            continue;
         }

         img.attr("src", dataUri);

         // Set explicit dimensions scaled to context
         int[] dimensions = getImageDimensions(imageBytes);
         if (dimensions != null && dimensions[0] > 0 && dimensions[1] > 0) {
            boolean insideTableCell = img.closest("td") != null || img.closest("th") != null;
            int maxWidth = insideTableCell ? MAX_IMAGE_WIDTH_IN_TABLE : MAX_IMAGE_WIDTH_STANDALONE;
            int renderWidth = Math.min(dimensions[0], maxWidth);
            int renderHeight = (int) Math.round((double) dimensions[1] * renderWidth / dimensions[0]);
            img.attr("width", String.valueOf(renderWidth));
            img.attr("height", String.valueOf(renderHeight));
         }
      }

      return doc.html();
   }

   /**
    * Reads image dimensions (width, height) from raw image bytes using javax.imageio.
    * Only parses file headers — does not fully decode the image into memory.
    * Returns null if dimensions cannot be determined.
    */
   private int[] getImageDimensions(byte[] imageBytes) {
      if (imageBytes == null || imageBytes.length == 0) {
         return null;
      }
      try (ImageInputStream iis = ImageIO.createImageInputStream(new ByteArrayInputStream(imageBytes))) {
         if (iis == null) {
            return null;
         }
         Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
         if (readers.hasNext()) {
            ImageReader reader = readers.next();
            try {
               reader.setInput(iis);
               int width = reader.getWidth(0);
               int height = reader.getHeight(0);
               return new int[]{width, height};
            } finally {
               reader.dispose();
            }
         }
      } catch (IOException e) {
         // If we can't read dimensions, image will be embedded without explicit sizing
         OseeLog.log(MarkdownConverter.class, Level.FINE,
            "Unable to read image dimensions, embedding without explicit size", e);
      }
      return null;
   }

   private String guessMediaType(String filename) {
      int dotIndex = filename.lastIndexOf('.');
      if (dotIndex != -1 && dotIndex < filename.length() - 1) {
         String ext = filename.substring(dotIndex + 1).toLowerCase();
         return MarkdownHtmlUtil.EXTENSION_TO_MEDIA_TYPE.getOrDefault(ext, "application/octet-stream");
      }
      return "application/octet-stream";
   }

   private String getCssStyles(String file) {
      return OseeInf.getResourceContents("markdown/" + file + ".css", MarkdownConverter.class);
   }

   public Node parseMarkdownAsNode(String markdownContent) {
      Parser parser = Parser.builder(this.options).build();

      return parser.parse(markdownContent);
   }

   public Document parseMarkdownAsDocument(String markdownContent) {
      Parser parser = Parser.builder(this.options).build();

      return parser.parse(markdownContent);
   }
}
