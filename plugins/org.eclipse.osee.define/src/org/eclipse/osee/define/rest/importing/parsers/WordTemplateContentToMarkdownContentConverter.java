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
   private final StringBuffer tableLog = new StringBuffer();
   private final StringBuffer artifactLinkGuidConvertedLog = new StringBuffer();
   private final StringBuffer artifactLinkNoGuidConvertedLog = new StringBuffer(); //NoGuidConverted means the link already contained a proper artifact id (requiring no GUID to artifact id replacement)
   private final StringBuffer linkToDeletedArtifactLog = new StringBuffer();
   private int imagesCreatedCount = 0;
   private int tablesConvertedCount = 0;
   private int artifactLinkGuidConvertedCount = 0;
   private int artifactLinkNoGuidConvertedCount = 0; //NoGuidConverted means the link already contained a proper artifact id (requiring no GUID to artifact id replacement)
   private int linkToDeletedArtifactCount = 0;

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
   private static final String PSTYLE_FIGURE_REGEX = "Figure";
   private static final String FEATURE_CONFIG_CONFIGGROUP_TAG_INDICATOR =
      "<w:highlight w:val=\"light-gray\"></w:highlight>";

   // Bold indicators
   private static final String BOLD_START_INDICATOR = "<w:b/>";
   private static final String BOLD_END_INDICATOR = "</w:b>";
   private static final String BOLD_COMPLEX_START_INDICATOR = "<w:b-cs/>";
   private static final String BOLD_COMPLEX_END_INDICATOR = "</w:b-cs>";

   // Italics indicators
   private static final String ITALICS_START_INDICATOR = "<w:i/>";
   private static final String ITALICS_END_INDICATOR = "</w:i>";
   private static final String ITALICS_COMPLEX_START_INDICATOR = "<w:i-cs/>";
   private static final String ITALICS_COMPLEX_END_INDICATOR = "</w:i-cs>";

   // Underline indicators
   private static final String UNDERLINE_START_INDICATOR = "<w:u w:val=\"single\"/>";
   private static final String UNDERLINE_END_INDICATOR = "</w:u>";

   private static final String V_SHAPE_REGEX = "(?s)<v:shape.*?>(.*?)</v:shape.*?>";
   private static final String BULLET_INDICATOR = "Bullet point";
   private static final String NO_PROOF_INDICATOR = "<w:noProof/>";
   private static final String SUPERSCRIPT_INDICATOR = "vertAlign w:val=\"superscript\"";
   private static final String SUBSCRIPT_INDICATOR = "vertAlign w:val=\"subscript\"";
   private static final String BREAK_INDICATOR = "<w:br/>";
   private static final String TAB_INDICATOR = "<w:tab/>";
   private static final String BULLET_PNG_INDICATOR = ".png";

   private static final Pattern RUN_TEXT_PATTERN =
      Pattern.compile("(?s)<w:r.*?>(?:(<w:br/>)|(.*?)<w:(?:pict|t)>(.*?)</w:(?:pict|t)>)</w:r>|OSEE_LINK\\((.*?)\\)");
   private static final Pattern BIN_DATA_PATTERN = Pattern.compile("(?s)<w:binData(.*?)>(.*?)</w:binData>(?s)");
   private static final Pattern BULLET_PNG_LOCATION_PATTERN = Pattern.compile("href=\"(.*?.png.*?)\"");
   private static final Pattern BULLET_PATTERN =
      Pattern.compile("<wx:t wx:val=\"·\"></wx:t><wx:font wx:val=\"Symbol\">");
   private static final Pattern NUMBERED_LIST_PATTERN = Pattern.compile("<wx:t wx:val=\"(\\d+\\.|·)\"></wx:t>");
   private static final Pattern TABLE_ROW_PATTERN = Pattern.compile("<w:tr(.*?)>(.*?)</w:tr>", Pattern.DOTALL);
   private static final Pattern TABLE_CELL_PATTERN = Pattern.compile("<w:tc(.*?)>(.*?)</w:tc>", Pattern.DOTALL);
   private static final Pattern GRID_SPAN_PATTERN = Pattern.compile("w:gridSpan w:val=\"(\\d+)\"");

   public static final String CAPTION_START_TAG = "<CAPTION_START_TAG>";
   public static final String CAPTION_END_TAG = "<CAPTION_END_TAG>";
   public static final String TEMP_IMAGE_ARTIFACT_NAME = "wordToMarkdownConversionImageTempName";

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
               /*
                * Post-conversion cleaning
                */
               String md = markdownContent.toString().replace("&amp;", "&");
               if (MarkdownCleaner.containsSpecialCharacters(md)) {
                  md = MarkdownCleaner.removeSpecialCharacters(md);
               }
               md = cleanCaptions(md);
               md = MarkdownCleaner.enforceProperDoubleBacktickSyntaxForFeatureConfigConfigGroupTags(md);
               md = MarkdownCleaner.removeEdgeSpacesUnicode(md);
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
                  PSTYLE_BODYTEXT_REGEX) || paragraphStyle[0].matches(PSTYLE_FIGURE_REGEX)) {
                  parseParagraphNormalContents(content, false, markdownContent, currentArtifactId);
               } else if (paragraphStyle[0].matches(PSTYLE_CAPTION_REGEX)) {
                  parseParagraphNormalContents(content, true, markdownContent, currentArtifactId);
               } else if (paragraphStyle[0].matches(PSTYLE_BULLETED_LIST_REGEX)) {
                  parseBulletedListContents(content, markdownContent, currentArtifactId);
               } else if (paragraphStyle[0].equals(PSTYLE_NUMBERED_LIST_REGEX)) {
                  parseNumberedListContents(content, markdownContent, currentArtifactId);
               } else {
                  parseParagraphNormalContents(content, false, markdownContent, currentArtifactId);
               }

               markdownContent.append("\n\n");

            } else if (element.toString().startsWith(TABLE_TAG)) {

               content.setLength(0);
               content.append(element);
               Readers.xmlForward(reader, content, "w:tbl");
               parseTableContents(content, markdownContent, currentArtifactId);
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
         markdownContent.append(CAPTION_START_TAG);
      }

      Matcher matcher = RUN_TEXT_PATTERN.matcher(content);
      outerLoop: while (matcher.find()) {
         // Artifact link (containing GUID (legacy) OR ArtifactId)
         if (matcher.group(4) != null) {
            String oseeLinkRefId = matcher.group(4);
            /*
             * Need to query for artifact to make sure that it has not been deleted
             */
            if (!branchId.equals(BranchId.SENTINEL)) {
               ArtifactReadable oseeLinkRefArt = ArtifactReadable.SENTINEL;
               if (!oseeLinkRefId.matches("\\d+")) {
                  // Word link has GUID
                  oseeLinkRefArt = orcsApi.getQueryFactory().fromBranch(branchId).andGuid(
                     oseeLinkRefId).includeDeletedArtifacts().getArtifact();
                  logArtifactLinkConversionGuid(currentArtifactId.toString());
               } else {
                  // Word link has artifact id
                  oseeLinkRefArt = orcsApi.getQueryFactory().fromBranch(branchId).andId(
                     ArtifactId.valueOf(oseeLinkRefId)).includeDeletedArtifacts().getArtifact();
                  logArtifactLinkConversionNoGuid(currentArtifactId.toString());
               }
               oseeLinkRefId = oseeLinkRefArt.getArtifactId().getIdString();
               if (oseeLinkRefArt.isDeleted()) {
                  markdownContent.append("<!-- LINK TO DELETED ARTIFACT (" + oseeLinkRefId + ") -->");
                  logArtifactLinkToDeletedArtifact(currentArtifactId.toString());
               } else {
                  markdownContent.append("<artifact-link>").append(oseeLinkRefId.trim()).append("</artifact-link>");
               }
               if (oseeLinkRefArt == ArtifactReadable.SENTINEL) {
                  oseeLinkRefId =
                     "OSEE_LINK conversion error: GUID/ArtifactId could not be found during query to check if it was deleted.";
               }
            } else {
               oseeLinkRefId =
                  "OSEE_LINK conversion error: GUID could not be converted to Artifact ID. Branch ID is SENTINEL.";
            }
         } else {
            if (matcher.group(1) == null) {
               content = matcher.group(3);
               String contentStr = content.toString();

               // image
               Matcher binDataMatcher = BIN_DATA_PATTERN.matcher(content);
               while (binDataMatcher.find()) {
                  if (binDataMatcher.group(2) != null) {
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
                           TEMP_IMAGE_ARTIFACT_NAME + currentArtifactId);
                        tx.createAttribute(token, CoreAttributeTypes.NativeContent, imageBytesInputStream);
                        tx.createAttribute(token, CoreAttributeTypes.Extension, imageExtension);
                        tx.commit();
                        markdownContent.append("<image-link>").append(token.getIdString()).append("</image-link>");
                        logImageCreation(token.getIdString(), TEMP_IMAGE_ARTIFACT_NAME + currentArtifactId);
                        continue outerLoop;
                     }
                  }
               }

               //               String c0 = matcher.group(0);
               //               String c1 = matcher.group(1);
               //               String c2 = matcher.group(2);
               //               String c3 = matcher.group(3);
               //               String c4 = matcher.group(4);

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
                  continue outerLoop;
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
                * content appending
                */

               // bold, italic, underline, superscript + content

               //@formatter:off
               boolean isBold =
                  matcher.group(2).contains(BOLD_START_INDICATOR) ||
                  matcher.group(2).contains(BOLD_END_INDICATOR) ||
                  matcher.group(2).contains(BOLD_COMPLEX_START_INDICATOR) ||
                  matcher.group(2).contains(BOLD_COMPLEX_END_INDICATOR);

               boolean isItalics =
                  matcher.group(2).contains(ITALICS_START_INDICATOR) ||
                  matcher.group(2).contains(ITALICS_END_INDICATOR) ||
                  matcher.group(2).contains(ITALICS_COMPLEX_START_INDICATOR) ||
                  matcher.group(2).contains(ITALICS_COMPLEX_END_INDICATOR);

               boolean isUnderline =
                  matcher.group(2).contains(UNDERLINE_START_INDICATOR) ||
                  matcher.group(2).contains(UNDERLINE_END_INDICATOR);

               //@formatter:on
               boolean isSuperscript = matcher.group(2).contains(SUPERSCRIPT_INDICATOR);
               boolean isSubscript = matcher.group(2).contains(SUBSCRIPT_INDICATOR);

               if (isBold || isItalics || isUnderline || isSuperscript || isSubscript) {

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
                     // Apply superscript
                     if (isSuperscript) {
                        formatted = "^" + formatted + "^";
                     }

                     // Apply subscript
                     if (isSubscript) {
                        formatted = "<sub>" + formatted + "</sub>";
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
         markdownContent.append(CAPTION_END_TAG);
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
         if ("·".equals(listNumber)) {
            listNumber = "1.";
         }
      }

      markdownContent.append(listNumber).append(" ");
      parseParagraphNormalContents(content, false, markdownContent, currentArtifactId);
   }

   private void parseTableContents(CharSequence content, StringBuilder markdownContent, ArtifactId currentArtifactId) {
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
            String cellText = extractTextFromCell(cellContent, currentArtifactId);

            int gridSpan = getGridSpan(cellContent);

            if (isHeaderRow) {
               // Write the header text once
               rowMarkdown.append("| ").append(cellText).append(" ");
               headerSeparator.append("|---");

               // Then add empty cells for the remaining spanned columns
               for (int i = 1; i < gridSpan; i++) {
                  rowMarkdown.append("|");
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
      logTableConversion(currentArtifactId.getIdString());
   }

   private int getGridSpan(String cellAttributes) {
      Matcher gridSpanMatcher = GRID_SPAN_PATTERN.matcher(cellAttributes);
      if (gridSpanMatcher.find()) {
         return Integer.parseInt(gridSpanMatcher.group(1));
      }
      return 1;
   }

   private String extractTextFromCell(String cellContent, ArtifactId currentArtifactId) {
      StringBuilder cellText = new StringBuilder();
      cellText.append(run(cellContent, currentArtifactId));

      return cellText.toString().trim();
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
      imagesCreatedCount++;
   }

   private synchronized void logTableConversion(String artifactId) {
      tableLog.append("| ").append(artifactId).append(" |\n");
      tablesConvertedCount++;
   }

   private synchronized void logArtifactLinkConversionGuid(String artifactId) {
      artifactLinkGuidConvertedLog.append("| ").append(artifactId).append(" |\n");
      artifactLinkGuidConvertedCount++;
   }

   private synchronized void logArtifactLinkConversionNoGuid(String artifactId) {
      artifactLinkNoGuidConvertedLog.append("| ").append(artifactId).append(" |\n");
      artifactLinkNoGuidConvertedCount++;
   }

   private synchronized void logArtifactLinkToDeletedArtifact(String artifactId) {
      linkToDeletedArtifactLog.append("| ").append(artifactId).append(" |\n");
      linkToDeletedArtifactCount++;
   }

   public synchronized String getErrorLog() {
      StringBuilder logHeader = new StringBuilder();
      logHeader.append("# Images Created During Conversion\n\n");
      logHeader.append("| Image Name | Artifact ID |\n");
      logHeader.append("|------------|-------------|\n");
      logHeader.append(imageLog.toString());

      logHeader.append("\n# Tables Converted During Conversion\n\n");
      logHeader.append("| Artifact ID |\n");
      logHeader.append("|-------------|\n");
      logHeader.append(tableLog.toString());

      logHeader.append("\n# Artifact Links Converted (GUID converted) During Conversion\n\n");
      logHeader.append("| Artifact ID Of Artifact That Owns The Link |\n");
      logHeader.append("|-------------|\n");
      logHeader.append(artifactLinkGuidConvertedLog.toString());

      logHeader.append("\n# Artifact Links Converted (No GUID converted) During Conversion\n\n");
      logHeader.append("| Artifact ID Of Artifact That Owns The Link |\n");
      logHeader.append("|-------------|\n");
      logHeader.append(artifactLinkNoGuidConvertedLog.toString());

      logHeader.append("\n# Artifact Links To Deleted Artifacts\n\n");
      logHeader.append("| Artifact ID Of Artifact That Owns The Link |\n");
      logHeader.append("|-------------|\n");
      logHeader.append(linkToDeletedArtifactLog.toString());

      logHeader.append("\n# Summary\n\n");
      logHeader.append("Total Images Created: ").append(imagesCreatedCount).append("\n");
      logHeader.append("Total Tables Converted: ").append(tablesConvertedCount).append("\n");
      logHeader.append("Total Artifact Links Converted (GUID converted): ").append(
         artifactLinkGuidConvertedCount).append("\n");
      logHeader.append("Total Artifact Links Converted (No GUID converted): ").append(
         artifactLinkNoGuidConvertedCount).append("\n");
      logHeader.append("Total Artifact Links To Deleted Artifacts: ").append(linkToDeletedArtifactCount).append("\n");

      return logHeader.toString() + "\n" + errorLog.toString();
   }

   public static String cleanCaptions(String input) {
      if (input == null || input.isEmpty()) {
         return input;
      }

      // Early exit if there are no caption tags at all
      if (input.indexOf(CAPTION_START_TAG) < 0) {
         return input;
      }

      final String START = CAPTION_START_TAG;
      final String END = CAPTION_END_TAG;
      final int START_LEN = START.length();
      final int END_LEN = END.length();

      StringBuilder out = new StringBuilder(input.length());
      int i = 0, n = input.length();

      while (true) {
         int s = input.indexOf(START, i);
         if (s < 0) {
            out.append(input, i, n);
            break;
         }
         int e = input.indexOf(END, s + START_LEN);
         if (e < 0) {
            out.append(input, i, n);
            break;
         }

         out.append(input, i, s);

         int innerStart = s + START_LEN;
         int innerEnd = e;

         // Trim leading/trailing whitespace in-place
         while (innerStart < innerEnd && Character.isWhitespace(input.charAt(innerStart))) {
            innerStart++;
         }
         while (innerEnd > innerStart && Character.isWhitespace(input.charAt(innerEnd - 1))) {
            innerEnd--;
         }

         boolean isFigure = false, isTable = false;
         int len = innerEnd - innerStart;

         if (len >= 6 && input.regionMatches(true, innerStart, "Figure", 0, 6)) {
            isFigure = true;
         } else if (len >= 5 && input.regionMatches(true, innerStart, "Table", 0, 5)) {
            isTable = true;
         }

         if (isFigure || isTable) {
            int colonPos = -1;
            for (int k = innerStart; k < innerEnd; k++) {
               if (input.charAt(k) == ':') {
                  colonPos = k;
                  break;
               }
            }
            int contentStart = (colonPos >= 0) ? colonPos + 1 : innerStart;
            while (contentStart < innerEnd && Character.isWhitespace(input.charAt(contentStart))) {
               contentStart++;
            }

            String tag = isFigure ? "image-caption" : "table-caption";
            out.append('<').append(tag).append('>');
            if (contentStart < innerEnd) {
               out.append(input, contentStart, innerEnd);
            }
            out.append("</").append(tag).append('>');
         } else {
            // Not a Figure/Table caption; remove tags, keep content
            out.append(input, innerStart, innerEnd);
         }

         i = e + END_LEN;
      }

      return out.toString();
   }
}
