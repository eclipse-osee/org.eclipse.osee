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
