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
package org.eclipse.osee.framework.ui.skynet.widgets.xBranch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerTextFilter;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.ArtifactExplorer;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.PromptChangeUtil;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Jeff C. Phillips
 */
public class BranchXViewer extends XViewer {
   private final XBranchWidget xBranchViewer;

   public BranchXViewer(Composite parent, int style, XBranchWidget xBranchViewer, boolean filterRealTime, boolean searchRealTime) {
      super(parent, style, new BranchXViewerFactory(), filterRealTime, searchRealTime);
      this.xBranchViewer = xBranchViewer;
   }

   @Override
   public void handleDoubleClick() {
      ArrayList<Branch> branches = xBranchViewer.getSelectedBranches();
      if (branches != null && !branches.isEmpty()) {
         for (Branch branch : branches) {
            if (!branch.getBranchType().isSystemRootBranch()) {
               ArtifactExplorer.exploreBranch(branch);
               BranchManager.setLastBranch(branch);
            }
         }
      }
   }

   @Override
   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      try {
         if (treeColumn.getData().equals(BranchXViewerFactory.branchType)) {
            PromptChangeUtil.promptChangeBranchType(treeItems);
         }
         if (treeColumn.getData().equals(BranchXViewerFactory.branchState)) {
            PromptChangeUtil.promptChangeBranchState(treeItems);
         }
         if (treeColumn.getData().equals(BranchXViewerFactory.archivedState)) {
            PromptChangeUtil.promptChangeBranchArchivedState(treeItems);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   protected void createSupportWidgets(Composite parent) {
      super.createSupportWidgets(parent);
      getFilterDataUI().setFocus();
   }

   @Override
   public void updateMenuActionsForTable() {
      MenuManager mm = getMenuManager();
      mm.insertBefore(MENU_GROUP_PRE, new Separator());
      mm.insertBefore(MENU_GROUP_PRE, new SetAsFavoriteAction());
   }

   private class SetAsFavoriteAction extends Action {

      public SetAsFavoriteAction() {
         super("Toggle Branch as Favorite");
      }

      @Override
      public ImageDescriptor getImageDescriptor() {
         return ImageManager.getImageDescriptor(FrameworkImage.BRANCH_FAVORITE_OVERLAY);
      }

      @Override
      public void run() {
         User user;
         try {
            user = UserManager.getUser();
            if (user.isSystemUser()) {
               AWorkbench.popup("Can not set favorite as system user");
               return;
            }
            List<Branch> branches = xBranchViewer.getSelectedBranches();
            if (branches.isEmpty()) {
               AWorkbench.popup("Must select branches");
               return;
            }
            if (MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Toggle Branch Favorites",
               "Toggle Branch Favorites for " + branches.size() + " branch(s)")) {
               for (Branch branch : branches) {
                  user.toggleFavoriteBranch(branch);
               }
               user.persist("Toggle Branch Favorites");
               xBranchViewer.refresh();
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex.toString(), ex);
         }
      }

   }

   /**
    * Release resources
    */
   @Override
   public void dispose() {
      if (getLabelProvider() != null) {
         getLabelProvider().dispose();
      }
   }

   /**
    * @return the xHistoryViewer
    */
   public XBranchWidget getXBranchViewer() {
      return xBranchViewer;
   }

   @Override
   public XViewerTextFilter getXViewerTextFilter() {
      return new XBranchTextFilter(this);
   }

}
