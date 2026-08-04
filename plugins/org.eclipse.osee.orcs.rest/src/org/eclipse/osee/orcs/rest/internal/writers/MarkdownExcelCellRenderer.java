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

import com.vladsch.flexmark.ext.tables.TableBlock;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.publishing.markdown.MarkdownHtmlUtil;
import org.eclipse.osee.framework.core.publishing.markdown.MarkdownTableRenderer;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * Renders markdown cell content into Excel cells using a hybrid approach:
 * <ul>
 * <li>Plain text, bold, italic, headings, lists → XSSFRichTextString with inline font formatting</li>
 * <li>Tables, complex constructs → rendered to PNG image and embedded in the cell</li>
 * <li>&lt;image-link&gt; tags → resolved to artifact binary data and embedded as images</li>
 * <li>Fenced code blocks → monospace font runs in rich text</li>
 * </ul>
 *
 * @author David W. Miller
 */
public class MarkdownExcelCellRenderer {

   private static final Pattern IMAGE_LINK_PATTERN = MarkdownHtmlUtil.IMAGE_LINK_PATTERN;
   private static final int IMAGE_MAX_WIDTH_PX = 400;
   private static final int IMAGE_MAX_HEIGHT_PX = 300;
   private static final double EMU_PER_PIXEL = 9525.0;

   private final OrcsApi orcsApi;
   private final BranchId branch;
   private final MutableDataSet parserOptions;
   private final MarkdownTableRenderer tableRenderer;

   public MarkdownExcelCellRenderer(OrcsApi orcsApi, BranchId branch) {
      this.orcsApi = orcsApi;
      this.branch = branch;
      this.parserOptions = MarkdownHtmlUtil.getMarkdownParserOptions();
      this.tableRenderer = new MarkdownTableRenderer();
   }

   /**
    * Represents the result of rendering a markdown cell. Contains both the rich text string for the cell value and any
    * images that need to be anchored to the cell.
    */
   public static class CellRenderResult {
      private final XSSFRichTextString richText;
      private final List<ImageEmbed> images;
      private final int requiredHeightInPoints;

      public CellRenderResult(XSSFRichTextString richText, List<ImageEmbed> images, int requiredHeightInPoints) {
         this.richText = richText;
         this.images = images;
         this.requiredHeightInPoints = requiredHeightInPoints;
      }

      public XSSFRichTextString getRichText() {
         return richText;
      }

      public List<ImageEmbed> getImages() {
         return images;
      }

      public int getRequiredHeightInPoints() {
         return requiredHeightInPoints;
      }
   }

   /**
    * Represents an image to be embedded in a cell via the drawing layer.
    */
   public static class ImageEmbed {
      private final byte[] imageBytes;
      private final int pictureType;
      private final int widthPx;
      private final int heightPx;

      public ImageEmbed(byte[] imageBytes, int pictureType, int widthPx, int heightPx) {
         this.imageBytes = imageBytes;
         this.pictureType = pictureType;
         this.widthPx = widthPx;
         this.heightPx = heightPx;
      }

      public byte[] getImageBytes() {
         return imageBytes;
      }

      public int getPictureType() {
         return pictureType;
      }

      public int getWidthPx() {
         return widthPx;
      }

      public int getHeightPx() {
         return heightPx;
      }
   }

   /**
    * Determines whether a cell value contains markdown content that benefits from rich rendering (tables, images, or
    * formatted text). Delegates to the shared utility in {@link MarkdownHtmlUtil}.
    */
   public boolean containsMarkdownContent(String cellValue) {
      return MarkdownHtmlUtil.containsMarkdownContent(cellValue);
   }

