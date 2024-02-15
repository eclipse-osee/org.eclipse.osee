/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.framework.core.xml.publishing;

import java.util.Optional;

/**
 * Class to encapsulate a list of the top level Word Paragraphs in the sub-section of a Word ML document.
 *
 * @author Loren K. Ashley
 */

public class WordParagraphList<P extends AbstractElement> extends AbstractElementList<P, WordParagraph> {

   /**
    * {@link WordElementParserFactory} instance for creating a {@link ParagraphList} with a {@link WordSection} parent.
    */

   public static WordElementParserFactory<WordDocument, WordParagraphList<WordDocument>, WordParagraph> wordDocumentParentFactory =
      new WordElementParserFactory<>(WordParagraphList::new, WordParagraph::new, WordMlTag.PARAGRAPH);

   /**
    * {@link WordElementParserFactory} instance for creating a {@link ParagraphList} with a {@link WordSection} parent.
    */

   public static WordElementParserFactory<WordSection, WordParagraphList<WordSection>, WordParagraph> wordSectionParentFactory =
      new WordElementParserFactory<>(WordParagraphList::new, WordParagraph::new, WordMlTag.PARAGRAPH);

   /**
    * {@link WordElementParserFactory} instance for creating a {@link ParagraphList} with a {@link AuxHintSubSection}
    * parent.
    */

   public static WordElementParserFactory<AuxHintSubSection, WordParagraphList<AuxHintSubSection>, WordParagraph> wordSubSectionParentFactory =
      new WordElementParserFactory<>(WordParagraphList::new, WordParagraph::new, WordMlTag.PARAGRAPH);

   /**
    * Creates a new open and empty list for {@link WordParagraph}s.
    *
    * @param wordSubSection the {@link AuxHintSubSection} for the sub-section of the Word ML document.
    * @throws NullPointerException when the parameter <code>wordSubSection</code> is <code>null</code>.
    */

   public WordParagraphList(P parent) {
      super(parent);
   }

   /**
    * Gets the containing (parent) {@link AuxHintSubSection}.
    *
    * @return the containing {@link AuxHintSubSection}.
    */

   public Optional<AuxHintSubSection> getWordSubSection() {
      final var parent = this.getParent();

      //@formatter:off
      return
         (parent instanceof AuxHintSubSection)
            ? Optional.of( (AuxHintSubSection) parent )
            : Optional.empty();
      //@formatter:on
   }

   /**
    * Gets the containing (parent) {@link WordSection}.
    *
    * @return the containing {@link WordSection}.
    */

   public Optional<WordSection> getWordSection() {
      final var parent = this.getParent();

      //@formatter:off
      return
         (parent instanceof WordSection)
            ? Optional.of( (WordSection) parent )
            : Optional.empty();
      //@formatter:on
   }

}

/* EOF */
