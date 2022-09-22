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
    * @apiNote This method is package private. Objects are created by package public methods in the class
    * {@link PublishingXmlUtils}.
    * @param wordDocument the parent {@link WordDocument}.
    * @param wordBodyElement the {@link org.w3c.dom.Element} with the tag "w:body" in the Word ML.
    * @throws NullPointerException when either of the parameters <code>wordDocument</code> or
    * <code>wordBodyElement</code> are <code>null</code>.
    */

   WordBody(WordDocument wordDocument, Element wordBodyElement) {
      super(wordDocument, wordBodyElement);
   }

   /**
    * Gets the containing (parent) {@link WordDocument}.
    *
    * @return the containing {@link WordDocument}.
    */

   public WordDocument getWordDocument() {
      return (WordDocument) this.getParent();
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