   /**
    * Renders markdown content to a CellRenderResult suitable for writing to an Excel cell.
    * <p>
    * <b>Deprecated:</b> This overload creates new fonts on every call. For batch rendering, use
    * {@link #renderCell(String, Workbook, WorkbookFonts)} with a pre-created {@link WorkbookFonts} instance to avoid
    * exceeding POI's per-workbook font limit (~32,767).
    *
    * @param cellValue the raw markdown content from the attribute
    * @param workbook the POI workbook (needed for font/image registration)
    * @return render result with rich text and optional embedded images
    * @deprecated Use {@link #renderCell(String, Workbook, WorkbookFonts)} instead
    */
   @Deprecated
   public CellRenderResult renderCell(String cellValue, Workbook workbook) {
      if (cellValue == null || cellValue.isEmpty()) {
         return new CellRenderResult(new XSSFRichTextString(""), new ArrayList<>(), 15);
      }

      // Parse the markdown content into AST
      Parser parser = Parser.builder(parserOptions).build();
      Node document = parser.parse(cellValue);

      // Classify blocks into renderable segments
      List<ContentSegment> segments = classifyContent(document, cellValue);

      // Render segments into rich text + images
      return buildCellContent(segments, workbook);
   }

   /**
    * Writes the cell render result into the actual Excel cell, including embedding images via the drawing layer. Images
    * are anchored to the target cell using a one-cell anchor so they maintain their intrinsic aspect ratio and move/
    * resize with the cell.
    */
   public void applyToCell(CellRenderResult result, Cell cell, Sheet sheet, int rowIndex, int colIndex) {
      cell.setCellValue(result.getRichText());

      if (!result.getImages().isEmpty()) {
         Drawing<?> drawing = sheet.createDrawingPatriarch();
         CreationHelper helper = sheet.getWorkbook().getCreationHelper();

         int totalImageHeightPx = 0;

         // Ensure the column is wide enough for images before calling resize()
         // POI's resize() reads column width to calculate target size — if width is too small it throws.
         int currentColWidth = sheet.getColumnWidth(colIndex);
         int minColWidthForImages = 50 * 256; // 50 characters (~400px)
         if (currentColWidth < minColWidthForImages) {
            sheet.setColumnWidth(colIndex, minColWidthForImages);
         }

         // Pre-size the row so resize() has enough vertical space
         Row row = sheet.getRow(rowIndex);
         if (row == null) {
            row = sheet.createRow(rowIndex);
         }

         for (ImageEmbed image : result.getImages()) {
            int pictureIdx = sheet.getWorkbook().addPicture(image.getImageBytes(), image.getPictureType());

            double scaleFactor = 1.0;
            if (image.getWidthPx() > IMAGE_MAX_WIDTH_PX) {
               scaleFactor = (double) IMAGE_MAX_WIDTH_PX / image.getWidthPx();
            }
            int scaledHeight = (int) (image.getHeightPx() * scaleFactor);

            // Ensure row height can fit this image before creating the picture
            int requiredHeightPts = (totalImageHeightPx + scaledHeight + 10);
            short requiredHeightTwips = (short) (Math.max(requiredHeightPts, 20) * 20);
            if (row.getHeight() < requiredHeightTwips) {
               row.setHeight(requiredHeightTwips);
            }

            // Create anchor starting at this cell with no offset tricks — let resize handle sizing
            ClientAnchor anchor = helper.createClientAnchor();
            anchor.setCol1(colIndex);
            anchor.setRow1(rowIndex);
            anchor.setDx1(0);
            anchor.setDy1((int) (totalImageHeightPx * EMU_PER_PIXEL));
            anchor.setCol2(colIndex + 1);
            anchor.setRow2(rowIndex + 1);
            anchor.setDx2(0);
            anchor.setDy2(0);
            anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);

            org.apache.poi.ss.usermodel.Picture picture = drawing.createPicture(anchor, pictureIdx);

            // resize() can throw if the cell geometry is still too small (edge case with very narrow
            // columns or merged cells). Fall back to manual sizing if it fails.
            try {
               picture.resize(scaleFactor);
            } catch (IllegalArgumentException ex) {
               // Fallback: manually set the anchor to approximate dimensions
               int scaledWidth = (int) (image.getWidthPx() * scaleFactor);
               anchor.setCol2(colIndex);
               anchor.setDx2((int) (scaledWidth * EMU_PER_PIXEL));
               anchor.setRow2(rowIndex);
               anchor.setDy2((int) ((totalImageHeightPx + scaledHeight) * EMU_PER_PIXEL));
            }

            totalImageHeightPx += scaledHeight + 5;
         }

         // Final row height adjustment
         int finalHeightPts = (int) (totalImageHeightPx * 0.75) + 5;
         short finalHeightTwips = (short) (Math.max(finalHeightPts, 20) * 20);
         if (row.getHeight() < finalHeightTwips) {
            row.setHeight(finalHeightTwips);
         }
      } else {
         // Set row height for text-only content
         Row row = sheet.getRow(rowIndex);
         if (row != null) {
            short currentHeight = row.getHeight();
            short requiredHeight = (short) (result.getRequiredHeightInPoints() * 20);
            if (requiredHeight > currentHeight) {
               row.setHeight(requiredHeight);
            }
         }
      }
   }

   // ---- Internal content classification ----

   private enum SegmentType {
      TEXT, TABLE, IMAGE_LINK, CODE_BLOCK
   }

   private static class ContentSegment {
      final SegmentType type;
      final String content;

      ContentSegment(SegmentType type, String content) {
         this.type = type;
         this.content = content;
      }
   }

   /**
    * Walks the FlexMark AST and classifies each top-level block into a segment type.
    */
   private List<ContentSegment> classifyContent(Node document, String rawContent) {
      List<ContentSegment> segments = new ArrayList<>();

      // First, handle image-links by splitting around them
      String processedContent = rawContent;
      List<ImageLinkRef> imageLinks = extractImageLinks(processedContent);

      if (imageLinks.isEmpty() && !hasTableBlock(document)) {
         // Simple text content - just treat the whole thing as text
         segments.add(new ContentSegment(SegmentType.TEXT, rawContent));
         return segments;
      }

      // Walk AST nodes
      Node child = document.getFirstChild();
      while (child != null) {
         if (child instanceof TableBlock) {
            String tableText = child.getChars().toString();
            segments.add(new ContentSegment(SegmentType.TABLE, tableText));
         } else if (child.getClass().getSimpleName().equals("FencedCodeBlock") || child.getClass().getSimpleName().equals(
            "IndentedCodeBlock")) {
            String codeText = child.getChars().toString();
            segments.add(new ContentSegment(SegmentType.CODE_BLOCK, codeText));
         } else {
            String blockText = child.getChars().toString();
            // Check if this block contains image-link tags
            Matcher matcher = IMAGE_LINK_PATTERN.matcher(blockText);
            if (matcher.find()) {
               // Split around image links
               splitTextAndImageLinks(blockText, segments);
            } else {
               segments.add(new ContentSegment(SegmentType.TEXT, blockText));
            }
         }
         child = child.getNext();
      }

      return segments;
   }

   private boolean hasTableBlock(Node document) {
      Node child = document.getFirstChild();
      while (child != null) {
         if (child instanceof TableBlock) {
            return true;
         }
         child = child.getNext();
      }
      return false;
   }

   private static class ImageLinkRef {
      final String fullMatch;
      final String size; // may be null
      final String artifactId;

      ImageLinkRef(String fullMatch, String size, String artifactId) {
         this.fullMatch = fullMatch;
         this.size = size;
         this.artifactId = artifactId;
      }
   }

   private List<ImageLinkRef> extractImageLinks(String content) {
      List<ImageLinkRef> links = new ArrayList<>();
      Matcher matcher = IMAGE_LINK_PATTERN.matcher(content);
      while (matcher.find()) {
         links.add(new ImageLinkRef(matcher.group(0), matcher.group(1), matcher.group(2)));
      }
      return links;
   }

   private void splitTextAndImageLinks(String text, List<ContentSegment> segments) {
      Matcher matcher = IMAGE_LINK_PATTERN.matcher(text);
      int lastEnd = 0;
      while (matcher.find()) {
         if (matcher.start() > lastEnd) {
            String preceding = text.substring(lastEnd, matcher.start()).trim();
            if (!preceding.isEmpty()) {
               segments.add(new ContentSegment(SegmentType.TEXT, preceding));
            }
         }
         segments.add(new ContentSegment(SegmentType.IMAGE_LINK, matcher.group(2))); // artifact ID
         lastEnd = matcher.end();
      }
      if (lastEnd < text.length()) {
         String trailing = text.substring(lastEnd).trim();
         if (!trailing.isEmpty()) {
            segments.add(new ContentSegment(SegmentType.TEXT, trailing));
         }
      }
   }

   // ---- Cell content building ----

   /**
    * Holds the set of fonts used for markdown rendering. Created once per workbook to avoid exceeding POI's font limit.
    */
   public static class WorkbookFonts {
      final Font normalFont;
      final Font boldFont;
      final Font italicFont;
      final Font codeFont;
      final Font headingFont;

      private WorkbookFonts(Font normalFont, Font boldFont, Font italicFont, Font codeFont, Font headingFont) {
         this.normalFont = normalFont;
         this.boldFont = boldFont;
         this.italicFont = italicFont;
         this.codeFont = codeFont;
         this.headingFont = headingFont;
      }

      /**
       * Creates a reusable font set for the given workbook. Call this once per workbook and pass the result to
       * {@link #renderCell(String, Workbook, WorkbookFonts)}.
       */
      public static WorkbookFonts create(Workbook workbook) {
         Font normalFont = workbook.createFont();
         normalFont.setFontName("Calibri");
         normalFont.setFontHeightInPoints((short) 11);

         Font boldFont = workbook.createFont();
         boldFont.setFontName("Calibri");
         boldFont.setFontHeightInPoints((short) 11);
         boldFont.setBold(true);

         Font italicFont = workbook.createFont();
         italicFont.setFontName("Calibri");
         italicFont.setFontHeightInPoints((short) 11);
         italicFont.setItalic(true);

         Font codeFont = workbook.createFont();
         codeFont.setFontName("Consolas");
         codeFont.setFontHeightInPoints((short) 10);

         Font headingFont = workbook.createFont();
         headingFont.setFontName("Calibri");
         headingFont.setFontHeightInPoints((short) 13);
         headingFont.setBold(true);

         return new WorkbookFonts(normalFont, boldFont, italicFont, codeFont, headingFont);
      }
   }

   /**
    * Renders markdown content using pre-created fonts. Prefer this overload for batch rendering to avoid font
    * proliferation in the workbook.
    */
   public CellRenderResult renderCell(String cellValue, Workbook workbook, WorkbookFonts fonts) {
      if (cellValue == null || cellValue.isEmpty()) {
         return new CellRenderResult(new XSSFRichTextString(""), new ArrayList<>(), 15);
      }

      Parser parser = Parser.builder(parserOptions).build();
      Node document = parser.parse(cellValue);

      List<ContentSegment> segments = classifyContent(document, cellValue);

      return buildCellContent(segments, fonts);
   }

   private CellRenderResult buildCellContent(List<ContentSegment> segments, Workbook workbook) {
      // Legacy overload — creates fonts each call (use WorkbookFonts variant for batch rendering)
      WorkbookFonts fonts = WorkbookFonts.create(workbook);
      return buildCellContent(segments, fonts);
   }

   private CellRenderResult buildCellContent(List<ContentSegment> segments, WorkbookFonts fonts) {
      StringBuilder textBuilder = new StringBuilder();
      List<FontRun> fontRuns = new ArrayList<>();
      List<ImageEmbed> images = new ArrayList<>();
      int totalImageHeight = 0;

      for (ContentSegment segment : segments) {
         switch (segment.type) {
            case TEXT:
               String rendered = renderTextSegment(segment.content);
               appendWithFormatting(textBuilder, fontRuns, rendered, fonts.normalFont, fonts.boldFont,
                  fonts.italicFont, fonts.headingFont);
               if (textBuilder.length() > 0 && textBuilder.charAt(textBuilder.length() - 1) != '\n') {
                  textBuilder.append('\n');
               }
               break;

            case TABLE:
               // Render table as image
               byte[] tableImage = tableRenderer.renderMarkdownToImage(segment.content);
               if (tableImage != null) {
                  int[] dims = getImageDimensions(tableImage);
                  if (dims == null) {
                     // Image format unreadable — fall back to monospace text
                     int codeStart = textBuilder.length();
                     textBuilder.append(segment.content);
                     fontRuns.add(new FontRun(codeStart, textBuilder.length(), fonts.codeFont));
                     textBuilder.append('\n');
                  } else {
                     int width = Math.min(dims[0], IMAGE_MAX_WIDTH_PX);
                     int height = Math.min(dims[1], IMAGE_MAX_HEIGHT_PX);
                     images.add(new ImageEmbed(tableImage, Workbook.PICTURE_TYPE_PNG, width, height));
                     totalImageHeight += height + 5;
                     // Add placeholder text
                     textBuilder.append("[Table]\n");
                  }
               } else {
                  // Fallback: render table as monospace text
                  int codeStart = textBuilder.length();
                  textBuilder.append(segment.content);
                  fontRuns.add(new FontRun(codeStart, textBuilder.length(), fonts.codeFont));
                  textBuilder.append('\n');
               }
               break;

            case IMAGE_LINK:
               // Resolve image from artifact
               ImageEmbed imageEmbed = resolveImageLink(segment.content);
               if (imageEmbed != null) {
                  images.add(imageEmbed);
                  totalImageHeight += imageEmbed.getHeightPx() + 5;
                  textBuilder.append("[Image]\n");
               } else {
                  textBuilder.append("[Image not found: ").append(segment.content).append("]\n");
               }
               break;

            case CODE_BLOCK:
               int codeStart = textBuilder.length();
               // Strip the fence markers (```...```)
               String codeContent = stripFenceMarkers(segment.content);
               textBuilder.append(codeContent);
               fontRuns.add(new FontRun(codeStart, textBuilder.length(), fonts.codeFont));
               textBuilder.append('\n');
               break;
         }
      }

      // Build the rich text string with font runs
      XSSFRichTextString richText = new XSSFRichTextString(textBuilder.toString());
      richText.applyFont(fonts.normalFont);
      for (FontRun run : fontRuns) {
         if (run.start < richText.length() && run.end <= richText.length()) {
            richText.applyFont(run.start, run.end, run.font);
         }
      }

      // Calculate required row height
      int textLines = textBuilder.toString().split("\n").length;
      int textHeight = textLines * 15; // ~15 points per line
      int totalHeight = Math.max(textHeight, totalImageHeight);

      return new CellRenderResult(richText, images, Math.max(totalHeight, 15));
   }

   private static class FontRun {
      final int start;
      final int end;
      final Font font;

      FontRun(int start, int end, Font font) {
         this.start = start;
         this.end = end;
         this.font = font;
      }
   }

   /**
    * Renders a text segment, stripping markdown formatting markers and returning cleaned text. The formatting markers
    * are tracked via font runs.
    */
   private String renderTextSegment(String markdown) {
      // Strip heading markers
      if (markdown.startsWith("#")) {
         int headingEnd = 0;
         while (headingEnd < markdown.length() && markdown.charAt(headingEnd) == '#') {
            headingEnd++;
         }
         if (headingEnd < markdown.length() && markdown.charAt(headingEnd) == ' ') {
            return markdown.substring(headingEnd + 1);
         }
      }
      return markdown;
   }

   private void appendWithFormatting(StringBuilder textBuilder, List<FontRun> fontRuns, String text, Font normalFont,
      Font boldFont, Font italicFont, Font headingFont) {
      // Simple bold/italic detection — handles **bold** and *italic*
      int pos = 0;

      while (pos < text.length()) {
         if (pos + 1 < text.length() && text.charAt(pos) == '*' && text.charAt(pos + 1) == '*') {
            // Bold
            int endBold = text.indexOf("**", pos + 2);
            if (endBold > 0) {
               int start = textBuilder.length();
               textBuilder.append(text, pos + 2, endBold);
               fontRuns.add(new FontRun(start, textBuilder.length(), boldFont));
               pos = endBold + 2;
               continue;
            }
         } else if (text.charAt(pos) == '*' && (pos == 0 || text.charAt(pos - 1) != '*') && (pos + 1 < text.length() && text.charAt(
            pos + 1) != '*' && text.charAt(pos + 1) != ' ')) {
            // Italic — require non-space after opening * (standard markdown rule)
            int endItalic = text.indexOf('*', pos + 1);
            if (endItalic > 0 && text.charAt(endItalic - 1) != ' ' && (endItalic + 1 >= text.length() || text.charAt(endItalic + 1) != '*')) {
               int start = textBuilder.length();
               textBuilder.append(text, pos + 1, endItalic);
               fontRuns.add(new FontRun(start, textBuilder.length(), italicFont));
               pos = endItalic + 1;
               continue;
            }
         } else if (pos + 1 < text.length() && text.charAt(pos) == '~' && text.charAt(pos + 1) == '~') {
            // Strikethrough — just render as plain text (Excel rich text doesn't support strikethrough easily)
            int endStrike = text.indexOf("~~", pos + 2);
            if (endStrike > 0) {
               textBuilder.append(text, pos + 2, endStrike);
               pos = endStrike + 2;
               continue;
            }
         }
         textBuilder.append(text.charAt(pos));
         pos++;
      }
   }

   private String stripFenceMarkers(String fencedCode) {
      String[] lines = fencedCode.split("\n");
      StringBuilder result = new StringBuilder();
      boolean started = false;
      for (String line : lines) {
         if (!started) {
            if (line.trim().startsWith("```")) {
               started = true;
               continue;
            }
         } else {
            if (line.trim().startsWith("```")) {
               break;
            }
            if (result.length() > 0) {
               result.append('\n');
            }
            result.append(line);
         }
      }
      return result.toString();
   }

   /**
    * Resolves an image-link artifact ID to an ImageEmbed by loading the artifact's NativeContent attribute.
    */
   private ImageEmbed resolveImageLink(String artifactIdStr) {
      try {
         ArtifactId artId = ArtifactId.valueOf(artifactIdStr);
         ArtifactReadable imageArt =
            orcsApi.getQueryFactory().fromBranch(branch).andId(artId).asArtifactOrSentinel();

         if (imageArt.isInvalid()) {
            return null;
         }

         byte[] imageBytes;
         try (InputStream inputStream = imageArt.getSoleAttributeValue(CoreAttributeTypes.NativeContent)) {
            imageBytes = Lib.inputStreamToBytes(inputStream);
         }

         String extension = imageArt.getSoleAttributeAsString(CoreAttributeTypes.Extension, "png");
         int pictureType = getPictureType(extension);

         int[] dims = getImageDimensions(imageBytes);
         if (dims == null) {
            OseeLog.log(MarkdownExcelCellRenderer.class, OseeLevel.WARNING,
               "Unsupported image format for artifact " + artifactIdStr + " — skipping embed");
            return null;
         }
         int width = dims[0];
         int height = dims[1];

         // Scale to fit max dimensions
         if (width > IMAGE_MAX_WIDTH_PX || height > IMAGE_MAX_HEIGHT_PX) {
            double scaleW = (double) IMAGE_MAX_WIDTH_PX / width;
            double scaleH = (double) IMAGE_MAX_HEIGHT_PX / height;
            double scale = Math.min(scaleW, scaleH);
            width = (int) (width * scale);
            height = (int) (height * scale);
         }

         return new ImageEmbed(imageBytes, pictureType, width, height);
      } catch (Exception ex) {
         OseeLog.log(MarkdownExcelCellRenderer.class, OseeLevel.WARNING,
            "Failed to resolve image-link artifact " + artifactIdStr, ex);
         return null;
      }
   }

   private int getPictureType(String extension) {
      switch (extension.toLowerCase()) {
         case "png":
            return Workbook.PICTURE_TYPE_PNG;
         case "jpg":
         case "jpeg":
            return Workbook.PICTURE_TYPE_JPEG;
         case "gif":
            // POI doesn't have a GIF type, store as PNG
            return Workbook.PICTURE_TYPE_PNG;
         case "bmp":
            return Workbook.PICTURE_TYPE_DIB;
         default:
            return Workbook.PICTURE_TYPE_PNG;
      }
   }

   private int[] getImageDimensions(byte[] imageBytes) {
      try {
         ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
         BufferedImage img = ImageIO.read(bais);
         if (img != null) {
            return new int[] {img.getWidth(), img.getHeight()};
         }
      } catch (IOException ex) {
         OseeLog.log(MarkdownExcelCellRenderer.class, OseeLevel.WARNING,
            "Failed to read image dimensions", ex);
      }
      // Return null to signal that the image format is unsupported/unreadable
      return null;
   }
}
