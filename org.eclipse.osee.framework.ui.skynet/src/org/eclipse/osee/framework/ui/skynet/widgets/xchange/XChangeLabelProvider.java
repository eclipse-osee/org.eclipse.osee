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

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xmerge.XMergeContentProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerCells;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerValueColumn;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TreeColumn;

public class XChangeLabelProvider implements ITableLabelProvider {
   Font font = null;

   private final ChangeXViewer changeXViewer;

   public XChangeLabelProvider(ChangeXViewer changeXViewer) {
      super();
      this.changeXViewer = changeXViewer;
   }

   public String getColumnText(Object element, int columnIndex) {
      try {
         TreeColumn treeCol = getTreeViewer().getTree().getColumn(columnIndex);
         if (treeCol.getData() instanceof XViewerValueColumn) {
            return ((XViewerValueColumn) treeCol.getData()).getColumnText(element,
                  (XViewerValueColumn) treeCol.getData());
         }
         if (element instanceof String) {
            if (columnIndex == 1)
               return (String) element;
            else
               return "";
         } else if (element instanceof Change) {
            Change change = (Change) element;
            XViewerColumn xCol = changeXViewer.getXTreeColumn(columnIndex);
            if (xCol == null) return "Can't determine XTreeColumn";
            ChangeColumn cCol = ChangeColumn.getAtsXColumn(xCol);
            if (cCol == null) return "Can't determine ChangeColumn";
            if (cCol == ChangeColumn.Empty) {
               return "";
            } else if (cCol == ChangeColumn.Name) {
               return change.getName();
            } else if (cCol == ChangeColumn.Change_Type) {
               return change.getModificationType().getDisplayName();
            } else if (cCol == ChangeColumn.Item_Kind) {
               return change.getItemKind();
            } else if (cCol == ChangeColumn.Item_Type) {
               return change.getItemTypeName();
            } else if (cCol == ChangeColumn.Is_Value) {
               return change.getIsValue();
            } else if (cCol == ChangeColumn.Was_Value) {
               return change.getWasValue();
            }
            // TODO Temporary column until dynamic attributes can be added
            else if (cCol == ChangeColumn.CSCI) {
               Artifact art = change.getArtifact();
               if (art != null) {
                  if (art.isAttributeTypeValid("CSCI"))
                     return art.getAttributesToString("CSCI");
                  else if (art.isAttributeTypeValid("Partition"))
                     return art.getAttributesToString("Partition");
                  else
                     return "";
               } else
                  return XViewerCells.getCellExceptionString("Artifact was null.");
            }
         }
      } catch (Exception ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
      return "Unknown Column";
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
      try {
         TreeColumn treeCol = getTreeViewer().getTree().getColumn(columnIndex);
         if (treeCol.getData() instanceof XViewerValueColumn) {
            return ((XViewerValueColumn) treeCol.getData()).getColumnImage(element,
                  (XViewerValueColumn) treeCol.getData());
         }
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
      } catch (Exception ex) {
         // do nothing
      }
      return null;
   }
}
