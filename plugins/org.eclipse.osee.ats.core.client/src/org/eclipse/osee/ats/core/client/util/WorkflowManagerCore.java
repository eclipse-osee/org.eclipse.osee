/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.util;

import java.util.Collection;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.RuleDefinitionOption;
import org.eclipse.osee.ats.core.client.task.TaskArtifact;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.client.workflow.StateManager;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Donald G. Dunne
 */
public class WorkflowManagerCore {

   public static StateManager getStateManager(Artifact artifact) {
      return cast(artifact).getStateMgr();
   }

   public static AbstractWorkflowArtifact cast(Artifact artifact) {
      if (artifact instanceof AbstractWorkflowArtifact) {
         return (AbstractWorkflowArtifact) artifact;
      }
      return null;
   }

   public static boolean isEditable(AbstractWorkflowArtifact sma, IAtsStateDefinition stateDef, boolean privilegedEditEnabled) throws OseeCoreException {
      // must be writeable
      return !sma.isReadOnly() &&
      // and access control writeable
      sma.isAccessControlWrite() &&
      // and current state
      (stateDef == null || sma.isInState(stateDef)) &&
      // and one of these
      //
      // page is define to allow anyone to edit
      (sma.getStateDefinition().hasRule(RuleDefinitionOption.AllowEditToAll.name()) ||
      // team definition has allowed anyone to edit
         sma.teamDefHasRule(RuleDefinitionOption.AllowEditToAll) ||
         // privileged edit mode is on
         privilegedEditEnabled ||
         // current user is assigned
         sma.isAssigneeMe() ||
      // current user is ats admin
      AtsUtilCore.isAtsAdmin());
   }

   /**
    * Return parent AWA. Note: Use WorkItemUtil.getParentTeamWorkflow instead.
    */
   public static AbstractWorkflowArtifact getParentAWA(Artifact artifact) throws OseeCoreException {
      if (artifact.isOfType(AtsArtifactTypes.Task)) {
         TaskArtifact ta = (TaskArtifact) artifact;
         Collection<Artifact> awas = artifact.getRelatedArtifacts(AtsRelationTypes.TeamWfToTask_TeamWf);
         if (awas.isEmpty()) {
            throw new OseeStateException("Task has no parent [%s]", ta.getAtsId());
         }
         return (AbstractWorkflowArtifact) awas.iterator().next();
      } else if (artifact.isOfType(AtsArtifactTypes.ReviewArtifact)) {
         Collection<Artifact> awas = artifact.getRelatedArtifacts(AtsRelationTypes.TeamWorkflowToReview_Team);
         if (!awas.isEmpty()) {
            return (AbstractWorkflowArtifact) awas.iterator().next();
         }
      }
      return null;
   }

   public static Artifact getTeamDefinition(Artifact artifact) throws OseeCoreException {
      Artifact team = getParentTeamWorkflow(artifact);
      if (team != null) {
         String teamDefGuid = team.getSoleAttributeValue(AtsAttributeTypes.TeamDefinition);
         return ArtifactQuery.getArtifactFromId(teamDefGuid, BranchManager.getCommonBranch());
      }
      return null;
   }

   public static Artifact getParentActionArtifact(Artifact artifact) throws OseeCoreException {
      Artifact team = getParentTeamWorkflow(artifact);
      if (team != null) {
         return artifact.getRelatedArtifact(AtsRelationTypes.ActionToWorkflow_Action);
      }
      return null;
   }

   /**
    * Return parent team workflow artifact. Note: Preferred use of WorkItemUtil.getParentTeamWorkflow
    */
   public static Artifact getParentTeamWorkflow(Artifact artifact) throws OseeCoreException {
      if (artifact.isOfType(AtsArtifactTypes.TeamWorkflow)) {
         return artifact;
      } else if (artifact.isOfType(AtsArtifactTypes.Task)) {
         return getParentAWA(artifact);
      } else if (artifact.isOfType(AtsArtifactTypes.ReviewArtifact)) {
         return getParentAWA(artifact);
      }
      return null;
   }

}
