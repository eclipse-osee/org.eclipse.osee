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
package org.eclipse.osee.ats.ide.integration.tests.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.core.workdef.WorkDefinitionSheet;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.review.DecisionReviewDefinitionManagerTest;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.review.PeerReviewDefinitionManagerTest;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.transition.TransitionManagerTest;
import org.eclipse.osee.ats.ide.workdef.IAtsWorkDefinitionSheetProvider;

/**
 * @author Donald G. Dunne
 */
public class AtsIntegrationTestWorkDefinitionSheetProvider implements IAtsWorkDefinitionSheetProvider {

   @Override
   public Collection<WorkDefinitionSheet> getWorkDefinitionSheets() {
      List<WorkDefinitionSheet> sheets = new ArrayList<>();
      sheets.add(new WorkDefinitionSheet(TransitionManagerTest.WorkDefTeamAtsTestUtil,
         AtsIntegrationTestWorkDefinitionSheetProvider.class));
      sheets.add(new WorkDefinitionSheet(TransitionManagerTest.WorkDefTargetedVersionId,
         AtsIntegrationTestWorkDefinitionSheetProvider.class));
      sheets.add(new WorkDefinitionSheet(TransitionManagerTest.WorkDefWidgetRequiredCompletionId,
         AtsIntegrationTestWorkDefinitionSheetProvider.class));
      sheets.add(new WorkDefinitionSheet(TransitionManagerTest.WorkDefWidgetRequiredTransitionId,
         AtsIntegrationTestWorkDefinitionSheetProvider.class));
      sheets.add(new WorkDefinitionSheet(DecisionReviewDefinitionManagerTest.DecisionWorkDefPrepareId,
         AtsIntegrationTestWorkDefinitionSheetProvider.class));
      sheets.add(new WorkDefinitionSheet(DecisionReviewDefinitionManagerTest.DecisionWorkDefToDecisionId,
         AtsIntegrationTestWorkDefinitionSheetProvider.class));
      sheets.add(new WorkDefinitionSheet(PeerReviewDefinitionManagerTest.PeerWorkDefId,
         AtsIntegrationTestWorkDefinitionSheetProvider.class));
      return sheets;
   }
}
