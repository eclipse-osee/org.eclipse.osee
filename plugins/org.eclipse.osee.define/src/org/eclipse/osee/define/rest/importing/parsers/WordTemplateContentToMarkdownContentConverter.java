/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.define.rest.importing.parsers;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Base64;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.define.operations.publisher.publishing.WordCoreUtilServer;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.publishing.WordCoreUtil;
import org.eclipse.osee.framework.core.publishing.markdown.MarkdownCleaner;
import org.eclipse.osee.framework.jdk.core.util.Readers;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * Thread safe
 *
 * @author Jaden W. Puckett
 */
public class WordTemplateContentToMarkdownContentConverter {

   private final OrcsApi orcsApi;
   private final BranchId branchId;
   private final StringBuffer errorLog = new StringBuffer();
   private final StringBuffer imageLog = new StringBuffer();

   private static final String PARAGRAPH_TAG_WITH_ATTRS = "<w:p ";
   private static final String PARAGRAPH_TAG_EMPTY = "<w:p/>";
   private static final String PARAGRAPH_TAG = "<w:p>";
   private static final String TABLE_TAG = "<w:tbl>";

   private static final CharSequence[] PARAGRAPH_AND_TABLE_TAGS = {
      PARAGRAPH_TAG_WITH_ATTRS,
      WordCoreUtil.PARAGRAPH,
      WordCoreUtil.PARAGRAPH_END,
      WordCoreUtilServer.BODY_END,
      TABLE_TAG};

