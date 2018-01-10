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
package org.eclipse.osee.framework.ui.skynet.widgets.xHistory;

import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerSorter;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xHistory.column.HistoryTransactionAuthorColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xHistory.column.HistoryTransactionBuildIdColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xHistory.column.HistoryTransactionCommentColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xHistory.column.HistoryTransactionDateColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xHistory.column.HistoryTransactionIdColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IOseeTreeReportProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;

/**
 * @author Jeff C. Phillips
 */
public class HistoryXViewerFactory extends SkynetXViewerFactory {
   private final HistoryTransactionDateColumn historyTransactionDateColumn;
   public final static XViewerColumn gamma = new XViewerColumn("framework.history.gamma", "Gamma", 60,
      XViewerAlign.Left, false, SortDataType.Integer, false, null);
   public final static XViewerColumn itemType = new XViewerColumn("framework.history.itemType", "Item Type", 150,
      XViewerAlign.Left, true, SortDataType.String, false, null);
   public final static XViewerColumn itemChange = new XViewerColumn("framework.history.itemChange", "Item Kind", 100,
      XViewerAlign.Left, true, SortDataType.String, false, null);
   public final static XViewerColumn modType = new XViewerColumn("framework.history.modType", "Mod Type", 70,
      XViewerAlign.Left, true, SortDataType.String, false, null);
   public final static XViewerColumn itemId = new XViewerColumn("framework.history.itemId", "Item ID", 55,
      XViewerAlign.Left, true, SortDataType.Integer, false, null);
   public final static XViewerColumn was =
      new XViewerColumn("framework.history.was", "Was", 150, XViewerAlign.Left, true, SortDataType.String, false, null);
   public final static XViewerColumn is =
      new XViewerColumn("framework.history.is", "Is", 150, XViewerAlign.Left, true, SortDataType.String, false, null);

   public final static String NAMESPACE = "osee.skynet.gui.HisotryXViewer";
   private final IHistoryTransactionProvider txCache;

   public HistoryXViewerFactory(IOseeTreeReportProvider reportProvider, IHistoryTransactionProvider txCache) {
      super(NAMESPACE, reportProvider);
      this.txCache = txCache;
      historyTransactionDateColumn = new HistoryTransactionDateColumn(txCache);
      registerColumns(new HistoryTransactionIdColumn(txCache), gamma, itemType, itemChange, modType, itemId, was, is,
         historyTransactionDateColumn, new HistoryTransactionAuthorColumn(txCache),
         new HistoryTransactionCommentColumn(txCache), new HistoryTransactionBuildIdColumn(txCache));
      registerAllAttributeColumns();
   }

   @Override
   public XViewerSorter createNewXSorter(XViewer xViewer) {
      return new XViewerSorter(xViewer);
   }

   @Override
   public CustomizeData getDefaultTableCustomizeData() {
      CustomizeData customizeData = super.getDefaultTableCustomizeData();
      for (XViewerColumn xCol : customizeData.getColumnData().getColumns()) {
         if (xCol.getId().equals(HistoryTransactionIdColumn.ID)) {
            xCol.setSortForward(false);
         }
      }
      customizeData.getSortingData().setSortingNames(HistoryTransactionIdColumn.ID);
      return customizeData;
   }

   public HistoryTransactionDateColumn getHistoryTransactionDateColumn() {
      return historyTransactionDateColumn;
   }

   public IHistoryTransactionProvider getTxCache() {
      return txCache;
   }

}
