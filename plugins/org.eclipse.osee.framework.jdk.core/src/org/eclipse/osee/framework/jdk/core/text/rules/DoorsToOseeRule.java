/*********************************************************************
 * Copyright (c) 2011 Boeing
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
 * Converts Doors exported Word documents to format friendlier to OSEE.
 *
 * @author Karol M. Wilk
 */
public final class DoorsToOseeRule extends Rule {

   private enum DoorsItemEnum {
      OBJECT_NUMBER,
      OBJECT_SHORT_TEXT_DASHED, //will be converted to regular paragraphs
      OBJECT_SHORT_TEXT_NON_DASHED, //will be deleted
      OBJECT_BODY,
      NONE
   }

   private final Pattern bodyRegex = Pattern.compile("<w:body>(.*?)</w:body>", Pattern.DOTALL);
   private final Pattern paragraphRegex = Pattern.compile("<w:p[ >].*?</w:p>", Pattern.DOTALL);
   private final Pattern objectNumberParagraphRegex =
      Pattern.compile("<w:t>.*?((?>\\d+\\.)+\\d+-\\d+(?>\\.\\d+-\\d+)*).*</w:t>", Pattern.DOTALL | Pattern.MULTILINE);
   private final Pattern regularObjectNumberParagraphRegex =
      Pattern.compile("<w:t>.*?Object Number\\s*.*:\\s+(.*?)</w:t>", Pattern.DOTALL | Pattern.MULTILINE);
   private final Pattern objectShortTextParagraphRegex =
      Pattern.compile("<w:p[ >].*?Object Short Text.*?<w:t>\\s+:\\s+(.*?)</w:t>.*?</w:p>", Pattern.DOTALL);
   private final Pattern headingParagraphRegex = Pattern.compile(
      "<w:p[ >].*?<w:pStyle w:val=\"Heading\\d\"/>.*?<w:t>(\\d+\\s+.*?)</w:t>.*?</w:p>", Pattern.DOTALL);

   private StringBuilder modifiedText;

   public DoorsToOseeRule() {
      super("_converted.xml");
   }

   @Override
   public ChangeSet computeChanges(CharSequence entireFile) {
      modifiedText = new StringBuilder(entireFile.length() * 2);

      Matcher bodySectionMatcher = bodyRegex.matcher(entireFile);
      if (bodySectionMatcher.find()) {
         write(entireFile.subSequence(0, bodySectionMatcher.start()));
         write("\n<w:body>");

         processText(bodySectionMatcher.group(1));

         write("\n</w:body>");
         write("\n</w:wordDocument>");
      }
      return decideOnResult(modifiedText, entireFile);
   }

   private void processText(CharSequence text) {
      Matcher paragraphMatcher = paragraphRegex.matcher(text);

      DoorsItemEnum currentDoorsItem = DoorsItemEnum.OBJECT_NUMBER;

      boolean startedSectionProcessing = true;
      int startIndexOfLastParagraph = -1;
      int endIndexOfLastParagraph = -1;

      while (paragraphMatcher.find()) {

         if (startedSectionProcessing) {
            startedSectionProcessing = false;
            write(text.subSequence(0, paragraphMatcher.start()));
         } else if (startIndexOfLastParagraph != -1 && endIndexOfLastParagraph != -1) {
            write(text.subSequence(endIndexOfLastParagraph, paragraphMatcher.start()));
         }

         DoorsItemEnum prevDoorsItem = currentDoorsItem;
         currentDoorsItem = processParagraph(paragraphMatcher.group(), currentDoorsItem);

         startIndexOfLastParagraph = paragraphMatcher.start();
         endIndexOfLastParagraph = paragraphMatcher.end();

         if (prevDoorsItem == currentDoorsItem) {
            write("\n" + paragraphMatcher.group());
         }
      }
      write(text.subSequence(endIndexOfLastParagraph, text.length()));
   }

