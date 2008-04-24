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
package org.eclipse.osee.framework.ui.skynet.widgets.xchange;

import java.sql.SQLException;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xmerge.XMergeContentProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

public class XChangeLabelProvider implements ITableLabelProvider {
   Font font = null;

   private final ChangeXViewer changeXViewer;

   public XChangeLabelProvider(ChangeXViewer changeXViewer) {
      super();
      this.changeXViewer = changeXViewer;
   }

   public String getColumnText(Object element, int columnIndex) {
      String text = "";

      if (element instanceof String) {
         text = (String) element;
      }
      try {
         if (element instanceof Change) {
            Change change = (Change) element;

            if (columnIndex == 0) {
               text = change.getName();
            } else if (columnIndex == 1) {
               text = change.getItemTypeName();
            } else if (columnIndex == 2) {
               text = change.getItemKind();
            } else if (columnIndex == 3) {
               text = change.getTransactionType().toString();
            } else if (columnIndex == 4) {
               text = change.getValue();
            }
         }
      } catch (SQLException exception) {

      }
      return text;
   }

   /**
    * Provided as optimization of subclassed classes so provider doesn't have to retrieve the same information that has
    * already been retrieved
    * 
    * @param element
    * @param columnIndex
    * @param branch
    * @param xCol
    * @param aCol
    * @return column string
    * @throws SQLException
    */
   public String getColumnText(Object element, int columnIndex, Branch branch, XViewerColumn xCol, ChangeColumn aCol) throws SQLException {
      if (!xCol.isShow()) return ""; // Since not shown, don't display
      if (aCol == ChangeColumn.Name) {
         return ChangeColumn.Name.getName();
      } else if (aCol == ChangeColumn.Item_Type) {
         return ChangeColumn.Item_Type.getName();
      } else if (aCol == ChangeColumn.Value) {
         return ChangeColumn.Value.getName();
      } else if (aCol == ChangeColumn.Item_Kind) {
         return ChangeColumn.Item_Kind.getName();
      } else if (aCol == ChangeColumn.Change_Type) {
         return ChangeColumn.Change_Type.getName();
      }
      return "Unhandled Column";
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

   public ChangeXViewer getTreeViewer() {
      return changeXViewer;
   }

   public Image getColumnImage(Object element, int columnIndex) {
      if (element instanceof String) return null;
      XViewerColumn xCol = changeXViewer.getXTreeColumn(columnIndex);
      if (xCol == null) return null;
      ChangeColumn dCol = ChangeColumn.getAtsXColumn(xCol);
      if (!xCol.isShow()) return null; // Since not shown, don't display

      if (element instanceof Change) {
         Change change = (Change) element;

         if (dCol == ChangeColumn.Name) {
            try {
               return change.getItemKindImage();
            } catch (IllegalArgumentException ex) {
               OSEELog.logException(XMergeContentProvider.class, ex, true);
            } catch (Exception ex) {
               OSEELog.logException(XMergeContentProvider.class, ex, true);
            }
         } else if (dCol == ChangeColumn.Item_Type) {
            return change.getItemTypeImage();
         }
      }

      return null;
   }
}
