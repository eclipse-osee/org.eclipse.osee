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
 * @author Shawn F. Cook This class is a simpler example than ExampleTableData1. It has only one KeyColumn and two
 * Columns. There is also no error column so any exceptions that occur might be lost.
 */
public class ExampleTableData2 extends TableDataImpl {

   public ExampleTableData2() {
      KeyColumn_1to10 keyCol1to10 = new KeyColumn_1to10();

      //colNumber is VISIBLE
      ColumnNumber colNumber = new ColumnNumber(keyCol1to10, true);

      //colLetter is NOT visible, but notice that it still contributes to the data set due to the dependency by colNumberLetter (below)
      ColumnCumulativeSum colSum = new ColumnCumulativeSum(keyCol1to10, true);

      //Adding columns to the list.
      //NOTE:  Order is important!!!
      addKeyColumn(keyCol1to10);

      addColumn(colNumber);
      addColumn(colSum);
   }

   @Override
   public String getName() {
      return "ExampleTableData2";
   }
}
