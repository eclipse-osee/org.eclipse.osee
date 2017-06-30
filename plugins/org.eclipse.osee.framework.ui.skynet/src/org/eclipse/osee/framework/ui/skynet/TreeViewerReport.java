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
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.framework.core.util.result.Manipulations;
import org.eclipse.osee.framework.core.util.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

public class TreeViewerReport {

   private final TreeViewer treeViewer;
   private ArrayList<Integer> ignoreCols = new ArrayList<>();
   private final String title;

   public TreeViewerReport(String title, TreeViewer treeViewer) {
      this.title = title;
      this.treeViewer = treeViewer;
   }

   public TreeViewerReport(TreeViewer treeViewer) {
      this("Table View Report", treeViewer);
   }

   public void open() {
      open(treeViewer.getTree().getItems());
   }

   public void setIgnoreColumns(ArrayList<Integer> ignoreCols) {
      this.ignoreCols = ignoreCols;
   }

   public void open(TreeItem items[]) {
      String html = getHtml(items);
      XResultData xResultData = new XResultData();
      xResultData.addRaw(html);
      XResultDataUI.report(xResultData, title, Manipulations.RAW_HTML);
   }

   public String getHtml(TreeItem items[]) {
      String html = "<html><body>";
      if (!title.equals("WebDialog")) {
         html += AHTML.heading(3, title);
      }
      html += AHTML.beginMultiColumnTable(100, 1);
      TreeColumn cols[] = treeViewer.getTree().getColumns();
      Integer width[] = new Integer[cols.length - ignoreCols.size()];
      String colNames[] = new String[cols.length - ignoreCols.size()];
      int colNum = 0;
      for (int x = 0; x < cols.length; x++) {
         if (!ignoreCols.contains(x)) {
            TreeColumn col = cols[x];
            width[colNum] = col.getWidth();
            colNames[colNum++] = col.getText();
         }
      }
      html += AHTML.addHeaderRowMultiColumnTable(colNames, width);
      // Get column widths and column name and setup the columns
      ITableLabelProvider labelProv = (ITableLabelProvider) treeViewer.getLabelProvider();
      ArrayList<String[]> list = new ArrayList<>();
      for (TreeItem item : items) {
         addRow(item, list, labelProv, cols.length, 1);
      }
      for (String[] strs : list) {
         html += AHTML.addRowMultiColumnTable(strs);
      }
      html += AHTML.endMultiColumnTable();
      html += "</body></html>";
      return html;
   }

   public void addRow(TreeItem item, ArrayList<String[]> list, ITableLabelProvider labelProv, int numCols, int level) {
      String str[] = new String[numCols - ignoreCols.size()];
      int colNum = 0;
      for (int x = 0; x < numCols; x++) {
         if (!ignoreCols.contains(x)) {
            str[colNum] = "";
            if (x == 0) {
               for (int y = 0; y < level; y++) {
                  str[colNum] += "&nbsp;&nbsp;&nbsp;&nbsp;";
               }
            }
            str[colNum++] += labelProv.getColumnText(item.getData(), x);
         }
      }
      list.add(str);
      if (item.getExpanded()) {
         for (TreeItem i : item.getItems()) {
            addRow(i, list, labelProv, numCols, level + 1);
         }
      }

   }

}