   private static final Pattern internalAttributeElementsPattern = Pattern.compile(
      "<((\\w+:)?(\\w+))(\\s+.*?)((/>)|(>(.*?)</\\1>))", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
   private static final Pattern proofErrTagKiller = Pattern.compile("</?w:proofErr.*?/?>");

   private static final int NAMESPACE_GROUP = 2;
   private static final int ELEMENT_NAME_GROUP = 3;
   private static final int ATTRIBUTE_BLOCK_GROUP = 4;
   private static final int CONTENT_GROUP = 8;

   private static final String HEADER_REGEX = "Heading[1-9]";
   private static final String PSTYLE_NORMALWEB_REGEX = "NormalWeb";
   private static final String PSTYLE_BODYTEXT_REGEX = "BodyText";
   private static final String PSTYLE_CAPTION_REGEX = "Caption";
   private static final String PSTYLE_BULLETED_LIST_REGEX = "BulletedList";
   private static final String PSTYLE_NUMBERED_LIST_REGEX = "NumberedList";
   private static final String FEATURE_CONFIG_CONFIGGROUP_TAG_INDICATOR =
      "<w:highlight w:val=\"light-gray\"></w:highlight>";
   private static final String BOLD_INDICATOR = "<w:b></w:b>";
   private static final String BOLD_COMPLEX_INDICATOR = "<w:b></w:b><w:b-cs></w:b-cs>";
   private static final String ITALICS_INDICATOR = "<w:i></w:i>";
   private static final String ITALICS_COMPLEX_INDICATOR = "<w:i></w:i><w:i-cs></w:i-cs>";
   private static final String UNDERLINE_INDICATOR = "<w:u w:val=\"single\"></w:u>";
   private static final String V_SHAPE_REGEX = "(?s)<v:shape.*?>(.*?)</v:shape.*?>";
   private static final String BULLET_INDICATOR = "Bullet point";
   private static final String NO_PROOF_INDICATOR = "<w:noProof/>";
   private static final String SUPERSCRIPT_INDICATOR = "vertAlign w:val=\"superscript\"";
   private static final String BREAK_INDICATOR = "<w:br/>";
   private static final String TAB_INDICATOR = "<w:tab/>";
   private static final String BULLET_PNG_INDICATOR = ".png";

   private static final Pattern RUN_TEXT_PATTERN =
      Pattern.compile("(?s)<w:r.*?>(?:(<w:br/>)|(.*?)<w:(?:pict|t)>(.*?)</w:(?:pict|t)>)</w:r>|OSEE_LINK\\((.*?)\\)");
   private static final Pattern BIN_DATA_PATTERN = Pattern.compile("(?s)<w:binData(.*?)>(.*?)</w:binData>(?s)");
   private static final Pattern BULLET_PNG_LOCATION_PATTERN = Pattern.compile("href=\"(.*?.png.*?)\"");
   private static final Pattern BULLET_PATTERN =
      Pattern.compile("<wx:t wx:val=\"·\"></wx:t><wx:font wx:val=\"Symbol\">");
   private static final Pattern NUMBERED_LIST_PATTERN = Pattern.compile("<wx:t wx:val=\"(\\d+\\.)\"></wx:t>");
   private static final Pattern TABLE_ROW_PATTERN = Pattern.compile("<w:tr(.*?)>(.*?)</w:tr>", Pattern.DOTALL);
   private static final Pattern TABLE_CELL_PATTERN = Pattern.compile("<w:tc(.*?)>(.*?)</w:tc>", Pattern.DOTALL);
   private static final Pattern GRID_SPAN_PATTERN = Pattern.compile("w:gridSpan w:val=\"(\\d+)\"");
   private static final Pattern TABLE_CELL_PARAGRAPH_PATTERN = Pattern.compile("<w:p(.*?)>(.*?)</w:p>", Pattern.DOTALL);
   private static final Pattern TABLE_CELL_RUN_PATTERN = Pattern.compile("<w:t(.*?)>(.*?)</w:t>", Pattern.DOTALL);
   private static final Pattern SUPPORTED_IMAGES_EXTENSION_PATTERN = Pattern.compile(".(jpg|png|wmz|emz)");

   public WordTemplateContentToMarkdownContentConverter(OrcsApi orcsApi, BranchId branchId) {
      this.orcsApi = orcsApi;
      this.branchId = branchId;
   }

   public String run(String wordXML, ArtifactId currentArtifactId) {
      // surround in body tags for tracking the conversion to markdown
      wordXML = WordCoreUtilServer.BODY_START + wordXML + WordCoreUtilServer.BODY_END;

      Reader reader = null;
      StringBuilder markdownContent = new StringBuilder();
      String[] paragraphStyle = new String[1]; // Use array to allow mutation in lambdas/methods

      try {
         InputStream inputStream = new ByteArrayInputStream(wordXML.getBytes());
         reader = new BufferedReader(new InputStreamReader(inputStream));

         if (Readers.forward(reader, WordCoreUtilServer.BODY_START) == null) {
            logError("No start of body tag", currentArtifactId);
         }

         CharSequence element;
         StringBuilder content = new StringBuilder(2000);

         while ((element = Readers.forward(reader, PARAGRAPH_AND_TABLE_TAGS)) != null) {

            // END
            if (element == WordCoreUtilServer.BODY_END) {
               String md = markdownContent.toString().replace("&amp;", "&");
               if (MarkdownCleaner.containsSpecialCharacters(md)) {
                  md = MarkdownCleaner.removeSpecialCharacters(md);
               }
               return md;

            } else if (element.toString().startsWith("<w:p")) {

               content.setLength(0);
               content.append(element);
               boolean emptyTagWithAttrs = false;

               if (element == PARAGRAPH_TAG_WITH_ATTRS) {
                  if (Readers.forward(reader, (Appendable) content, ">") == null) {
                     logError("Did not find expected end of tag", currentArtifactId);
                  }
                  emptyTagWithAttrs = content.toString().endsWith("/>");
               }
               if (element == PARAGRAPH_TAG || !emptyTagWithAttrs && element == PARAGRAPH_TAG_WITH_ATTRS) {
                  Readers.xmlForward(reader, content, "w:p");
               } else if (element != PARAGRAPH_TAG_WITH_ATTRS && element != PARAGRAPH_TAG_EMPTY) {
                  logError("Unexpected element returned", currentArtifactId);
               }

               content = new StringBuilder(proofErrTagKiller.matcher(content).replaceAll(""));
               paragraphStyle[0] = null;
               parseParagraphAttributes(content, new Stack<String>(), paragraphStyle);

               // parse and convert based on paragraphStyling
               if (paragraphStyle[0] == null) {
                  parseParagraphNormalContents(content, false, markdownContent, currentArtifactId);
               } else if (paragraphStyle[0].matches(HEADER_REGEX)) {
                  parseParagraphHeaderContents(content, markdownContent);
               } else if (paragraphStyle[0].matches(PSTYLE_NORMALWEB_REGEX) || paragraphStyle[0].matches(
                  PSTYLE_BODYTEXT_REGEX)) {
                  parseParagraphNormalContents(content, false, markdownContent, currentArtifactId);
               } else if (paragraphStyle[0].matches(PSTYLE_CAPTION_REGEX)) {
                  parseParagraphNormalContents(content, true, markdownContent, currentArtifactId);
               } else if (paragraphStyle[0].matches(PSTYLE_BULLETED_LIST_REGEX)) {
                  parseBulletedListContents(content, markdownContent, currentArtifactId);
               } else if (paragraphStyle[0].equals(PSTYLE_NUMBERED_LIST_REGEX)) {
                  parseNumberedListContents(content, markdownContent, currentArtifactId);
               }

               markdownContent.append("\n\n");

            } else if (element.toString().startsWith(TABLE_TAG)) {

               content.setLength(0);
               content.append(element);
               Readers.xmlForward(reader, content, "w:tbl");
               parseTableContents(content, markdownContent);
            }
         }

      } catch (Exception e) {
         logError(e.toString(), currentArtifactId);
         e.printStackTrace();
      }

      // Should not reach this point unless there was an issue reading through wordXML
      return markdownContent.toString() + "\n\n<!-- ERROR CONVERTING WORD TEMPLATE CONTENT TO MARKDOWN\nArtifact ID: " + currentArtifactId + "\nBranch ID: " + branchId + " -->";
   }

   private void parseParagraphNormalContents(CharSequence content, Boolean isCaption, StringBuilder markdownContent,
      ArtifactId currentArtifactId) {

      if (isCaption) {
         markdownContent.append("<div style=\"text-align: center;\">");
      }

      Matcher matcher = RUN_TEXT_PATTERN.matcher(content);
      outerLoop: while (matcher.find()) {
         if (matcher.group(4) != null) {
            String oseeLinkRefId = matcher.group(4);
            if (!oseeLinkRefId.matches("\\d+")) {
               if (!branchId.equals(BranchId.SENTINEL)) {
                  ArtifactReadable oseeLinkRefArt = orcsApi.getQueryFactory().fromBranch(branchId).andGuid(
                     oseeLinkRefId).includeDeletedArtifacts().getArtifact();
                  oseeLinkRefId = oseeLinkRefArt.getArtifactId().getIdString();
                  if (oseeLinkRefArt.isDeleted()) {
                     markdownContent.append("<!-- LINK TO DELETED ARTIFACT (" + oseeLinkRefId + ") -->");
                  } else {
                     markdownContent.append("<artifact-link>").append(oseeLinkRefId.trim()).append("</artifact-link>");
                  }
               } else {
                  oseeLinkRefId =
                     "OSEE_LINK conversion error: GUID could not be converted to Artifact ID. Branch ID is SENTINEL.";
               }
            }
         } else {
            if (matcher.group(1) == null) {
               content = matcher.group(3);
               String contentStr = content.toString();

               // image
               Matcher binDataMatcher = BIN_DATA_PATTERN.matcher(content);
               while (binDataMatcher.find()) {
                  if (binDataMatcher.group(2) != null) {
                     Matcher imageExtMatcher = SUPPORTED_IMAGES_EXTENSION_PATTERN.matcher(binDataMatcher.group(1));
                     if (imageExtMatcher.find()) {
                        String binDataAttributes = binDataMatcher.group(1);
                        String base64ImageString = binDataMatcher.group(2);
                        if (base64ImageString.length() > 0) {
                           base64ImageString = base64ImageString.replaceAll("\\s+", "");
                           byte[] imageBytes = Base64.getDecoder().decode(base64ImageString);
                           InputStream imageBytesInputStream = new ByteArrayInputStream(imageBytes);
                           String imageExtension = extractImageExtension(binDataAttributes);
                           TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branchId,
                              "WTC to Markdown conversion - extract image data from artifact and create image artifact as child");
                           ArtifactToken token = tx.createArtifact(currentArtifactId, CoreArtifactTypes.Image,
                              "wordToMarkdownConversionImageTempName" + currentArtifactId);
                           tx.createAttribute(token, CoreAttributeTypes.NativeContent, imageBytesInputStream);
                           tx.createAttribute(token, CoreAttributeTypes.Extension, imageExtension);
                           tx.commit();
                           markdownContent.append("<image-link>").append(token.getIdString()).append("</image-link>");
                           logImageCreation(token.getIdString(),
                              "wordToMarkdownConversionImageTempName" + currentArtifactId);
                           continue outerLoop;
                        }
                     }
                  }
               }

               String c0 = matcher.group(0);
               String c1 = matcher.group(1);
               String c2 = matcher.group(2);
               String c3 = matcher.group(3);
               String c4 = matcher.group(4);

               /*
                * non-content appending
                */

               // line break
               if (matcher.group(2).contains(BREAK_INDICATOR)) {
                  markdownContent.append("\n");
               }

               // bullet point
               if (matcher.group(2).contains(NO_PROOF_INDICATOR)) {
                  if (matcher.group(3).contains(BULLET_INDICATOR) && matcher.group(3).matches(V_SHAPE_REGEX)) {
                     markdownContent.append("* ");
                  } else if (matcher.group(3).contains(BULLET_PNG_INDICATOR)) {
                     Matcher imageLocMatcher = BULLET_PNG_LOCATION_PATTERN.matcher(matcher.group(3));
                     while (imageLocMatcher.find()) {
                        markdownContent.append(imageLocMatcher.group(1));
                     }
                  }
               }

               // tab
               if (matcher.group(2).contains(TAB_INDICATOR)) {
                  markdownContent.append("    ");
               }

               // feature/config/config group tag
               boolean needsCommentSyntaxAppendedToEnd = false;
               if (matcher.group(2).contains(FEATURE_CONFIG_CONFIGGROUP_TAG_INDICATOR)) {
                  //@formatter:off
                  boolean needsCommentSyntaxAppendedToStart =
                     contentStr.startsWith("End") ||
                     (contentStr.startsWith("Feature") && !markdownContent.toString().trim().endsWith("End")) ||
                     (contentStr.startsWith("Configuration") && !markdownContent.toString().trim().endsWith("End")) ||
                     (contentStr.startsWith("ConfigurationGroup") && !markdownContent.toString().trim().endsWith("End"));

                  needsCommentSyntaxAppendedToEnd =
                     contentStr.endsWith("]") ||
                     contentStr.endsWith("Else") ||
                     (contentStr.endsWith("Feature") &&
                        (markdownContent.toString().trim().endsWith("End") || contentStr.startsWith("End"))
                     ) ||
                     (contentStr.endsWith("Configuration") &&
                        (markdownContent.toString().trim().endsWith("End") || contentStr.startsWith("End"))
                     ) ||
                     (contentStr.endsWith("ConfigurationGroup") &&
                        (markdownContent.toString().trim().endsWith("End") || contentStr.startsWith("End"))
                     );
                  //@formatter:on

                  if (needsCommentSyntaxAppendedToStart) {
                     markdownContent.append("``");
                  }
               }

               /*
                * superscript - typically all superscripts (and the content superscripted) are standalone (i.e. ^12345,
                * where 12345 is superscripted)
                */
               if (matcher.group(2).contains(SUPERSCRIPT_INDICATOR)) {
                  markdownContent.append("^");
               }

               /*
                * content appending
                */

               // bold, italic, underline + content
               boolean isBold =
                  matcher.group(2).contains(BOLD_INDICATOR) || matcher.group(2).contains(BOLD_COMPLEX_INDICATOR);
               boolean isItalics =
                  matcher.group(2).contains(ITALICS_INDICATOR) || matcher.group(2).contains(ITALICS_COMPLEX_INDICATOR);
               boolean isUnderline = matcher.group(2).contains(UNDERLINE_INDICATOR);
               if (isBold || isItalics || isUnderline) {

                  // Only apply formatting if content is not just whitespace
                  if (contentStr.trim().isEmpty()) {
                     markdownContent.append(contentStr);
                  } else {
                     String formatted = contentStr;

                     // Handle leading space for all formats
                     String leadingSpace = "";
                     if (formatted.startsWith(" ")) {
                        leadingSpace = " ";
                        formatted = formatted.substring(1);
                     }

                     // Apply underline
                     if (isUnderline) {
                        formatted = "<u>" + formatted + "</u>";
                     }
                     // Apply italics
                     if (isItalics) {
                        int len = markdownContent.length();
                        if (len >= 1 && markdownContent.charAt(len - 1) == '*') {
                           markdownContent.setLength(len - 1);
                           formatted = formatted + "*";
                        } else {
                           formatted = "*" + formatted + "*";
                        }
                     }
                     // Apply bold (outside of italics/underline)
                     if (isBold) {
                        int len = markdownContent.length();
                        if (len >= 2 && markdownContent.substring(len - 2).equals("**")) {
                           markdownContent.setLength(len - 2);
                           formatted = formatted + "**";
                        } else {
                           formatted = "**" + formatted + "**";
                        }
                     }

                     markdownContent.append(leadingSpace).append(formatted);
                  }
               }
               // content
               else {
                  markdownContent.append(content);
               }

               /*
                * non-content appending
                */

               if (needsCommentSyntaxAppendedToEnd) {
                  markdownContent.append("``");
               }

            } else if (matcher.group(1).equals(BREAK_INDICATOR)) {
               markdownContent.append("\n");
            }
         }
      }

      if (isCaption) {
         markdownContent.append("</div>");
      }
   }

