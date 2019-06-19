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
package org.eclipse.osee.ats.ide.integration.tests.ats.workdef;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionBuilder;

/**
 * @author Donald G. Dunne
 */
public class AtsIntegrationTestsWorkDefinitionProvider {

   public Collection<IAtsWorkDefinitionBuilder> getWorkDefinitionBuilders() {
      return Arrays.asList(new WorkDefTeamAtsTestUtil(), new WorkDefTeamDecisionReviewDefinitionManagerTestPrepare(),
         new WorkDefTeamDecisionReviewDefinitionManagerTesttoDecision(),
         new WorkDefTeamPeerReviewDefinitionManagerTestTransition(),
         new WorkDefTeamTransitionManagerTestTargetedVersion(),
         new WorkDefTeamTransitionManagerTestWidgetRequiredCompletion(),
         new WorkDefTeamTransitionManagerTestWidgetRequiredTransition(),
         new WorkDefTeamDecisionReviewDefinitionManagerTestPrepare());
   }

}
