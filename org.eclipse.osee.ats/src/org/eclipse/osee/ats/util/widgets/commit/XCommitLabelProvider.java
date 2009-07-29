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
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.swt.graphics.Image;

public class XCommitLabelProvider extends XViewerLabelProvider {

   private final CommitXManager commitXManager;

   public XCommitLabelProvider(CommitXManager commitXManager) {
      super(commitXManager);
      this.commitXManager = commitXManager;
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn xCol, int columnIndex) throws OseeCoreException {
      ICommitConfigArtifact configArt = (ICommitConfigArtifact) element;
      Branch branch = configArt.getParentBranch();
      if (xCol.equals(CommitXManagerFactory.Action_Col)) {
         return ImageManager.getImage(FrameworkImage.ARROW_RIGHT_YELLOW);
      }
      if (branch == null)
         return null;
      if (xCol.equals(CommitXManagerFactory.Status_Col)) {
         try {
            CommitStatus commitStatus =
                  commitXManager.getXCommitViewer().getTeamArt().getSmaMgr().getBranchMgr().getCommitStatus(configArt);
            if (commitStatus == CommitStatus.Branch_Not_Configured ||
            //
            commitStatus == CommitStatus.Branch_Commit_Disabled ||
            //
            commitStatus == CommitStatus.Commit_Needed ||
            //
            commitStatus == CommitStatus.Working_Branch_Not_Created) {
               return ImageManager.getImage(FrameworkImage.DOT_RED);
            }

            if (commitStatus == CommitStatus.Merge_In_Progress) {
               return ImageManager.getImage(FrameworkImage.DOT_YELLOW);
            }

            if (commitStatus == CommitStatus.Committed ||
            //
            commitStatus == CommitStatus.Committed_With_Merge) {
               return ImageManager.getImage(FrameworkImage.DOT_GREEN);
            }
            return null;
         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         }
      } else if (xCol.equals(CommitXManagerFactory.Merge_Col)) {
         try {
            CommitStatus commitStatus =
                  commitXManager.getXCommitViewer().getTeamArt().getSmaMgr().getBranchMgr().getCommitStatus(configArt);
            if (commitStatus == CommitStatus.Merge_In_Progress || commitStatus == CommitStatus.Committed_With_Merge) {
               return ImageManager.getImage(FrameworkImage.OUTGOING_MERGED);
            }
            return null;
         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         }
      }
      return null;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn xCol, int columnIndex) throws OseeCoreException {
      ICommitConfigArtifact configArt = (ICommitConfigArtifact) element;
      Branch branch = configArt.getParentBranch();

      if (xCol.equals(CommitXManagerFactory.Status_Col)) {
         return commitXManager.getXCommitViewer().getTeamArt().getSmaMgr().getBranchMgr().getCommitStatus(configArt).getDisplayName();
      } else if (xCol.equals(CommitXManagerFactory.Merge_Col)) {
         return "";
      } else if (xCol.equals(CommitXManagerFactory.Version_Col)) {
         return ((Artifact) element).getName();
      } else if (xCol.equals(CommitXManagerFactory.Configuring_Object_Col)) {
         return ((Artifact) element).getArtifactTypeName();
      } else if (xCol.equals(CommitXManagerFactory.Dest_Branch_Col)) {
         if (element instanceof VersionArtifact) {
            return (branch == null ? "Parent Branch Not Configured for Version [" + (element) + "]" : branch.getShortName());
         } else if (element instanceof TeamDefinitionArtifact) {
            return (branch == null ? "Parent Branch Not Configured for Team Definition [" + (element) + "]" : branch.getShortName());
         }
      } else if (xCol.equals(CommitXManagerFactory.Action_Col)) {
         CommitStatus commitStatus =
               commitXManager.getXCommitViewer().getTeamArt().getSmaMgr().getBranchMgr().getCommitStatus(configArt);
         if (commitStatus == CommitStatus.Branch_Not_Configured)
            return "Configure Branch";
         else if (commitStatus == CommitStatus.Branch_Commit_Disabled)
            return "Enable Branch Commit";
         else if (commitStatus == CommitStatus.Commit_Needed)
            return "Start Commit";
         else if (commitStatus == CommitStatus.Merge_In_Progress)
            return "Merge Conflicts and Commit";
         else if (commitStatus == CommitStatus.Committed)
            return "Show Change Report";
         else if (commitStatus == CommitStatus.Committed_With_Merge)
            return "Show Change/Merge Report";
         else if (commitStatus == CommitStatus.Working_Branch_Not_Created)
            return "Working Branch Not Created";
         return "Error: Need to handle this";
      }
      return "unhandled column";
   }

   public void dispose() {
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
