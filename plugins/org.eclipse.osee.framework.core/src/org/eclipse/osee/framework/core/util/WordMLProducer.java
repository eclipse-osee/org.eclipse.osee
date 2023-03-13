/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.core.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.xml.XmlEncoderDecoder;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Ryan D. Brooks
 * @author Loren K. Ashley
 */

public class WordMLProducer {

   /**
    * Generated Word ML is appended to this {@link Appendable}.
    */

   private final Appendable appendable;

   /**
    * Counts the number of outline levels the maximum ({@link WordCoreUtil#OUTLINE_LEVEL_MAXIMUM}) has been exceeded by.
    * This member is incremented in {@link #startOutlineSubSection}. It is decremented in {@link #endOutlineSubSection}.
    * <p>
    * TODO: This member is not accounted for in {@link #setNextParagraphNumberTo}.
    */

   private int flattenedLevelCount;

   /**
    * The maximum number of outline levels to use. This value cannot exceed {@link WordCoreUtil#OUTLINE_LEVEL_MAXIMUM}.
    */

   private int maxOutlineLevel;

   /**
    * Tracks the current outlining depth. This member is incremented by {@link #startOutlineSubSection}. It is
    * decremented by {@link #endOutlineSubSection}. It is set in {@link #setNextParagraphNumberTo}.
    */

   private int outlineLevel;

   /**
    * Word supports outline levels 1 to 9. This array is indexed using the Word outline level, index 0 is not used.
    */

   private final int[] outlineNumber;

   /**
    * Creates a new {@link WordMLProducer} and initializes the outlining level to 1 with a sequence number of 0.
    *
    * @param appendable the {@link Appendable} the produced Word ML is to be appended to.
    * @throws NullPointerException when <code>appendable</code> is <code>null</code>.
    */

   public WordMLProducer(Appendable appendable) {
      this.appendable =
         Objects.requireNonNull(appendable, "WordMLProducer::new, parameter \"appendable\" cannot be null.");
      this.flattenedLevelCount = 0;
      this.maxOutlineLevel = WordCoreUtil.OUTLINE_LEVEL_MAXIMUM;
      this.outlineLevel = 0;
      this.outlineNumber = new int[WordCoreUtil.OUTLINE_LEVEL_MAXIMUM + 1];
   }

   public void addBold() {
      this.append(WordCoreUtil.BOLD);
   }

   public void addEditParagraphNoEscape(CharSequence text) {
      startParagraph();
      append(text);
      endParagraph();
   }

   public void addErrorRow(CharSequence id, CharSequence name, CharSequence type, CharSequence description) {
      this.addTableRow(id, name, type, description);
   }

   public void addNoProof() {
      this.append(WordCoreUtil.NO_PROOF);
   }

   public void addOleData(CharSequence oleData) {
      append("<w:docOleData>");
      append(oleData);
      append("</w:docOleData>");
   }

   public void addPageMargins() {
      this.append(WordCoreUtil.PAGE_MARGINS);
   }

   public void addPageSize(WordCoreUtil.pageType pageType) {
      this.append(pageType.getPageSize());
   }

   public void addParagraph(CharSequence text) {
      startParagraph();
      addTextInsideParagraph(text);
      endParagraph();
   }

   public void addParagraphBold(CharSequence text) {
      this.startParagraph();
      this.startRun();
      this.startRunPresentation();
      this.addBold();
      this.endRunPresentation();
      this.startText();
      this.appendEscaped(text);
      this.endText();
      this.startRunPresentation();
      this.addBold();
      this.endRunPresentation();
      this.endRun();
      this.endParagraph();
   }

   public void addParagraphNoEscape(CharSequence text) {
      this.startParagraph();
      this.startRun();
      this.startText();
      this.append(text);
      this.endText();
      this.endRun();
      this.endParagraph();
   }

