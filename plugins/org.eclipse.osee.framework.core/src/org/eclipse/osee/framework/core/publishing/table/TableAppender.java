/*********************************************************************
 * Copyright (c) 2024 Boeing
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
package org.eclipse.osee.framework.core.publishing.table;

import java.util.List;

/**
 * Interface for appending table content in different formats (e.g., HTML, Word). This interface provides methods to
 * start and end table appending processes, append headers and rows, and retrieve the final table content in the
 * specified format.
 *
 * @author Jaden W. Puckett
 */
public interface TableAppender {

   /**
    * Starts the table appending process.
    */
   void startTable();

   /**
    * Appends the table header.
    *
    * @param headerString the string to display in the header
    * @param columnCount the number of columns in the table
    */
   void appendTableHeading(String headerString, int columnCount);

   /**
    * Appends column headers to the table.
    *
    * @param columns the list of column headers
    */
   void appendColumnHeaders(List<String> columns);

   /**
    * Appends a row with the given cell values.
    *
    * @param cellValues the list of cell values for the row
    */
   void appendRow(List<String> cellValues);

   /**
    * Ends the table appending process.
    */
   void endTable();

   /**
    * Retrieves the final table content in the specified format.
    *
    * @return the table content as a string
    */
   String getTable();

   /**
    * Clears the appender content.
    */
   void clearContent();
}