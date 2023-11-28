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
 * Class to encapsulate the body of a Word ML document.
 *
 * @author Loren K. Ashley
 */

public class WordBody extends AbstractElement {

   /**
    * Creates a new {@link WordBody}.
    *
    * @param wordDocument the parent {@link WordDocument}.
    * @param wordBodyElement the {@link org.w3c.dom.Element} for the new {@link WordBody}.
    * @throws NullPointerException when either of the parameters <code>wordDocument</code> or
    * <code>wordBodyElement</code> are <code>null</code>.
    */

   public WordBody(WordDocument wordDocument, Element wordBodyElement) {
      super(wordDocument, wordBodyElement, WordXmlTag.BODY);
   }

   /**
    * Gets the containing (parent) {@link WordDocument}.
    *
    * @return when the {@link WordBody} is contained by a {@link WordDocument} an {@link Optional} containing the
    * {@link WordDocument}; otherwise, an empty {@link Optional}.
    */

   public Optional<WordDocument> getWordDocument() {
      var parent = this.getParent();
      //@formatter:off
      return
         ( parent instanceof WordDocument )
            ? Optional.of( (WordDocument) parent )
            : Optional.empty();
      //@formatter:on
   }

   /**
    * Gets a list of the top level Word tables in the {@link WordBody}.
    *
    * @return a {@link WordTableList} of the top level Word tables.
    */

   public Optional<WordTableList> getWordTableList() {
      return this.getChild(WordTableList.class);
   }
}

/* EOF */
