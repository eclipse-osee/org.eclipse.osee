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
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.w3c.dom.Element;

/**
 * Class to encapsulate a Word paragraph that is not contained within another Word paragraph.
 *
 * @author Loren K. Ashley
 */

public class WordParagraph extends AbstractElement {

   //@formatter:off
   private static Pair<Class<?>,Class<?>> wordTextListWithParentWordParagraphChildKey =
      Pair.createNonNullImmutable( WordTextList.class, WordTableColumn.class );
   //@formatter:on

   /**
    * Creates a new {@link WordParagraph}.
    *
    * @param parent the {@link WordElement} implementation that is considered the parent by the parser implementation.
    * @param wordParagraphElement the {@link org.w3c.dom.Element} with the tag "w:p" in the Word ML.
    * @throws NullPointerException when either of the parameters <code>wordSubSection</code> or
    * <code>wordParagraphElement</code> are <code>null</code>.
    */

   public WordParagraph(WordElement parent, Element wordParagraphElement) {
      super(parent, wordParagraphElement, WordMlTag.PARAGRAPH);
   }

   /**
    * For top level Word paragraphs, gets the containing (parent) {@link AuxHintSubSection}.
    *
    * @return when the {@link WordParagraph} is for a top level Word Sub-Section, a {@link Optional} containing the
    * parent {@link AuxHintSubSection}; otherwise, an empty {@link Optional}.
    */

   public Optional<AuxHintSubSection> getWordSubSection() {
      var parent = this.getParent();

      //@formatter:off
      return
         (parent instanceof AuxHintSubSection)
            ? Optional.of( (AuxHintSubSection) this.getParent() )
            : Optional.empty();
      //@formatter:on
   }

   /**
    * For top level Word paragraphs, gets the containing (parent) {@link WordSection}.
    *
    * @return when the {@link WordParagraph} is for a top level Word Section, a {@link Optional} containing the parent
    * {@link WordSection}; otherwise, an empty {@link Optional}.
    */

   public Optional<WordSection> getWordSection() {
      var parent = this.getParent();

      //@formatter:off
      return
         (parent instanceof WordSection)
            ? Optional.of( (WordSection) this.getParent() )
            : Optional.empty();
      //@formatter:on
   }

   /**
    * Gets the {@link WordTextList} for the Paragraph.
    *
    * @return when Texts have been parsed for Paragraph, a {@link Optional} containing a possibly empty
    * {@link WordTextList}; otherwise, a {@link Optional}.
    */

   public Optional<WordTextList<WordParagraph>> getWordTextList() {
      return this.getChild(WordParagraph.wordTextListWithParentWordParagraphChildKey);
   }
}

/* EOF */
