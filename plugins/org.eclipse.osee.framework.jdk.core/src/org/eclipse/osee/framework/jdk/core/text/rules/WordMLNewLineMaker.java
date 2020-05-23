/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.framework.jdk.core.text.rules;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.text.Rule;
import org.eclipse.osee.framework.jdk.core.text.change.ChangeSet;

/**
 * Processes WordML documents and inserts WordML equivalent new lines. Used in importing tasks. TODO: update methods to
 * use ChangeSet rather than current logic
 *
 * @see WordMLNewLineMakerTest
 * @author Karol M. Wilk
 */
public final class WordMLNewLineMaker extends Rule {

   private static final String TERMINATING_WP_XML = "\n</w:p>";
   private static final String DUMMY_PARAGRAPH_BEGINNING =
      "\n<w:p wsp:rsidR=\"01234567\" wsp:rsidRDefault=\"01234567\">" + "<w:pPr>" + "<w:spacing w:before=\"48\" w:line=\"273\" w:line-rule=\"at-least\"/>" + "</w:pPr>";

   private final Pattern wordDocumentEndRegex = Pattern.compile(".*?</w:wordDocument>", Pattern.DOTALL);
   private final Pattern bodyRegex = Pattern.compile("<w:body>.*?</w:body>", Pattern.DOTALL);
   private final Pattern wxSubsectionRegex = Pattern.compile("<wx:sub-section>.*?</wx:sub-section>", Pattern.DOTALL);
   private final Pattern paragraphRegex = Pattern.compile("<w:p[ >].*?</w:p>", Pattern.DOTALL);
   private final Pattern wordRunRegex = Pattern.compile("<w:r[ >].*?</w:r>", Pattern.DOTALL);
   private final Pattern wordTextRegex = Pattern.compile("<w:t>(.*?)</w:t>", Pattern.DOTALL);

   private StringBuilder modifiedText = null;

   @Override
   public ChangeSet computeChanges(CharSequence entireFile) {
      modifiedText = new StringBuilder(entireFile.length() * 2);
      int lastBodySectionMatchEndIndex = -1;
      boolean foundSomething = false;

      Matcher bodySectionMatcher = bodyRegex.matcher(entireFile);

      if (bodySectionMatcher.find()) {
         Matcher wxSubSectionMatcher = wxSubsectionRegex.matcher(bodySectionMatcher.group());
         while (wxSubSectionMatcher.find()) {
            if (!foundSomething) {
               //write only at first time
               writeToBuffer(entireFile.subSequence(0, bodySectionMatcher.start()));
               writeToBuffer("\n<w:body>");
            }
            lastBodySectionMatchEndIndex = bodySectionMatcher.end();
            processWxSubSection(wxSubSectionMatcher.group());
            foundSomething = true;
         }

         if (foundSomething) {
            writeToBuffer("\n</w:body>");
         }
      }

      //data from </w:body> to end of </w:wordDocument>
      if (foundSomething && lastBodySectionMatchEndIndex != -1) {
         writeToBuffer(trimWordDocumentEnd(entireFile.subSequence(lastBodySectionMatchEndIndex, entireFile.length())));
      }

      return decideOnResultAndReturnIt(modifiedText, entireFile);
   }

   private CharSequence trimWordDocumentEnd(CharSequence endOfWordDocumentChunk) {
      Matcher endOfWordDocumentMatcher = wordDocumentEndRegex.matcher(endOfWordDocumentChunk);
      int chopOffAt = -1;
      if (endOfWordDocumentMatcher.find()) {
         chopOffAt = endOfWordDocumentMatcher.end();
      }
      if (chopOffAt != -1) {
         return endOfWordDocumentChunk.subSequence(0, chopOffAt);
      } else {
         return endOfWordDocumentChunk;
      }
   }

   /**
    * if modifiedText is valid, it will be the changeset, otherwise entirefile
    *
    * @return changset representing result
    */
   private ChangeSet decideOnResultAndReturnIt(StringBuilder modifiedFile, CharSequence entireFile) {
      boolean insertModified = modifiedText.length() > 0;
      setRuleWasApplicable(insertModified);
      return new ChangeSet(insertModified ? modifiedText : entireFile);
   }

