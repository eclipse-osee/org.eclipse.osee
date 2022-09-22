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

/**
 * Class to encapsulate an ordered list of the columns in a row of a Word table.
 *
 * @author Loren K. Ashley
 */

public class WordTableColumnList extends AbstractElementList<WordTableRow, WordTableColumn> {

   /**
    * Creates a new open and empty list for {@link WordTableColumn}s.
    *
    * @apiNote This method is package private. Objects are created by package public methods in the class
    * {@link PublishingXmlUtils}.
    * @param WordTableRow the {@link WordTableRow} containing the table columns.
    * @throws NullPointerException when the parameter <code>wordTableRow</code> is <code>null</code>.
    */

   WordTableColumnList(WordTableRow wordTableRow) {
      super(wordTableRow);
   }

   /**
    * Get the containing (parent) {@link WordTableRow}.
    *
    * @return the containing {@link WordTableRow}.
    */

   public WordTableRow getWordTableRow() {
      return this.getParent();
   }

}

/* EOF */
