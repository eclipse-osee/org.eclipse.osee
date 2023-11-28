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
 * Class to encapsulate a Word Field Character element.
 *
 * @author Loren K. Ashley
 */

public class WordFieldCharacter extends AbstractElement {

   /**
    * Creates a new {@link WordFieldCharacter}.
    *
    * @param parent the containing {@link WordRun}.
    * @param wordRunElement the {@link org.w3c.dom.Element} for the new {@link WordFieldCharacter}.
    * @throws NullPointerException when either of the parameters <code>parent</code> or
    * <code>wordFieldCharacterElement</code> are <code>null</code>.
    */

   public WordFieldCharacter(WordRun parent, Element wordFieldCharacterElement) {
      super(parent, wordFieldCharacterElement, WordXmlTag.FIELD_CHARACTER);
   }

   /**
    * Get the containing {@link WordRun} of the {@link WordFieldCharacter}.
    *
    * @return when the {@link WordFieldCharacter} is contained by a {@link WordRun} an {@link Optional} containing the
    * {@link WordRun}; otherwise, an empty {@link Optional}.
    */

   public Optional<WordRun> getWordRun() {
      var parent = this.getParent();

      //@formatter:off
      return
         (parent instanceof WordRun)
            ? Optional.of( (WordRun) parent )
            : Optional.empty();
      //@formatter:on
   }
}

/* EOF */