/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.explorer;

import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.action.OpenAssociatedArtifactFromBranchProvider;
import org.eclipse.osee.framework.ui.skynet.change.ChangeUiUtil;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.search.QuickSearchView;
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
   private Action showChangeReport;
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
      addOpenQuickSearchAction(toolbarManager);
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

   private void addOpenQuickSearchAction(IToolBarManager toolbarManager) {
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
                           BranchId branch = artifactExplorer.getBranch();
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
               if (artifactExplorer.getBranch() != null) {
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
      showChangeReport = new Action("Show Change Report") {
         @Override
         public void run() {
            try {
               ChangeUiUtil.open(artifactExplorer.getBranch());
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      };

      showChangeReport.setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.BRANCH_CHANGE));
      toolbarManager.add(showChangeReport);
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
