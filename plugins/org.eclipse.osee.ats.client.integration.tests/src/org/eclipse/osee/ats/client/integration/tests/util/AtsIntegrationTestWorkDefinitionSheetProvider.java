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
import org.eclipse.osee.ats.workdef.IAtsWorkDefinitionSheetProvider;

/**
 * @author Donald G. Dunne
 */
public class AtsIntegrationTestWorkDefinitionSheetProvider implements IAtsWorkDefinitionSheetProvider {

   @Override
   public Collection<WorkDefinitionSheet> getWorkDefinitionSheets() {
      List<WorkDefinitionSheet> sheets = new ArrayList<>();
      sheets.add(new WorkDefinitionSheet("WorkDef_Team_DecisionReviewDefinitionManagerTest_Prepare",
         AtsIntegrationTestWorkDefinitionSheetProvider.class));
      sheets.add(new WorkDefinitionSheet("WorkDef_Team_DecisionReviewDefinitionManagerTest_toDecision",
         AtsIntegrationTestWorkDefinitionSheetProvider.class));
      sheets.add(new WorkDefinitionSheet("WorkDef_Team_PeerReviewDefinitionManagerTest_Transition",
         AtsIntegrationTestWorkDefinitionSheetProvider.class));
      sheets.add(new WorkDefinitionSheet("WorkDef_Team_TransitionManagerTest_TargetedVersion",
         AtsIntegrationTestWorkDefinitionSheetProvider.class));
      sheets.add(new WorkDefinitionSheet("WorkDef_Team_TransitionManagerTest_WidgetRequiredCompletion",
         AtsIntegrationTestWorkDefinitionSheetProvider.class));
      sheets.add(new WorkDefinitionSheet("WorkDef_Team_TransitionManagerTest_WidgetRequiredTransition",
         AtsIntegrationTestWorkDefinitionSheetProvider.class));
      return sheets;
   }
}
