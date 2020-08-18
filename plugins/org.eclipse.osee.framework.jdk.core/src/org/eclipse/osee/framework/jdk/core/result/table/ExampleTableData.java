/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.jdk.core.result.table;

import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * Used to test ResultsEditor and XResultTable used in integration testing and examples section of IDE Navigator
 *
 * @author Donald G. Dunne
 */
public class ExampleTableData {

   public static enum Columns {
      Date,
      Priority_123_Open_Bugs,
      Goal;
   };
   public final static List<String> chartDateStrs = Arrays.asList("09/07/2008", "09/21/2008", "10/05/2008",
      "10/19/2008", "11/02/2008", "11/16/2008", "11/30/2008", "12/14/2008", "12/28/2008", "01/11/2009", "01/25/2009",
      "02/08/2009", "02/22/2009", "03/08/2009", "03/22/2009", "04/05/2009", "04/19/2009");
   public final static List<Double> chartValueStrs = Arrays.asList(177.0, 174.0, 167.0, 161.0, 167.0, 167.0, 163.0,
      165.0, 171.0, 179.0, 178.0, 177.0, 164.0, 159.0, 159.0, 157.0, 157.0);
   public final static List<Double> chartValueStrsGoal = Arrays.asList(177.0, 174.0, 167.0, 161.0, 167.0, 167.0, 163.0,
      165.0, 171.0, 179.0, 177.0, 175.0, 173.0, 171.0, 169.0, 167.0, 165.0);

   public static List<XResultTableColumn> columns = Arrays.asList( //
      new XResultTableColumn(Columns.Date.name(), Columns.Date.name(), 100, XResultTableDataType.Date),
      new XResultTableColumn(Columns.Priority_123_Open_Bugs.name(), Columns.Priority_123_Open_Bugs.name(), 80,
         XResultTableDataType.Integer),
      new XResultTableColumn(Columns.Goal.name(), Columns.Goal.name(), 80, XResultTableDataType.Integer));

   /**
    * Utility class
    */
   private ExampleTableData() {
      // do nothing
   }

   public static XResultData getResultTable() {
      XResultData rd = new XResultData();
      rd.log("Testing XResultTable capabilities, serialization and serves as example to developers.\n\n" //
         + "This call:\n\n" //
         + "- Makes a REST call to server\n" //
         + "- Server generates an XResultData with a XResultTable included\n" //
         + "- Return opens in Results Editor where text is displayed on this tab and data on next tab\n\n" //
         + "Select Example Tab below.");

      XResultTable table = new XResultTable();
      table.setName("Example Table");

      table.getColumns().addAll(ExampleTableData.columns);

      for (int x = 0; x < ExampleTableData.chartDateStrs.size(); x++) {
         table.getRows().add(new XResultTableRow(ExampleTableData.chartDateStrs.get(x),
            String.valueOf(ExampleTableData.chartValueStrs.get(x)),
            String.valueOf(ExampleTableData.chartValueStrsGoal.get(x))));
      }

      rd.getTables().add(table);
      return rd;
   }
}