   private void processWxSubSection(String wxSubSection) {
      Matcher paragraphMatcher = paragraphRegex.matcher(wxSubSection);
      boolean writtenPreBodySection = false;
      int lastParagraphMatchEndIndex = -1;
      while (paragraphMatcher.find()) {

         String paragraph = paragraphMatcher.group();

         //write anything between section and paragraph
         if (!writtenPreBodySection) {
            writeToBuffer(wxSubSection.subSequence(0, paragraphMatcher.start()));
            writtenPreBodySection = true;
         }
         //write anything between last and current <w:p>
         if (lastParagraphMatchEndIndex != -1) {
            writeToBuffer(wxSubSection.subSequence(lastParagraphMatchEndIndex, paragraphMatcher.start()));
         }

         processParagraph(paragraph);

         lastParagraphMatchEndIndex = paragraphMatcher.end();
      }
      //write anything after paragraph and before section end...
      writeToBuffer(wxSubSection.subSequence(lastParagraphMatchEndIndex, wxSubSection.length()));
   }

   private void processParagraph(String paragraph) {
      boolean outlineNumberAndNameDetected = false;

      // search for <w:r> containing extra meta data
      Matcher wordRunMatcher = wordRunRegex.matcher(paragraph);
      boolean foundSplitArea = false;
      int indexOfWrPreceedingTheSplit = -1;

      while (wordRunMatcher.find()) {

         String singleWordRun = wordRunMatcher.group();

         boolean boldHint = singleWordRun.contains("<w:b/>");
         boolean underlineHint = singleWordRun.contains("<w:b-cs/>");

         if (boldHint && underlineHint) {
            outlineNumberAndNameDetected = true;
            indexOfWrPreceedingTheSplit = wordRunMatcher.end();
         }

         if (!boldHint && !underlineHint && outlineNumberAndNameDetected) {
            foundSplitArea = true;
            break;
         }
      }

      if (indexOfWrPreceedingTheSplit != -1 && foundSplitArea) {
         //write everything from paragraph.at(0) to paragraph.at(indexOfWrPreceedingTheSplit)...
         writeToBuffer(paragraph.subSequence(0, indexOfWrPreceedingTheSplit));
         //terminate wp, start new wp
         writeToBuffer(TERMINATING_WP_XML);
         writeToBuffer(DUMMY_PARAGRAPH_BEGINNING);
         //write remainder of paragraph
         writeToBuffer(cleanUpWtFromChunk(paragraph.subSequence(indexOfWrPreceedingTheSplit, paragraph.length())));
      } else {
         writeToBuffer(paragraph);
      }
   }

   /**
    * Grabs only the first <w:r><w:t> combination and looks for a dot. When found removes the dot
    */
   private StringBuilder cleanUpWtFromChunk(CharSequence chunk) {
      StringBuilder tempBuffer = new StringBuilder(chunk.length());
      Matcher wordRunMatcher = wordRunRegex.matcher(chunk);
      if (wordRunMatcher.find()) {
         tempBuffer.append(chunk.subSequence(0, wordRunMatcher.start()));
         Matcher wordTextMatcher = wordTextRegex.matcher(wordRunMatcher.group());
         if (wordTextMatcher.find()) {
            String text = wordTextMatcher.group(1);
            if (text.startsWith(".") && text.trim().length() > 1) {
               tempBuffer.append("<w:r><w:t>");
               tempBuffer.append(text.substring(1, text.length()));
               tempBuffer.append("</w:t></w:r>");
            } else if (text.trim().length() > 1) {
               tempBuffer.append("<w:r><w:t>");
               tempBuffer.append(text);
               tempBuffer.append("</w:t></w:r>");
            }
            tempBuffer.append(chunk.subSequence(wordRunMatcher.end(), chunk.length()));
         }
      }
      return tempBuffer;
   }

   private void writeToBuffer(CharSequence subSequence) {
      if (modifiedText != null) {
         modifiedText.append(subSequence);
      } else {
         System.out.println("StringBuilder modifiedText is null! Ignoring write...");
      }
   }
}
