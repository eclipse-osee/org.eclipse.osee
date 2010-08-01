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
package org.eclipse.osee.ats.navigate;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IRegistryEventListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.ats.actions.MyFavoritesAction;
import org.eclipse.osee.ats.actions.MyWorldAction;
import org.eclipse.osee.ats.actions.NewAction;
import org.eclipse.osee.ats.actions.NewGoal;
import org.eclipse.osee.ats.actions.OpenChangeReportByIdAction;
import org.eclipse.osee.ats.actions.OpenWorkflowByIdAction;
import org.eclipse.osee.ats.actions.OpenWorldByIdAction;
import org.eclipse.osee.ats.config.AtsBulkLoad;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.operation.CompositeOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.IActionable;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.plugin.OseeUiActions;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.OseeContributionItem;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.action.CollapseAllAction;
import org.eclipse.osee.framework.ui.skynet.action.ExpandAllAction;
import org.eclipse.osee.framework.ui.skynet.notify.OseeNotificationManager;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionExceptionComposite;
import org.eclipse.osee.framework.ui.skynet.util.LoadingComposite;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.UIJob;

/**
 * @author Donald G. Dunne
 */
public class NavigateView extends ViewPart implements IActionable {

   public static final String VIEW_ID = "org.eclipse.osee.ats.navigate.NavigateView";
   public static final String HELP_CONTEXT_ID = "atsNavigator";
   private AtsNavigateComposite xNavComp;
   private final boolean includeCompleteCancelled = false;
   private Composite parent;
   private LoadingComposite loadingComposite;

   @Override
   public void createPartControl(Composite parent) {
      this.parent = parent;
      loadingComposite = new LoadingComposite(parent);
      refreshData();
   }

   public void refreshData() {
      List<IOperation> ops = new ArrayList<IOperation>();
      ops.add(AtsBulkLoad.getConfigLoadingOperation());
      ops.add(new AtsNavigateViewItemsOperation());
      IOperation operation = new CompositeOperation("Load ATS Navigator", SkynetGuiPlugin.PLUGIN_ID, ops);
      Operations.executeAsJob(operation, false, Job.LONG, new ReloadJobChangeAdapter(this));
   }

   private final class ReloadJobChangeAdapter extends JobChangeAdapter {

      private final NavigateView navView;

      private ReloadJobChangeAdapter(NavigateView navView) {
         this.navView = navView;
      }

      @Override
      public void scheduled(IJobChangeEvent event) {
         super.scheduled(event);
      }

      @Override
      public void aboutToRun(IJobChangeEvent event) {
         super.aboutToRun(event);
      }

