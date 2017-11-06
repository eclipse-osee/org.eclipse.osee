/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.integration.tests.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.core.workdef.WorkDefinitionSheet;
import org.eclipse.osee.ats.workdef.AtsWorkDefinitionSheetProviders;
import org.eclipse.osee.ats.workdef.IAtsWorkDefinitionSheetProvider;

/**
 * @author Donald G. Dunne
 */
public class AtsIntegrationTestWorkDefinitionSheetProvider implements IAtsWorkDefinitionSheetProvider {

   private static final String PLUGIN_ID = "org.eclipse.osee.ats.client.integration.tests";

   @Override
   public Collection<WorkDefinitionSheet> getWorkDefinitionSheets() {
      List<WorkDefinitionSheet> sheets = new ArrayList<>();
      sheets.add(new WorkDefinitionSheet("WorkDef_Team_DecisionReviewDefinitionManagerTest_Prepare",
         AtsWorkDefinitionSheetProviders.getSupportFile(PLUGIN_ID,
            "OSEE-INF/support/WorkDef_Team_DecisionReviewDefinitionManagerTest_Prepare.ats")));
      sheets.add(new WorkDefinitionSheet("WorkDef_Team_DecisionReviewDefinitionManagerTest_toDecision",
         AtsWorkDefinitionSheetProviders.getSupportFile(PLUGIN_ID,
            "OSEE-INF/support/WorkDef_Team_DecisionReviewDefinitionManagerTest_toDecision.ats")));
      sheets.add(new WorkDefinitionSheet("WorkDef_Team_PeerReviewDefinitionManagerTest_Transition",
         AtsWorkDefinitionSheetProviders.getSupportFile(PLUGIN_ID,
            "OSEE-INF/support/WorkDef_Team_PeerReviewDefinitionManagerTest_Transition.ats")));
      sheets.add(new WorkDefinitionSheet("WorkDef_Team_TransitionManagerTest_TargetedVersion",
         AtsWorkDefinitionSheetProviders.getSupportFile(PLUGIN_ID,
            "OSEE-INF/support/WorkDef_Team_TransitionManagerTest_TargetedVersion.ats")));
      sheets.add(new WorkDefinitionSheet("WorkDef_Team_TransitionManagerTest_WidgetRequiredCompletion",
         AtsWorkDefinitionSheetProviders.getSupportFile(PLUGIN_ID,
            "OSEE-INF/support/WorkDef_Team_TransitionManagerTest_WidgetRequiredCompletion.ats")));
      sheets.add(new WorkDefinitionSheet("WorkDef_Team_TransitionManagerTest_WidgetRequiredTransition",
         AtsWorkDefinitionSheetProviders.getSupportFile(PLUGIN_ID,
            "OSEE-INF/support/WorkDef_Team_TransitionManagerTest_WidgetRequiredTransition.ats")));
      return sheets;
   }
}
