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
 * Class to encapsulate an ordered list of the rows in a Word table.
 *
 * @author Loren K. Ashley
 */

public class WordTableRowList extends AbstractElementList<WordTable, WordTableRow> {

   /**
    * Creates a new open and empty list for {@link WordTableRow}s.
    *
    * @apiNote This method is package private. Objects are created by package public methods in the class
    * {@link PublishingXmlUtils}.
    * @param WordTable the {@link WordTable} containing the table rows.
    * @throws NullPointerException when the parameter <code>wordTable</code> is <code>null</code>.
    */

   WordTableRowList(WordTable wordTable) {
      super(wordTable);
   }

   /**
    * Get the containing (parent) {@link WordTable}.
    *
    * @return the containing {@link WordTable}.
    */

   public WordTable getWordTable() {
      return this.getParent();
   }

}

/* EOF */