   private void addParagraphStyle(CharSequence style) {
      this.append(WordCoreUtil.PARAGRAPH_STYLE_TEMPLATE_PART_A);
      this.append(style);
      this.append(WordCoreUtil.PARAGRAPH_STYLE_TEMPLATE_PART_B);
   }

   private void addParagraphStyle(CharSequence style, int outlineLevel) {
      this.append(WordCoreUtil.PARAGRAPH_STYLE_TEMPLATE_PART_A);
      this.append(style);
      this.append(Integer.toString(outlineLevel));
      this.append(WordCoreUtil.PARAGRAPH_STYLE_TEMPLATE_PART_B);
   }

   public void addRunWithTextNoEscape(CharSequence text) {
      this.startRun();
      this.startText();
      this.append(text);
      this.endText();
      this.endRun();
   }

   public void addRunWithTextNoEscape(CharSequence... texts) {

      if (Objects.isNull(texts) || texts.length == 0) {
         return;
      }

      this.startRun();
      this.startText();

      for (var text : texts) {
         this.append(text);
      }

      this.endText();
      this.endRun();
   }

   public void addTableCaption(CharSequence captionText) {

      this.append("<w:p wsp:rsidR=\"003571A9\" wsp:rsidRDefault=\"00AE7B3F\" wsp:rsidP=\"00AE7B3F\">");
      this.startParagraphPresentation();
      this.addParagraphStyle("Caption");
      this.endParagraphPresentation();
      this.addRunWithTextNoEscape("Table ");
      this.append("<w:fldSimple w:instr=\" SEQ Table \\* ARABIC \">");
      this.startRun();
      this.startRunPresentation();
      this.addNoProof();
      this.endRunPresentation();
      this.startText();
      this.append("#");
      this.endText();
      this.endRun();
      this.append("</w:fldSimple>");
      this.addRunWithTextNoEscape(": ", captionText);
      this.endParagraph();
   }

   public void addTableColumn(CharSequence text) {
      this.startTableColumn();
      this.addParagraph(text);
      this.endTableColumn();
   }

   public void addTableColumns(CharSequence... texts) {
      for (var text : texts) {
         this.addTableColumn(text);
      }
   }

   public void addTableColumnHeader(CharSequence text) {
      this.startTableColumn();
      this.addParagraphBold(text);
      this.endTableColumn();
   }

   public void addTableColumnHeaders(CharSequence... texts) {
      for (var text : texts) {
         this.addTableColumnHeader(text);
      }
   }

   public void addTableRow(CharSequence... texts) {
      this.startTableRow();
      this.addTableColumns(texts);
      this.endTableRow();
   }

   public void addTableHeaderRow(CharSequence... texts) {
      this.startTableRow();
      this.addTableColumnHeaders(texts);
      this.endTableRow();
   }

   public void addTablePresentation(WordCoreUtil.tablePresentation tablePresentation) {
      this.append(tablePresentation.get());
   }

   /**
    * Does the following:
    * <ul>
    * <li>XML Escapes the text.</li>
    * <li>Replaces vertical white space with Word Ml hard line breaks.</li>
    * <li>Wraps the text in a Word Ml run and text.</li>
    * <li>Appends to the {@link #appendable}.</li>
    * </ul>
    *
    * @param text the text to be appended.
    */

   public void addTextInsideParagraph(CharSequence text) {
      this.startRun();
      this.startText();
      this.appendEscaped(text);
      this.endText();
      this.endRun();
   }

   /**
    * Does the following:
    * <ul>
    * <li>XML Escapes the text.</li>
    * <li>Replaces vertical white space with Word Ml hard line breaks.</li>
    * <li>Wraps the text in a Word Ml run with a run presentation that sets the color.</li>
    * <li>Appends to the {@link #appendable}</li>
    * </ul>
    *
    * @param text the text to be appended.
    * @param rgbHexColor a 6 hex digit RGB color code.
    * @throws IllegalArgumentException when the parameter <code>rgbHexColor</code> is not a 6 character string composed
    * of the characters A-F, a-f, or 0-9.
    */