   private String extractImageExtension(String binDataAttributes) {
      Pattern extensionPattern = Pattern.compile("\\.(\\w+)");
      Matcher extensionMatcher = extensionPattern.matcher(binDataAttributes);
      if (extensionMatcher.find()) {
         return extensionMatcher.group(1);
      }
      return "png";
   }

   private void parseBulletedListContents(CharSequence content, StringBuilder markdownContent,
      ArtifactId currentArtifactId) {
      Matcher bulletMatcher = BULLET_PATTERN.matcher(content);
      if (bulletMatcher.find()) {
         markdownContent.append("* ");
      }
      parseParagraphNormalContents(content, false, markdownContent, currentArtifactId);
   }

   private void parseNumberedListContents(CharSequence content, StringBuilder markdownContent,
      ArtifactId currentArtifactId) {
      Matcher numberMatcher = NUMBERED_LIST_PATTERN.matcher(content);
      String listNumber = "";

      if (numberMatcher.find()) {
         listNumber = numberMatcher.group(1);
      }

      markdownContent.append(listNumber).append(" ");
      parseParagraphNormalContents(content, false, markdownContent, currentArtifactId);
   }

   private void parseTableContents(CharSequence content, StringBuilder markdownContent) {
      StringBuilder tableMarkdown = new StringBuilder();
      tableMarkdown.append("\n");

      Matcher rowMatcher = TABLE_ROW_PATTERN.matcher(content);
      boolean isHeaderRow = true;

      while (rowMatcher.find()) {
         String rowContent = rowMatcher.group(2);
         Matcher cellMatcher = TABLE_CELL_PATTERN.matcher(rowContent);

         StringBuilder rowMarkdown = new StringBuilder();
         StringBuilder headerSeparator = new StringBuilder();

         while (cellMatcher.find()) {
            String cellContent = cellMatcher.group(2);
            String cellText = extractTextFromCell(cellContent);

            int gridSpan = getGridSpan(cellContent);

            if (isHeaderRow) {
               for (int i = 0; i < gridSpan; i++) {
                  rowMarkdown.append("| ").append(cellText).append(" ");
                  headerSeparator.append("|---");
               }
            } else {
               rowMarkdown.append("| ").append(cellText).append(" ");
            }
         }

         rowMarkdown.append("|");
         tableMarkdown.append(rowMarkdown.toString()).append("\n");

         if (isHeaderRow) {
            tableMarkdown.append(headerSeparator.toString()).append("|\n");
            isHeaderRow = false;
         }
      }

      markdownContent.append(tableMarkdown.toString());
   }

