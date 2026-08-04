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
package org.eclipse.osee.orcs.rest.internal.writers;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.publishing.markdown.MarkdownHtmlUtil;
import org.eclipse.osee.framework.core.util.OseeInf;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * HTML table report writer for the Generic Report system. Produces a self-contained HTML file with:
 * <ul>
 * <li>Report data rendered as an HTML table</li>
 * <li>Markdown content (including tables) rendered inline as HTML via FlexMark</li>
 * <li>Images from &lt;image-link&gt; tags embedded as Base64 data URIs</li>
 * </ul>
 *
 * @author David W. Miller
 */
public final class PublishTemplateReportHtml implements StreamingOutput {
   private final OrcsApi orcsApi;
   private final BranchId branch;
   private final ArtifactId view;
   private final ArtifactId reportTemplateArt;
   private final GenericReportBuilder report;
   private final XResultData results;
   private final MutableDataSet parserOptions;

   public PublishTemplateReportHtml(OrcsApi orcsApi, BranchId branch, ArtifactId view, ArtifactId templateArt) {
      this.orcsApi = orcsApi;
      this.branch = branch;
      this.view = view;
      this.reportTemplateArt = templateArt;
      this.results = new XResultData();
      this.report = new GenericReportBuilder(branch, view, orcsApi);
      this.parserOptions = MarkdownHtmlUtil.getMarkdownParserOptions();
   }

   @Override
   public void write(OutputStream output) {
      try {
         Writer writer = new OutputStreamWriter(output, StandardCharsets.UTF_8);
         if (reportTemplateArt.isValid()) {
            writeReport(writer);
         } else {
            results.errorf("Invalid Template Report artifact provided: %s", reportTemplateArt);
         }
         writer.flush();
      } catch (IOException ex) {
         throw new WebApplicationException(ex);
      }
   }

   private void writeReport(Writer writer) throws IOException {
      TemplateParser parser = new TemplateParser(orcsApi, branch, view, reportTemplateArt, results);
      parser.parseTemplateData(report);

      String reportTitle = parser.getTemplateArtifact().getName();

      if (!results.isErrors()) {
         List<Object[]> data = new ArrayList<>();
         report.getDataRowsFromQuery(data);
         writeHtmlDocument(writer, reportTitle, data);
      } else {
         writeErrorPage(writer, reportTitle);
      }
   }

