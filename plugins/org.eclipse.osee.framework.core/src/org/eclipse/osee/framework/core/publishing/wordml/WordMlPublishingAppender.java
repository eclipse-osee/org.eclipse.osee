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

package org.eclipse.osee.framework.core.publishing.wordml;

import java.util.Objects;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osee.framework.core.publishing.IncludeBookmark;
import org.eclipse.osee.framework.core.publishing.PublishingAppender;
import org.eclipse.osee.framework.core.publishing.PublishingAppenderBase;
import org.eclipse.osee.framework.core.publishing.PublishingArtifact;
import org.eclipse.osee.framework.core.publishing.WordCoreUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.xml.XmlEncoderDecoder;

/**
 * An implemenation of the {@link PublishingAppender} interface for Word Markup Language publishing.
 *
 * @author Ryan D. Brooks
 * @author Loren K. Ashley
 */

public class WordMlPublishingAppender extends PublishingAppenderBase {

   /**
    * Creates a new {@link WordMlPublishingAppender} and initializes the outlining level to 1 with a sequence number of
    * 0.
    *
    * @param appendable the {@link Appendable} the produced Word ML is to be appended to.
    * @throws NullPointerException when <code>appendable</code> is <code>null</code>.
    */

   public WordMlPublishingAppender(Appendable appendable) {
      super(appendable);
   }

   @Override
   public void addBold() {
      this.append(WordCoreUtil.BOLD);
   }

   @Override
   public void addEditParagraphNoEscape(CharSequence text) {
      startParagraph();
      append(text);
      endParagraph();
   }

   @Override
   public void addErrorRow(CharSequence id, CharSequence name, CharSequence type, CharSequence description) {
      this.addTableRow(id, name, type, description);
   }

   @Override
   public void addNoProof() {
      this.append(WordCoreUtil.NO_PROOF);
   }

   @Override
   public void addOleData(CharSequence oleData) {
      append("<w:docOleData>");
      append(oleData);
      append("</w:docOleData>");
   }

   @Override
   public void addPageMargins() {
      this.append(WordCoreUtil.PAGE_MARGINS);
   }

   @Override
   public void addPageSize(WordCoreUtil.pageType pageType) {
      this.append(pageType.getPageSize());
   }

   @Override
   public void addParagraph(CharSequence text) {
      startParagraph();
      addTextInsideParagraph(text);
      endParagraph();
   }

   @Override
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

   @Override
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

   /**
    * Appends the following to the Word ML output:
    * <ul>
    * <li>&lt;w:r&gt;&lt;w:t&gt;</li>
    * <li>XML escaped <code>text</code></li>
    * <li>&lt;/w:t&gt;&lt;/w:r&gt;</li>
    * </ul>
    *
    * @param text the text for the run.
    */

   @Override
   public void addRunWithTextEscape(CharSequence text) {

      var xmlEncodedText = XmlEncoderDecoder.textToXml(text);

      this.startRun();
      this.startText();
      this.append(xmlEncodedText);
      this.endText();
      this.endRun();
   }

   /**
    * Appends the following to the Word ML output:
    * <ul>
    * <li>&lt;w:r&gt;&lt;w:t&gt;</li>
    * <li>The concatenation of each element of the <code>texts</code> array after being XML escaped.</li>
    * <li>&lt;/w:t&gt;&lt;/w:r&gt;</li>
    * </ul>
    *
    * @param texts an array of the text segments for the run.
    */

   @Override
   public void addRunWithTextEscape(CharSequence... texts) {

      if (Objects.isNull(texts) || texts.length == 0) {
         return;
      }

      this.startRun();
      this.startText();

      for (var text : texts) {
         var xmlEncodedText = XmlEncoderDecoder.textToXml(text);
         this.append(xmlEncodedText);
      }

      this.endText();
      this.endRun();
   }

   /**
    * Appends the following to the Word ML output:
    * <ul>
    * <li>&lt;w:r&gt;&lt;w:t&gt;</li>
    * <li><code>text</code></li>
    * <li>&lt;/w:t&gt;&lt;/w:r&gt;</li>
    * </ul>
    *
    * @param text the text for the run.
    */