   private int getGridSpan(String cellAttributes) {
      Matcher gridSpanMatcher = GRID_SPAN_PATTERN.matcher(cellAttributes);
      if (gridSpanMatcher.find()) {
         return Integer.parseInt(gridSpanMatcher.group(1));
      }
      return 1;
   }

   private String extractTextFromCell(String cellContent) {
      StringBuilder cellText = new StringBuilder();
      Matcher paragraphMatcher = TABLE_CELL_PARAGRAPH_PATTERN.matcher(cellContent);

      while (paragraphMatcher.find()) {
         String paragraphContent = paragraphMatcher.group(2);
         cellText.append(extractTextFromParagraph(paragraphContent)).append(" ");
      }

      return cellText.toString().trim();
   }

   private String extractTextFromParagraph(String paragraphContent) {
      StringBuilder paragraphText = new StringBuilder();
      Matcher runMatcher = TABLE_CELL_RUN_PATTERN.matcher(paragraphContent);

      while (runMatcher.find()) {
         paragraphText.append(runMatcher.group(2));
      }

      return paragraphText.toString();
   }

   private int calcHeaderLevel(CharSequence content) {
      String[] parts = ((String) content).split("\\.");
      return parts.length;
   }

   private void parseParagraphHeaderContents(CharSequence content, StringBuilder markdownContent) {
      Matcher matcher = RUN_TEXT_PATTERN.matcher(content);
      if (matcher.find()) {
         content = matcher.group(3);
      }
      int level = calcHeaderLevel(content);
      if (level > 0) {
         for (int i = 0; i < level; i++) {
            markdownContent.append("#");
         }
         markdownContent.append(" ");
      }
      markdownContent.append(content);
   }

