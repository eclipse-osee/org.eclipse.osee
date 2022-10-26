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

package org.eclipse.osee.ats.ide.navigate;

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
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.osee.ats.help.ui.AtsHelpContext;
import org.eclipse.osee.ats.ide.actions.MyFavoritesAction;
import org.eclipse.osee.ats.ide.actions.MyWorldAction;
import org.eclipse.osee.ats.ide.actions.NewAction;
import org.eclipse.osee.ats.ide.actions.OpenChangeReportByIdAction;
import org.eclipse.osee.ats.ide.actions.OpenWorkflowByIdAction;
import org.eclipse.osee.ats.ide.actions.OpenWorldByIdAction;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.search.quick.AtsQuickSearchComposite;
import org.eclipse.osee.framework.core.operation.OperationBuilder;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.result.Manipulations;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.HelpUtil;
import org.eclipse.osee.framework.ui.plugin.util.WorkbenchTargetProvider;
import org.eclipse.osee.framework.ui.plugin.xnavigate.INavigateItemRefresher;
import org.eclipse.osee.framework.ui.plugin.xnavigate.IXNavigateEventListener;
import org.eclipse.osee.framework.ui.plugin.xnavigate.NavigateItemCollector;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateEventManager;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemProviders;
import org.eclipse.osee.framework.ui.skynet.OseeStatusContributionItemFactory;
import org.eclipse.osee.framework.ui.skynet.OseeTargetContributionItem;
import org.eclipse.osee.framework.ui.skynet.action.CollapseAllAction;
import org.eclipse.osee.framework.ui.skynet.action.ExpandAllAction;
import org.eclipse.osee.framework.ui.skynet.action.RefreshAction;
import org.eclipse.osee.framework.ui.skynet.action.RefreshAction.IRefreshActionHandler;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionExceptionComposite;
import org.eclipse.osee.framework.ui.skynet.util.LoadingComposite;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.UIJob;

/**
 * @author Donald G. Dunne
 */
public class NavigateView extends ViewPart implements IXNavigateEventListener, IRefreshActionHandler, INavigateItemRefresher {

   public static final String VIEW_ID = "org.eclipse.osee.navigate.NavigateView";
   private static final String INPUT = "filter";
   private static final String FILTER_STR = "filterStr";
   private static NavigateView navView;

   private String savedFilterStr;
   private AtsNavigateComposite xNavComp;
   private Composite parent;
   private LoadingComposite loadingComposite;
   private NavigateItemCollector itemCollector;
   private XResultData rd;

   @Override
   public void createPartControl(Composite parent) {
      this.parent = parent;
      NavigateView.navView = this;
      if (DbConnectionExceptionComposite.dbConnectionIsOk(parent)) {
         // Preload user groups for improved performance
         AtsApiService.get().userService().getMyUserGroups();
         loadingComposite = new LoadingComposite(parent);
         refreshData();
      }
   }

   public void refreshData() {
      OperationBuilder builder = Operations.createBuilder("Load OSEE Navigator");
      rd = new XResultData();
      itemCollector = new NavigateItemCollector(XNavigateItemProviders.getProviders(), navView, rd);
      builder.addOp(new AtsNavigateViewItemsOperation(itemCollector));
      Operations.executeAsJob(builder.build(), false, Job.LONG, new ReloadJobChangeAdapter(this));
   }

   private final class ReloadJobChangeAdapter extends JobChangeAdapter {

      private final NavigateView navView;

      private ReloadJobChangeAdapter(NavigateView navView) {
         this.navView = navView;
      }

