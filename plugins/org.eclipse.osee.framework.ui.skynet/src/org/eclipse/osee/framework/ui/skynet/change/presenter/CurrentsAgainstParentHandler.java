package org.eclipse.osee.framework.ui.skynet.change.presenter;

import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.TransactionDelta;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.change.ChangeUiData;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

public final class CurrentsAgainstParentHandler implements IChangeReportUiHandler {

   @Override
   public String getActionName() {
      return "Open changes between the branch and the lastest changes from its parent branch";
   }

   @Override
   public String getActionDescription() {
      return "Computes changes between a child branch and its parent branch. If a merge branch exists, its changes are included as part of the child branch.";
   }

   @Override
   public String getName(TransactionDelta txDelta) {
      String branchName;
      try {
         branchName = txDelta.getStartTx().getBranch().getShortName();
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex.toString(), ex);
         branchName = "Unknown";
      }
      return String.format("%s - Compared to Parent Branch", branchName);
   }

   @Override
   public KeyedImage getActionImage() {
      return FrameworkImage.COMPARE_PARENT_BRANCH;
   }

   @Override
   public KeyedImage getScenarioImage(ChangeUiData changeUiData) {
      KeyedImage imageKey = FrameworkImage.DELTAS;
      if (changeUiData.isMergeBranchValid()) {
         imageKey = FrameworkImage.DELTAS_DIFFERENT_BRANCHES_WITH_MERGE;
      } else {
         imageKey = FrameworkImage.DELTAS_DIFFERENT_BRANCHES;
      }
      return imageKey;
   }

   @Override
   public String getScenarioDescription(ChangeUiData changeUiData) throws OseeCoreException {
      TransactionDelta txDelta = changeUiData.getTxDelta();
      String data;
      if (changeUiData.isMergeBranchValid()) {
         data =
               String.format(
                     "Shows all changes made to [<b>%s</b>], including changes found in the merge branch compared to%s branch [<b>%s</b>].",
                     txDelta.getStartTx().getBranch(), " its parent", txDelta.getEndTx().getBranch());
      } else {
         data =
               String.format("Shows all changes made to [<b>%s</b>] compared to%s branch [<b>%s</b>].",
                     txDelta.getStartTx().getBranch(), " its parent", txDelta.getEndTx().getBranch());
      }
      return data;
   }

   @Override
   public void appendTransactionInfo(StringBuilder sb, ChangeUiData changeUiData) throws OseeCoreException {
      TransactionDelta txDelta = changeUiData.getTxDelta();
      sb.append("<b>Working Branch Last Modified</b>:<br/>");
      ChangeReportInfoPresenter.addTransactionInfo(sb, txDelta.getStartTx());
      sb.append("<br/><br/>");
      sb.append("<b>Parent Branch Last Modified: </b><br/>");
      ChangeReportInfoPresenter.addTransactionInfo(sb, txDelta.getEndTx());
   }
}