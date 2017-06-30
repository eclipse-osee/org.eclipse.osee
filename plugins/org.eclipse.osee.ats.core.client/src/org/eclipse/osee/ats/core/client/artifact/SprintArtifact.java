/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.artifact;

import java.util.List;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.core.client.action.ActionArtifact;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class SprintArtifact extends CollectorArtifact implements IAgileSprint {

   public SprintArtifact(Long id, String guid, BranchId branch, ArtifactTypeId artifactType) {
      super(id, guid, branch, artifactType, AtsRelationTypes.AgileSprintToItem_AtsItem);
   }

   @Override
   public ActionArtifact getParentActionArtifact() {
      return null;
   }

   @Override
   public AbstractWorkflowArtifact getParentAWA() throws OseeCoreException {
      return null;
   }

   @Override
   public TeamWorkFlowArtifact getParentTeamWorkflow() {
      return null;
   }

   @Override
   public boolean isActive() {
      return getStateMgr().getStateType().isInWork();
   }

   @Override
   public long getTeamUuid() {
      return getArtId();
   }

   @Override
   public List<Artifact> getMembers() throws OseeCoreException {
      return AtsClientService.get().getSprintItemsCache().getMembers(this);
   }

   public RelationTypeSide getMembersRelationType() {
      return AtsRelationTypes.AgileSprintToItem_AtsItem;
   }

}
