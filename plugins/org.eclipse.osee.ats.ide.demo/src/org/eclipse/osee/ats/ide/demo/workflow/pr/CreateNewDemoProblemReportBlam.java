/*******************************************************************************
 * Copyright (c) 2025 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.ats.ide.demo.workflow.pr;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.demo.DemoWorkDefinitions;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.workflow.NewActionData;
import org.eclipse.osee.ats.ide.demo.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.pr.AbstractCreateNewProblemReportBlam;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.osgi.service.component.annotations.Component;

@Component(service = AbstractBlam.class, immediate = true)
public class CreateNewDemoProblemReportBlam extends AbstractCreateNewProblemReportBlam {

   private static final String BLAM_NAME = "Create New Demo Problem Report";

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public CreateNewDemoProblemReportBlam() {
      super(BLAM_NAME, AtsImage.PROBLEM_REPORT);
   }

   //////////////////////////////////////////
   // Helpers
   //////////////////////////////////////////

   @Override
   public void createActionData(NewActionData data) {
      super.createActionData(data);
      data.andWorkDef(DemoWorkDefinitions.WorkDefTeamDemoProblemReport) //
         .andArtType(AtsArtifactTypes.DemoProblemReportTeamWorkflow);
   }

   @Override
   public boolean isApplicable() {
      return AtsApiService.get().getStoreService().isDemoDb();
   }

   @Override
   public Collection<IAtsActionableItem> getProgramCrAis() {
      IAtsActionableItem ai =
         AtsApiService.get().getActionableItemService().getActionableItemById(DemoArtifactToken.SAW_PL_PR_AI);
      return Collections.singleton(ai);
   }

   //////////////////////////////////////////
   // Categories & Description
   //////////////////////////////////////////

   @Override
   public String getDescriptionUsage() {
      return "Creates a new Demo Problem Report workflow using the SAW PL Problem Report actionable item and demo work definition. Only available on demo databases.";
   }

}
