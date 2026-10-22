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
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.ide.blam.AbstractAtsBlam;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.util.CoreImage;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.demo.DemoUtilCore;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.osgi.service.component.annotations.Component;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.ats.api.data.AtsUserGroups;

/**
 * @author Megumi Telles
 */
@Component(service = AbstractBlam.class, immediate = true)
public class CreateWorkingBranchFromTxBlam extends AbstractAtsBlam {

   private static final String BLAM_NAME = "Create Working Branch from TX Id";
   private static final String BLAM_DESC =
      "Enter Team Workflow ATS ID,Transaction Id (pair on each line) from which to create working branch.";
   static final String ATS_ID_TX_WIDGET_NAME = "Team Workflow ATS ID, TransactionId";
   private static final int PAIR_SIZE = 2;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public CreateWorkingBranchFromTxBlam() {
      super(BLAM_NAME, CoreImage.BRANCH_WORKING);
   }

   //////////////////////////////////////////
   // Widgets
   //////////////////////////////////////////

   @Override
   public List<XWidgetData> getXWidgetItems() {
      createWidgetBuilder();
      wb.andTextWidget(ATS_ID_TX_WIDGET_NAME).fillVert();
      return wb.getXWidgetDatas();
   }

   //////////////////////////////////////////
   // Operation
   //////////////////////////////////////////

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      String input = variableMap.getString(ATS_ID_TX_WIDGET_NAME);

      // Remove when demo operation test is implemented in Blam
      if (DemoUtilCore.isDemoDbBlamTest()) {
         return;
      }

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

   //////////////////////////////////////////
   // Helpers
   //////////////////////////////////////////

   @Override
   public Collection<IUserGroupArtifactToken> getUserGroups() {
      return Arrays.asList(AtsUserGroups.AtsAdmin);
   }

   //////////////////////////////////////////
   // Categories & Description
   //////////////////////////////////////////

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(XNavigateItem.DEFINE_ADMIN);
   }

   @Override
   public String getDescriptionUsage() {
      return BLAM_DESC;
   }

}
