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

package org.eclipse.osee.framework.ui.skynet.widgets.xHistory;

import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.widgets.xviewer.customize.XViewerCustomMenu;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.revision.HistoryTransactionItem;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.skynet.OpenWithMenuListener;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.listener.IRebuildMenuListener;
import org.eclipse.osee.framework.ui.skynet.menu.ArtifactDiffMenu;
import org.eclipse.osee.framework.ui.skynet.util.SkynetViews;
import org.eclipse.osee.framework.ui.skynet.widgets.xchange.ChangeView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

/**
 * Displays persisted changes made to an artifact.
 * 
 * @author Jeff C. Phillips
 */
public class HistoryView extends ViewPart implements IActionable, IBranchEventListener, IRebuildMenuListener {

   public static final String VIEW_ID = "org.eclipse.osee.framework.ui.skynet.widgets.xHistory.HistoryView";
   private static String HELP_CONTEXT_ID = "HistoryView";
   private XHistoryWidget xHistoryWidget;
   private Artifact artifact;

   public HistoryView() {
   }

   public static void open(Artifact artifact) throws OseeArgumentException {
      if (artifact == null) throw new OseeArgumentException("Artifact can't be null");
      HistoryView.openViewUpon(artifact, true);
   }

   private static void openViewUpon(final Artifact artifact, final Boolean loadHistory) {
      Job job = new Job("Open History: " + artifact.getDescriptiveName()) {

         @Override
         protected IStatus run(final IProgressMonitor monitor) {
            Displays.ensureInDisplayThread(new Runnable() {
               public void run() {
                  try {
                     IWorkbenchPage page = AWorkbench.getActivePage();
                     HistoryView historyView =
                           (HistoryView) page.showView(VIEW_ID,
                                 artifact.getGuid() + artifact.getBranch().getBranchId(), IWorkbenchPage.VIEW_VISIBLE);

                     historyView.explore(artifact, loadHistory);
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

      xHistoryWidget = new XHistoryWidget();
      xHistoryWidget.setDisplayLabel(false);
      xHistoryWidget.createWidgets(parent, 1);

      MenuManager menuManager = new MenuManager();
      menuManager.setRemoveAllWhenShown(true);
      menuManager.addMenuListener(new IMenuListener() {
         public void menuAboutToShow(IMenuManager manager) {
            MenuManager menuManager = (MenuManager) manager;
            menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
         }
      });

      menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
      xHistoryWidget.getXViewer().getTree().setMenu(
            menuManager.createContextMenu(xHistoryWidget.getXViewer().getTree()));
      getSite().registerContextMenu(VIEW_ID, menuManager, xHistoryWidget.getXViewer());

      getSite().setSelectionProvider(xHistoryWidget.getXViewer());
      SkynetGuiPlugin.getInstance().setHelp(parent, HELP_CONTEXT_ID);

      setupMenus();
   }

   private void setupMenus() {
      Menu popupMenu = new Menu(xHistoryWidget.getXViewer().getTree().getParent());
      createOpenWithMenuItem(popupMenu);
      createChangeReportMenuItem(popupMenu);
      ArtifactDiffMenu.createDiffMenuItem(popupMenu, xHistoryWidget.getXViewer(), "Compare two Artifacts", null);

      // Setup generic xviewer menu items
      XViewerCustomMenu xMenu = new XViewerCustomMenu(xHistoryWidget.getXViewer());
      new MenuItem(popupMenu, SWT.SEPARATOR);
      xMenu.createTableCustomizationMenuItem(popupMenu);
      xMenu.createViewTableReportMenuItem(popupMenu);
      new MenuItem(popupMenu, SWT.SEPARATOR);
      xMenu.addCopyViewMenuBlock(popupMenu);
      new MenuItem(popupMenu, SWT.SEPARATOR);
      xMenu.addFilterMenuBlock(popupMenu);
      new MenuItem(popupMenu, SWT.SEPARATOR);
      xHistoryWidget.getXViewer().getTree().setMenu(popupMenu);
   }

   /**
    * @param popupMenu
    */
   private void createChangeReportMenuItem(Menu popupMenu) {
      final MenuItem changeReportMenuItem = new MenuItem(popupMenu, SWT.CASCADE);
      changeReportMenuItem.setText("&Change Report");
      changeReportMenuItem.setImage(SkynetGuiPlugin.getInstance().getImage("branch_change.gif"));
      popupMenu.addMenuListener(new MenuListener() {

         @Override
         public void menuHidden(MenuEvent e) {
         }

         @Override
         public void menuShown(MenuEvent e) {
            List<?> selections = ((IStructuredSelection) xHistoryWidget.getXViewer().getSelection()).toList();
            try {
               changeReportMenuItem.setEnabled(selections.size() == 1 && ((HistoryTransactionItem) selections.iterator().next()).getTransactionData().getTransactionId().getTxType() != TransactionDetailsType.Baselined);
            } catch (OseeCoreException ex) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }

      });

      changeReportMenuItem.addSelectionListener(new SelectionListener() {

         @Override
         public void widgetDefaultSelected(SelectionEvent e) {
         }

         @Override
         public void widgetSelected(SelectionEvent e) {
            IStructuredSelection selection = (IStructuredSelection) xHistoryWidget.getXViewer().getSelection();
            Object selectedObject = selection.getFirstElement();

            if (selectedObject instanceof HistoryTransactionItem) {
               try {
                  ChangeView.open(((HistoryTransactionItem) selectedObject).getTransactionData().getTransactionId());
               } catch (Exception ex) {
                  OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         }

      });
   }

   private void createOpenWithMenuItem(Menu parentMenu) {
      MenuItem openWithMenuItem = new MenuItem(parentMenu, SWT.CASCADE);
      openWithMenuItem.setText("&Open With");
      final Menu submenu = new Menu(openWithMenuItem);
      openWithMenuItem.setMenu(submenu);
      parentMenu.addMenuListener(new OpenWithMenuListener(submenu, xHistoryWidget.getXViewer(), this));
   }

   private void explore(final Artifact artifact, boolean loadHistory) {
      if (xHistoryWidget != null) {
         this.artifact = artifact;

         setPartName("History: " + artifact.getDescriptiveName());
         xHistoryWidget.setInputData(artifact, loadHistory);
      }
   }

   public String getActionDescription() {
      return "";
   }

   private static final String INPUT = "input";
   private static final String ART_GUID = "artifactGuid";
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
      if (artifact != null) {
         memento.putString(ART_GUID, artifact.getGuid());
         memento.putInteger(BRANCH_ID, artifact.getBranch().getBranchId());
         SkynetViews.addDatabaseSourceId(memento);
      }
   }

   @Override
   public void init(IViewSite site, IMemento memento) throws PartInitException {
      super.init(site, memento);
      try {
         if (memento != null) {
            memento = memento.getChild(INPUT);
            if (memento != null) {
               if (SkynetViews.isSourceValid(memento)) {
                  String guid = memento.getString(ART_GUID);
                  Integer branchId = memento.getInteger(BRANCH_ID);
                  Artifact artifact = ArtifactQuery.getArtifactFromId(guid, BranchManager.getBranch(branchId));
                  openViewUpon(artifact, false);
               } else {
                  closeView();
               }
            }
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.WARNING, "History View error on init", ex);
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
                  explore(artifact, true);
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
               if (xHistoryWidget == null || xHistoryWidget.getXViewer().getTree() == null || xHistoryWidget.getXViewer().getTree().isDisposed()) return;
               xHistoryWidget.getXViewer().getTree().setEnabled(artifact.getBranch().getBranchId() == branchId);
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

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.listener.IRebuildMenuListener#rebuildMenu()
    */
   @Override
   public void rebuildMenu() {
      setupMenus();
   }
}