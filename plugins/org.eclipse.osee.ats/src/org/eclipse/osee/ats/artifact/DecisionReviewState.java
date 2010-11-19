package org.eclipse.osee.ats.artifact;

import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IWorkPage;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageType;

public enum DecisionReviewState implements IWorkPage {
   Prepare(WorkPageType.Working),
   Decision(WorkPageType.Working),
   Followup(WorkPageType.Working),
   Completed(WorkPageType.Completed);

   private final WorkPageType workPageType;

   private DecisionReviewState(WorkPageType workPageType) {
      this.workPageType = workPageType;
   }

   @Override
   public WorkPageType getWorkPageType() {
      return workPageType;
   }

   @Override
   public String getPageName() {
      return name();
   }

   @Override
   public boolean isCompletedOrCancelledPage() {
      return getWorkPageType().isCompletedOrCancelledPage();
   }

   @Override
   public boolean isCompletedPage() {
      return getWorkPageType().isCompletedPage();
   }

   @Override
   public boolean isCancelledPage() {
      return getWorkPageType().isCancelledPage();
   }

   @Override
   public boolean isWorkingPage() {
      return getWorkPageType().isWorkingPage();
   }

};
