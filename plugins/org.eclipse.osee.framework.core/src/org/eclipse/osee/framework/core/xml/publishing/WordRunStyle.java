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
 * Class to encapsulate a Word Run Style element.
 *
 * @author Loren K. Ashley
 */

public class WordRunStyle extends AbstractElement {

   /**
    * Creates a new {@link WordRunStyle}.
    *
    * @param parent the containing {@link WordRun}.
    * @param wordRunStyleElement the {@link org.w3c.dom.Element} for the new {@link WordRunStyle}.
    * @throws NullPointerException when either of the parameters <code>parent</code> or <code>wordRunStyleElement</code>
    * are <code>null</code>.
    */

   public WordRunStyle(WordRun parent, Element wordRunStyleElement) {
      super(parent, wordRunStyleElement, WordXmlTag.RUN_STYLE);
   }

   /**
    * Get the containing {@link WordRun} of the {@link WordRunStyle}.
    *
    * @return when the {@link WordRunStyle} is contained by a {@link WordRun} an {@link Optional} containing the
    * {@link WordRun}; otherwise, an empty {@link Optional}.
    */

   public Optional<WordRun> getWordRun() {
      var parent = this.getParent();
      //@formatter:off
      return
         (parent instanceof WordRun)
            ? Optional.of( (WordRun) parent )
            : Optional.empty();
   }
}

/* EOF */
