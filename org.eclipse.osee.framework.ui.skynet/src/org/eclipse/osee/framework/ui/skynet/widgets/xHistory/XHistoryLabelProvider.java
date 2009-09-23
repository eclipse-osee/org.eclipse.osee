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

import java.text.SimpleDateFormat;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.nebula.widgets.xviewer.XViewerCells;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.change.RelationChange;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.swt.graphics.Image;

/**
 * @author Jeff C. Phillips
 */
public class XHistoryLabelProvider extends XViewerLabelProvider {
   private final HistoryXViewer changeXViewer;

   public XHistoryLabelProvider(HistoryXViewer changeXViewer) {
      super(changeXViewer);
      this.changeXViewer = changeXViewer;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn cCol, int columnIndex) throws OseeCoreException {
      try {
         if (!(element instanceof Change)) {
            return "";
         }
         Change data = (Change) element;

         if (cCol.equals(HistoryXViewerFactory.transaction)) {
            return String.valueOf(data.getToTransactionId().getTransactionNumber());
         } else if (cCol.equals(HistoryXViewerFactory.gamma)) {
            return String.valueOf(data.getGamma());
         } else if (cCol.equals(HistoryXViewerFactory.itemType)) {
            return data instanceof RelationChange ? data.getName() : data.getItemTypeName();
         } else if (cCol.equals(HistoryXViewerFactory.itemChange)) {
            return data.getItemKind();
         } else if (cCol.equals(HistoryXViewerFactory.modType)) {
            return data.getModificationType().getDisplayName();
         } else if (cCol.equals(HistoryXViewerFactory.itemId)) {
            return String.valueOf(data.getItemId());
         } else if (cCol.equals(HistoryXViewerFactory.was)) {
            return data.getWasValue();
         } else if (cCol.equals(HistoryXViewerFactory.is)) {
            return data.getIsValue();
         } else if (cCol.equals(HistoryXViewerFactory.timeStamp)) {
            return new SimpleDateFormat("MM/dd/yyyy hh:mm a").format(data.getToTransactionId().getDate());
         } else if (cCol.equals(HistoryXViewerFactory.author)) {
            return UserManager.getUserNameById(data.getToTransactionId().getAuthorArtId());
         } else if (cCol.equals(HistoryXViewerFactory.comment)) {
            return data.getToTransactionId().getComment();
         }
      } catch (Exception ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
      return "unhandled column";
   }

   public void dispose() {
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
         if (!(element instanceof Change)) {
            return null;
         }
         Change change = (Change) element;
         if (xCol.equals(HistoryXViewerFactory.transaction)) {
            return ImageManager.getImage(FrameworkImage.DB_ICON_BLUE);
         } else if (xCol.equals(HistoryXViewerFactory.itemType)) {
            return ImageManager.getChangeTypeImage(change);
         }

      } catch (Exception ex) {
         // do nothing
      }
      return null;
   }
}
