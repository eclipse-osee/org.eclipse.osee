/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.util;

import java.rmi.activation.Activator;
import java.util.Arrays;
import java.util.List;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.core.client.search.AtsArtifactQuery;
import org.eclipse.osee.ats.health.ValidateAtsDatabase;
import org.eclipse.osee.ats.health.ValidateResults;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class DoesNotWorkItemAts extends XNavigateItemAction {

   public DoesNotWorkItemAts(XNavigateItem parent) {
      super(parent, "Does Not Work - ATS - Fix Working attributes", PluginUiImage.ADMIN);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      if (!MessageDialog.openConfirm(Displays.getActiveShell(), getName(), getName())) {
         return;
      }

      List<String> ids =
         Arrays.asList("ZW43T", "LQ4GH", "F090B", "1BN39", "FF7LC", "JXJ7G", "SP4TF", "37SWW", "Y3TRT", "UF9Z8",
            "XQ0GL", "H9MKB", "SML1L", "J27TH", "S192U", "UZSGK", "RMLXB", "KPY74", "2BQZM", "2LXD8", "SH9P9", "D25LM",
            "KS57V", "R4Z07", "VHKFM", "7SK10", "AKF8Y", "W37FQ", "AR47S", "8S42M", "V588M", "59TJZ", "NPFH7", "DVT59",
            "8RBCN", "X3YX0", "0NVB8", "37T1Q", "LJ43A", "KCM0M", "QB69W", "R9WH4", "U3DM2", "XK7XZ", "H6KK9", "SLHHC",
            "2J78V", "AMGR8", "ABZ4Y", "EYGZ1", "94NJT", "CWTFT", "9J4F9", "XDT9J", "75L5S", "YPYFZ", "MYC7Z", "H1MLN",
            "B1Y4G", "ZLDGF", "03G1A", "62VXP", "FZNF5", "82KPQ", "F1DHN", "X0RME", "GWB6V", "DTN1W", "DBBGL", "W258D",
            "KXMB8", "M26QT", "66QZY", "LB74L", "NDGMB", "XCHJ9", "72PTX", "SV3L9", "TN0RA", "XK66N", "7PPB2", "Q540H",
            "9C084", "5ZWF1", "U7Q7S", "KD07Q", "WYDFX", "AGS5Y", "4XDN4", "HLR25", "3ZC5D", "DR6HC", "2TGJE", "5DNQN",
            "AZ0SL", "YWV4E", "DPYL1", "44TSE", "H9DQU", "TP6NH", "64DGV", "JRF5C", "LQGN1", "LB7LM", "PFK3K", "2J4BV",
            "N4JK9", "M39JC", "5H7Y1", "YY6FM", "9ND31", "RLGD3", "ZLNVL", "2FTZN", "U1PB8", "DGK7V", "SWWR4", "H9ZGD",
            "18JBF", "T6PXC", "NVGXG", "2910L", "Q7X5S", "T2MB4", "WV9RH", "AGY4Z", "HFQ33", "N1Y2K", "CYSGQ", "9W3D4",
            "5GRJF", "CTVRS", "PYJRJ", "612WQ", "GYTZ6", "YPFGW", "PHQGK", "ULB5K", "16KTW", "QHTKB", "QRNTJ", "HS1CR",
            "UTN8A", "QP3RQ", "8QPCY", "BZ0LK", "5RT4C", "LYFPD", "88KQV", "ACGJV", "U0M9B");

      try {
         List<Artifact> artifacts = AtsArtifactQuery.getArtifactListFromIds(ids);

         if (ids.size() != artifacts.size()) {
            System.err.println(String.format("Id size %d doesn't match artifact size %d", ids.size(), artifacts.size()));
         }
         ValidateResults results = new ValidateResults();
         SkynetTransaction transaction =
            TransactionManager.createTransaction(AtsUtil.getAtsBranch(), "Fix Working attributes.");
         ValidateAtsDatabase.testCompletedCancelledStateAttributesSet(artifacts, transaction, results);

         XResultData xResultData = new XResultData();
         results.addResultsMapToResultData(xResultData);
         XResultDataUI.report(xResultData, getName());

         transaction.execute();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

      AWorkbench.popup("Completed", "Complete");
   }
}
