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
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.publishing.WordCoreUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Readers;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Jaden W. Puckett
 */
public class WordTemplateContentToMarkdownConverter {

   private final OrcsApi orcsApi;
   private final BranchId branchId;
   private final ArtifactId currentArtifactId;
   private final Boolean includeErrorLog;
   private final StringBuilder errorLog = new StringBuilder();

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
   private static final String RUN_TEXT_REGEX =
      "(?s)<w:r.*?>(?:(<w:br/>)|(.*?)<w:(?:pict|t)>(.*?)</w:(?:pict|t)>)</w:r>|OSEE_LINK\\((.*?)\\)";
   private static final String BIN_DATA_REGEX = "(?s)<w:binData(.*?)>(.*?)</w:binData>(?s)";
   private static final String PSTYLE_NORMALWEB_REGEX = "NormalWeb";
   private static final String PSTYLE_BODYTEXT_REGEX = "BodyText";
   private static final String PSTYLE_CAPTION_REGEX = "Caption";
   private static final String PSTYLE_BULLETED_LIST_REGEX = "BulletedList";

   private static final String FEATURE_CONFIG_CONFIGGROUP_TAG_INDICATOR =
      "<wx:font wx:val=\"Courier New\"></wx:font><w:highlight w:val=\"light-gray\"></w:highlight>";
   private static final String BOLD_INDICATOR = "<w:rPr><w:b></w:b></w:rPr>";
   private static final String BOLD_COMPLEX_INDICATOR = "<w:rPr><w:b></w:b><w:b-cs></w:b-cs></w:rPr>";
   private static final String ITALICS_INDICATOR = "<w:rPr><w:i></w:i></w:rPr>";
   private static final String ITALICS_COMPLEX_INDICATOR = "<w:rPr><w:i></w:i><w:i-cs></w:i-cs></w:rPr>";
   private static final String UNDERLINE_INDICATOR = "<w:rPr><w:u w:val=\"single\"></w:u></w:rPr>";
   private static final String V_SHAPE_REGEX = "(?s)<v:shape.*?>(.*?)</v:shape.*?>";
   private static final String BULLET_INDICATOR = "Bullet point";
   private static final String NO_PROOF_INDICATOR = "<w:rPr><w:noProof/>.*?</w:rPr>";
   private static final String SUPERSCRIPT_INDICATOR = "<w:rPr><w:vertAlign w:val=\"superscript\"/></w:rPr>";
   private static final String BREAK_INDICATOR = "<w:br/>";
   private static final String IMAGE_INDICATOR = ".png";
   private static final String IMAGE_LOCATION_INDICATOR = "href=\"(.*?.png.*?)\"";

   private String markdownContent = "";
   private String paragraphStyle = "";

   public WordTemplateContentToMarkdownConverter(OrcsApi orcsApi, BranchId branchId, ArtifactId currentArtifactId, Boolean includeErrorLog) {
      this.orcsApi = orcsApi;
      this.branchId = branchId;
      this.currentArtifactId = currentArtifactId;
      this.includeErrorLog = includeErrorLog;
   }

   public String getErrorLog() {
      if (errorLog.length() > 0 && includeErrorLog) {
         String message =
            "WordTemplateContentToMarkdownConverter error log for \nartifact: " + currentArtifactId + " \non branch: " + branchId;
         errorLog.insert(0, message + "\n");
         return errorLog.toString();
      } else {
         return "";
      }
   }

