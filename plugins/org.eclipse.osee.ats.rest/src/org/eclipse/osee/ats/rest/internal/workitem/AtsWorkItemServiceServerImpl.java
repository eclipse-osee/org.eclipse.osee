/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.ats.rest.internal.workitem;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.workflow.ITeamWorkflowProvidersLazy;
import org.eclipse.osee.ats.core.workflow.AtsWorkItemServiceImpl;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkItemServiceServerImpl extends AtsWorkItemServiceImpl {

   private final OrcsApi orcsApi;

   public AtsWorkItemServiceServerImpl(AtsApi atsApi, OrcsApi orcsApi, ITeamWorkflowProvidersLazy teamWorkflowProvidersLazy) {
      super(atsApi, teamWorkflowProvidersLazy);
      this.orcsApi = orcsApi;
   }

   @Override
   public IAtsWorkItem getWorkItemNew(ArtifactId workItemId) {
      ArtifactReadable art = orcsApi.getQueryFactory().fromBranch(atsApi.getAtsBranch()) //
         .andId(workItemId) //
         .includeTransactionDetails() //
         .follow(AtsRelationTypes.ActionToWorkflow_Action) //
         .followFork(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version) //
         .asArtifact();
      IAtsWorkItem workItem = atsApi.getWorkItemService().getWorkItem(art);
      return workItem;
   }

}
