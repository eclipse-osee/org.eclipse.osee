/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.integration.tests.ats.demo;

import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.ats.api.config.tx.IAtsWorkDefinitionArtifactToken;
import org.eclipse.osee.ats.core.config.ImportWorkDefinitions;
import org.eclipse.osee.ats.ide.integration.tests.AtsClientService;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.review.DecisionReviewDefinitionManagerTest;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.review.PeerReviewDefinitionManagerTest;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.transition.TransitionManagerTest;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.junit.Test;

/**
 * Import work definitions needed for tests.
 *
 * @author Donald G. Dunne
 */
public class CreateTestWorkDefsTypesTest {

   @Test
   public void testAction() throws Exception {

      List<IAtsWorkDefinitionArtifactToken> workDefs = Arrays.asList(TransitionManagerTest.WorkDefTeamAtsTestUtil,
         TransitionManagerTest.WorkDefTargetedVersionId, TransitionManagerTest.WorkDefWidgetRequiredCompletionId,
         TransitionManagerTest.WorkDefWidgetRequiredTransitionId,
         DecisionReviewDefinitionManagerTest.DecisionWorkDefPrepareId,
         DecisionReviewDefinitionManagerTest.DecisionWorkDefToDecisionId,
         PeerReviewDefinitionManagerTest.PeerWorkDefId);
      ImportWorkDefinitions importWorkDefs =
         new ImportWorkDefinitions(AtsClientService.get(), CreateTestWorkDefsTypesTest.class);
      XResultData results = importWorkDefs.importWorkDefinitionSheets(
         workDefs.toArray(new IAtsWorkDefinitionArtifactToken[workDefs.size()]));
      if (results.isErrors()) {
         throw new OseeStateException(results.toString());
      }

   }

}
