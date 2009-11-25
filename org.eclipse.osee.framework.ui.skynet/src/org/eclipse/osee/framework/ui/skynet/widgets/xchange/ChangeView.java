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

package org.eclipse.osee.framework.ui.skynet.widgets.xchange;

import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.ITransactionsDeletedEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.OseeContributionItem;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.util.SkynetViews;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Jeff C. Phillips
 * @author Donald G. Dunne
 */
public class ChangeView extends ViewPart implements IActionable, IBranchEventListener, ITransactionsDeletedEventListener {

   public static final String VIEW_ID = "org.eclipse.osee.framework.ui.skynet.widgets.xchange.ChangeView";
   private static String HELP_CONTEXT_ID = "ChangeView";
   private XChangeWidget xChangeWidget;
   private Branch branch;
   private TransactionRecord transactionId;
   private ChangeViewPresentationPreferences changeViewPresentationPreferences;

   public ChangeView() {
      OseeEventManager.addListener(this);
   }

   public static void open(Branch branch) throws OseeArgumentException {
      if (branch == null) {
         throw new OseeArgumentException("Branch can't be null");
      }
      ChangeView.openViewUpon(branch, null, true);
   }

   public static void open(TransactionRecord transactionId) throws OseeArgumentException {
      if (transactionId == null) {
         throw new OseeArgumentException("TransactionId can't be null");
      }
      ChangeView.openViewUpon(null, transactionId, true);
   }

   private static void openViewUpon(final Branch branch, final TransactionRecord transactionId, final Boolean loadChangeReport) {
      Job job = new Job("Open Change View") {

         @Override
         protected IStatus run(final IProgressMonitor monitor) {
            Displays.ensureInDisplayThread(new Runnable() {
               public void run() {
                  try {
                     IWorkbenchPage page = AWorkbench.getActivePage();
                     ChangeView changeView =
                           (ChangeView) page.showView(VIEW_ID,
                                 String.valueOf(branch != null ? branch.getId() : transactionId.getId()),
                                 IWorkbenchPage.VIEW_ACTIVATE);

                     changeView.explore(branch, transactionId, loadChangeReport);
                  } catch (Exception ex) {
                     OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
                  }
               }
            });
            monitor.done();
            return Status.OK_STATUS;
         }
      };

      Jobs.startJob(job);
   }

   @Override
   public void dispose() {
      OseeEventManager.removeListener(this);
      changeViewPresentationPreferences.setDisposed(true);
      super.dispose();
   }

   @Override
   public void setFocus() {
   }

   /*
    * @see IWorkbenchPart#createPartControl(Composite)
    */
   @Override
   public void createPartControl(Composite parent) {
      /*
       * Create a grid layout object so the text and treeviewer are layed out the way I want.
       */
      GridLayout layout = new GridLayout();
      layout.numColumns = 1;
      layout.verticalSpacing = 0;
      layout.marginWidth = 0;
      layout.marginHeight = 0;
      parent.setLayout(layout);
      parent.setLayoutData(new GridData(GridData.FILL_BOTH));

      xChangeWidget = new XChangeWidget();
      xChangeWidget.setDisplayLabel(false);
      xChangeWidget.createWidgets(parent, 1);

      changeViewPresentationPreferences = new ChangeViewPresentationPreferences(this);

      MenuManager menuManager = new MenuManager();
      menuManager.setRemoveAllWhenShown(true);
      menuManager.addMenuListener(new IMenuListener() {
         public void menuAboutToShow(IMenuManager manager) {
            MenuManager menuManager = (MenuManager) manager;
            menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
         }
      });

      menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
      xChangeWidget.getXViewer().getTree().setMenu(menuManager.createContextMenu(xChangeWidget.getXViewer().getTree()));
      getSite().registerContextMenu(VIEW_ID, menuManager, xChangeWidget.getXViewer());

      getSite().setSelectionProvider(xChangeWidget.getXViewer());
      SkynetGuiPlugin.getInstance().setHelp(parent, HELP_CONTEXT_ID, "org.eclipse.osee.framework.help.ui");
      OseeContributionItem.addTo(this, true);
   }

