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
 * Class to encapsulate a list of texts.
 *
 * @author Loren K. Ashley
 */

public class WordTextList extends AbstractElementList<AbstractElement, WordText> {

   /**
    * Creates a new open and empty list for {@link WordText}s.
    *
    * @apiNote This method is package private. Objects are created by package public methods in the class
    * {@link PublishingXmlUtils}.
    * @param WordTableColumn the {@link WordTableColumn} containing the texts.
    * @throws NullPointerException when the parameter <code>wordTableColumn</code> is <code>null</code>.
    */

   public WordTextList(WordTableColumn wordTableColumn) {
      super(wordTableColumn);
   }

   /**
    * Creates a new open and empty list for {@link WordText}s.
    *
    * @apiNote This method is package private. Objects are created by package public methods in the class
    * {@link PublishingXmlUtils}.
    * @param WordParagrap the {@link WordParagraph} containing the texts.
    * @throws NullPointerException when the parameter <code>wordParagraph</code> is <code>null</code>.
    */

   public WordTextList(WordParagraph wordParagraph) {
      super(wordParagraph);
   }

   /**
    * Get the containing (parent) {@link WordTableColumn}.
    *
    * @return the containing {@link WordTableColumn}.
    */

   public Optional<WordTableColumn> getWordTableColumn() {
      var parent = this.getParent();
      //@formatter:off
      return
         ( parent instanceof WordTableColumn )
            ? Optional.of( (WordTableColumn) parent )
            : Optional.empty();
      //@formatter:on
   }

   /**
    * Get the containing (parent) {@link WordParagraph}.
    *
    * @return the containing {@link WordParagraph}.
    */

   public Optional<WordParagraph> getWordParagraph() {
      var parent = this.getParent();
      //@formatter:off
      return
         ( parent instanceof WordParagraph )
            ? Optional.of( (WordParagraph) parent )
            : Optional.empty();
      //@formatter:on
   }

}

/* EOF */
