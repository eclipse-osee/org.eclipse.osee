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
import org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.change.ArtifactChange;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.change.RelationChange;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

/**
 * @author Jeff C. Phillips
 */
public class XHistoryLabelProvider extends XViewerLabelProvider {

   private final HistoryXViewer historyXViewer;
   private static Color lightGreyColor;

   public XHistoryLabelProvider(HistoryXViewer historyXViewer) {
      super(historyXViewer);
      this.historyXViewer = historyXViewer;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn cCol, int columnIndex) {
      String toReturn = "";
      try {
         if (element instanceof Change) {
            Change data = (Change) element;

            if (cCol.equals(HistoryXViewerFactory.transaction)) {
               toReturn = String.valueOf(data.getTxDelta().getEndTx().getId());
            } else if (cCol.equals(HistoryXViewerFactory.gamma)) {
               toReturn = String.valueOf(data.getGamma());
            } else if (cCol.equals(HistoryXViewerFactory.itemType)) {
               if (data instanceof ArtifactChange && data.getChangeArtifact() == null) {
                  toReturn = "Artifact Does Exist In This Transaction";
               } else {
                  toReturn = data instanceof RelationChange ? data.getName() : data.getItemTypeName();
               }
            } else if (cCol.equals(HistoryXViewerFactory.itemChange)) {
               toReturn = data.getItemKind();
            } else if (cCol.equals(HistoryXViewerFactory.modType)) {
               toReturn = data.getModificationType().getDisplayName();
            } else if (cCol.equals(HistoryXViewerFactory.itemId)) {
               toReturn = String.valueOf(data.getItemId());
            } else if (cCol.equals(HistoryXViewerFactory.was)) {
               toReturn = data.getWasValue();
            } else if (cCol.equals(HistoryXViewerFactory.is)) {
               toReturn = data.getIsValue();
            } else if (cCol.equals(HistoryXViewerFactory.timeStamp)) {
               toReturn =
                  new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a").format(data.getTxDelta().getEndTx().getTimeStamp());
            } else if (cCol.equals(HistoryXViewerFactory.author)) {
               toReturn = UserManager.getSafeUserNameById(data.getTxDelta().getEndTx().getAuthor());
            } else if (cCol.equals(HistoryXViewerFactory.comment)) {
               toReturn = data.getTxDelta().getEndTx().getComment();
            } else {
               toReturn = "unhandled column";
            }
         }
      } catch (Exception ex) {
         toReturn = XViewerCells.getCellExceptionString(ex);
      }
      return toReturn;
   }

   /**
    * Provides the XViewerSorter the actual Date object to sort instead of having to convert the text back to Date (and
    * loose the precision)
    */
   @Override
   public Object getBackingData(Object element, XViewerColumn xCol, int columnIndex) throws Exception {
      if (!(element instanceof Change)) {
         return "";
      }
      Change data = (Change) element;
      if (xCol.equals(HistoryXViewerFactory.timeStamp)) {
         return data.getTxDelta().getEndTx().getTimeStamp();
      }
      return super.getBackingData(element, xCol, columnIndex);
   }

   @Override
   public void dispose() {
      // do nothing
   }

   @Override
   public boolean isLabelProperty(Object element, String property) {
      return false;
   }

   @Override
   public void addListener(ILabelProviderListener listener) {
      // do nothing
   }

   @Override
   public void removeListener(ILabelProviderListener listener) {
      // do nothing
   }

   public HistoryXViewer getTreeViewer() {
      return historyXViewer;
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn xCol, int columnIndex) {
      try {
         if (!(element instanceof Change)) {
            return null;
         }
         Change change = (Change) element;
         if (xCol.equals(HistoryXViewerFactory.transaction)) {
            return ImageManager.getImage(FrameworkImage.DB_ICON_BLUE);
         } else if (xCol.equals(HistoryXViewerFactory.itemType)) {
            return ArtifactImageManager.getChangeTypeImage(change);
         }

      } catch (Exception ex) {
         // do nothing
      }
      return null;
   }

   @Override
   public Color getBackground(Object element, int columnIndex) {
      if (historyXViewer.isSortByTransaction()) {
         Change change = (Change) element;
         long transactionId = change.getTxDelta().getEndTx().getId();
         if (historyXViewer.getXHisotryViewer().isShaded(transactionId)) {
            return getLightGreyColor();
         }
      }
      return super.getBackground(element, columnIndex);
   }

   private Color getLightGreyColor() {
      if (lightGreyColor == null) {
         lightGreyColor = Displays.getColor(234, 234, 234);
      }
      return lightGreyColor;
   }

}
