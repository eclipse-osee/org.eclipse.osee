package org.eclipse.osee.framework.ui.skynet.change.presenter;

import java.text.NumberFormat;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.TransactionDelta;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.change.ChangeUiData;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

public final class SpecificTxsHandler implements IChangeReportUiHandler {

   @Override
   public String getActionName() {
      return "Compare two transactions on a branch";
   }

   @Override
   public String getActionDescription() {
      return "Compares two specific transactions on a branch.";
   }

   @Override
   public String getName(TransactionDelta txDelta) {
      String branchName;
      try {
         branchName = txDelta.getEndTx().getBranch().getShortName();
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex.toString(), ex);
         branchName = "Unknown";
      }
      String toReturn;
      if (txDelta.getEndTx().getComment() != null) {
         toReturn = String.format(" - %s", txDelta.getEndTx().getComment());
      } else {
         toReturn = String.format("%s - Transactions", branchName);
      }
      return toReturn;
   }

   @Override
   public KeyedImage getActionImage() {
      return FrameworkImage.BRANCH_CHANGE;
   }

   @Override
   public KeyedImage getScenarioImage(ChangeUiData changeUiData) {
      return FrameworkImage.DELTAS_TXS_SAME_BRANCH;
   }

   @Override
   public String getScenarioDescription(ChangeUiData changeUiData) throws OseeCoreException {
      TransactionDelta txDelta = changeUiData.getTxDelta();
      NumberFormat formatter = NumberFormat.getInstance();
      return String.format("Shows changes made to [<b>%s</b>] between transactions [<b>%s</b>] and [<b>%s</b>].",
            txDelta.getStartTx().getBranch(), formatter.format(txDelta.getStartTx().getId()),
            formatter.format(txDelta.getEndTx().getId()));
   }

   @Override
   public void appendTransactionInfo(StringBuilder sb, ChangeUiData changeUiData) throws OseeCoreException {
      TransactionDelta txDelta = changeUiData.getTxDelta();
      //      sb.append("<b>Branch 1 Last Modified</b>:<br/>");
      //      ChangeReportInfoPresenter.addTransactionInfo(sb, txDelta.getStartTx());
      //      sb.append("<br/><br/>");
      sb.append("<b>Committed: </b><br/>");
      ChangeReportInfoPresenter.addTransactionInfo(sb, txDelta.getEndTx());
   }
}