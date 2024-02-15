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

package org.eclipse.osee.framework.core.xml.publishing;

import java.util.Optional;

/**
 * Class to encapsulate a list of Word Paragraph Style elements.
 *
 * @author Loren K. Ashley
 */

public class WordParagraphStyleList<P extends AbstractElement> extends AbstractElementList<P, WordParagraphStyle> {

   /**
    * {@link WordElementParserFactory} instance for creating a {@link WordParagraphStyleList} with a
    * {@link WordParagraph} parent.
    */

   public static WordElementParserFactory<WordParagraph, WordParagraphStyleList<WordParagraph>, WordParagraphStyle> wordParagraphParentFactory =
      new WordElementParserFactory<>(WordParagraphStyleList::new, WordParagraphStyle::new, WordMlTag.PARAGRAPH_STYLE);

   /**
    * {@link WordElementParserFactory} instance for creating a {@link ParagraphList} with a {@link AuxHintSubSection}
    * parent.
    */

   public static WordElementParserFactory<AuxHintSubSection, WordParagraphList<AuxHintSubSection>, WordParagraph> wordSubSectionParentFactory =
      new WordElementParserFactory<>(WordParagraphList::new, WordParagraph::new, WordMlTag.PARAGRAPH);

   /**
    * Creates a new open and empty {@link WordParagraphStyleList}.
    *
    * @param parent the parent {@link AbstactElement} implementation the list is parsed from.
    * @throws NullPointerException when the parameter <code>parent</code> is <code>null</code>.
    */

   public WordParagraphStyleList(P parent) {
      super(parent);
   }

   /**
    * Gets the containing (parent) {@link WordParagraph} the {@link WordParagraphStyleList} was parsed from.
    *
    * @return the parent {@link WordParagraph}.
    */

   public Optional<WordParagraph> getWordParagraph() {
      final var parent = this.getParent();

      //@formatter:off
      return
         (parent instanceof WordParagraph)
            ? Optional.of( (WordParagraph) parent )
            : Optional.empty();
      //@formatter:on
   }

}

/* EOF */
