/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.framework.ui.skynet;

import java.util.ArrayList;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultData;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultPage.Manipulations;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class TableViewerReport {

   private final TableViewer tableViewer;
   private final String title;

   public TableViewerReport(String title, TableViewer tableViewer) {
      this.title = title;
      this.tableViewer = tableViewer;
   }

   public TableViewerReport(TableViewer tableViewer) {
      this("Table View Report", tableViewer);
   }

   public void open() {
      open(tableViewer.getTable().getItems());
   }

   public void open(TableItem items[]) {
      String html = getHtml(items);
      XResultData xResultData = new XResultData();
      xResultData.addRaw(html);
      xResultData.report(title, Manipulations.RAW_HTML);
   }

   public String getHtml(TableItem items[]) {
      String html = "<html><body>";
      html += AHTML.beginMultiColumnTable(100, 1);
      TableColumn cols[] = tableViewer.getTable().getColumns();
      Integer width[] = new Integer[cols.length + 1];
      String colNames[] = new String[cols.length + 1];
      for (int x = 0; x < cols.length + 1; x++) {
         if (x < cols.length) {
            TableColumn col = cols[x];
            width[x] = col.getWidth();
            colNames[x] = col.getText();
         } else {
            width[x] = 20;
            colNames[x] = "ID";
         }
      }
      html += AHTML.addHeaderRowMultiColumnTable(colNames, width);
      // Get column widths and column name and setup the columns
      IBaseLabelProvider labelProvider = tableViewer.getLabelProvider();
      ArrayList<String[]> list = new ArrayList<String[]>();
      for (TableItem item : items) {
         addRow(item, list, labelProvider, cols.length > 0 ? cols.length : 1, 1);
      }
      for (String[] strs : list) {
         html += AHTML.addRowMultiColumnTable(strs);
      }
      html += AHTML.endMultiColumnTable();
      html += "</body></html>";
      return html;
   }

   public void addRow(TableItem item, ArrayList<String[]> list, IBaseLabelProvider labelProvider, int numCols, int level) {
      String str[] = new String[numCols + 1];
      for (int x = 0; x < numCols; x++) {
         str[x] = "";
         if (x == 0) {
            for (int y = 0; y < level; y++)
               str[x] += "&nbsp;&nbsp;&nbsp;&nbsp;";
         }

         if (labelProvider instanceof LabelProvider) {
            str[x] += ((LabelProvider) labelProvider).getText(item.getData());
         } else if (labelProvider instanceof ITableLabelProvider) {
            str[x] += ((ITableLabelProvider) labelProvider).getColumnText(item.getData(), x);
         }
         // str[numCols] = GUID.toHumanId(((BaseModel)item.getData()).guid.get());
      }
      list.add(str);
   }
}
