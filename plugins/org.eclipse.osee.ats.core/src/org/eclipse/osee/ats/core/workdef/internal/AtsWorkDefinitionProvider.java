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
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionProvider;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.core.workdef.WorkDefTeamMIM;
import org.eclipse.osee.ats.core.workdef.WorkDefTeamProductLine;
import org.eclipse.osee.ats.core.workdef.WorkDefTeamSimpleInWork;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefGoal;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefReviewDecision;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefReviewPeerToPeer;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefSprint;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefTaskAtsConfig2Example;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefTaskDefault;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefTeamAtsConfig2Example;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefTeamDefault;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefTeamSimple;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefTeamSimpleAnalyze;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkDefinitionProvider implements IAtsWorkDefinitionProvider {

   @Override
   public Collection<WorkDefinition> getWorkDefinitions() {
      List<WorkDefinition> workDefs = new ArrayList<WorkDefinition>();
      // Goal
      workDefs.add(new WorkDefGoal().build());
      // Reviews
      workDefs.add(new WorkDefReviewDecision().build());
      workDefs.add(new WorkDefReviewPeerToPeer().build());
      // Agile
      workDefs.add(new WorkDefSprint().build());
      // Tasks
      workDefs.add(new WorkDefTaskAtsConfig2Example().build());
      workDefs.add(new WorkDefTaskDefault().build());
      // Team Workflows
      workDefs.add(new WorkDefTeamAtsConfig2Example().build());
      workDefs.add(new WorkDefTeamDefault().build());
      workDefs.add(new WorkDefTeamProductLine().build());
      workDefs.add(new WorkDefTeamMIM().build());
      workDefs.add(new WorkDefTeamSimpleInWork().build());
      workDefs.add(new WorkDefTeamSimple().build());
      workDefs.add(new WorkDefTeamSimpleAnalyze().build());
      return workDefs;
   }

}