      @Override
      public void done(IJobChangeEvent event) {
         super.done(event);
         Job job = new UIJob("Load ATS Navigator") {

            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
               try {
                  showBusy(false);
                  if (!DbConnectionExceptionComposite.dbConnectionIsOk(parent)) {
                     return new Status(IStatus.ERROR, AtsPlugin.PLUGIN_ID, "Navigate View - !dbConnectionIsOk");
                  }

                  if (Widgets.isAccessible(loadingComposite)) {
                     loadingComposite.dispose();
                  }
                  xNavComp = new AtsNavigateComposite(AtsNavigateViewItems.getInstance(), parent, SWT.NONE);

                  AtsPlugin.getInstance().setHelp(xNavComp, HELP_CONTEXT_ID, "org.eclipse.osee.ats.help.ui");
                  createToolBar();

                  // add search text box      
                  AtsQuickSearchComposite composite = new AtsQuickSearchComposite(xNavComp, SWT.NONE);
                  composite.addDisposeListener(new DisposeListener() {

                     @Override
                     public void widgetDisposed(DisposeEvent e) {
                        OseeNotificationManager.getInstance().sendNotifications();
                     }
                  });

                  if (savedFilterStr != null) {
                     xNavComp.getFilteredTree().getFilterControl().setText(savedFilterStr);
                  }
                  xNavComp.refresh();
                  xNavComp.getFilteredTree().getFilterControl().setFocus();

                  Label label = new Label(xNavComp, SWT.None);
                  String str = getWhoAmI();
                  if (AtsUtil.isAtsAdmin()) {
                     str += " - Admin";
                  }
                  if (!str.equals("")) {
                     if (AtsUtil.isAtsAdmin()) {
                        label.setForeground(Displays.getSystemColor(SWT.COLOR_RED));
                     } else {
                        label.setForeground(Displays.getSystemColor(SWT.COLOR_BLUE));
                     }
                  }
                  label.setText(str);
                  label.setToolTipText(str);
                  GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.VERTICAL_ALIGN_CENTER);
                  gridData.heightHint = 15;
                  label.setLayoutData(gridData);

                  OseeContributionItem.addTo(navView, false);
                  xNavComp.layout();

                  addExtensionPointListenerBecauseOfWorkspaceLoading();

               } catch (Exception ex) {
                  OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
               }
               return Status.OK_STATUS;
            }
         };
         Operations.scheduleJob(job, false, Job.SHORT, null);
      }
   }

   private void addExtensionPointListenerBecauseOfWorkspaceLoading() {
      IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
      extensionRegistry.addListener(new IRegistryEventListener() {
         @Override
         public void added(IExtension[] extensions) {
            xNavComp.refresh();
         }

         @Override
         public void added(IExtensionPoint[] extensionPoints) {
            xNavComp.refresh();
         }

         @Override
         public void removed(IExtension[] extensions) {
            xNavComp.refresh();
         }

         @Override
         public void removed(IExtensionPoint[] extensionPoints) {
            xNavComp.refresh();
         }
      }, "org.eclipse.osee.framework.ui.skynet.BlamOperation");
   }

   public boolean isIncludeCompleteCancelled() {
      return includeCompleteCancelled;
   }

   private String getWhoAmI() {
      try {
         String userName = UserManager.getUser().getName();
         return String.format("%s - %s:%s", userName, ClientSessionManager.getDataStoreName(),
            ClientSessionManager.getDataStoreLoginName());
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         return "Exception: " + ex.getLocalizedMessage();
      }
   }

   protected void createToolBar() {
      IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
      toolbarManager.add(new MyWorldAction());
      toolbarManager.add(new MyFavoritesAction());
      toolbarManager.add(new CollapseAllAction(xNavComp.getFilteredTree().getViewer()));
      toolbarManager.add(new ExpandAllAction(xNavComp.getFilteredTree().getViewer()));
      toolbarManager.add(new OpenChangeReportByIdAction());
      toolbarManager.add(new OpenWorldByIdAction());
      toolbarManager.add(new OpenWorkflowByIdAction());
      toolbarManager.add(new NewAction());
      getViewSite().getActionBars().updateActionBars();

      IActionBars bars = getViewSite().getActionBars();
      IMenuManager mm = bars.getMenuManager();
      mm.add(new NewAction());
      mm.add(new NewGoal());
      mm.add(OseeUiActions.createBugAction(AtsPlugin.getInstance(), this, VIEW_ID, "ATS Navigator"));

      toolbarManager.update(true);
   }

   public static NavigateView getNavigateView() {
      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      try {
         return (NavigateView) page.showView(NavigateView.VIEW_ID);
      } catch (PartInitException e1) {
         MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Launch Error",
            "Couldn't Launch OSEE ATS NavigateView " + e1.getMessage());
      }
      return null;
   }

   @Override
   public String getActionDescription() {
      IStructuredSelection sel = (IStructuredSelection) xNavComp.getFilteredTree().getViewer().getSelection();
      if (sel.iterator().hasNext()) {
         return String.format("Currently Selected - %s", ((XNavigateItem) sel.iterator().next()).getName());
      }
      return "";
   }

   private static final String INPUT = "filter";
   private static final String FILTER_STR = "filterStr";

   @Override
   public void saveState(IMemento memento) {
      super.saveState(memento);
      memento = memento.createChild(INPUT);

      if (xNavComp != null && xNavComp.getFilteredTree().getFilterControl() != null && !xNavComp.getFilteredTree().isDisposed()) {
         String filterStr = xNavComp.getFilteredTree().getFilterControl().getText();
         memento.putString(FILTER_STR, filterStr);
      }
   }
   private String savedFilterStr = null;

   @Override
   public void init(IViewSite site, IMemento memento) throws PartInitException {
      super.init(site, memento);
      try {
         if (memento != null) {
            memento = memento.getChild(INPUT);
            if (memento != null) {
               savedFilterStr = memento.getString(FILTER_STR);
            }
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.WARNING, "NavigateView error on init", ex);
      }
   }

   @Override
   public void setFocus() {
      // do nothing
   }

}