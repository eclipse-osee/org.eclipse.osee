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

import java.util.Collection;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

public class XCommitLabelProvider extends XViewerLabelProvider {
   Font font = null;

   private final CommitXManager commitXManager;
   public static enum CommitStatus {
      Branch_Not_Configured("Branch Not Configured"),
      Commit_Needed("Start Commit"),
      Merge_In_Progress("Merge Needed"),
      Committed("Committed");

      private final String displayName;

      private CommitStatus(String displayName) {
         this.displayName = displayName;
      }

      /**
       * @return the displayName
       */
      public String getDisplayName() {
         return displayName;
      }
   };

   public XCommitLabelProvider(CommitXManager commitXManager) {
      super(commitXManager);
      this.commitXManager = commitXManager;
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn xCol, int columnIndex) throws OseeCoreException {
      VersionArtifact verArt = (VersionArtifact) element;
      Branch branch = verArt.getParentBranch();
      if (xCol.equals(CommitXManagerFactory.Action_Col)) {
         return SkynetGuiPlugin.getInstance().getImage("nav_forward.gif");
      }
      if (branch == null) return null;
      if (xCol.equals(CommitXManagerFactory.Status_Col)) {
         try {
            CommitStatus commitStatus = getCommitStatus(commitXManager.getXCommitViewer().getTeamArt(), verArt);
            if (commitStatus == CommitStatus.Branch_Not_Configured)
               return SkynetGuiPlugin.getInstance().getImage("red_light.gif");
            else if (commitStatus == CommitStatus.Commit_Needed)
               return SkynetGuiPlugin.getInstance().getImage("red_light.gif");
            else if (commitStatus == CommitStatus.Merge_In_Progress)
               return SkynetGuiPlugin.getInstance().getImage("yellow_light.gif");
            else if (commitStatus == CommitStatus.Committed) {
               return SkynetGuiPlugin.getInstance().getImage("green_light.gif");
            }
            return null;
         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         }
      } else if (xCol.equals(CommitXManagerFactory.Merge_Col)) {
         try {
            CommitStatus commitStatus = getCommitStatus(commitXManager.getXCommitViewer().getTeamArt(), verArt);
            if (commitStatus == CommitStatus.Merge_In_Progress) {
               return SkynetGuiPlugin.getInstance().getImage("branch_merge.gif");
            }
            return null;
         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         }
      }
      return null;
   }

   public static CommitStatus getCommitStatus(TeamWorkFlowArtifact teamArt, VersionArtifact verArt) throws OseeCoreException {
      Branch branch = verArt.getParentBranch();
      if (branch == null)
         return CommitStatus.Branch_Not_Configured;
      else if (teamArt.getSmaMgr().getBranchMgr().isMergeBranchExists(verArt.getParentBranch()))
         return CommitStatus.Merge_In_Progress;
      else {
         Set<Branch> branches = BranchManager.getAssociatedArtifactBranches(teamArt, false);
         if (branches.contains(branch)) {
            return CommitStatus.Committed;
         } else {
            Collection<TransactionId> transactions = TransactionIdManager.getCommittedArtifactTransactionIds(teamArt);
            for (TransactionId transId : transactions) {
               if (transId.getBranchId() == branch.getBranchId()) {
                  return CommitStatus.Committed;
               }
            }
            return CommitStatus.Commit_Needed;
         }
      }
   }

   @Override
   public String getColumnText(Object element, XViewerColumn xCol, int columnIndex) throws OseeCoreException {
      VersionArtifact verArt = (VersionArtifact) element;
      Branch branch = verArt.getParentBranch();
      if (xCol.equals(CommitXManagerFactory.Type_Col)) {
         if (branch == null)
            return "";
         else if (commitXManager.getWorkingBranch() != null && branch.equals(commitXManager.getWorkingBranch().getParentBranch()))
            return "Parent Baseline";
         else {
            return "Parallel Branch";
         }
      } else if (xCol.equals(CommitXManagerFactory.Status_Col)) {
         return getCommitStatus(commitXManager.getXCommitViewer().getTeamArt(), verArt).getDisplayName();
      } else if (xCol.equals(CommitXManagerFactory.Merge_Col)) {
         return "";
      } else if (xCol.equals(CommitXManagerFactory.Version_Col)) {
         return verArt.getDescriptiveName();
      } else if (xCol.equals(CommitXManagerFactory.Dest_Branch_Col)) {
         return (branch == null ? "Parent Branch Not Configured for Version [" + verArt + "]" : branch.getBranchShortName());
      } else if (xCol.equals(CommitXManagerFactory.Action_Col)) {
         CommitStatus commitStatus = getCommitStatus(commitXManager.getXCommitViewer().getTeamArt(), verArt);
         if (commitStatus == CommitStatus.Branch_Not_Configured)
            return "Configure Branch";
         else if (commitStatus == CommitStatus.Commit_Needed)
            return "Start Commit";
         else if (commitStatus == CommitStatus.Merge_In_Progress)
            return "Merge Conflicts";
         else if (commitStatus == CommitStatus.Committed) {
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

}