   private void parseParagraphAttributes(CharSequence content, Stack<String> parentElementNames,
      String[] paragraphStyle) {
      Matcher matcher = internalAttributeElementsPattern.matcher(content);

      while (matcher.find()) {
         String elementName = matcher.group(ELEMENT_NAME_GROUP);
         String elementNamespace = matcher.group(NAMESPACE_GROUP);
         String elementAttributes =
            matcher.group(ATTRIBUTE_BLOCK_GROUP) == null ? "" : matcher.group(ATTRIBUTE_BLOCK_GROUP);
         String elementContent = matcher.group(CONTENT_GROUP) == null ? "" : matcher.group(CONTENT_GROUP);

         if ("pStyle".equals(elementName)) {
            paragraphStyle[0] = getAttributeValue("w:val", elementAttributes);
         }

         if (elementContent.contains("w:ilvl") && elementContent.contains("wx:t wx:val")) {
            paragraphStyle[0] = "NumberedList";
         }

         parentElementNames.push(elementName);
         parseParagraphAttributes(elementContent, parentElementNames, paragraphStyle);
         parentElementNames.pop();
      }
   }

   private static final String getAttributeValue(String attributeName, String attributeStorage) {
      attributeName += "=\"";
      String attribute = "";

      int index = attributeStorage.indexOf(attributeName);
      if (index != -1) {
         int startIndex = index + attributeName.length();
         if (startIndex < attributeStorage.length()) {
            attribute = attributeStorage.substring(startIndex, attributeStorage.indexOf('"', startIndex)).trim();
         }
      }
      return attribute;
   }

   private synchronized void logError(String message, ArtifactId artifactId) {
      errorLog.append("\n<!----------------------------------------\n").append("Error for artifact: ").append(
         artifactId).append(" on branch: ").append(branchId).append("\n").append(message).append(
            "\n---------------------------------------->\n");
   }

   private synchronized void logImageCreation(String artifactId, String imageName) {
      imageLog.append("| ").append(imageName).append(" | ").append(artifactId).append(" |\n");
   }

   public synchronized String getErrorLog() {
      StringBuilder logHeader = new StringBuilder();
      logHeader.append("# Images Created During Conversion\n\n");
      logHeader.append("| Image Name | Artifact ID |\n");
      logHeader.append("|------------|-------------|\n");
      return logHeader.toString() + imageLog.toString() + "\n" + errorLog.toString();
   }
}
