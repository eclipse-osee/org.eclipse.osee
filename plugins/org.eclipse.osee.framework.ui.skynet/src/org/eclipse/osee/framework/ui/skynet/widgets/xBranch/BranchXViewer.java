/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.widgets.xBranch;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerTextFilter;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.model.MergeBranch;
import org.eclipse.osee.framework.help.ui.OseeHelpContext;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.plugin.util.HelpUtil;
import org.eclipse.osee.framework.ui.skynet.explorer.ArtifactExplorer;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.util.PromptChangeUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.XResultDataDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.xBranch.XBranchWidget.IBranchWidgetMenuListener;
import org.eclipse.osee.framework.ui.skynet.widgets.xmerge.MergeView;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Jeff C. Phillips
 */
public class BranchXViewer extends XViewer {
   private final XBranchWidget xBranchViewer;
   private IBranchWidgetMenuListener menuListener;

   public BranchXViewer(Composite parent, int style, BranchXViewerFactory xViewerFactory, XBranchWidget xBranchViewer, boolean filterRealTime, boolean searchRealTime) {
      super(parent, style, xViewerFactory, filterRealTime, searchRealTime);
      this.xBranchViewer = xBranchViewer;
   }

   @Override
   public void handleDoubleClick() {
      XResultData rd = new XResultData();
      ArrayList<BranchToken> branches = xBranchViewer.getSelectedBranches();
      if (branches != null && !branches.isEmpty()) {
         for (BranchToken branch : branches) {
            ServiceUtil.accessControlService().hasBranchPermission(branch, PermissionEnum.READ, rd);
            if (rd.isSuccess()) {
               if (branch.notEqual(CoreBranches.SYSTEM_ROOT)) {
                  if (!BranchManager.getType(branch).isMergeBranch()) {
                     ArtifactExplorer.exploreBranch(branch);
                     BranchManager.setLastBranch(branch);
                  } else {
                     if (branch instanceof MergeBranch) {
                        MergeBranch mergeBranch = (MergeBranch) branch;
                        BranchToken source = mergeBranch.getSourceBranch();
                        BranchToken destination = mergeBranch.getDestinationBranch();
                        MergeView.openView(source, destination, BranchManager.getBaseTransaction(source));
                     }
                  }
               }
            } else {
               rd.errorf("Access Restricted on branch %s\n", BranchManager.toStringWithId(branch));
            }
         }
      }
      if (rd.isErrors()) {
         XResultDataDialog.open(rd, "Branch Access Denied", "Branch Access Denied");
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
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   protected void createSupportWidgets(Composite parent) {
      super.createSupportWidgets(parent);
      getFilterDataUI().setFocus();
      HelpUtil.setHelp(getControl(), OseeHelpContext.BRANCH_MANAGER);
   }

   @Override
   public void updateMenuActionsForTable() {
      MenuManager mm = getMenuManager();
      if (getMenuListener() != null) {
         menuListener.updateMenuActionsForTable(mm);
      }
   }

   @Override
   public void dispose() {
      if (getLabelProvider() != null) {
         getLabelProvider().dispose();
      }
   }

   public XBranchWidget getXBranchViewer() {
      return xBranchViewer;
   }

   @Override
   public XViewerTextFilter getXViewerTextFilter() {
      if (getBranchFactory().isBranchManager()) {
         return new XBranchTextFilter(this);
      } else {
         return super.getXViewerTextFilter();
      }
   }

   private BranchXViewerFactory getBranchFactory() {
      return (BranchXViewerFactory) getXViewerFactory();
   }

   public IBranchWidgetMenuListener getMenuListener() {
      return menuListener;
   }

   public void setMenuListener(IBranchWidgetMenuListener menuListener) {
      this.menuListener = menuListener;
   }

   @Override
   public boolean isRemoveItemsMenuOptionEnabled() {
      return false;
   }

}
