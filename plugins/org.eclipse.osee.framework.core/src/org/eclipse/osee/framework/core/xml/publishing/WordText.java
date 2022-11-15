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
import org.w3c.dom.Element;

/**
 * Class to encapsulate a Word ML text element.
 *
 * @author Loren K. Ashley
 */

public class WordText extends AbstractElement {

   /**
    * Creates a new {@link WordText}.
    *
    * @apiNote This method is package private. Objects are created by package public methods in the class
    * {@link PublishingXmlUtils}.
    * @param parent the {@link WordElement} implementation that is considered the parent by the parser implementation.
    * @param wordTextElement the {@link org.w3c.dom.Element} with the tag "w:t" in the Word ML.
    * @throws NullPointerException when either of the parameters <code>parent</code> or <code>wordTextElement</code> are
    * <code>null</code>.
    */

   WordText(WordElement parent, Element wordTextElement) {
      super(parent, wordTextElement);
   }

   /**
    * When the parent is a {@link WordTableColumn}, gets the parent.
    *
    * @return when the parent is a {@link WordTableColumn}, an {@link Optional} containing the parent
    * {@link WordTableColumn}; otherwise, an empty {@link Optional}.
    */

   public Optional<WordTableColumn> getWordTableColumn() {
      var parent = this.getParent();

      //@formatter:off
      return
         (parent instanceof WordTableColumn)
            ? Optional.of( (WordTableColumn) this.getParent() )
            : Optional.empty();
      //@formatter:on
   }

   /**
    * When the parent is a {@link WordParagraph}, gets the parent.
    *
    * @return when the parent is a {@link WordParagraph}, an {@link Optional} containing the parent
    * {@link WordParagraph}; otherwise, an empty {@link Optional}.
    */

   public Optional<WordParagraph> getWordParagraph() {
      var parent = this.getParent();

      //@formatter:off
      return
         (parent instanceof WordParagraph)
            ? Optional.of( (WordParagraph) this.getParent() )
            : Optional.empty();
      //@formatter:on
   }

}

/* EOF */
