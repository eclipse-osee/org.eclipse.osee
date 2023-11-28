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
 * Class to encapsulate a Word Run element.
 *
 * @author Loren K. Ashley
 */

public class WordRun extends AbstractElement {

   /**
    * Creates a new {@link WordRun}.
    *
    * @param parent the containing {@link WordParagraph}.
    * @param wordRunElement the {@link org.w3c.dom.Element} for the new {@link WordRun}.
    * @throws NullPointerException when either of the parameters <code>parent</code> or <code>wordRunElement</code> are
    * <code>null</code>.
    */

   public WordRun(WordParagraph wordParagraph, Element wordRunElement) {
      super(wordParagraph, wordRunElement, WordXmlTag.RUN);
   }

   /**
    * Get the containing {@link WordParagraph} of the {@link WordRun}.
    *
    * @return when the {@link WordRun} is contained by a {@link WordParagraph} an {@link Optional} containing the
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
