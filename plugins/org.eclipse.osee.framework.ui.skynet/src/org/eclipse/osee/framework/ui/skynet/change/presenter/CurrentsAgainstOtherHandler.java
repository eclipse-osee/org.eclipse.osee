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
package org.eclipse.osee.framework.ui.skynet.change.presenter;

import java.util.logging.Level;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.TransactionDelta;
import org.eclipse.osee.framework.jdk.core.util.AXml;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.change.ChangeUiData;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

public final class CurrentsAgainstOtherHandler implements IChangeReportUiHandler {

   @Override
   public String getActionName() {
      return "Open changes between the branch and the lastest changes from another branch";
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
      return String.format("%s - Compared to Other Branch", branchName);
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
   public String getScenarioDescriptionHtml(ChangeUiData changeUiData) throws OseeCoreException {
      TransactionDelta txDelta = changeUiData.getTxDelta();
      String data;
      if (changeUiData.isMergeBranchValid()) {
         data =
            String.format(
               "Shows all changes made to [<b>%s</b>], including changes found in the merge branch compared to branch [<b>%s</b>].",
               AXml.textToXml(txDelta.getStartTx().getBranch().getName()),
               AXml.textToXml(txDelta.getEndTx().getBranch().getName()));
      } else {
         data =
            String.format("Shows all changes made to [<b>%s</b>] compared to branch [<b>%s</b>].",
               AXml.textToXml(txDelta.getStartTx().getBranch().getName()),
               AXml.textToXml(txDelta.getEndTx().getBranch().getName()));
      }
      return data;
   }

   @Override
   public void appendTransactionInfoHtml(StringBuilder sb, ChangeUiData changeUiData) throws OseeCoreException {
      TransactionDelta txDelta = changeUiData.getTxDelta();
      sb.append("<b>Branch 1 Last Modified</b>:<br/>");
      ChangeReportInfoPresenter.addTransactionInfo(sb, txDelta.getStartTx());
      sb.append("<br/><br/>");
      sb.append("<b>Branch 2 Last Modified: </b><br/>");
      ChangeReportInfoPresenter.addTransactionInfo(sb, txDelta.getEndTx());
   }
}