/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.api.team;

import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionToken;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;

/**
 * @author Donald G. Dunne
 */
public interface ITeamWorkflowProvider {

   /**
    * Assigned or computed Id that will show at the top of the editor. Default is <ATS Id> - <Legacy PCR Id (if any)>
    */
   public default String getComputedPcrId(IAtsWorkItem workItem) {
      return null;
   }

   /**
    * 5-9 character short name for UI and display purposes
    */
   public default String getArtifactTypeShortName(IAtsTeamWorkflow teamWf) {
      return null;
   }

   public default String getBranchName(IAtsTeamWorkflow teamWf, String defaultBranchName) {
      return null;
   }

   public boolean isResponsibleFor(IAtsWorkItem workItem);

   public default AtsWorkDefinitionToken getWorkflowDefinitionId(IAtsWorkItem workItem) {
      return null;
   }

   public default AtsWorkDefinitionToken getRelatedTaskWorkflowDefinitionId(IAtsTeamWorkflow teamWf) {
      return null;
   }

   public default AtsWorkDefinitionToken getOverrideWorkflowDefinitionId(IAtsTeamDefinition teamDef) {
      return null;
   }
}
