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
 * Class to encapsulate a list of Auxiliary Hint Text elements.
 *
 * @author Loren K. Ashley
 */

public class AuxHintTextList<P extends AbstractElement> extends AbstractElementList<P, AuxHintText> {

   /**
    * {@link WordElementParserFactory} instance for creating a {@link AuxHintTextList} with a {@link WordParagraph}
    * parent.
    */

   public static WordElementParserFactory<WordParagraph, AuxHintTextList<WordParagraph>, AuxHintText> wordParagraphParentFactory =
      new WordElementParserFactory<>(AuxHintTextList::new, AuxHintText::new, AuxHintTag.TEXT);

   /**
    * Creates a new open and empty list for {@link AuxHintText}s.
    *
    * @apiNote This method is package private. Objects are created by package public methods in the class
    * {@link PublishingXmlUtils}.
    * @param parent the {@link AbstractElement} implementation the Auxiliary Hint Texts were parsed from.
    * @throws NullPointerException when the parameter <code>parent</code> is <code>null</code>.
    */

   public AuxHintTextList(P parent) {
      super(parent);
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
