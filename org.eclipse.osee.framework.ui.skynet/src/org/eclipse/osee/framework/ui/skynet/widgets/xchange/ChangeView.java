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

import java.sql.SQLException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
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
 * @see ViewPart
 * @author Donald G. Dunne
 */
public class ChangeView extends ViewPart implements IActionable {

   public static final String VIEW_ID = "org.eclipse.osee.framework.ui.skynet.widgets.xchange.ChangeView";
   private static String HELP_CONTEXT_ID = "ChangeView";
   private XChangeViewer xChangeViewer;
   private Branch branch;
   private int transactionNumber;

   /**
    * @author Donald G. Dunne
    * @author Jeff C. Phillips
    */
   public ChangeView() {
   }

   public static void open(Branch branch) throws SQLException {
      if (branch == null) throw new IllegalArgumentException("Branch can't be null");
      ChangeView.openViewUpon(branch, -1);
   }

   public static void open(int transactionNumber) throws SQLException {
      if (transactionNumber < 0) throw new IllegalArgumentException("Branch can't be null");
      ChangeView.openViewUpon(null, transactionNumber);
   }

   private static void openViewUpon(final Branch branch, final int transactionNumber) {
      Job job = new Job("Open Change View") {

         @Override
         protected IStatus run(final IProgressMonitor monitor) {
            Displays.ensureInDisplayThread(new Runnable() {
               public void run() {
                  try {
                     IWorkbenchPage page = AWorkbench.getActivePage();
                     ChangeView changeView =
                           (ChangeView) page.showView(VIEW_ID,
                                 String.valueOf(branch != null ? branch.getBranchId() : transactionNumber),
                                 IWorkbenchPage.VIEW_VISIBLE);
                     changeView.explore(branch, transactionNumber);
                  } catch (Exception ex) {
                     OSEELog.logException(SkynetGuiPlugin.class, ex, true);
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

   public void setFocus() {
   }

   /*
    * @see IWorkbenchPart#createPartControl(Composite)
    */
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

   private void explore(final Branch branch, final int transactionNumber) throws SQLException, BranchDoesNotExist {
      if (xChangeViewer != null) {
         this.branch = branch;
         this.transactionNumber = transactionNumber;
         xChangeViewer.setInputData(branch, transactionNumber);
         if (branch != null) {
            setPartName("Change Report: " + branch.getBranchShortName());
         } else {
            TransactionId transId =
                  TransactionIdManager.getInstance().getPossiblyEditableTransactionId(transactionNumber);
            if (transId != null)
               setPartName("Change Report: " + transId.getBranch().getBranchShortestName() + " - " + transId.getComment());
            else
               setPartName("Change Report: " + BranchPersistenceManager.getInstance().getBranchForTransactionNumber(
                     transactionNumber));
         }
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
      } else {
         memento.putInteger(TRANSACTION_NUMBER, transactionNumber);
      }
   }

   @Override
   public void init(IViewSite site, IMemento memento) throws PartInitException {
      super.init(site, memento);
      try {
         Integer branchId = null;
         Integer transactionId = null;

         if (memento != null) {
            memento = memento.getChild(INPUT);
            if (memento != null) {
               branchId = memento.getInteger(BRANCH_ID);
               if (branchId != null) {
                  openViewUpon(BranchPersistenceManager.getInstance().getBranch(branchId), -1);
               } else {
                  transactionId = memento.getInteger(TRANSACTION_NUMBER);
                  if (transactionId != null) {
                     openViewUpon(null, transactionId);
                  }
               }
            }
         }
      } catch (Exception ex) {
         OSEELog.logWarning(SkynetGuiPlugin.class, "Change report error on init", ex, false);
      }
   }
}