/*********************************************************************
 * Copyright (c) 2023 Boeing
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
 * Class to encapsulate a Word Hyperlink element.
 *
 * @author Loren K. Ashley
 */

public class WordHlink extends AbstractElement {

   /**
    * Creates a new {@link WordHlink}.
    *
    * @param parent the containing {@link WordParagraph}.
    * @param wordHlinkElement the {@link org.w3c.dom.Element} for the new {@link WordHlink}.
    * @throws NullPointerException when either of the parameters <code>parent</code> or <code>wordHlinkElement</code>
    * are <code>null</code>.
    */

   public WordHlink(WordParagraph wordParagraph, Element wordHlinkElement) {
      super(wordParagraph, wordHlinkElement, WordXmlTag.HLINK);
   }

   /**
    * Get the containing {@link WordParagraph} of the {@link WordHlink}.
    *
    * @return when the {@link WordHlink} is contained by a {@link WordParagraph} an {@link Optional} containing the
    * {@link WordHlink}; otherwise, an empty {@link Optional}.
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
