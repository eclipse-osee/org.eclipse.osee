package org.eclipse.osee.framework.ui.skynet.change.actions;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.Action;
import org.eclipse.osee.framework.core.data.TransactionDelta;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.ui.skynet.change.ChangeReportEditorInput;
import org.eclipse.osee.framework.ui.skynet.change.ChangeUiData;
import org.eclipse.osee.framework.ui.skynet.change.ChangeUiUtil;
import org.eclipse.osee.framework.ui.skynet.change.CompareType;
import org.eclipse.osee.framework.ui.skynet.change.IChangeReportView;
import org.eclipse.osee.framework.ui.skynet.change.ParentBranchProvider;
import org.eclipse.osee.framework.ui.skynet.change.UiOtherBranchDialogProvider;
import org.eclipse.osee.framework.ui.skynet.change.operations.LoadCompareBaseToHead;
import org.eclipse.osee.framework.ui.skynet.change.operations.LoadCompareBranchToBranch;
import org.eclipse.osee.framework.ui.swt.ImageManager;

public final class CompareAction extends Action {

   private final ChangeUiData uiData;
   private final IChangeReportView view;
   private final CompareType compareType;

   public CompareAction(CompareType compareType, IChangeReportView view, ChangeUiData uiData) {
      super(compareType.getHandler().getActionName(), Action.AS_PUSH_BUTTON);
      this.compareType = compareType;
      this.view = view;
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