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
package org.eclipse.osee.framework.ui.skynet.user;

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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.operation.OperationBuilder;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.access.UserGroupService;
import org.eclipse.osee.framework.ui.plugin.xnavigate.IXNavigateEventListener;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.OseeStatusContributionItemFactory;
import org.eclipse.osee.framework.ui.skynet.action.CollapseAllAction;
import org.eclipse.osee.framework.ui.skynet.action.ExpandAllAction;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionExceptionComposite;
import org.eclipse.osee.framework.ui.skynet.util.LoadingComposite;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
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
public class UserNavigateView extends ViewPart implements IXNavigateEventListener {

   public static final String VIEW_ID = "org.eclipse.osee.framework.ui.skynet.UserNavigateView";
   private static final String INPUT = "filter";
   private static final String FILTER_STR = "filterStr";

   private String savedFilterStr;
   private XNavigateComposite xNavComp;
   private Composite parent;
   private LoadingComposite loadingComposite;

   @Override
   public void createPartControl(Composite parent) {
      this.parent = parent;
      if (DbConnectionExceptionComposite.dbConnectionIsOk(parent)) {
         loadingComposite = new LoadingComposite(parent);
         refreshData();
      }
   }

   @Override
   public void refresh(XNavigateItem item) {
      if (xNavComp != null && Widgets.isAccessible(xNavComp.getFilteredTree()) && Widgets.isAccessible(
         xNavComp.getFilteredTree().getViewer().getTree())) {
         xNavComp.getFilteredTree().getViewer().refresh(item);
      }
   }

   public void refreshData() {
      OperationBuilder builder = Operations.createBuilder("Load User Navigator", new UserNavigateViewItemsOperation());
      Operations.executeAsJob(builder.build(), false, Job.LONG, new ReloadJobChangeAdapter(this));
   }

   private final class ReloadJobChangeAdapter extends JobChangeAdapter {

      private final UserNavigateView navView;

      private ReloadJobChangeAdapter(UserNavigateView navView) {
         this.navView = navView;
      }

      @Override
      public void done(IJobChangeEvent event) {
         Job job = new UIJob("Load Review Navigator") {

            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
               try {
                  showBusy(false);
                  if (Widgets.isAccessible(loadingComposite)) {
                     loadingComposite.dispose();
                  }

                  if (DbConnectionExceptionComposite.dbConnectionIsOk(parent)) {

                     xNavComp =
                        new XNavigateComposite(UserNavigateViewItems.getInstance(), parent, SWT.NONE, savedFilterStr);

                     createToolBar();

                     Label label = new Label(xNavComp, SWT.None);
                     String str = getWhoAmI();
                     if (UserGroupService.getOseeAdmin().isCurrentUserMember()) {
                        str += " - Admin";
                     }
                     if (!str.equals("")) {
                        if (UserGroupService.getOseeAdmin().isCurrentUserMember()) {
                           label.setForeground(Displays.getSystemColor(SWT.COLOR_RED));
                        } else {
                           label.setForeground(Displays.getSystemColor(SWT.COLOR_BLUE));
                        }
                     }
                     label.setText(str);
                     label.setToolTipText(str);

                     GridData gridData = new GridData(SWT.CENTER, SWT.CENTER, true, false);
                     gridData.heightHint = 15;
                     label.setLayoutData(gridData);

                     xNavComp.refresh();
                     xNavComp.getFilteredTree().getFilterControl().setFocus();

                     parent.getParent().layout(true);
                     parent.layout(true);

                     OseeStatusContributionItemFactory.addTo(navView, false);
                     addExtensionPointListenerBecauseOfWorkspaceLoading();

                  }

               } catch (Exception ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
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

   private String getWhoAmI() {
      try {
         String userName = UserManager.getUser().getName();
         return String.format("%s - %s:%s", userName, ClientSessionManager.getDataStoreName(),
            ClientSessionManager.getDataStoreLoginName());
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return "Exception: " + ex.getLocalizedMessage();
      }
   }

   protected void createToolBar() {
      IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
      toolbarManager.add(new CollapseAllAction(xNavComp.getFilteredTree().getViewer()));
      toolbarManager.add(new ExpandAllAction(xNavComp.getFilteredTree().getViewer()));
      getViewSite().getActionBars().updateActionBars();
      toolbarManager.update(true);
   }

   public static UserNavigateView getNavigateView() {
      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      try {
         return (UserNavigateView) page.showView(UserNavigateView.VIEW_ID);
      } catch (PartInitException e1) {
         MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Launch Error",
            "Couldn't Launch OSEE User NavigateView " + e1.getMessage());
      }
      return null;
   }

   @Override
   public void saveState(IMemento memento) {
      super.saveState(memento);
      memento = memento.createChild(INPUT);

      if (DbConnectionExceptionComposite.dbConnectionIsOk()) {
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

}