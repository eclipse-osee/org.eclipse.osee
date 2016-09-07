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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.widgets.xHistory.column.HistoryTransactionIdColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.OseeTreeReportAdapter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Jeff C. Phillips
 */
public class HistoryXViewer extends XViewer implements IHistoryTransactionProvider {
   private final XHistoryWidget xHistoryViewer;
   private final IHistoryTransactionProvider txCache;

   public HistoryXViewer(Composite parent, int style, XHistoryWidget xRoleViewer) {
      super(parent, style, new HistoryXViewerFactory(new OseeTreeReportAdapter("Table Report - History View"),
         new HistoryTransactionCache()));
      this.xHistoryViewer = xRoleViewer;
      txCache = ((HistoryXViewerFactory) getXViewerFactory()).getTxCache();
   }

   @Override
   public void handleDoubleClick() {
      if (getSelectedChanges().isEmpty()) {
         return;
      }

      Artifact artifact = getSelectedChanges().iterator().next();

      if (artifact != null) {
         RendererManager.openInJob(artifact, PresentationType.DEFAULT_OPEN);
      }
   }

   public ArrayList<Artifact> getSelectedChanges() {
      ArrayList<Artifact> arts = new ArrayList<>();
      TreeItem items[] = getTree().getSelection();

      if (items.length > 0) {
         for (TreeItem item : items) {
            Artifact artifact = null;
            if (item.getData() instanceof IAdaptable) {
               artifact = ((IAdaptable) item.getData()).getAdapter(Artifact.class);

               if (artifact != null) {
                  arts.add(artifact);
               }
            }
         }
      }
      return arts;
   }

   @Override
   protected void createSupportWidgets(Composite parent) {
      super.createSupportWidgets(parent);
      createMenuActions();
   }

   public void createMenuActions() {
      MenuManager mm = getMenuManager();
      mm.createContextMenu(getControl());
      mm.addMenuListener(new IMenuListener() {
         @Override
         public void menuAboutToShow(IMenuManager manager) {
            updateMenuActionsForTable();
         }
      });
   }

   @Override
   public void updateMenuActionsForTable() {
      MenuManager mm = getMenuManager();
      mm.insertBefore(MENU_GROUP_PRE, new Separator());
   }

   @Override
   public void dispose() {
      if (getLabelProvider() != null) {
         getLabelProvider().dispose();
      }
   }

   public XHistoryWidget getXHistoryViewer() {
      return xHistoryViewer;
   }

   public boolean isSortByTransaction() {
      List<XViewerColumn> sortXCols = xHistoryViewer.getXViewer().getCustomizeMgr().getSortXCols();
      for (XViewerColumn col : sortXCols) {
         if (col.getId().equals(HistoryTransactionIdColumn.ID)) {
            return true;
         }
      }
      return false;
   }

   public boolean isDisposed() {
      return getTree() == null || getTree().isDisposed();
   }

   @Override
   public void refresh() {
      if (isDisposed()) {
         return;
      }
      super.refreshColumnsWithPreCompute(getInput());
   }

   @Override
   public TransactionRecord getTransactionRecord(Long id) {
      return txCache.getTransactionRecord(id);
   }

   public IHistoryTransactionProvider getTxCache() {
      return txCache;
   }

   @Override
   public void put(Long id, TransactionRecord transaction) {
      txCache.put(id, transaction);
   }

}