   private DoorsItemEnum processParagraph(String paragraph, DoorsItemEnum nextDoorsItem) {
      switch (nextDoorsItem) {

         case OBJECT_NUMBER:
            Matcher objectNumberMatcher = objectNumberParagraphRegex.matcher(paragraph);
            if (objectNumberMatcher.find()) {

               String updatedObjectNumber = objectNumberMatcher.group(1).replace("-", ".");

               System.out.print("\t" + objectNumberMatcher.group(1) + " --> " + updatedObjectNumber);

               write("\n\t<w:p wsp:rsidR=\"007916EC\" wsp:rsidRDefault=\"007916EC\" wsp:rsidP=\"007916EC\">");

               write("<w:pPr>");
               write("<w:spacing w:before=\"48\" w:line=\"273\" w:line-rule=\"at-least\"/>");
               write("</w:pPr>");

               write("\n\t<w:r>");
               write("<w:rPr>");
               write("<w:b/>");
               write("<w:b-cs/>");
               write("</w:rPr>");
               write(String.format("\n<w:t>%s  </w:t>", updatedObjectNumber));
               write("\n\t</w:r>");

               nextDoorsItem = DoorsItemEnum.OBJECT_SHORT_TEXT_DASHED;
            } else {
               //Attempt to remove unnecessary Object Number nodes
               Matcher regularParagraphMatcher = regularObjectNumberParagraphRegex.matcher(paragraph);
               if (regularParagraphMatcher.find()) {

                  nextDoorsItem = DoorsItemEnum.OBJECT_SHORT_TEXT_NON_DASHED;
                  System.out.print("- \t\t" + regularParagraphMatcher.group(1));
               }
            }
            break;

         case OBJECT_SHORT_TEXT_DASHED: {
            Matcher objectShortTextMatcher = objectShortTextParagraphRegex.matcher(paragraph);
            if (objectShortTextMatcher.find()) {

               System.out.print(" " + objectShortTextMatcher.group(1) + "\n");

               write("\n\t<w:r>");
               write("\n\t<w:rPr>");
               write("\n\t<w:b/>");
               write("\n\t<w:b-cs/>");
               write("\n\t<w:u w:val=\"single\"/>");
               write("\n\t</w:rPr>");
               write(String.format("\n\t<w:t>%s</w:t>", objectShortTextMatcher.group(1)));
               write("\n\t</w:r>");
               write("\n\t</w:p>");

               nextDoorsItem = DoorsItemEnum.OBJECT_BODY;
            }
         }
            break;

         case OBJECT_SHORT_TEXT_NON_DASHED:
            Matcher objectShortTextMatcher = objectShortTextParagraphRegex.matcher(paragraph);
            if (objectShortTextMatcher.find()) {

               System.out.print(" " + objectShortTextMatcher.group(1) + "\n");

               nextDoorsItem = DoorsItemEnum.OBJECT_BODY;
            }
            break;

         case OBJECT_BODY:
            //detect body or heading paragraph that need to be adjusted...
            Matcher headingParagraphMatcher = headingParagraphRegex.matcher(paragraph);
            if (headingParagraphMatcher.find()) {
               System.out.print("\t" + headingParagraphMatcher.group(1));

               write(paragraph.subSequence(0, headingParagraphMatcher.start(1)));

               String wt = headingParagraphMatcher.group(1);

               StringBuilder resultingWt = new StringBuilder(wt.length() * 2 / 3);
               //Split at " "
               int indexOfFirstSpace = wt.indexOf(" ");

               resultingWt.append(wt.subSequence(0, indexOfFirstSpace));
               resultingWt.append(".0");
               resultingWt.append(wt.subSequence(indexOfFirstSpace, wt.length()));

               System.out.print(" --> " + resultingWt + "\n");
               write(resultingWt);

               write(paragraph.subSequence(headingParagraphMatcher.end(1), paragraph.length()));
            } else {
               write(paragraph);
            }
            nextDoorsItem = DoorsItemEnum.OBJECT_NUMBER;
            break;
         case NONE:
         default:
            break;
      }
      return nextDoorsItem;
   }

   private void write(CharSequence subSequence) {
      modifiedText.append(subSequence);
   }

   private ChangeSet decideOnResult(StringBuilder modifiedFile, CharSequence entireFile) {
      boolean insertModified = modifiedText.length() > 0;
      setRuleWasApplicable(insertModified);
      return new ChangeSet(insertModified ? modifiedText : entireFile);
   }
}
