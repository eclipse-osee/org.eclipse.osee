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

package org.eclipse.osee.ats.core.demo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionProvider;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.core.demo.test.WorkDefTeamDecisionReviewDefinitionManagerTestPrepare;
import org.eclipse.osee.ats.core.demo.test.WorkDefTeamDecisionReviewDefinitionManagerTesttoDecision;
import org.eclipse.osee.ats.core.demo.test.WorkDefTeamPeerReviewDefinitionManagerTestTransition;
import org.eclipse.osee.ats.core.demo.test.WorkDefTeamTransitionManagerTestTargetedVersion;
import org.eclipse.osee.ats.core.demo.test.WorkDefTeamTransitionManagerTestWidgetRequiredCompletion;
import org.eclipse.osee.ats.core.demo.test.WorkDefTeamTransitionManagerTestWidgetRequiredTransition;
import org.eclipse.osee.ats.core.workdef.WorkDefTaskDemoForCrEstimating;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefReviewPeerToPeerDemo;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkDefinitionProviderDemo implements IAtsWorkDefinitionProvider {

   @Override
   public Collection<WorkDefinition> getWorkDefinitions() {
      List<WorkDefinition> workDefs = new ArrayList<>();
      if (DemoUtil.isDemoDatabase()) {
         // Team Wf
         workDefs.add(new WorkDefTeamDemoProblemReport().build());
         workDefs.add(new WorkDefTeamDemoChangeRequest().build());
         workDefs.add(new WorkDefTeamDemoCode().build());
         workDefs.add(new WorkDefTeamDemoReq().build());
         workDefs.add(new WorkDefTeamDemoReqSimple().build());
         workDefs.add(new WorkDefTeamDemoSwDesign().build());
         workDefs.add(new WorkDefTeamDemoTest().build());
         // Review
         workDefs.add(new WorkDefReviewPeerToPeerDemo().build());
         workDefs.add(new WorkDefReviewPeerDemoSwDesign().build());
         // Task
         workDefs.add(new WorkDefTaskDemoSwDesign().build());
         workDefs.add(new WorkDefTaskDemoForCrEstimating().build());

         // ATS Integration Test Workflows
         workDefs.add(new WorkDefTeamDecisionReviewDefinitionManagerTestPrepare().build());
         workDefs.add(new WorkDefTeamDecisionReviewDefinitionManagerTesttoDecision().build());
         workDefs.add(new WorkDefTeamPeerReviewDefinitionManagerTestTransition().build());
         workDefs.add(new WorkDefTeamTransitionManagerTestTargetedVersion().build());
         workDefs.add(new WorkDefTeamTransitionManagerTestWidgetRequiredCompletion().build());
         workDefs.add(new WorkDefTeamTransitionManagerTestWidgetRequiredTransition().build());
      }
      return workDefs;
   }

}
