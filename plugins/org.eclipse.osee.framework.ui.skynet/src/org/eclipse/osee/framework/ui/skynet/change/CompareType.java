package org.eclipse.osee.framework.ui.skynet.change;

import org.eclipse.osee.framework.ui.skynet.change.presenter.BaseToHeadHandler;
import org.eclipse.osee.framework.ui.skynet.change.presenter.CurrentsAgainstOtherHandler;
import org.eclipse.osee.framework.ui.skynet.change.presenter.CurrentsAgainstParentHandler;
import org.eclipse.osee.framework.ui.skynet.change.presenter.IChangeReportUiHandler;
import org.eclipse.osee.framework.ui.skynet.change.presenter.SpecificTxsHandler;

public enum CompareType {
   COMPARE_SPECIFIC_TRANSACTIONS(new SpecificTxsHandler()),
   COMPARE_BASE_TO_HEAD(new BaseToHeadHandler()),
   COMPARE_CURRENTS_AGAINST_PARENT(new CurrentsAgainstParentHandler()),
   COMPARE_CURRENTS_AGAINST_OTHER_BRANCH(new CurrentsAgainstOtherHandler());

   private IChangeReportUiHandler handler;

   private CompareType(IChangeReportUiHandler handler) {
      this.handler = handler;
   }

   public boolean areSpecificTxs() {
      return this == CompareType.COMPARE_SPECIFIC_TRANSACTIONS;
   }

   public boolean isBaselineTxIncluded() {
      return this == CompareType.COMPARE_BASE_TO_HEAD;
   }

   public IChangeReportUiHandler getHandler() {
      return handler;
   }
}