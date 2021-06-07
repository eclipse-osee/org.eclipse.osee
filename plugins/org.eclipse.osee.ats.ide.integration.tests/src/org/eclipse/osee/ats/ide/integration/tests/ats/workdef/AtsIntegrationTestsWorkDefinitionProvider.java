/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.workdef;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionBuilder;

/**
 * @author Donald G. Dunne
 */
public class AtsIntegrationTestsWorkDefinitionProvider {

   public Collection<IAtsWorkDefinitionBuilder> getWorkDefinitionBuilders() {
      return Arrays.asList(new WorkDefTeamDecisionReviewDefinitionManagerTestPrepare(),
         new WorkDefTeamDecisionReviewDefinitionManagerTesttoDecision(),
         new WorkDefTeamPeerReviewDefinitionManagerTestTransition(),
         new WorkDefTeamTransitionManagerTestTargetedVersion(),
         new WorkDefTeamTransitionManagerTestWidgetRequiredCompletion(),
         new WorkDefTeamTransitionManagerTestWidgetRequiredTransition(),
         new WorkDefTeamDecisionReviewDefinitionManagerTestPrepare());
   }

}