   @Override
   public void addRunWithTextNoEscape(CharSequence text) {
      this.startRun();
      this.startText();
      this.append(text);
      this.endText();
      this.endRun();
   }

   /**
    * Appends the following to the Word ML output:
    * <ul>
    * <li>&lt;w:r&gt;&lt;w:t&gt;</li>
    * <li>The concatenation of each element of the <code>texts</code> array.</li>
    * <li>&lt;/w:t&gt;&lt;/w:r&gt;</li>
    * </ul>
    *
    * @param texts an array of the text segments for the run.
    */

   @Override
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

   @Override
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

   @Override
   public void addTableColumn(CharSequence text) {
      this.startTableColumn();
      this.addParagraph(text);
      this.endTableColumn();
   }

   @Override
   public void addTableColumns(CharSequence... texts) {
      for (var text : texts) {
         this.addTableColumn(text);
      }
   }

   @Override
   public void addTableColumnHeader(CharSequence text) {
      this.startTableColumn();
      this.addParagraphBold(text);
      this.endTableColumn();
   }

   @Override
   public void addTableColumnHeaders(CharSequence... texts) {
      for (var text : texts) {
         this.addTableColumnHeader(text);
      }
   }

   @Override
   public void addTableRow(CharSequence... texts) {
      this.startTableRow();
      this.addTableColumns(texts);
      this.endTableRow();
   }

   @Override
   public void addTableHeaderRow(CharSequence... texts) {
      this.startTableRow();
      this.addTableColumnHeaders(texts);
      this.endTableRow();
   }

   @Override
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

   @Override
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

   @Override
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

   @Override
   public void createHyperLinkDoc(CharSequence filename) {

      var hyperlinkDocument = WordCoreUtil.getHyperlinkDocument(filename);

      this.append(hyperlinkDocument);
   }

   @Override
   public void endAppendixSubSection() {
      this.endSubSection();
   }

   @Override
   public void endErrorLog() {
      endTable();
      addTableCaption("Error Log");
      endAppendixSubSection();
      setPageBreak(true, 1, true);
   }

   @Override
   public void endListPresentation() {
      this.append(WordCoreUtil.LIST_PRESENTATION_END);
   }

   @Override
   public PublishingAppender endParagraph() {
      this.append(WordCoreUtil.PARAGRAPH_END);
      return this;
   }

   @Override
   public void endParagraphPresentation() {
      this.append(WordCoreUtil.PARAGRAPH_PRESENTATION_END);
   }

   @Override
   public void endRun() {
      this.append(WordCoreUtil.RUN_END);
   }

   @Override
   public void endRunPresentation() {
      this.append(WordCoreUtil.RUN_PRESENTATION_END);
   }

   @Override
   public void endSection() {
      this.append(WordCoreUtil.SECTION_END);
   }

   @Override
   public void endSectionPresentation() {
      this.append(WordCoreUtil.SECTION_PRESENTATION_END);
   }

   @Override
   public void endSubSection() {
      this.append(WordCoreUtil.SUBSECTION_END);
   }

   @Override
   public void endTable() {
      this.append(WordCoreUtil.TABLE_END);
      this.endSubSection();
   }

   @Override
   public void endTableColumn() {
      this.append(WordCoreUtil.TABLE_COLUMN_END);
   }

   @Override
   public void endTableRow() {
      this.append(WordCoreUtil.TABLE_ROW_END);
   }

   @Override
   public void endText() {
      this.append(WordCoreUtil.TEXT_END);
   }

   @Override
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

