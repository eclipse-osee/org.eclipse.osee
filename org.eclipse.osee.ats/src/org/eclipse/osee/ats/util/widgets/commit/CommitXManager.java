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

import java.util.ArrayList;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.util.widgets.commit.XCommitLabelProvider.CommitStatus;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class CommitXManager extends XViewer {

   private final XCommitManager xCommitManager;

   public CommitXManager(Composite parent, int style, XCommitManager xRoleViewer) {
      super(parent, style, new CommitXManagerFactory());
      this.xCommitManager = xRoleViewer;
   }

   @Override
   protected void createSupportWidgets(Composite parent) {
      super.createSupportWidgets(parent);
   }

   public void updateMenuActions() {
      MenuManager mm = getMenuManager();

      mm.insertBefore(MENU_GROUP_PRE, new Separator());
   }

   /**
    * Release resources
    */
   @Override
   public void dispose() {
      getLabelProvider().dispose();
   }

   public ArrayList<VersionArtifact> getSelectedVersions() {
      ArrayList<VersionArtifact> arts = new ArrayList<VersionArtifact>();
      TreeItem items[] = getTree().getSelection();
      if (items.length > 0) for (TreeItem item : items)
         arts.add((VersionArtifact) item.getData());
      return arts;
   }

   /**
    * @return the xUserRoleViewer
    */
   public XCommitManager getXCommitViewer() {
      return xCommitManager;
   }

   /**
    * @return the workingBranch
    */
   public Branch getWorkingBranch() throws OseeCoreException {
      return xCommitManager.getWorkingBranch();
   }

   /* (non-Javadoc)
    * @see org.eclipse.nebula.widgets.xviewer.XViewer#handleDoubleClick()
    */
   @Override
   public void handleDoubleClick() {
      try {
         VersionArtifact verArt = getSelectedVersions().iterator().next();
         Branch destBranch = verArt.getParentBranch();
         CommitStatus commitStatus = XCommitLabelProvider.getCommitStatus(xCommitManager.getTeamArt(), verArt);
         if (commitStatus == CommitStatus.Working_Branch_Not_Created) {
            AWorkbench.popup(commitStatus.getDisplayName(), "Need to create a working branch");
         } else if (commitStatus == CommitStatus.Branch_Not_Configured) {
            AWorkbench.popup(commitStatus.getDisplayName(),
                  "Talk to project lead or admin to configure branch for version [" + verArt + "]");
         } else if (commitStatus == CommitStatus.Commit_Needed || commitStatus == CommitStatus.Commit_Needed_After_Merge) {
            destBranch = verArt.getParentBranch();
            xCommitManager.getTeamArt().getSmaMgr().getBranchMgr().commitWorkingBranch(true, false, destBranch,
                  xCommitManager.getTeamArt().getSmaMgr().getBranchMgr().getBranchesLeftToCommit().size() == 1);
         } else if (commitStatus == CommitStatus.Merge_In_Progress) {
            destBranch = verArt.getParentBranch();
            xCommitManager.getTeamArt().getSmaMgr().getBranchMgr().showMergeManager(destBranch);
         } else if (commitStatus == CommitStatus.Committed) {
            destBranch = verArt.getParentBranch();
            xCommitManager.getTeamArt().getSmaMgr().getBranchMgr().showChangeReportForBranch(destBranch);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

}
