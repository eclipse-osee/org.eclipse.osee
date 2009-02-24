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
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
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
public class ChangeView extends ViewPart implements IActionable, IBranchEventListener {

   public static final String VIEW_ID = "org.eclipse.osee.framework.ui.skynet.widgets.xchange.ChangeView";
   private static String HELP_CONTEXT_ID = "ChangeView";
   private XChangeViewer xChangeViewer;
   private Branch branch;
   private TransactionId transactionId;

   public ChangeView() {
   }

   public static void open(Branch branch) throws OseeArgumentException {
      if (branch == null) throw new OseeArgumentException("Branch can't be null");
      ChangeView.openViewUpon(branch, null, true);
   }

   public static void open(TransactionId transactionId) throws OseeArgumentException {
      if (transactionId == null) throw new OseeArgumentException("TransactionId can't be null");
      ChangeView.openViewUpon(null, transactionId, true);
   }

   private static void openViewUpon(final Branch branch, final TransactionId transactionId, final Boolean loadChangeReport) {
      Job job = new Job("Open Change View") {

         @Override
         protected IStatus run(final IProgressMonitor monitor) {
            Displays.ensureInDisplayThread(new Runnable() {
               public void run() {
                  try {
                     IWorkbenchPage page = AWorkbench.getActivePage();
                     ChangeView changeView =
                           (ChangeView) page.showView(
                                 VIEW_ID,
                                 String.valueOf(branch != null ? branch.getBranchId() : transactionId.getTransactionNumber()),
                                 IWorkbenchPage.VIEW_VISIBLE);

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

      xChangeViewer = new XChangeViewer();
      xChangeViewer.setDisplayLabel(false);
      xChangeViewer.createWidgets(parent, 1);

      MenuManager menuManager = new MenuManager();
      menuManager.setRemoveAllWhenShown(true);
      menuManager.addMenuListener(new IMenuListener() {
         public void menuAboutToShow(IMenuManager manager) {
            MenuManager menuManager = (MenuManager) manager;
            menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
         }
      });

      menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
      xChangeViewer.getXViewer().getTree().setMenu(menuManager.createContextMenu(xChangeViewer.getXViewer().getTree()));
      getSite().registerContextMenu("org.eclipse.osee.framework.ui.skynetd.widgets.xchange.ChangeView", menuManager,
            xChangeViewer.getXViewer());

      getSite().setSelectionProvider(xChangeViewer.getXViewer());
      SkynetGuiPlugin.getInstance().setHelp(parent, HELP_CONTEXT_ID);
   }

   private void explore(final Branch branch, final TransactionId transactionId, boolean loadChangeReport) {
      if (xChangeViewer != null) {
         this.branch = branch;
         this.transactionId = transactionId;

         if (branch == null) {
            String comment = transactionId.getComment() != null ? " - " + transactionId.getComment() : "";
            setPartName("Change Report: " + transactionId.getBranch().getBranchShortName() + comment);
         } else {
            setPartName("Change Report: " + branch.getBranchShortName());
         }

         xChangeViewer.setInputData(branch, transactionId, loadChangeReport);
      }
   }

   public String getActionDescription() {
      return "";
   }

   private static final String INPUT = "input";
   private static final String BRANCH_ID = "branchId";
   private static final String TRANSACTION_NUMBER = "transactionNumber";

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.part.ViewPart#saveState(org.eclipse.ui.IMemento)
    */
   @Override
   public void saveState(IMemento memento) {
      super.saveState(memento);
      memento = memento.createChild(INPUT);
      if (branch != null) {
         memento.putInteger(BRANCH_ID, branch.getBranchId());
      }
      if (transactionId != null) {
         memento.putInteger(TRANSACTION_NUMBER, transactionId.getTransactionNumber());
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
                        openViewUpon(null, TransactionIdManager.getTransactionId(transactionNumber), false);
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

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IBranchEventListener#handleBranchEvent(org.eclipse.osee.framework.ui.plugin.event.Sender, org.eclipse.osee.framework.skynet.core.artifact.BranchModType, int)
    */
   @Override
   public void handleBranchEvent(Sender sender, BranchEventType branchModType, final int branchId) {
      if (branchModType == BranchEventType.Deleted) {
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
      } else if (branchModType == BranchEventType.DefaultBranchChanged) {
         Displays.ensureInDisplayThread(new Runnable() {
            /* (non-Javadoc)
             * @see java.lang.Runnable#run()
             */
            @Override
            public void run() {
               if (xChangeViewer == null || xChangeViewer.getXViewer().getTree() == null || xChangeViewer.getXViewer().getTree().isDisposed()) return;
               xChangeViewer.getXViewer().getTree().setEnabled(branch.getBranchId() == branchId);
            }
         });
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IBranchEventListener#handleLocalBranchToArtifactCacheUpdateEvent(org.eclipse.osee.framework.ui.plugin.event.Sender)
    */
   @Override
   public void handleLocalBranchToArtifactCacheUpdateEvent(Sender sender) {
   }

   private void closeView() {
      SkynetViews.closeView(VIEW_ID, getViewSite().getSecondaryId());
   }
}