   private void writeHtmlDocument(Writer writer, String title, List<Object[]> data) throws IOException {
      writer.write("<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n");
      writer.write("<meta charset=\"UTF-8\">\n");
      writer.write("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
      writer.write("<title>");
      writer.write(escapeHtml(title));
      writer.write("</title>\n");
      writeStyles(writer);
      writer.write("</head>\n<body>\n");
      writer.write("<h1>");
      writer.write(escapeHtml(title));
      writer.write("</h1>\n");

      writer.write("<table class=\"report-table\">\n");

      int rowIndex = 0;
      for (Object[] rowData : data) {
         if (rowIndex == 0) {
            // Top row — level names
            writeTopRow(writer, rowData);
         } else if (rowIndex == 1) {
            // Header row — column names
            writeHeaderRow(writer, rowData);
            writer.write("</thead>\n<tbody>\n");
         } else {
            // Data rows
            writeDataRow(writer, rowData);
         }
         rowIndex++;
      }

      writer.write("</tbody>\n</table>\n");
      writer.write("</body>\n</html>\n");
   }

   private void writeTopRow(Writer writer, Object[] rowData) throws IOException {
      writer.write("<thead>\n<tr class=\"top-row\">\n");
      for (Object cell : rowData) {
         writer.write("<th class=\"level-header\">");
         if (cell != null) {
            writer.write(escapeHtml(cell.toString()));
         }
         writer.write("</th>\n");
      }
      writer.write("</tr>\n");
   }

   private void writeHeaderRow(Writer writer, Object[] rowData) throws IOException {
      writer.write("<tr class=\"header-row\">\n");
      for (Object cell : rowData) {
         writer.write("<th>");
         if (cell != null) {
            writer.write(escapeHtml(cell.toString()));
         }
         writer.write("</th>\n");
      }
      writer.write("</tr>\n");
   }

   private void writeDataRow(Writer writer, Object[] rowData) throws IOException {
      writer.write("<tr>\n");
      for (Object cell : rowData) {
         writer.write("<td>");
         if (cell != null) {
            String cellValue = cell.toString();
            if (containsMarkdown(cellValue)) {
               writer.write(renderMarkdownToHtml(cellValue));
            } else {
               writer.write(escapeHtml(cellValue));
            }
         }
         writer.write("</td>\n");
      }
      writer.write("</tr>\n");
   }

   private boolean containsMarkdown(String value) {
      return MarkdownHtmlUtil.containsMarkdownContent(value);
   }

   private String renderMarkdownToHtml(String markdown) {
      // Resolve image-link tags to inline base64 images before parsing
      String resolved = resolveImageLinks(markdown);

      Parser mdParser = Parser.builder(parserOptions).build();
      Node document = mdParser.parse(resolved);
      HtmlRenderer renderer = HtmlRenderer.builder(parserOptions).build();
      return renderer.render(document);
   }

   private String resolveImageLinks(String content) {
      Matcher matcher = MarkdownHtmlUtil.IMAGE_LINK_PATTERN.matcher(content);
      StringBuffer sb = new StringBuffer();
      while (matcher.find()) {
         String artifactIdStr = matcher.group(2);
         String replacement = buildBase64ImageTag(artifactIdStr);
         matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
      }
      matcher.appendTail(sb);
      return sb.toString();
   }

   private String buildBase64ImageTag(String artifactIdStr) {
      try {
         ArtifactId artId = ArtifactId.valueOf(artifactIdStr);
         ArtifactReadable imageArt = orcsApi.getQueryFactory().fromBranch(branch).andId(artId).asArtifactOrSentinel();

         if (imageArt.isInvalid()) {
            return "[Image not found: " + artifactIdStr + "]";
         }

         byte[] imageBytes;
         try (InputStream inputStream = imageArt.getSoleAttributeValue(CoreAttributeTypes.NativeContent)) {
            imageBytes = Lib.inputStreamToBytes(inputStream);
         }

         String extension = imageArt.getSoleAttributeAsString(CoreAttributeTypes.Extension, "png");
         String mimeType = extensionToMimeType(extension);
         String base64 = Base64.getEncoder().encodeToString(imageBytes);
         String name = imageArt.getName();

         return "<img src=\"data:" + mimeType + ";base64," + base64 + "\" alt=\"" + escapeHtml(
            name) + "\" style=\"max-width:100%;height:auto;\" />";
      } catch (IOException ex) {
         return "[Image error: " + artifactIdStr + "]";
      }
   }

   private String extensionToMimeType(String extension) {
      return MarkdownHtmlUtil.EXTENSION_TO_MEDIA_TYPE.getOrDefault(extension.toLowerCase(Locale.US), "image/png");
   }

   private void writeErrorPage(Writer writer, String title) throws IOException {
      writer.write("<!DOCTYPE html>\n<html lang=\"en\"><head><title>Report Error</title></head>\n<body>\n");
      writer.write("<h1>Error generating report: ");
      writer.write(escapeHtml(title));
      writer.write("</h1>\n<ul>\n");
      for (String result : results.getResults()) {
         writer.write("<li>");
         writer.write(escapeHtml(result));
         writer.write("</li>\n");
      }
      writer.write("</ul>\n</body>\n</html>\n");
   }

   private static final String FALLBACK_CSS =
      "body { font-family: Calibri, Arial, sans-serif; margin: 20px; }\n"
         + ".report-table { border-collapse: collapse; width: 100%; }\n"
         + ".report-table th, .report-table td { border: 1px solid #999; padding: 6px 10px; }\n";

   private static final String REPORT_CSS = loadReportCss();

   private static String loadReportCss() {
      try {
         return OseeInf.getResourceContents("report/reportStyles.css", PublishTemplateReportHtml.class);
      } catch (Exception ex) {
         return null;
      }
   }

   private void writeStyles(Writer writer) throws IOException {
      writer.write("<style>\n");
      if (REPORT_CSS != null) {
         writer.write(REPORT_CSS);
      } else {
         results.warning("Could not load OSEE-INF/report/reportStyles.css from bundle; using fallback CSS");
         writer.write(FALLBACK_CSS);
      }
      writer.write("\n</style>\n");
   }

   private static String escapeHtml(String text) {
      if (text == null) {
         return "";
      }
      return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
   }
}
