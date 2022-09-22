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
 * Class to encapsulate a Word table column.
 *
 * @author Loren K. Ashley
 */

public class WordTableColumn extends AbstractElement {

   /**
    * Creates a new {@link WordTableColumn}.
    *
    * @apiNote This method is package private. Objects are created by package public methods in the class
    * {@link PublishingXmlUtils}.
    * @param wordTableRow the parent {@link WordTableRow}.
    * @param wordTableColumnElement the {@link org.w3c.dom.Element} with the tag "w:tc" in the Word ML.
    * @throws NullPointerException when either of the parameters <code>wordTableRow</code> or
    * <code>wordTableColumnElment</code> are <code>null</code>.
    */

   WordTableColumn(WordTableRow wordTableRow, Element wordTableColumnElement) {
      super(wordTableRow, wordTableColumnElement);
   }

   /**
    * Gets the containing (parent) {@link WordTableRow}.
    *
    * @return the containing {@link WordTableRow}.
    */

   public WordTableRow getWordTableRow() {
      return (WordTableRow) this.getParent();
   }

   /**
    * Gets a list of the text elements contained in the table column. The returned list will also contain text elements
    * from any tables nested within the table column.
    *
    * @return a {@link WordTextList} of all the text elements contained in the table column.
    */

   public Optional<WordTextList> getWordTextList() {
      return this.getChild(WordTextList.class);
   }
}

/* EOF */