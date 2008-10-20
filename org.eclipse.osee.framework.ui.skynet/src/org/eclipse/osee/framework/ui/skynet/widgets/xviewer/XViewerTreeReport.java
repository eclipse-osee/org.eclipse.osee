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

package org.eclipse.osee.framework.ui.skynet.widgets.xviewer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultData;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultPage.Manipulations;
import org.eclipse.swt.widgets.TreeItem;

public class XViewerTreeReport {

   private final XViewer xViewer;
   private final String title;

   public XViewerTreeReport(String title, XViewer treeViewer) {
      this.title = title;
      this.xViewer = treeViewer;
   }

   public XViewerTreeReport(XViewer xViewer) {
      this("Table View Report", xViewer);
   }

   public void open() {
      open(xViewer.getTree().getItems());
   }

   public void open(TreeItem items[]) {
      try {
         String html = getHtml(items);
         XResultData xResultData = new XResultData();
         xResultData.addRaw(html);
         xResultData.report(title, Manipulations.RAW_HTML);
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }
   }

   private Map<XViewerColumn, Integer> xColToColumnIndex = null;

   public String getHtml(TreeItem items[]) throws OseeCoreException {
      StringBuffer sb = new StringBuffer("<html><body>");
      sb.append(AHTML.beginMultiColumnTable(100, 1));
      List<XViewerColumn> columns = xViewer.getCustomizeMgr().getCurrentTableColumnsInOrder();
      List<String> headerStrs = new ArrayList<String>(50);
      List<XViewerColumn> showCols = new ArrayList<XViewerColumn>(50);
      xColToColumnIndex = xViewer.getCustomizeMgr().getCurrentTableColumnsIndex();
      for (XViewerColumn xCol : columns) {
         if (xCol.isShow()) {
            showCols.add(xCol);
            headerStrs.add(xCol.getName());
         }
      }
      sb.append(AHTML.addHeaderRowMultiColumnTable(headerStrs.toArray(new String[headerStrs.size()])));
      // Get column widths and column name and setup the columns
      XViewerLabelProvider labelProv = (XViewerLabelProvider) xViewer.getLabelProvider();
      ArrayList<String[]> list = new ArrayList<String[]>();
      for (TreeItem item : items) {
         addRow(item, list, labelProv, showCols, 1);
      }
      for (String[] strs : list) {
         sb.append(AHTML.addRowMultiColumnTable(strs));
      }
      sb.append(AHTML.endMultiColumnTable());
      sb.append("</body></html>");
      return sb.toString();
   }

   public void addRow(TreeItem item, ArrayList<String[]> rowData, XViewerLabelProvider labelProv, List<XViewerColumn> showCols, int level) throws OseeCoreException {
      List<String> cellData = new ArrayList<String>(showCols.size());
      boolean firstCell = true;
      for (XViewerColumn xCol : showCols) {
         String str = "";
         if (firstCell) {
            for (int y = 0; y < level; y++) {
               str += "&nbsp;&nbsp;&nbsp;&nbsp;";
            }
            firstCell = false;
         }
         str += labelProv.getColumnText(item.getData(), xColToColumnIndex.get(xCol));
         cellData.add(str);
      }
      rowData.add(cellData.toArray(new String[cellData.size()]));
      if (item.getExpanded()) {
         for (TreeItem i : item.getItems()) {
            addRow(i, rowData, labelProv, showCols, level + 1);
         }
      }

   }

}
