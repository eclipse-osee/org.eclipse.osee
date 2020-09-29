/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.framework.ui.skynet.explorer;

import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.access.IAccessControlService;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.access.internal.OseeApiService;
import org.eclipse.osee.framework.ui.skynet.action.OpenAssociatedArtifactFromBranchProvider;
import org.eclipse.osee.framework.ui.skynet.change.ChangeUiUtil;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.search.QuickSearchView;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.CheckBoxDialog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;

/**
 * @author Donald G. Dunne
 */
public class ArtifactExplorerToolbar {

   private final ArtifactExplorer artifactExplorer;
   private final IToolBarManager toolbarManager;
   private Action newArtifactExplorer;
   private Action collapseAllAction;
   private Action refreshAction;
   private Action upAction;

   public ArtifactExplorerToolbar(ArtifactExplorer artifactExplorer) {
      this.artifactExplorer = artifactExplorer;
      this.toolbarManager = artifactExplorer.getViewSite().getActionBars().getToolBarManager();
   }

   public void createToolbar() {
      createCollapseAllAction(toolbarManager);
      createUpAction(toolbarManager);
      createNewArtifactExplorerAction(toolbarManager);
      createShowChangeReportAction(toolbarManager);
      createOpenQuickSearchAction(toolbarManager);
      createRefreshAction(toolbarManager);
      toolbarManager.add(new OpenAssociatedArtifactFromBranchProvider(artifactExplorer));

   }