   private void parseParagraphNormalContents(CharSequence content, Boolean isCaption) {
      if (isCaption) {
         markdownContent += "<div style=\"text-align: center;\">";
      }

      Pattern regex = Pattern.compile(RUN_TEXT_REGEX);
      Matcher matcher = regex.matcher(content);
      // extract word run content
      outerLoop: while (matcher.find()) {

         // OSEE_LINK
         if (matcher.group(4) != null) {
            String oseeLinkRefId = matcher.group(4);
            // convert guids to art id if the ref id in the OSEE_LINK is a guid
            if (!oseeLinkRefId.matches("\\d+")) {
               if (!branchId.equals(BranchId.SENTINEL)) {
                  oseeLinkRefId = orcsApi.getQueryFactory().fromBranch(branchId).andGuid(
                     oseeLinkRefId).getArtifact().getArtifactId().getIdString();
               } else {
                  oseeLinkRefId =
                     "OSEE_LINK conversion error: GUID could not be converted to Artifact ID. Branch ID is SENTINEL.";
               }
            }
            markdownContent += "<osee-artifact>" + oseeLinkRefId.trim() + "</osee-artifact>";
         }

         else {
            if (matcher.group(1) == null) {
               // ---------------------DEBUGGING BLOCK DELETE LATER---------------------
               String c1 = matcher.group(1);
               String c2 = matcher.group(2);
               String c3 = matcher.group(3);
               String c4 = matcher.group(4);
               // ----------------------------------------------------------------------
               content = matcher.group(3);

               // binary data (image) extraction
               Pattern binDataRegex = Pattern.compile(BIN_DATA_REGEX);
               Matcher binDataMatcher = binDataRegex.matcher(content);
               while (binDataMatcher.find()) {
                  // ---------------------DEBUGGING BLOCK DELETE LATER---------------------
                  String t1 = binDataMatcher.group(1);
                  String t2 = binDataMatcher.group(2);
                  // ----------------------------------------------------------------------
                  if (binDataMatcher.group(2) != null) {
                     if (binDataMatcher.group(1).contains(IMAGE_INDICATOR)) {
                        String base64ImageString = binDataMatcher.group(2);
                        if (base64ImageString.length() > 0) {
                           base64ImageString = base64ImageString.replaceAll("\\s+", "");
                           byte[] imageBytes = Base64.getDecoder().decode(base64ImageString);
                           InputStream imageBytesInputStream = new ByteArrayInputStream(imageBytes);
                           // create image artifact, set parent to the current artifact id, set native content to binDataString
                           TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branchId,
                              "WTC to Markdown conversion - extract image data from artifact and create image artifact as child");
                           ArtifactToken token = tx.createArtifact(currentArtifactId, CoreArtifactTypes.Image,
                              "wordToMarkdownConversionImageTempName" + currentArtifactId);
                           tx.createAttribute(token, CoreAttributeTypes.NativeContent, imageBytesInputStream);
                           tx.createAttribute(token, CoreAttributeTypes.Extension, "png"); //@todo: update this to find and capture proper extension!!!
                           tx.commit();
                           // create image link in the current artifact's markdown content
                           markdownContent += "<osee-image>" + token.getIdString() + "</osee-image>";
                           continue outerLoop;
                        }

                     }
                  }
               }

               if (matcher.group(2).equals(BREAK_INDICATOR)) {
                  markdownContent += "\n";
                  markdownContent += content;
               } else if (matcher.group(2).equals(BOLD_INDICATOR) || matcher.group(2).equals(BOLD_COMPLEX_INDICATOR)) {
                  if (((String) content).startsWith(" ")) {
                     markdownContent += " **" + ((String) content).substring(1) + "**";
                  } else {
                     markdownContent += "**" + content + "**";
                  }
               } else if (matcher.group(2).equals(ITALICS_INDICATOR) || matcher.group(2).equals(
                  ITALICS_COMPLEX_INDICATOR)) {
                  if (((String) content).startsWith(" ")) {
                     markdownContent += " *" + ((String) content).substring(1) + "*";
                  } else {
                     markdownContent += "*" + content + "*";
                  }
               } else if (matcher.group(2).equals(UNDERLINE_INDICATOR)) {
                  markdownContent += "<u>" + content + "</u>";
               } else if (matcher.group(2).matches(NO_PROOF_INDICATOR)) {
                  // handles strange word bullet points images generated during export
                  if (matcher.group(3).contains(BULLET_INDICATOR) && matcher.group(3).matches(V_SHAPE_REGEX)) {
                     markdownContent += "* ";
                  } // replaces image xml with file location (and name) of image on exporter's PC
                  else if (matcher.group(3).contains(IMAGE_INDICATOR)) {
                     Pattern imageRegex = Pattern.compile(IMAGE_LOCATION_INDICATOR);
                     Matcher imageLocMatcher = imageRegex.matcher(matcher.group(3));
                     while (imageLocMatcher.find()) {
                        markdownContent += imageLocMatcher.group(1);
                     }
                  } else {
                     // Covers case where # is after Figure in caption - <w:fldSimple w:instr=" SEQ Figure \* ARABIC "><w:r><w:rPr><w:noProof/></w:rPr><w:t>#</w:t></w:r></w:fldSimple>
                     markdownContent += content;
                  }
               } else if (matcher.group(2).matches(SUPERSCRIPT_INDICATOR)) {
                  markdownContent += "^" + content;
               } else if (matcher.group(2).contains(FEATURE_CONFIG_CONFIGGROUP_TAG_INDICATOR)) {
                  /*
                   * e.g. ``Feature[featA=included]`` A ``Feature Else`` Not A ``End Feature``
                   */
                  // @formatter:off
                  if (
                        content.toString().endsWith("]") ||
                        content.toString().endsWith("Else") ||
                        ( content.toString().endsWith("Feature") && (markdownContent.trim().endsWith("End") || markdownContent.trim().endsWith("Else")) ) ||
                        ( content.toString().endsWith("Configuration") && (markdownContent.trim().endsWith("End") || markdownContent.trim().endsWith("Else")) ) ||
                        ( content.toString().endsWith("ConfigurationGroup") && (markdownContent.trim().endsWith("End") || markdownContent.trim().endsWith("Else")) )
                     ) {
                     markdownContent += content + "``";
                 } else if (
                       content.toString().startsWith("End") ||
                       content.toString().startsWith("Feature") ||
                       content.toString().startsWith("Configuration") ||
                       content.toString().startsWith("ConfigurationGroup")
                    ) {
                     markdownContent += "``" + content;
                 } else { // any content between beginning `` and ending ``
                    markdownContent += content;
                 }
                  // @formatter:on
               } else {
                  markdownContent += content;
               }
            } else if (matcher.group(1).equals(BREAK_INDICATOR)) {
               markdownContent += "\n";
            }
         }
      }
      if (isCaption) {
         markdownContent += "</div>";
      }
   }

   private void parseBulletedListContents(CharSequence content) {
      // Logic to handle bullet points
      Pattern bulletPattern = Pattern.compile("<wx:t wx:val=\"·\"></wx:t><wx:font wx:val=\"Symbol\">");
      Matcher bulletMatcher = bulletPattern.matcher(content);
      if (bulletMatcher.find()) {
         markdownContent += "* "; // Markdown bullet point
      }
      // Continue processing the rest of the content
      parseParagraphNormalContents(content, false);
   }

   public String run(String wordXML) {

      // surround in body tags for tracking the conversion to markdown
      wordXML = WordCoreUtilServer.BODY_START + wordXML + WordCoreUtilServer.BODY_END;

      Reader reader = null;
      try {
         InputStream inputStream = new ByteArrayInputStream(wordXML.getBytes());
         reader = new BufferedReader(new InputStreamReader(inputStream));

         if (Readers.forward(reader, WordCoreUtilServer.BODY_START) == null) {
            handleFormatError("no start of body tag");
         }

         CharSequence element;
         StringBuilder content = new StringBuilder(2000);

         while ((element = Readers.forward(reader, PARAGRAPH_AND_TABLE_TAGS)) != null) {

            if (element == WordCoreUtilServer.BODY_END) {
               return markdownContent;

            } else if (element.toString().startsWith("<w:p")) {

               content.setLength(0);
               content.append(element);
               // if the tag had attributes, check that it isn't empty
               boolean emptyTagWithAttrs = false;

               if (element == PARAGRAPH_TAG_WITH_ATTRS) {
                  if (Readers.forward(reader, (Appendable) content, ">") == null) {
                     handleFormatError("did not find expected end of tag");
                  }
                  emptyTagWithAttrs = content.toString().endsWith("/>");
               }
               if (element == PARAGRAPH_TAG || !emptyTagWithAttrs && element == PARAGRAPH_TAG_WITH_ATTRS) {
                  Readers.xmlForward(reader, content, "w:p");
               } else if (element != PARAGRAPH_TAG_WITH_ATTRS && element != PARAGRAPH_TAG_EMPTY) {
                  throw new IllegalStateException("Unexpected element returned");
               }

               content = new StringBuilder(proofErrTagKiller.matcher(content).replaceAll(""));
               paragraphStyle = null;
               parseParagraphAttributes(content, new Stack<String>());

               // parse and convert based on paragraphStyling
               if (paragraphStyle == null) {
                  parseParagraphNormalContents(content, false);
               } else if (paragraphStyle.matches(HEADER_REGEX)) {
                  parseParagraphHeaderContents(content);
               } else if (paragraphStyle.matches(PSTYLE_NORMALWEB_REGEX) || paragraphStyle.matches(
                  PSTYLE_BODYTEXT_REGEX)) {
                  parseParagraphNormalContents(content, false);
               } else if (paragraphStyle.matches(PSTYLE_CAPTION_REGEX)) {
                  parseParagraphNormalContents(content, true);
               } else if (paragraphStyle.matches(PSTYLE_BULLETED_LIST_REGEX)) {
                  parseBulletedListContents(content);
               }

               // add a return after each paragraph
               markdownContent += "\n\n";

            } else if (element.toString().startsWith(TABLE_TAG)) {

               content.setLength(0);
               content.append(element);
               Readers.xmlForward(reader, content, "w:tbl");
               parseTableContents(content);
            }
         }

      } catch (Exception e) {
         markdownContent += e.toString();
         e.printStackTrace();
      }

      return markdownContent + "... - ERROR CONVERTING WORD TEMPLATE CONTENT TO MARKDOWN";
   }

   private void parseTableContents(CharSequence content) {
      StringBuilder tableMarkdown = new StringBuilder();
      tableMarkdown.append("\n");

      Pattern rowPattern = Pattern.compile("<w:tr(.*?)>(.*?)</w:tr>", Pattern.DOTALL);
      Matcher rowMatcher = rowPattern.matcher(content);
      boolean isHeaderRow = true;

      while (rowMatcher.find()) {
         String rowContent = rowMatcher.group(2);
         Pattern cellPattern = Pattern.compile("<w:tc(.*?)>(.*?)</w:tc>", Pattern.DOTALL);
         Matcher cellMatcher = cellPattern.matcher(rowContent);

         StringBuilder rowMarkdown = new StringBuilder();
         StringBuilder headerSeparator = new StringBuilder();

         while (cellMatcher.find()) {
            String cellContent = cellMatcher.group(2);
            String cellText = extractTextFromCell(cellContent);

            // Handle cell merging based on w:gridSpan attribute
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

      markdownContent += tableMarkdown.toString();
   }

   private int getGridSpan(String cellAttributes) {
      Pattern gridSpanPattern = Pattern.compile("w:gridSpan w:val=\"(\\d+)\"");
      Matcher gridSpanMatcher = gridSpanPattern.matcher(cellAttributes);
      if (gridSpanMatcher.find()) {
         return Integer.parseInt(gridSpanMatcher.group(1));
      }
      return 1;
   }

   private String extractTextFromCell(String cellContent) {
      StringBuilder cellText = new StringBuilder();
      Pattern paragraphPattern = Pattern.compile("<w:p(.*?)>(.*?)</w:p>", Pattern.DOTALL);
      Matcher paragraphMatcher = paragraphPattern.matcher(cellContent);

      while (paragraphMatcher.find()) {
         String paragraphContent = paragraphMatcher.group(2);
         cellText.append(extractTextFromParagraph(paragraphContent)).append(" ");
      }

      return cellText.toString().trim();
   }

   private String extractTextFromParagraph(String paragraphContent) {
      StringBuilder paragraphText = new StringBuilder();
      Pattern runPattern = Pattern.compile("<w:t(.*?)>(.*?)</w:t>", Pattern.DOTALL);
      Matcher runMatcher = runPattern.matcher(paragraphContent);

      while (runMatcher.find()) {
         paragraphText.append(runMatcher.group(2));
      }

      return paragraphText.toString();
   }

   private int calcHeaderLevel(CharSequence content) {
      String[] parts = ((String) content).split("\\.");
      return parts.length;
   }

   private void parseParagraphHeaderContents(CharSequence content) {
      Pattern regex = Pattern.compile(RUN_TEXT_REGEX);
      Matcher matcher = regex.matcher(content);
      // extract word run content
      if (matcher.find()) {
         content = matcher.group(3);
      }
      int level = calcHeaderLevel(content);
      if (level > 0) {
         for (int i = 0; i < level; i++) {
            markdownContent += "#";
         }
         markdownContent += " ";
      }
      markdownContent += content;
   }

   private void parseParagraphAttributes(CharSequence content, Stack<String> parentElementNames) {
      Matcher matcher = internalAttributeElementsPattern.matcher(content);

      while (matcher.find()) {
         String elementName = matcher.group(ELEMENT_NAME_GROUP);
         String elementNamespace = matcher.group(NAMESPACE_GROUP);
         String elementAttributes =
            matcher.group(ATTRIBUTE_BLOCK_GROUP) == null ? "" : matcher.group(ATTRIBUTE_BLOCK_GROUP);
         String elementContent = matcher.group(CONTENT_GROUP) == null ? "" : matcher.group(CONTENT_GROUP);

         if ("pStyle".equals(elementName)) {
            paragraphStyle = getAttributeValue("w:val", elementAttributes);
         }

         parentElementNames.push(elementName);
         parseParagraphAttributes(elementContent, parentElementNames);
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

   private void handleFormatError(String message) {
      throw new OseeStateException("File format error: %s in file [%s]", message);
   }

}
