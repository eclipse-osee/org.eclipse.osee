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
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.revision.RevisionManager;
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
   private Change[] changes;
   private static Branch branch;

   /**
    * @author Donald G. Dunne
    */
   public ChangeView() {
   }

   public static void open(Branch branch) throws SQLException {
      ChangeView.branch = branch;
      if (branch == null) throw new IllegalArgumentException("Branch can't be null");
      Change[] changes = new Change[0];
      ChangeView.openViewUpon(RevisionManager.getInstance().getChangesPerBranch(branch).toArray(changes));
   }

   private static void openViewUpon(final Change[] changes) {
      Job job = new Job("Open Change View") {

         @Override
         protected IStatus run(final IProgressMonitor monitor) {
            Displays.ensureInDisplayThread(new Runnable() {
               public void run() {
                  try {
                     if (changes == null || changes.length == 0) {
                        AWorkbench.popup("Information", "There are no changes on this branch.");
                     } else {
                        IWorkbenchPage page = AWorkbench.getActivePage();
                        ChangeView changeView =
                              (ChangeView) page.showView(VIEW_ID,
                                    String.valueOf(changes[0].getToTransactionId().getBranch().getBranchId()),
                                    IWorkbenchPage.VIEW_ACTIVATE);
                        changeView.explore(changes);
                     }
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
      try {
         if (changes != null) {
            xChangeViewer.setChanges(changes);
         }
      } catch (SQLException ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }

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

   private void explore(final Change[] changes) {
      this.changes = changes;
      try {
         if (xChangeViewer != null && changes != null) xChangeViewer.setChanges(changes);
         setPartName("Change Report: " + changes[0].getToTransactionId().getBranch().getBranchShortName());
      } catch (SQLException ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }
   }

   private void explore(final Branch branch) throws SQLException {
      Change[] changes = new Change[0];
      explore(RevisionManager.getInstance().getChangesPerBranch(branch).toArray(changes));
   }

   public String getActionDescription() {
      return "";
   }

   private static final String INPUT = "input";
   private static final String BRANCH_ID = "branchId";

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.part.ViewPart#saveState(org.eclipse.ui.IMemento)
    */
   @Override
   public void saveState(IMemento memento) {
      super.saveState(memento);
      memento = memento.createChild(INPUT);
      memento.putInteger(BRANCH_ID, branch.getBranchId());
   }

   @Override
   public void init(IViewSite site, IMemento memento) throws PartInitException {
      super.init(site, memento);
      try {
         if (memento != null) {
            memento = memento.getChild(INPUT);
            if (memento != null) {
               Integer branchId = memento.getInteger(BRANCH_ID);
               if (branchId != null) explore(BranchPersistenceManager.getInstance().getBranch(branchId));
            }
         }
      } catch (Exception ex) {
         OSEELog.logWarning(getClass(), "Change report error on init: " + ex.getLocalizedMessage(), false);
      }
   }
}