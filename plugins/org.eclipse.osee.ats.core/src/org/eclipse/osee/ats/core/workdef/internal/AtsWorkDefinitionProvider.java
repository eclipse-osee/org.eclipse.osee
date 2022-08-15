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

package org.eclipse.osee.ats.core.workdef.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionProvider;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.core.internal.AtsApiService;
import org.eclipse.osee.ats.core.workdef.WorkDefTaskDemoForCrEstimating;
import org.eclipse.osee.ats.core.workdef.WorkDefTeamProductLine;
import org.eclipse.osee.ats.core.workdef.WorkDefTeamSimpleInWork;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefGoal;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefReviewDecision;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefReviewPeerDemoSwDesign;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefReviewPeerToPeer;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefReviewPeerToPeerDemo;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefSprint;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefTaskAtsConfig2Example;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefTaskDefault;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefTaskDemoSwDesign;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefTeamAtsConfig2Example;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefTeamDefault;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefTeamDemoChangeRequest;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefTeamDemoCode;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefTeamDemoReq;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefTeamDemoSwDesign;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefTeamDemoTest;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefTeamSimple;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefTeamSimpleAnalyze;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkDefinitionProvider implements IAtsWorkDefinitionProvider {

   @Override
   public Collection<WorkDefinition> getWorkDefinitions() {
      // return empty if not AtsDB
      List<WorkDefinition> ret = new ArrayList<WorkDefinition>();
      // @formatter:off
         ret.addAll(Arrays.asList(
            new WorkDefGoal().build(),
            // Review
            new WorkDefReviewDecision().build(),
            new WorkDefReviewPeerToPeer().build(),
            // Agile
            new WorkDefSprint().build(),
            // Task
            new WorkDefTaskAtsConfig2Example().build(),
            new WorkDefTaskDefault().build(),
            // Team Wf
            new WorkDefTeamAtsConfig2Example().build(),
            new WorkDefTeamDefault().build(),
            new WorkDefTeamProductLine().build(),
            new WorkDefTeamSimpleInWork().build(),
            new WorkDefTeamSimple().build(),
            new WorkDefTeamSimpleAnalyze().build()));
         if (isDemoDb()) {
            ret.addAll(Arrays.asList(
            // Team Wf
            new WorkDefTeamDemoChangeRequest().build(),
            new WorkDefTeamDemoCode().build(),
            new WorkDefTeamDemoReq().build(),
            new WorkDefTeamDemoSwDesign().build(),
            new WorkDefTeamDemoTest().build(),
            // Review
            new WorkDefReviewPeerToPeerDemo().build(),
            new WorkDefReviewPeerDemoSwDesign().build(),
            // Task
            new WorkDefTaskDemoSwDesign().build(),
            new WorkDefTaskDemoForCrEstimating().build()));
      }
      // @formatter:on
      return ret;
   }

   public boolean isDemoDb() {
      return AtsApiService.get().getUserService().getUserByUserId("3333") != null;
   }

}
