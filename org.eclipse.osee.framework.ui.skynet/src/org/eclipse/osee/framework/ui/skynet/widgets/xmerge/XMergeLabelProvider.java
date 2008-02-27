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
package org.eclipse.osee.framework.ui.skynet.widgets.xmerge;

import java.sql.SQLException;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.osee.framework.skynet.core.conflict.AttributeConflict;
import org.eclipse.osee.framework.skynet.core.conflict.Conflict;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

public class XMergeLabelProvider implements ITableLabelProvider {
   Font font = null;

   private final MergeXViewer mergeXViewer;

   public XMergeLabelProvider(MergeXViewer mergeXViewer) {
      super();
      this.mergeXViewer = mergeXViewer;
   }

   public String getColumnText(Object element, int columnIndex) {
      if (element instanceof String) {
         if (columnIndex == 1)
            return (String) element;
         else
            return "";
      }

      XViewerColumn xCol = mergeXViewer.getXTreeColumn(columnIndex);
      if (xCol != null) {
         MergeColumn aCol = MergeColumn.getAtsXColumn(xCol);
         try {
            return getColumnText(element, columnIndex, xCol, aCol);
         } catch (SQLException ex) {
            OSEELog.logException(SkynetGuiPlugin.class, ex, false);
         }
      }
      return "";
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
   public String getColumnText(Object element, int columnIndex, XViewerColumn xCol, MergeColumn aCol) throws SQLException {
      if (!xCol.isShow()) return ""; // Since not shown, don't display

      if (element instanceof AttributeConflict) {
         AttributeConflict attributeChange = (AttributeConflict) element;
         if (aCol == MergeColumn.Artifact_Name) {
            return attributeChange.getArtifact().getDescriptiveName();
         } else if (aCol == MergeColumn.Change_Item) {
            return attributeChange.getDynamicAttributeDescriptor().getName();
         } else if (aCol == MergeColumn.Source) {
            return attributeChange.getSourceDisplayData();
         } else if (aCol == MergeColumn.Destination)
            return attributeChange.getDestDisplayData();
         else if (aCol == MergeColumn.Merged){ 
        	 return attributeChange.getArtifact().getSoleAttributeValue(attributeChange.getDynamicAttributeDescriptor().getName());
        	 }

      }
      return "";
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

   public MergeXViewer getTreeViewer() {
      return mergeXViewer;
   }

   private int x = 0;

   public Image getColumnImage(Object element, int columnIndex) {
      if (element instanceof String) return null;
      XViewerColumn xCol = mergeXViewer.getXTreeColumn(columnIndex);
      if (xCol == null) return null;
      MergeColumn dCol = MergeColumn.getAtsXColumn(xCol);
      if (!xCol.isShow()) return null; // Since not shown, don't display

      if (element instanceof Conflict) {
         Conflict conflict = (Conflict) element;

         if (dCol == MergeColumn.Artifact_Name) {
            try {
               return conflict.getArtifactImage();
            } catch (IllegalArgumentException ex) {
               OSEELog.logException(XMergeContentProvider.class, ex, true);
            } catch (SQLException ex) {
               OSEELog.logException(XMergeContentProvider.class, ex, true);
            }
         } else if (dCol == MergeColumn.Change_Item) {
            return conflict.getImage();
         } else if (dCol == MergeColumn.Source) {
            return SkynetGuiPlugin.getInstance().getImage("green_s.gif");
         } else if (dCol == MergeColumn.Destination) {
            return SkynetGuiPlugin.getInstance().getImage("blue_d.gif");
         } else if (dCol == MergeColumn.Merged) {
            if (x++ < 6)
               return SkynetGuiPlugin.getInstance().getImage("green_s.gif");
            else if (x < 15)
               return SkynetGuiPlugin.getInstance().getImage("blue_d.gif");
            else
               return SkynetGuiPlugin.getInstance().getImage("yellow_m.gif");
         } else if (dCol == MergeColumn.Conflict_Resolved) {
            if (x++ < 6)
               return SkynetGuiPlugin.getInstance().getImage("chkbox_disabled.gif");
            else if (x < 15)
               return SkynetGuiPlugin.getInstance().getImage("chkbox_enabled.gif");
            else
               return SkynetGuiPlugin.getInstance().getImage("chkbox_enabled_conflicted.gif");
         }
      }

      if (dCol == MergeColumn.Source) {
         //         return SkynetGuiPlugin.getInstance().getImage("branch.gif");
         return null;
      }
      return null;
   }

}
