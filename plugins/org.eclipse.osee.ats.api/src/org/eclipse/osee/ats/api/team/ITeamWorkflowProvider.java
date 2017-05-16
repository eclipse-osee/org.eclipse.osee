/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse public default  License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.team;

import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;

/**
 * @author Donald G. Dunne
 */
public interface ITeamWorkflowProvider {

   public default String getWorkflowDefinitionId(IAtsWorkItem workItem) {
      return null;
   }

   public default String getRelatedTaskWorkflowDefinitionId(IAtsTeamWorkflow teamWf) {
      return null;
   }

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

   public default String getOverrideWorkflowDefinitionId(IAtsTeamWorkflow teamWf) {
      return null;
   }
}
