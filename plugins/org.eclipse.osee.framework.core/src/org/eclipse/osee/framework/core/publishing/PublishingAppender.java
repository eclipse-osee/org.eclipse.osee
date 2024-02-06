/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.framework.core.publishing;

/**
 * Interface for publishing format specific appender.
 *
 * @implSpec Unimplemented methods for a publishing format should do nothing and not throw any exceptions.
 * @author Loren K. Ashley
 */

public interface PublishingAppender {

   public void addBold();

   public void addEditParagraphNoEscape(CharSequence text);

   public void addErrorRow(CharSequence id, CharSequence name, CharSequence type, CharSequence description);

   public void addNoProof();

   public void addOleData(CharSequence oleData);

   public void addPageMargins();

   public void addPageSize(WordCoreUtil.pageType pageType);

   public void addParagraph(CharSequence text);

   public void addParagraphBold(CharSequence text);

   public void addParagraphNoEscape(CharSequence text);

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

   public void addRunWithTextEscape(CharSequence text);

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

   public void addRunWithTextEscape(CharSequence... texts);

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

   public void addRunWithTextNoEscape(CharSequence text);

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

   public void addRunWithTextNoEscape(CharSequence... texts);

   public void addTableCaption(CharSequence captionText);

   public void addTableColumn(CharSequence text);

   public void addTableColumns(CharSequence... texts);

   public void addTableColumnHeader(CharSequence text);

   public void addTableColumnHeaders(CharSequence... texts);

   public void addTableRow(CharSequence... texts);

   public void addTableHeaderRow(CharSequence... texts);

   public void addTablePresentation(WordCoreUtil.tablePresentation tablePresentation);

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

   public void addTextInsideParagraph(CharSequence text);

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

   public void addTextInsideParagraph(CharSequence text, CharSequence rgbHexColor);

   /**
    * Appends the provided text as it is to the {@link #appendable}.
    *
    * @param value the text to be appended.
    */

   public PublishingAppender append(CharSequence value);

   /**
    * Creates a Word ML paragraph with a hyperlink to a file and appends it to the {@link #appendable}.
    *
    * @param filename the name of the file for the hyperlink.
    * @throws IllegalArgumentException when the filename is <code>null</code> or empty.
    */

   public void createHyperLinkDoc(CharSequence filename);

   public void endAppendixSubSection();

   public void endErrorLog();

   public void endListPresentation();

   public void endOutlineSubSection();

   public PublishingAppender endParagraph();

   public void endParagraphPresentation();

   public void endRun();

   public void endRunPresentation();

   public void endSection();

   public void endSectionPresentation();

   public void endSubSection();

   public void endTable();

   public void endTableColumn();

   public void endTableRow();

   public void endText();

   public boolean okToStartSubsection();

   public void resetListValue();

   public void setMaxOutlineLevel(int maxOutlineLevel);

   public void setNextParagraphNumberTo(String outlineNumber);

   public void setPageBreak(boolean chapterNumbering, int chapterStyle, boolean restartNumbering);

   /**
    * @param chapterNumbering - Whether or not chapter number (1-1) will be applied
    * @param chapterStyle = Which style to use (1-1, 1.1-1, 1.2.3-1 etc)
    * @param restartNumbering - Restart the numbering from the previous section
    * @param pageType - Set to landscape if needed
    */

   public void setPageBreak(boolean chapterNumbering, int chapterStyle, boolean restartNumbering, WordCoreUtil.pageType pageType);

   /**
    * Sets the page layout to either portrait/landscape depending on the artifacts pageType attribute value. Note: This
    * call should be done after processing each artifact so if a previous artifact was landscaped the following artifact
    * would be set back to portrait.
    */

   public void setPageLayout(WordCoreUtil.pageType pageType);

   public void startAppendixSubSection(CharSequence style, CharSequence headingText);

   public void startErrorLog();

   public void startListPresentation();

   public CharSequence startOutlineSubSection();

   public void startOutlineSubSection(CharSequence style, int outlineLevel, CharSequence outlineNumber, CharSequence font, CharSequence headingText);

   public CharSequence startOutlineSubSection(CharSequence font, CharSequence headingText, CharSequence outlineType);

   public PublishingAppender startParagraph();

   public void startParagraphPresentation();

   public void startRun();

   public void startRunPresentation();

   public void startSection();

   public void startSectionPresentation();

   public void startSubSection();

   public void startTable();

   public void startTableColumn();

   public void startTableRow();

   public void startText();

}

/* EOF */
