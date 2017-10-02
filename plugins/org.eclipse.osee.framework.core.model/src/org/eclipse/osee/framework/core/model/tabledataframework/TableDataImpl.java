/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.model.tabledataframework;

import java.rmi.activation.Activator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Shawn F. Cook
 */
public class TableDataImpl implements TableData {

   private final List<Column> columns = new ArrayList<>();
   private final List<KeyColumn> keyColumns = new ArrayList<>();
   private boolean firstRun = true;

   protected void addColumn(Column column) {
      columns.add(column);
   }

   protected void addKeyColumn(KeyColumn keyColumn) {
      keyColumns.add(keyColumn);
   }

   @Override
   public int getColumnCount() {
      return columns.size();
   }

   @Override
   public Collection<Object> getHeaderStrings() {
      Collection<Object> headerStrings = new ArrayList<>();
      for (Column col : columns) {
         if (col.isVisible()) {
            headerStrings.add(col.getHeaderString());
         }
      }
      return headerStrings;
   }

   @Override
   public Iterator<Collection<Object>> iterator() {
      return new TableDataIterator();
   }

   public class TableDataIterator implements Iterator<Collection<Object>> {
      @Override
      public boolean hasNext() {
         boolean hasNext = false;
         for (KeyColumn keyColumn : keyColumns) {
            if (keyColumn.hasNext()) {
               hasNext = true;
               break;
            }
         }
         return hasNext;
      }

      @Override
      public Collection<Object> next() {
         Collection<Object> cols = new ArrayList<>();
         try {
            beforeRow();

            //isRowValid is used in conjunction with the validateRow() method.  This method is overriden by
            // subclasses and gives them an opportunity to implement validation logic (of any sort).  See
            // the validateRow() method below.  This boolean is initialized to false so the while loop completes
            // at least one iteration.
            boolean isRowValid = false;

            while (hasNext() && !isRowValid) {
               //The first time we run we'll need to go through all the keyColumns (except the last one) and 'touch' them in order to prime them.
               if (firstRun) {
                  for (int keyColIndex = 0; keyColIndex < keyColumns.size() - 1; keyColIndex++) {
                     KeyColumn keyColumn = keyColumns.get(keyColIndex);
                     keyColumn.next();
                  }
                  firstRun = false;
               }

               //Update key columns - call next() and reset() where necessary
               for (int keyColIndex = keyColumns.size() - 1; keyColIndex >= 0; keyColIndex--) {
                  KeyColumn keyColumn = keyColumns.get(keyColIndex);
                  if (!keyColumn.hasNext()) {
                     keyColumn.reset();
                     keyColumn.next();
                  } else {
                     keyColumn.next();
                     break;
                  }
               }

               boolean areKeyColsValid = validateRowUseOnlyKeyColums();
               if (areKeyColsValid) {
                  for (Column col : columns) {
                     if (col.isVisible()) {
                        cols.add(col.getData());
                     }
                  }

                  //validateRow() method gives subclasses an opportunity to reject rows.
                  isRowValid = validateRow(cols);
               }
            }
            //see method notes below.
            afterRow();
         } catch (Exception ex) {
            handleExceptions(ex);
            cols.add(ex.toString());
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
         return cols;
      }

      @Override
      public void remove() {
         //
      }

   }

   @Override
   public int getRowCount() {
      int manyRows = 1;
      for (KeyColumn keyColumn : keyColumns) {
         List<Object> allObjs = keyColumn.getAll();
         manyRows *= allObjs.size();
      }
      return manyRows;
   }

   @Override
   public String getName() {
      //Sub-classes should override this with a table name, if desired.
      return "";
   }

   /**
    * Subclasses should override this method (if needed) an evaluate the row of data for validity. It is the subclass's
    * opportunity to reject rows and remove them from the report. This method is called once for every row after it has
    * been generated in the next() method above.
    *
    * @param rowData - The row of data that was just generated.
    * @return Return TRUE if rowData is valid and should be included in the report. Return FALSE if rowData is invalid
    * (for any reason) and should not be included in the report.
    * 
    */
   protected boolean validateRow(Collection<Object> rowData)  {
      //Override with subclass if needed
      return true;
   }

   /**
    * This is nearly the same as validateRow() method (see above) but it is called after the key columns have been
    * incremented and BEFORE the row's data has been generated. Therefore, the subclass that overrides this should only
    * use the key column objects to determine if this row should be rejected or validated.
    *
    * @return Return symantics is the same as validateRow()
    * 
    */
   protected boolean validateRowUseOnlyKeyColums()  {
      //Override with subclass if needed
      return true;
   }

   //NOTE: before Row is called in the next() method above.
   protected void beforeRow() {
      //Override with subclass if needed.
   }

   //NOTE: afterRow() is called in the next() method above.  Notice that it is
   // called after all the columns have been called for this row but not before
   // the calling object has had a chance to actually use the data that was collected
   // for this row.  So "cleanup" should probably be done in the beforeRow() method.
   protected void afterRow() {
      //Override with subclass if needed.
   }

   /**
    * Subclasses should override this method if they wish to receive exceptions and error messages.
    *
    * @param ex The exception to handle
    */
   protected void handleExceptions(Exception ex) {
      //Do nothing
   }

}
