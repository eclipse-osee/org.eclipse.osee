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
package org.eclipse.osee.framework.ui.skynet.widgets.xcommit;

import java.sql.SQLException;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerCells;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

public class XCommitLabelProvider implements ITableLabelProvider {
   Font font = null;

   private final CommitXViewer commitXViewer;

   public XCommitLabelProvider(CommitXViewer commitXViewer) {
      super();
      this.commitXViewer = commitXViewer;
   }

   public String getColumnText(Object element, int columnIndex) {
      if (element instanceof String) {
         if (columnIndex == 1)
            return (String) element;
         else
            return "";
      }
      Branch branch = ((Branch) element);
      if (branch == null) return "";
      XViewerColumn xCol = commitXViewer.getXTreeColumn(columnIndex);
      if (xCol != null) {
         CommitColumn aCol = CommitColumn.getAtsXColumn(xCol);
         try {
            return getColumnText(element, columnIndex, branch, xCol, aCol);
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
   public String getColumnText(Object element, int columnIndex, Branch branch, XViewerColumn xCol, CommitColumn aCol) throws SQLException {
      if (!xCol.isShow()) return ""; // Since not shown, don't display
      if (aCol == CommitColumn.Type_Col) {
         if (branch.equals(commitXViewer.getWorkingBranch()))
            return "Working";
         else if (branch.equals(commitXViewer.getWorkingBranch().getParentBranch()))
            return "Parent Baseline";
         else {
            try {
               if (branch.isBaselineBranch()) return "Baseline";
            } catch (Exception ex) {
               return XViewerCells.getCellExceptionString(ex);
            }
         }
         return "";
      } else if (aCol == CommitColumn.Status_Col) {
         if (branch.equals(commitXViewer.getWorkingBranch()))
            return "";
         else if (branch.equals(commitXViewer.getWorkingBranch().getParentBranch()) || branch.isBaselineBranch()) return isCommittedInto(branch) ? "Committed" : "UnCommitted";
         return "";
      } else if (aCol == CommitColumn.Name_Col)
         return branch.getBranchName();
      else if (aCol == CommitColumn.Short_Name_Col) return branch.getBranchShortName();
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

   public CommitXViewer getTreeViewer() {
      return commitXViewer;
   }

   public Image getColumnImage(Object element, int columnIndex) {
      if (element instanceof String) return null;
      Branch branch = (Branch) element;
      XViewerColumn xCol = commitXViewer.getXTreeColumn(columnIndex);
      if (xCol == null) return null;
      CommitColumn dCol = CommitColumn.getAtsXColumn(xCol);
      if (!xCol.isShow()) return null; // Since not shown, don't display
      if (dCol == CommitColumn.Name_Col) {
         if (branch.equals(commitXViewer.getWorkingBranch())) return SkynetGuiPlugin.getInstance().getImage(
               "nav_forward.gif");
         return SkynetGuiPlugin.getInstance().getImage("branch.gif");
      } else if (dCol == CommitColumn.Status_Col) {
         try {
            return getCommitStatusImage(branch);
         } catch (Exception ex) {
            OSEELog.logException(SkynetGuiPlugin.class, ex, false);
         }
      }
      return null;
   }

   private boolean isCommittedInto(Branch branch) {
      return !branch.getBranchName().equals("ftb2");
   }

   private Image getCommitStatusImage(Branch branch) throws IllegalArgumentException, SQLException {
      if (branch.equals(commitXViewer.getWorkingBranch()))
         return null;
      else if (branch.equals(commitXViewer.getWorkingBranch().getParentBranch()) || branch.isBaselineBranch()) return isCommittedInto(branch) ? SkynetGuiPlugin.getInstance().getImage(
            "green_light.gif") : SkynetGuiPlugin.getInstance().getImage("red_light.gif");
      return null;
   }
}