   public void addTextInsideParagraph(CharSequence text, CharSequence rgbHexColor) {

      var runPresentationWithColor = WordCoreUtil.getRunPresentationWithRgbHexColor(rgbHexColor);

      this.startRun();
      this.append(runPresentationWithColor);
      this.startText();
      this.appendEscaped(text);
      this.endText();
      this.endRun();
   }

   /**
    * Appends the provided text as it is to the {@link #appendable}.
    *
    * @param value the text to be appended.
    */

   public void addWordMl(CharSequence wordMl) {
      this.append(wordMl);
   }

   /**
    * Appends the provided text as it is to the {@link #appendable}.
    *
    * @param value the text to be appended.
    */

   protected void append(CharSequence value) {
      try {
         this.appendable.append(value);
      } catch (IOException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
   }

   /**
    * XML escapes the provided text, replaces vertical white space characters with Word ML hard line breaks, and appends
    * the result to the {@link #appendable}.
    *
    * @param value the text to be appended.
    */

   private void appendEscaped(CharSequence value) {
      var xmlEncodedText = XmlEncoderDecoder.textToXml(value);
      var xmlEncodedTextWithWordMlHardLineBreaks =
         WordCoreUtil.replaceVerticalWhitespaceCharactersWithWordMlHardLineBreaks(xmlEncodedText);
      this.append(xmlEncodedTextWithWordMlHardLineBreaks);
   }

   /**
    * Creates a Word ML paragraph with a hyperlink to a file and appends it to the {@link #appendable}.
    *
    * @param filename the name of the file for the hyperlink.
    * @throws IllegalArgumentException when the filename is <code>null</code> or empty.
    */

   public void createHyperLinkDoc(CharSequence filename) {

      var hyperlinkDocument = WordCoreUtil.getHyperlinkDocument(filename);

      this.append(hyperlinkDocument);
   }

   public void endAppendixSubSection() {
      this.endSubSection();
   }

   public void endErrorLog() {
      endTable();
      addTableCaption("Error Log");
      endAppendixSubSection();
      setPageBreak(true, 1, true);
   }

   public void endListPresentation() {
      this.append(WordCoreUtil.LIST_PRESENTATION_END);
   }

   public void endOutlineSubSection() {
      endOutlineSubSection(false);
   }

   private void endOutlineSubSection(boolean force) {
      if (!force && this.flattenedLevelCount > 0) {
         this.flattenedLevelCount--;
      } else {
         this.endSubSection();
         if (this.outlineLevel + 1 < this.outlineNumber.length) {
            this.outlineNumber[this.outlineLevel + 1] = 0;
         }
         this.outlineLevel--;
      }
   }

   public void endParagraph() {
      this.append(WordCoreUtil.PARAGRAPH_END);
   }

   public void endParagraphPresentation() {
      this.append(WordCoreUtil.PARAGRAPH_PRESENTATION_END);
   }

   public void endRun() {
      this.append(WordCoreUtil.RUN_END);
   }

   public void endRunPresentation() {
      this.append(WordCoreUtil.RUN_PRESENTATION_END);
   }

   public void endSection() {
      this.append(WordCoreUtil.SECTION_END);
   }

   public void endSectionPresentation() {
      this.append(WordCoreUtil.SECTION_PRESENTATION_END);
   }

   public void endSubSection() {
      this.append(WordCoreUtil.SUBSECTION_END);
   }

   public void endTable() {
      this.append(WordCoreUtil.TABLE_END);
      this.append(WordCoreUtil.SUBSECTION_END);
   }

   public void endTableColumn() {
      this.append(WordCoreUtil.TABLE_COLUMN_END);
   }

   public void endTableRow() {
      this.append(WordCoreUtil.TABLE_ROW_END);
   }

   public void endText() {
      this.append(WordCoreUtil.TEXT_END);
   }

   private CharSequence getOutlineNumber() {
      StringBuilder strB = new StringBuilder();
      for (int i = 1; i < this.outlineLevel; i++) {
         strB.append(String.valueOf(this.outlineNumber[i]));
         strB.append(".");
      }
      strB.append(String.valueOf(this.outlineNumber[this.outlineLevel]));
      return strB;
   }

   public boolean okToStartSubsection() {
      return this.outlineLevel < this.maxOutlineLevel;
   }

   public void resetListValue() {
      // extra paragraph needed to support WORD's bug to add in a trailing zero when using field codes
      this.startParagraph();
      this.append(WordCoreUtil.LIST_NUMBER_FIELD_PARAGRAPH_PRESENTATION);
      this.endParagraph();

      //The listnum also acts a template delimiter to know when to remove unwanted content.
      this.startParagraph();
      this.append(WordCoreUtil.LIST_NUMBER_FIELD);
      this.endParagraph();
   }

   public void setMaxOutlineLevel(int maxOutlineLevel) {
      //@formatter:off
      this.maxOutlineLevel = maxOutlineLevel <= WordCoreUtil.OUTLINE_LEVEL_MAXIMUM
                                ? maxOutlineLevel
                                : WordCoreUtil.OUTLINE_LEVEL_MAXIMUM;
      //@formatter:on
   }

   public void setNextParagraphNumberTo(String outlineNumber) {

      var nextOutlineNumbers = outlineNumber.split("\\.");

      if (nextOutlineNumbers.length > WordCoreUtil.OUTLINE_LEVEL_MAXIMUM) {
         nextOutlineNumbers = Arrays.copyOf(nextOutlineNumbers, WordCoreUtil.OUTLINE_LEVEL_MAXIMUM);
      }

      Arrays.fill(this.outlineNumber, 0);

      try {
         for (int i = 0; i < nextOutlineNumbers.length; i++) {

            this.outlineNumber[i + 1] = Integer.parseInt(nextOutlineNumbers[i]);
         }
         this.outlineNumber[nextOutlineNumbers.length]--;
         this.outlineLevel = nextOutlineNumbers.length - 1;
      } catch (NumberFormatException ex) {
         //Do nothing
      }
   }

   public void setPageBreak(boolean chapterNumbering, int chapterStyle, boolean restartNumbering) {
      // Default to no page layout style which will stay with portrait
      setPageBreak(chapterNumbering, chapterStyle, restartNumbering, WordCoreUtil.pageType.PORTRAIT);
   }

   /**
    * @param chapterNumbering - Whether or not chapter number (1-1) will be applied
    * @param chapterStyle = Which style to use (1-1, 1.1-1, 1.2.3-1 etc)
    * @param restartNumbering - Restart the numbering from the previous section
    * @param pageType - Set to landscape if needed
    */
   public void setPageBreak(boolean chapterNumbering, int chapterStyle, boolean restartNumbering, WordCoreUtil.pageType pageType) {

      this.startParagraph();
      this.startParagraphPresentation();
      this.startSectionPresentation();
      this.addPageSize(pageType);
      this.addPageMargins();
      if (chapterNumbering) {
         this.append("<w:pgNumType ");
         if (restartNumbering) {
            this.append("w:start=\"1\" ");
         }
         this.append("w:chap-style=\"");
         this.append(Integer.toString(chapterStyle));
         this.append("\"/>");
      }
      this.endSectionPresentation();
      this.endParagraphPresentation();
      this.endParagraph();
   }

   /**
    * Sets the page layout to either portrait/landscape depending on the artifacts pageType attribute value. Note: This
    * call should be done after processing each artifact so if a previous artifact was landscaped the following artifact
    * would be set back to portrait.
    */
   public void setPageLayout(WordCoreUtil.pageType pageType) {

      if (WordCoreUtil.pageType.LANDSCAPE.equals(pageType)) {
         this.startParagraph();
         this.startParagraphPresentation();
         this.startSectionPresentation();
         this.addPageSize(pageType);
         this.endSectionPresentation();
         this.endParagraphPresentation();
         this.endParagraph();
      }
   }

   public void startAppendixSubSection(CharSequence style, CharSequence headingText) {
      this.startSubSection();
      if (Strings.isValid(headingText)) {
         this.startParagraph();
         this.startParagraphPresentation();
         this.addParagraphStyle(style);
         this.endParagraphPresentation();
         this.addTextInsideParagraph(headingText);
         this.endParagraph();
      }
   }

   public void startErrorLog() {
      this.startAppendixSubSection("Heading1", "Error Log");
      this.startTable();
      this.append(
         "<w:tblPr><w:tblW w:w=\"0\" w:type=\"auto\"/><w:tblBorders><w:top w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/><w:left w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/><w:bottom w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/><w:right w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/><w:insideH w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/><w:insideV w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/></w:tblBorders></w:tblPr>");
      this.addTableHeaderRow("Artifact Id", "Artifact Name", "Artifact Type", "Description");
   }

   public void startListPresentation() {
      this.append(WordCoreUtil.LIST_PRESENTATION);
   }

   public CharSequence startOutlineSubSection() {
      CharSequence paragraphNumber = startOutlineSubSection(WordCoreUtil.DEFAULT_FONT, null, null);
      return paragraphNumber;
   }

   public void startOutlineSubSection(CharSequence style, int outlineLevel, CharSequence outlineNumber, CharSequence font, CharSequence headingText) {
      this.startSubSection();
      if (Strings.isValid(headingText)) {
         this.startParagraph();
         this.startParagraphPresentation();
         this.addParagraphStyle(style, outlineLevel);
         this.startListPresentation();
         this.append("<wx:t wx:val=\"");
         this.append(outlineNumber);
         this.append("\" wx:wTabBefore=\"540\" wx:wTabAfter=\"90\"/><wx:font wx:val=\"");
         this.append(font);
         this.append("\"/>");
         this.endListPresentation();
         this.endParagraphPresentation();
         this.addTextInsideParagraph(headingText);
         this.endParagraph();
      }
   }

   public CharSequence startOutlineSubSection(CharSequence font, CharSequence headingText, CharSequence outlineType) {

      if (this.okToStartSubsection()) {

         this.outlineNumber[++this.outlineLevel]++;

         var paragraphNumber = this.getOutlineNumber();
         //@formatter:off
         this.startOutlineSubSection
            (
               Objects.nonNull( outlineType ) ? outlineType : "Heading",
               this.outlineLevel,
               paragraphNumber,
               font,
               headingText
            );
         //@formatter:on
         return paragraphNumber;

      } else {

         this.flattenedLevelCount++;

         this.endOutlineSubSection(true);

         OseeLog.log(this.getClass(), Level.WARNING,
            "Outline level flattened, max outline level is currently set to " + this.maxOutlineLevel + ", ms word only goes 9 levels deep");

         return this.startOutlineSubSection(font, headingText, outlineType);

      }
   }

   public void startParagraph() {
      this.append(WordCoreUtil.PARAGRAPH);
   }

   public void startParagraphPresentation() {
      this.append(WordCoreUtil.PARAGRAPH_PRESENTATION);
   }

   public void startRun() {
      this.append(WordCoreUtil.RUN);
   }

   public void startRunPresentation() {
      this.append(WordCoreUtil.RUN_PRESENTATION);
   }

   public void startSection() {
      this.append(WordCoreUtil.SECTION);
   }

   public void startSectionPresentation() {
      this.append(WordCoreUtil.SECTION_PRESENTATION);
   }

   public void startSubSection() {
      this.append(WordCoreUtil.SUBSECTION);
   }

   public void startTable() {
      this.append(WordCoreUtil.SUBSECTION);
      this.append(WordCoreUtil.TABLE);
   }

   public void startTableColumn() {
      this.append(WordCoreUtil.TABLE_COLUMN);
   }

   public void startTableRow() {
      this.append(WordCoreUtil.TABLE_ROW);
   }

   public void startText() {
      this.append(WordCoreUtil.TEXT);
   }

}
