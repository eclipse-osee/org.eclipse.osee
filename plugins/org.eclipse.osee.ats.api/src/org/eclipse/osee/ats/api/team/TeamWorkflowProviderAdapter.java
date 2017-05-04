/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.team;

import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public abstract class TeamWorkflowProviderAdapter implements ITeamWorkflowProvider {

   @Override
   public String getWorkflowDefinitionId(IAtsWorkItem workItem) throws OseeCoreException {
      return null;
   }

   @Override
   public String getRelatedTaskWorkflowDefinitionId(IAtsTeamWorkflow teamWf) throws OseeCoreException {
      return null;
   }

   @Override
   public String getComputedPcrId(IAtsWorkItem workItem) throws OseeCoreException {
      return null;
   }

   @Override
   public String getArtifactTypeShortName(IAtsTeamWorkflow teamWf) {
      return null;
   }

   @Override
   public String getBranchName(IAtsTeamWorkflow teamWf, String defaultBranchName) {
      return null;
   }

}
