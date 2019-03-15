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

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.nebula.widgets.xviewer.XViewerCells;
import org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.change.ArtifactChange;
import org.eclipse.osee.framework.skynet.core.change.AttributeChange;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.change.RelationChange;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.widgets.xHistory.column.HistoryTransactionDateColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xHistory.column.HistoryTransactionIdColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xchange.XChangeLabelProvider;
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
   private final Map<Object, Image> objectToImage = new HashMap<>(500);
   private static Image transactionImage = null;

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
            if (cCol.equals(HistoryXViewerFactory.gamma)) {
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
               toReturn = data.getModificationType().getName();
            } else if (cCol.equals(HistoryXViewerFactory.itemId)) {
               toReturn = String.valueOf(data.getItemId());
            } else if (cCol.equals(HistoryXViewerFactory.was)) {
               if (data instanceof AttributeChange && Strings.isValid(((AttributeChange) data).getWasUri())) {
                  toReturn = XChangeLabelProvider.LARGE;
               } else {
                  toReturn = data.getWasValue();
               }
            } else if (cCol.equals(HistoryXViewerFactory.is)) {
               if (data instanceof AttributeChange && Strings.isValid(((AttributeChange) data).getIsUri())) {
                  toReturn = XChangeLabelProvider.LARGE;
               } else {
                  toReturn = data.getIsValue();
               }
            } else {
               toReturn = super.getColumnText(element, columnIndex);
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
      if (xCol.getId().equals(HistoryTransactionDateColumn.ID)) {
         HistoryTransactionDateColumn column =
            ((HistoryXViewerFactory) ((HistoryXViewer) xCol.getXViewer()).getXViewerFactory()).getHistoryTransactionDateColumn();
         Date date = column.getTransactionDate(data.getTxDelta().getEndTx().getId());

         if (date == null) {
            column.populateCachedValues(Collections.singleton(element), column.getPreComputedValueMap());
            date = column.getTransactionDate(data.getTxDelta().getEndTx().getId());
         }

         return date;
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
      Image result = null;
      try {
         if (element instanceof Change) {
            if (xCol.getId().equals(HistoryTransactionIdColumn.ID)) {
               if (transactionImage == null) {
                  transactionImage = ImageManager.getImage(FrameworkImage.DB_ICON_BLUE);
               }
               result = transactionImage;
            } else if (xCol.equals(HistoryXViewerFactory.itemType)) {
               result = objectToImage.get(element);
               objectToImage.put(element, result);
            }
         }
      } catch (Exception ex) {
         // do nothing
      }
      return result;
   }

   @Override
   public Color getBackground(Object element, int columnIndex) {
      Color searchColor = null;
      try {
         if (historyXViewer.isSearch()) {
            searchColor = getSearchBackground(element, columnIndex);
         }
      } catch (Exception ex) {
         //do nothing
      }

      if (historyXViewer.isSortByTransaction() && searchColor == null) {
         Change change = (Change) element;
         long transactionId = change.getTxDelta().getEndTx().getId();
         if (historyXViewer.getXHistoryViewer().isShaded(transactionId)) {
            return getLightGreyColor();
         } else {
            return super.getBackground(element, columnIndex);
         }
      } else if (searchColor != null) {
         return searchColor;
      } else {
         return super.getBackground(element, columnIndex);
      }
   }

   private Color getLightGreyColor() {
      if (lightGreyColor == null) {
         lightGreyColor = Displays.getColor(234, 234, 234);
      }
      return lightGreyColor;
   }

   public void calculateImages(Collection<Change> changes) {
      for (Change change : changes) {
         Image result = ArtifactImageManager.getChangeTypeImage(change);
         objectToImage.put(change, result);
      }
   }

}
