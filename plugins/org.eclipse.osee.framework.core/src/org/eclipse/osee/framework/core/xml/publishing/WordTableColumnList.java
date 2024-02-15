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

/**
 * Class to encapsulate an ordered list of the columns in a row of a Word table.
 *
 * @author Loren K. Ashley
 */

public class WordTableColumnList<P extends AbstractElement> extends AbstractElementList<P, WordTableColumn> {

   /**
    * {@link WordElementParserFactory} instance for creating a {@link WordTableColumnList} with a {@link WordTableRow}
    * parent.
    */

   public static WordElementParserFactory<WordTableRow, WordTableColumnList<WordTableRow>, WordTableColumn> wordTableRowParentFactory =
      new WordElementParserFactory<>(WordTableColumnList::new, WordTableColumn::new, WordMlTag.TABLE_COLUMN);

   /**
    * Creates a new open and empty list for {@link WordTableColumn}s.
    *
    * @param WordTableRow the {@link WordTableRow} containing the table columns.
    * @throws NullPointerException when the parameter <code>wordTableRow</code> is <code>null</code>.
    */

   public WordTableColumnList(P parent) {
      super(parent);
   }

   /**
    * Get the containing (parent) {@link WordTableRow}.
    *
    * @return the containing {@link WordTableRow}.
    */

   public Optional<WordTableRow> getWordTableRow() {
      var parent = this.getParent();
      //@formatter:off
      return
         (parent instanceof WordTableRow)
            ? Optional.of((WordTableRow) parent)
            : Optional.empty();
      //@formatter:off
   }
}

/* EOF */