   @Override
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
   @Override
   public void setPageBreak(boolean chapterNumbering, int chapterStyle, boolean restartNumbering,
      WordCoreUtil.pageType pageType) {

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
   @Override
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

   @Override
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

   @Override
   public void startErrorLog() {
      this.startAppendixSubSection("Heading1", "Error Log");
      this.startTable();
      this.append(
         "<w:tblPr><w:tblW w:w=\"0\" w:type=\"auto\"/><w:tblBorders><w:top w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/><w:left w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/><w:bottom w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/><w:right w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/><w:insideH w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/><w:insideV w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/></w:tblBorders></w:tblPr>");
      this.addTableHeaderRow("Artifact Id", "Artifact Name", "Artifact Type", "Description");
   }

   @Override
   public void startListPresentation() {
      this.append(WordCoreUtil.LIST_PRESENTATION);
   }

   /**
    * {@inheritDoc}
    * <p>
    * Appends the Word ML for a heading to the output {@link Appendable}.
    */

   @Override
   //@formatter:off
   public void
      startOutlineSubSection
         (
            @Nullable String[]        bookmark,
            @Nullable CharSequence    headingNumber,
                      int             headingLevel,
            @Nullable CharSequence    headingText,
            @Nullable CharSequence    outlineType,
            @Nullable CharSequence    font
         ) {
      //@formatter:on

      String openBookmark;
      String closeBookmark;
      IncludeBookmark includeBookmark;

      //@formatter:off
      if(    ( bookmark != null )
          && ( bookmark.length == 2 )
          && Strings.isValidAndNonBlank( bookmark[0] )
          && Strings.isValidAndNonBlank( bookmark[1] ) ) {
      //@formatter:on

         includeBookmark = IncludeBookmark.YES;
         openBookmark = bookmark[0];
         closeBookmark = bookmark[1];

      } else {

         includeBookmark = IncludeBookmark.NO;
         openBookmark = Strings.EMPTY_STRING;
         closeBookmark = Strings.EMPTY_STRING;

      }

      this.startSubSection();

      if (includeBookmark.isYes()) {
         this.append(openBookmark);
      }

      if (Strings.isValid(headingText) && Strings.isValid(headingNumber)) {

         var safeFont = Strings.isValidAndNonBlank(font) ? font : WordCoreUtil.DEFAULT_FONT;
         var safeOutlineType = Strings.isValidAndNonBlank(outlineType) ? outlineType : "Heading";

         this.startParagraph();
         this.startParagraphPresentation();
         this.addParagraphStyle(safeOutlineType, headingLevel);
         this.startListPresentation();
         this.append("<wx:t wx:val=\"");
         this.append(headingNumber);
         this.append("\" wx:wTabBefore=\"540\" wx:wTabAfter=\"90\"/><wx:font wx:val=\"");
         this.append(safeFont);
         this.append("\"/>");
         this.endListPresentation();
         this.endParagraphPresentation();
         this.addTextInsideParagraph(headingText);
         this.endParagraph();
      }

      if (includeBookmark.isYes()) {
         this.append(closeBookmark);
      }

   }

   @Override
   public void endOutlineSubSection() {
      this.endSubSection();
   }

   @Override
   public PublishingAppender startParagraph() {
      this.append(WordCoreUtil.PARAGRAPH);
      return this;
   }

   @Override
   public void startParagraphPresentation() {
      this.append(WordCoreUtil.PARAGRAPH_PRESENTATION);
   }

   @Override
   public void startRun() {
      this.append(WordCoreUtil.RUN);
   }

   @Override
   public void startRunPresentation() {
      this.append(WordCoreUtil.RUN_PRESENTATION);
   }

   @Override
   public void startSection() {
      this.append(WordCoreUtil.SECTION);
   }

   @Override
   public void startSectionPresentation() {
      this.append(WordCoreUtil.SECTION_PRESENTATION);
   }

   @Override
   public void startSubSection() {
      this.append(WordCoreUtil.SUBSECTION);
   }

   @Override
   public void startTable() {
      this.startSubSection();
      this.append(WordCoreUtil.TABLE);
   }

   @Override
   public void startTableColumn() {
      this.append(WordCoreUtil.TABLE_COLUMN);
   }

   @Override
   public void startTableRow() {
      this.append(WordCoreUtil.TABLE_ROW);
   }

   @Override
   public void startText() {
      this.append(WordCoreUtil.TEXT);
   }

   @Override
   public void appendLinkAnchor(PublishingArtifact artifact) {
      // This is not applicable to a word publish.
   }

   @Override
   public void endArtifact() {
   }

}
