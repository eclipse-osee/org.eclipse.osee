/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.change;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.model.TransactionDelta;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.IBranchProvider;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.branch.BranchSelectionDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.progress.UIJob;

public final class UiSelectBetweenDeltasBranchProvider implements IBranchProvider {
   private final ChangeUiData uiData;

   public UiSelectBetweenDeltasBranchProvider(ChangeUiData uiData) {
      this.uiData = uiData;
   }

   @Override
   public BranchId getBranch() {
      final BranchId[] selectedBranch = new BranchId[1];

      TransactionDelta txDelta = uiData.getTxDelta();
      if (txDelta.areOnTheSameBranch()) {
         selectedBranch[0] = txDelta.getStartTx().getBranch();
      } else {
         final Collection<BranchId> selectable = new ArrayList<>();
         selectable.add(uiData.getTxDelta().getStartTx().getBranch());
         selectable.add(uiData.getTxDelta().getEndTx().getBranch());
         executeInUiThread(selectable, selectedBranch);
      }
      return selectedBranch[0];
   }

   private IStatus executeInUiThread(final Collection<BranchId> selectable, final BranchId[] selectedBranch)  {
      IStatus status = null;
      Display display = AWorkbench.getDisplay();
      if (display.getThread().equals(Thread.currentThread())) {
         status = getUserSelection(selectable, selectedBranch);
      } else {
         Job job = new UIJob("Select Branch") {
            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
               return getUserSelection(selectable, selectedBranch);
            }
         };
         try {
            Jobs.startJob(job).join();
         } catch (InterruptedException ex) {
            OseeCoreException.wrapAndThrow(ex);
         }
         status = job.getResult();
      }
      return status;
   }

   private IStatus getUserSelection(Collection<BranchId> selectable, BranchId[] selectedBranch) {
      IStatus status = Status.OK_STATUS;
      BranchSelectionDialog dialog = new BranchSelectionDialog("Select branch to compare against", selectable);
      int result = dialog.open();
      if (result == Window.OK) {
         selectedBranch[0] = dialog.getSelection();
      } else {
         status = Status.CANCEL_STATUS;
      }
      return status;
   }

}