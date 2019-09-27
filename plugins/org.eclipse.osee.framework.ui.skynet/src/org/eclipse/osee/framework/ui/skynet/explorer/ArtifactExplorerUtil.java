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
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.menu.GlobalMenuPermissions;
import org.eclipse.osee.framework.ui.skynet.menu.IGlobalMenuHelper;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.progress.UIJob;

/**
 * @author Donald G. Dunne
 */
public class ArtifactExplorerUtil {

   private ArtifactExplorerUtil() {
      // Utility Class
   }

   protected static ArtifactExplorer findView(BranchId inputBranch, IWorkbenchPage page) {
      for (IViewReference view : page.getViewReferences()) {
         if (view.getId().equals(ArtifactExplorer.VIEW_ID)) {
            if (view.getView(false) != null && inputBranch.equals(
               ((ArtifactExplorer) view.getView(false)).getBranch())) {
               try {
                  return (ArtifactExplorer) page.showView(view.getId(), view.getSecondaryId(),
                     IWorkbenchPage.VIEW_ACTIVATE);
               } catch (Exception ex) {
                  throw new RuntimeException(ex);
               }
            }
         }
      }
      try {
         ArtifactExplorer explorer =
            (ArtifactExplorer) page.showView(ArtifactExplorer.VIEW_ID, GUID.create(), IWorkbenchPage.VIEW_ACTIVATE);
         explorer.explore(OseeSystemArtifacts.getDefaultHierarchyRootArtifact(inputBranch));
         return explorer;
      } catch (Exception ex) {
         throw new RuntimeException(ex);
      }
   }

   public static void refreshBranchWarning(ArtifactExplorer artifactExplorer, TreeViewer treeViewer, IGlobalMenuHelper globalMenuHelper, BranchId branch, BranchWarningComposite branchWarningComposite) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            try {
               if (treeViewer == null || !Widgets.isAccessible(treeViewer.getTree())) {
                  return;
               }

               Control control = treeViewer.getTree();
               if (branch.isValid()) {
                  String warningStr = null;
                  if (!new GlobalMenuPermissions(globalMenuHelper).isBranchReadable(branch)) {
                     warningStr = "Branch Read Access Denied.\nContact your administrator.";
                  } else {
                     BranchState state = BranchManager.getState(branch);
                     if (state == BranchState.CREATION_IN_PROGRESS) {
                        warningStr = "Branch Creation in Progress, Please Wait.";
                     } else if (state == BranchState.COMMIT_IN_PROGRESS) {
                        warningStr = "Branch Commit in Progress, Please Close Artifact Explorer.";
                     } else if (state == BranchState.COMMITTED) {
                        warningStr = "Branch Committed, Please Close Artifact Explorer.";
                     } else if (state == BranchState.DELETED) {
                        warningStr = "Branch Deleted, Please Close Artifact Explorer.";
                     } else if (state == BranchState.REBASELINE_IN_PROGRESS) {
                        warningStr = "Branch Rebaseline in Progress, Please Wait.";
                     } else if (state == BranchState.REBASELINED) {
                        warningStr = "Branch Rebaselined, Please Close Artifact Explorer.";
                     } else if (state == BranchState.DELETE_IN_PROGRESS) {
                        warningStr = "Branch Delete in Progress, Please Close Artifact Explorer.";
                     } else if (state == BranchState.PURGE_IN_PROGRESS) {
                        warningStr = "Branch Purge in Progress, Please Close Artifact Explorer.";
                     } else if (state == BranchState.PURGED) {
                        warningStr = "Branch Purged, Please Close Artifact Explorer.";
                     }
                  }
                  if (warningStr != null) {
                     control = branchWarningComposite;
                     artifactExplorer.getBranchWarningLabel().setText(warningStr);
                     artifactExplorer.getBranchWarningLabel().update();
                     branchWarningComposite.update();
                  }
               }

               artifactExplorer.getStackLayout().topControl = control;
               artifactExplorer.getStackComposite().layout();
               artifactExplorer.getStackComposite().getParent().layout();
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      });
   }

   public static void revealArtifact(Artifact artifact) {
      final ArtifactData data = new ArtifactData(artifact);
      IOperation operation = new CheckArtifactBeforeReveal(data);
      Operations.executeAsJob(operation, true, Job.SHORT, new JobChangeAdapter() {

         @Override
         public void done(IJobChangeEvent event) {
            IStatus status = event.getResult();
            if (status.isOK()) {
               Job uiJob = new UIJob("Reveal in Artifact Explorer") {

                  @Override
                  public IStatus runInUIThread(IProgressMonitor monitor) {
                     Artifact artifact = data.getArtifact();
                     IWorkbenchPage page = AWorkbench.getActivePage();
                     ArtifactExplorer artifactExplorer = ArtifactExplorerUtil.findView(artifact.getBranch(), page);
                     artifactExplorer.getTreeViewer().setSelection(new StructuredSelection(artifact), true);
                     artifactExplorer.getTreeViewer().expandToLevel(artifact, 1);
                     return Status.OK_STATUS;
                  }
               };
               Jobs.startJob(uiJob);
            }
         }
      });
   }

}
