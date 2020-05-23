/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.framework.core.model.tabledataframework.example;

import org.eclipse.osee.framework.core.model.tabledataframework.TableDataImpl;

/**
 * @author Shawn F. Cook
 */
public class ExampleTableData1 extends TableDataImpl {
   ColumnErrors errorsCol = new ColumnErrors(true);

   public ExampleTableData1() {
      KeyColumn_1to10 keyCol1to10 = new KeyColumn_1to10();
      KeyColumn_AtoG keyColAtoG = new KeyColumn_AtoG();

      //colNumber is VISIBLE
      ColumnNumber colNumber = new ColumnNumber(keyCol1to10, true);

      //colLetter is NOT visible, but notice that it still contributes to the data set due to the dependency by colNumberLetter (below)
      ColumnLetter colLetter = new ColumnLetter(keyColAtoG, false);
      ColumnConcatNumberLetter colNumberLetter = new ColumnConcatNumberLetter(keyCol1to10, keyColAtoG, true);

      //Adding columns to the list.
      //NOTE:  Order is important!!!
      addKeyColumn(keyCol1to10);
      addKeyColumn(keyColAtoG);

      addColumn(colNumber);
      addColumn(colNumberLetter);
      addColumn(colLetter);
      addColumn(errorsCol);
   }

   @Override
   protected void beforeRow() {
      errorsCol.clearAllMsgs();
   }

   @Override
   protected void handleExceptions(Exception ex) {
      errorsCol.addErrorMessage(ex.toString());
   }

   @Override
   public String getName() {
      return "ExampleTableData1";
   }
}