      @Override
      public void done(IJobChangeEvent event) {
         Job job = new UIJob("Load OSEE Navigator") {

            private Label userLabel;

            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
               try {
                  showBusy(false);
                  if (Widgets.isAccessible(loadingComposite)) {
                     loadingComposite.dispose();
                  } else if (Widgets.isAccessible(xNavComp)) {
                     getViewSite().getActionBars().getToolBarManager().removeAll();
                     xNavComp.dispose();
                  }

                  if (DbConnectionExceptionComposite.dbConnectionIsOk(parent)) {

                     if (Widgets.isAccessible(parent)) {

                        xNavComp = new AtsNavigateComposite(itemCollector, parent, SWT.NONE, savedFilterStr);

                        if (rd.isErrors()) {
                           XResultDataUI.report(rd, "OSEE Navigator", Manipulations.ERROR_WARNING_HEADER,
                              Manipulations.CONVERT_NEWLINES, Manipulations.ERROR_RED);
                        }

                        XNavigateEventManager.register(navView);
                        HelpUtil.setHelp(xNavComp, AtsHelpContext.NAVIGATOR);
                        createToolBar();

                        new AtsQuickSearchComposite(xNavComp, SWT.NONE);

                        userLabel = new Label(xNavComp, SWT.None);
                        userLabel.addListener(SWT.MouseDoubleClick, new Listener() {
                           @Override
                           public void handleEvent(Event event) {
                              ToggleAtsAdmin.run();
                           }
                        });
                        refreshUserLabel();

                        GridData gridData = new GridData(SWT.CENTER, SWT.TOP, true, false);
                        gridData.heightHint = 20;
                        userLabel.setLayoutData(gridData);

                        xNavComp.refresh();

                        xNavComp.layout(true);
                        parent.layout(true);
                        parent.getParent().layout(true);

                        OseeStatusContributionItemFactory.addTo(navView, false);
                        addExtensionPointListenerBecauseOfWorkspaceLoading();

                        xNavComp.getFilteredTree().getFilterControl().addModifyListener(new ModifyListener() {

                           @Override
                           public void modifyText(ModifyEvent e) {
                              String filterText = xNavComp.getFilteredTree().getFilterControl().getText();
                              if (Strings.isInValid(filterText)) {
                                 refreshItems();
                              }
                           }
                        });
                     }

                  }
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               }
               return Status.OK_STATUS;
            }

            public void refreshUserLabel() {
               String str = getWhoAmI();
               String oseeTarget = null;
               WorkbenchTargetProvider provider = OseeTargetContributionItem.getWorkbenchProvider();
               if (provider != null) {
                  oseeTarget = provider.getText();
               }
               boolean atsAdmin = AtsApiService.get().getUserService().isAtsAdmin();
               boolean oseeTargetIsValid = Strings.isValid(oseeTarget);
               if (oseeTargetIsValid) {
                  str += " - [" + oseeTarget + "]";
               }
               if (atsAdmin) {
                  str += " - [Admin]";
                  userLabel.setForeground(Displays.getSystemColor(SWT.COLOR_RED));
               } else {

                  userLabel.setForeground(Displays.getSystemColor(SWT.COLOR_BLUE));
               }
               userLabel.setText(str);
               str += " - [" + System.getProperty("user.name") + "]";
               userLabel.setToolTipText(str);
            }
         };
         Operations.scheduleJob(job, false, Job.SHORT, null);
      }
   }

   @Override
   public void refresh(XNavigateItem item) {
      if (xNavComp != null && Widgets.isAccessible(xNavComp.getFilteredTree()) && Widgets.isAccessible(
         xNavComp.getFilteredTree().getViewer().getTree())) {
         xNavComp.getFilteredTree().getViewer().refresh(item);
      }
   }

   private void addExtensionPointListenerBecauseOfWorkspaceLoading() {
      IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
      extensionRegistry.addListener(new IRegistryEventListener() {
         @Override
         public void added(IExtension[] extensions) {
            refreshNavComp();
         }

         @Override
         public void added(IExtensionPoint[] extensionPoints) {
            refreshNavComp();
         }

         @Override
         public void removed(IExtension[] extensions) {
            refreshNavComp();
         }

         @Override
         public void removed(IExtensionPoint[] extensionPoints) {
            refreshNavComp();
         }
      }, "org.eclipse.osee.framework.ui.skynet.BlamOperation");
   }

   private void refreshNavComp() {
      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            xNavComp.refresh();
         }
      });
   }

   private String getWhoAmI() {
      try {
         String userName = AtsApiService.get().userService().getUser().getName();
         return String.format("[%s]", userName);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return "Exception: " + ex.getLocalizedMessage();
      }
   }

   protected void createToolBar() {
      IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
      toolbarManager.add(new RefreshAction(this));
      toolbarManager.add(new MyWorldAction());
      toolbarManager.add(new MyFavoritesAction());
      toolbarManager.add(new CollapseAllAction(xNavComp.getFilteredTree().getViewer()));
      toolbarManager.add(new ExpandAllAction(xNavComp.getFilteredTree().getViewer()));
      toolbarManager.add(new OpenChangeReportByIdAction());
      toolbarManager.add(new OpenWorldByIdAction());
      toolbarManager.add(new OpenWorkflowByIdAction());
      toolbarManager.add(new NewAction());
      getViewSite().getActionBars().updateActionBars();
      toolbarManager.update(true);
   }

   public static NavigateView getNavigateView() {
      return navView;
   }

   @Override
   public void saveState(IMemento memento) {
      super.saveState(memento);
      if (DbConnectionExceptionComposite.dbConnectionIsOk()) {
         memento = memento.createChild(INPUT);

         if (xNavComp != null && xNavComp.getFilteredTree().getFilterControl() != null && !xNavComp.getFilteredTree().isDisposed()) {
            String filterStr = xNavComp.getFilteredTree().getFilterControl().getText();
            memento.putString(FILTER_STR, filterStr);
         }
      }
   }

   @Override
   public void init(IViewSite site, IMemento memento) throws PartInitException {
      super.init(site, memento);
      if (DbConnectionExceptionComposite.dbConnectionIsOk()) {

         try {
            if (memento != null) {
               memento = memento.getChild(INPUT);
               if (memento != null) {
                  savedFilterStr = memento.getString(FILTER_STR);
               }
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.WARNING, "NavigateView error on init", ex);
         }
      }
   }

   @Override
   public void setFocus() {
      if (loadingComposite != null && !loadingComposite.isDisposed()) {
         loadingComposite.setFocus();
      }
   }

   public XNavigateItem getItem(long topLinkId, boolean recurseChildren) {
      for (TreeItem treeItem : xNavComp.getFilteredTree().getViewer().getTree().getItems()) {
         XNavigateItem treeNavItem = (XNavigateItem) treeItem.getData();
         XNavigateItem foundItem = getItem(treeNavItem, topLinkId, recurseChildren);
         if (foundItem != null) {
            return foundItem;
         }
      }
      return null;
   }

   public XNavigateItem getItem(XNavigateItem item, long topLinkId, boolean recurseChildren) {
      if (item.getId() == topLinkId) {
         return item;
      }
      if (recurseChildren) {
         for (XNavigateItem child : item.getChildren()) {
            XNavigateItem found = getItem(child, topLinkId, recurseChildren);
            if (found != null) {
               return found;
            }
         }
      }
      return null;
   }

   /**
    * Calls refresh on all XNavigateItem(s)
    */
   public void refreshItems() {
      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            for (TreeItem treeItem : xNavComp.getFilteredTree().getViewer().getTree().getItems()) {
               XNavigateItem treeNavItem = (XNavigateItem) treeItem.getData();
               refreshItem(treeNavItem);
            }
         }
      });
   }

   public void refreshItem(XNavigateItem item) {
      item.refresh();
      for (XNavigateItem child : item.getChildren()) {
         refreshItem(child);
      }
   }

   @Override
   public void refreshActionHandler() {
      refreshData();
      refreshItems();
   }

   public static boolean isAccessible() {
      return navView != null && Widgets.isAccessible(navView.parent);
   }

   public AtsNavigateComposite getxNavComp() {
      return xNavComp;
   }

}