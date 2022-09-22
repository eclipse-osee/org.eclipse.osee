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

public class WordText extends AbstractElement {

   WordText(WordElement parent, Element wordTextElement) {
      super(parent, wordTextElement);
   }

   public Optional<WordTableColumn> getWordTableColumn() {
      var parent = this.getParent();

      //@formatter:off
      return
         (parent instanceof WordTableColumn)
            ? Optional.of( (WordTableColumn) this.getParent() )
            : Optional.empty();
      //@formatter:on
   }

}

/* EOF */
