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
package org.eclipse.osee.ats.util.widgets.commit;

import java.util.logging.Level;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.nebula.widgets.xviewer.XViewerCells;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

public class XCommitLabelProvider extends XViewerLabelProvider {
   Font font = null;

   private final CommitXManager commitXManager;

   public XCommitLabelProvider(CommitXManager commitXManager) {
      super(commitXManager);
      this.commitXManager = commitXManager;
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn xCol, int columnIndex) throws OseeCoreException {
      Branch branch = ((Branch) element);
      if (xCol.equals(CommitXManagerFactory.Name_Col)) {
         if (branch.equals(commitXManager.getWorkingBranch())) return SkynetGuiPlugin.getInstance().getImage(
               "nav_forward.gif");
         return SkynetGuiPlugin.getInstance().getImage("branch.gif");
      } else if (xCol.equals(CommitXManagerFactory.Status_Col)) {
         try {
            return getCommitStatusImage(branch);
         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         }
      }
      return null;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn xCol, int columnIndex) throws OseeCoreException {
      Branch branch = ((Branch) element);
      if (xCol.equals(CommitXManagerFactory.Type_Col)) {
         if (branch.equals(commitXManager.getWorkingBranch()))
            return "Working";
         else if (branch.equals(commitXManager.getWorkingBranch().getParentBranch()))
            return "Parent Baseline";
         else {
            try {
               if (branch.isBaselineBranch()) return "Baseline";
            } catch (Exception ex) {
               return XViewerCells.getCellExceptionString(ex);
            }
         }
         return "Unknown";
      } else if (xCol.equals(CommitXManagerFactory.Status_Col)) {
         if (branch.equals(commitXManager.getWorkingBranch()))
            return "";
         else if (branch.equals(commitXManager.getWorkingBranch().getParentBranch()) || branch.isBaselineBranch()) return isCommittedInto(branch) ? "Committed" : "UnCommitted";
         return "";
      } else if (xCol.equals(CommitXManagerFactory.Name_Col))
         return branch.getBranchName();
      else if (xCol.equals(CommitXManagerFactory.Short_Name_Col)) return branch.getBranchShortName();
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

   public CommitXManager getTreeViewer() {
      return commitXManager;
   }

   private boolean isCommittedInto(Branch branch) {
      return !branch.getBranchName().equals("ftb2");
   }

   private Image getCommitStatusImage(Branch branch) throws OseeCoreException {
      if (branch.equals(commitXManager.getWorkingBranch()))
         return null;
      else if (branch.equals(commitXManager.getWorkingBranch().getParentBranch()) || branch.isBaselineBranch()) {
         return isCommittedInto(branch) ? SkynetGuiPlugin.getInstance().getImage("green_light.gif") : SkynetGuiPlugin.getInstance().getImage(
               "red_light.gif");
      }
      return null;
   }
}
