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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.eclipse.osee.framework.core.util.OseeInf;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Jaden W. Puckett
 */

public class MarkdownConverter {

   private MutableDataSet options;

   public MarkdownConverter() {
      setDefaultOptions();
   }

   public MarkdownConverter(MutableDataSet options) {
      this.options = options;
   }

   public void setOptions(MutableDataSet options) {
      this.options = options;
   }

   private void setDefaultOptions() {
      options = new MutableDataSet();
      options.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create(), TaskListExtension.create(),
         TocExtension.create(), AutolinkExtension.create()));
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

   public ByteArrayInputStream convertToHtmlStream(ByteArrayInputStream markdownInputStream) {
      // Convert Markdown ByteArrayInputStream to String
      String markdownContent = new String(markdownInputStream.readAllBytes(), StandardCharsets.UTF_8);

      // Convert Markdown to HTML
      String htmlContent = convertToHtmlString(markdownContent);

      // Convert HTML String to ByteArrayInputStream
      return new ByteArrayInputStream(htmlContent.getBytes(StandardCharsets.UTF_8));
   }

   public String convertToHtmlString(String markdownContent) {
      Parser parser = Parser.builder(this.options).build();
      HtmlRenderer renderer = HtmlRenderer.builder(this.options).build();
      Node document = parser.parse(markdownContent);
      return "<html><head><meta charset=\"UTF-8\">" + getCssStyles() + "</head><body>\n" + renderer.render(
         document) + "</body></html>";
   }

   public byte[] convertToHtmlBytes(Node markdownDocument) {
      HtmlRenderer renderer = HtmlRenderer.builder(this.options).build();
      String htmlString = "<html><head><meta charset=\"UTF-8\">" + getCssStyles() + "</head><body>\n" + renderer.render(
         markdownDocument) + "</body></html>";
      return htmlString.getBytes(StandardCharsets.UTF_8);
   }

   private String getCssStyles() {
      return "<style>\n" + OseeInf.getResourceContents("markdownToHtmlStyles.css",
         MarkdownConverter.class) + "\n</style>";
   }

}
