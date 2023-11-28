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
 * Class to encapsulate a Word table that is not contained within another Word table.
 *
 * @author Loren K. Ashley
 */

public class WordTable extends AbstractElement {

   /**
    * Creates a new {@link WordTable}.
    *
    * @param parent the {@link WordElement} implementation that is considered the parent by the parser implementation.
    * @param wordTableElement the {@link org.w3c.dom.Element} with the tag "w:tbl" in the Word ML.
    * @throws NullPointerException when either of the parameters <code>wordBody</code> or <code>wordTableElement</code>
    * are <code>null</code>.
    */

   public WordTable(WordElement parent, Element wordTableElement) {
      super(parent, wordTableElement, WordXmlTag.TABLE);
   }

   /**
    * For top level Word tables, gets the containing (parent) {@link WordBody}.
    *
    * @return when the {@link WordTable} is for a top level Word table, a {@link Optional} containing the parent
    * {@link WordBody}; otherwise, an empty {@link Optional}.
    */

   public Optional<WordBody> getWordBody() {
      var parent = this.getParent();

      //@formatter:off
      return
         (parent instanceof WordBody)
            ? Optional.of( (WordBody) this.getParent() )
            : Optional.empty();
      //@formatter:on
   }

   /**
    * Gets the {@link WordTableRowList} for the table rows.
    *
    * @return when table rows have been parsed for the table, a {@link Optional} containing a possibly empty
    * {@link WordTableRowList}; otherwise, a {@link Optional}.
    */

   public Optional<WordTableRowList> getWordTableRowList() {
      return this.getChild(WordTableRowList.class);
   }
}

/* EOF */