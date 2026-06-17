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
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.eclipse.osee.framework.core.publishing.PublishingOutputFormatter;
import org.eclipse.osee.framework.core.util.OseeInf;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Jaden W. Puckett
 */

public class MarkdownConverter {

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

   public String embedImages(String html, HashMap<String, String> imageContentMap) {
      for (Map.Entry<String, String> entry : imageContentMap.entrySet()) {
         String imageName = entry.getKey();
         String base64 = entry.getValue();

         String dataUri;
         byte[] imageBytes;
         if (base64.startsWith("data:")) {
            dataUri = base64;
            int commaIdx = base64.indexOf(',');
            imageBytes = (commaIdx >= 0) ? Base64.getDecoder().decode(base64.substring(commaIdx + 1)) : new byte[0];
         } else {
            String mediaType = guessMediaType(imageName);
            dataUri = "data:" + mediaType + ";base64," + base64;
            imageBytes = Base64.getDecoder().decode(base64);
         }

         // Read image dimensions to scale the image to fit within a table cell.
         // OpenHTMLToPDF requires explicit width/height attributes to render data URI images
         // in table cells, but percentage-based max-width does not work in that context.
         // We scale to a max of 150px wide (reasonable for a table cell) while preserving aspect ratio.
         int[] dimensions = getImageDimensions(imageBytes);

         String srcReplacement;
         if (dimensions != null && dimensions[0] > 0 && dimensions[1] > 0) {
            int maxWidth = 150;
            int renderWidth = dimensions[0];
            int renderHeight = dimensions[1];
            if (renderWidth > maxWidth) {
               renderHeight = (int) Math.round((double) renderHeight * maxWidth / renderWidth);
               renderWidth = maxWidth;
            }
            srcReplacement = "src=\"" + dataUri + "\" width=\"" + renderWidth + "\" height=\"" + renderHeight + "\"";
         } else {
            srcReplacement = "src=\"" + dataUri + "\"";
         }

         html = html.replace("src=\"" + imageName + "\"", srcReplacement);
      }

      return html;
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
         // If we can't read dimensions, return null and the image will be embedded without sizing
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
