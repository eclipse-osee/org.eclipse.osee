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
import org.eclipse.osee.ats.health.ValidateAtsDatabase;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
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
      super(parent, "Does Not Work - ATS - Fix Completed Cancelled attributes", PluginUiImage.ADMIN);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      if (!MessageDialog.openConfirm(Displays.getActiveShell(), getName(), getName())) {
         return;
      }

      List<String> hrids =
         Arrays.asList("ZW43T", "LQ4GH", "F090B", "1BN39", "FF7LC", "JXJ7G", "SP4TF", "37SWW", "Y3TRT", "UF9Z8",
            "E9QPQ", "FP0ML", "K0QXZ", "RNPLG", "W9ZML", "N0QSC", "A496H", "SMS0Q", "4KNWK", "UNNPV", "4BSXM", "HDWS9",
            "ALPTS", "W9S49", "L0WRL", "AHZHZ", "R3V4K", "VTC09", "1NN86", "SQ7WN", "R3JJA", "UKQ09", "R0GRX", "V1KNA",
            "7JP0L", "VN31S", "EHMF7", "Q6QJM", "0SFYF", "NZKL6", "FB4J0", "FN9XF", "80W10", "6XPT8", "MPM7R", "T2WJ2",
            "T6P5V", "LH29W", "S2J26", "HNV7D");

      try {
         List<Artifact> artifacts = ArtifactQuery.getArtifactListFromIds(hrids, AtsUtil.getAtsBranchToken());

         if (hrids.size() != artifacts.size()) {
            System.err.println(String.format("Hrid size %d doesn't match artifact size %d", hrids.size(),
               artifacts.size()));
         }
         HashCollection<String, String> testNameToResultsMap = null;
         testNameToResultsMap = new HashCollection<String, String>();

         SkynetTransaction transaction =
            TransactionManager.createTransaction(AtsUtil.getAtsBranch(), "Fix Cancelled/Completed attributes.");
         ValidateAtsDatabase.testCompletedCancelledStateAttributesSet(artifacts, transaction, testNameToResultsMap);

         XResultData xResultData = new XResultData();
         ValidateAtsDatabase.addResultsMapToResultData(xResultData, testNameToResultsMap);
         XResultDataUI.report(xResultData, getName());

         transaction.execute();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

      AWorkbench.popup("Completed", "Complete");
   }
}