   private void explore(final Branch branch, final TransactionRecord transactionId, boolean loadChangeReport) throws OseeCoreException {
      if (xChangeWidget != null) {
         this.branch = branch;
         this.transactionId = transactionId;

         if (branch == null) {
            String comment = transactionId.getComment() != null ? " - " + transactionId.getComment() : "";
            setPartName("Change Report: " + transactionId.getBranch().getShortName() + comment);
         } else {
            setPartName("Change Report: " + branch.getShortName());
         }

         xChangeWidget.setInputData(branch, transactionId, loadChangeReport);
      }
   }

   public String getActionDescription() {
      return "";
   }

   private static final String INPUT = "input";
   private static final String BRANCH_ID = "branchId";
   private static final String TRANSACTION_NUMBER = "transactionNumber";

   @Override
   public void saveState(IMemento memento) {
      super.saveState(memento);
      memento = memento.createChild(INPUT);
      if (branch != null) {
         memento.putInteger(BRANCH_ID, branch.getId());
      }
      if (transactionId != null) {
         memento.putInteger(TRANSACTION_NUMBER, transactionId.getId());
      }
      if (branch != null || transactionId != null) {
         SkynetViews.addDatabaseSourceId(memento);
      }
   }

   @Override
   public void init(IViewSite site, IMemento memento) throws PartInitException {
      super.init(site, memento);
      try {
         Integer branchId = null;

         if (memento != null) {
            memento = memento.getChild(INPUT);
            if (memento != null) {
               if (SkynetViews.isSourceValid(memento)) {
                  branchId = memento.getInteger(BRANCH_ID);
                  if (branchId != null) {
                     openViewUpon(BranchManager.getBranch(branchId), null, false);
                  } else {
                     Integer transactionNumber = memento.getInteger(TRANSACTION_NUMBER);
                     if (transactionNumber != null && transactionNumber > -1) {
                        openViewUpon(null, TransactionManager.getTransactionId(transactionNumber), false);
                     }
                  }
               } else {
                  closeView();
               }
            }
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.WARNING, "Change report error on init", ex);
      }
   }

   @Override
   public void handleBranchEvent(Sender sender, BranchEventType branchModType, final int branchId) {
      if (branch == null || branchId != branch.getId()) {
         return;
      }

      if (branchModType == BranchEventType.Deleted && branchModType == BranchEventType.Purged) {
         Displays.ensureInDisplayThread(new Runnable() {
            public void run() {
               closeView();
            }
         });
         return;
      } else if (branchModType == BranchEventType.Committed) {
         Displays.ensureInDisplayThread(new Runnable() {
            public void run() {
               try {
                  explore(branch, transactionId, true);
               } catch (Exception ex) {
                  OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         });
         // refresh view with new branch and transaction id
      }
   }

   @Override
   public void handleLocalBranchToArtifactCacheUpdateEvent(Sender sender) {
   }

   private void closeView() {
      System.out.println("ID: " + getViewSite().getSecondaryId());
      SkynetViews.closeView(VIEW_ID, getViewSite().getSecondaryId());
   }

   @Override
   public void handleTransactionsDeletedEvent(Sender sender, int[] transactionIds) {
      if (transactionId == null) {
         return;
      }

      for (int transactionNumber : transactionIds) {
         if (transactionNumber == transactionId.getId()) {
            Displays.ensureInDisplayThread(new Runnable() {
               public void run() {
                  closeView();
               }
            });
            return;
         }
      }
   }

   public void changeShowDocumentOrder(boolean showDocOrder) {
      if (changeViewPresentationPreferences != null) {
         changeViewPresentationPreferences.getViewPreference().putBoolean(
               ChangeViewPresentationPreferences.SHOW_DOC_ORDER, showDocOrder);
      }
   }

   /**
    * @param showArchivedBranches
    */
   protected void setShowDocumentOrder(boolean showDocOrder) {
      xChangeWidget.setShowDocumentOrder(showDocOrder);

   }
}