package org.eclipse.osee.framework.ui.skynet.change.presenter;

import java.text.NumberFormat;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.TransactionDelta;
import org.eclipse.osee.framework.jdk.core.util.AXml;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.change.ChangeUiData;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

public final class BaseToHeadHandler implements IChangeReportUiHandler {

   @Override
   public String getActionName() {
      return "Open report showing all changes made the branch";
   }

   @Override
   public String getActionDescription() {
      return "Compute changes made to a branch from when the branch was create until its last transaction.";
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
      return String.format("%s - All Changes", branchName);
   }

   @Override
   public KeyedImage getActionImage() {
      return FrameworkImage.COMPARE_HEAD_TX;
   }

   @Override
   public KeyedImage getScenarioImage(ChangeUiData changeUiData) {
      return FrameworkImage.DELTAS_BASE_TO_HEAD_TXS;
   }

   @Override
   public String getScenarioDescriptionHtml(ChangeUiData changeUiData) throws OseeCoreException {
      TransactionDelta txDelta = changeUiData.getTxDelta();
      NumberFormat formatter = NumberFormat.getInstance();
      return String.format(
            "Shows all changes made to [<b>%s</b>] from when it was created (transaction <b>%s</b>) until it was last modified (transaction <b>%s</b>).",
            AXml.textToXml(txDelta.getStartTx().getBranch().getName()),
            AXml.textToXml(formatter.format(txDelta.getStartTx().getId())),
            AXml.textToXml(formatter.format(txDelta.getEndTx().getId())));
   }

   @Override
   public void appendTransactionInfoHtml(StringBuilder sb, ChangeUiData changeUiData) throws OseeCoreException {
      TransactionDelta txDelta = changeUiData.getTxDelta();
      sb.append("<b>Created: </b><br/>");
      ChangeReportInfoPresenter.addTransactionInfo(sb, txDelta.getStartTx());
      sb.append("<br/><br/>");
      sb.append("<b>Last Modified: </b><br/>");
      ChangeReportInfoPresenter.addTransactionInfo(sb, txDelta.getEndTx());
   }

}