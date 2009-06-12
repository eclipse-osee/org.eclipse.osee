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

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.nebula.widgets.xviewer.XViewerCells;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.revision.HistoryTransactionItem;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * @author Jeff C. Phillips
 */
public class XHistoryLabelProvider extends XViewerLabelProvider {

   Font font = null;
   private final HistoryXViewer changeXViewer;

   public XHistoryLabelProvider(HistoryXViewer changeXViewer) {
      super(changeXViewer);
      this.changeXViewer = changeXViewer;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn cCol, int columnIndex) throws OseeCoreException {
      try {
         if (!(element instanceof HistoryTransactionItem)) return "";
         HistoryTransactionItem data = (HistoryTransactionItem) element;

         if (cCol.equals(HistoryXViewerFactory.transaction)) {
            return String.valueOf(data.getTransactionNumber());
         } else if (cCol.equals(HistoryXViewerFactory.gamma)) {
            return String.valueOf(data.getGamma());
         } else if (cCol.equals(HistoryXViewerFactory.itemType)) {
            return data.getChangeType();
         } else if (cCol.equals(HistoryXViewerFactory.was)) {
            return data.getWasValue();
         } else if (cCol.equals(HistoryXViewerFactory.is)) {
            return data.getIsValue();
         } else if (cCol.equals(HistoryXViewerFactory.timeStamp)) {
            return data.getTimeStamp();
         } else if (cCol.equals(HistoryXViewerFactory.author)) {
            return data.getAuthorName();
         } else if (cCol.equals(HistoryXViewerFactory.comment)) {
            return data.getComment();
         }
      } catch (Exception ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
      return "unhandled column";
   }

   public void dispose() {
      if (font != null) font.dispose();
      font = null;
   }

   public boolean isLabelProperty(Object element, String property) {
      return false;
   }

   public void addListener(ILabelProviderListener listener) {
   }

   public void removeListener(ILabelProviderListener listener) {
   }

   public HistoryXViewer getTreeViewer() {
      return changeXViewer;
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn xCol, int columnIndex) throws OseeCoreException {
      try {
         if (!(element instanceof HistoryTransactionItem)) return null;
         HistoryTransactionItem change = (HistoryTransactionItem) element;
         if (xCol.equals(HistoryXViewerFactory.transaction)) {
            try {
               return ImageManager.getImage(FrameworkImage.DB_ICON_BLUE);
            } catch (IllegalArgumentException ex) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            } catch (Exception ex) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         } else if (xCol.equals(HistoryXViewerFactory.itemType)) {
            return ImageManager.getChangeImage(change.getRevisionChange());
         }

      } catch (Exception ex) {
         // do nothing
      }
      return null;
   }
}
