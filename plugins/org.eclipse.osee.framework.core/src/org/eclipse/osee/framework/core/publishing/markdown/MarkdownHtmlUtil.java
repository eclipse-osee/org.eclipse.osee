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
package org.eclipse.osee.framework.core.publishing.markdown;

import com.vladsch.flexmark.ext.autolink.AutolinkExtension;
import com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.ext.toc.TocExtension;
import com.vladsch.flexmark.ext.superscript.SuperscriptExtension;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class MarkdownHtmlUtil {

   public static final MutableDataSet markdownParserOptions =
      new MutableDataSet().set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create(), TaskListExtension.create(),
         TocExtension.create(), AutolinkExtension.create(), SuperscriptExtension.create()));

   public static final Set<String> SUPPORTED_IMAGE_EXTENSIONS =
      Set.of("png", "jpg", "jpeg", "gif", "bmp", "webp", "svg");

   private static boolean isImageFile(String fileName) {
      String lowerName = fileName.toLowerCase();
      return SUPPORTED_IMAGE_EXTENSIONS.stream().anyMatch(lowerName::endsWith);
   }

   public static MarkdownZip processMarkdownZip(ZipInputStream zipInputStream) throws IOException {
      HashMap<String, String> imageContentMap = new HashMap<>();
      Node markdownDocument = null;

      ZipEntry entry;

      while ((entry = zipInputStream.getNextEntry()) != null) {
         if (entry.getName().equals("document.md")) {
            markdownDocument = readMarkdown(zipInputStream);
         } else if (!entry.isDirectory() && isImageFile(entry.getName())) {
            String imageName = entry.getName();
            String imageContent = readImageContent(zipInputStream);
            imageContentMap.put(imageName, imageContent);
         }
         zipInputStream.closeEntry();
      }

      return new MarkdownZip(imageContentMap, markdownDocument);
   }

   public static HtmlZip processHtmlZip(ZipInputStream zipInputStream) throws IOException {
      HashMap<String, String> imageContentMap = new HashMap<>();
      Document htmlDocument = null;

      ZipEntry entry;

      while ((entry = zipInputStream.getNextEntry()) != null) {
         if (entry.getName().equals("document.html")) {
            htmlDocument = readHtml(zipInputStream);
         } else if (!entry.isDirectory() && isImageFile(entry.getName())) {
            String imageName = entry.getName();
            String imageContent = readImageContent(zipInputStream);
            imageContentMap.put(imageName, imageContent);
         }
         zipInputStream.closeEntry();
      }

      return new HtmlZip(imageContentMap, htmlDocument);
   }

   public static Node readMarkdown(InputStream inputStream) {
      Parser parser = Parser.builder(markdownParserOptions).build();

      try {
         String mdContent = new String(readInputStream(inputStream), StandardCharsets.UTF_8);
         return parser.parse(mdContent);
      } catch (Exception e) {
         throw new OseeCoreException("An unexpected error occurred while parsing the markdown: " + e.getMessage(), e);
      }
   }

   public static Document readHtml(InputStream inputStream) {
      try {
         String htmlContent = new String(readInputStream(inputStream), StandardCharsets.UTF_8);
         return Jsoup.parse(htmlContent);
      } catch (Exception e) {
         throw new OseeCoreException("An unexpected error occurred while parsing the html: " + e.getMessage(), e);
      }
   }

   public static String readImageContent(InputStream inputStream) {
      // Read the image content and convert it to a Base64 string
      try {
         return Base64.getEncoder().encodeToString(readInputStream(inputStream));
      } catch (IOException e) {
         throw new OseeCoreException("An unexpected error occurred while reading image content: " + e.getMessage(), e);
      }
   }

   public static byte[] readInputStream(InputStream inputStream) throws IOException {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      byte[] buffer = new byte[8192];
      int bytesRead;

      try {
         while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
         }
         return byteArrayOutputStream.toByteArray();
      } catch (IOException e) {
         throw new IOException("Error reading input stream: " + e.getMessage(), e);
      }
   }

   public static MutableDataSet getMarkdownParserOptions() {
      return MarkdownHtmlUtil.markdownParserOptions;
   }

}
