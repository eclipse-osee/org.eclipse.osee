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

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.osee.framework.core.model.TransactionDelta;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.ui.skynet.change.ChangeReportEditorInput;
import org.eclipse.osee.framework.ui.skynet.change.ChangeUiData;
import org.eclipse.osee.framework.ui.skynet.change.ChangeUiUtil;
import org.eclipse.osee.framework.ui.skynet.change.CompareType;
import org.eclipse.osee.framework.ui.skynet.change.ParentBranchProvider;
import org.eclipse.osee.framework.ui.skynet.change.UiOtherBranchDialogProvider;
import org.eclipse.osee.framework.ui.skynet.change.operations.LoadCompareBaseToHead;
import org.eclipse.osee.framework.ui.skynet.change.operations.LoadCompareBranchToBranch;
import org.eclipse.osee.framework.ui.swt.ImageManager;

public final class CompareAction extends Action {

   private final ChangeUiData uiData;
   private final CompareType compareType;

   public CompareAction(CompareType compareType, ChangeUiData uiData) {
      super(compareType.getHandler().getActionName(), IAction.AS_PUSH_BUTTON);
      this.compareType = compareType;
      this.uiData = uiData;
      setToolTipText(compareType.getHandler().getActionDescription());
      setImageDescriptor(ImageManager.getImageDescriptor(compareType.getHandler().getActionImage()));
   }

   @Override
   public void run() {
      TransactionDelta txDelta = new TransactionDelta(uiData.getTxDelta().getStartTx(), uiData.getTxDelta().getEndTx());
      final ChangeUiData newUiData = new ChangeUiData(compareType, txDelta);

      IOperation operation = null;
      switch (compareType) {
         case COMPARE_BASE_TO_HEAD:
            operation = new LoadCompareBaseToHead(newUiData);
            break;
         case COMPARE_CURRENTS_AGAINST_OTHER_BRANCH:
            operation = new LoadCompareBranchToBranch(newUiData, new UiOtherBranchDialogProvider(newUiData));
            break;
         case COMPARE_CURRENTS_AGAINST_PARENT:
            operation = new LoadCompareBranchToBranch(newUiData, new ParentBranchProvider(newUiData));
            break;
         default:
            throw new UnsupportedOperationException();
      }
      Operations.executeAsJob(operation, true, Job.LONG, new JobChangeAdapter() {

         @Override
         public void done(IJobChangeEvent event) {
            if (event.getResult().isOK()) {
               newUiData.setLoadOnOpen(true);
               ChangeUiUtil.open(new ChangeReportEditorInput(newUiData));
            }
         }
      });
   }
}