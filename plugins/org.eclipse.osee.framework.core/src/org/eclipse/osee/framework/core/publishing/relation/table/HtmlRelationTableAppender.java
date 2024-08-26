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
package org.eclipse.osee.framework.core.publishing.relation.table;

import java.util.ArrayList;
import java.util.List;

/**
 * Appends relation table content in HTML format. This class implements the {@link RelationTableAppender} interface and
 * provides methods to build and append HTML tables for relation data. It handles the formatting and content of tables,
 * including headers and rows, specific to HTML format.
 * 
 * @author Jaden W. Puckett
 */
public class HtmlRelationTableAppender implements RelationTableAppender {

   private StringBuilder htmlContent = new StringBuilder();
   private List<String> columnHeaders = new ArrayList<>();

   /**
    * Starts the table appending process by initializing the HTML table tag.
    */
   @Override
   public void startTable() {
      htmlContent.append("<br><table border='1'>");
   }

   /**
    * Appends the table header, which spans all columns.
    *
    * @param relationTypeName the name of the relation type to display in the header
    * @param relationTypeSideName the name of the relation type side to display in the header
    * @param columnCount the number of columns in the table
    */
   @Override
   public void appendTableHeader(String relationTypeName, String relationTypeSideName, int columnCount) {
      htmlContent.append("<tr>").append("<th colspan='").append(columnCount).append(
         "' style='text-align:center; padding: 8px;'>").append(relationTypeName).append(" relation to ").append(
            relationTypeSideName).append("</th>").append("</tr>");
   }

   /**
    * Appends the column headers to the HTML table based on the provided column names. This method removes the prefix
    * from column names if they contain a '.' character.
    *
    * @param columns the list of column names or IDs to include in the table header
    */
   @Override
   public void appendColumnHeaders(List<String> columns) {
      this.columnHeaders = columns;
      if (!columnHeaders.isEmpty()) {
         htmlContent.append("<tr>");
         for (String column : columnHeaders) {
            // Remove the prefix if the column name contains a "."
            if (column.contains(".")) {
               column = column.substring(column.indexOf('.') + 1);
            }
            htmlContent.append("<th>").append(column).append("</th>");
         }
         htmlContent.append("</tr>");
      }
   }

   /**
    * Appends a row with the given cell values to the HTML table.
    *
    * @param cellValues the list of cell values to be included in the row
    */
   @Override
   public void appendRow(List<String> cellValues) {
      htmlContent.append("<tr>");
      for (String value : cellValues) {
         htmlContent.append("<td>").append(value).append("</td>");
      }
      htmlContent.append("</tr>");
   }

   /**
    * Ends the table appending process by closing the HTML table tag.
    */
   @Override
   public void endTable() {
      htmlContent.append("</table>");
   }

   /**
    * Retrieves the final HTML table content as a string.
    *
    * @return the HTML table content
    */
   @Override
   public String getTable() {
      return htmlContent.toString();
   }
}