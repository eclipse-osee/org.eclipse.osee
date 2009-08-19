/*******************************************************************************
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

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.ITransactionsDeletedEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.OseeContributionItem;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

/**
 * Displays persisted changes made to an artifact.
 * 
 * @author Jeff C. Phillips
 */
public class BranchView extends ViewPart implements IActionable, IBranchEventListener, ITransactionsDeletedEventListener {
   public static final String VIEW_ID = "org.eclipse.osee.framework.ui.skynet.widgets.xBranch.BranchView";
   private BranchViewPresentationPreferences branchViewPresentationPreferences;
   private static String HELP_CONTEXT_ID = "BranchView";
   public static final String BRANCH_ID = "branchId";
   private XBranchWidget xBranchWidget;

   public BranchView() {
      super();

      OseeEventManager.addListener(this);
   }

   @Override
   public void dispose() {
      super.dispose();

      branchViewPresentationPreferences.setDisposed(true);
      OseeEventManager.removeListener(this);
   }

   @Override
   public void setFocus() {
   }

   @Override
   public void createPartControl(Composite parent) {
      setPartName("Branch Manager");

      GridLayout layout = new GridLayout();
      layout.numColumns = 1;
      layout.verticalSpacing = 0;
      layout.marginWidth = 0;
      layout.marginHeight = 0;
      parent.setLayout(layout);
      parent.setLayoutData(new GridData(GridData.FILL_BOTH));

      xBranchWidget = new XBranchWidget();
      xBranchWidget.setDisplayLabel(false);
      xBranchWidget.createWidgets(parent, 1);

      branchViewPresentationPreferences = new BranchViewPresentationPreferences(this);
      xBranchWidget.loadData();

      MenuManager menuManager = new MenuManager();
      menuManager.setRemoveAllWhenShown(true);
      menuManager.addMenuListener(new IMenuListener() {
         public void menuAboutToShow(IMenuManager manager) {
            MenuManager menuManager = (MenuManager) manager;
            menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
         }
      });

      menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
      xBranchWidget.getXViewer().getTree().setMenu(menuManager.createContextMenu(xBranchWidget.getXViewer().getTree()));
      getSite().registerContextMenu(VIEW_ID, menuManager, xBranchWidget.getXViewer());

      getSite().setSelectionProvider(xBranchWidget.getXViewer());
      SkynetGuiPlugin.getInstance().setHelp(parent, HELP_CONTEXT_ID, "org.eclipse.osee.framework.help.ui");
      OseeContributionItem.addTo(this, true);
      getViewSite().getActionBars().updateActionBars();

   }

   public String getActionDescription() {
      return "";
   }

   public static void revealBranch(Branch branch) throws OseeWrappedException {
      try {
         BranchView branchView = (BranchView) AWorkbench.getActivePage().showView(VIEW_ID);
         branchView.reveal(branch);
      } catch (PartInitException ex) {
         throw new OseeWrappedException(ex);
      }
   }

   private void reveal(Branch branch) {
      xBranchWidget.reveal(branch);
   }

   @Override
   public void handleBranchEvent(Sender sender, BranchEventType branchModType, final int branchId) {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            try {
               xBranchWidget.refresh();
            } catch (Exception ex) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });
   }

   @Override
   public void handleLocalBranchToArtifactCacheUpdateEvent(Sender sender) {
   }

   @Override
   public void handleTransactionsDeletedEvent(Sender sender, int[] transactionIds) {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            try {
               xBranchWidget.refresh();
            } catch (Exception ex) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });
   }

   public void changeBranchPresentation(boolean flat) {
      if (branchViewPresentationPreferences != null) {
         branchViewPresentationPreferences.getViewPreference().putBoolean(BranchViewPresentationPreferences.FLAT_KEY,
               flat);
      }
   }

   public void changeTransactionPresentation(boolean showTransactions) {
      if (branchViewPresentationPreferences != null) {
         branchViewPresentationPreferences.getViewPreference().putBoolean(
               BranchViewPresentationPreferences.SHOW_TRANSACTIONS, showTransactions);
      }
   }

   public void changeMergeBranchPresentation(boolean showMergeBranches) {
      if (branchViewPresentationPreferences != null) {
         branchViewPresentationPreferences.getViewPreference().putBoolean(
               BranchViewPresentationPreferences.SHOW_MERGE_BRANCHES, showMergeBranches);
      }
   }

   public void changeArchivedBranchPresentation(boolean showArchivedBranches) {
      if (branchViewPresentationPreferences != null) {
         branchViewPresentationPreferences.getViewPreference().putBoolean(
               BranchViewPresentationPreferences.SHOW_ARCHIVED_BRANCHES, showArchivedBranches);
      }
   }

   public void changeFavoritesFirstPresentation(boolean showArchivedBranches) {
      if (branchViewPresentationPreferences != null) {
         branchViewPresentationPreferences.getViewPreference().putBoolean(
               BranchViewPresentationPreferences.FAVORITE_KEY, showArchivedBranches);
      }
   }

   /**
    * These five methods is called by BranchViewPresentationPreferences to change the branch view data presentation. Not
    * part of the regular API.
    */
   protected void setPresentation(boolean flat) {
      xBranchWidget.setPresentation(flat);
   }

   protected void setFavoritesFirst(boolean favoritesFirst) {
      xBranchWidget.setFavoritesFirst(favoritesFirst);
   }

   protected void setShowMergeBranches(boolean showMergeBranches) {
      xBranchWidget.setShowMergeBranches(showMergeBranches);
   }

   protected void setShowTransactions(boolean showTransactions) {
      xBranchWidget.setShowTransactions(showTransactions);
   }

   protected void setShowArchivedBranches(boolean showArchivedBranches) {
      xBranchWidget.setShowArchivedBranches(showArchivedBranches);
   }
}