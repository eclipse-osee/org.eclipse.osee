/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.ide.operation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

/**
 * @author Megumi Telles
 */
public class CreateWorkingBranchFromTxBlam extends AbstractBlam {

   private static final String ATS_ID_TX_WIDGET_NAME = "USAGE: Team Workflow ATS ID, TransactionId (pair on each line)";
   private static final int PAIR_SIZE = 2;

   private static final String description =
      "'Copy and paste' or 'type' in Team Workflow ATS ID,Transaction Id from which to create working branch";

   public CreateWorkingBranchFromTxBlam() {
      super(null, description, BlamUiSource.FILE);
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      String input = variableMap.getString(ATS_ID_TX_WIDGET_NAME);
      ArrayList<String> idTxs = new ArrayList<>(Arrays.asList(input.split("\\r?\\n")));
      for (String idTx : idTxs) {
         String[] pairs = idTx.split("[,\\s]+");
         if (pairs.length == PAIR_SIZE) {
            String idNumber = pairs[0];
            TransactionToken parentTransactionId = TransactionManager.getTransaction(Long.valueOf(pairs[1]));
            try {
               Artifact art = AtsApiService.get().getQueryServiceIde().getArtifact(Long.valueOf(idNumber));
               if (art.isOfType(AtsArtifactTypes.TeamWorkflow)) {
                  TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) art;
                  if (AtsApiService.get().getBranchService().isCommittedBranchExists(teamArt)) {
                     AWorkbench.popup(
                        "Committed branch already exists. Can not create another working branch once changes have been committed.");
                     return;
                  }
                  AtsApiService.get().getBranchServiceIde().createWorkingBranch(teamArt, parentTransactionId, true);
               } else {
                  AWorkbench.popup("ERROR", "Must enter a Team Workflow ID");
                  return;
               }
            } catch (Exception ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         } else {
            log(String.format("Skipping Input [%s] - Not in usage format <Team Workflow ID, TransactionId> \n",
               Arrays.toString(pairs)));
         }
      }
   }

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(XNavigateItem.TOP_ADMIN, XNavItemCat.OSEE_ADMIN);
   }

}
