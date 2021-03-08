/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.framework.ui.skynet.change.actions;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.IBranchProvider;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.search.QuickSearchView;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;

public class OpenQuickSearchAction extends Action {

   private final IBranchProvider branchProvider;

   public OpenQuickSearchAction(IBranchProvider branchProvider) {
      super("Open Quick Search", IAction.AS_PUSH_BUTTON);
      this.branchProvider = branchProvider;
      setId("open.quick.search.change.report");
      setToolTipText("Open Quick Search");
      setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.ARTIFACT_SEARCH));
   }

   @Override
   public void run() {
      Job job = new UIJob("Open Quick Search") {

         @Override
         public IStatus runInUIThread(IProgressMonitor monitor) {
            IStatus status = Status.OK_STATUS;
            try {
               BranchToken branch = BranchManager.getBranchToken(branchProvider.getBranch());
               if (branch != null) {
                  IViewPart viewPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(
                     QuickSearchView.VIEW_ID);
                  if (viewPart != null) {
                     ((QuickSearchView) viewPart).setBranch(branch);
                  }
               }
            } catch (Exception ex) {
               status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Error opening quick search view", ex);
            }
            return status;
         }
      };
      Jobs.startJob(job, true);
   }
}