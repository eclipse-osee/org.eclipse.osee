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
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.define.operations.publishing.WordCoreUtilServer;
import org.eclipse.osee.framework.core.publishing.WordCoreUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Readers;

/**
 * @author Jaden W. Puckett
 */
public class WordTemplateContentToMarkdownConverter {
   private static final String PARAGRAPH_TAG_WITH_ATTRS = "<w:p ";
   private static final String PARAGRAPH_TAG_EMPTY = "<w:p/>";
   private static final String PARAGRAPH_TAG = "<w:p>";

   private static final CharSequence[] PARAGRAPH_TAGS =
      {PARAGRAPH_TAG_WITH_ATTRS, WordCoreUtil.PARAGRAPH, WordCoreUtil.PARAGRAPH_END, WordCoreUtilServer.BODY_END};

   private static final Pattern internalAttributeElementsPattern = Pattern.compile(
      "<((\\w+:)?(\\w+))(\\s+.*?)((/>)|(>(.*?)</\\1>))", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
   private static final Pattern proofErrTagKiller = Pattern.compile("</?w:proofErr.*?/?>");
   private static final int NAMESPACE_GROUP = 2;
   private static final int ELEMENT_NAME_GROUP = 3;
   private static final int ATTRIBUTE_BLOCK_GROUP = 4;
   private static final int CONTENT_GROUP = 8;

   private static final String HEADER_REGEX = "Heading[1-9]";
   private static final String RUN_TEXT_REGEX =
      "(?s)<w:r.*?>(?:(<w:br/>)|(.*?)<w:(?:pict|t)>(.*?)</w:(?:pict|t)>)</w:r>";
   private static final String NORMALWEB_REGEX = "NormalWeb";
   private static final String BOLD_INDICATOR = "<w:rPr><w:b/></w:rPr>";
   private static final String BOLD_COMPLEX_INDICATOR = "<w:rPr><w:b/><w:b-cs/></w:rPr>";
   private static final String ITALICS_INDICATOR = "<w:rPr><w:i/></w:rPr>";
   private static final String V_SHAPE_REGEX = "(?s)<v:shape.*?>(.*?)</v:shape.*?>";
   private static final String BULLET_INDICATOR = "Bullet point";
   private static final String NO_PROOF_INDICATOR = "<w:rPr><w:noProof/>.*?</w:rPr>";
   private static final String BREAK_INDICATOR = "<w:br/>";
   private static final String IMAGE_INDICATOR = ".png";
   private static final String IMAGE_LOCATION_INDICATOR = "href=\"(.*?.png.*?)\"";

   private String markdownContent = "";
   private String paragraphStyle = "";

   public WordTemplateContentToMarkdownConverter() {
   }

   private void parseParagraphNormalContents(CharSequence content) {
      Pattern regex = Pattern.compile(RUN_TEXT_REGEX);
      Matcher matcher = regex.matcher(content);
      // extract word run content
      while (matcher.find()) {
         if (matcher.group(1) == null) {
            content = matcher.group(3);
            if (matcher.group(2).equals(BREAK_INDICATOR)) {
               markdownContent += "\n";
               markdownContent += content;
            } else if (matcher.group(2).equals(BOLD_INDICATOR) || matcher.group(2).equals(BOLD_COMPLEX_INDICATOR)) {
               if (((String) content).startsWith(" ")) {
                  markdownContent += " **" + ((String) content).substring(1) + "**";
               } else {
                  markdownContent += "**" + content + "**";
               }
            } else if (matcher.group(2).equals(ITALICS_INDICATOR)) {
               if (((String) content).startsWith(" ")) {
                  markdownContent += " *" + ((String) content).substring(1) + "*";
               } else {
                  markdownContent += "*" + content + "*";
               }
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
               }

            } else {
               markdownContent += content;
            }
         } else if (matcher.group(1).equals(BREAK_INDICATOR)) {
            markdownContent += "\n";
         }

      }
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

         while ((element = Readers.forward(reader, PARAGRAPH_TAGS)) != null) {

            if (element == WordCoreUtilServer.BODY_END) {
               return markdownContent;

            } else {

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
                  parseParagraphNormalContents(content);
               } else if (paragraphStyle.matches(HEADER_REGEX)) {
                  parseParagraphHeaderContents(content);
               } else if (paragraphStyle.matches(NORMALWEB_REGEX)) {
                  parseParagraphNormalContents(content);
               }

               // add a return after each paragraph
               markdownContent += "\n\n";
            }
         }

      } catch (Exception e) {
         e.printStackTrace();
      }

      return markdownContent + "... - ERROR CONVERTING WORD TEMPLATE CONTENT TO MARKDOWN";
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
