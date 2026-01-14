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

package org.eclipse.osee.framework.core.publishing.markdown;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osee.framework.core.publishing.PublishingAppender;
import org.eclipse.osee.framework.core.publishing.PublishingAppenderBase;
import org.eclipse.osee.framework.core.publishing.PublishingArtifact;
import org.eclipse.osee.framework.core.publishing.WordCoreUtil.pageType;
import org.eclipse.osee.framework.core.publishing.WordCoreUtil.tablePresentation;
import org.eclipse.osee.framework.core.publishing.wordml.WordMlPublishingAppender;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * An implementation of the {@link PublishingAppender} interface for Markdown publishing.
 *
 * @author Loren K. Ashley
 */

public class MarkdownPublishingAppender extends PublishingAppenderBase {

   /**
    * Creates a new {@link WordMlPublishingAppender} and initializes the outlining level to 1 with a sequence number of
    * 0.
    *
    * @param appendable the {@link Appendable} the produced Word ML is to be appended to.
    * @throws NullPointerException when <code>appendable</code> is <code>null</code>.
    */

   public MarkdownPublishingAppender(Appendable appendable) {
      super(appendable);
   }

   @Override
   public void addBold() {
   }

   @Override
   public void addEditParagraphNoEscape(CharSequence text) {
   }

   @Override
   public void addErrorRow(CharSequence id, CharSequence name, CharSequence type, CharSequence description) {
   }

   @Override
   public void addNoProof() {
   }

   @Override
   public void addOleData(CharSequence oleData) {
   }

   @Override
   public void addPageMargins() {
   }

   @Override
   public void addPageSize(pageType pageType) {
   }

   @Override
   public void addParagraph(CharSequence text) {
   }

   @Override
   public void addParagraphBold(CharSequence text) {
   }

   @Override
   public void addParagraphNoEscape(CharSequence text) {
   }

   @Override
   public void addRunWithTextEscape(CharSequence text) {
   }

   @Override
   public void addRunWithTextEscape(CharSequence... texts) {
   }

   @Override
   public void addRunWithTextNoEscape(CharSequence text) {
   }

   @Override
   public void addRunWithTextNoEscape(CharSequence... texts) {
   }

   @Override
   public void addTableCaption(CharSequence captionText) {
   }

   @Override
   public void addTableColumn(CharSequence text) {
   }

   @Override
   public void addTableColumns(CharSequence... texts) {
   }

   @Override
   public void addTableColumnHeader(CharSequence text) {
   }

   @Override
   public void addTableColumnHeaders(CharSequence... texts) {
   }

   @Override
   public void addTableRow(CharSequence... texts) {
   }

   @Override
   public void addTableHeaderRow(CharSequence... texts) {
   }

   @Override
   public void addTablePresentation(tablePresentation tablePresentation) {
   }

   @Override
   public void addTextInsideParagraph(CharSequence text) {
   }

   @Override
   public void addTextInsideParagraph(CharSequence text, CharSequence rgbHexColor) {
   }

   @Override
   public void createHyperLinkDoc(CharSequence filename) {
   }

   @Override
   public void endAppendixSubSection() {
   }

   @Override
   public void endErrorLog() {
   }

   @Override
   public void endListPresentation() {
   }

   @Override
   public PublishingAppender endParagraph() {
      this.append("\n\n");
      return this;
   }

   @Override
   public void endParagraphPresentation() {
   }

   @Override
   public void endRun() {
   }

   @Override
   public void endRunPresentation() {
   }

   @Override
   public void endSection() {
   }

   @Override
   public void endSectionPresentation() {
   }

   @Override
   public void endSubSection() {
   }

   @Override
   public void endTable() {
   }

   @Override
   public void endTableColumn() {
   }

   @Override
   public void endTableRow() {
   }

   @Override
   public void endText() {
   }

   @Override
   public void resetListValue() {
   }

   @Override
   public void setPageBreak(boolean chapterNumbering, int chapterStyle, boolean restartNumbering) {
   }

   @Override
   public void setPageBreak(boolean chapterNumbering, int chapterStyle, boolean restartNumbering, pageType pageType) {
   }

   @Override
   public void setPageLayout(pageType pageType) {
   }

   @Override
   public void startAppendixSubSection(CharSequence style, CharSequence headingText) {
   }

   @Override
   public void startErrorLog() {
   }

   @Override
   public void startListPresentation() {
   }

   /**
    * A lookup array of hash mark strings used for markdown heading.
    */

   //@formatter:off
   private static String[] outlinePounds =
      new String[]
      {
         Strings.EMPTY_STRING,
         "#",
         "##",
         "###",
         "####",
         "#####",
         "######"
      };
   //@formatter:on

   /**
    * {@inheritDoc}
    * <p>
    * This implementation inserts a heading with a <code>headingLevel</code> up to level 6 (number of leading
    * &quot;#&quot;s) with the <code>headingNumber</code> and <code>headingText</code>.
    * <p>
    * The following parameters are ignored:
    * <ul>
    * <li><code>bookmark</code>,</li>
    * <li><code>outlineType</code>, and</li>
    * <li><code>font</code>.</li>
    * </ul>
    */

   @Override
   //@formatter:off
   public void
      startOutlineSubSection
         (
            @Nullable String[]     bookmark,
            @Nullable CharSequence headingNumber,
                      int          headingLevel,
            @Nullable CharSequence headingText,
            @Nullable CharSequence outlineType,
            @Nullable CharSequence font
         ) {
   //@formatter:on

      final var safeHeadingNumber = (headingNumber != null) ? headingNumber : Strings.EMPTY_STRING;
      final var safeHeadingText = (headingText != null) ? headingText : Strings.EMPTY_STRING;

      //@formatter:off
      final var safeHeadingLevel =
         ( headingLevel >= 1 )
            ? ( headingLevel < outlinePounds.length )
                 ? headingLevel
                 : outlinePounds.length - 1
            : outlinePounds.length - 1;

      this
         .append( "\n\n" )
         .append( MarkdownPublishingAppender.outlinePounds[safeHeadingLevel] )
         .append( " " )
         .append( safeHeadingNumber )
         .append( " " )
         .append( safeHeadingText )
         .append( "\n\n" )
         ;
      //@formatter:on
   }

   @Override
   public void endOutlineSubSection() {
   }

   @Override
   public PublishingAppender startParagraph() {
      return this;
   }

   @Override
   public void startParagraphPresentation() {
   }

   @Override
   public void startRun() {
   }

   @Override
   public void startRunPresentation() {
   }

   @Override
   public void startSection() {
   }

   @Override
   public void startSectionPresentation() {
   }

   @Override
   public void startSubSection() {
   }

   @Override
   public void startTable() {
   }

   @Override
   public void startTableColumn() {
   }

   @Override
   public void startTableRow() {
   }

   @Override
   public void startText() {
   }

   @Override
   public void appendLinkAnchor(PublishingArtifact artifact) {
      this.append("<a id=\"" + artifact.getIdString() + "\"></a>");
   }

   @Override
   public void endArtifact() {
      this.append("\n\n");
      this.append("___");
      this.append("\n\n");
   }
}
