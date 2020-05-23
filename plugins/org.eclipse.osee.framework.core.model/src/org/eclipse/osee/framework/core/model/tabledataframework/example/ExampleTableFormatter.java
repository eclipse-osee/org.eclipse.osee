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

import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.model.tabledataframework.TableData;
import org.eclipse.osee.framework.core.model.tabledataframework.TableFormatter;

/**
 * @author Shawn F. Cook
 */
public class ExampleTableFormatter implements TableFormatter {

   @Override
   public void writeReport(IProgressMonitor monitor, Collection<TableData> tableDatas) throws Exception {
      int totalReportSize = 0;
      for (TableData tableData : tableDatas) {
         totalReportSize += tableData.getRowCount();
      }

      for (TableData tableData : tableDatas) {
         System.out.println(tableData.getName());
         System.out.println("Many Columns:" + tableData.getColumnCount());
         System.out.println(tableData.getHeaderStrings());
         monitor.beginTask("Collection data and writing table:" + tableData.getName(), totalReportSize);
         for (Collection<Object> cols : tableData) {
            if (monitor.isCanceled()) {
               return;
            }
            System.out.println(cols);
            monitor.worked(1);
         }
         System.out.println("\n");
      }

      System.out.println("Done.");
   }
}