   public void updateEnablement() {
      // The upAction may be null if this viewpart has not been layed out yet
      if (upAction != null) {
         try {
            Artifact explorerRoot = artifactExplorer.getExplorerRoot();
            upAction.setEnabled(explorerRoot != null && explorerRoot.hasParent());
         } catch (OseeCoreException ex) {
            upAction.setEnabled(false);
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }

   private void createRefreshAction(IToolBarManager toolbarManager) {
      // Refresh access control, then refresh the view
      refreshAction = new Action("Refresh") {
         @Override
         public void run() {
            try {
               if (!MessageDialog.openConfirm(Displays.getActiveShell(), "Refresh Access and Reload",
                  "Normal operation of Artifact Explorer requires no Refresh.  This refresh is " //
                     + "to help recover from an Access Control bug that we are tracking down.  If " //
                     + "you are locked out from Access Control and think it's incorrect, this will reload " //
                     + "Access Control and the reload Artifact Explorer.\n\nContinue?")) {
                  return;
               }

               artifactExplorer.setRefreshing(true);
               artifactExplorer.refreshBranchWarning();

               Job refreshJob = new Job("Refreshing Access and Artifact Explorer") {

                  @Override
                  protected IStatus run(IProgressMonitor monitor) {
                     OseeApiService.get().getAccessControlService().clearCaches();
                     OseeApiService.get().getAccessControlService().ensurePopulated();

                     Displays.ensureInDisplayThread(new Runnable() {

                        @Override
                        public void run() {
                           artifactExplorer.setRefreshing(false);
                           artifactExplorer.refreshBranchWarning();
                           ArtifactExplorer.exploreBranch(artifactExplorer.getBranch());
                           if (OseeApiService.get().getAccessControlService().isOseeAdmin()) {
                              CheckBoxDialog dialog =
                                 new CheckBoxDialog("Admin - Enable Debug", "Enable Branch Access Debug",
                                    "Check to enable Branch Access Debug if instructed to do so");
                              if (dialog.open() == Window.OK) {
                                 System.setProperty(IAccessControlService.DEBUG_BRANCH_ACCESS, "true");
                              } else {
                                 System.setProperty(IAccessControlService.DEBUG_BRANCH_ACCESS, "false");
                              }
                           }
                        }
                     });
                     return Status.OK_STATUS;
                  }
               };
               Jobs.startJob(refreshJob);
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      };
      refreshAction.setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.REFRESH));
      toolbarManager.add(refreshAction);
   }

   private void createOpenQuickSearchAction(IToolBarManager toolbarManager) {
      Action openQuickSearch =
         new Action("Quick Search", ImageManager.getImageDescriptor(FrameworkImage.ARTIFACT_SEARCH)) {
            @Override
            public void run() {
               Job job = new UIJob("Open Quick Search") {

                  @Override
                  public IStatus runInUIThread(IProgressMonitor monitor) {
                     IStatus status = Status.OK_STATUS;
                     try {
                        IViewPart viewPart =
                           PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(
                              QuickSearchView.VIEW_ID);
                        if (viewPart != null) {
                           BranchToken branch = BranchManager.getBranchToken(artifactExplorer.getBranch());
                           if (branch != null) {
                              ((QuickSearchView) viewPart).setBranch(branch);
                           }
                        }
                     } catch (Exception ex) {
                        status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Error opening quick search", ex);
                     }
                     return status;
                  }
               };
               Jobs.startJob(job);
            }
         };
      openQuickSearch.setToolTipText("Open Quick Search View");
      toolbarManager.add(openQuickSearch);
   }

   protected void createUpAction(IToolBarManager toolbarManager) {
      upAction = new Action("View Parent") {
         @Override
         public void run() {
            try {
               Artifact explorerRoot = artifactExplorer.getExplorerRoot();
               Artifact parent = explorerRoot.getParent();

               if (parent == null) {
                  return;
               }

               Object[] expanded = artifactExplorer.getTreeViewer().getExpandedElements();
               Object[] expandedPlus = new Object[expanded.length + 1];
               for (int i = 0; i < expanded.length; i++) {
                  expandedPlus[i] = expanded[i];
               }
               expandedPlus[expandedPlus.length - 1] = explorerRoot;

               artifactExplorer.explore(parent);

               artifactExplorer.getTreeViewer().setExpandedElements(expandedPlus);
            } catch (Exception ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      };

      upAction.setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.ARROW_UP_YELLOW));
      upAction.setToolTipText("View Parent");
      updateEnablement();
      toolbarManager.add(upAction);
   }

   private void createNewArtifactExplorerAction(IToolBarManager toolbarManager) {

      newArtifactExplorer = new Action("New Artifact Explorer") {
         @Override
         public void run() {
            IWorkbenchPage page = AWorkbench.getActivePage();
            ArtifactExplorer artifactExplorer;
            try {
               artifactExplorer = (ArtifactExplorer) page.showView(ArtifactExplorer.VIEW_ID, GUID.create(),
                  IWorkbenchPage.VIEW_ACTIVATE);
               if (artifactExplorer.getBranch().isValid()) {
                  artifactExplorer.explore(
                     OseeSystemArtifacts.getDefaultHierarchyRootArtifact(artifactExplorer.getBranch()));
                  artifactExplorer.setExpandedArtifacts(artifactExplorer.getTreeViewer().getExpandedElements());
               }
            } catch (Exception ex) {
               throw new RuntimeException(ex);
            }
         }
      };

      newArtifactExplorer.setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.ARTIFACT_EXPLORER));
      toolbarManager.add(newArtifactExplorer);
   }

   private void createShowChangeReportAction(IToolBarManager toolbarManager) {
      refreshAction = new Action("Show Change Report") {
         @Override
         public void run() {
            try {
               ChangeUiUtil.open(artifactExplorer.getBranch());
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      };

      refreshAction.setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.BRANCH_CHANGE));
      toolbarManager.add(refreshAction);
   }

   private void createCollapseAllAction(IToolBarManager toolbarManager) {
      collapseAllAction = new Action("Collapse All") {
         @Override
         public void run() {
            if (artifactExplorer.getTreeViewer() != null) {
               artifactExplorer.getTreeViewer().collapseAll();
            }
         }
      };

      collapseAllAction.setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.COLLAPSE_ALL));
      toolbarManager.add(collapseAllAction);
   }

}
