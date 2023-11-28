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
 * Class to encapsulate a Word ML document.
 *
 * @author Loren K. Ashley
 */

public class WordDocument extends AbstractElement {

   /**
    * Creates a new {@link WordDocument} object.
    *
    * @param wordDocumentElement The {@link org.w3c.dom.Element} with the tag "w:wordDocument" in the Word ML document.
    * @throws NullPointerException when either of the parameter <code>wordDocumentElement</code> is <code>null</code>.
    */

   public WordDocument(Element wordDocumentElement) {
      super(wordDocumentElement, WordXmlTag.WORD_DOCUMENT);
   }

   /**
    * Gets the body of the Word ML document as a {@link WordBody}.
    *
    * @return when the {@link WordDocument} contains a {@link WordBody}, an {@link Optional} containing the
    * {@link WordBody}; otherwise, an empty {@link Optional}.
    */

   public Optional<WordBody> getWordBody() {
      return this.getChild(WordBody.class);
   }
}

/* EOF */
