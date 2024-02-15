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

public class AuxHintText extends AbstractElement {

   /**
    * Creates a new {@link AuxHitText}.
    *
    * @apiNote This method is package private. Objects are created by package public methods in the class
    * {@link PublishingXmlUtils}.
    * @param parent the {@link WordElement} implementation that is considered the parent by the parser implementation.
    * @param element the {@link org.w3c.dom.Element} with the tag "wx:t" in the Word ML.
    * @throws NullPointerException when either of the parameters <code>parent</code> or <code>element</code> are
    * <code>null</code>.
    */

   public AuxHintText(WordElement parent, Element element) {
      super(parent, element, AuxHintTag.TEXT);
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
