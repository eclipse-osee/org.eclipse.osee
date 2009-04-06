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
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.osee.ats.artifact.VersionArtifact;
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
      VersionArtifact verArt = (VersionArtifact) element;
      Branch branch = verArt.getParentBranch();
      if (branch == null) return null;
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
      VersionArtifact verArt = (VersionArtifact) element;
      Branch branch = verArt.getParentBranch();
      if (xCol.equals(CommitXManagerFactory.Type_Col)) {
         if (branch == null)
            return "";
         else if (commitXManager.getWorkingBranch() != null && branch.equals(commitXManager.getWorkingBranch()))
            return "Working";
         else if (commitXManager.getWorkingBranch() != null && branch.equals(commitXManager.getWorkingBranch().getParentBranch()))
            return "Parent Baseline";
         else {
            return "Parallel Branch";
         }
      } else if (xCol.equals(CommitXManagerFactory.Status_Col)) {
         if (branch == null)
            return "";
         else if (branch.equals(commitXManager.getWorkingBranch()))
            return "";
         else if (isMergeNeeded(branch))
            return "Merge Needed";
         else
            return isCommittedInto(branch) ? "Committed" : "Commit Needed";
      } else if (xCol.equals(CommitXManagerFactory.Name_Col)) {
         if (branch == null)
            return verArt + " - " + (branch == null ? "Parent Branch Not Configured" : branch.getBranchShortName());
         else
            return branch.getBranchName();
      } else if (xCol.equals(CommitXManagerFactory.Short_Name_Col)) {
         return verArt + " - " + (branch == null ? "Parent Branch Not Configured" : branch.getBranchShortName());
      } else if (xCol.equals(CommitXManagerFactory.Action_Col)) {
         if (branch == null) {
            return "Configure Branch";
         } else if (true) {
            return "Start Commit";
         } else if (true) {
            return "Merge Conflicts";
         } else if (true) {
            return "Show Change Report";
         }
         return "Error: Need to handle this";
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

   public CommitXManager getTreeViewer() {
      return commitXManager;
   }

   private boolean isCommittedInto(Branch branch) {
      return false;
   }

   private boolean isMergeNeeded(Branch branch) {
      return false;
   }

   private Image getCommitStatusImage(Branch branch) throws OseeCoreException {
      if (branch == null)
         return null;
      else if (commitXManager.getWorkingBranch() != null && branch.equals(commitXManager.getWorkingBranch()))
         return null;
      else if (commitXManager.getWorkingBranch() != null && !isCommittedInto(branch)) {
         return isCommittedInto(branch) ? SkynetGuiPlugin.getInstance().getImage("green_light.gif") : SkynetGuiPlugin.getInstance().getImage(
               "red_light.gif");
      }
      return null;
   }
}
