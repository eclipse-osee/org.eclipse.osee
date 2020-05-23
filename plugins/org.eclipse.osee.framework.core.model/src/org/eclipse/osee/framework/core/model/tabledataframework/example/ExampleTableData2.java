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
 * This class is a simpler example than ExampleTableData1. It has only one KeyColumn and two Columns. There is also no
 * error column so any exceptions that occur might be lost.
 * 
 * @author Shawn F. Cook
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
