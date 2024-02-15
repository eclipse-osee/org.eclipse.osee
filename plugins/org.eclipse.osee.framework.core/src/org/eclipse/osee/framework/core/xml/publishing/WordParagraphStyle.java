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
import org.w3c.dom.Element;

/**
 * Class to encapsulate a Word paragraph that is not contained within another Word paragraph.
 *
 * @author Loren K. Ashley
 */

public class WordParagraphStyle extends AbstractElement {

   /**
    * Creates a new {@link WordParagraphStyle}.
    *
    * @param parent the {@link WordElement} implementation that is considered the parent by the parser implementation.
    * @param wordParagraphStyleElement the {@link org.w3c.dom.Element} with the tag "w:pStyle" in the Word ML.
    * @throws NullPointerException when either of the parameters <code>wordSubSection</code> or
    * <code>wordParagraphElement</code> are <code>null</code>.
    */

   public WordParagraphStyle(WordElement parent, Element wordParagraphStyleElement) {
      super(parent, wordParagraphStyleElement, WordMlTag.PARAGRAPH_STYLE);
   }

   /**
    * For {@link WordParagraphStyle} elements, gets the containing (parent) {@link WordParagraph}.
    *
    * @return when the parsed parent is a {@link WordParagraph}, an {@link Optional} containing the parent
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
