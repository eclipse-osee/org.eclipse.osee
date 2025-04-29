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
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionToken;
import org.eclipse.osee.ats.ide.demo.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.pr.CreateNewProblemReportBlam;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;

public class CreateNewDemoProblemReportBlam extends CreateNewProblemReportBlam {

   public CreateNewDemoProblemReportBlam() {
      super("Create New Demo Problem Report");
   }

   @Override
   public AtsWorkDefinitionToken getOverrideWorkDefinitionId(IAtsTeamDefinition teamDef) {
      return DemoWorkDefinitions.WorkDef_Team_Demo_Problem_Report;
   }

   @Override
   public ArtifactTypeToken getOverrideArtifactType(IAtsTeamDefinition teamDef) {
      return AtsArtifactTypes.DemoProblemReportTeamWorkflow;
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

}
