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
 * Class to encapsulate a Word table row.
 *
 * @author Loren K. Ashley
 */

public class WordTableRow extends AbstractElement {

   /**
    * Creates a new {@link WordTableRow}.
    *
    * @apiNote This method is package private. Objects are created by package public methods in the class
    * {@link PublishingXmlUtils}.
    * @param wordTable the parent {@link WordTable}.
    * @param wordTableRowElement the {@link org.w3c.dom.Element} with the tag "w:tr" in the Word ML.
    * @throws NullPointerException when either of the parameters <code>wordBody</code> or
    * <code>wordTableRowElement</code> are <code>null</code>.
    */

   public WordTableRow(WordTable wordTable, Element wordTableRowElement) {
      super(wordTable, wordTableRowElement, WordXmlTag.TABLE_ROW);
   }

   /**
    * Gets the containing (parent) {@link WordTable}.
    *
    * @return the containing {@link WordTable}.
    */

   public Optional<WordTable> getWordTable() {
      var parent = this.getParent();
      //@formatter:off
      return
         (parent instanceof WordTable)
            ? Optional.of( (WordTable) parent )
            : Optional.empty();
      //@formatter:on
   }

   /**
    * Gets a list of the table columns belonging to the table row. Table columns from any tables nested within the table
    * row are not included.
    *
    * @return a {@link WordTableColumnList} of the table columns belonging to the table row.
    */
   public Optional<WordTableColumnList> getWordTableColumnList() {
      return this.getChild(WordTableColumnList.class);
   }
}

/* EOF */
