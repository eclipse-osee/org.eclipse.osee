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
package org.eclipse.osee.ats.util.widgets.defect;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.util.widgets.defect.DefectItem.Disposition;
import org.eclipse.osee.ats.util.widgets.defect.DefectItem.InjectionActivity;
import org.eclipse.osee.ats.util.widgets.defect.DefectItem.Severity;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

public class DefectLabelProvider implements ITableLabelProvider {
   Font font = null;

   private final DefectXViewer treeViewer;

   public DefectLabelProvider(DefectXViewer treeViewer) {
      super();
      this.treeViewer = treeViewer;
   }

   public String getColumnText(Object element, int columnIndex) {
      if (element instanceof String) {
         if (columnIndex == 1)
            return (String) element;
         else
            return "";
      }
      DefectItem defectItem = ((DefectItem) element);
      if (defectItem == null) return "";
      XViewerColumn xCol = treeViewer.getXTreeColumn(columnIndex);
      if (xCol != null) {
         DefectColumn aCol = DefectColumn.getAtsXColumn(xCol);
         return getColumnText(element, columnIndex, defectItem, xCol, aCol);
      }
      return "";
   }

   /**
    * Provided as optimization of subclassed classes so provider doesn't have to retrieve the same information that has
    * already been retrieved
    * 
    * @param element
    * @param columnIndex
    * @param defectItem
    * @param xCol
    * @param aCol
    * @return column string
    */
   public String getColumnText(Object element, int columnIndex, DefectItem defectItem, XViewerColumn xCol, DefectColumn aCol) {
      if (!xCol.isShow()) return ""; // Since not shown, don't display
      if (aCol == DefectColumn.User_Col)
         return defectItem.getUser().getName();
      else if (aCol == DefectColumn.Closed_Col)
         return String.valueOf(defectItem.isClosed());
      else if (aCol == DefectColumn.Created_Date_Col)
         return defectItem.getCreatedDate(XDate.MMDDYYHHMM);
      else if (aCol == DefectColumn.Description_Col)
         return defectItem.getDescription();
      else if (aCol == DefectColumn.Resolution_Col)
         return defectItem.getResolution();
      else if (aCol == DefectColumn.Location_Col)
         return defectItem.getLocation();
      else if (aCol == DefectColumn.Severity_Col)
         return defectItem.getSeverity() == Severity.None ? "" : defectItem.getSeverity().name();
      else if (aCol == DefectColumn.Disposition_Col)
         return defectItem.getDisposition() == Disposition.None ? "" : defectItem.getDisposition().name();
      else if (aCol == DefectColumn.Injection_Activity_Col) return defectItem.getInjectionActivity() == InjectionActivity.None ? "" : defectItem.getInjectionActivity().name();

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

   public DefectXViewer getTreeViewer() {
      return treeViewer;
   }

   public Image getColumnImage(Object element, int columnIndex) {
      if (element instanceof String) return null;
      DefectItem defectItem = (DefectItem) element;
      XViewerColumn xCol = treeViewer.getXTreeColumn(columnIndex);
      if (xCol == null) return null;
      DefectColumn dCol = DefectColumn.getAtsXColumn(xCol);
      if (!xCol.isShow()) return null; // Since not shown, don't display
      if (dCol == DefectColumn.Severity_Col)
         return Severity.getImage(defectItem.getSeverity());
      else if (dCol == DefectColumn.Injection_Activity_Col)
         return AtsPlugin.getInstance().getImage("info.gif");
      else if (dCol == DefectColumn.Disposition_Col)
         return Disposition.getImage(defectItem.getDisposition());
      else if (dCol == DefectColumn.Closed_Col) {
         return defectItem.isClosed() ? SkynetGuiPlugin.getInstance().getImage("chkbox_enabled.gif") : SkynetGuiPlugin.getInstance().getImage(
               "chkbox_disabled.gif");
      } else if (dCol == DefectColumn.User_Col) {
         if (defectItem.getUser().equals(SkynetAuthentication.getUser()))
            return SkynetGuiPlugin.getInstance().getImage("red_user_sm.gif");
         else
            return SkynetGuiPlugin.getInstance().getImage("user_sm.gif");
      }
      return null;
   }
}
